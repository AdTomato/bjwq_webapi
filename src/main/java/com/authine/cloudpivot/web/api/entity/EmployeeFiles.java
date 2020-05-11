package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
     * 所属部门
     */
    String subordinateDepartment;
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
    Double socialSecurityBase;
    /**
     * 公积金基数
     */
    Double providentFundBase;
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

    public EmployeeFiles(String sequenceStatus,String creater, String createdDeptId, Date createdTime, String owner, String ownerDeptId, String ownerDeptQueryCode,
                         String employeeName, String idType, String idNo, String gender, Date birthDate,
                         String employeeNature, String householdRegisterNature, String mobile,
                         String socialSecurityCity, String providentFundCity, Date reportEntryTime,
                         String reportRecruits, Date entryTime, Date socialSecurityChargeStart,
                         Date providentFundChargeStart, String entryDescription, String email,
                         int stopGenerateBill, int isOldEmployee, String subordinateDepartment,
                         String firstLevelClientName, String secondLevelClientName, String householdRegisterRemarks,
                         Double socialSecurityBase, Double providentFundBase, String sWelfareHandler,
                         String gWelfareHandler, String isRetiredSoldier, String isPoorArchivists, String isDisabled) {
        this.creater = creater;
        this.createdDeptId = createdDeptId;
        this.createdTime = createdTime;
        this.sequenceStatus = sequenceStatus;
        this.owner = owner;
        this.ownerDeptId = ownerDeptId;
        this.ownerDeptQueryCode = ownerDeptQueryCode;
        this.employeeName = employeeName;
        this.idType = idType;
        this.idNo = idNo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.employeeNature = employeeNature;
        this.householdRegisterNature = householdRegisterNature;
        this.mobile = mobile;
        this.socialSecurityCity = socialSecurityCity;
        this.providentFundCity = providentFundCity;
        this.reportEntryTime = reportEntryTime;
        this.reportRecruits = reportRecruits;
        this.entryTime = entryTime;
        this.socialSecurityChargeStart = socialSecurityChargeStart;
        this.providentFundChargeStart = providentFundChargeStart;
        this.entryDescription = entryDescription;
        this.email = email;
        this.stopGenerateBill = stopGenerateBill;
        this.isOldEmployee = isOldEmployee;
        this.subordinateDepartment = subordinateDepartment;
        this.firstLevelClientName = firstLevelClientName;
        this.secondLevelClientName = secondLevelClientName;
        this.householdRegisterRemarks = householdRegisterRemarks;
        this.socialSecurityBase = socialSecurityBase;
        this.providentFundBase = providentFundBase;
        this.sWelfareHandler = sWelfareHandler;
        this.gWelfareHandler = gWelfareHandler;
        this.isRetiredSoldier = isRetiredSoldier;
        this.isPoorArchivists = isPoorArchivists;
        this.isDisabled = isDisabled;
    }
}
