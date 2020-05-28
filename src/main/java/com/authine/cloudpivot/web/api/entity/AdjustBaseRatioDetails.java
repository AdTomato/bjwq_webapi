package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 调基调比详情
 * @ClassName com.authine.cloudpivot.web.api.entity.AdjustBaseRatioDetails
 * @Date 2020/4/23 13:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjustBaseRatioDetails extends BaseEntity {
    /**
     * 一级客户名称
     */
    String firstLevelClientName;
    /**
     * 二级客户名称
     */
    String secondLevelClientName;
    /**
     * 员工姓名
     */
    String employeeName;
    /**
     * 证件号码
     */
    String identityNo;
    /**
     * 福利地
     */
    String city;
    /**
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 原基数
     */
    Double oldBase;
    /**
     * 新基数
     */
    Double newBase;
    /**
     * 公积金比例
     */
    Double providentFundRatio;
    /**
     * 备注
     */
    String remarks;
    /**
     * 调整任务
     */
    String taskId;
}
