package com.alepi.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

@Data
public class RaffleActivitySku {

    private Integer id;
    /* 商品sku - 把每一个组合当做一个商品 */
    private Long sku;

    private Long activityId;
    /* 活动个人参与次数ID */
    private Long activityCountId;
    /* 商品库存 */
    private Integer stockCount;
    /* 剩余库存 */
    private Integer stockCountSurplus;

    private Date createTime;

    private Date updateTime;

}
