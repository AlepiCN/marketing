package com.alepi.infrastructure.persistent.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class RuleTree {
    private Long id;
    private String treeId;
    private String treeName;
    private String treeDesc;
    private String treeRootRuleKey;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
