package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> getStrategyAwardListByStrategyId(Long strategyId);
}
