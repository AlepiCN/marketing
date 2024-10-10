package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyRuleDao {
    List<StrategyRule> getStrategyRuleList();
}
