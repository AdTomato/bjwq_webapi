package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wangyong
 * @Date:2020/4/15 14:48
 * @Description: 业务员维护
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesmansMaintain extends BaseEntity {

    /**
     * 一级客户名称
     */
    private String firstLevelClientName;

    /**
     * 二级客户名称
     */
    private String secondLevelClientName;

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
    private String welfareOperator;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 操作人
     */
    String operator;

    /**
     * 查询人
     */
    String inquirer;

    String subordinateDepartment;
}
