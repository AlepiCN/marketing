package com.alepi.domain.strategy.service.impl;

import com.alepi.domain.strategy.model.entity.StrategyAwardEntity;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public void assembleLotteryStrategy(Long strategyId) {
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardList(strategyId);

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

        strategyRepository.storageStrategyAwardSearchRateTables(strategyId, rateRange, awardMap);

    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = strategyRepository.getRateRange(strategyId);
        return strategyRepository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }
}
