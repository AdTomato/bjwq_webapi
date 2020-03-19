package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-14 15:57
 * @Description: 员工档案实体类
 * 增员
 * 字段名称		    增员_客户实体		        增员_上海实体		        增员_全国实体
 * 委托单位		    从客户账号查询		        签约方供应商		        固定值，北京外企德科
 * 客户名称		    客户名称		            业务客户名称		        客户名称
 * 业务员		    从客户档案查询		        从客户档案查询		        从客户档案查询
 * 员工姓名		    姓名		                姓名		                员工姓名
 * 证件类型		    证件类型		            证件类型		            证件类型
 * 证件号码		    证件号码		            证件号码		            证件号码
 * 性别		        有身份证号从身份证号获取	有身份证号从身份证号获取	有身份证号从身份证号获取
 * 出生年月		    有身份证号从身份证号获取	有身份证号从身份证号获取	有身份证号从身份证号获取
 * 员工性质		    员工性质		            不填		                不填
 * 户籍性质		    户籍性质		            不填		                不填
 * 联系电话		    联系电话		            联系电话		            手机
 * 合同开始日期	    合同开始日期		        不填		                不填
 * 合同结束日期	    合同结束日期		        不填		                不填
 * 合同工资		    合同工资		            不填		                不填
 * 社保福利地	    社保福利地		            涉及执行地		            城市名称
 * 公积金福利地	    公积金福利地		        涉及执行地		            城市名称
 * 报入职时间	    增员创建时间		        增员创建时间		        增员创建时间
 * 报入职人		    增员创建人		            增员创建人		            增员创建人
 * 入职日期		    入职日期		            入职日期		            入职时间
 * 社保收费开始	    社保起做时间		        社保订单开始日期		    福利起始时间
 * 公积金收费开始	公积金起做时间		        公积金订单开始日期		    公积金开始时间
 * 社保福利办理方	从委派单设置获取		    从委派单设置获取		    从委派单设置获取
 * 公积金福利办理方	从委派单设置获取		    从委派单设置获取		    从委派单设置获取
 * 入职备注		    备注		                备注		                入职备注
 * 是否入职通知		判断是否有入职材料清单		判断是否有入职材料清单		判断是否有入职材料清单
 * 是否发薪		    从薪资档案获取		        从薪资档案获取		        从薪资档案获取
 * 银行卡账号		从薪资档案获取		        从薪资档案获取		        从薪资档案获取
 * 开户行		    从薪资档案获取		        从薪资档案获取		        从薪资档案获取
 * 开户地		    从薪资档案获取		        从薪资档案获取		        从薪资档案获取
 * 邮箱		        邮箱		                雇员邮箱		            电子邮件
 *
 * 减员
 * 字段名称		    减员_客户实体		        减员_上海实体		        减员_全国实体
 * 报离职时间		减员创建时间		        减员创建时间		        减员创建时间
 * 报离职人		    减员创建人		            减员创建人		            减员创建人
 * 离职日期		    离职日期		            离职日期		            离职时间
 * 社保收费截止		社保终止时间		        社保订单结束日期		    社保收费结束时间
 * 公积金收费截止	公积金终止时间		        公积金订单结束日期		    公积金收费结束时间
 * 离职原因		    离职原因		            社保停做原因		        离职原因
 * 离职备注		    备注		                离职备注		            离职备注
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFiles extends BaseEntity{

    String employeeFilesId;

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

    /**
     * 停止生成账单
     */
    private Integer stopGenerateBill;


    /**
     * 方法说明：增员客户创建员工档案实体
     * @param entrustedUnit 委托单位
     * @param clientName 客户名称
     * @param employeeName 员工姓名
     * @param idType 证件类型
     * @param idNo 证件号码
     * @param gender 性别
     * @param birthDate 出生年月
     * @param employeeNature 员工性质
     * @param householdRegisterNature 户籍性质
     * @param mobile 联系电话
     * @param labourContractStartTime 合同开始日期
     * @param labourContractEndTime 合同结束日期
     * @param salary 合同工资
     * @param socialSecurityCity 社保福利地
     * @param providentFundCity 公积金福利地
     * @param reportEntryTime 报入职时间
     * @param entryTime 入职时间
     * @param socialSecurityChargeStart 社保收费开始
     * @param providentFundChargeStart 公积金收费开始
     * @param socialSecurityArea 社保福利办理方
     * @param providentFundArea 公积金福利办理方
     * @param entryDescription 入职备注
     * @param email 邮箱
     * @author liulei
     * @Date 2020/2/26 10:49
     */
    public EmployeeFiles(String entrustedUnit, String clientName, String employeeName, String idType
            , String idNo, String gender, Date birthDate, String employeeNature, String householdRegisterNature,
                         String mobile, Date labourContractStartTime, Date labourContractEndTime, Double salary,
                         String socialSecurityCity, String providentFundCity, Date reportEntryTime,
                         Date entryTime, Date socialSecurityChargeStart,
                         Date providentFundChargeStart, String socialSecurityArea, String providentFundArea,
                         String entryDescription, String email) {
        this.entrustedUnit = entrustedUnit;
        this.clientName = clientName;
        this.employeeName = employeeName;
        this.idType = idType;
        this.idNo = idNo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.employeeNature = employeeNature;
        this.householdRegisterNature = householdRegisterNature;
        this.mobile = mobile;
        this.labourContractStartTime = labourContractStartTime;
        this.labourContractEndTime = labourContractEndTime;
        this.salary = salary;
        this.socialSecurityCity = socialSecurityCity;
        this.providentFundCity = providentFundCity;
        this.reportEntryTime = reportEntryTime;
        this.entryTime = entryTime;
        this.socialSecurityChargeStart = socialSecurityChargeStart;
        this.providentFundChargeStart = providentFundChargeStart;
        this.socialSecurityArea = socialSecurityArea;
        this.providentFundArea = providentFundArea;
        this.entryDescription = entryDescription;
        this.email = email;
    }

    /**
     * 方法说明：增员_上海&&增员_全国创建员工档案实体
     * @param entrustedUnit 委托单位
     * @param clientName 客户名称
     * @param employeeName 员工姓名
     * @param idType 证件类型
     * @param idNo 证件号码
     * @param gender 性别
     * @param birthDate 出生年月
     * @param employeeNature 员工性质
     * @param mobile 联系电话
     * @param socialSecurityCity 社保福利地
     * @param providentFundCity 公积金福利地
     * @param reportEntryTime 报入职时间
     * @param entryTime 入职时间
     * @param socialSecurityChargeStart 社保收费开始
     * @param providentFundChargeStart 公积金收费开始
     * @param socialSecurityArea 社保福利办理方
     * @param providentFundArea 公积金福利办理方
     * @param entryDescription 入职备注
     * @param email 邮箱
     * @author liulei
     * @Date 2020/2/26 10:49
     */
    public EmployeeFiles(String entrustedUnit, String clientName, String employeeName, String idType, String idNo,
                         String gender, Date birthDate, String employeeNature, String mobile, String socialSecurityCity,
                         String providentFundCity, Date reportEntryTime, Date entryTime, Date socialSecurityChargeStart,
                         Date providentFundChargeStart, String socialSecurityArea, String providentFundArea,
                         String entryDescription, String email) {
        this.entrustedUnit = entrustedUnit;
        this.clientName = clientName;
        this.employeeName = employeeName;
        this.idType = idType;
        this.idNo = idNo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mobile = mobile;
        this.socialSecurityCity = socialSecurityCity;
        this.providentFundCity = providentFundCity;
        this.reportEntryTime = reportEntryTime;
        this.entryTime = entryTime;
        this.socialSecurityChargeStart = socialSecurityChargeStart;
        this.providentFundChargeStart = providentFundChargeStart;
        this.socialSecurityArea = socialSecurityArea;
        this.providentFundArea = providentFundArea;
        this.entryDescription = entryDescription;
        this.email = email;
    }

    public EmployeeFiles(String entrustedUnit, String clientName, String salesman, String employeeName, String idType
            , String idNo, String gender, Date birthDate, String employeeNature, String householdRegisterNature,
                         String mobile, Date labourContractStartTime, Date labourContractEndTime, Double salary,
                         Date probationStartTime, Date probationEndTime, Double probationSalary,
                         String socialSecurityCity, String providentFundCity, Date reportEntryTime,
                         String reportRecruits, Date entryTime, Date socialSecurityChargeStart,
                         Date providentFundChargeStart, String socialSecurityArea, String providentFundArea,
                         String entryDescription, Date reportQuitDate, String reportSeveranceOfficer, Date quitDate,
                         Date socialSecurityChargeEnd, Date providentFundChargeEnd, String quitReason,
                         String quitRemark, String entryNotice, String healthCheck, String whetherPay,
                         String bankAccountNumber, String bankName, String bankArea, String position, String email,
                         String employeeLabels) {
        this.entrustedUnit = entrustedUnit;
        this.clientName = clientName;
        this.salesman = salesman;
        this.employeeName = employeeName;
        this.idType = idType;
        this.idNo = idNo;
        this.gender = gender;
        this.birthDate = birthDate;
        this.employeeNature = employeeNature;
        this.householdRegisterNature = householdRegisterNature;
        this.mobile = mobile;
        this.labourContractStartTime = labourContractStartTime;
        this.labourContractEndTime = labourContractEndTime;
        this.salary = salary;
        this.probationStartTime = probationStartTime;
        this.probationEndTime = probationEndTime;
        this.probationSalary = probationSalary;
        this.socialSecurityCity = socialSecurityCity;
        this.providentFundCity = providentFundCity;
        this.reportEntryTime = reportEntryTime;
        this.reportRecruits = reportRecruits;
        this.entryTime = entryTime;
        this.socialSecurityChargeStart = socialSecurityChargeStart;
        this.providentFundChargeStart = providentFundChargeStart;
        this.socialSecurityArea = socialSecurityArea;
        this.providentFundArea = providentFundArea;
        this.entryDescription = entryDescription;
        this.reportQuitDate = reportQuitDate;
        this.reportSeveranceOfficer = reportSeveranceOfficer;
        this.quitDate = quitDate;
        this.socialSecurityChargeEnd = socialSecurityChargeEnd;
        this.providentFundChargeEnd = providentFundChargeEnd;
        this.quitReason = quitReason;
        this.quitRemark = quitRemark;
        this.entryNotice = entryNotice;
        this.healthCheck = healthCheck;
        this.whetherPay = whetherPay;
        this.bankAccountNumber = bankAccountNumber;
        this.bankName = bankName;
        this.bankArea = bankArea;
        this.position = position;
        this.email = email;
        this.employeeLabels = employeeLabels;
    }
}
