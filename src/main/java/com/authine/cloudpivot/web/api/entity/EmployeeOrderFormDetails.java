package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.web.api.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.EmployeeOrderFormDetails
 * @Date 2020/5/13 9:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeOrderFormDetails {
    /**
     * 主键id
     */
    String id;
    /**
     * 子表业务对象父类ID
     */
    String parentId;
    /**
     * 子表排序号
     */
    Double sortKey;
    /**
     * 产品名称
     */
    String productName;
    /**
     * 收费开始时间
     */
    Date startChargeTime;
    /**
     * 收费结束时间
     */
    Date endChargeTime;
    /**
     * 基数
     */
    Double companyBase;
    Double employeeBase;
    /**
     * 企业比例
     */
    Double companyRatio;
    /**
     * 个人比例
     */
    Double employeeRatio;
    /**
     * 总金额
     */
    Double sum;
    /**
     * 企业金额
     */
    Double companyMoney;
    /**
     * 个人金额
     */
    Double employeeMoney;
    /**
     * 企业附加金额
     */
    Double companySurchargeValue;
    /**
     * 个人附加金额
     */
    Double employeeSurchargeValue;

    public EmployeeOrderFormDetails(PaymentRules rules, Double base, NccpsWorkInjuryRatio workInjuryRatio, Double sortKey) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.sortKey = sortKey;
        this.productName = rules.getProductName();
        this.endChargeTime = rules.getEffectiveTerminationMonth();
        if(base > rules.getCompanyMaxBaseNum()) {
            this.companyBase = rules.getCompanyMaxBaseNum();
        } else if (base < rules.getCompanyMinBaseNum()) {
            this.companyBase = rules.getCompanyMinBaseNum();
        } else {
            this.companyBase = base;
        }
        if(base > rules.getEmployeeMaxBaseNum()) {
            this.employeeBase = rules.getEmployeeMaxBaseNum();
        } else if (base < rules.getEmployeeMinBaseNum()) {
            this.employeeBase = rules.getEmployeeMinBaseNum();
        } else {
            this.employeeBase = base;
        }
        if (rules.getProductName().indexOf("工伤") >= 0 && rules.getProductName().indexOf("补充") < 0
                && workInjuryRatio != null && workInjuryRatio.getWorkInjuryUnitRatio() != null) {
            this.companyRatio = workInjuryRatio.getWorkInjuryUnitRatio();
            this.employeeRatio = 0d;
        } else {
            this.companyRatio = rules.getCompanyRatio();
            this.employeeRatio = rules.getEmployeeRatio();
        }
        this.companySurchargeValue = rules.getCompanySurchargeValue();
        this.employeeSurchargeValue = rules.getEmployeeSurchargeValue();

        this.companyMoney = CommonUtils.processingData(this.companyBase * this.companyRatio + this.companySurchargeValue,
                rules.getCompanyRoundingPolicy(), rules.getCompanyPrecision());
        this.employeeMoney = CommonUtils.processingData(this.employeeBase * this.employeeRatio + this.employeeSurchargeValue,
                rules.getEmployeeRoundingPolicy(), rules.getEmployeePrecision());
        this.sum = this.companyMoney + this.employeeMoney;
    }

    public EmployeeOrderFormDetails(CollectionRule rules, Double base, NccpsWorkInjuryRatio workInjuryRatio, Double sortKey) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.sortKey = sortKey;
        this.productName = rules.getProductName();
        if(base > rules.getCompanyMaxBaseNum()) {
            this.companyBase = rules.getCompanyMaxBaseNum();
        } else if (base < rules.getCompanyMinBaseNum()) {
            this.companyBase = rules.getCompanyMinBaseNum();
        } else {
            this.companyBase = base;
        }
        if(base > rules.getEmployeeMaxBaseNum()) {
            this.employeeBase = rules.getEmployeeMaxBaseNum();
        } else if (base < rules.getEmployeeMinBaseNum()) {
            this.employeeBase = rules.getEmployeeMinBaseNum();
        } else {
            this.employeeBase = base;
        }
        if (rules.getProductName().indexOf("工伤") >= 0 && rules.getProductName().indexOf("补充") < 0
                && workInjuryRatio != null && workInjuryRatio.getWorkInjuryUnitRatio() != null) {
            this.companyRatio = workInjuryRatio.getWorkInjuryUnitRatio();
            this.employeeRatio = 0d;
        } else {
            this.companyRatio = rules.getCompanyRatio();
            this.employeeRatio = rules.getEmployeeRatio();
        }
        this.companySurchargeValue = rules.getCompanySurchargeValue();
        this.employeeSurchargeValue = rules.getEmployeeSurchargeValue();

        this.companyMoney = CommonUtils.processingData(this.companyBase * this.companyRatio + this.companySurchargeValue,
                rules.getCompanyRoundingPolicy(), rules.getCompanyPrecision());
        this.employeeMoney = CommonUtils.processingData(this.employeeBase * this.employeeRatio + this.employeeSurchargeValue,
                rules.getEmployeeRoundingPolicy(), rules.getEmployeePrecision());
        this.sum = this.companyMoney + this.employeeMoney;
    }

    public EmployeeOrderFormDetails(String parentId, Double sortKey, String productName,
                                    Date startChargeTime, Date endChargeTime, Double sum) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.parentId = parentId;
        this.sortKey = sortKey;
        this.productName = productName;
        this.startChargeTime = startChargeTime;
        this.endChargeTime = endChargeTime;
        this.sum = sum;
    }
}
