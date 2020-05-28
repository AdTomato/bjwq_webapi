package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 征缴规则中的汇缴子表
 *
 * @author wangyong
 * @time 2020/5/27 17:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrmCollectionRule {

    /**
     * id
     */
    private String id;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 排序字段
     */
    private Double sortKey;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 采集周期
     */
    private String acquisitionCycle;

    /**
     * 缴费频率
     */
    private String paymentFrequency;

    /**
     * 公司最高基数
     */
    private Double companyMaxBaseNum;

    /**
     * 公司最低基数
     */
    private Double companyMinBaseNum;

    /**
     * 个人最高基数
     */
    private Double employeeMaxBaseNum;

    /**
     * 个人最低基数
     */
    private Double employeeMinBaseNum;

    /**
     * 公司附加金额
     */
    private Double companySurchargeValue;

    /**
     * 个人附加金额
     */
    private Double employeeSurchargeValue;

    /**
     * 公司比例
     */
    private Double companyRatio;

    /**
     * 个人比例
     */
    private Double employeeRatio;

    /**
     * 有效起始月
     */
    private Date effectiveStartMonth;

    /**
     * 有效终止月
     */
    private Date effectiveTerminationMonth;

    /**
     * 公司舍入原则
     */
    private String companyRoundingPolicy;

    /**
     * 个人舍入原则
     */
    private String employeeRoundingPolicy;

    /**
     * 公司精度
     */
    private String companyPrecision;

    /**
     * 个人精度
     */
    private String employeePrecision;

    /**
     * 产品说明
     */
    private String productDescription;

}
