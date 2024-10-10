package com.alepi.infrastructure.persistent.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class Award {

    private Integer id;
    private Integer awardId;
    private String awardKey;
    private String awardConfig;
    private String awardDesc;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
