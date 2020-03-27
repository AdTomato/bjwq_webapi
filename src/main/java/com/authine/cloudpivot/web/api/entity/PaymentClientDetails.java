package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.PaymentClientDetails
 * @Date 2020/3/20 14:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentClientDetails {
    /**
     * 主键id
     */
    String id;
    /**
     * 支付申请主键id（PaymentApplication.id）
     */
    String parentId;
    /**
     * 排序号
     */
    Double sortKey;
    /**
     * 委托单位
     */
    String entrustUnit;
    /**
     * 客户名称
     */
    String clientName;
    /**
     * 投保地
     */
    String insuredArea;
    /**
     * 社保
     */
    Double socialSecurity;
    /**
     * 公积金
     */
    Double accumulationFund;
    /**
     * 工资
     */
    Double wages;
    /**
     * 福利产品
     */
    Double welfareProducts;
    /**
     * 服务费
     */
    Double serviceFee;
    /**
     * 增值税
     */
    Double addedTax;
    /**
     * 支付总额
     */
    Double paidAmount;
    /**
     * 客户代码
     */
    String clientCode;
    /**
     * 部门代码
     */
    String departmentCode;
    /**
     * 毛利分析代码
     */
    String grossProfitAnalysisCode;

    String sourceId;

    String dataType;
}
