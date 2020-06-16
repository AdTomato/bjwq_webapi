package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 10:17
 * @Description: 批量撤离
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
     * 终止用工协议
     */
    private String endEmploymentAgreement;

    private String employeeNature;

    private String deleteEmployeeId;

    public BatchEvacuation(String deleteEmployeeId, EmployeeFiles employeeFiles, boolean sbHave, boolean sbNotInAnhui,
                           boolean gjjHave, boolean gjjNotInAnhui) {
        this.deleteEmployeeId = deleteEmployeeId;
        this.employeeName = employeeFiles.getEmployeeName();
        this.identityNoType = employeeFiles.getIdType();
        this.identityNo = employeeFiles.getIdNo();
        this.orderEndDate = employeeFiles.getReportQuitDate();
        if (gjjHave && gjjNotInAnhui) {
            this.providentFundEndTime = employeeFiles.getProvidentFundChargeEnd();
        }
        if (sbHave && sbNotInAnhui) {
            this.socialSecurityEndTime = employeeFiles.getSocialSecurityChargeEnd();
        }
        this.socialSecurityStopReason = employeeFiles.getQuitReason();
        this.remark = employeeFiles.getQuitRemark();
        this.employeeNature = employeeFiles.getEmployeeNature();
    }
}
