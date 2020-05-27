package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @Description 提交之前返回查询到的信息
 * @ClassName com.authine.cloudpivot.web.api.entity.QueryInfo
 * @Date 2020/5/6 13:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfo {
    /** 操作人*/
    String operator;
    /** 查询人*/
    String inquirer;
    /** 所属部门*/
    String subordinateDepartment;
    /** 福利地 */
    String city;
    /** 福利办理方 */
    String welfareHandler;
    /** 省份 */
    String province;
    /** 一级客户名称 */
    String firstLevelClientName;
    /** 二级客户名称 */
    String secondLevelClientName;

    /** 是否VIP员工 */
    String isVip;

    /** 原商保套餐等级 */
    String beforeLevel;
    /** 原商保服务费 */
    Double beforeServiceFee;
    /** 原商保生效日 */
    Date beforeEffectiveDate;
    /** 原商保套餐内容 */
    String beforeContent;

    /** 超出提交时间 */
    Boolean overTime;
    /** 返回原因*/
    String returnReason;
    /** 性别*/
    String gender;
    /** 生日*/
    Date birthday;

    List <NccpsProvidentFundRatio> nccpsProvidentFundRatios;

}
