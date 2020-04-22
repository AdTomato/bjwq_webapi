package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.LaborContractInfo
 * @Date 2020/4/21 10:40
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaborContractInfo extends BaseEntity{
    /**
     * 业务部
     */
    String businessUnit;
    /**
     * 姓名
     */
    String personnelName;
    /**
     * 身份证号
     */
    String idnumber;
    /**
     * 员工状态
     */
    String employeeStatus;
    /**
     * 联系方式
     */
    String phone;
    /**
     * 邮箱
     */
    String email;
    /**
     * 员工类型
     */
    String employeeType;
    /**
     * 入职时间
     */
    Date entryTime;
    /**
     * 学历
     */
    String education;
    /**
     * 毕业院校
     */
    String graduatedSchool;
    /**
     * 毕业时间
     */
    Date graduatedTime;
    /**
     * 专业
     */
    String major;
    /**
     * 户籍所在地
     */
    String domicilePlace;
    /**
     * 现住址
     */
    String currentAddress;
    /**
     * 合同编号
     */
    String contractNum;
    /**
     * 劳动合同终止时间
     */
    Date contractEndTime;
    /**
     * 劳动合同起始时间
     */
    Date contractStartTime;
    /**
     * 试用期终止时间
     */
    Date probationEndTime;
    /**
     * 试用期起始时间
     */
    Date probationStartTime;
    /**
     * 用工形式
     */
    String employmentForms;
    /**
     * 工时类别
     */
    String workhoursCategory;
    /**
     * 员工岗位
     */
    String employeePosition;
    /**
     * 员工薪资
     */
    Double employeeSalary;
    /**
     * 试用期薪资
     */
    Double probationSalary;
    /**
     * 离职日期
     */
    Date resignationTime;
    /**
     * 离职原因
     */
    String resignationReason;
    /**
     * 员工档案
     */
    String employeeFilesId;
    /**
     * 一级客户
     */
    String firstLevelClientName;
    /**
     * 二级客户
     */
    String secondLevelClientName;

    public LaborContractInfo(AddEmployee addEmployee) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.creater = addEmployee.getCreater();
        this.createdDeptId = addEmployee.getCreatedDeptId();
        this.createdTime = addEmployee.getCreatedTime();
        this.sequenceStatus = "COMPLETED";
        this.owner = addEmployee.getOwner();
        this.ownerDeptId = addEmployee.getOwnerDeptId();
        this.ownerDeptQueryCode = addEmployee.getOwnerDeptQueryCode();
        this.modifier = addEmployee.getModifier();
        this.modifiedTime = addEmployee.getModifiedTime();
        this.name = addEmployee.getEmployeeName();

        this.businessUnit = addEmployee.getSubordinateDepartment();
        this.firstLevelClientName = addEmployee.getFirstLevelClientName();
        this.secondLevelClientName = addEmployee.getSecondLevelClientName();
        this.personnelName = addEmployee.getEmployeeName();
        this.idnumber = addEmployee.getIdentityNo();
        this.phone = addEmployee.getMobile();
        this.email = addEmployee.getEmail();
        this.entryTime = addEmployee.getEntryTime();
        this.contractStartTime = addEmployee.getContractStartTime();
        this.contractEndTime = addEmployee.getContractEndTime();
        this.employeeSalary = addEmployee.getContractSalary();
    }
}
