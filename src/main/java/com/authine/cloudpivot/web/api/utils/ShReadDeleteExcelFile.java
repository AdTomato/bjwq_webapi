package com.authine.cloudpivot.web.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:11
 * @Description: 读取减员
 */
@Component
public class ShReadDeleteExcelFile extends ReadExcelFile {

    public static final String TABLE_NAME = "";

    public static final String WORKFLOW_CODE = "";

    @Override
    protected Object conversion(String key, Object value) throws ParseException {
        Object result = value;
        if (StringUtils.isEmpty(value + "")) {
            return result;
        }
        switch (key) {
            case "OS_initiated_departure_time":
            case "departure_time":
            case "charge_end_time":
                // 转换成时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.parse(result + "");
                break;
        }
        return result;
    }


}
