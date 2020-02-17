package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import javax.swing.*;
import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-14 15:57
 * @Description: 员工档案实体类
 */
@Data
public class EmployeeFiles extends BaseEntity{

    /**
     * 委托单位
     */
    private String entrustedUnit;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNo;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生年月
     */
    private Date birthDate;

    /**
     * 员工性质
     */
    private String employeeNature;

    /**
     * 户籍性质
     */
    private String householdRegisterNature;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 合同开始日期
     */
    private Date labourContractStartTime;

    /**
     * 合同结束日期
     */
    private Date labourContractEndTime;

    /**
     * 合同工资
     */
    private Double salary;

    /**
     * 试用期起始时间
     */
    private Date probationStartTime;

    /**
     * 试用期结束时间
     */
    private Date probationEndTime;

    /**
     * 试用期工资
     */
    private Double probationSalary;

    /**
     * 社保福利地
     */
    private String socialSecurityCity;

    /**
     * 公积金福利地
     */
    private String providentFundCity;

    /**
     * 报入职时间
     */
    private Date reportEntryTime;

    /**
     * 报入职人
     */
    private String reportRecruits;

    /**
     * 入职日期
     */
    private Date entryTime;

    /**
     * 社保收费开始
     */
    private Date socialSecurityChargeStart;

    /**
     * 公积金收费开始
     */
    private Date providentFundChargeStart;

    /**
     * 社保福利办理方
     */
    private String socialSecurityArea;

    /**
     * 公积金福利办理方
     */
    private String providentFundArea;

    /**
     * 入职备注
     */
    private String entryDescription;

    /**
     * 报离职时间
     */
    private Date reportQuitDate;

    /**
     * 报离职人
     */
    private String reportSeveranceOfficer;

    /**
     * 离职日期
     */
    private Date quitDate;

    /**
     * 社保收费截止
     */
    private Date socialSecurityChargeEnd;

    /**
     * 公积金收费截止
     */
    private Date providentFundChargeEnd;

    /**
     * 离职原因
     */
    private String quitReason;

    /**
     * 离职备注
     */
    private String quitRemark;

    /**
     * 是否入职通知
     */
    private String entryNotice;

    /**
     * 是否体检
     */
    private String healthCheck;

    /**
     * 是否发薪
     */
    private String whetherPay;

    /**
     * 银行卡账号
     */
    private String bankAccountNumber;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 开户地
     */
    private String bankArea;

    /**
     * 职位
     */
    private String position;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 员工标签
     */
    private String employeeLabels;


}
