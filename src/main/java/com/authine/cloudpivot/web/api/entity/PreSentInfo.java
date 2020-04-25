package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName PreSentInfo
 * @Author:lfh
 * @Date:2020/4/8 14:43
 * @Description: 批量预派对应excel实体类
 **/
@Data
public class PreSentInfo extends BaseEntity{

    /**
     * 序号
     */
    private Integer serialNum;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 派单日期
     */
    private String dispatchDate;

    /**
     * 客户名称
     */
    private String  clientName;

    /**
     *  姓名
     */
    private String employeeName;

    /**
     * 身份证号
     */
    private String identityNo;
    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 户籍性质
     */
    private String householdRegisterNature;

    /**
     * 福利地
     */
    private  String welfarePlaces;

    /**
     * 入职日期
     */
    private String entryTime;

    /**
     * 社保起做时间
     */
    private String socialSecurityStartTime;

    /**
     * 社保基数
     */
    private Double socialInsuranceAmount;

    /**
     * 公积金起做时间
     */
    private String providentFundStartTime;

    /**
     * 公积金基数
     */
    private Double providentFundAmount;

    /**
     * 公积金比例
     */
    private String providentFundProportion;
    /**
     * 备注
     */
    private String remarks;

}
