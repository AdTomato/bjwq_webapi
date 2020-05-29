package com.authine.cloudpivot.web.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyong
 * @time 2020/5/29 1:30
 */
public class UnitUtils {

    /**
     * 根据传递进来的数据库存储的组织数据获取其中的id值
     *
     * @param str 组织数据
     * @return id值
     */
    public static final List<String> getUnitIds(String str) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmpty(str)) {
            JSONArray data = JSON.parseArray(str);
            for (Object datum : data) {
                JSONObject unit = (JSONObject)datum;
                result.add(unit.get("id") + "");
            }
        }
        return result;
    }

}
