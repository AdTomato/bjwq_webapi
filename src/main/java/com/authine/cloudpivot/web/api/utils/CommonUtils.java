package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.text.NumberFormat;
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

    /**
     * 方法说明：资金数据处理
     * @param value 处理前的值
     * @param rounding 舍入规则（四舍五入，单边见角进元取整，单边见角舍元取整）
     * @param precision 四舍五入时保留的精度（0,1,2 位小数）
     * @return java.lang.Double
     * @author liulei
     * @Date 2020/3/18 10:04
     */
    public static Double processingData(Double value, String rounding, String precision) {
        if(value == null) {
            return null;
        }
        Double returnValue = 0.0;
        if ("四舍五入".equals(rounding)) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            // 如果不需要四舍五入，可以使用RoundingMode.DOWN
            nf.setRoundingMode(RoundingMode.UP);
            if ("0".equals(precision)) {
                // 四舍五入取整
                returnValue = (double)Math.round(value);
            } else if ("1".equals(precision)) {
                // 保留两位小数
                nf.setMaximumFractionDigits(1);
                String companyStr = nf.format(value);
                returnValue = Double.parseDouble(companyStr);
            } else if ("2".equals(precision)) {
                nf.setMaximumFractionDigits(2);
                String companyStr = nf.format(value);
                returnValue = Double.parseDouble(companyStr);
            }
        } else if ("单边见角进元取整".equals(rounding)) {
            // 向上取整
            returnValue = Math.ceil(value);
        } else if ("单边见角舍元取整".equals(rounding)) {
            returnValue = Math.floor(value);
        }

        return returnValue;
    }
}
