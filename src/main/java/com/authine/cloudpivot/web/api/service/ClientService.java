package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.Unit;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:26
 * @Description: 客户service接口
 */
public interface ClientService {

    /**
     * 获取一级客户里面的业务员
     *
     * @param clientName 客户名称
     * @return 业务员
     */
    public String getFirstLevelClientSalesman(String clientName);

    /**
     * 获取二级客户里面的业务员
     *
     * @param clientName 客户名称
     * @return 业务员
     */
    public String getSecondLevelClientSalesman(String clientName);

    /**
     * 获取客户里面的业务员
     *
     * @param clientName 客户名称
     * @return 业务员
     */
    public Unit getClientSalesman(String clientName);

}
