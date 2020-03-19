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

    /** 单位公积金比例*/
    Double companyProvidentFundBl;
    /** 个人公积金比例*/
    Double employeeProvidentFundBl;

    /** 备注*/
    String remark;
}
