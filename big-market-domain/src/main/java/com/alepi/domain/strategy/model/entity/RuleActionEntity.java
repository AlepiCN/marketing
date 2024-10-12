package com.alepi.domain.strategy.model.entity;

import com.alepi.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * 规则动作实体
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    static public class RaffleEntity {

    }

    /**
     * 抽奖前实体
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    static public class RaffleBeforeEntity extends RaffleEntity {
        private Long strategyId;

        /* 权重值key */
        private String ruleWeightValueKey;

        private Integer awardId;
    }

    /**
     * 抽奖中实体
     */
    static public class RafflingEntity extends RaffleEntity {

    }

    /**
     * 抽奖后实体
     */
    static public class RaffleAfterEntity extends RaffleEntity {

    }
}
