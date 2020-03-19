package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;

import java.util.List;

/**
 * 社保卡service接口
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.service.SocialSecurityCardService
 * @Date 2019/12/16 10:20
 **/
public interface SocialSecurityCardService {
    /**
     * 方法说明：导入社保卡办理申请
     * @param fileName 文件名称
     * @param user 当前导入人
     * @param dept 当前部门
     * @param workflowInstanceFacade 流程实例
     * @return java.util.List<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 9:43
     */
    void importSocialSecurityCard(String fileName, UserModel user, DepartmentModel dept,
                                          WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：导入办理记录数据
     * @param fileName 文件名称
     * @param user 当前导入人
     * @param dept 当前部门
     * @param workflowInstanceFacade 流程实例
     * @return void
     * @author liulei
     * @Date 2020/3/17 11:21
     */
    void importProcessFeedback(String fileName, UserModel user, DepartmentModel dept,
                               WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：导入发卡记录数据
     * @param fileName 文件名称
     * @param user 当前导入人
     * @param dept 当前部门
     * @param workflowInstanceFacade 流程实例
     * @return void
     * @author liulei
     * @Date 2020/3/17 11:21
     */
    void importIssueFeedback(String fileName, UserModel user, DepartmentModel dept,
                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception;
}
