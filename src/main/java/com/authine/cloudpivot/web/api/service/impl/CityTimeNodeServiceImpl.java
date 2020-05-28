package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.mapper.CityTimeNodeMapper;
import com.authine.cloudpivot.web.api.service.CityTimeNodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 城市时间节点
 *
 * @author wangyong
 * @time 2020/5/27 15:33
 */
@Service
public class CityTimeNodeServiceImpl implements CityTimeNodeService {

    @Resource
    CityTimeNodeMapper cityTimeNodeMapper;

    /**
     * 根据业务年月、城市获取城市时间节点
     *
     * @param businessYear 业务年月
     * @param city         城市
     * @return 城市时间节点
     * @author wangyong
     */
    @Override
    public CityTimeNode getCityTimeNodeByBusinessYearAndCity(String businessYear, String city) {
        return cityTimeNodeMapper.getCityTimeNodeByBusinessYearAndCity(businessYear, city);
    }
}
