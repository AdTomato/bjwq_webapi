package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.ChildrenInformation
 * @Date 2020/3/14 11:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenInformation {
    /** 主键id*/
    String id;
    /** 父级idid*/
    String parentId;
    /** 排序号*/
    Double sortKey;
    /** 子女名称*/
    String childrenName;
    /** 子女证件号码*/
    String childrenIdentityNo;
}
