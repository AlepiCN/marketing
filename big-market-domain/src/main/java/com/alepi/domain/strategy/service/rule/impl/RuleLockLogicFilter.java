package com.alepi.domain.strategy.service.rule.impl;

import com.alepi.domain.strategy.model.entity.RuleActionEntity;
import com.alepi.domain.strategy.model.entity.RuleMatterEntity;
import com.alepi.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.annotation.LogicStrategy;
import com.alepi.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户抽奖n次后，对应奖品可解锁抽奖
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RafflingEntity> {

    @Resource
    private IStrategyRepository strategyRepository;

    private Long userRaffleTimes = 5L;

    /**
     * 1. 执行某策略的抽奖
     * 2. 查询该策略下的次数解锁处理器
     * 3. 查询该策略下需要解锁的奖品
     * 4. 查询该策略下兜底奖品列表
     * 5. 查询当前抽奖次数，如果+1 < 抽奖次数，则返回兜底奖品
     * 1. 如何区分已解锁和未解锁的奖品？
     * 不存在是否解锁的奖品，奖池的奖品包括策略下的所有奖品，抽到未解锁的奖品时只需要返回
     * 兜底奖品即可，库存没了同理。
     * 6. 如果+1 >= 抽奖次数，查看奖品库存，如果库存不够，则返回兜底奖品
     */
    @Override
    public RuleActionEntity<RuleActionEntity.RafflingEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        // 查询规则值配置；当前奖品ID，抽奖中规则对应的校验值。如；1、2、6
        String ruleValue = strategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        long raffleCount = Long.parseLong(ruleValue);

        // 用户抽奖次数大于规则限定值，规则放行
        if (userRaffleTimes >= raffleCount) {
            return RuleActionEntity.<RuleActionEntity.RafflingEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 用户抽奖次数小于规则限定值，规则拦截
        return RuleActionEntity.<RuleActionEntity.RafflingEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
