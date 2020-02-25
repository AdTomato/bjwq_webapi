package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 10:17
 * @Description: 批量撤离
 */
@Data
public class BatchEvacuation extends BaseEntity{

    /**
     * 雇员唯一号
     */
    private String uniqueNum;

    /**
     * 雇员姓名
     */
    private String employeeName;

    /**
     * 证件类型
     */
    private String identityNoType;

    /**
     * 证件号码
     */
    private String identityNo;

    /**
     * 内部员工编号
     */
    private String employeeInternalNum;

    /**
     * 支付外企（派遣类）
     */
    private Double payForeignCompany;

    /**
     * 支付个人
     */
    private Double payPerson;

    /**
     * 离职费金额
     */
    private Double separationCosts;

    /**
     * 离职费月数
     */
    private Double separationCostsMonths;

    /**
     * 订单结束日期
     */
    private Date orderEndDate;

    /**
     * 社保（代收代付)止做日期
     */
    private Date socialSecurityEndTime;

    /**
     * 公积金（代收代付）止做日期
     */
    private Date providentFundEndTime;

    /**
     * 工资（服务费）止做日期
     */
    private Date wServiceFeeEndDate;

    /**
     * 社保停做原因
     */
    private String socialSecurityStopReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 备注
     */
    private String endEmploymentAgreement;

}