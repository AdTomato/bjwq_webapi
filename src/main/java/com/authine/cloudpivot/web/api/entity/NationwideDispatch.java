package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * @author liulei
 * @Description 全国派单
 * @ClassName com.authine.cloudpivot.web.api.entity.NationwideDispatch
 * @Date 2020/2/25 16:08
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NationwideDispatch extends BaseEntity{

    /** 增员_全国修改表单中关联表单（增员_全国表单）id
     *  减员_全国修改表单中关联表单（减员_全国表单）id*/
    String nationwideDispatchId;
    String nationwideDispatchDelId;

    /** 唯一号*/
    String uniqueNum;

    /** 姓名*/
    String employeeName;

    /** 联系电话*/
    String contactNumber;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 全国业务流程标识*/
    String nationalBusinessWfFlag;

    /** 涉及执行地*/
    String involved;

    /** 业务客户编号*/
    String businessCustomerNum;

    /** 业务客户名称*/
    String businessCustomerName;

    /** 业务流转状态*/
    String businessWfStatus;

    /** 订单类型*/
    String orderType;

    /** 处理状态*/
    String processingStatus;

    /** 签约方供应商*/
    String contractingSupplier;

    /** 签约方业务代表*/
    String contractingRepresentative;

    /** 签约方业务部*/
    String contractingPartyDepartment;

    /** 进入待处理时间*/
    Date intoPendingDate;

    /** 任务单更新时间*/
    Date taskListUpdateDate;

    /** 入职日期*/
    Date entryDate;

    /** 员工内部编号*/
    String employeeInternalNum;

    /** 雇员邮箱*/
    String employeeEmail;

    /** 订单id*/
    String orderId;

    /** 订单开始日期*/
    Date orderStartDate;

    /** 办派日期*/
    Date assignmentDate;

    /** 入职手续数量*/
    Double entry_procedures_num;

    /** 业务提示*/
    String businessTips;

    /** 订单结束日期*/
    Date orderEndDate;

    /** 办撤日期*/
    Date withdrawalDate;

    /** 社保申报工资*/
    Double socialInsuranceAmount;

    /** 公积金申报工资*/
    Double providentFundAmount;

    /** 补充公积金申报工资*/
    Double suppleProvidentFundAmount;

    /** 委派撤销原因*/
    String revocationReason;

    /** 社保停做原因*/
    String socialSecurityStopReason;

    /** 离职日期*/
    Date departureDate;

    /** 离职备注*/
    String departureRemark;

    /** 要求变化明细内容*/
    String changeDetails;

    /** 要求变化生效时间*/
    Date changeTakeEffectDate;

    /** 业务部*/
    String businessUnit;

    /** 业务员*/
    String salesman;

    /** 社保缴费方式*/
    String socialSecurityPayMethod;

    /** 公积金缴费方式*/
    String providentFundPayMethod;

    /** 供应商协议名称*/
    String supplierAgreementName;

    /** 公积金比例（企业+个人）*/
    String providentFundRatio;

    /** 补充公积金比例（企业+个人）*/
    String suppleProvidentFundRatio;

    /** 社保地方标准*/
    String socialSecurityStandard;

    /** 社保服务费起做日期*/
    Date sServiceFeeStartDate;

    /** 社保服务费止做日期*/
    Date sServiceFeeEndDate;

    /** 公积金服务费起做日期*/
    Date gServiceFeeStartDate;

    /** 公积金服务费止做日期*/
    Date gServiceFeeEndDate;

    /** 备注*/
    String remark;

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
     * 性别
     */
    String gender;
    /**
     * 出生年月
     */
    Date birthday;
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
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 工作地
     */
    String workplace;
    /**
     * 户籍备注
     */
    String householdRegisterRemarks;

}
