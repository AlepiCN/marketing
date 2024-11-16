package com.alepi.domain.activity.service;

import com.alepi.domain.activity.model.entity.ActivityOrderEntity;
import com.alepi.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleOrder {

    /**
     * 创建抽奖活动订单
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动
     * @return 活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);

}
