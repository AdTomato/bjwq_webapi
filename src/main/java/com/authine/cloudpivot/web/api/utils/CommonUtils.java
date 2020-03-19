package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liulei
 * @Description 公共方法类
 * @ClassName com.authine.cloudpivot.web.api.utils.CommonUtils
 * @Date 2020/1/10 15:54
 **/
@Slf4j
public class CommonUtils {

    /**
     * 方法说明：批量生成业务数据
     * @Param bizObjectFacade
     * @Param userId 当前用户id
     * @Param models 运行时业务数据对象
     * @Param queryField 查询字段
     * @return java.util.List<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/4 19:11
     */
    public static List <String> addBizObjects(BizObjectFacade bizObjectFacade, String userId,
                                              List <BizObjectModel> models, String queryField) throws Exception {
        List <String> modelIds = new ArrayList <>();
        if (models != null && models.size() > 0) {
            modelIds = bizObjectFacade.addBizObjects(userId, models, queryField);
        }
        return modelIds;
    }

    /**
     * 方法说明：启动流程节点
     * @Param workflowInstanceFacade
     * @Param userId 当前用户id
     * @Param workflowCode 流程编码
     * @Param modelIds 业务对象id List
     * @Param finishStart 是否结束发起节点
     * @return void
     * @throws
     * @author liulei
     * @Date 2019/12/17 19:56
     */
    public static void startWorkflowInstance(WorkflowInstanceFacade workflowInstanceFacade, String departmentId,
                                             String userId, String workflowCode, List <String> modelIds,
                                             boolean finishStart) throws Exception {
        if (modelIds != null && modelIds.size() > 0) {
            for (int i = 0; i < modelIds.size(); i++) {
                // 启动流程，返回流程实例id
                String workflowId = workflowInstanceFacade.startWorkflowInstance(departmentId, userId, workflowCode,
                        modelIds.get(i), finishStart);
                log.info("启动流程:" + workflowId);
            }
        }
    }

    /**
     * 方法说明：提交流程
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemSuccessIds 待办任务id
     * @Param agree 是否同意
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void submitWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List <String> workItemSuccessIds, boolean agree) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemSuccessIds.size(); i++) {
            boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemSuccessIds.get(i), agree);
            log.info(workItemSuccessIds.get(i) + "提交：" + flag);
        }
    }

    /**
     * 方法说明：驳回流程
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemErrorIds 待办任务id
     * @Param rejectToActivityCode 驳回到指定的节点
     * @Param submitToReject 是否可以直接提交到驳回的节点
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void rejectWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List <String> workItemErrorIds, String rejectToActivityCode,
                                      boolean submitToReject) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemErrorIds.size(); i++) {
            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemErrorIds.get(i),
                    rejectToActivityCode, submitToReject);
            log.info(workItemErrorIds.get(i) + "驳回：" + flag);
        }
    }
}
