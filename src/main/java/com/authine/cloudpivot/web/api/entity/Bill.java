package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill extends BaseEntity {
    /**
     * supplier	varchar(200)
     * business_year	varchar(200)
     * client_name	varchar(200)
     * employee_name	varchar(200)
     * id_no	varchar(200)
     * unique_num	varchar(200)
     * delegated_area	varchar(200)
     */
    private String supplier;
    private String billYear;
    private String businessYear;
    private String clientName;
    private String employeeName;
    private String idNo;
    private String uniqueNum;
    private String delegatedArea;

    /**
     * pension_enterprise_base	decimal(19,6)
     * pension_enterprise_ratio	decimal(19,6)
     * pension_enterprise_attach	decimal(19,6)
     * pension_enterprise_pay	decimal(19,6)
     * pension_personal_base	decimal(19,6)
     * pension_personal_ratio	decimal(19,6)
     * pension_personal_attach	decimal(19,6)
     * pension_personal_pay	decimal(19,6)
     * pension_subtotal	decimal(19,6)
     * pension_payment_method	varchar(200)
     */
    private Double pensionEnterpriseBase;
    private Double pensionEnterpriseRatio;
    private Double pensionEnterpriseAttach;
    private Double pensionEnterprisePay;
    private Double pensionPersonalBase;
    private Double pensionPersonalRatio;
    private Double pensionPersonalAttach;
    private Double pensionPersonalPay;
    private Double pensionSubtotal;
    private String pensionPaymentMethod;

    /**
     * medical_enterprise_base	decimal(19,6)
     * medical_enterprise_ratio	decimal(19,6)
     * medical_enterprise_attach	decimal(19,6)
     * medical_enterprise_pay	decimal(19,6)
     * medical_personal_base	decimal(19,6)
     * medical_personal_ratio	decimal(19,6)
     * medical_personal_attach	decimal(19,6)
     * medical_personal_pay	decimal(19,6)
     * medical_subtotal	decimal(19,6)
     * medical_payment_method	varchar(200)
     */
    private Double medicalEnterpriseBase;
    private Double medicalEnterpriseRatio;
    private Double medicalEnterpriseAttach;
    private Double medicalEnterprisePay;
    private Double medicalPersonalBase;
    private Double medicalPersonalRatio;
    private Double medicalPersonalAttach;
    private Double medicalPersonalPay;
    private Double medicalSubtotal;
    private String medicalPaymentMethod;

    /**
     * unemp_enterprise_base	decimal(19,6)
     * unemp_enterprise_ratio	decimal(19,6)
     * unemp_enterprise_attach	decimal(19,6)
     * unemp_enterprise_pay	decimal(19,6)
     * unemp_personal_base	decimal(19,6)
     * unemp_personal_ratio	decimal(19,6)
     * unemp_personal_attach	decimal(19,6)
     * unemp_personal_pay	decimal(19,6)
     * unemp_subtotal	decimal(19,6)
     * unemp_payment_method	varchar(200)
     */
    private Double unempEnterpriseBase;
    private Double unempEnterpriseRatio;
    private Double unempEnterpriseAttach;
    private Double unempEnterprisePay;
    private Double unempPersonalBase;
    private Double unempPersonalRatio;
    private Double unempPersonalAttach;
    private Double unempPersonalPay;
    private Double unempSubtotal;
    private String unempPaymentMethod;

    /**
     * injury_enterprise_base	decimal(19,6)
     * injury_enterprise_ratio	decimal(19,6)
     * injury_enterprise_attach	decimal(19,6)
     * injury_enterprise_pay	decimal(19,6)
     * injury_subtotal	decimal(19,6)
     * injury_payment_method	varchar(200)
     */
    private Double injuryEnterpriseBase;
    private Double injuryEnterpriseRatio;
    private Double injuryEnterpriseAttach;
    private Double injuryEnterprisePay;
    private Double injurySubtotal;
    private String injuryPaymentMethod;

    /**
     * fertility_enterprise_base	decimal(19,6)
     * fertility_enterprise_ratio	decimal(19,6)
     * fertility_enterprise_attach	decimal(19,6)
     * fertility_enterprise_pay	decimal(19,6)
     * fertility_subtotal	decimal(19,6)
     * fertility_payment_method	varchar(200)
     */
    private Double fertilityEnterpriseBase;
    private Double fertilityEnterpriseRatio;
    private Double fertilityEnterpriseAttach;
    private Double fertilityEnterprisePay;
    private Double fertilitySubtotal;
    private String fertilityPaymentMethod;

    /**
     * d_medical_enterprise_base	decimal(19,6)
     * d_medical_enterprise_ratio	decimal(19,6)
     * d_medical_enterprise_attach	decimal(19,6)
     * d_medical_enterprise_pay	decimal(19,6)
     * d_medical_personal_base	decimal(19,6)
     * d_medical_personal_ratio	decimal(19,6)
     * d_medical_personal_attach	decimal(19,6)
     * d_medical_personal_pay	decimal(19,6)
     * d_medical_subtotal	decimal(19,6)
     * d_medical_payment_method	varchar(200)
     */
    private Double dMedicalEnterpriseBase;
    private Double dMedicalEnterpriseRatio;
    private Double dMedicalEnterpriseAttach;
    private Double dMedicalEnterprisePay;
    private Double dMedicalPersonalBase;
    private Double dMedicalPersonalRatio;
    private Double dMedicalPersonalAttach;
    private Double dMedicalPersonalPay;
    private Double dMedicalSubtotal;
    private String dMedicalPaymentMethod;

    /**
     * complex_enterprise_base	decimal(19,6)
     * complex_enterprise_ratio	decimal(19,6)
     * complex_enterprise_attach	decimal(19,6)
     * complex_enterprise_pay	decimal(19,6)
     * complex_personal_base	decimal(19,6)
     * complex_personal_ratio	decimal(19,6)
     * complex_personal_attach	decimal(19,6)
     * complex_personal_pay	decimal(19,6)
     * complex_subtotal	decimal(19,6)
     * complex_payment_method	varchar(200)
     */
    private Double complexEnterpriseBase;
    private Double complexEnterpriseRatio;
    private Double complexEnterpriseAttach;
    private Double complexEnterprisePay;
    private Double complexPersonalBase;
    private Double complexPersonalRatio;
    private Double complexPersonalAttach;
    private Double complexPersonalPay;
    private Double complexSubtotal;
    private String complexPaymentMethod;

    /**
     * b_injury_enterprise_base	decimal(19,6)
     * b_injury_enterprise_ratio	decimal(19,6)
     * b_injury_enterprise_attach	decimal(19,6)
     * b_injury_enterprise_pay	decimal(19,6)
     * b_injury_subtotal	decimal(19,6)
     * b_injury_payment_method	varchar(200)
     */
    private Double bInjuryEnterpriseBase;
    private Double bInjuryEnterpriseRatio;
    private Double bInjuryEnterpriseAttach;
    private Double bInjuryEnterprisePay;
    private Double bInjurySubtotal;
    private String bInjuryPaymentMethod;

    /**
     * social_security_enterprise	decimal(19,6)
     * social_security_personal	decimal(19,6)
     * social_security_total	decimal(19,6)
     */
    private Double socialSecurityEnterprise;
    private Double socialSecurityPersonal;
    private Double socialSecurityTotal;

    /**
     * provident_enterprise_base	decimal(19,6)
     * provident_enterprise_ratio	decimal(19,6)
     * provident_enterprise_pay	decimal(19,6)
     * provident_personal_base	decimal(19,6)
     * provident_personal_ratio	decimal(19,6)
     * provident_personal_pay	decimal(19,6)
     * provident_subtotal	decimal(19,6)
     * provident_payment_method	varchar(200)
     */
    private Double providentEnterpriseBase;
    private Double providentEnterpriseRatio;
    private Double providentEnterprisePay;
    private Double providentPersonalBase;
    private Double providentPersonalRatio;
    private Double providentPersonalPay;
    private Double providentSubtotal;
    private String providentPaymentMethod;

    /**
     * b_provident_enterprise_base	decimal(19,6)
     * b_provident_enterprise_ratio	decimal(19,6)
     * b_provident_enterprise_pay	decimal(19,6)
     * b_provident_personal_base	decimal(19,6)
     * b_provident_personal_ratio	decimal(19,6)
     * b_provident_personal_pay	decimal(19,6)
     * b_provident_subtotal	decimal(19,6)
     * b_provident_payment_method	varchar(200)
     */
    private Double bProvidentEnterpriseBase;
    private Double bProvidentEnterpriseRatio;
    private Double bProvidentEnterprisePay;
    private Double bProvidentPersonalBase;
    private Double bProvidentPersonalRatio;
    private Double bProvidentPersonalPay;
    private Double bProvidentSubtotal;
    private String bProvidentPaymentMethod;

    /**
     * provident_enterprise	decimal(19,6)
     * provident_personal	decimal(19,6)
     * provident_total	decimal(19,6)
     */
    private Double providentEnterprise;
    private Double providentPersonal;
    private Double providentTotal;

    /**
     * y_toll_total	decimal(19,6)
     * y_toll_remark	varchar(200)
     */
    private Double yTollTotal;
    private String yTollRemark;

    /**
     * y_social_enterprise_d	decimal(19,6)
     * y_social_personal_d	decimal(19,6)
     * y_social_total_d	decimal(19,6)
     */
    private Double ySocialEnterpriseD;
    private Double ySocialPersonalD;
    private Double ySocialTotalD;

    /**
     * y_provident_enterprise_d	decimal(19,6)
     * y_provident_personal_d	decimal(19,6)
     * y_provident_total_d	decimal(19,6)
     */
    private Double yProvidentEnterpriseD;
    private Double yProvidentPersonalD;
    private Double yProvidentTotalD;

    /**
     * y_social_enterprise_t	decimal(19,6)
     * y_social_personal_t	decimal(19,6)
     * y_social_total_t	decimal(19,6)
     */
    private Double ySocialEnterpriseT;
    private Double ySocialPersonalT;
    private Double ySocialTotalT;


    /**
     * 一次性公积金企业-托收
     */
    private Double yProvidentEnterpriseT;
    /**
     * 一次性公积金个人-托收
     */
    private Double yProvidentPersonalT;
    /**
     * 一次性公积金合计-托收
     */
    private Double yProvidentTotalT;

    /**
     * 社保公积金合计
     */
    private Double socialProvidentTotal;

    /**
     * 增值税税费
     */
    private Double vatTax;
    /**
     * 福利产品总额
     */
    private Double totalWelfareProducts;
    /**
     * 风险管理费
     */
    private Double riskManageFee;
    /**
     * 外包管理费
     */
    private Double outsourcingManageFee;
    /**
     * 营业税税费
     */
    private Double businessTax;
    /**
     * 合计收费
     */
    private Double totalCharge;

    /**
     * 是否确定
     */
    private Integer whetherDefine;
    /**
     * 是否差异数据
     */
    private Integer whetherDifferenceData;
    /**
     * 是否对比
     */
    private Integer whetherCompare;
    /**
     * 是否锁定
     */
    private Integer isLock;
    /**
     * 关联查询应收
     */
    private String relationEnquiryReceivable;
    /**
     * 关联员工档案
     */
    private String relationEmployeeFiles;

}
