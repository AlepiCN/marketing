package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IRaffleActivitySkuDao {

    RaffleActivitySku queryActivitySku(@Param("sku") Long sku);
}
