package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IAwardDao {
    List<Award> getAwardList();
}
