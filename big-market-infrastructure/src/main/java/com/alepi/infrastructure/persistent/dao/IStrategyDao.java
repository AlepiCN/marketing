package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyDao {
    List<Strategy> getStrategyList();
}
