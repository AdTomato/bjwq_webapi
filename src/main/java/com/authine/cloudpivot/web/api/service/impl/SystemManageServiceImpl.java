package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.SystemManageMapper;
import com.authine.cloudpivot.web.api.service.SystemManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 16:00
 * @Description:
 */
@Service
public class SystemManageServiceImpl implements SystemManageService {

    @Resource
    SystemManageMapper systemManageMapper;

    /**
     * @param cityName : 城市名称
     * @return : java.lang.Integer
     * @Author: wangyong
     * @Date: 2020/2/7 16:01
     * @Description: 根据城市名称获取时间节点
     */
    @Override
    public Integer getTimeNodeByCity(String cityName) {
        return systemManageMapper.getTimeNodeByCity(cityName);
    }
}
