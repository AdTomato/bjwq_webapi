package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
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
     * 方法说明：修改员工订单状态
     * @param ids 员工订单id,多个id用“,”隔开
     * @param field 修改的字段
     * @param status 修改后的状态
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 9:40
     */
    void updateEmployeeOrderFormStatus(String ids, String field, String status) throws Exception;

    /**
     * 方法说明：社保，公积金运行提交时重新生成员工订单数据
     * @param id 社保申报，公积金申报表单id
     * @param employeeOrderFormId 员工订单表单id
     * @param type 类型
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 16:02
     */
    void createNewEmployeeOrderForm(BizObjectFacade bizObjectFacade, String id, String employeeOrderFormId,
                                    String type) throws Exception;

    /**
     * 方法说明：根据地区和福利办理方获取运行负责人
     * @param city 地区
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/26 10:00
     */
    @Deprecated
    String getOperateLeaderByCity(String city) throws Exception;

    /**
     * 方法说明：获取员工订单数据
     * @param socialSecurityCity 社保福利地
     * @param socialSecurityStartTime 社保开始时间
     * @param socialSecurityBase 社保基数
     * @param providentFundCity 公积金福利地
     * @param providentFundStartTime 公积金开始时间
     * @param providentFundBase 公积金基数
     * @param companyRatio 公司比例
     * @param employeeRatio 员工比例
     * @param sMonth 社保补缴月份
     * @param gMonth 公积金补缴月份
     * @param ccps 个性化设置数据
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/2/26 14:18
     */
    EmployeeOrderForm getEmployeeOrderForm(String socialSecurityCity, Date socialSecurityStartTime,
                                           Double socialSecurityBase, String providentFundCity,
                                           Date providentFundStartTime, Double providentFundBase,
                                           Double companyRatio, Double employeeRatio, int sMonth, int gMonth,
                                           Ccps ccps) throws Exception;

    /**
     * 方法说明：创建员工订单
     * @param bizObjectFacade
     * @param employeeOrderForm  员工订单
     * @param userId  客服
     * @return java.lang.String
     * @author liulei
     * @Date 2020/3/11 16:22
     */
    String createEmployeeOrderForm(BizObjectFacade bizObjectFacade, EmployeeOrderForm employeeOrderForm,
                                   String userId, String owner, String ownerDeptId, String ownerDeptQueryCode) throws Exception;

    /**
     * 方法说明：员工档案数据修改审核提交
     * @param employeeFiles 员工档案修改数据
     * @return void
     * @author liulei
     * @Date 2020/2/27 16:48
     */
    void employeeFilesUpdateSubmit(EmployeeFiles employeeFiles) throws Exception;

    /**
     * 方法说明：批量提交
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param userId 当前用户
     * @param ids 表单ids
     * @param code 表单code
     * @return java.lang.String
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    void batchSubmit(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                     String userId, String ids, String code) throws Exception;

    /**
     * 方法说明：批量驳回
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param userId 当前用户
     * @param ids 表单ids
     * @param code 表单code
     * @return void
     * @author liulei
     * @Date 2020/3/12 14:42
     */
    void batchReject(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade, String userId,
                     String ids, String code) throws Exception;

    /**
     * 方法说明：修改员工钉钉状态为预点
     * @param ids 表单id
     * @param field 修改字段名称
     * @return void
     * @author liulei
     * @Date 2020/3/17 14:17
     */
    void updateStatusToPrePoint(String ids, String field) throws Exception;

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
     * 方法说明：根据一级，二级客户名称查询客户的个性化设置
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @return com.authine.cloudpivot.web.api.entity.Ccps
     * @author liulei
     * @Date 2020/4/17 15:55
     */
    Ccps getCcps(String firstLevelClientName, String secondLevelClientName) throws Exception;

    /**
     * 方法说明：获取城市的时间节点设置
     * @param socialSecurityCity
     * @return int
     * @author liulei
     * @Date 2020/4/17 15:55
     */
    int getTimeNode(String socialSecurityCity) throws Exception;

    /**
     * 方法说明：增员客户获取入职通知
     * @param addEmployee
     * @param sMonth
     * @param gMonth
     * @param collectionRule
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/17 15:54
     */
    BizObjectModel getEntryNotice(AddEmployee addEmployee, int sMonth, int gMonth, CollectionRule collectionRule) throws Exception;

    /**
     * 方法说明：增员上海获取入职通知
     * @param shAddEmployee
     * @param sMonth
     * @param gMonth
     * @param collectionRule
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/17 15:54
     */
    BizObjectModel getEntryNotice(ShAddEmployee shAddEmployee, int sMonth, int gMonth, CollectionRule collectionRule) throws Exception;

    /**
     * 方法说明：增员全国获取入职通知
     * @param nationwideDispatch
     * @param sMonth
     * @param gMonth
     * @param collectionRule
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/17 15:54
     */
    BizObjectModel getEntryNotice(NationwideDispatch nationwideDispatch, int sMonth, int gMonth, CollectionRule collectionRule) throws Exception;

    /**
     * 方法说明：根据福利地，福利办理方，二级客户名称查询运行负责人
     * @param city
     * @param welfareHandler
     * @param secondLevelClientName
     * @return com.authine.cloudpivot.web.api.entity.OperateLeader
     * @author liulei
     * @Date 2020/4/17 15:52
     */
    OperateLeader getOperateLeader(String city, String welfareHandler, String secondLevelClientName) throws Exception;

    /**
     * 方法说明：创建子表数据
     * @param id 父id
     * @param socialSecurityDetail 子表的数据
     * @param code 子表的编码
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:52
     */
    void createSocialSecurityFundDetail(String id, List<Map<String, String>> socialSecurityDetail, String code) throws Exception;

    /**
     * 方法说明：增员变更时更新员工档案数据
     * @param employeeFiles 员工档案
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:51
     */
    void updateEmployeeFiles(EmployeeFiles employeeFiles) throws Exception;

    /**
     * 方法说明：订单改为历史订单
     * @param id 订单id
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:51
     */
    void updateEmployeeOrderFormIsHistory(String id) throws Exception;

    /**
     * 方法说明：更新申报的员工订单id
     * @param oldId 原订单id
     * @param newId 新订单id
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:50
     */
    void updateDeclareEmployeeOrderFormId(String oldId, String newId) throws Exception;

    /**
     * 方法说明：增员变更时更新社保申报
     * @param socialSecurityDeclare 社保申报
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:49
     */
    void updateSocialSecurityDeclare(SocialSecurityDeclare socialSecurityDeclare) throws Exception;

    /**
     * 方法说明：增员变更时更新公积金申报申报
     * @param providentFundDeclare
     * @return void
     * @author liulei
     * @Date 2020/4/17 15:49
     */
    void updateProvidentFundDeclare(ProvidentFundDeclare providentFundDeclare)throws Exception;

    /**
     * 方法说明：增员客户变更修改增员客户数据
     * @param id 增员客户变更id
     * @param addEmployeeId 增员客户id
     * @return void
     * @author liulei
     * @Date 2020/4/17 16:21
     */
    void updateAddEmployee(String id, String addEmployeeId) throws Exception;

    /**
     * 方法说明：增员上海变更修改增员客户数据
     * @param id 增员上海变更id
     * @param shAddEmployeeId 增员上海id
     * @return void
     * @author liulei
     * @Date 2020/4/17 16:21
     */
    void updateShAddEmployee(String id, String shAddEmployeeId) throws Exception;
    /**
     * 方法说明：增员全国变更修改增员客户数据
     * @param id 增员全国变更id
     * @param nationwideDispatchId 增员全国id
     * @return void
     * @author liulei
     * @Date 2020/4/17 16:21
     */
    void updateQgAddEmployee(String id, String nationwideDispatchId, String type)throws Exception;

    /**
     * 方法说明：获取订单中的社保公积金数据
     * @param id 订单id
     * @param productType 产品类型
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail
     * @author liulei
     * @Date 2020/4/20 10:54
     */
    SocialSecurityFundDetail getSocialSecurityFundDetail(String id, String productType) throws Exception;

    /**
     * 方法说明：减员更新社保停缴数据
     * @param securityClose
     * @return void
     * @author liulei
     * @Date 2020/4/20 14:01
     */
    void updateSocialSecurityClose(SocialSecurityClose securityClose) throws Exception;

    /**
     * 方法说明：减员更新公积金停缴数据
     * @param fundClose
     * @return void
     * @author liulei
     * @Date 2020/4/20 14:01
     */
    void updateProvidentFundClose(ProvidentFundClose fundClose) throws Exception;

    /**
     * 方法说明：减员变更时更新减员数据
     * @param id 减员变更id
     * @param deleteEmployeeId 对应的减员表id
     * @return void
     * @author liulei
     * @Date 2020/4/20 14:24
     */
    void updateDeleteEmployee(String id, String deleteEmployeeId) throws Exception;

    /**
     * 方法说明：减员变更时更新减员数据
     * @param id 减员变更id
     * @param shDeleteEmployeeId 对应的减员表id
     * @return void
     * @author liulei
     * @Date 2020/4/20 14:24
     */
    void updateShDeleteEmployee(String id, String shDeleteEmployeeId) throws Exception;
}
