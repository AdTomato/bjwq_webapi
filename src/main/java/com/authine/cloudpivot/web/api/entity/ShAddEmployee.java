package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 增员_上海实体
 * @ClassName com.authine.cloudpivot.web.api.entity.ShAddEmployee
 * @Date 2020/2/25 15:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShAddEmployee extends BaseEntity{
    String shAddEmployeeId;

    /** 员工姓名*/
    String employeeName;

    /** 唯一号*/
    String uniqueNum;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 委托/派遣*/
    String commissionSend;

    /** 委派单代码*/
    String orderCode;

    /** 报价单代码*/
    String quotationCode;

    /** 系统客户编号*/
    String sysClientNum;

    /** 客户名称*/
    String clientName;

    /** 客户简称*/
    String clientShortName;

    /** 项目书名称*/
    String projectProposalsName;

    /** 社保支付方向*/
    String socialSecurityPayDirect;

    /** 公积金支付方向*/
    String providentFundPayDirect;

    /** 服务费*/
    Double serviceFee;

    /** 含档*/
    String withFile;

    /** 套餐涉及手续*/
    String packageInvolvesProcedures;

    /** 入职时间*/
    Date entryTime;

    /** 福利起始时间*/
    Date benefitStartTime;

    /** 手机*/
    String mobile;

    /** 公积金开始时间*/
    Date providentFundStartTime;

    /** 福利起始时间与公积金开始时间是否一致:一致；不一致。*/
    String whetherConsistent;

    /** 社保组*/
    String socialSecuritySection;

    /** 社会保险基数*/
    Double socialSecurityBase;

    /** 住房公积金基数*/
    Double providentFundBase;

    /** 补充住房公积金比例*/
//    Double supplementProvidentFundP;

    /** 补充个人住房公积金比例*/
    Double pSupplementProvidentFund;

    /** 补充个人住房公积金比例*/
    Double uSupplementProvidentFund;

    /** 补充住房公积金基数*/
    Double supplementProvidentFundB;

    /** 收费起始日*/
    Date chargeStartDate;

    /** 派遣期限起始日期*/
    Date dispatchPeriodStartDate;

    /** 派遣期限截至日期*/
    Date dispatchDeadline;

    /** 开始日期(试)*/
    Date startDateTrial;

    /** 结束日期(试)*/
    Date endTimeTrial;

    /** 工资(试)*/
    Double wageTrial;

    /** 开始日期(正)*/
    Date startDatePositive;

    /** 结束日期(正)*/
    Date endDatePositive;

    /** 工资(正)*/
    Double wagePositive;

    /** 电话*/
    String phone;

    /** 员工客户方编号*/
    String employeeCustomerSideNum;

    /** 联系地址*/
    String contactAddress;

    /** 邮政编码*/
    String postalCode;

    /** 户口所在地*/
    String hukouLocation;

    /** 电子邮件*/
    String mail;

    /** 开户银行名称*/
    String bankName;

    /** 帐号*/
    String bankAccount;

    /** 帐号省名*/
    String accountProvinceName;

    /** 帐号市区名*/
    String accountCityName;

    /** 帐户名*/
    String accountName;

    /** 银行类别*/
    String bankCategory;

    /** 城市名称*/
    String cityName;

    /** 报税属性*/
    String taxProperties;

    /** 工号*/
    String jobNum;

    /** HRO*/
    String hro;

    /** 业务部门*/
    String businessUnit;

    /** 员工状态*/
    String employeeStatus;

    /** 同步CSS*/
    String synchronousCss;

    /** 入职备注*/
    String InductionRemark;

    /** 报税税局*/
    String taxBureau;

    /** 员工属性*/
    String employeeAttributes;

    /** 社保标准*/
    String socialSecurityStandards;

    /** 是否线上*/
    String weatherOnline;

    /** 工作制*/
    String workSystem;

    /** 是否入职E化*/
    String weatherInductionE;

    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 所属部门
     */
    String subordinateDepartment;
    /**
     * 退役士兵
     */
    String isRetiredSoldier;
    /**
     * 贫困建档人员
     */
    String isPoorArchivists;
    /**
     * 残疾人
     */
    String isDisabled;
    /**
     * 户籍备注
     */
    String householdRegisterRemarks;
    /**
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 性别
     */
    String gender;
    /**
     * 出生年月
     */
    Date birthday;
    /**
     * 工作地
     */
    String workplace;
}
