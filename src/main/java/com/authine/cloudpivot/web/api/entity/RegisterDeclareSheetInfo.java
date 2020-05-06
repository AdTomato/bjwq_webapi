package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName RegisterDeclareSheetInfo
 * @Author:lfh
 * @Date:2020/3/27 13:40
 * @Description: 合肥市就业失业登记、劳动用工备案、社会保险登记申请表
 **/
@Data
public class RegisterDeclareSheetInfo{

    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 姓名
     */
    private String  employee_name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号码
     */
    private String identityNo;

    /**
     * 用工形式或职务
     */
    private String workType;

    /**
     * 合同起始日期
     */
    private String contractBeginDate;

    /**
     * 合同终止日期
     */
    private String contractEndDate;

    /**
     * 合同期限
     */
    private String contractTerm;

    /**
     * 工资收入
     */
    private Double contract_salary;

    /**
     * 参加保险险种
     */
    // private String insuranceType;

    /**
     * 养老
     */
    private String insurancePension;

    /**
     * 失业
     */
    private String insuranceUnemployment;

    /**
     * 医疗
     */
    private String insuranceMedical;
    /**
     * 工伤
     */
    private String insuranceJobInjury;

    /**
     *  用工形式
     */
    private String employeeForm;

    /**
     * 户籍所在地
     */
    private String DomicileLocation;

    /**
     * 备注
     */
    private String remarks;

}
