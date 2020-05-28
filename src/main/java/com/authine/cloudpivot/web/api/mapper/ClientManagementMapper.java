package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ClientManagement;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/5/26 13:12
 */
public interface ClientManagementMapper {

    /**
     * 根据用户id获取一级客户名称、二级客户名称
     *
     * @param userId 用户id
     * @return 一级客户名称、二级客户名称
     * @author wangyong
     */
    ClientManagement getClientNameByUserId(String userId);

}
