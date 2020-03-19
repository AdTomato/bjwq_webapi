package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 增员_客户实体
 * @ClassName com.authine.cloudpivot.web.api.entity.AddEmployee
 * @Date 2020/2/25 14:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployee extends BaseEntity {
    String addEmployeeId;
    /** 客户名称*/
    String clientName;

    /** ERP成本中心*/
    String erp;

    /** 姓名*/
    String employeeName;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 联系电话*/
    String mobile;

    /** 邮箱*/
    String email;

    /** 户籍性质*/
    String familyRegisterNature;

    /** 员工性质*/
    String employeeNature;

    /** 入职日期*/
    Date entryTime;

    /** 合同开始日期*/
    Date contractStartTime;

    /** 合同结束日期*/
    Date contractEndTime;

    /** 合同工资*/
    Double contractSalary;

    /** 社保福利地*/
    String socialSecurityCity;

    /** 社保起做时间*/
    Date socialSecurityStartTime;

    /** 社保基数*/
    Double socialSecurityBase;

    /** 公积金福利地*/
    String providentFundCity;

    /** 公积金起做时间*/
    Date providentFundStartTime;

    /** 公积金基数*/
    Double providentFundBase;

    /** 公积金比例*/
    Double providentFundRatio;

    /** 备注*/
    String remark;

    public AddEmployee(String clientName, String erp, String employeeName, String identityNoType, String identityNo,
                       String mobile, String email, String familyRegisterNature, String employeeNature,
                       Date entryTime, Date contractStartTime, Date contractEndTime, Double contractSalary,
                       String socialSecurityCity, Date socialSecurityStartTime, Double socialSecurityBase,
                       String providentFundCity, Date providentFundStartTime, Double providentFundBase,
                       Double providentFundRatio, String remark, String id, String creater, Date createdTime) {
        this.clientName = clientName;
        this.erp = erp;
        this.employeeName = employeeName;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.mobile = mobile;
        this.email = email;
        this.familyRegisterNature = familyRegisterNature;
        this.employeeNature = employeeNature;
        this.entryTime = entryTime;
        this.contractStartTime = contractStartTime;
        this.contractEndTime = contractEndTime;
        this.contractSalary = contractSalary;
        this.socialSecurityCity = socialSecurityCity;
        this.socialSecurityStartTime = socialSecurityStartTime;
        this.socialSecurityBase = socialSecurityBase;
        this.providentFundCity = providentFundCity;
        this.providentFundStartTime = providentFundStartTime;
        this.providentFundBase = providentFundBase;
        this.providentFundRatio = providentFundRatio;
        this.remark = remark;
        setId(id);
        setCreater(creater);
        setCreatedTime(createdTime);
    }
}
