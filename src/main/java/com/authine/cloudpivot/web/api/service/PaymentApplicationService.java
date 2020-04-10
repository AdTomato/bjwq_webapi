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
     * 方法说明：创建支付明细账单和支付申请数据
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

    /**
     * 方法说明：导入一次性费用数据
     * @param fileName 文件名称
     * @param user 当前用户
     * @param dept 当前部门
     * @return void
     * @author liulei
     * @Date 2020/3/30 16:16
     */
    @Deprecated
    void importOneTimeFee(String fileName, UserModel user, DepartmentModel dept) throws Exception;

    /**
     * 方法说明：省内导入临时收费数据
     * @param fileName 文件名称
     * @param user 当前用户
     * @param dept 当前部门
     * @return void
     * @author liulei
     * @Date 2020/4/8 11:04
     */
    void importTemporaryCharge(String fileName, UserModel user, DepartmentModel dept) throws Exception;

    /**
     * 方法说明：导入支付明细，生成支付申请
     * @param fileName 文件名称
     * @param user 当前用户
     * @param dept 当前部门
     * @param systemType 系统类型（互为系统：mutual_system; 全国系统：national_system）
     * @param workflowInstanceFacade 流程实例接口
     * @return void
     * @author liulei
     * @Date 2020/3/30 16:17
     */
    void importPaymentDetails(String fileName, UserModel user, DepartmentModel dept, String systemType,
                              WorkflowInstanceFacade workflowInstanceFacade) throws Exception;
}
