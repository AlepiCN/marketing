package com.alepi.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖因子
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {

    private Long strategyId;

    private Integer awardId;

    // 奖品对接标识-发奖策略
    private String awardKey;

    // 奖品配置信息
    private String awardConfig;

    // 奖品描述
    private String awardDesc;
}
