package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/18 0:19
 * @Description: 销售合同主表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesContractBasic extends BaseEntity{

    private String contractNum;
    private String contractStates;
    private String clientName;
    private String salesPerson;
    private String salesDepartment;
    private String salesman;
    private String businessUnit;
    private String businessType;
    private String productService;
    private String billType;
    private String billCycle;
    private String billDay;
    private String feesInvoic;
    private Double riskRatio;
    private String agreedRepaymentDate;
    private Double unitPrice;
    private Double quantity;
    private Double totalAmount;
    private Date contractSigningDate;
    private Date contractExpiryDate;
    private Date contractRenewalDate;
    private Date renewalContractExpiryDate;
    private Date contractTerminationDate;
    private String contractTerminationReason;
    private String remark;
    private Date contractReminderDay;
    private String whetherRemind;
}
