package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二级客户
 *
 * @author wangyong
 * @time 2020/5/21 13:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecondLevelClient extends BaseEntity {

    /**
     * 关联一级客户
     */
    private String relatedFirstLevelClient;

    /**
     * 集团客户名称
     */
    private String groupCustomers;

    /**
     * 客户名称
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

}
