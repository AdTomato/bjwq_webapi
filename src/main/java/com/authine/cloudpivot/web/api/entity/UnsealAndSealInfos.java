package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName UnsealAndSealInfos
 * @Author:lfh
 * @Date:2020/4/2 10:10
 * @Description: 启封封存模板映射bean
 **/
@Data
public class UnsealAndSealInfos  extends BaseEntity{
    /**
     * 序号
     */
    private int serialNumber;

    /**
     * 业务月度
     */
    private String businessMonth;

    /**
     * 个人账号
     */
    private String personalAccountNum;

    /**
     * 姓名
     */
    private String employee_name;
    /**
     * 证件类型
     */
    private String identityNo_type;
    /**
     * 证件号码
     */
    private String identityNo;
    /**
     * 变更类型
     */
    private String changeType;
    /**
     * 个人缴存基数
     */
    private Double personalSaveBase;
    /**
     * 单位缴存比例
     */
    private Double unitSaveProportion;
    /**
     * 个人缴存比例
     */
    private Double personalSaveProportion;
    /**
     * 月缴存总额
     */
    private Double monthSaveAccount;
    /**
     * 单位月缴存总额
     */
    private Double unitMonthSaveAccount;
    /**
     * 个人月缴存总额
     */
    private Double  personalMonthSaveCount;

    /**
     * 变更原因
     */
    private String changeReason;
}
