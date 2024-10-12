package com.alepi.domain.strategy.service.raffle;

import com.alepi.domain.strategy.model.entity.RaffleAwardEntity;
import com.alepi.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);

}
