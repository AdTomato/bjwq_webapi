package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.ClientMapper;
import com.authine.cloudpivot.web.api.service.ClientService;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:26
 * @Description: 客户service
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Resource
    ClientMapper clientMapper;

    @Autowired
    Unit unit;

    /**
     * @param clientName : 客户名称
     * @return : java.util.List<com.authine.cloudpivot.web.api.entity.Unit>
     * @Author: wangyong
     * @Date: 2020/2/6 9:27
     * @Description: 根据客户名称获取一级客户中的业务员
     */
    @Override
    public String getFirstLevelClientSalesman(String clientName) {
        List<String> result = clientMapper.getFirstLevelClientSalesman(clientName);
        return null == result ? null : result.get(0);
    }

    /**
     * @param clientName : 客户名称
     * @return : java.util.List<com.authine.cloudpivot.web.api.entity.Unit>
     * @Author: wangyong
     * @Date: 2020/2/6 9:34
     * @Description: 根据客户名称获取二级客户中的业务员
     */
    @Override
    public String getSecondLevelClientSalesman(String clientName) {
        List<String> result = clientMapper.getSecondLevelClientSalesman(clientName);
        return null == result ? null : result.get(0);
    }

    /**
     * @param clientName : 客户名称
     * @return : com.authine.cloudpivot.web.api.entity.Unit
     * @Author: wangyong
     * @Date: 2020/2/6 9:42
     * @Description: 根据客户名称获取业务员
     */
    @Transactional
    @Override
    public Unit getClientSalesman(String clientName) {
        String salesman = getFirstLevelClientSalesman(clientName);
        if (null == salesman) {
            // 不是一级客户
            salesman = getSecondLevelClientSalesman(clientName);
            if (null == salesman) {
                // 既不是一级客户也不是二级客户返回null
                return null;
            } else {
                // 是二级客户
                setUnitData(salesman);
            }
        } else {
            // 是一级客户
            setUnitData(salesman);
        }
        return unit;
    }

    private void setUnitData(String salesman) {
        JSONArray parseData = (JSONArray) JSON.parse(salesman);
        JSONObject data = (JSONObject) parseData.get(0);
        unit.setId(data.get("id") + "");
        unit.setType(data.get("type") + "");
    }
}
