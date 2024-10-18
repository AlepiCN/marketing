package com.alepi.domain.strategy.service.raffle;

import com.alepi.domain.strategy.model.valobj.RuleTreeVO;
import com.alepi.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.alepi.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.AbstractRaffleStrategy;
import com.alepi.domain.strategy.service.armory.IStrategyDispatch;
import com.alepi.domain.strategy.service.rule.chain.ILogicChain;
import com.alepi.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.alepi.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(strategyRepository, strategyDispatch, defaultChainFactory, defaultTreeFactory);
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModel(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }

        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (ruleTreeVO == null) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表rule_tree, rule_tree_node, rule_tree_line 配置对应的规则信息");
        }

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        return strategyRepository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }
}
