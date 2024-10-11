package com.alepi.infrastructure.persistent.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class StrategyRule {

    private Long id;
    // 抽奖策略ID
    private Integer strategyId;
    // 抽奖奖品ID【规则类型为策略，则不需要奖品ID】
    private Integer awardId;
    // 抽奖规则类型：1-策略规则、2-奖品规则
    private Integer ruleType;
    // 抽奖规则【rule_random-随机值计算、rule_lock-抽奖几次后解锁、rule_luck_award-兜底奖品、rule_weight-抽奖奖品权重】
    private String ruleModel;
    // 抽奖规则比值
    private String ruleValue;
    // 抽奖规则描述
    private String ruleDesc;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
