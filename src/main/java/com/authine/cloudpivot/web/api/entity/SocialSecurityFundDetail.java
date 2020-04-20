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
    private Double baseNum = 0d;
    private Double sum = 0d;
    private Double companyMoney = 0d;
    private Double employeeMoney = 0d;
    private Double companyRatio = 0d;
    private Double employeeRatio = 0d;
    private Double companySurchargeValue = 0d;
    private Double employeeSurchargeValue = 0d;
    private String precollected;
    private String payCycle;
    private Date startChargeTime;
    private Date endChargeTime;
    private String nameHide;
}
