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
    /**
     * 增员_客户变更时对应的增员_客户表id
     */
    String addEmployeeId;

    /**
     * 姓名
     */
    String employeeName;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 联系电话
     */
    String mobile;
    /**
     * 邮箱
     */
    String email;
    /**
     * 户籍性质
     */
    String familyRegisterNature;
    /**
     * 员工性质
     */
    String employeeNature;
    /**
     * 入职日期
     */
    Date entryTime;
    /**
     * 合同开始日期
     */
    Date contractStartTime;
    /**
     * 合同结束日期
     */
    Date contractEndTime;
    /**
     * 合同工资
     */
    Double contractSalary;
    /**
     * 社保起做时间
     */
    Date socialSecurityStartTime;
    /**
     * 备注
     */
    String remark;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 社保基数
     */
    Double socialSecurityBase;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 公积金起做时间
     */
    Date providentFundStartTime;
    /**
     * 公积金基数
     */
    Double providentFundBase;
    /**
     * 证件类型
     */
    String identityNoType;
    /**
     * 单位公积金比例
     */
    Double companyProvidentFundBl;
    /**
     * 个人公积金比例
     */
    Double employeeProvidentFundBl;
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
     * 户籍备注
     */
    String householdRegisterRemarks;
    /**
     * 性别
     */
    String gender;
    /**
     * 日期
     */
    Date birthday;
    /**
     * 工作地
     */
    String workplace;
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
     * 社保福利办理方
     */
    String sWelfareHandler;
    /**
     * 公积金利办理方
     */
    String gWelfareHandler;
}
