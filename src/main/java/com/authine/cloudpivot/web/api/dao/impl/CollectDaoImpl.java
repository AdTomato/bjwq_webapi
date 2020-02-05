package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.CollectDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.CollectDaoImpl
 * @Date 2020/1/10 15:01
 **/
@Repository
public class CollectDaoImpl implements CollectDao {
    /**
     * 方法说明：根据id获取发起采集业务信息
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @Param id
     * @author liulei
     * @Date 2020/1/10 15:03
     */
    @Override
    public Map<String, String> getBizObjectById(String id) throws Exception {
        String sql = "select id, title, date_format(end_time, '%Y-%m-%d') end_time, client, remarks from " +
                "i080j_start_collect where id = '" + id + "'";//io57z_start_collect
        // 返回值
        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() > 0) {
            result.put("start_collect_id", list.get(0).get("id").toString());
            result.put("title", list.get(0).get("title").toString());
            result.put("end_time", list.get(0).get("end_time").toString());
            result.put("client", list.get(0).get("client").toString());
            result.put("remarks", list.get(0).get("remarks") == null ? "" : list.get(0).get("remarks").toString());
        }
        return result;
    }

    /**
     * 方法说明：生成对应的采集信息模板
     *
     * @return void
     * @throws
     * @Param id 发起采集信息模板
     * @Param refIds 附件的refId
     * @Param modelIds 新生成的模型id
     * @author liulei
     * @Date 2020/1/10 17:35
     */
    @Override
    public void addCollectTemplate(String id, String refIds, List<String> modelIds) throws Exception {
        String bizObjectIds = "";
        if (modelIds != null && modelIds.size() > 0) {
            for (int i = 0; i < modelIds.size(); i++) {
                bizObjectIds += modelIds.get(i) + ",";
            }
            bizObjectIds = bizObjectIds.substring(0, bizObjectIds.length() - 1);
        } else {
            return;
        }
        StringBuffer insertSql = new StringBuffer();
        insertSql.append(" INSERT INTO");
        insertSql.append("      h_biz_attachment (");
        insertSql.append("          id, bizObjectId, bizPropertyCode, createdTime, creater,");
        insertSql.append("          fileExtension, fileSize, mimeType, NAME, parentBizObjectId,");
        insertSql.append("          parentSchemaCode, refId, schemaCode )");
        insertSql.append(" SELECT");
        insertSql.append("          UUID(), a.id, b.bizPropertyCode, b.createdTime, b.creater,");
        insertSql.append("          b.fileExtension, b.fileSize,b.mimeType, b.NAME, b.parentBizObjectId,");
        insertSql.append("          b.parentSchemaCode, b.refId, 'submit_collect' ");
        insertSql.append(" FROM i080j_submit_collect a ");//io57z_submit_collect
        insertSql.append(" JOIN h_biz_attachment b ON a.start_collect_id = b.bizObjectId ");
        insertSql.append(" WHERE a.id in ('" + bizObjectIds.replaceAll(",", "','") + "')");
        insertSql.append("      AND b.refId in ('" + refIds.replaceAll(",", "','") + "')");

        ConnectionUtils.executeSql(insertSql.toString());
    }

    /**
     * 方法说明：基数采集回写附件到发起采集中去
     *
     * @return void
     * @throws
     * @Param id 基数采集业务id
     * @Param refIds 基数采集附件refId
     * @Param startCollectId 发起采集业务id
     * @author liulei
     * @Date 2020/1/13 15:36
     */
    @Override
    public void returnCollect(String id, String refIds, String startCollectId) throws Exception {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append(" INSERT INTO");
        insertSql.append("      h_biz_attachment (");
        insertSql.append("          id, bizObjectId, bizPropertyCode, createdTime, creater,");
        insertSql.append("          fileExtension, fileSize, mimeType, NAME, parentBizObjectId,");
        insertSql.append("          parentSchemaCode, refId, schemaCode )");
        insertSql.append(" SELECT");
        insertSql.append("          UUID(), '" + startCollectId + "', bizPropertyCode, createdTime, creater,");
        insertSql.append("          fileExtension, fileSize, mimeType, NAME, parentBizObjectId,");
        insertSql.append("          parentSchemaCode, refId, 'start_collect' ");
        insertSql.append(" FROM h_biz_attachment");
        insertSql.append(" WHERE bizObjectId = '" + id + "'");
        insertSql.append("      AND refId in ('" + refIds.replaceAll(",", "','") + "')");

        ConnectionUtils.executeSql(insertSql.toString());
    }

    /**
     * 方法说明：过节点申请修改截止时间
     *
     * @return void
     * @throws
     * @Param id 基数采集业务id
     * @Param endTime
     * @author liulei
     * @Date 2020/1/13 21:48
     */
    @Override
    public void updateEndTime(String id, String endTime) throws Exception {
        String sql = " update i080j_submit_collect set end_time = '" + endTime + "' where id ='" + id + "'";//io57z_submit_collect

        ConnectionUtils.executeSql(sql);
    }
}
