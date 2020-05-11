package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 社保申报实体
 * 字段名            增员_客户实体		增员_上海实体		增员_全国实体
 * 姓名		         姓名		        员工姓名		    姓名
 * 性别		         取自身份证		    取自身份证		    取自身份证
 * 18位证件号码		 身份证号码		    证件号		        证件号码
 * 出生日期		     取自身份证		    取自身份证		    取自身份证
 * 派出单位		     取自客户账号		固定值，上海德科	签约方供应商
 * 客户名称		     客户名称		    客户名称		    业务客户名称
 * 客服		         取自客户档案业务员	取自客户档案业务员	取自客户档案业务员
 * 客服部门		     客服的部门		    客服的部门		    客服的部门
 * 福利办理方		 取自委派单		    取自委派单		    取自委派单
 * 申请人		     增员创建人		    增员创建人		    增员创建人
 * 申请日期		     增员创建日期		增员创建日期		增员创建日期
 * 合同签订日期		 合同开始日期		不填		        不填
 * 合同截止日期		 合同结束日期		不填		        不填
 * 起始月		     社保起做时间		福利开始时间		订单开始日期
 * 转正工资		     合同工资		    社会保险基数		社保申报工资
 * 缴费基数		     社保基数		    社会保险基数		社保申报工资
 * 是否兼职工伤		 看是否有工伤		看是否有工伤		看是否有工伤
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
 * @Date 2020/2/25 16:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialSecurityDeclare extends BaseEntity {
    /** 员工订单id 新增的值*/
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

    /** 合同签订日期*/
    Date contractSigningDate;

    /** 合同截止日期*/
    Date contractDeadline;

    /** 起始月*/
    Date startMonth;

    /** 转正工资*/
    Double positiveSalary;

    /** 缴费基数*/
    Double basePay;

    /** 手机号码*/
    String mobile;

    /** 运行负责人*/
    String operateLeader;

    /** 已有退回原因*/
    String returnReasonAlready;

    String isChange;

    /** 待办，在办，预点，在缴，驳回*/
    String status;
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
     * 福利地
     */
    String city;
    /**
     * 账单年月
     */
    String billYear;
    /**
     * 备注
     */
    String remark;

    // 社保申报详细
    List <Map <String, String>> socialSecurityDetail;

    public SocialSecurityDeclare(String sequenceStatus, String creater, String createdDeptId, Date createdTime,
                                 String owner, String ownerDeptId, String ownerDeptQueryCode,
                                 Date startMonth, String employeeName, String gender, String identityNo,
                                 String identityNoType, Date contractSigningDate, Date contractDeadline,
                                 Double positiveSalary, Double basePay, String mobile, String welfareHandler,
                                 Date birthday, String operateLeader, String employeeOrderFormId, String status,
                                 String firstLevelClientName, String secondLevelClientName,
                                 String subordinateDepartment, String city, String remark) {
        this.creater = creater;
        this.createdDeptId = createdDeptId;
        this.createdTime = createdTime;
        this.sequenceStatus = sequenceStatus;
        this.owner = owner;
        this.ownerDeptId = ownerDeptId;
        this.ownerDeptQueryCode = ownerDeptQueryCode;
        this.startMonth = startMonth;
        this.employeeName = employeeName;
        this.gender = gender;
        this.identityNo = identityNo;
        this.identityNoType = identityNoType;
        this.contractSigningDate = contractSigningDate;
        this.contractDeadline = contractDeadline;
        this.positiveSalary = positiveSalary;
        this.basePay = basePay;
        this.mobile = mobile;
        this.welfareHandler = welfareHandler;
        this.birthday = birthday;
        this.operateLeader = operateLeader;
        this.employeeOrderFormId = employeeOrderFormId;
        this.status = status;
        this.firstLevelClientName = firstLevelClientName;
        this.secondLevelClientName = secondLevelClientName;
        this.subordinateDepartment = subordinateDepartment;
        this.city = city;
        this.remark = remark;

    }
}
