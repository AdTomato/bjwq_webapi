package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/18 0:07
 * @Description: 员工订单里面的社保公积金
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialSecurityFundDetail {

    private String id;
    private String parentId;
    private Double sortKey;
    private String productName;
    private String socialSecurityGroup;
    private Double baseNum;
    private Double sum;
    private Double companyMoney;
    private Double employeeMoney;
    private Double companyRatio;
    private Double employeeRatio;
    private Double companySurchargeValue;
    private Double employeeSurchargeValue;
    private String precollected;
    private String payCycle;
    private Date startChargeTime;
    private Date endChargeTime;
    private String nameHide;
}
