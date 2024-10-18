package com.alepi.domain.strategy.service;

import com.alepi.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

/**
 * 抽奖库存相关服务，获取库存消耗队列
 */
public interface IRaffleStock {

    /**
     * 获取奖品库存消耗队列
     * @return 奖品库存Key信息
     */
    StrategyAwardStockKeyVO takeQueueValue();

    /**
     * 更新奖品库存消耗记录
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}