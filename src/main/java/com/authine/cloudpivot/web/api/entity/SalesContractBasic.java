package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 销售合同主表
 *
 * @author wangyong
 * @Date:2020/3/18 0:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesContractBasic extends BaseEntity {

    /**
     * 合同编号
     */
    private String contractNum;

    /**
     * 合同状态
     */
    private String contractStates;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 销售人员
     */
    private String salesPerson;

    /**
     * 销售部门
     */
    private String salesDepartment;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 业务部门
     */
    private String businessUnit;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 客户来源
     */
    private String clientSource;

    /**
     * 产品服务
     */
    private String productService;

    /**
     * 账单类型
     */
    private String billType;

    /**
     * 账单周期
     */
    private String billCycle;

    /**
     * 生成账单日期
     */
    private Date generateSillDate;

    /**
     * 账单日
     */
    private String billDay;

    /**
     * 费用及开票形式
     */
    private String feesInvoic;

    /**
     * 账单生成时间
     */
    private Date generateBillDate;

    /**
     * 约定回款日
     */
    private String agreedRepaymentDate;

    /**
     * 单价
     */
    private Double unitPrice;

    /**
     * 数量
     */
    private Double quantity;

    /**
     * 总金额
     */
    private Double totalAmount;

    /**
     * 合同签订日
     */
    private Date contractSigningDate;

    /**
     * 合同到期日
     */
    private Date contractExpiryDate;

    /**
     * 续签合同签订日
     */
    private Date contractRenewalDate;

    /**
     * 续签合同到期日
     */
    private Date renewalContractExpiryDate;

    /**
     * 合同终止日期
     */
    private Date contractTerminationDate;

    /**
     * 合同终止原因
     */
    private String contractTerminationReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 合同提醒日
     */
    private Date contractReminderDay;

    /**
     * 是否提醒
     */
    private String whetherRemind;

    /**
     * 是否终止
     */
    private String whetherEnd;
}
