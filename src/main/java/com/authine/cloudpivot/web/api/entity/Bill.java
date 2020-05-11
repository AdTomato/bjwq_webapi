package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

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
     * 业务员
     */
    private String salesman;
    /**
     * 业务部门
     */
    private String businessUnit;
    /**
     * 员工性质
     */
    private String employeeNature;
    /**
     * 业务类型
     */
    private String businessType;


    /**
     * 养老企业基数
     */
    private Double pensionEnterpriseBase = 0D;
    /**
     * 养老企业比例
     */
    private Double pensionEnterpriseRatio = 0D;
    /**
     * 养老企业附加
     */
    private Double pensionEnterpriseAttach = 0D;
    /**
     * 养老企业缴纳
     */
    private Double pensionEnterprisePay = 0D;
    /**
     * 养老个人基数
     */
    private Double pensionPersonalBase = 0D;
    /**
     * 养老个人比例
     */
    private Double pensionPersonalRatio = 0D;
    /**
     * 养老个人附加
     */
    private Double pensionPersonalAttach = 0D;
    /**
     * 养老个人缴纳
     */
    private Double pensionPersonalPay = 0D;
    /**
     * 养老缴纳小计
     */
    @Value("0")
    private Double pensionSubtotal = 0D;
    /**
     * 养老支付方式
     */
    private String pensionPaymentMethod;


    /**
     * 医疗企业基数
     */
    private Double medicalEnterpriseBase = 0D;
    /**
     * 医疗企业比例
     */
    private Double medicalEnterpriseRatio = 0D;
    /**
     * 医疗企业附加
     */
    private Double medicalEnterpriseAttach = 0D;
    /**
     * 医疗企业缴纳
     */
    private Double medicalEnterprisePay = 0D;
    /**
     * 医疗个人基数
     */
    private Double medicalPersonalBase = 0D;
    /**
     * 医疗个人比例
     */
    private Double medicalPersonalRatio = 0D;
    /**
     * 医疗个人附加
     */
    private Double medicalPersonalAttach = 0D;
    /**
     * 医疗个人缴纳
     */
    private Double medicalPersonalPay = 0D;
    /**
     * 医疗缴纳小计
     */
    private Double medicalSubtotal = 0D;
    /**
     * 医疗支付方式
     */
    private String medicalPaymentMethod;


    /**
     * 失业企业基数
     */
    private Double unempEnterpriseBase = 0D;
    /**
     * 失业企业比例
     */
    private Double unempEnterpriseRatio = 0D;
    /**
     * 失业企业附加
     */
    private Double unempEnterpriseAttach = 0D;
    /**
     * 失业企业缴纳
     */
    private Double unempEnterprisePay = 0D;
    /**
     * 失业个人基数
     */
    private Double unempPersonalBase = 0D;
    /**
     * 失业个人比例
     */
    private Double unempPersonalRatio = 0D;
    /**
     * 失业个人附加
     */
    private Double unempPersonalAttach = 0D;
    /**
     * 失业个人缴纳
     */
    private Double unempPersonalPay = 0D;
    /**
     * 失业缴纳小计
     */
    private Double unempSubtotal = 0D;
    /**
     * 失业支付方式
     */
    private String unempPaymentMethod;


    /**
     * 工伤企业基数
     */
    private Double injuryEnterpriseBase = 0D;
    /**
     * 工伤企业比例
     */
    private Double injuryEnterpriseRatio = 0D;
    /**
     * 工伤企业附加
     */
    private Double injuryEnterpriseAttach = 0D;
    /**
     * 工伤企业缴纳
     */
    private Double injuryEnterprisePay = 0D;
    /**
     * 工伤缴纳小计
     */
    private Double injurySubtotal = 0D;
    /**
     * 工伤支付方式
     */
    private String injuryPaymentMethod;


    /**
     * 生育企业基数
     */
    private Double fertilityEnterpriseBase = 0D;
    /**
     * 生育企业比例
     */
    private Double fertilityEnterpriseRatio = 0D;
    /**
     * 生育企业附加
     */
    private Double fertilityEnterpriseAttach = 0D;
    /**
     * 生育企业缴纳
     */
    private Double fertilityEnterprisePay = 0D;
    /**
     * 生育缴纳小计
     */
    private Double fertilitySubtotal = 0D;
    /**
     * 生育支付方式
     */
    private String fertilityPaymentMethod;


    /**
     * 大病医疗企业基数
     */
    private Double dMedicalEnterpriseBase = 0D;
    /**
     * 大病医疗企业比例
     */
    private Double dMedicalEnterpriseRatio = 0D;
    /**
     * 大病医疗企业附加
     */
    private Double dMedicalEnterpriseAttach = 0D;
    /**
     * 大病医疗企业缴纳
     */
    private Double dMedicalEnterprisePay = 0D;
    /**
     * 大病医疗个人基数
     */
    private Double dMedicalPersonalBase = 0D;
    /**
     * 大病医疗个人比例
     */
    private Double dMedicalPersonalRatio = 0D;
    /**
     * 大病医疗个人附加
     */
    private Double dMedicalPersonalAttach = 0D;
    /**
     * 大病医疗个人缴纳
     */
    private Double dMedicalPersonalPay = 0D;
    /**
     * 大病医疗缴纳小计
     */
    private Double dMedicalSubtotal = 0D;
    /**
     * 大病医疗支付方式
     */
    private String dMedicalPaymentMethod;


    /**
     * 综合企业基数
     */
    private Double complexEnterpriseBase = 0D;
    /**
     * 综合企业比例
     */
    private Double complexEnterpriseRatio = 0D;
    /**
     * 综合企业附加
     */
    private Double complexEnterpriseAttach = 0D;
    /**
     * 综合企业缴纳
     */
    private Double complexEnterprisePay = 0D;
    /**
     * 综合个人基数
     */
    private Double complexPersonalBase = 0D;
    /**
     * 综合个人比例
     */
    private Double complexPersonalRatio = 0D;
    /**
     * 综合个人附加
     */
    private Double complexPersonalAttach = 0D;
    /**
     * 综合个人缴纳
     */
    private Double complexPersonalPay = 0D;
    /**
     * 综合缴纳小计
     */
    private Double complexSubtotal = 0D;
    /**
     * 综合支付方式
     */
    private String complexPaymentMethod;

    /**
     * 补充工伤企业基数
     */
    private Double bInjuryEnterpriseBase = 0D;
    /**
     * 补充工伤企业比例
     */
    private Double bInjuryEnterpriseRatio = 0D;
    /**
     * 补充工伤企业附加
     */
    private Double bInjuryEnterpriseAttach = 0D;
    /**
     * 补充工伤企业缴纳
     */
    private Double bInjuryEnterprisePay = 0D;
    /**
     * 补充工伤支付小计
     */
    private Double bInjurySubtotal = 0D;
    /**
     * 补充工伤支付方式
     */
    private String bInjuryPaymentMethod;

    /**
     * 社保企业缴纳合计
     */
    private Double socialSecurityEnterprise = 0D;
    /**
     * 社保个人缴纳合计
     */
    private Double socialSecurityPersonal = 0D;
    /**
     * 社保缴纳合计
     */
    private Double socialSecurityTotal = 0D;


    /**
     * 公积金企业基数
     */
    private Double providentEnterpriseBase = 0D;
    /**
     * 公积金企业比例
     */
    private Double providentEnterpriseRatio = 0D;
    /**
     * 公积金企业缴纳
     */
    private Double providentEnterprisePay = 0D;
    /**
     * 公积金个人基数
     */
    private Double providentPersonalBase = 0D;
    /**
     * 公积金个人比例
     */
    private Double providentPersonalRatio = 0D;
    /**
     * 公积金个人比例
     */
    private Double providentPersonalPay = 0D;
    /**
     * 公积金缴纳小计
     */
    private Double providentSubtotal = 0D;
    /**
     * 公积金支付方式
     */
    private String providentPaymentMethod;


    /**
     * 补充公积金企业基数
     */
    private Double bProvidentEnterpriseBase = 0D;
    /**
     * 补充公积金企业比例
     */
    private Double bProvidentEnterpriseRatio = 0D;
    /**
     * 补充公积金企业缴纳
     */
    private Double bProvidentEnterprisePay = 0D;
    /**
     * 补充公积金个人基数
     */
    private Double bProvidentPersonalBase = 0D;
    /**
     * 补充公积金个人比例
     */
    private Double bProvidentPersonalRatio = 0D;
    /**
     * 补充公积金个人缴纳
     */
    private Double bProvidentPersonalPay = 0D;
    /**
     * 补充公积金缴纳小计
     */
    private Double bProvidentSubtotal = 0D;
    /**
     * 补充公积金支付方式
     */
    private String bProvidentPaymentMethod;

    /**
     * 公积金企业缴纳合计
     */
    private Double providentEnterprise = 0D;
    /**
     * 公积金个人缴纳合计
     */
    private Double providentPersonal = 0D;
    /**
     * 公积金缴纳合计
     */
    private Double providentTotal = 0D;

    /**
     * 一次性收费合计
     */
    private Double yTollTotal = 0D;
    /**
     * 一次性收费备注
     */
    private String yTollRemark;

    /**
     * 一次性社保企业-代收代付
     */
    private Double ySocialEnterpriseD = 0D;
    /**
     * 一次性社保个人-代收代付
     */
    private Double ySocialPersonalD = 0D;
    /**
     * 一次性社保合计-代收代付
     */
    private Double ySocialTotalD = 0D;

    /**
     * 一次性公积金企业-代收代付
     */
    private Double yProvidentEnterpriseD = 0D;
    /**
     * 一次性公积金个人-代收代付
     */
    private Double yProvidentPersonalD = 0D;
    /**
     * 一次性公积金合计-代收代付
     */
    private Double yProvidentTotalD = 0D;

    /**
     * 一次性社保企业-托收
     */
    private Double ySocialEnterpriseT = 0D;
    /**
     * 一次性社保个人-托收
     */
    private Double ySocialPersonalT = 0D;
    /**
     * 一次性社保合计-托收
     */
    private Double ySocialTotalT = 0D;


    /**
     * 一次性公积金企业-托收
     */
    private Double yProvidentEnterpriseT = 0D;
    /**
     * 一次性公积金个人-托收
     */
    private Double yProvidentPersonalT = 0D;
    /**
     * 一次性公积金合计-托收
     */
    private Double yProvidentTotalT = 0D;

    /**
     * 企业缴费
     */
    private Double enterprisePayment = 0D;
    /**
     * 个人缴费
     */
    private Double individualPayment = 0D;
    /**
     * 实发工资合计
     */
    private Double paidWages = 0D;
    /**
     * 个税合计
     */
    private Double tax = 0D;

    /**
     * 社保公积金合计
     */
    private Double socialProvidentTotal = 0D;
    /**
     * 服务费
     */
    private Double serviceFee = 0D;
    /**
     * 增值税税费
     */
    private Double vatTax = 0D;
    /**
     * 福利产品总额
     */
    private Double totalWelfareProducts = 0D;
    /**
     * 风险管理费
     */
    private Double riskManageFee = 0D;
    /**
     * 外包管理费
     */
    private Double outsourcingManageFee = 0D;
    /**
     * 营业税税费
     */
    private Double businessTax = 0D;
    /**
     * 合计收费
     */
    private Double totalCharge = 0D;

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
    /**
     * 收费类型
     */
    private String chargeType;

}
