package com.authine.cloudpivot.web.api.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName ContractTerminationInfo
 * @Author:lfh
 * @Date:2020/3/27 9:33
 * @Description: 合同解除终止对应excel
 **/
@Data
public class ContractTerminationInfo  extends BaseEntity{

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
    //性别
    private String gender;
    //证件号码
    private String identityNo;
    //劳动合同起始时间
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String contract_start_time;
    //劳动合同终止时间
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String contract_end_time;
    //离职时间
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String quit_date;
    //社保终止时间
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String charge_end_month;
    //社保基数
    private String base_pay;
    //用工形式
    private String employment_forms;
    //社保账号
    private String social_security_num;
    //离职原因
    private String quit_reason;
    //联系电话
    private String phone;

}
