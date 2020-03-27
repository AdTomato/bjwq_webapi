package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/24 11:33
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartCollect extends BaseEntity {

    private String title;
    private Date endTime;
    private String client;
    private String remarks;

}
