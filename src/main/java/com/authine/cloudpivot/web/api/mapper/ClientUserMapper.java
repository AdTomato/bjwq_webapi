package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.Client;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.ClientUserMapper
 * @Date 2020/5/11 9:18
 **/
public interface ClientUserMapper {
    Client getClientByUserId(String userId);
}
