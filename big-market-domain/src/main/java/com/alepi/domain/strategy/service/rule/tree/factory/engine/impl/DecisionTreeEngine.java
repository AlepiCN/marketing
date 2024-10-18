package com.alepi.domain.strategy.service.rule.tree.factory.engine.impl;

import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.alepi.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.alepi.domain.strategy.model.valobj.RuleTreeVO;
import com.alepi.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.alepi.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);

        while (nextNode != null) {
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            String ruleValue = ruleTreeNode.getRuleValue();

            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleValue);
            RuleLogicCheckTypeVO ruleLogicCheckType = logicEntity.getRuleLogicCheckType();
            strategyAwardData = logicEntity.getStrategyAwardData();

            log.info("决策树引擎【{}】 treeId: {} node: {} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckType.getCode());
            nextNode = nextNode(ruleLogicCheckType.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }

        return strategyAwardData;
    }

    private String nextNode(String matterValue, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList) {
        if (ruleTreeNodeLineVOList == null || ruleTreeNodeLineVOList.isEmpty()) return null;

        for (RuleTreeNodeLineVO nodeLine : ruleTreeNodeLineVOList) {
            if (decisionLogic(matterValue, nodeLine)) {
                return nodeLine.getRuleNodeTo();
            }
        }

        log.info("决策树引擎【{}】未找到可执行节点！treeId: {} node: {} limitType:{}",
                ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), ruleTreeNodeLineVOList.get(0).getRuleNodeFrom(), matterValue);
        return null;
//        throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            case GE:
            case GT:
            case LE:
            case LT:
            default:
                return false;
        }
    }
}
