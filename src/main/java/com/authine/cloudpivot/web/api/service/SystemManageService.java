package com.authine.cloudpivot.web.api.service;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:59
 * @Description: 系统管理service接口
 */
public interface SystemManageService {

    /**
     * 根据城市名称获取时间节点
     * @param cityName
     * @return 时间节点
     */
    public Integer getTimeNodeByCity(String cityName);

}
