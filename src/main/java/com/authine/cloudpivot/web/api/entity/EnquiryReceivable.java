package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/17 13:35
 * @Description: 账单查询
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryReceivable extends BaseEntity {

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 业务部门
     */
    private String businessDept;

    /**
     * 委托单位
     */
    private String entrustedUnit;

    /**
     * 业务客户名称
     */
    private String businessCustomerName;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 账单年月
     */
    private String billYear;

    /**
     * 社保合计
     */
    private Double socialSecurityTotal = 0D;

    /**
     * 公积金合计
     */
    private Double accumulationFundTotal = 0D;

    /**
     * 实发工资合计
     */
    private Double realWagesTotal = 0D;

    /**
     * 个税合计
     */
    private Double personalTaxTotal = 0D;

    /**
     * 福利产品合计
     */
    private Double welfareProductsTotal = 0D;

    /**
     * 服务费
     */
    private Double serviceCharge = 0D;

    /**
     * 魏姚加
     * 一次性质服务费
     */
    private Double serviceChargeOnce = 0D;

    /**
     * 增值税税费
     */
    private Double valueAddedTaxTotal = 0D;

    /**
     * 风险管理费
     */
    private Double riskManagementFee = 0D;

    /**
     * 外包管理费
     */
    private Double outsourcingManageFee = 0D;

    /**
     * 营业税税费
     */
    private Double businessTaxesFees = 0D;

    /**
     * 应收总计
     */
    private Double receivableTotal = 0D;

    /**
     * 约定回款日期
     */
    private Date agreedPaymentDate;

    /**
     * 系统生成时间
     */
    private Date systemGenerationDate;

    /**
     * 是否锁定
     */
    private Integer isLock;

    /**
     * 账单修改人
     */
    private String billModify;

    /**
     * 账单修改时间
     */
    private Date billModifyDate;

    /**
     * 账单确认人
     */
    private String billDefine;

    /**
     * 账单是否确认
     */
    private Integer billWetherDefine;

}
