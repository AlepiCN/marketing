package com.alepi.domain.strategy.service.rule.tree.factory;

import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.model.valobj.RuleTreeVO;
import com.alepi.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.alepi.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.alepi.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 规则树工厂
 */
@Service
public class DefaultTreeFactory {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeGroup) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeNodeGroup, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {
        private RuleLogicCheckTypeVO ruleLogicCheckType;
        private StrategyAwardVO strategyAwardData;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        private Integer awardId;
        private String awardRuleValue;
    }
}
