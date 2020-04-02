package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/3/18 0:26
 * @Description: 销售合同中的销售单价列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceChargeUnitPrice {

    private String id;
    private String parentId;
    private Double sortKey;
    private String productService;
    private String serviceArea;
    private String areaDetails;
    private Double serviceChargeUnitPrice;
    private Double riskManagementFee;
    private Double totalWelfareProducts;
    private Double vatTaxes;
    private Double outsourcingManagementFee;
    private Double businessTax;
    private String remark;

}
