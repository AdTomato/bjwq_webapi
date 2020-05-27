package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一级客户中的业务员信息子表
 *
 * @author wangyong
 * @time 2020/5/20 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlcSalesman {

    /**
     * id
     */
    private String id;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 排序字段
     */
    private Double sortKey;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 福利地
     */
    private String welfare;

    /**
     * 福利办理方
     */
    private String welfareHandler;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 所属部门
     */
    private String department;

}
