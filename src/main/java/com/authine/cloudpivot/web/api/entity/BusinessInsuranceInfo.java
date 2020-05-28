package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * @author liulei
 * @Description 商保人员信息
 * @ClassName com.authine.cloudpivot.web.api.entity.BusinessInsuranceInfo
 * @Date 2020/5/7 14:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessInsuranceInfo extends BaseEntity {
    /**
     * 业务员
     */
    String salesman;
    /**
     * 部门
     */
    String department;
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 身份证号码
     */
    String identityNo;
    /**
     * 银行卡号
     */
    String bankCardNumber;
    /**
     * 开户行
     */
    String bank;
    /**
     * 被保险人手机号
     */
    String mobile;
    /**
     * 商保套餐等级
     */
    String level;
    /**
     * 商保服务费
     */
    Double serviceFee;
    /**
     * 商保生效日
     */
    Date effectiveDate;
    /**
     * 商保套餐内容
     */
    String content;
    /**
     * 员工状态
     */
    String employeeStatus;
    /**
     * 福利截止时间
     */
    Date benefitDeadline;
    /**
     * 备注
     */
    String remarks;
    /**
     * 员工档案
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

    public BusinessInsuranceInfo(String employeeFilesId, AddBusinessInsurance insurance) {
        super(UUID.randomUUID().toString().replaceAll("-", ""), insurance.getName(), insurance.getCreater(), insurance.getCreatedDeptId(), insurance.getOwner(), insurance.getOwnerDeptId(), new Date(), insurance.getModifier(), new Date(),
                null, null, "COMPLETED", insurance.getOwnerDeptQueryCode());
        this.salesman = insurance.getSalesman();
        this.department = insurance.getDepartment();
        this.employeeName = insurance.getEmployeeName();
        this.identityNo = insurance.getIdentityNo();
        this.bankCardNumber = insurance.getBankCardNumber();
        this.bank = insurance.getBank();
        this.mobile = insurance.getMobile();
        this.level = insurance.getLevel();
        this.serviceFee = insurance.getServiceFee();
        this.effectiveDate = insurance.getEffectiveDate();
        this.content = insurance.getContent();
        this.employeeStatus = "在职";
        this.employeeFilesId = employeeFilesId;
        this.firstLevelClientName = insurance.getFirstLevelClientName();
        this.secondLevelClientName = insurance.getSecondLevelClientName();
    }
}
