package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.EmployeeMaintainService
 * @Date 2020/2/4 17:35
 **/
public interface EmployeeMaintainService {

    /**
     * 方法说明：增员导入
     * @param workflowInstanceFacade
     * @param fileName
     * @param user
     * @param dept
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/4 17:38
     */
    void addEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                     DepartmentModel dept) throws Exception;

    /**
     * 方法说明：减员导入
     * @param workflowInstanceFacade
     * @param fileName
     * @param user
     * @param dept
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/4 19:17
     */
    void deleteEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                        DepartmentModel dept) throws Exception;

    /**
     * 方法说明：批量提交申报，停缴
     * @param workflowInstanceFacade 流程实例接口
     * @param userId 当前用户
     * @param idList 表单idList
     * @param code 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；
     *             社保停缴：social_security_close；公积金停缴：provident_fund_close。）
     * @param billYear 账单年月（社保申报，公积金申报提交时填写的值）
     * @return java.lang.String
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    void batchSubmit(WorkflowInstanceFacade workflowInstanceFacade, String userId, List <String> idList, String code,
                     String billYear) throws Exception;

    /**
     * 方法说明：批量驳回申报，停缴接口
     * @param workflowInstanceFacade 流程实例接口
     * @param userId 当前用户
     * @param idList 表单idList
     * @param code 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；
     *             社保停缴：social_security_close；公积金停缴：provident_fund_close。）
     * @param returnReasonAlready 驳回原因
     * @return void
     * @author liulei
     * @Date 2020/3/12 14:42
     */
    void batchReject(WorkflowInstanceFacade workflowInstanceFacade, String userId, List <String> idList, String code,
                     String returnReasonAlready) throws Exception;

    /**
     * 方法说明：获取增减员的任务id
     * @param ids
     * @param tableName
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author liulei
     * @Date 2020/3/26 10:24
     */
    List<Map<String, Object>> getAddOrDelWorkItemId(String ids, String tableName) throws Exception;

    /**
     * 方法说明：申报驳回导入
     * @param fileName 文件名称
     * @param code 表单code
     * @param userId 当前用户
     * @param workflowInstanceFacade 流程实例接口
     * @return void
     * @author liulei
     * @Date 2020/3/27 10:07
     */
    void rejectImport(String fileName, String code, String userId, WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：申报/停缴流程节点变化，更新订单，增员对应状态
     * @param ids 表单id
     * @param schemaCode 表单编码（增员客户：add_employee；减员客户：delete_employee；社保申报：social_security_declare；
     *                             公积金申报：provident_fund_declare；社保停缴：social_security_close；
     *                             公积金停缴：provident_fund_close；）
     * @param status 状态 （增员客户、减员客户：取派；
     *                      社保申报、公积金申报：在办、预点、在缴、驳回；
     *                      社保停缴、公积金停缴：在办、停缴、驳回）
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/22 9:49
     */
    void updateStatus(String ids, String schemaCode, String status, String userId, BizObjectFacade bizObjectFacade,
                      WorkflowInstanceFacade workflowInstanceFacade) throws Exception;
}
