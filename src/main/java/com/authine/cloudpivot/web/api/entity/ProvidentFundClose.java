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

    /** 派出单位*/
    String dispatchUnit;

    /** 客户名称*/
    String clientName;

    /** 客服*/
    String customerService;

    /** 福利办理方*/
    String welfareHandler;

    /** 申请人*/
    String applicant;

    /** 申请日期*/
    Date applicationTime;

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
}
