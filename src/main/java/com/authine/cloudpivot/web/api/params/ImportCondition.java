package com.authine.cloudpivot.web.api.params;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName ImportCondition
 * @Author:lfh
 * @Date:2020/5/8 13:58
 * @Description: 导入筛选条件
 **/
@Data
public class ImportCondition {
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //福利办理方
    private String welfare_handler;
    //福利地
    private String city;
    //二级库户名称
    private String second_level_client_name;

    //起缴开始时间时间
    private Date beginPaymentStartTime;

    //起缴结束时间
    private Date beginPaymentEndTime;

    //停缴开始时间
    private Date stopPaymentStartTime;
    //停缴日期
    private Date stopPaymentEndTime;

}
