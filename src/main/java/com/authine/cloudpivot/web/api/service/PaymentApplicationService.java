package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;

import java.util.List;

/**
 * @author liulei
 * @Description 支付申请Service
 * @ClassName com.authine.cloudpivot.web.api.service.PaymentApplicationService
 * @Date 2020/3/20 14:57
 **/
public interface PaymentApplicationService {
    /**
     * 方法说明：合并支付申请数据
     * @param ids 支付申请表id
     * @param supplierId 供应商表单id
     * @param user 当前用户
     * @param dept 当前部门
     * @param workflowInstanceFacade
     * @param bizObjectFacade
     * @return void
     * @author liulei
     * @Date 2020/3/20 15:04
     */
    void mergeData(List<String> ids, String supplierId, UserModel user, DepartmentModel dept,
                   WorkflowInstanceFacade workflowInstanceFacade, BizObjectFacade bizObjectFacade) throws Exception;

    /**
     * 方法说明：
     * @param user
     * @param dept
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @return void
     * @author liulei
     * @Date 2020/3/23 14:13
     */
    void createPaymentApplicationData(UserModel user, DepartmentModel dept, BizObjectFacade bizObjectFacade,
                                      WorkflowInstanceFacade workflowInstanceFacade) throws Exception;
}
