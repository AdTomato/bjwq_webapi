package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 社保申报实体
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
 * @Date 2020/2/25 16:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialSecurityDeclare extends BaseEntity {
    /**
     * 起始月
     */
    Date startMonth;
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
     * 合同签订日期
     */
    Date contractSigningDate;
    /**
     * 合同截止日期
     */
    Date contractDeadline;
    /**
     * 转正工资
     */
    Double positiveSalary;
    /**
     * 缴费基数
     */
    Double basePay;
    /**
     * 手机号码
     */
    String mobile;
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
     * 已有退回原因
     */
    String returnReasonAlready;
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
     * 账单年月
     */
    String billYear;
    /**
     * 备注
     */
    String remark;
    /**
     * 操作人
     */
    String operator;
    /**
     * 查询人
     */
    String inquirer;
    /**
     * 所属部门
     */
    String subordinateDepartment;
    /**
     * 员工档案
     */
    String employeeFilesId;
    /**
     * 增员表单id
     */
    String addEmployeeId;
    /**
     * 汇缴订单明细
     */
    List <EmployeeOrderFormDetails> remittanceList;
    /**
     * 补缴订单明细
     */
    List <EmployeeOrderFormDetails> payBackList;

    public SocialSecurityDeclare(AddEmployee addEmployee, String employeeFilesId) {
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

        this.startMonth = addEmployee.getSocialSecurityStartTime();
        this.employeeName = addEmployee.getEmployeeName();
        this.gender = addEmployee.getGender();
        this.identityNo = addEmployee.getIdentityNo();
        this.contractSigningDate = addEmployee.getContractStartTime();
        this.contractDeadline = addEmployee.getContractEndTime();
        this.positiveSalary = addEmployee.getContractSalary();
        this.basePay = addEmployee.getSocialSecurityBase();
        this.mobile = addEmployee.getMobile();
        this.welfareHandler = addEmployee.getSWelfareHandler();
        this.birthday = addEmployee.getBirthday();
        this.identityNoType = addEmployee.getIdentityNoType();
        /*this.returnReasonAlready = returnReasonAlready;
        this.operateLeader = operateLeader;
        this.employeeOrderFormId = employeeOrderFormId;
        this.isChange = isChange;*/
        this.status = "待办";
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.city = addEmployee.getSocialSecurityCity();
        /*this.billYear = billYear;*/
        this.remark = addEmployee.getRemark();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
        this.employeeFilesId = employeeFilesId;
        this.addEmployeeId = addEmployee.getId();
        /*this.remittanceList = remittanceList;
        this.payBackList = payBackList;*/
    }
}
