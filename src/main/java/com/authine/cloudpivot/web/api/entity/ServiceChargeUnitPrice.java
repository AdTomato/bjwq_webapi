package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 销售合同中的销售单价列表
 *
 * @author wangyong
 * @Date:2020/3/18 0:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceChargeUnitPrice {

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
     * 产品服务
     */
    private String productService;

    /**
     * 服务地区
     */
    private String serviceArea;

    /**
     * 地区详情
     */
    private String areaDetails;

    /**
     * 服务费单价
     */
    private Double serviceChargeUnitPrice = 0d;

    /**
     * 风险管理费
     */
    private Double riskManagementFee = 0d;

    /**
     * 增值税税费
     */
    private Double vatTaxes = 0d;

    /**
     * 福利产品总额
     */
    private Double totalWelfareProducts = 0d;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段：是否预收
     */
    private String precollected;

    /**
     * 扩展字段：收费频率
     */
    private String payCycle;

    String orderFormId;

    Integer serviceMaxPayBack = 0;

    Integer riskMaxPayBack = 0;

    Integer vatMaxPayBack = 0;

    Integer welfareMaxPayBack = 0;
}
