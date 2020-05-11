package com.authine.cloudpivot.web.api.entity;

// import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ContractImportInfo
 * @Author:lfh
 * @Date:2020/3/24 10:15
 * @Description: 合同导入信息
 **/
@Data
public class ContractImportInfo extends BaseEntity {

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
    //民族
    private String national;
    //出生日期
    private String birthday;
    //证件号码
    private String identityNo;
    //户籍性质 -员工档案
    private String household_register_nature;
    //学历
    private String education;
    //劳动合同起始时间
    private String contract_start_time;
    //劳动合同终止时间
    private String contract_end_time;
    //社保起做时间
    private String start_month;
    //社保基数
    private String base_pay;
    //用工形式
    private String employment_forms;
    //联系电话
    private String phone;
    //现住址
    private String current_address;

}