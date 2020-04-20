package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 支付明细实体
 * @ClassName com.authine.cloudpivot.web.api.entity.PaymentDetails
 * @Date 2020/4/10 15:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails extends BaseEntity {
    /**
     * 账单年月
     */
    String billYear;
    /**
     * 业务员
     */
    String salesman;
    /**
     * 雇员唯一号
     */
    String uniqueNum;
    /**
     * 雇员姓名
     */
    String employeeName;
    /**
     * 身份证
     */
    String idNo;
    /**
     * 投保地
     */
    String insuredArea;
    /**
     * 业务客户名称
     */
    String clientName;
    /**
     * 委托单位
     */
    String entrustUnit;
    /**
     * 业务年月
     */
    String businessYear;
    /**
     * 企业缴纳合计
     */
    Double enterpriseTotal = 0d;
    /**
     * 个人缴纳合计
     */
    Double personalTotal = 0d;
    /**
     * 社保公积金缴纳合计
     */
    Double socialProvidentTotal = 0d;
    /**
     * 社保实付缴纳额
     */
    Double socialSecurityPayment = 0d;
    /**
     * 住房公积金实付缴纳额
     */
    Double providentPayment = 0d;
    /**
     * 社保公积金实付缴纳额
     */
    Double socialProvidentPayment = 0d;
    /**
     * 服务费缴纳合计
     */
    Double serviceFeeTotal = 0d;
    /**
     * 福利产品总金额
     */
    Double totalWelfareProducts;
    /**
     * 临时费用总金额
     */
    Double temporaryCostTotal = 0d;
    /**
     * 总金额
     */
    Double totalSum = 0d;
    /**
     * 实付工资合计
     */
    Double totalPaidWages;
    /**
     * 实付总金额
     */
    Double totalAmountPaid = 0d;
    /**
     * 工伤赔付返款
     */
    Double injuryCompensationRefunds = 0d;
    /**
     * 生育津贴
     */
    Double maternityAllowance = 0d;
    /**
     * 公积金住房退费返款
     */
    Double providentRefund = 0d;
    /**
     * 社保清算返款
     */
    Double socialSettlementRefund = 0d;
    /**
     * 社保退费返款
     */
    Double socialRefund = 0d;
    /**
     * 重复缴费退费
     */
    Double repeatedPaymentRefund = 0d;
    /**
     * 人身意外返款
     */
    Double personalAccidentRefund = 0d;
    /**
     * 员工重疾安康保障
     */
    Double seriousIllnessProtection = 0d;
    /**
     * 临时收费产品方案说明
     */
    String temporaryCostDescription;
    /**
     * 临时收费备注
     */
    String temporaryCostRemarks;
    /**
     * 临时费用社保企业缴纳
     */
    Double lSocialSecurityEnterprise = 0d;
    /**
     * 临时费用社保个人缴纳
     */
    Double lSocialSecurityPersonal = 0d;
    /**
     * 临时费用社保支付方式
     */
    String lSocialSecurityPayMethod;
    /**
     * 临时费用公积金企业缴纳
     */
    Double lProvidentEnterprise = 0d;
    /**
     * 临时费用公积金个人缴纳
     */
    Double lProvidentPersonal = 0d;
    /**
     * 临时费用公积金支付方式
     */
    String lProvidentPayMethod;
    /**
     * 离职临时收费
     */
    Double quitTemporaryCharge = 0d;
    /**
     * 工资临时收费
     */
    Double wagesTemporaryCharge = 0d;
    /**
     * 实付其他临收
     */
    Double paidOtherTemporaryCharge = 0d;
    /**
     * 社保类临时费用合计
     */
    Double sTemporaryCharge = 0d;
    /**
     * 公积金类临时费用合计
     */
    Double gTemporaryCharge = 0d;
    /**
     * 服务费类临时费用合计
     */
    Double serviceFeeTemporaryCharge = 0d;
    /**
     * 福利类临时费用合计
     */
    Double welfareTemporaryCharge = 0d;
    /**
     * 托收类临时费用合计
     */
    Double tsTemporaryCharge = 0d;
    /**
     * 实付类临时费用合计
     */
    Double paidTemporaryCharge = 0d;
    /**
     * 养老保险企业基数
     */
    Double pensionEnterpriseBase = 0d;
    /**
     * 养老保险企业比例
     */
    Double pensionEnterpriseRatio = 0d;
    /**
     * 养老保险企业附加
     */
    Double pensionEnterpriseAttach = 0d;
    /**
     * 养老保险企业缴纳金额
     */
    Double pensionEnterprisePay = 0d;
    /**
     * 养老保险个人基数
     */
    Double pensionPersonalBase = 0d;
    /**
     * 养老保险个人比例
     */
    Double pensionPersonalRatio = 0d;
    /**
     * 养老保险个人附加
     */
    Double pensionPersonalAttach = 0d;
    /**
     * 养老保险个人缴纳金额
     */
    Double pensionPersonalPay = 0d;
    /**
     * 养老保险缴纳额
     */
    Double pensionSubtotal = 0d;
    /**
     * 养老保险支付方式
     */
    String pensionPaymentMethod;
    /**
     * 失业保险企业基数
     */
    Double unempEnterpriseBase = 0d;
    /**
     * 失业保险企业比例
     */
    Double unempEnterpriseRatio = 0d;
    /**
     * 失业保险企业附加
     */
    Double unempEnterpriseAttach = 0d;
    /**
     * 失业保险企业缴纳金额
     */
    Double unempEnterprisePay = 0d;
    /**
     * 失业保险个人基数
     */
    Double unempPersonalBase = 0d;
    /**
     * 失业保险个人比例
     */
    Double unempPersonalRatio = 0d;
    /**
     * 失业保险个人附加
     */
    Double unempPersonalAttach = 0d;
    /**
     * 失业保险个人缴纳金额
     */
    Double unempPersonalPay = 0d;
    /**
     * 失业保险缴纳额
     */
    Double unempSubtotal = 0d;
    /**
     * 失业保险支付方式
     */
    String unempPaymentMethod;
    /**
     * 工伤保险企业基数
     */
    Double injuryEnterpriseBase = 0d;
    /**
     * 工伤保险企业比例
     */
    Double injuryEnterpriseRatio = 0d;
    /**
     * 工伤保险企业附加
     */
    Double injuryEnterpriseAttach = 0d;
    /**
     * 工伤保险企业缴纳金额
     */
    Double injuryEnterprisePay = 0d;
    /**
     * 工伤保险个人基数
     */
    Double injuryPersonalBase = 0d;
    /**
     * 工伤保险个人比例
     */
    Double injuryPersonalRatio = 0d;
    /**
     * 工伤保险个人附加
     */
    Double injuryPersonalAttach = 0d;
    /**
     * 工伤保险个人缴纳金额
     */
    Double injuryPersonalPay = 0d;
    /**
     * 工伤保险缴纳额
     */
    Double injurySubtotal = 0d;
    /**
     * 工伤保险支付方式
     */
    String injuryPaymentMethod;
    /**
     * 生育保险企业基数
     */
    Double fertilityEnterpriseBase = 0d;
    /**
     * 生育保险企业比例
     */
    Double fertilityEnterpriseRatio = 0d;
    /**
     * 生育保险企业附加
     */
    Double fertilityEnterpriseAttach = 0d;
    /**
     * 生育保险企业缴纳金额
     */
    Double fertilityEnterprisePay = 0d;
    /**
     * 生育保险个人基数
     */
    Double fertilityPersonalBase = 0d;
    /**
     * 生育保险个人比例
     */
    Double fertilityPersonalRatio = 0d;
    /**
     * 生育保险个人附加
     */
    Double fertilityPersonalAttach = 0d;
    /**
     * 生育保险个人缴纳金额
     */
    Double fertilityPersonalPay = 0d;
    /**
     * 生育保险缴纳额
     */
    Double fertilitySubtotal = 0d;
    /**
     * 生育保险支付方式
     */
    String fertilityPaymentMethod;
    /**
     * 基本医疗保险企业基数
     */
    Double medicalEnterpriseBase = 0d;
    /**
     * 基本医疗保险企业比例
     */
    Double medicalEnterpriseRatio = 0d;
    /**
     * 基本医疗保险企业附加
     */
    Double medicalEnterpriseAttach = 0d;
    /**
     * 基本医疗保险企业缴纳金额
     */
    Double medicalEnterprisePay = 0d;
    /**
     * 基本医疗保险个人基数
     */
    Double medicalPersonalBase = 0d;
    /**
     * 基本医疗保险个人比例
     */
    Double medicalPersonalRatio = 0d;
    /**
     * 基本医疗保险个人附加
     */
    Double medicalPersonalAttach = 0d;
    /**
     * 基本医疗保险个人缴纳金额
     */
    Double medicalPersonalPay = 0d;
    /**
     * 基本医疗保险缴纳额
     */
    Double medicalSubtotal = 0d;
    /**
     * 基本医疗保险支付方式
     */
    String medicalPaymentMethod;
    /**
     * 大病附加保险企业基数
     */
    Double dMedicalEnterpriseBase = 0d;
    /**
     * 大病附加保险企业比例
     */
    Double dMedicalEnterpriseRatio = 0d;
    /**
     * 大病附加保险企业附加
     */
    Double dMedicalEnterpriseAttach = 0d;
    /**
     * 大病附加保险企业缴纳金额
     */
    Double dMedicalEnterprisePay = 0d;
    /**
     * 大病附加保险个人基数
     */
    Double dMedicalPersonalBase = 0d;
    /**
     * 大病附加保险个人比例
     */
    Double dMedicalPersonalRatio = 0d;
    /**
     * 大病附加保险个人附加
     */
    Double dMedicalPersonalAttach = 0d;
    /**
     * 大病附加保险个人缴纳金额
     */
    Double dMedicalPersonalPay = 0d;
    /**
     * 大病附加保险缴纳额
     */
    Double dMedicalSubtotal = 0d;
    /**
     * 大病附加保险支付方式
     */
    String dMedicalPaymentMethod;
    /**
     * 综合保险企业基数
     */
    Double complexEnterpriseBase = 0d;
    /**
     * 综合保险企业比例
     */
    Double complexEnterpriseRatio = 0d;
    /**
     * 综合保险企业附加
     */
    Double complexEnterpriseAttach = 0d;
    /**
     * 综合保险企业缴纳金额
     */
    Double complexEnterprisePay = 0d;
    /**
     * 综合保险个人基数
     */
    Double complexPersonalBase = 0d;
    /**
     * 综合保险个人比例
     */
    Double complexPersonalRatio = 0d;
    /**
     * 综合保险个人附加
     */
    Double complexPersonalAttach = 0d;
    /**
     * 综合保险个人缴纳金额
     */
    Double complexPersonalPay = 0d;
    /**
     * 综合保险缴纳额
     */
    Double complexSubtotal = 0d;
    /**
     * 综合保险支付方式
     */
    String complexPaymentMethod;
    /**
     * 补充工伤企业基数
     */
    Double bInjuryEnterpriseBase = 0d;
    /**
     * 补充工伤企业比例
     */
    Double bInjuryEnterpriseRatio = 0d;
    /**
     * 补充工伤企业附加
     */
    Double bInjuryEnterpriseAttach = 0d;
    /**
     * 补充工伤企业缴纳金额
     */
    Double bInjuryEnterprisePay = 0d;
    /**
     * 补充工伤个人基数
     */
    Double bInjuryPersonalBase = 0d;
    /**
     * 补充工伤个人比例
     */
    Double bInjuryPersonalRatio = 0d;
    /**
     * 补充工伤个人附加
     */
    Double bInjuryPersonalAttach = 0d;
    /**
     * 补充工伤个人缴纳金额
     */
    Double bInjuryPersonalPay = 0d;
    /**
     * 补充工伤缴纳额
     */
    Double bInjurySubtotal = 0d;
    /**
     * 补充工伤支付方式
     */
    String bInjuryPaymentMethod;
    /**
     * 住房公积金企业基数
     */
    Double providentEnterpriseBase = 0d;
    /**
     * 住房公积金企业比例
     */
    Double providentEnterpriseRatio = 0d;
    /**
     * 住房公积金企业附加
     */
    Double providentEnterpriseAttach = 0d;
    /**
     * 住房公积金企业缴纳金额
     */
    Double providentEnterprisePay = 0d;
    /**
     * 住房公积金个人基数
     */
    Double providentPersonalBase = 0d;
    /**
     * 住房公积金个人比例
     */
    Double providentPersonalRatio = 0d;
    /**
     * 住房公积金个人附加
     */
    Double providentPersonalAttach = 0d;
    /**
     * 住房公积金个人缴纳金额
     */
    Double providentPersonalPay = 0d;
    /**
     * 住房公积金缴纳额
     */
    Double providentSubtotal = 0d;
    /**
     * 住房公积金支付方式
     */
    String providentPaymentMethod;
    /**
     * 补充公积金企业基数
     */
    Double bProvidentEnterpriseBase = 0d;
    /**
     * 补充公积金企业比例
     */
    Double bProvidentEnterpriseRatio = 0d;
    /**
     * 补充公积金企业附加
     */
    Double bProvidentEnterpriseAdd = 0d;
    /**
     * 补充公积金企业缴纳金额
     */
    Double bProvidentEnterprisePay = 0d;
    /**
     * 补充公积金个人基数
     */
    Double bProvidentPersonalBase = 0d;
    /**
     * 补充公积金个人比例
     */
    Double bProvidentPersonalRatio = 0d;
    /**
     * 补充公积金个人附加
     */
    Double bProvidentPersonalAttach = 0d;
    /**
     * 补充公积金个人缴纳金额
     */
    Double bProvidentPersonalPay = 0d;
    /**
     * 补充公积金缴纳额
     */
    Double bProvidentSubtotal = 0d;
    /**
     * 补充公积金支付方式
     */
    String bProvidentPaymentMethod;
    /**
     * 关联支付申请
     */
    String paymentApplicationId;
    /**
     * 是否锁定
     */
    Integer isLock;
    /**
     * 是否对比
     */
    Integer whetherCompare;
    /**
     * sourceId隐藏字段
     */
    String sourceId;
    /**
     * 数据类型，隐藏字段
     */
    String dataType;
    /**
     * 是否差异数据
     */
    Integer whetherDifferenceData;
}
