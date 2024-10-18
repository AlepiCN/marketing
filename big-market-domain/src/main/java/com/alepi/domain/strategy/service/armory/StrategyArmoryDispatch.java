package com.alepi.domain.strategy.service.armory;

import com.alepi.domain.strategy.model.entity.StrategyAwardEntity;
import com.alepi.domain.strategy.model.entity.StrategyEntity;
import com.alepi.domain.strategy.model.entity.StrategyRuleEntity;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.types.common.Constants;
import com.alepi.types.enums.ResponseCode;
import com.alepi.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 策略装载类
 */
@Service
@Slf4j
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository strategyRepository;

    /**
     * 策略及配置规则后的策略的分析及组装
     */
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardList(strategyId);

        // 缓存奖品库存-用于库存扣减
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            Integer awardCount = strategyAward.getAwardCountSurplus();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }

        // 默认策略装配
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);

        // 查询策略规则并配置规则
        StrategyEntity strategy = strategyRepository.queryStrategyEntityByStrategyId(strategyId);

        String ruleWeight = strategy.getRuleWeight();
        if (ruleWeight == null) return true;

        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRuleEntity(strategyId, ruleWeight);

        if (strategyRuleEntity == null) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }

        Map<String, List<Integer>> ruleWeightValuesMap = strategyRuleEntity.getRuleWeightValues();

        Set<String> keys = ruleWeightValuesMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValuesMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity -> !ruleWeightValues.contains(strategyAwardEntity.getAwardId()));
            // 装载权重规则的策略
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardEntitiesClone);
        }

        return true;
    }

    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        strategyRepository.cacheStrategyAwardCount(cacheKey,awardCount);
    }

    /**
     * 根据奖品列表的概率生成对应的概率table和概率range
     * @param key 唯一ID
     * @param strategyAwardEntities 奖品实体列表
     */
    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        BigDecimal rateScale = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算所需要的格子（map中的元素）数量，以最小的概率为单位生成格子，此时需保证每个奖品的概率都能被最小概率除尽，排除误差。
        // 如果某个奖品概率不能被除尽，则不能以最小概率为单位生成格子，需要以最小概率的分位为单位生成格子。
        // 此时如果所有奖品的概率和不为1，则每个奖品的概率并不是真实概率。
        // 所以要么保证一个策略下，所有奖品的概率和为1；要么就达成“概率是一个假值，真实概率需要根据概率和重新计算”的共识
        // 以下代码，以前者为背景进行编写。

        List<BigDecimal> rateList = strategyAwardEntities.stream().map(StrategyAwardEntity::getAwardRate).collect(Collectors.toList());

        boolean flag = true;
        for (BigDecimal rate : rateList) {
            // 计算每个奖品的概率值是否能被其中最小的概率值整除，以 flag 标记
            BigDecimal remainder = rate.remainder(rateScale);
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                flag = false;
            }
        }
        int rateRange;

        // 如果能整除，则以最小概率为单位，计算所有奖品所需要的格子数。格子总数量 = 总概率 / 最小概率
        if (flag) {
            rateRange = totalRate.divide(rateScale, RoundingMode.CEILING).intValue();
        } else {
            rateScale = BigDecimal.ONE.divide(BigDecimal.TEN.pow(rateScale.scale()), rateScale.scale(), RoundingMode.HALF_UP);
            rateRange = totalRate.divide(rateScale, RoundingMode.CEILING).intValue();
        }

        List<Integer> allAwardList = new ArrayList<>(rateRange);

        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            // 计算出该奖品所占格子数：格子总量 * 奖品概率
            int awardMountInTable = awardRate.divide(totalRate, RoundingMode.CEILING).multiply(new BigDecimal(rateRange)).intValue();

            for (int i = 0; i < awardMountInTable; i++) {
                allAwardList.add(awardId);
            }
        }

        // 打乱元素位置
        Collections.shuffle(allAwardList);

        HashMap<Integer, Integer> awardMap = new HashMap<>();

        for (int i = 0; i < allAwardList.size(); i++) {
            awardMap.put(i, allAwardList.get(i));
        }

        strategyRepository.storageStrategyAwardSearchRateTables(key, rateRange, awardMap);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取
        int rateRange = strategyRepository.getRateRange(String.valueOf(strategyId));
        // 通过生成的随机值，获取该策略下的奖品ID
        return strategyRepository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        int rateRange = strategyRepository.getRateRange(String.valueOf(strategyId).concat("_").concat(ruleWeightValue));
        return strategyRepository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return strategyRepository.subtractionAwardStock(cacheKey);
    }
}
