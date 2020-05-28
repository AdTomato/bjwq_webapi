package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.UpdateBusinessInsurance
 * @Date 2020/5/7 14:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBusinessInsurance {
    /**
     * 姓名
     */
    String employeeName;
    /**
     * 身份证号码
     */
    String identityNo;
    /**
     * 变更日期
     */
    Date changeDate;
    /**
     * 原商保套餐等级
     */
    String beforeLevel;
    /**
     * 原商保服务费
     */
    Double beforeServiceFee;
    /**
     * 原商保生效日
     */
    Date beforeEffectiveDate;
    /**
     * 原商保套餐内容
     */
    String beforeContent;
    /**
     * 变更后商保套餐等级
     */
    String afterLevel;
    /**
     * 变更后商保服务费
     */
    Double afterServiceFee;
    /**
     * 变更后商保生效日
     */
    Date afterEffectiveDate;
    /**
     * 变更后商保套餐内容
     */
    String afterContent;
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
}
