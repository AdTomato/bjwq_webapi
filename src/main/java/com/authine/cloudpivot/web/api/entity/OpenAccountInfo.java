package com.authine.cloudpivot.web.api.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName OpenAccountInfo
 * @Author:lfh
 * @Date:2020/3/28 14:13
 * @Description: 导出开户信息对应excel实体类
 **/
@Data
public class OpenAccountInfo  extends BaseEntity{
    /**
     * 序号
     */
    // private Integer serialNumber;

    //福利办理方
    private String welfare_handler;
    //福利地
    private String city;
    //一级库户名称
    private String first_level_client_name;
    //二级库户名称
    private String second_level_client_name;
    // 业务员
    private String owner;
    //姓名
    private String employee_name;
    //证件号码
    private String identityNo;

    /**
     * 公积金起缴日期--起缴月
     */
    private String start_month;
    /**
     * 公积金基数
     */
    private Double provident_fund_base;

    //单位公积金比例
    private Double company_ratio;

    //个人公积金比例
    private Double employee_ratio;
    //单位缴存额
    private Double corporate_payment;

    //个人缴存额
    private Double personal_deposit;

    //缴存额
    private Double total_deposit;

    //联系电话
    private String phone;
    //现住址
    private String current_address;





}
