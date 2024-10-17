package com.alepi.infrastructure.persistent.dao;

import com.alepi.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IRuleTreeDao  {

    RuleTree queryRuleTreeByTreeId(String treeId);
}