package com.authine.cloudpivot.web.api.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 21:35
 * @Description: 地区工具类
 */
public class AreaUtils {
    /**
     * @param cityName: 城市名称
     * @Author: wangyong
     * @Date: 2020/3/18 21:35
     * @return: boolean
     * @Description: 判断该城市是否是安徽省内城市
     */
    public static boolean isAnhuiCity(String cityName) {
        if (cityName.contains("巢湖") || cityName.contains("合肥") || cityName.contains("芜湖") || cityName.contains("蚌埠") || cityName.contains("淮南") || cityName.contains("马鞍山") || cityName.contains("铜陵") || cityName.contains("安庆") || cityName.contains("黄山") || cityName.contains("滁州") || cityName.contains("阜阳") || cityName.contains("宿州") || cityName.contains("六安") || cityName.contains("亳州") || cityName.contains("池州") || cityName.contains("宣城")) {
            return true;
        }
        return false;
    }

}
