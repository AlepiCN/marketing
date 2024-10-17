package com.alepi.domain.strategy.service.rule.tree;

import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId);
}