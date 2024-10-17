package com.alepi.domain.strategy.service.rule.chain.factory;

import com.alepi.domain.strategy.model.entity.StrategyEntity;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 责任链默认工厂
 */
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainGroup;

    private IStrategyRepository strategyRepository;

    public DefaultChainFactory(IStrategyRepository strategyRepository, Map<String, ILogicChain> logicChainGroup) {
        this.logicChainGroup = logicChainGroup;
        this.strategyRepository = strategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.getRuleModels();

        if (ruleModels == null || ruleModels.length == 0) {
            return logicChainGroup.get("default");
        }

        ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);
        ILogicChain current = logicChain;

        for (int i = 1; i < ruleModels.length; i++) {
            current = current.appendNext(logicChainGroup.get(ruleModels[i]));
        }

        current.appendNext(logicChainGroup.get("default"));

        return logicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        private Integer awardId;
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {
        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重抽奖"),
        ;

        private final String code;
        private final String info;
    }
}
