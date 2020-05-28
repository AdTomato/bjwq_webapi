package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 减员_客户实体
 * @ClassName com.authine.cloudpivot.web.api.entity.DeleteEmployee
 * @Date 2020/2/25 15:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEmployee extends BaseEntity {
    String deleteEmployeeId;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 证件类型
     */
    String identityNoType;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 离职原因
     */
    String leaveReason;
    /**
     * 离职日期
     */
    Date leaveTime;
    /**
     * 社保终止时间
     */
    Date socialSecurityEndTime;
    /**
     * 公积金终止时间
     */
    Date providentFundEndTime;

    /**
     * 备注
     */
    String remark;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 社保福利办理方
     */
    String sWelfareHandler;
    /**
     * 公积金福利办理方
     */
    String gWelfareHandler;
    /**
     * 所属部门
     */
    String subordinateDepartment;
    /**
     * 性别
     */
    String gender;
    /**
     * 生日
     */
    Date birthday;
    /** 社保状态*/
    String sbStatus;
    /** 公积金状态*/
    String gjjStatus;
    /** 操作人*/
    String operator;
    /** 查询人*/
    String inquirer;

    String returnReason;
}
