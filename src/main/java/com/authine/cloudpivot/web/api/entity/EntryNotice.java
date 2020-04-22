package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.EntryNotice
 * @Date 2020/2/26 11:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryNotice extends BaseEntity {
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 身份证号码
     */
    String identityNo;
    /**
     * 社保
     */
    String socialSecurity;
    /**
     * 公积金
     */
    String providentFund;
    /**
     * 递交方式
     */
    String submissionMethod;
    /**
     * 运行签收人
     */
    String operateSignatory;
    /**
     * 运行签收
     */
    String operateSignFor;
    /**
     * 单位公积金账号
     */
    String unitProvidentFundNum;
    /**
     * 个人公积金账号
     */
    String personalProvidentFundNum;
    /**
     * 就业登记证账号
     */
    String employmentRegisterNum;
    /**
     * 入职联系备注
     */
    String entryContactRemark;
    /**
     * 所属部门
     */
    String subordinateDepartment;
    /**
     * 一级客户
     */
    String firstLevelClientName;
    /**
     * 二级客户
     */
    String secondLevelClientName;
    /**
     * 联系电话
     */
    String mobile;
    /**
     * 社保福利地
     */
    String socialSecurityCity;
    /**
     * 公积金福利地
     */
    String providentFundCity;
    /**
     * 退役士兵
     */
    String isRetiredSoldier;
    /**
     * 残疾人
     */
    String isDisabled;
    /**
     * 贫困建档人员
     */
    String isPoorArchivists;
    /**
     * 用工备案
     */
    String recordOfEmployment;
    /**
     * 运行反馈
     */
    String feedback;
    /**
     * 入职联系状态
     */
    String status;
}
