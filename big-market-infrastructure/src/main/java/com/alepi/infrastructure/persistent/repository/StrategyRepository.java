package com.alepi.infrastructure.persistent.repository;

import com.alepi.domain.strategy.model.entity.StrategyAwardEntity;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.infrastructure.persistent.dao.IStrategyAwardDao;
import com.alepi.infrastructure.persistent.po.StrategyAward;
import com.alepi.infrastructure.persistent.redis.IRedisService;
import com.alepi.types.common.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {

        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;

        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);

        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;

        List<StrategyAward> list = strategyAwardDao.getStrategyAwardListByStrategyId(strategyId);

        strategyAwardEntities = new ArrayList<>(list.size());

        for (StrategyAward strategyAward : list) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                        .strategyId(strategyAward.getStrategyId())
                        .awardId(strategyAward.getAwardId())
                        .awardCount(strategyAward.getAwardCount())
                        .awardCountSurplus(strategyAward.getAwardCountSurplus())
                        .awardRate(strategyAward.getAwardRate())
                        .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }

        redisService.setValue(cacheKey, strategyAwardEntities);

        return strategyAwardEntities;
    }

    @Override
    public void storageStrategyAwardSearchRateTables(Long strategyId, Integer rateRange, HashMap<Integer, Integer> awardMap) {
        // 1. 存储抽奖策略范围值
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);

        // 2. 存储概率查找表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(awardMap);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }
}
