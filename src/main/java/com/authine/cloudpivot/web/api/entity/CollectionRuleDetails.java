package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CollectionRuleDetails
 * @Date 2020/4/2 13:47
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRuleDetails {
    /**
     * 险种，社保，公积金
     */
    String policyEnjoySuperclass;
    /**
     * 产品名称id
     */
    String policyCollectPay;
    /**
     * 补缴月份
     */
    Integer payMonth;
    /**
     * 补缴月份上限（不包含）
     */
    Integer payBackMaxMonth;
    /**
     * 补缴月份下限（包含）
     */
    Integer payBackMinMonth;
    /**
     * 险种名称
     */
    String insuranceName;
    /**
     * 产品名称
     */
    String productName;
    private String id;
    private String parentId;
    private Double sortKey;
}
