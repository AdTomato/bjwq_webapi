package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.ClientManagement;

/**
 * @author wangyong
 * @time 2020/5/26 13:15
 */
public interface ClientManagementService {

    /**
     * 根据用户id获取一级客户名称、二级客户名称
     *
     * @param userId 用户id
     * @return 一级客户名称、二级客户名称
     * @author wangyong
     */
    ClientManagement getClientNameByUserId(String userId);

}
