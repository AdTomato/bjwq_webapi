package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 09:44
 * @Description: 批量预派
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchPreDispatch extends BaseEntity {

    /**
     * 姓名
     */
    private String employeeName;

    /**
     * 唯一号
     */
    private String uniqueNum;

    /**
     * 身份证
     */
    private String identityNo;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 社保申报工资
     */
    private Double socialInsuranceAmount;

    /**
     * 公积金比例
     */
    private String providentFundRatio;

    /**
     * 补充公积金比例
     */
    private String suppleProvidentFundRatio;

    /**
     * 公积金申报工资
     */
    private Double providentFundAmount;

    /**
     * 入职日期
     */
    private Date entryDate;

    /**
     * 订单开始日期
     */
    private Date orderStartDate;

    /**
     * 订单预计结束日期
     */
    private Date orderExpectedEndDate;

    /**
     * 客户劳动合同开始日期
     */
    private Date contractStartTime;

    /**
     * 客户劳动合同结束日期
     */
    private Date contractEndTime;

    /**
     * 内部员工编号
     */
    private String employeeInternalNum;

    /**
     * 是否按外籍人报税
     */
    private String whetherForeignTaxReturn;

    /**
     * 是否劳务费
     */
    private String whetherServiceFee;

    /**
     * 委派地区
     */
    private String delegatedArea;

    /**
     * 人员组成类型
     */
    private String staffingType;

    /**
     * 人员组成
     */
    private String staffing;

    /**
     * 职务
     */
    private String position;

    /**
     * 做中行卡
     */
    private String doBankCard;

    /**
     * 是否京源毕业生
     */
    private String whetherBeijingGraduate;

    /**
     * 备注
     */
    private String remark;

    private String employeeNature;

    private String addEmployeeId;

    public BatchPreDispatch(AddEmployee addEmployee, boolean sbHave, boolean sbNotInAnhui, boolean gjjHave,
                            boolean gjjNotInAnhui) {
        this.addEmployeeId = addEmployee.getId();
        this.employeeName = addEmployee.getEmployeeName();
        this.identityNo = addEmployee.getIdentityNo();
        this.mobile = addEmployee.getMobile();
        this.entryDate = addEmployee.getEntryTime();
        this.orderStartDate = addEmployee.getCreatedTime();
        this.remark = addEmployee.getRemark();
        this.employeeNature = addEmployee.getEmployeeNature();
        if (gjjHave && gjjNotInAnhui) {
            this.providentFundRatio =
                    addEmployee.getCompanyProvidentFundBl() + "+" + addEmployee.getEmployeeProvidentFundBl();
            this.providentFundAmount = addEmployee.getProvidentFundBase();
            this.delegatedArea = addEmployee.getProvidentFundCity();
        }
        if (sbHave && sbNotInAnhui) {
            this.socialInsuranceAmount = addEmployee.getSocialSecurityBase();
            this.delegatedArea = addEmployee.getSocialSecurityCity();
        }
    }
}
