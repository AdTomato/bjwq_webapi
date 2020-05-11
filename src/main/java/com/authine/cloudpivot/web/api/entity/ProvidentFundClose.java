package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 公积金停缴实体
 * 字段名		减员_客户实体		        减员_上海实体		        减员_全国实体
 * 姓名		    姓名		                员工姓名		            姓名
 * 性别		    取自身份证		            取自身份证		            取自身份证
 * 18位证件号码	身份证号码		            证件号		                证件号码
 * 出生日期		取自身份证		            取自身份证		            取自身份证
 * 派出单位		取自客户账号		        固定值，上海德科		    签约方供应商
 * 客服		    取自客户档案业务员		    取自客户档案业务员		    取自客户档案业务员
 * 福利办理方	取自委派单		            取自委派单		            取自委派单
 * 申请人		减员创建人		            减员创建人		            减员创建人
 * 申请日期		减员创建日期		        减员创建日期		        减员创建日期
 * 起始月		取自员工档案订单起始时间	取自员工档案订单起始时间	订单开始时间
 * 收费截止月	公积金终止时间		        公积金收费结束时间		    公积金订单截止日期
 * 公积金基数	取自员工档案公积金基数		取自员工档案公积金基数		公积金申报工资
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
 * @Date 2020/2/25 16:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvidentFundClose extends BaseEntity {
    /** 员工订单id*/
    String employeeOrderFormId;

    /** 姓名*/
    String employeeName;

    /** 性别*/
    String gender;

    /** 出生日期*/
    Date birthday;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 客户名称*/
    String	firstLevelClientName;
    String	secondLevelClientName;

    /** 福利办理方*/
    String welfareHandler;

    /** 起始月*/
    Date startMonth;

    /** 收费截止月*/
    Date chargeEndMonth;

    /** 公积金基数*/
    Double providentFundBase;

    /** 企业缴存额*/
    Double enterpriseDeposit;

    /** 个人缴存额*/
    Double personalDeposit;

    /** 缴存总额*/
    Double totalDeposit;

    /** 运行负责人*/
    String operateLeader;

    /** 待办，在办，停缴，驳回*/
    String status;

    String subordinateDepartment;
    String city;

    public ProvidentFundClose(String sequenceStatus, String creater, String createdDeptId, Date createdTime, String owner, String ownerDeptId, String ownerDeptQueryCode,
                              String employeeOrderFormId, String employeeName, String gender, Date birthday,
                              String identityNoType, String identityNo, String firstLevelClientName,
                              String secondLevelClientName, String welfareHandler, Date startMonth, Date chargeEndMonth,
                              Double providentFundBase, Double enterpriseDeposit, Double personalDeposit,
                              Double totalDeposit, String operateLeader, String status, String subordinateDepartment,
                              String city) {
        this.creater = creater;
        this.createdDeptId = createdDeptId;
        this.createdTime = createdTime;
        this.sequenceStatus = sequenceStatus;
        this.owner = owner;
        this.ownerDeptId = ownerDeptId;
        this.ownerDeptQueryCode = ownerDeptQueryCode;
        this.employeeOrderFormId = employeeOrderFormId;
        this.employeeName = employeeName;
        this.gender = gender;
        this.birthday = birthday;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.firstLevelClientName = firstLevelClientName;
        this.secondLevelClientName = secondLevelClientName;
        this.welfareHandler = welfareHandler;
        this.startMonth = startMonth;
        this.chargeEndMonth = chargeEndMonth;
        this.providentFundBase = providentFundBase;
        this.enterpriseDeposit = enterpriseDeposit;
        this.personalDeposit = personalDeposit;
        this.totalDeposit = totalDeposit;
        this.operateLeader = operateLeader;
        this.status = status;
        this.subordinateDepartment = subordinateDepartment;
        this.city = city;
    }
}
