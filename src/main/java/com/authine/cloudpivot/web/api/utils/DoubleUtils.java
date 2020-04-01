package com.authine.cloudpivot.web.api.utils;

import java.math.BigDecimal;

/**
 * @Author:wangyong
 * @Date:2020/4/1 14:10
 * @Description: Double工具类
 */
public class DoubleUtils {

    /**
     * @param d1:
     * @param d2:
     * @Author: wangyong
     * @Date: 2020/4/1 14:13
     * @return: java.lang.Double
     * @Description: 比较两个Double的差值
     */
    public static Double getDifference(Double d1, Double d2) {
        Double result = 0D;
        if (d1 == null) {
            d1 = 0D;
        }
        if (d2 == null) {
            d2 = 0D;
        }
        result = d1 - d2;
        return result;
    }

}
