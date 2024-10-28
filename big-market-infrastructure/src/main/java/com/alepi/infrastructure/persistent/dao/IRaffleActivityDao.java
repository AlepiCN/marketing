package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IRaffleActivityDao {

    List<RaffleActivity> findAll();
}




