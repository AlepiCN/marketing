package com.alepi.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeLineVO {

    private Integer treeId;

    /* 规则key节点 from */
    private String ruleNodeFrom;

    /* 规则key节点 to */
    private String ruleNodeTo;

    /* 限定类型 1-=; 2->; 3-<; 4->=; 5-<=; 6-enum */
    private RuleLimitTypeVO ruleLimitType;

    /* 限定值（到下个节点） */
    private RuleLogicCheckTypeVO ruleLimitValue;
}
