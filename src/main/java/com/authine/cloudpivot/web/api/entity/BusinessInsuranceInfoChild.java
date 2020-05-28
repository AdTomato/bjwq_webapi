package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.BusinessInsuranceInfoChild
 * @Date 2020/5/7 14:11
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessInsuranceInfoChild {
    /**
     * 子女姓名
     */
    String childrenName;
    /**
     * 子女证件号码
     */
    String childrenIdentityNo;
    /**
     * 主键id
     */
    String id;
    /**
     * 子表业务对象父类ID
     */
    String parentId;
    /**
     * 子表排序号
     */
    Double sortKey;
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
     * 员工状态
     */
    String employeeStatus;
    /**
     * 福利截止时间
     */
    Date benefitDeadline;

    public BusinessInsuranceInfoChild(String parentId, AddBusinessInsurance insurance) {
        this.childrenName = insurance.getEmployeeName();
        this.childrenIdentityNo = insurance.getIdentityNo();
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.parentId = parentId;
        this.sortKey = 0d;
        this.level = insurance.getLevel();
        this.serviceFee = insurance.getServiceFee();
        this.effectiveDate = insurance.getEffectiveDate();
        this.employeeStatus = "在职";
    }
}
