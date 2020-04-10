package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.PolicyCollectPay
 * @Date 2020/4/2 15:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyCollectPay extends BaseEntity{
    String policyEnjoySuperclass;
    String city;
    String socialSecurityGroup;
    String productName;
    String collectCycle;
    String payCycle;
    String closeDate;
    Double companyRatio;
    Double employeeRatio;
    Double companySurchargeValue;
    Double employeeSurchargeValue;
    String companyRoundingPolicy;
    String employeeRoundingPolicy;
    String companyPrecision;
    String employeePrecision;
    String productDescription;
    Integer payMonth;
    List <ProductBaseNum> productBaseNums;
}
