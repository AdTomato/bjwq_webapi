package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName LeaveInfo
 * @Author:lfh
 * @Date:2020/4/9 9:41
 * @Description: 批量撤离实体类对应excel
 **/
@Data
public class LeaveInfo extends BaseEntity {
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
     * 福利地
     */
    private  String welfarePlaces;

    /**
     * 离职日期
     */
    private String departureDate;

    /**
     * 离职原因
     */
    private String departureReason;

    /**
     * 社保终止时间
     */
    private String socialSecurityEndTime;

    /**
     * 公积金终止时间
     */
    private String providentFundEndTime;

    /**
     * 备注
     */
    private String remarks;
}
