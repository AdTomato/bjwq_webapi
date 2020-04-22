package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CollectionRule
 * @Date 2020/4/2 13:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRule extends BaseEntity{
    /**
     * 地区
     */
    String city;
    /**
     * 补缴月份
     */
    int payMonth;
    /**
     * 补缴月份上限（不包含）
     */
    int payBackMaxMonth;
    /**
     * 补缴月份下限（包含）
     */
    int payBackMinMonth;
    /**
     * 用工备案
     */
    String recordOfEmployment;
    /**
     * 社保
     */
    String socialSecurity;
    /**
     * 公积金
     */
    String providentFund;
    /**
     * 备注
     */
    String remark;
    /**
     * 规则详情
     */
    List <CollectionRuleDetails> collectionRuleDetails;
}
