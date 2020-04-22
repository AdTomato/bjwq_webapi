package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName BaseInfoCollection
 * @Author:lfh
 * @Date:2020/3/12 15:07
 * @Description: 基数采集 excel对应的实体类
 **/
@Data
public class BaseInfoCollection extends BaseEntity{

    /**
     * 序号
     */
    private Integer serialNum;

    /**
     * 委托方
     */
    private String entrustedUnit;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     *证件号码
     */
     private String identityNo;

    /** 福利办理方
     *
     */
    private String welfareHandler;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 原基数
     */
    private String primaryBaseNum;

    /**
     * 新基数
     */
    private String nowBaseNum;

    /**
     * 公积金比例
     */
    private String providentFundProportion;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 发起基数采集id
     */
    private String start_collect_id;
}
