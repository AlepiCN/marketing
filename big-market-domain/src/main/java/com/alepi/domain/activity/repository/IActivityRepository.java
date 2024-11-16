package com.alepi.domain.activity.repository;

import com.alepi.domain.activity.model.entity.ActivityCountEntity;
import com.alepi.domain.activity.model.entity.ActivityEntity;
import com.alepi.domain.activity.model.entity.ActivitySkuEntity;

/**
 * 活动仓储接口
 */
public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

}
