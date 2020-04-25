package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName SalesContractInfo
 * @Author:lfh
 * @Date:2020/4/11 15:15
 * @Description: 销售合同实体
 **/
@Data
public class SalesContractInfo extends BaseEntity {

    //销售合同id
    private String sales_contract_id;

    //销售人员
    private String x_sales_person;

    // 业务员
    private String x_salesman;

    // 合同编码
    private String x_contract_num;

    //客户名称
    private String x_client_name;

    // 单价
    private Double x_unit_price;

    //数量
    private Integer x_quantity;

    // 业务类型
    private String x_business_type;

    // 产品服务
    private String x_product_service;

    //金额
    private  Double x_total_amount;

    //合同签订日
    private Date x_contract_signing_date;

    //合同到期日
    private Date x_contract_expiry_date;

    //续签合同签订日
    private Date x_contract_renewal_date;

    //续签合同到期日
    private Date x_renewal_contract_end_date;

    //约定回款日
    private String x_agreed_repayment_date;

    //账单类型
    private String x_bill_type;

    //账单周期
    private String x_bill_cycle;

    //账单日
    private String x_bill_day;

    //费用及开票形式
    private String x_fees_invoic;

    //备注
    private String x_remark;

    //是否终止
    private String x_whether_end;
}
