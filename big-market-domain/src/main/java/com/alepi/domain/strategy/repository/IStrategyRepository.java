package com.alepi.domain.strategy.repository;

import com.alepi.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storageStrategyAwardSearchRateTables(Long strategyId, Integer rateRange, HashMap<Integer, Integer> awardMap);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey);
}
