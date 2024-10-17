package com.alepi.domain.strategy.service.rule.tree.factory.engine;

import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树决策引擎
 */

public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);
}
