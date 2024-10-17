package com.alepi.test.infrastructure;

import com.alepi.domain.strategy.model.valobj.RuleTreeVO;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyRepositoryTest {

    @Resource
    private IStrategyRepository strategyRepository;

    @Test
    public void test_queryRuleTreeVOByTreeId() {
        RuleTreeVO ruleValue = strategyRepository.queryRuleTreeVOByTreeId("rule_lock");
        log.info("ruleValue: {}", ruleValue);
    }
}
