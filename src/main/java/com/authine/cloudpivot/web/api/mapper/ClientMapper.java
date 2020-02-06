package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.Unit;
import org.apache.catalina.LifecycleState;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:06
 * @Description: 客户mapper
 */
public interface ClientMapper {

    /**
     * 获取一级客户里面的业务员
     *
     * @param clientName 客户名称
     * @return 业务员
     */
    public List<String> getFirstLevelClientSalesman(String clientName);

    /**
     * 获取二级客户里面的业务员
     *
     * @param clientName 客户名称
     * @return 业务员
     */
    public List<String> getSecondLevelClientSalesman(String clientName);

}
