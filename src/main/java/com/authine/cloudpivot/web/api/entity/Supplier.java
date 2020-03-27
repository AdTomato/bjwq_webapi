package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.Supplier
 * @Date 2020/3/20 16:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {
    /**
     * 公司名称
     */
    String companyName;
    /**
     * 公司税号
     */
    String companyTaxNumber;
    /**
     * 开户行
     */
    String openingBank;
    /**
     * 银行账号
     */
    String bankAccount;
    /**
     * 开户地
     */
    String openingPlace;
    /**
     * 注册地址及电话
     */
    String registerAddressAndPhone;
    /**
     * 联系人
     */
    String contacts;
    /**
     * 联系电话
     */
    String phone;
    /**
     * 联系地址
     */
    String address;
    /**
     * 联系邮箱
     */
    String email;
    /**
     * 备注
     */
    String remarks;
    /**
     * 业务员
     */
    String salesman;
    /**
     * 供应商代码
     */
    String supplierCode;
    /**
     * 供应商分类
     */
    String supplierType;
}
