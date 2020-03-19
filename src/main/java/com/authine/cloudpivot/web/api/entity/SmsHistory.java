package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/15 23:35
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsHistory extends BaseEntity {

    private String phone;
    private Date time;
    private String code;

}
