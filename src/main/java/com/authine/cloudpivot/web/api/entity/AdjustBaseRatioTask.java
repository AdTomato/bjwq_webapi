package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 调基调比任务
 * @ClassName com.authine.cloudpivot.web.api.entity.AdjustBaseRatioTask
 * @Date 2020/4/23 14:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjustBaseRatioTask  extends BaseEntity {
    /**
     * 调整任务名称
     */
    String taskName;
    /**
     * 开始调整时间
     */
    Date startTime;
    /**
     * 福利地
     */
    String city;
    /**
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 调整类型
     */
    String adjustType;
    /**
     * 是否替换比例
     */
    String replaceRatioOrNot;
    /**
     * 险种
     */
    String productId;
    /**
     * 新企业比例
     */
    Double companyRatio;
    /**
     * 新个人比例
     */
    Double employeeRatio;
    /**
     * 调整时间
     */
    Date adjustTime;
    /**
     * 调整状态
     */
    String adjustStatus;
}
