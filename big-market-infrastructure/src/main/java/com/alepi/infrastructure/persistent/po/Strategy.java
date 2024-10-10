package com.alepi.infrastructure.persistent.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class Strategy {

    private Long id;
    private Long strategyId;
    private String strategyDesc;
    private String ruleModels;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
