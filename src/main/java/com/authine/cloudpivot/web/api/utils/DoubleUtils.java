package com.authine.cloudpivot.web.api.utils;

import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @Author: wangyong
 * @Date: 2020-01-10 16:29
 * @Description: double工具类
 */
public class DoubleUtils {

    /**
     * @param d     : 需要四舍五入的数值
     * @param digit : 保留小数点后的位数
     * @return : java.lang.Double
     * @Author: wangyong
     * @Date: 2020/1/10 16:31
     * @Description: 四舍五入
     */
    public static Double doubleRound(Double d, Integer digit) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(digit);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.HALF_EVEN);
        if (null == d) {
            return 0D;
        } else {
            return Double.parseDouble(nf.format(d));
        }
    }

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

    /**
     * @Author: wangyong
     * @Date: 2020/4/2 13:37
     * @param d:
     * @return: java.lang.Double
     * @Description: 空转double
     */
    public static Double nullToDouble(Double d) {
        return d == null ? 0D : d;
    }

}
