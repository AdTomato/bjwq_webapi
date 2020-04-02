package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill extends BaseEntity {
    /**
     * 主供应商
     */
    private String supplier;
    /**
     * 账单年月
     */
    private String billYear;
    /**
     * 业务年月
     */
    private String businessYear;
    /**
     * 客户名称
     */
    private String clientName;
    /**
     * 雇员姓名
     */
    private String employeeName;
    /**
     * 雇员证件号
     */
    private String idNo;
    /**
     * 雇员唯一号
     */
    private String uniqueNum;
    /**
     * 委派地区
     */
    private String delegatedArea;


    /**
     * 养老企业基数
     */
    private Double pensionEnterpriseBase;
    /**
     * 养老企业比例
     */
    private Double pensionEnterpriseRatio;
    /**
     * 养老企业附加
     */
    private Double pensionEnterpriseAttach;
    /**
     * 养老企业缴纳
     */
    private Double pensionEnterprisePay;
    /**
     * 养老个人基数
     */
    private Double pensionPersonalBase;
    /**
     * 养老个人比例
     */
    private Double pensionPersonalRatio;
    /**
     * 养老个人附加
     */
    private Double pensionPersonalAttach;
    /**
     * 养老个人缴纳
     */
    private Double pensionPersonalPay;
    /**
     * 养老缴纳小计
     */
    private Double pensionSubtotal;
    /**
     * 养老支付方式
     */
    private String pensionPaymentMethod;


    /**
     * 医疗企业基数
     */
    private Double medicalEnterpriseBase;
    /**
     * 医疗企业比例
     */
    private Double medicalEnterpriseRatio;
    /**
     * 医疗企业附加
     */
    private Double medicalEnterpriseAttach;
    /**
     * 医疗企业缴纳
     */
    private Double medicalEnterprisePay;
    /**
     * 医疗个人基数
     */
    private Double medicalPersonalBase;
    /**
     * 医疗个人比例
     */
    private Double medicalPersonalRatio;
    /**
     * 医疗个人附加
     */
    private Double medicalPersonalAttach;
    /**
     * 医疗个人缴纳
     */
    private Double medicalPersonalPay;
    /**
     * 医疗缴纳小计
     */
    private Double medicalSubtotal;
    /**
     * 医疗支付方式
     */
    private String medicalPaymentMethod;


    /**
     * 失业企业基数
     */
    private Double unempEnterpriseBase;
    /**
     * 失业企业比例
     */
    private Double unempEnterpriseRatio;
    /**
     * 失业企业附加
     */
    private Double unempEnterpriseAttach;
    /**
     * 失业企业缴纳
     */
    private Double unempEnterprisePay;
    /**
     * 失业个人基数
     */
    private Double unempPersonalBase;
    /**
     * 失业个人比例
     */
    private Double unempPersonalRatio;
    /**
     * 失业个人附加
     */
    private Double unempPersonalAttach;
    /**
     * 失业个人缴纳
     */
    private Double unempPersonalPay;
    /**
     * 失业缴纳小计
     */
    private Double unempSubtotal;
    /**
     * 失业支付方式
     */
    private String unempPaymentMethod;


    /**
     * 工伤企业基数
     */
    private Double injuryEnterpriseBase;
    /**
     * 工伤企业比例
     */
    private Double injuryEnterpriseRatio;
    /**
     * 工伤企业附加
     */
    private Double injuryEnterpriseAttach;
    /**
     * 工伤企业缴纳
     */
    private Double injuryEnterprisePay;
    /**
     * 工伤缴纳小计
     */
    private Double injurySubtotal;
    /**
     * 工伤支付方式
     */
    private String injuryPaymentMethod;


    /**
     * 生育企业基数
     */
    private Double fertilityEnterpriseBase;
    /**
     * 生育企业比例
     */
    private Double fertilityEnterpriseRatio;
    /**
     * 生育企业附加
     */
    private Double fertilityEnterpriseAttach;
    /**
     * 生育企业缴纳
     */
    private Double fertilityEnterprisePay;
    /**
     * 生育缴纳小计
     */
    private Double fertilitySubtotal;
    /**
     * 生育支付方式
     */
    private String fertilityPaymentMethod;


    /**
     * 大病医疗企业基数
     */
    private Double dMedicalEnterpriseBase;
    /**
     * 大病医疗企业比例
     */
    private Double dMedicalEnterpriseRatio;
    /**
     * 大病医疗企业附加
     */
    private Double dMedicalEnterpriseAttach;
    /**
     * 大病医疗企业缴纳
     */
    private Double dMedicalEnterprisePay;
    /**
     * 大病医疗个人基数
     */
    private Double dMedicalPersonalBase;
    /**
     * 大病医疗个人比例
     */
    private Double dMedicalPersonalRatio;
    /**
     * 大病医疗个人附加
     */
    private Double dMedicalPersonalAttach;
    /**
     * 大病医疗个人缴纳
     */
    private Double dMedicalPersonalPay;
    /**
     * 大病医疗缴纳小计
     */
    private Double dMedicalSubtotal;
    /**
     * 大病医疗支付方式
     */
    private String dMedicalPaymentMethod;


    /**
     * 综合企业基数
     */
    private Double complexEnterpriseBase;
    /**
     * 综合企业比例
     */
    private Double complexEnterpriseRatio;
    /**
     * 综合企业附加
     */
    private Double complexEnterpriseAttach;
    /**
     * 综合企业缴纳
     */
    private Double complexEnterprisePay;
    /**
     * 综合个人基数
     */
    private Double complexPersonalBase;
    /**
     * 综合个人比例
     */
    private Double complexPersonalRatio;
    /**
     * 综合个人附加
     */
    private Double complexPersonalAttach;
    /**
     * 综合个人缴纳
     */
    private Double complexPersonalPay;
    /**
     * 综合缴纳小计
     */
    private Double complexSubtotal;
    /**
     * 综合支付方式
     */
    private String complexPaymentMethod;

    /**
     * 补充工伤企业基数
     */
    private Double bInjuryEnterpriseBase;
    /**
     * 补充工伤企业比例
     */
    private Double bInjuryEnterpriseRatio;
    /**
     * 补充工伤企业附加
     */
    private Double bInjuryEnterpriseAttach;
    /**
     * 补充工伤企业缴纳
     */
    private Double bInjuryEnterprisePay;
    /**
     * 补充工伤支付小计
     */
    private Double bInjurySubtotal;
    /**
     * 补充工伤支付方式
     */
    private String bInjuryPaymentMethod;

    /**
     * 社保企业缴纳合计
     */
    private Double socialSecurityEnterprise;
    /**
     * 社保个人缴纳合计
     */
    private Double socialSecurityPersonal;
    /**
     * 社保缴纳合计
     */
    private Double socialSecurityTotal;


    /**
     * 公积金企业基数
     */
    private Double providentEnterpriseBase;
    /**
     * 公积金企业比例
     */
    private Double providentEnterpriseRatio;
    /**
     * 公积金企业缴纳
     */
    private Double providentEnterprisePay;
    /**
     * 公积金个人基数
     */
    private Double providentPersonalBase;
    /**
     * 公积金个人比例
     */
    private Double providentPersonalRatio;
    /**
     * 公积金个人比例
     */
    private Double providentPersonalPay;
    /**
     * 公积金缴纳小计
     */
    private Double providentSubtotal;
    /**
     * 公积金支付方式
     */
    private String providentPaymentMethod;


    /**
     * 补充公积金企业基数
     */
    private Double bProvidentEnterpriseBase;
    /**
     * 补充公积金企业比例
     */
    private Double bProvidentEnterpriseRatio;
    /**
     * 补充公积金企业缴纳
     */
    private Double bProvidentEnterprisePay;
    /**
     * 补充公积金个人基数
     */
    private Double bProvidentPersonalBase;
    /**
     * 补充公积金个人比例
     */
    private Double bProvidentPersonalRatio;
    /**
     * 补充公积金个人缴纳
     */
    private Double bProvidentPersonalPay;
    /**
     * 补充公积金缴纳小计
     */
    private Double bProvidentSubtotal;
    /**
     * 补充公积金支付方式
     */
    private String bProvidentPaymentMethod;

    /**
     * 公积金企业缴纳合计
     */
    private Double providentEnterprise;
    /**
     * 公积金个人缴纳合计
     */
    private Double providentPersonal;
    /**
     * 公积金缴纳合计
     */
    private Double providentTotal;

    /**
     * 一次性收费合计
     */
    private Double yTollTotal;
    /**
     * 一次性收费备注
     */
    private String yTollRemark;

    /**
     * 一次性社保企业-代收代付
     */
    private Double ySocialEnterpriseD;
    /**
     * 一次性社保个人-代收代付
     */
    private Double ySocialPersonalD;
    /**
     * 一次性社保合计-代收代付
     */
    private Double ySocialTotalD;

    /**
     * 一次性公积金企业-代收代付
     */
    private Double yProvidentEnterpriseD;
    /**
     * 一次性公积金个人-代收代付
     */
    private Double yProvidentPersonalD;
    /**
     * 一次性公积金合计-代收代付
     */
    private Double yProvidentTotalD;

    /**
     * 一次性社保企业-托收
     */
    private Double ySocialEnterpriseT;
    /**
     * 一次性社保个人-托收
     */
    private Double ySocialPersonalT;
    /**
     * 一次性社保合计-托收
     */
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
     * 服务费
     */
    private Double serviceFee;
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
