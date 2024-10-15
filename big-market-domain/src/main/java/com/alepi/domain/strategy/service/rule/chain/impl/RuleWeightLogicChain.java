package com.alepi.domain.strategy.service.rule.chain.impl;

import com.alepi.domain.strategy.model.entity.RuleActionEntity;
import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.armory.IStrategyDispatch;
import com.alepi.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.alepi.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository strategyRepository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    public Long userLevel = 0L;


    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {}, strategyId: {}, ruleModel: {}", userId, strategyId, ruleModel());
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, ruleModel());

        Map<Long, String> ruleValueMap = getAnalyticalValue(ruleValue);
        if (ruleValueMap == null || ruleValueMap.isEmpty()) return null;

        ArrayList<Long> keys = new ArrayList<>(ruleValueMap.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);

        Long nextValue = keys.stream()
                .filter(key -> userLevel >= key)
                .findFirst()
                .orElse(null);

        if (nextValue != null) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, ruleValueMap.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {}, strategyId: {}, ruleModel: {}, awardId: {}", userId, strategyId, ruleModel(), awardId);
            return awardId;
        }

        log.info("抽奖责任链-权重放行 userId: {}, strategyId: {}, ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return "rule_weight";
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleWeightValues = ruleValue.split(Constants.SPACE);

        Map<Long, String> ruleValueMap = new HashMap<>();

        for (String ruleWeightValue : ruleWeightValues) {
            if (ruleWeightValue == null || ruleWeightValue.isEmpty()) return  ruleValueMap;

            String[] parts = ruleWeightValue.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight element invalid input format: " + ruleWeightValue);
            }

            ruleValueMap.put(Long.valueOf(parts[0]), ruleWeightValue);
        }

        return ruleValueMap;
    }
}
