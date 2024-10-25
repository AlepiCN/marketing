package com.alepi.domain.strategy.service;

import com.alepi.domain.strategy.model.entity.RaffleAwardEntity;
import com.alepi.domain.strategy.model.entity.RaffleFactorEntity;

import java.util.List;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);

    List<RaffleAwardEntity> tenTimesRaffle(RaffleFactorEntity raffleFactorEntity);

}
