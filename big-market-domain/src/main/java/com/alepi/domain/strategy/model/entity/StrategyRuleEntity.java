package com.alepi.domain.strategy.model.entity;

import com.alepi.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {
    // 抽奖策略ID
    private Integer strategyId;
    // 抽奖奖品ID【规则类型为策略，则不需要奖品ID】
    private Integer awardId;
    // 抽奖规则类型：1-策略规则、2-奖品规则
    private Integer ruleType;
    // 抽奖规则【rule_random-随机值计算、rule_lock-抽奖几次后解锁、rule_luck_award-兜底奖品、rule_weight-抽奖奖品权重】
    private String ruleModel;
    // 抽奖规则对应值
    private String ruleValue;
    // 抽奖规则描述
    private String ruleDesc;


    /**
     * 解析权重规则值为map
     * @return 权重规则元素-该元素下涉及的奖品id列表
     */
    public Map<String, List<Integer>> getRuleWeightValues() {
        if (!"rule_weight".equals(ruleModel)) return null;
        String[] ruleWeightGroups = ruleValue.split(Constants.SPACE);
        HashMap<String, List<Integer>> result = new HashMap<>();
        for (String ruleWeightGroup : ruleWeightGroups) {
            if (ruleWeightGroup == null || ruleWeightGroup.isEmpty()) return result;

            String[] parts = ruleWeightGroup.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight element invalid input format: " + ruleWeightGroup);
            }

            String[] valueStrings = parts[1].split(Constants.SPLIT);
            List<Integer> values = new ArrayList<>();
            for (String valueString : valueStrings) {
                values.add(Integer.parseInt(valueString));
            }

            result.put(ruleWeightGroup, values);
        }

        return result;
    }
}
