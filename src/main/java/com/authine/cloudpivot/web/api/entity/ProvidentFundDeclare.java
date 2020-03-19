package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 公积金申报实体
 * 字段名		增员_客户实体		增员_上海实体		增员_全国实体
 * 姓名		    姓名		        员工姓名		    姓名
 * 性别		    取自身份证		    取自身份证		    取自身份证
 * 18位证件号码	身份证号码		    证件号		        证件号码
 * 出生日期		取自身份证		    取自身份证		    取自身份证
 * 派出单位		取自客户账号	    固定值，上海德科	签约方供应商
 * 客户名称		客户名称		    客户名称		    业务客户名称
 * 客服		    取自客户档案业务员	取自客户档案业务员	取自客户档案业务员
 * 客服部门		客服的部门		    客服的部门		    客服的部门
 * 福利办理方	取自委派单		    取自委派单		    取自委派单
 * 申请人		增员创建人		    增员创建人		    增员创建人
 * 申请日期		增员创建日期		增员创建日期		增员创建日期
 * 起始月		公积金起做时间		公积金开始时间		订单开始日期
 * 已有退回原因	手填		        手填		        手填
 * 公积金基数	公积金基数		    住房公积金基数		公积金申报工资
 * 企业比例		公积金比例企业比例	补充住房公积金比例	公积金比例（企业+个人）
 * 个人比例		公积金比例个人比例	补充住房公积金比例	公积金比例（企业+个人）
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
 * @Date 2020/2/25 16:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvidentFundDeclare extends BaseEntity {
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

    /** 客服部门*/
    String customerServices;

    /** 福利办理方*/
    String welfareHandler;

    /** 申请人*/
    String applicant;

    /** 申请日期*/
    Date applicationDate;

    /** 起始月*/
    Date startMonth;

    /** 公积金基数*/
    Double providentFundBase;

    /** 企业缴存额*/
    Double corporatePayment;

    /** 个人缴存额*/
    Double personalDeposit;

    /** 缴存总额*/
    Double totalDeposit;

    /** 运行负责人*/
    String operateLeader;

    String isChange;

    // 社保申报详细
    List <Map <String, String>> providentFundDetail;

    public ProvidentFundDeclare(String employeeName, String gender, Date birthday,
                                String identityNoType, String identityNo, String dispatchUnit, String clientName,
                                String customerService, String customerServices, String welfareHandler,
                                String applicant, Date applicationDate, Date startMonth, Double providentFundBase,
                                String operateLeader, List <Map <String, String>> providentFundDetail) {
        this.employeeName = employeeName;
        this.gender = gender;
        this.birthday = birthday;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.dispatchUnit = dispatchUnit;
        this.clientName = clientName;
        this.customerService = customerService;
        this.customerServices = customerServices;
        this.welfareHandler = welfareHandler;
        this.applicant = applicant;
        this.applicationDate = applicationDate;
        this.startMonth = startMonth;
        this.providentFundBase = providentFundBase;
        this.operateLeader = operateLeader;
        this.providentFundDetail = providentFundDetail;
    }
}
