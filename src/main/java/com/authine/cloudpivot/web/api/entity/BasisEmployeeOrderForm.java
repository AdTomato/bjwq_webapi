package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/17 9:12
 * @Description: 员工订单基础类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasisEmployeeOrderForm extends BaseEntity{

    private String employeeFilesId;
    private String detail;
    private Double total;
    private String workRelatedInjury;
    private String unemployment;
    private String medical;
    private String childbirth;
    private String criticalIllness;
    private String housingAccumulationFunds;
    private Double serviceFee;
    private Date startTime;
    private Date endTime;
    private Date inputTime;
    private String socialSecurityStatus;
    private String isHistory;
    private String providentFundStatus;
    private String socialSecurityCity;
    private String providentFundCity;

}
