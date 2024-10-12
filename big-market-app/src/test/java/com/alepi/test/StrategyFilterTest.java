package com.alepi.test;

import com.alepi.domain.strategy.model.entity.RaffleAwardEntity;
import com.alepi.domain.strategy.model.entity.RaffleFactorEntity;
import com.alepi.domain.strategy.service.armory.IStrategyArmory;
import com.alepi.domain.strategy.service.armory.IStrategyDispatch;
import com.alepi.domain.strategy.service.raffle.IRaffleStrategy;
import com.alepi.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyFilterTest {

    @Resource
    private IStrategyDispatch strategyDispatch;

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userLevel", 4500L);
    }

    @Test
    public void test_performRaffle() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder().userId("alepi").strategyId(100001L).build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果:{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_performRaffle_blackList() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder().userId("user003").strategyId(100001L).build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果:{}", JSON.toJSONString(raffleAwardEntity));
    }
}
