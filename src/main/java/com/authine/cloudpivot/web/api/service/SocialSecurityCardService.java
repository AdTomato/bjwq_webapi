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
     * 方法说明：导入社保卡/五险一金享受办理申请
     * @param fileName 文件名称
     * @param user 当前导入人
     * @param dept 当前部门
     * @param code 模型编码
     * @param workflowInstanceFacade 流程实例
     * @return java.util.List<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 9:43
     */
    void importAddData(String fileName, UserModel user, DepartmentModel dept, String code,
                                          WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：导入办理过程中数据
     * @param fileName 文件名称
     * @param user 当前导入人
     * @param dept 当前部门
     * @param type 导入类型
     * @param workflowInstanceFacade 流程实例
     * @return void
     * @author liulei
     * @Date 2020/3/17 11:21
     */
    void importUpdateData(String fileName, UserModel user, DepartmentModel dept, String type,
                               WorkflowInstanceFacade workflowInstanceFacade) throws Exception;
}
