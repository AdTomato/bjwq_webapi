package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import lombok.extern.slf4j.Slf4j;

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
     * 方法说明：启动流程节点
     *
     * @return void
     * @throws
     * @Param workflowInstanceFacade
     * @Param userId 当前用户id
     * @Param workflowCode 流程编码
     * @Param modelIds 业务对象id List
     * @Param finishStart 是否结束发起节点
     * @author liulei
     * @Date 2019/12/17 19:56
     */
    public static void startWorkflowInstance(WorkflowInstanceFacade workflowInstanceFacade, String departmentId,
                                             String userId, String workflowCode, List<String> modelIds,
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
     *
     * @return void
     * @throws
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemSuccessIds 待办任务id
     * @Param agree 是否同意
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void submitWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List<String> workItemSuccessIds, boolean agree) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemSuccessIds.size(); i++) {
            boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemSuccessIds.get(i), agree);
            log.info(workItemSuccessIds.get(i) + "提交：" + flag);
        }
    }

    /**
     * 方法说明：驳回流程
     *
     * @return void
     * @throws
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemErrorIds 待办任务id
     * @Param rejectToActivityCode 驳回到指定的节点
     * @Param submitToReject 是否可以直接提交到驳回的节点
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void rejectWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List<String> workItemErrorIds, String rejectToActivityCode,
                                      boolean submitToReject) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemErrorIds.size(); i++) {
            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemErrorIds.get(i),
                    rejectToActivityCode, submitToReject);
            log.info(workItemErrorIds.get(i) + "驳回：" + flag);
        }
    }
}
