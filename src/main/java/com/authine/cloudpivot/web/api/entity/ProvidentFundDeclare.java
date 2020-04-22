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

    /** 福利办理方*/
    String welfareHandler;

    String city;

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

    /** 待办，在办，预点，在缴，驳回*/
    String status;

    /** 已有退回原因*/
    String returnReasonAlready;

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

    // 社保申报详细
    List <Map <String, String>> providentFundDetail;

    public ProvidentFundDeclare(String sequenceStatus, String creater, String createdDeptId, Date createdTime,
                                String owner, String ownerDeptId,
                                String ownerDeptQueryCode, String employeeOrderFormId,
                                String employeeName, String gender, Date birthday,
                                String identityNoType, String identityNo,
                                String welfareHandler, Date startMonth,
                                Double providentFundBase, Double corporatePayment,
                                Double personalDeposit, Double totalDeposit,
                                String operateLeader, String status, String city,
                                String firstLevelClientName, String secondLevelClientName,
                                String subordinateDepartment) {
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
        this.welfareHandler = welfareHandler;
        this.startMonth = startMonth;
        this.providentFundBase = providentFundBase;
        this.corporatePayment = corporatePayment;
        this.personalDeposit = personalDeposit;
        this.totalDeposit = totalDeposit;
        this.operateLeader = operateLeader;
        this.status = status;
        this.city = city;
        this.firstLevelClientName = firstLevelClientName;
        this.secondLevelClientName = secondLevelClientName;
        this.subordinateDepartment = subordinateDepartment;
    }
}
