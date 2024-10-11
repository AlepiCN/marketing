package com.alepi.test;

import com.alepi.domain.strategy.service.IStrategyArmory;
import com.alepi.domain.strategy.service.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IStrategyDispatch strategyDispatch;

    @Resource
    private IStrategyArmory strategyArmory;

    @Before
    public void test_strategyArmory() {
        boolean result = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("测试结果：{}", result);
    }

    @Test
    public void test_getAssembleRandomValue() {
        log.info("测试结果：{}", strategyDispatch.getRandomAwardId(100001L));
    }

    @Test
    public void test_getAssembleRandomValue_ruleWeight() {
        log.info("测试结果：{} - 4000", strategyDispatch.getRandomAwardId(100001L, "4000:102,103,104,105"));
        log.info("测试结果：{} - 5000", strategyDispatch.getRandomAwardId(100001L, "5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000", strategyDispatch.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));
    }

}
