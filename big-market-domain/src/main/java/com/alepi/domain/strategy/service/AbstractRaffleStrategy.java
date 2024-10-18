package com.alepi.domain.strategy.service;

import com.alepi.domain.strategy.model.entity.RaffleAwardEntity;
import com.alepi.domain.strategy.model.entity.RaffleFactorEntity;
import com.alepi.domain.strategy.repository.IStrategyRepository;
import com.alepi.domain.strategy.service.armory.IStrategyDispatch;
import com.alepi.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.alepi.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.alepi.types.enums.ResponseCode;
import com.alepi.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 抽奖策略抽象类
 */

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy, IRaffleStock {

    protected IStrategyRepository strategyRepository;

    protected IStrategyDispatch strategyDispatch;

    protected DefaultChainFactory defaultChainFactory;

    protected DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.strategyRepository = strategyRepository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        Long strategyId = raffleFactorEntity.getStrategyId();
        String userId = raffleFactorEntity.getUserId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        DefaultChainFactory.StrategyAwardVO strategyAwardVO = raffleLogicChain(userId, strategyId);
        log.info("抽奖策略计算-责任链 {} {} {} {}", userId, strategyId, strategyAwardVO.getAwardId(), strategyAwardVO.getLogicModel());
        if (!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(strategyAwardVO.getLogicModel())) {
            return RaffleAwardEntity.builder()
                    .awardId(strategyAwardVO.getAwardId())
                    .build();
        }

        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, strategyAwardVO.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, strategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());

        return RaffleAwardEntity.builder()
                .awardId(treeStrategyAwardVO.getAwardId())
                .awardConfig(treeStrategyAwardVO.getAwardRuleValue())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     * @param userId
     * @param strategyId
     * @return
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖计算，规则树抽象方法
     * @param userId
     * @param strategyId
     * @return
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);

}
