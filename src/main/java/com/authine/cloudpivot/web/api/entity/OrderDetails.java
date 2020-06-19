package com.authine.cloudpivot.web.api.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;

import java.util.Date;

/**
 * 社保公积金明细
 *
 * @author wangyong
 * @time 2020/6/17 15:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {

    private String id;
    private String parentId;
    private Double sortKey;

    private String productName;

    private Date startChargeTime;

    private Date endChargeTime;

    private Double companyBase;

    private Double employeeBase;

    private Double companyRatio;

    private Double employeeRatio;

    private Double sum;

    private Double companyMoney;

    private Double employeeMoney;

    private Double companySurchargeValue;

    private Double employeeSurchargeValue;

}
