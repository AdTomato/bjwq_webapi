package com.authine.cloudpivot.web.api.entity;

import javafx.beans.binding.DoubleExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/3/17 23:42
 * @Description: 薪资收费账单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollBill extends BaseEntity {

    /**
     * 账单年月
     */
    private String billYear;
    /**
     * 委托单位
     */
    private String entrustedUnit;
    /**
     * 业务客户名称
     */
    private String businessCustomerName;
    /**
     * 雇员姓名
     */
    private String employeeName;
    /**
     * 证件号码
     */
    private String idNo;
    /**
     * 证件类型
     */
    private String idType;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 银行卡账号
     */
    private String bankAccountNum;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 开户地
     */
    private String bankArea;
    /**
     * 工资发放月
     */
    private String salaryMonth;
    /**
     * 报税类型
     */
    private String taxReturnType;
    /**
     * 计税类型
     */
    private String taxCalculationType;
    /**
     * 业务员
     */
    private String salesman;
    /**
     * 业务部门
     */
    private String businessUnit;
    /**
     * 个税
     */
    private Double tax;
    /**
     * 实发工资
     */
    private Double paidWages;
    /**
     * 应收合计
     */
    private Double totalReceivable;
    /**
     * 服务费
     */
    private Double serviceFee;
    /**
     * 代收其他
     */
    private Double collectionOther;
    /**
     * 其他费用
     */
    private Double otherFee;
    /**
     * 应发工资
     */
    private Double payable;
    /**
     * 社保个人部分
     */
    private Double socialSecurityPersonal;
    /**
     * 公积金个人部分
     */
    private Double providentFundPersonal;

}
