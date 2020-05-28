package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.ClientManagement;
import com.authine.cloudpivot.web.api.mapper.ClientManagementMapper;
import com.authine.cloudpivot.web.api.service.ClientManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangyong
 * @time 2020/5/27 13:20
 */
@Service
public class ClientManagementServiceImpl implements ClientManagementService {

    @Resource
    ClientManagementMapper clientManagementMapper;

    /**
     * 根据用户id获取一级客户名称、二级客户名称
     *
     * @param userId 用户id
     * @return 一级客户名称、二级客户名称
     * @author wangyong
     */
    @Override
    public ClientManagement getClientNameByUserId(String userId) {
        return clientManagementMapper.getClientNameByUserId(userId);
    }
}
