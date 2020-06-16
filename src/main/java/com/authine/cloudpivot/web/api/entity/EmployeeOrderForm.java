package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author liulei
 * @Description 员工订单表单实体
 * @ClassName com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
 * @Date 2020/2/21 10:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeOrderForm extends BaseEntity {
    String employeeName;
    /**
     * 备注
     */
    String detail;
    /**
     * 社保状态
     */
    String socialSecurityStatus;
    /**
     * 公积金状态
     */
    String providentFundStatus;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 员工订单
     */
    String employeeFilesId;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 证件类型
     */
    String idType;
    /**
     * 业务类型
     */
    String businessType;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 社保福利办理方
     */
    String sWelfareHandler;
    /**
     * 公积金利办理方
     */
    String gWelfareHandler;
    /**
     * 社保基数
     */
    Double socialSecurityBase = 0d;
    /**
     * 公积金基数
     */
    Double providentFundBase = 0d;
    /**
     * 社保起做时间
     */
    Date socialSecurityChargeStart;
    /**
     * 公积金起做时间
     */
    Date providentFundChargeStart;
    /**
     * 社保截止时间
     */
    Date socialSecurityChargeEnd;
    /**
     * 公积金截止时间
     */
    Date providentFundChargeEnd;
    /**
     * 是否预收
     */
    String precollected;
    /**
     * 收费频率
     */
    String payCycle;
    String sbBillYear;
    String gjjBillYear;
    /**
     * 汇缴订单明细
     */
    List <EmployeeOrderFormDetails> remittanceList;
    /**
     * 补缴订单明细
     */
    List <EmployeeOrderFormDetails> payBackList;

    // 辅助字段
    Double remittanceSortKey = 0d;
    Double payBackSortKey = 0d;

    public EmployeeOrderForm(AddEmployee addEmployee, String employeeFilesId) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
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
        this.sequenceStatus = "COMPLETED";
        this.ownerDeptQueryCode = addEmployee.getOwnerDeptQueryCode();

        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            // 有社保申报
            this.socialSecurityStatus = "待办";
            this.socialSecurityCity = addEmployee.getSocialSecurityCity();
            this.sWelfareHandler = addEmployee.getSWelfareHandler();
            this.socialSecurityBase = addEmployee.getSocialSecurityBase();
            this.socialSecurityChargeStart = addEmployee.getSocialSecurityStartTime();
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            // 有公积金申报
            this.providentFundStatus = "待办";
            this.providentFundCity = addEmployee.getProvidentFundCity();
            this.gWelfareHandler = addEmployee.getGWelfareHandler();
            this.providentFundBase = addEmployee.getProvidentFundBase();
            this.providentFundChargeStart = addEmployee.getProvidentFundStartTime();
        }
        this.employeeName = addEmployee.getEmployeeName();
        this.employeeFilesId = employeeFilesId;
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.idType = addEmployee.getIdentityNoType();
        this.businessType = addEmployee.getEmployeeNature();
        this.identityNo = addEmployee.getIdentityNo();
        /*this.precollected = precollected;
        this.payCycle = payCycle;
        this.remittanceList = remittanceList;
        this.payBackList = payBackList;*/
        this.detail = addEmployee.getRemark();
    }
}
