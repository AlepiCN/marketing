package com.alepi.domain.strategy.service.rule.tree.impl;

import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    private Long userRaffleCount = 10L;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        long raffleCount = 0L;

        try {
            raffleCount = Long.parseLong(ruleValue);
        } catch (Exception e) {
            log.error("规则过滤-次数锁异常 ruleValue:{} ", ruleValue + "配置不正确");
        }

        if(raffleCount <= userRaffleCount) {
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
