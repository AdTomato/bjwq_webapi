package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CollectionRule
 * @Date 2020/4/2 13:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRule {
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
     * 公司最高基数
     */
    Double companyMaxBaseNum = Double.MAX_VALUE;
    /**
     * 公司最低基数
     */
    Double companyMinBaseNum = 0d;
    /**
     * 个人最高基数
     */
    Double employeeMaxBaseNum = Double.MAX_VALUE;
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
     * 公司比例
     */
    Double companyRatio = 0d;
    /**
     * 个人比例
     */
    Double employeeRatio = 0d;
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
     * 公司精度（保留小数位）
     */
    String companyPrecision = "0";
    /**
     * 个人精度（保留小数位）
     */
    String employeePrecision = "0";
    /**
     * 产品说明
     */
    String productDescription;
    /**
     * 采集周期
     */
    String acquisitionCycle;
    /**
     * 缴费频率
     */
    String paymentFrequency;
}
