package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.CollectService
 * @Date 2020/1/10 14:01
 **/
public interface CollectService {

    /**
     * 方法说明：激活提交采集流程
     *
     * @return void
     * @throws Exception
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param id 发起采集流程业务id
     * @Param userId
     * @Param departmentId
     * @author liulei
     * @Date 2020/1/10 14:08
     */
    void startCollect(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                      OrganizationFacade organizationFacade, String id, String refIds, String userId,
                      String departmentId) throws Exception;

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
