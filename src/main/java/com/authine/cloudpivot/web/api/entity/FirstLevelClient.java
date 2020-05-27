package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一级客户
 *
 * @author wangyong
 * @time 2020/5/20 9:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirstLevelClient extends BaseEntity {

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 客户级别
     */
    private String clientLevel;

    /**
     * 企业性质
     */
    private String enterpriseNature;

    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 公司税号
     */
    private String companyTaxNum;

    /**
     * 银行开户行及账号
     */
    private String bankAndAccount;

    /**
     * 注册地址及电话
     */
    private String registeredAddressAndPhone;

    /**
     * 发票收件信息
     */
    private String invoiceReceiptInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 查询人
     */
    private String inquirer;

    /**
     * 客户分类
     */
    private String customerClassification;

    /**
     * 部门代码
     */
    private String departmentCode;

    /**
     * 客户代码
     */
    private String customerCode;

    /**
     * 毛利分析代码
     */
    private String grossProfitAnalysisCode;

}
