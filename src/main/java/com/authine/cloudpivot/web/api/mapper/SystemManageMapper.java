package com.authine.cloudpivot.web.api.mapper;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:09
 * @Description: 系统管理mapper
 */
public interface SystemManageMapper {

    /**
     * 根据城市名称获取时间节点
     * @param cityName
     * @return 时间节点
     */
    public int getTimeNodeByCity(String cityName);

}
