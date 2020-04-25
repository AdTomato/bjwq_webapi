package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName PurchaseContractInfo
 * @Author:lfh
 * @Date:2020/4/11 15:19
 * @Description: 采购合同实体类
 **/
@Data
public class PurchaseContractInfo extends BaseEntity {

    //采购合id
    private String purchase_contract_id;

    // 采购员
    private String buyer;

    //合同编码
    private String c_contract_num;

    //供应商名称
    private String c_supplier_name;

    //业务类型
    private String c_business_type;

    //产品服务
    private String c_product_service;

    //单价
    private Double c_unit_price;

    //数量
    private Integer c_quantity;

    //金额
    private Double c_total_amount;

    //合同签订日
    private Date c_contract_signing_date;

    //合同到期日
    private Date c_contract_expiry_date;

    //续签合同签订日
    private Date c_contract_renewal_date;

    //续签合同到期日
    private Date c_renewal_contract_end_date;

    //备注
    private String c_remark;

    //是否终止
    private String c_whether_end;


}
