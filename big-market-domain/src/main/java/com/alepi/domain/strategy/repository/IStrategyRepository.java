package com.alepi.domain.strategy.repository;

import com.alepi.domain.strategy.model.entity.StrategyAwardEntity;
import com.alepi.domain.strategy.model.entity.StrategyEntity;
import com.alepi.domain.strategy.model.entity.StrategyRuleEntity;
import com.alepi.domain.strategy.model.valobj.RuleTreeVO;
import com.alepi.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.alepi.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.HashMap;
import java.util.List;

public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storageStrategyAwardSearchRateTables(String key, Integer rateRange, HashMap<Integer, Integer> awardMap);

    int getRateRange(String strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRuleEntity(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId);

    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);

    /**
     * 缓存奖品库存
     */
    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    /**
     * 奖品库存扣减
     */
    Boolean subtractionAwardStock(String key);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
