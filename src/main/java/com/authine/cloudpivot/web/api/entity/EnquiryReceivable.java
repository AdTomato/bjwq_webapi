package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/17 13:35
 * @Description: 账单查询
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryReceivable extends BaseEntity {

    private String billYear;
    private String entrustedUnit;
    private String salesman;
    private String businessDept;
    private String businessType;
    private Double socialSecurityTotal;
    private Double accumulationFundTotal;
    private Double realWagesTotal;
    private Double personalTaxTotal;
    private Double welfareProductsTotal;
    private Double valueAddedTaxTotal;
    private Double riskManagementFee;
    private Double serviceCharge;
    private Double receivableTotal;
    private String businessCustomerName;
    private Double outsourcingManageFee;
    private Double businessTaxesFees;
    private Date agreedPaymentDate;
    private Date systemGenerationDate;
    private String billModify;
    private Date billModifyDate;
    private String billWetherDefine;
    private String billDefine;
    private Integer isLock;
}
