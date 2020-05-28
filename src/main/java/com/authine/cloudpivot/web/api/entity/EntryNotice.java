package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.EntryNotice
 * @Date 2020/2/26 11:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryNotice extends BaseEntity {
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 身份证号码
     */
    String identityNo;
    /**
     * 社保
     */
    String socialSecurity;
    /**
     * 公积金
     */
    String providentFund;
    /**
     * 递交方式
     */
    String submissionMethod;
    /**
     * 运行签收人
     */
    String operateSignatory;
    /**
     * 运行签收
     */
    String operateSignFor;
    /**
     * 单位公积金账号
     */
    String unitProvidentFundNum;
    /**
     * 个人公积金账号
     */
    String personalProvidentFundNum;
    /**
     * 就业登记证账号
     */
    String employmentRegisterNum;
    /**
     * 入职联系备注
     */
    String entryContactRemark;
    /**
     * 操作人
     */
    String operator;
    /**
     * 操作人
     */
    String inquirer;
    /**
     * 一级客户
     */
    String firstLevelClientName;
    /**
     * 二级客户
     */
    String secondLevelClientName;
    /**
     * 联系电话
     */
    String mobile;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 退役士兵
     */
    String isRetiredSoldier;
    /**
     * 残疾人
     */
    String isDisabled;
    /**
     * 贫困建档人员
     */
    String isPoorArchivists;
    /**
     * 用工备案
     */
    String recordOfEmployment;
    /**
     * 运行反馈
     */
    String feedback;
    /**
     * 入职联系状态
     */
    String status;

    String subordinateDepartment;

    public EntryNotice(AddEmployee addEmployee) {
        this.name = addEmployee.getName();
        this.creater = addEmployee.getCreater();
        this.createdDeptId = addEmployee.getCreatedDeptId();
        this.owner = addEmployee.getOwner();
        this.ownerDeptId = addEmployee.getOwnerDeptId();
        this.createdTime = addEmployee.getCreatedTime();
        this.modifier = addEmployee.getModifier();
        this.modifiedTime = addEmployee.getModifiedTime();
        this.workflowInstanceId = null;
        this.sequenceNo = null;
        this.sequenceStatus = "PROCESSING";
        this.ownerDeptQueryCode = addEmployee.getOwnerDeptQueryCode();

        this.employeeName = addEmployee.getEmployeeName();
        this.identityNo = addEmployee.getIdentityNo();
        /*this.socialSecurity = socialSecurity;
        this.providentFund = providentFund;
        this.submissionMethod = submissionMethod;
        this.operateSignatory = operateSignatory;
        this.operateSignFor = operateSignFor;
        this.unitProvidentFundNum = unitProvidentFundNum;
        this.personalProvidentFundNum = personalProvidentFundNum;
        this.employmentRegisterNum = employmentRegisterNum;*/
        this.entryContactRemark = addEmployee.getRemark();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.mobile = addEmployee.getMobile();
        this.socialSecurityCity = addEmployee.getSocialSecurityCity();
        this.providentFundCity = addEmployee.getProvidentFundCity();
        this.isRetiredSoldier = addEmployee.getIsRetiredSoldier();
        this.isDisabled = addEmployee.getIsDisabled();
        this.isPoorArchivists = addEmployee.getIsPoorArchivists();
        /*this.recordOfEmployment = recordOfEmployment;
        this.feedback = feedback;*/
        this.status = "通知中";
    }
}
