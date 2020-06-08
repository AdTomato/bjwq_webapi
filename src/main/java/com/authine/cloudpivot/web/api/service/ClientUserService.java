package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.Client;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.ClientUserService
 * @Date 2020/1/3 17:10
 **/
public interface ClientUserService {

    /**
     * 方法说明：更新客户信息表对应的用户id
     * @Param id
     * @Param userId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/6 12:57
     */
    void updateClientUserId(String id, String userId) throws Exception;
}
