package com.authine.cloudpivot.web.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 13:37
 * @Description: 读取全国派单导入文件
 */
@Component
public class NationalDeliveryReadExcelFile extends ReadExcelFile {


    @Override
    protected Object conversion(String key, Object value) throws ParseException {
        Object result = value;
        if (StringUtils.isEmpty(result + "")) {
            return null;
        }
        switch (key) {
            case "entry_procedures_num":
            case "social_insurance_amount":
            case "provident_fund_amount":
            case "supple_provident_fund_amount":
                // 转换成double
                result = Double.parseDouble(result + "");
                break;
            case "into_pending_date":
            case "task_list_update_date":
            case "entry_date":
            case "order_start_date":
            case "assignment_date":
            case "order_end_date":
            case "withdrawal_date":
            case "departure_date":
            case "change_take_effect_date":
            case "s_service_fee_start_date":
            case "s_service_fee_end_date":
            case "g_service_fee_start_date":
            case "g_service_fee_end_date":
                // 转换成时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.parse(result + "");
                break;
        }
        return result;
    }


}
