package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

/**
 * 社保卡service接口
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.service.SocialSecurityCardService
 * @Date 2019/12/16 10:20
 **/
public interface SocialSecurityCardService {
    /**
     * 方法说明：办理社保卡，根据办理人员信息批量开启流程
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param fileName
     * @Param userId
     * @Param departmentId
     * @return void
     * @throws
     * @author liulei
     * @Date 2019/12/16 10:35
     */
    void socialSecurityCardProcess(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                   String fileName, String userId, String departmentId) throws Exception;

    /**
     * 方法说明：导入社保卡办理信息
     * @Param filePath 导入文件的名称
     * @Param completionRateId 完成率表主键id
     * @Param userId
     * @return void
     * @throws
     * @author liulei
     * @Date 2019/12/16 17:02
     */
    void importProcessFeedBack(WorkflowInstanceFacade workflowInstanceFacade, String fileName, String userId)
            throws Exception;

    /**
     * 方法说明：导入发卡进度信息
     * @Param filePath 导入文件的名称
     * @Param completionRateId 完成率表主键id
     * @Param userId
     * @return void
     * @throws
     * @author liulei
     * @Date 2019/12/18 16:46
     */
    void importIssueFeedBack(WorkflowInstanceFacade workflowInstanceFacade, String fileName, String userId)
            throws Exception;
}
