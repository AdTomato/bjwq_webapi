package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 增员_客户实体
 * @ClassName com.authine.cloudpivot.web.api.entity.AddEmployee
 * @Date 2020/2/25 14:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployee extends BaseEntity {
    String addEmployeeId;
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 联系电话
     */
    String mobile;
    /**
     * 邮箱
     */
    String email;
    /**
     * 户籍性质
     */
    String familyRegisterNature;
    /**
     * 员工性质
     */
    String employeeNature;
    /**
     * 入职日期
     */
    Date entryTime;
    /**
     * 合同开始日期
     */
    Date contractStartTime;
    /**
     * 合同结束日期
     */
    Date contractEndTime;
    /**
     * 合同工资
     */
    Double contractSalary;
    /**
     * 社保起做时间
     */
    Date socialSecurityStartTime;
    /**
     * 备注
     */
    String remark;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 社保基数
     */
    Double socialSecurityBase = 0d;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 公积金起做时间
     */
    Date providentFundStartTime;
    /**
     * 公积金基数
     */
    Double providentFundBase = 0d;
    /**
     * 证件类型
     */
    String identityNoType;
    /**
     * 单位公积金比例
     */
    Double companyProvidentFundBl;
    /**
     * 个人公积金比例
     */
    Double employeeProvidentFundBl;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 户籍备注
     */
    String householdRegisterRemarks;
    /**
     * 性别
     */
    String gender;
    /**
     * 日期
     */
    Date birthday;
    /**
     * 工作地
     */
    String workplace;
    /**
     * 退役士兵
     */
    String isRetiredSoldier;
    /**
     * 贫困建档人员
     */
    String isPoorArchivists;
    /**
     * 残疾人
     */
    String isDisabled;
    /**
     * 社保福利办理方
     */
    String sWelfareHandler;
    /**
     * 公积金利办理方
     */
    String gWelfareHandler;

    /**
     * 操作人
     */
    String operator;
    /**
     * 查询人
     */
    String inquirer;

    /**
     * 社保状态
     */
    String sbStatus;
    /**
     * 公积金状态
     */
    String gjjStatus;
    /**
     * 退回原因
     */
    String returnReason;
    /** 公积金种类（省直公积金、市直公积金）*/
    String providentFundName;

    /**
     * 所属部门
     */
    String subordinateDepartment;
}
