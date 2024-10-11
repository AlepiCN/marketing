package com.alepi.domain.strategy.model.entity;

import com.alepi.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {

    // 策略id
    private Long strategyId;
    // 策略描述
    private String strategyDesc;
    // 策略规则模型
    private String ruleModels;

    public String[] getRuleModels() {
        if (StringUtils.isBlank(ruleModels)) return null;
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight() {
        String[] ruleModels = this.getRuleModels();
        for (String ruleModel : ruleModels) {
            if ("rule_weight".equals(ruleModel)) return ruleModel;
        }
        return null;
    }
}
