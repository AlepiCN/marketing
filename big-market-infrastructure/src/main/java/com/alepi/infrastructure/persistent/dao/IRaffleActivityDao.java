package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IRaffleActivityDao {

    RaffleActivity queryRaffleActivityByActivityId(@Param("activityId") Long activityId);
}




