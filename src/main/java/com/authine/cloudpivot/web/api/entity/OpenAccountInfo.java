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
    private Integer serialNumber;

    /**
     * 个人姓名
     */
    private String employee_name;



    /**
     * 证件号码
     */
    private String identityNo;




    /**
     * 个人缴存基数
     */
    private Double personalSaveBase;

    /**
     * 个人缴存比例
     */
    private Double personalSaveProportion;


    /**
     * 起始月
      */
    private String start_month;

}
