package com.authine.cloudpivot.web.api.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.SendMessageDao
 * @Date 2020/1/7 13:32
 **/
public interface SendMessageDao {

    /**
     * 方法说明：获取超时信息
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:37
     */
    List<Map<String, String>> getHaveTimeOutInfo() throws Exception;
}
