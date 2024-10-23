package com.alepi.trigger.api;

import com.alepi.trigger.api.dto.RaffleAwardListRequestDTO;
import com.alepi.trigger.api.dto.RaffleAwardListResponseDTO;
import com.alepi.trigger.api.dto.RaffleRequestDTO;
import com.alepi.trigger.api.dto.RaffleResponseDTO;
import com.alepi.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface IRaffleService {

    /**
     * 策略装配接口
     * @param strategyId 策略ID
     * @return 装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询奖品列表
     * @param raffleAwardListRequestDTO 列表查询请求参数
     * @return 奖品列表数据
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO raffleAwardListRequestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
