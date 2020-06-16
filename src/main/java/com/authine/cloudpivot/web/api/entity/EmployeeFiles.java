package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * @Author: wangyong
 * @Date: 2020-02-14 15:57
 * @Description: 员工档案实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFiles extends BaseEntity {
    String employeeFilesId;
    /**
     * 员工姓名
     */
    String employeeName;
    /**
     * 证件类型
     */
    String idType;
    /**
     * 证件号码
     */
    String idNo;
    /**
     * 性别
     */
    String gender;
    /**
     * 出生年月
     */
    Date birthDate;
    /**
     * 员工性质
     */
    String employeeNature;
    /**
     * 户籍性质
     */
    String householdRegisterNature;
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
     * 报入职时间
     */
    Date reportEntryTime;
    /**
     * 报入职人
     */
    String reportRecruits;
    /**
     * 入职日期
     */
    Date entryTime;
    /**
     * 社保收费开始
     */
    Date socialSecurityChargeStart;
    /**
     * 公积金收费开始
     */
    Date providentFundChargeStart;
    /**
     * 入职备注
     */
    String entryDescription;
    /**
     * 报离职时间
     */
    Date reportQuitDate;
    /**
     * 报离职人
     */
    String reportSeveranceOfficer;
    /**
     * 离职日期
     */
    Date quitDate;
    /**
     * 社保收费截止
     */
    Date socialSecurityChargeEnd;
    /**
     * 公积金收费截止
     */
    Date providentFundChargeEnd;
    /**
     * 离职原因
     */
    String quitReason;
    /**
     * 离职备注
     */
    String quitRemark;
    /**
     * 是否入职通知
     */
    String entryNotice;
    /**
     * 是否体检
     */
    String healthCheck;
    /**
     * 是否发薪
     */
    String whetherPay;
    /**
     * 职位
     */
    String position;
    /**
     * 邮箱
     */
    String email;
    /**
     * 员工标签
     */
    String employeeLabels;
    /**
     * 停止生成账单
     */
    Integer stopGenerateBill;
    /**
     * 是否老员工
     */
    Integer isOldEmployee;
    /**
     * 社保账号
     */
    String socialSecurityNum;
    /**
     * 公积金账号
     */
    String providentFundNum;
    /**
     * 社保支付账单年月
     */
    String sPaymentApplication;
    /**
     * 公积金支付账单年月
     */
    String gPaymentApplication;
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
     * 社保基数
     */
    Double socialSecurityBase = 0d;
    /**
     * 公积金基数
     */
    Double providentFundBase = 0d;
    /**
     * 社保福利办理方
     */
    String sWelfareHandler;
    /**
     * 公积金福利办理方
     */
    String gWelfareHandler;
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
     * 操作人
     */
    String operator;
    /**
     * 查询人
     */
    String inquirer;
    /**
     * 社保增员表单id
     */
    String sbAddEmployeeId;
    /**
     * 公积金增员表单id
     */
    String gjjAddEmployeeId;
    /**
     * 社保减员表单id
     */
    String sbDelEmployeeId;
    /**
     * 公积金减员表单id
     */
    String gjjDelEmployeeId;
    /**
     * 所属部门
     */
    String subordinateDepartment;

    public EmployeeFiles(AddEmployee addEmployee) {
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

        this.employeeName = addEmployee.getEmployeeName();
        this.idType = addEmployee.getIdentityNoType();
        this.idNo = addEmployee.getIdentityNo();
        this.gender = addEmployee.getGender();
        this.birthDate = addEmployee.getBirthday();
        this.employeeNature = addEmployee.getEmployeeNature();
        this.householdRegisterNature = addEmployee.getFamilyRegisterNature();
        this.mobile = addEmployee.getMobile();
        this.reportEntryTime = addEmployee.getCreatedTime();
        this.reportRecruits = "[{\"id\":\"" + addEmployee.getCreater() + "\",\"type\":3}]";
        this.entryTime = addEmployee.getEntryTime();
        this.entryDescription = addEmployee.getRemark();
        this.email = addEmployee.getEmail();
        this.stopGenerateBill = 0;
        this.isOldEmployee = 0;
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.householdRegisterRemarks = addEmployee.getHouseholdRegisterRemarks();
        this.isRetiredSoldier = addEmployee.getIsRetiredSoldier();
        this.isPoorArchivists = addEmployee.getIsPoorArchivists();
        this.isDisabled = addEmployee.getIsDisabled();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            this.sbAddEmployeeId = addEmployee.getId();
            this.socialSecurityBase = addEmployee.getSocialSecurityBase();
            this.sWelfareHandler = addEmployee.getSWelfareHandler();
            this.socialSecurityCity = addEmployee.getSocialSecurityCity();
            this.socialSecurityChargeStart = addEmployee.getSocialSecurityStartTime();
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            this.gjjAddEmployeeId = addEmployee.getId();
            this.providentFundBase = addEmployee.getProvidentFundBase();
            this.gWelfareHandler = addEmployee.getGWelfareHandler();
            this.providentFundCity = addEmployee.getProvidentFundCity();
            this.providentFundChargeStart = addEmployee.getProvidentFundStartTime();
        }
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
    }

    public EmployeeFiles(ShAddEmployee addEmployee) {
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

        this.employeeName = addEmployee.getEmployeeName();
        this.idType = addEmployee.getIdentityNoType();
        this.idNo = addEmployee.getIdentityNo();
        this.gender = addEmployee.getGender();
        this.birthDate = addEmployee.getBirthday();
        this.employeeNature = "代理";
        this.mobile = addEmployee.getMobile();
        this.reportEntryTime = addEmployee.getCreatedTime();
        this.reportRecruits = "[{\"id\":\"" + addEmployee.getCreater() + "\",\"type\":3}]";
        this.entryTime = addEmployee.getEntryTime();
        this.entryDescription = addEmployee.getInductionRemark();
        this.email = addEmployee.getMail();
        this.stopGenerateBill = 0;
        this.isOldEmployee = 0;
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.householdRegisterRemarks = addEmployee.getHouseholdRegisterRemarks();
        this.isRetiredSoldier = addEmployee.getIsRetiredSoldier();
        this.isPoorArchivists = addEmployee.getIsPoorArchivists();
        this.isDisabled = addEmployee.getIsDisabled();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            this.sbAddEmployeeId = addEmployee.getId();
            this.socialSecurityCity = addEmployee.getCityName();
            this.socialSecurityChargeStart = addEmployee.getBenefitStartTime();
            this.socialSecurityBase = addEmployee.getSocialSecurityBase();
            this.sWelfareHandler = addEmployee.getWelfareHandler();
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            this.gjjAddEmployeeId = addEmployee.getId();
            this.providentFundCity = addEmployee.getCityName();
            this.providentFundChargeStart = addEmployee.getProvidentFundStartTime();
            this.providentFundBase = addEmployee.getProvidentFundBase();
            this.gWelfareHandler = addEmployee.getWelfareHandler();
        }
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
    }

    public EmployeeFiles(NationwideDispatch addEmployee) {
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

        this.employeeName = addEmployee.getEmployeeName();
        this.idType = addEmployee.getIdentityNoType();
        this.idNo = addEmployee.getIdentityNo();
        this.gender = addEmployee.getGender();
        this.birthDate = addEmployee.getBirthday();
        this.employeeNature = "代理";
        this.mobile = addEmployee.getContactNumber();
        this.reportEntryTime = addEmployee.getCreatedTime();
        this.reportRecruits = "[{\"id\":\"" + addEmployee.getCreater() + "\",\"type\":3}]";
        this.entryTime = addEmployee.getEntryDate();
        this.entryDescription = addEmployee.getRemark();
        this.email = addEmployee.getEmployeeEmail();
        this.stopGenerateBill = 0;
        this.isOldEmployee = 0;
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.householdRegisterRemarks = addEmployee.getHouseholdRegisterRemarks();
        this.isRetiredSoldier = addEmployee.getIsRetiredSoldier();
        this.isPoorArchivists = addEmployee.getIsPoorArchivists();
        this.isDisabled = addEmployee.getIsDisabled();
        this.operator = addEmployee.getOperator();
        this.inquirer = addEmployee.getInquirer();
        if (addEmployee.getSocialInsuranceAmount() - 0d > 0d) {
            this.sbAddEmployeeId = addEmployee.getId();
            this.socialSecurityCity = addEmployee.getInvolved();
            this.socialSecurityChargeStart = addEmployee.getSServiceFeeStartDate();
            this.socialSecurityBase = addEmployee.getSocialInsuranceAmount();
            this.sWelfareHandler = addEmployee.getWelfareHandler();
        }
        if (addEmployee.getProvidentFundAmount() - 0d > 0d) {
            this.gjjAddEmployeeId = addEmployee.getId();
            this.providentFundCity = addEmployee.getInvolved();
            this.providentFundChargeStart = addEmployee.getGServiceFeeStartDate();
            this.providentFundBase = addEmployee.getProvidentFundAmount();
            this.gWelfareHandler = addEmployee.getWelfareHandler();
        }
        this.subordinateDepartment = addEmployee.getSubordinateDepartment();
    }

}
