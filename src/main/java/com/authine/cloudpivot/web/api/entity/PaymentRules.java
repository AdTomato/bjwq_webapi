package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.PaymentRules
 * @Date 2020/5/12 13:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRules {
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
     * 补缴月份下限（包含）
     */
    Integer paymentBackMinMonth = 0;
    /**
     * 补缴月份上限（不包含）
     */
    Integer paymentBackMaxMonth = 100;
    /**
     * 是否可以补缴
     */
    String canPayBack;
    /**
     * 是否跨年度补缴
     */
    String canCrossYearPayBack;
    /**
     * 公司最高基数
     */
    Double companyMaxBaseNum = 0d;
    /**
     * 公司最低基数
     */
    Double companyMinBaseNum = 0d;
    /**
     * 个人最高基数
     */
    Double employeeMaxBaseNum = 0d;
    /**
     * 个人最低基数
     */
    Double employeeMinBaseNum = 0d;
    /**
     * 公司附加金额
     */
    Double companySurchargeValue = 0d;
    /**
     * 个人附加金额
     */
    Double employeeSurchargeValue = 0d;
    /**
     * 有效起始月
     */
    Date effectiveStartMonth;
    /**
     * 有效终止月
     */
    Date effectiveTerminationMonth;
    /**
     * 公司舍入原则
     */
    String companyRoundingPolicy = "四舍五入";
    /**
     * 个人舍入原则
     */
    String employeeRoundingPolicy = "四舍五入";
    /**
     * 公司精度
     */
    String companyPrecision = "0";
    /**
     * 个人精度
     */
    String employeePrecision = "0";
    /**
     * 产品说明
     */
    String product_description;
    /**
     * 补缴公司比例
     */
    Double companyRatio = 0d;
    /**
     * 补缴个人比例
     */
    Double employeeRatio = 0d;
    /**
     * 申报当月是否为补缴月
     */
    String curMonthCanPayBack;
}
