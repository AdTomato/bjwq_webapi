package com.authine.cloudpivot.web.api.entity;

import javafx.beans.binding.DoubleExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/3/17 23:42
 * @Description: 薪资收费账单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollBill extends BaseEntity{

    private String billYear;
    private String entrustedUnit;
    private String businessCustomerName;
    private String employeeName;
    private String idNo;
    private String idType;
    private String phone;
    private String bankAccountNum;
    private String bank;
    private String bankArea;
    private String salaryMonth;
    private String taxReturnType;
    private String taxCalculationType;
    private String salesman;
    private String businessUnit;
    private Double tax;
    private Double paidWages;
    private Double totalReceivable;
    private Double serviceFee;
    private Double collectionOther;

}
