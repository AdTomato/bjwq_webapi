package com.authine.cloudpivot.web.api.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.web.api.dao.OperatorDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 运行人员接口实现类
 *
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.OperatorDaoImpl
 * @Date 2019/12/18 14:13
 **/
@Repository
@Slf4j
public class OperatorDaoImpl implements OperatorDao {

    @Override
    public Map<String, String> getOperatorByArea(String area) throws Exception {
        // 返回值
        Map<String, String> result = new HashMap<>();
        // 查询地址
        Map<String, String> areaMap = new HashMap<>();
        areaMap = getAreaMapByArea(area);
        // 省级名称
        String provinceName = areaMap.get("provinceName");
        // 市级名称
        String cityName = areaMap.get("cityName");

        String sql = "select id, operator from id34a_operate_area  where area like '%" + provinceName + "%' ";
        if (StringUtils.isNotBlank(cityName)) {
            sql += " and area like '%" + cityName + "%'";
        }
        List<Map<String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() > 0) {
            result.put("id", list.get(0).get("id").toString());
            result.put("operator", list.get(0).get("operator").toString());
        }

        return result;
    }

    /**
     * 方法说明：根据地址返回对应的省，市
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @Param area
     * @author liulei
     * @Date 2019/12/24 9:28
     */
    private Map<String, String> getAreaMapByArea(String area) {
        Map<String, String> areaMap = new HashMap<>();
        // 如果是xx省xx
        if (area.indexOf("省") >= 0) {
            int i = area.indexOf("省");
            areaMap.put("provinceName", area.substring(0, i + 1));
            areaMap.put("cityName", area.substring(i + 1, area.length()));
        } else if (area.indexOf("自治区") >= 0) {
            int i = area.indexOf("区");
            areaMap.put("provinceName", area.substring(0, i + 1));
            areaMap.put("cityName", area.substring(i + 1, area.length()));
        } else if (area.indexOf("香港") >= 0) {
            areaMap.put("provinceName", "香港特别行政区");
        } else if (area.indexOf("澳门") >= 0) {
            areaMap.put("provinceName", "澳门特别行政区");
        } else if (area.indexOf("北京") >= 0) {
            areaMap.put("provinceName", "北京市");
        } else if (area.indexOf("上海") >= 0) {
            areaMap.put("provinceName", "上海市");
        } else if (area.indexOf("重庆") >= 0) {
            int i = area.indexOf("市");
            areaMap.put("provinceName", "重庆市");
            if (i > 0) {
                areaMap.put("cityName", area.substring(i + 1, area.length()));
            }
        } else if (area.indexOf("天津") >= 0) {
            areaMap.put("provinceName", "天津市");
        }
        return areaMap;
    }

}
