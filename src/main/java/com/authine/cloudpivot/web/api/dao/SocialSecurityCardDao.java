package com.authine.cloudpivot.web.api.dao;

import java.util.Map;

/**
 * 社保卡Dao接口
 *
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.dao.SocialSecurityCardDao
 * @Date 2019/12/18 15:07
 **/
public interface SocialSecurityCardDao {
    /**
     * 方法说明：根据单据号和当前节点code查询办理社保卡表单id,所在流程id,状态子表最大sortKey
     *
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     * @Param sequenceNo
     * @Param code
     * @author liulei
     * @Date 2019/12/19 11:31
     */
    Map<String, Object> getSocialSecurityCardInfo(String sequenceNo, String userId, String code) throws Exception;

    /**
     * 方法说明：处理sql
     *
     * @return void
     * @throws
     * @Param sql
     * @author liulei
     * @Date 2019/12/19 11:32
     */
    void executeSql(String sql) throws Exception;
}
