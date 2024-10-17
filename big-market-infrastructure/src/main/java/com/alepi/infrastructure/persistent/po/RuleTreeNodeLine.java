package com.alepi.infrastructure.persistent.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class RuleTreeNodeLine {

    private Long id;
    private String treeId;
    private String ruleNodeFrom;
    private String ruleNodeTo;
    private String ruleLimitType;
    private String ruleLimitValue;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
