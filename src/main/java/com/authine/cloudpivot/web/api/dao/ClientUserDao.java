package com.authine.cloudpivot.web.api.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.ClientUserDao
 * @Date 2020/1/3 17:12
 **/
public interface ClientUserDao {
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
