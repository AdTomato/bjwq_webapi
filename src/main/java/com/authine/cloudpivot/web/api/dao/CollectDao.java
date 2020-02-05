package com.authine.cloudpivot.web.api.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.CollectDao
 * @Date 2020/1/10 15:00
 **/
public interface CollectDao {
    /**
     * 方法说明：根据id获取发起采集业务信息
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @Param id
     * @author liulei
     * @Date 2020/1/10 15:03
     */
    Map<String, String> getBizObjectById(String id) throws Exception;

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
    void addCollectTemplate(String id, String refIds, List<String> modelIds) throws Exception;

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
    void returnCollect(String id, String refIds, String startCollectId) throws Exception;

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
    void updateEndTime(String id, String endTime) throws Exception;
}
