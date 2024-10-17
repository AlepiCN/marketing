package com.alepi.domain.strategy.service.rule.chain.impl;

import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.alepi.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.alepi.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository strategyRepository;


    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {}, strategyId: {}, ruleModel: {}", userId, strategyId, ruleModel());
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, ruleModel());

        String[] parts = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(parts[0]);
        String[] userBlackIds = parts[1].split(Constants.SPLIT);

        for (String userBlackId : userBlackIds) {
            if (userBlackId.equals(userId)) {
                log.info("抽奖责任链-黑名单接管 userId: {}, strategyId: {}, ruleModel: {}, awardId: {}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(ruleModel())
                        .build();
            }
        }

        log.info("抽奖责任链-黑名单放行 userId: {}, strategyId: {}, ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
}
