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


    public ShAddEmployee(String employeeName, String uniqueNum, String identityNoType, String identityNo,
                         String commissionSend, String orderCode, String quotationCode, String sysClientNum,
                         String clientName, String clientShortName, String projectProposalsName,
                         String socialSecurityPayDirect, String providentFundPayDirect, Double serviceFee,
                         String withFile, String packageInvolvesProcedures, Date entryTime, Date benefitStartTime,
                         String mobile, Date providentFundStartTime, String whetherConsistent,
                         String socialSecuritySection, Double socialSecurityBase, Double providentFundBase,
                         Double pSupplementProvidentFund, Double uSupplementProvidentFund,Double supplementProvidentFundB, Date chargeStartDate,
                         Date dispatchPeriodStartDate, Date dispatchDeadline, Date startDateTrial, Date endTimeTrial,
                         Double wageTrial, Date startDatePositive, Date endDatePositive, Double wagePositive,
                         String phone, String employeeCustomerSideNum, String contactAddress, String postalCode,
                         String hukouLocation, String mail, String bankName, String bankAccount,
                         String accountProvinceName, String accountCityName, String accountName, String bankCategory,
                         String cityName, String taxProperties, String jobNum, String hro, String businessUnit,
                         String employeeStatus, String synchronousCss, String inductionRemark, String taxBureau,
                         String employeeAttributes, String socialSecurityStandards, String weatherOnline,
                         String workSystem, String weatherInductionE, String id, String creater, Date createdTime) {
        this.employeeName = employeeName;
        this.uniqueNum = uniqueNum;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.commissionSend = commissionSend;
        this.orderCode = orderCode;
        this.quotationCode = quotationCode;
        this.sysClientNum = sysClientNum;
        this.clientName = clientName;
        this.clientShortName = clientShortName;
        this.projectProposalsName = projectProposalsName;
        this.socialSecurityPayDirect = socialSecurityPayDirect;
        this.providentFundPayDirect = providentFundPayDirect;
        this.serviceFee = serviceFee;
        this.withFile = withFile;
        this.packageInvolvesProcedures = packageInvolvesProcedures;
        this.entryTime = entryTime;
        this.benefitStartTime = benefitStartTime;
        this.mobile = mobile;
        this.providentFundStartTime = providentFundStartTime;
        this.whetherConsistent = whetherConsistent;
        this.socialSecuritySection = socialSecuritySection;
        this.socialSecurityBase = socialSecurityBase;
        this.providentFundBase = providentFundBase;
        this.uSupplementProvidentFund = uSupplementProvidentFund;
        this.pSupplementProvidentFund = pSupplementProvidentFund;
//        this.supplementProvidentFundP = supplementProvidentFundP;
        this.supplementProvidentFundB = supplementProvidentFundB;
        this.chargeStartDate = chargeStartDate;
        this.dispatchPeriodStartDate = dispatchPeriodStartDate;
        this.dispatchDeadline = dispatchDeadline;
        this.startDateTrial = startDateTrial;
        this.endTimeTrial = endTimeTrial;
        this.wageTrial = wageTrial;
        this.startDatePositive = startDatePositive;
        this.endDatePositive = endDatePositive;
        this.wagePositive = wagePositive;
        this.phone = phone;
        this.employeeCustomerSideNum = employeeCustomerSideNum;
        this.contactAddress = contactAddress;
        this.postalCode = postalCode;
        this.hukouLocation = hukouLocation;
        this.mail = mail;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.accountProvinceName = accountProvinceName;
        this.accountCityName = accountCityName;
        this.accountName = accountName;
        this.bankCategory = bankCategory;
        this.cityName = cityName;
        this.taxProperties = taxProperties;
        this.jobNum = jobNum;
        this.hro = hro;
        this.businessUnit = businessUnit;
        this.employeeStatus = employeeStatus;
        this.synchronousCss = synchronousCss;
        InductionRemark = inductionRemark;
        this.taxBureau = taxBureau;
        this.employeeAttributes = employeeAttributes;
        this.socialSecurityStandards = socialSecurityStandards;
        this.weatherOnline = weatherOnline;
        this.workSystem = workSystem;
        this.weatherInductionE = weatherInductionE;
        setId(id);
        setCreater(creater);
        setCreatedTime(createdTime);
    }
}
