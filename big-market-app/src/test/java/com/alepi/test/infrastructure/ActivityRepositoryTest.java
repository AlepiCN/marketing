package com.alepi.test.infrastructure;

import com.alepi.infrastructure.persistent.dao.IRaffleActivityDao;
import com.alepi.infrastructure.persistent.po.RaffleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ActivityRepositoryTest {

    @Resource
    private IRaffleActivityDao raffleActivityDao;

    @Test
    public void test_findAll() {
        List<RaffleActivity> all = raffleActivityDao.findAll();
        log.info("查询所有RaffleActivity:{}", all);
    }
}
