package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IRaffleActivityCountDao {

    RaffleActivityCount queryRaffleActivityCountByActivityCountId(@Param("activityCountId") Long activityCountId);
}




