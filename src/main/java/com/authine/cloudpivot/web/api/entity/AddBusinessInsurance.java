package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.AddBusinessInsurance
 * @Date 2020/3/13 17:30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBusinessInsurance extends BaseEntity {
    /**
     * 业务员
     */
    String salesman;
    /**
     * 部门
     */
    String department;
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 身份证号码
     */
    String identityNo;
    /**
     * 银行卡号
     */
    String bankCardNumber;
    /**
     * 开户行
     */
    String bank;
    /**
     * 被保险人手机号
     */
    String mobile;
    /**
     * 商保套餐等级
     */
    String level;
    /**
     * 商保服务费
     */
    Double serviceFee;
    /**
     * 商保生效日
     */
    Date effectiveDate;
    /**
     * 商保套餐内容
     */
    String content;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 人员类别
     */
    String employeeType;
    /**
     * 办理状态
     */
    String status;
    /**
     * 主保险人姓名
     */
    String mainEmploeeName;
    /**
     * 主保险人身份证号
     */
    String mainIdentityNo;

    public AddBusinessInsurance(String id, UserModel user, DepartmentModel dept, String sequenceStatus, String salesman,
                                String department, String clientName, String employeeName, String identityNo,
                                String bankCardNumber, String bank, String accountOwnerName, String mobile,
                                String level, Double serviceFee, Date effectiveDate, String content) {
        setId(id);
        setName(employeeName);
        setCreater(user.getId());
        setCreatedDeptId(dept.getId());
        setOwner(user.getId());
        setOwnerDeptId(dept.getId());
        setCreatedTime(new Date());
        setModifier(user.getId());
        setModifiedTime(new Date());
        setSequenceStatus(sequenceStatus);
        setOwnerDeptQueryCode(dept.getQueryCode());
        this.salesman = salesman;
        this.department = department;
        this.firstLevelClientName = clientName;
        this.employeeName = employeeName;
        this.identityNo = identityNo;
        this.bankCardNumber = bankCardNumber;
        this.bank = bank;
        this.secondLevelClientName = accountOwnerName;
        this.mobile = mobile;
        this.level = level;
        this.serviceFee = serviceFee;
        this.effectiveDate = effectiveDate;
        this.content = content;
    }
}
