package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;

/**
 * 城市时间节点
 *
 * @author wangyong
 * @time 2020/5/27 15:28
 */
public interface CityTimeNodeMapper {

    /**
     * 根据业务年月、城市获取城市时间节点
     *
     * @param businessYear 业务年月
     * @param city         城市
     * @return 城市时间节点
     * @author wangyong
     */
    CityTimeNode getCityTimeNodeByBusinessYearAndCity(String businessYear, String city);

}
