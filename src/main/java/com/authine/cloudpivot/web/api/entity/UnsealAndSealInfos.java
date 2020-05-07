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
     * 个人账号
     */
    private String personalAccountNum;

    /**
     * 变更原因
     */
    private String changeReason;



    /**
     * 个人缴存比例
     */
    private Double personalSaveProportion;


    /**
     * 截止月
     */
    private String charge_end_month;

}
