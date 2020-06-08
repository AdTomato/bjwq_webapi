package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dao.ClientUserDao;
import com.authine.cloudpivot.web.api.entity.Client;
import com.authine.cloudpivot.web.api.mapper.ClientUserMapper;
import com.authine.cloudpivot.web.api.service.ClientUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.ClientUserServiceImpl
 * @Date 2020/1/3 17:10
 **/
@Service
public class ClientUserServiceImpl implements ClientUserService {

    @Resource
    private ClientUserDao clientUserDao;

    @Resource
    private ClientUserMapper clientUserMapper;

    /**
     * 方法说明：更新客户信息表对应的用户id
     * @Param id
     * @Param userId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/6 12:57
     */
    @Override
    public void updateClientUserId(String id, String userId) throws Exception {
        clientUserDao.updateClientUserId(id, userId);
    }
}
