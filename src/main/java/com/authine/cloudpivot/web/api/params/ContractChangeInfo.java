package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ContractChangeInfo
 * @Author:lfh
 * @Date:2020/4/11 13:54
 * @Description: 合同变更参数
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractChangeInfo {

    private String parentId;
    //变更类型
    private String change_type;
    // --- 销售合同 ---
    //销售合同id
    private String sales_contract;

    //销售人员
    private List<Unit> x_sales_person;

    // 业务员
    private List<Unit> x_salesman;

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

    // -----采购合同 ------
    //采购合id
    private String purchase_contract;

    // 采购员
    private List<Unit> buyer;

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
