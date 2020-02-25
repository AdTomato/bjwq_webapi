package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 09:18
 * @Description: 云枢所有表单实体类的基类
 */
@Data
public class BaseEntity {

    /**
     * id
     */
    public String id;

    /**
     * 数据标题
     */
    public String name;

    /**
     * 创建人
     */
    public String creater;

    /**
     * 创建人部门id
     */
    public String createdDeptId;

    /**
     * 拥有者
     */
    public String owner;

    /**
     * 拥有者部门id
     */
    public String ownerDeptId;

    /**
     * 创建时间
     */
    public Date createdTime;

    /**
     * 修改人
     */
    public String modifier;

    /**
     * 修改时间
     */
    public Date modifiedTime;

    /**
     * 流程实例id
     */
    public String workflowInstanceId;

    /**
     * 单据号
     */
    public String sequenceNo;

    /**
     * 单据状态
     */
    public String sequenceStatus;

    /**
     * 部门查询编码
     */
    public String ownerDeptQueryCode;

}
