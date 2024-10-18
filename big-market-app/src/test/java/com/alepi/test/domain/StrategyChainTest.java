package com.alepi.test.domain;

import com.alepi.domain.strategy.model.entity.RaffleAwardEntity;
import com.alepi.domain.strategy.model.entity.RaffleFactorEntity;
import com.alepi.domain.strategy.service.IRaffleStrategy;
import com.alepi.domain.strategy.service.armory.IStrategyArmory;
import com.alepi.domain.strategy.service.rule.chain.ILogicChain;
import com.alepi.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.alepi.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import com.alepi.domain.strategy.service.rule.tree.impl.RuleLockLogicTreeNode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyChainTest {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private DefaultChainFactory defaultChainFactory;

    @Resource
    private RuleLockLogicTreeNode ruleLockLogicTreeNode;

    @Before
    public void test_strategyArmory() {
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100006L));
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));

        ReflectionTestUtils.setField(ruleWeightLogicChain, "userLevel", 5000L);
        ReflectionTestUtils.setField(ruleLockLogicTreeNode, "userRaffleCount", 3L);
    }

    @Test
    public void test_performRaffle() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder().userId("alepi").strategyId(100006L).build();

            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

            log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
            log.info("测试结果:{}", JSON.toJSONString(raffleAwardEntity));
        }
        new CountDownLatch(1).await();
    }

    @Test
    public void test_LogicChain_rule_blacklist() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO awardId = logicChain.logic("user001", 100001L);
        log.info("测试结果：{}", awardId);
    }

    @Test
    public void test_LogicChain_rule_weight() {
        // 通过反射 mock 规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userLevel", 4900L);
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO awardId = logicChain.logic("xiaofuge", 100001L);
        log.info("测试结果：{}", awardId);
    }

    @Test
    public void test_LogicChain_rule_default() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO awardId = logicChain.logic("xiaofuge", 100001L);
        log.info("测试结果：{}", awardId);
    }
}
