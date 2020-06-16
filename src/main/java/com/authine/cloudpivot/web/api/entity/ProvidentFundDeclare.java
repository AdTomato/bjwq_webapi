package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 公积金申报实体
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
 * @Date 2020/2/25 16:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvidentFundDeclare extends BaseEntity {
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 性别
     */
    String gender;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 起始月
     */
    Date startMonth;
    /**
     * 公积金基数
     */
    Double providentFundBase;
    /**
     * 企业缴存额
     */
    Double corporatePayment;
    /**
     * 个人缴存额
     */
    Double personalDeposit;
    /**
     * 缴存总额
     */
    Double totalDeposit;
    /**
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 出生日期
     */
    Date birthday;
    /**
     * 证件类型
     */
    String identityNoType;
    /**
     * 运行负责人
     */
    String operateLeader;
    /**
     * 员工订单
     */
    String employeeOrderFormId;
    /**
     * 状态
     */
    String status;
    /**
     * 已有退回原因
     */
    String returnReasonAlready;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 福利地
     */
    String city;
    /**
     * 操作人
     */
    String operator;
    /**
     * 查询人
     */
    String inquirer;
    /**
     * 员工档案
     */
    String employeeFilesId;
    /**
     * 增员表单id
     */
    String addEmployeeId;
    /**
     * 所属部门
     */
    String subordinateDepartment;

    /**
     * 单位公积金比例
     */
    Double companyProvidentFundBl;
    /**
     * 个人公积金比例
     */
    Double employeeProvidentFundBl;

    String employeeNature;

    String billYear;

    /**
     * 汇缴订单明细
     */
    List <EmployeeOrderFormDetails> remittanceList;
    /**
     * 补缴订单明细
     */
    List <EmployeeOrderFormDetails> payBackList;

    public ProvidentFundDeclare(AddEmployee addEmployee, String employeeFilesId) {
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
        this.gender = addEmployee.getGender();
        this.identityNo = addEmployee.getIdentityNo();
        this.startMonth = addEmployee.getProvidentFundStartTime();
        this.providentFundBase = addEmployee.getProvidentFundBase();
        /*this.corporatePayment = corporatePayment;
        this.personalDeposit = personalDeposit;
        this.totalDeposit = totalDeposit;*/
        this.welfareHandler = addEmployee.getGWelfareHandler();
        this.birthday = addEmployee.getBirthday();
        this.identityNoType = addEmployee.getIdentityNoType();
        /*this.operateLeader = operateLeader;
        this.employeeOrderFormId = employeeOrderFormId;*/
        this.status = "待办";
        /*this.returnReasonAlready = returnReasonAlready;*/
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.city = addEmployee.getProvidentFundCity();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        this.employeeFilesId = employeeFilesId;
        this.addEmployeeId = addEmployee.getId();
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
        /*this.remittanceList = remittanceList;
        this.payBackList = payBackList;*/
        this.employeeNature = addEmployee.getEmployeeNature();
        this.companyProvidentFundBl = addEmployee.getCompanyProvidentFundBl();
        this.employeeProvidentFundBl = addEmployee.getEmployeeProvidentFundBl();
    }
}
