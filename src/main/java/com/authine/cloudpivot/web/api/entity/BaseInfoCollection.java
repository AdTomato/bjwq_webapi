package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @ClassName BaseInfoCollection
 * @Author:lfh
 * @Date:2020/3/12 15:07
 * @Description: 基数采集 excel对应的实体类
 **/
@Data
public class BaseInfoCollection {

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
     * 新比例
     */
    private String newProportion;


}
