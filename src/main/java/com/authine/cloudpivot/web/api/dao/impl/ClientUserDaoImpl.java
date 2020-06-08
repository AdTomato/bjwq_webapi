package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.ClientUserDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import jodd.util.BCrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.ClientUserDaoImpl
 * @Date 2020/1/3 17:12
 **/
@Repository
public class ClientUserDaoImpl implements ClientUserDao {
    @Override
    public void updateClientUserId(String id, String userId) throws Exception {
        String sql = "update id34a_client_management set  user_id = '" + userId + "'  where id = '" + id + "' ";
        ConnectionUtils.executeSql(sql);
    }
}
