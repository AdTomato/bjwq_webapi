package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.SocialSecurityCardDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.SocialSecurityCardDaoImpl
 * @Date 2019/12/18 15:08
 **/
@Repository
@Slf4j
public class SocialSecurityCardDaoImpl implements SocialSecurityCardDao {
    /**
     * 方法说明：根据单据号和当前节点code查询办理社保卡表单id,所在流程id,状态子表最大sortKey
     * @Param sequenceNo
     * @Param code
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @throws
     * @author liulei
     * @Date 2019/12/19 11:31
     */
    @Override
    public Map <String, Object> getSocialSecurityCardInfo(String sequenceNo, String userId, String code) throws Exception {

        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT ");
        sql.append("   ssc.id, ");
        sql.append("   work.id workItemId ");
        sql.append(" FROM ");
        sql.append("   ivdlf_social_security_card ssc ");// 办卡流程对应表单
        sql.append("   JOIN biz_workflow_instance instance ON instance.bizObjectId = ssc.id ");
        sql.append("   JOIN biz_workitem work ON work.instanceId = instance.id ");
        sql.append(" WHERE ");
        sql.append("   ssc.sequenceNo = '" + sequenceNo + "'");// 办理人身份证号
        sql.append("   AND work.sourceId = '" + code + "'");// 节点code
        sql.append("   AND work.participant = '" + userId + "'");

        List <Map <String, Object>> result = ConnectionUtils.executeSelectSql(sql.toString());

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public void executeSql(String sql) throws Exception {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        String[] sqlArr = sql.split(";");
        try {
            ConnectionUtils.executeSql(sqlArr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据库连接失败");
        }
    }
}
