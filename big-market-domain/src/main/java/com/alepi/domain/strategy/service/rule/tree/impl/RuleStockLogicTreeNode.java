package com.alepi.domain.strategy.service.rule.tree.impl;

import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.armory.IStrategyDispatch;
import com.alepi.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyDispatch strategyDispatch;
    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {
        log.info("规则过滤-库存扣减 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        // 库存扣减
        Boolean status = strategyDispatch.subtractionAwardStock(strategyId, awardId);
        if (status) {
            // 写入延迟队列
            strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyId)
                    .awardId(awardId)
                    .build());

            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardData(DefaultTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue("")
                            .build())
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
