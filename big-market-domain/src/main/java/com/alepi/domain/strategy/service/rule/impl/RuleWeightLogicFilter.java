package com.alepi.domain.strategy.service.rule.impl;

import com.alepi.domain.strategy.model.entity.RuleActionEntity;
import com.alepi.domain.strategy.model.entity.RuleMatterEntity;
import com.alepi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.annotation.LogicStrategy;
import com.alepi.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.alepi.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权重规则前置过滤
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository strategyRepository;

    private Long userLevel = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String ruleValue = strategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        Map<Long, String> ruleValueMap = getAnalyticalValue(ruleValue);
        if (ruleValueMap == null || ruleValueMap.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        ArrayList<Long> keys = new ArrayList<>(ruleValueMap.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);

        Long nextValue = keys.stream()
                .filter(key -> userLevel >= key)
                .findFirst()
                .orElse(null);

        if (nextValue != null) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(ruleMatterEntity.getStrategyId())
                            .ruleWeightValueKey(ruleValueMap.get(nextValue))
                            .build())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
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
