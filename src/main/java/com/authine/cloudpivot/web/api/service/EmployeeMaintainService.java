package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
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
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param fileName
     * @param userId
     * @param departmentId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/4 17:38
     */
    void addEmployee(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade, String fileName,
                     String userId, String departmentId) throws Exception;

    /**
     * 方法说明：减员导入
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param fileName
     * @param userId
     * @param departmentId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/4 19:17
     */
    void deleteEmployee(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                        String fileName, String userId, String departmentId) throws Exception;

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
     * 方法说明：获取委托单位
     * @param clientName 客户名称
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/26 9:01
     */
    String getDispatchUnitByClientName(String clientName) throws Exception;

    /**
     * 方法说明：根据客户名称和地区获取福利办理方
     * @param clientName 客户名称
     * @param city 地区
     * @return com.authine.cloudpivot.web.api.entity.AppointmentSheet
     * @author liulei
     * @Date 2020/2/26 9:07
     */
    AppointmentSheet getWelfareHandlerByClientNameAndCity(String clientName, String city) throws Exception;

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
     * 方法说明：根据地区和福利办理方获取社保，公积金经办人
     * @param city
     * @param welfareHandler
     * @return com.authine.cloudpivot.web.api.entity.OperateLeader
     * @author liulei
     * @Date 2020/3/18 16:30
     */
    OperateLeader getOperateLeaderByCityAndWelfareHandler(String city, String welfareHandler) throws Exception;

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
     * @param personal 个性化设置数据
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/2/26 14:18
     */
    EmployeeOrderForm getEmployeeOrderForm(String socialSecurityCity, Date socialSecurityStartTime,
                                           Double socialSecurityBase, String providentFundCity,
                                           Date providentFundStartTime, Double providentFundBase,
                                           Double companyRatio, Double employeeRatio, Map <String, String> personal) throws Exception;

    /**
     * 方法说明：获取当前时间节点,如果是个性化客户，获取个性化设置
     * @param clientName 客户名称
     * @param city 地区
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author liulei
     * @Date 2020/2/26 11:06
     */
    Map<String, String> getTimeNode(String clientName, String city) throws Exception;

    /**
     * 方法说明：获取入职通知清单数据
     * @param monthDifference 补缴月份
     * @param city 地区
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author liulei
     * @Date 2020/2/26 11:18
     */
    Map<String, String> getEntryNoticeInfo(int monthDifference, String city) throws Exception;

    /**
     * 方法说明：创建员工档案
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param employeeFiles 员工档案实体
     * @param customerService 客服
     * @return java.lang.String
     * @author liulei
     * @Date 2020/3/11 16:16
     */
    String createEmployeeInfoModel(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                   EmployeeFiles employeeFiles, UserModel customerService) throws Exception;

    /**
     * 方法说明：创建入职通知
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param entryNotice 入职通知实体
     * @param customerService 客服
     * @return void
     * @author liulei
     * @Date 2020/3/11 16:19
     */
    void createEntryNotice(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                           EntryNotice entryNotice, UserModel customerService) throws Exception;

    /**
     * 方法说明：创建员工订单
     * @param bizObjectFacade
     * @param employeeOrderForm  员工订单
     * @param customerService  客服
     * @return java.lang.String
     * @author liulei
     * @Date 2020/3/11 16:22
     */
    String createEmployeeOrderForm(BizObjectFacade bizObjectFacade,
                                   EmployeeOrderForm employeeOrderForm, UserModel customerService) throws Exception;

    /**
     * 方法说明：创建社保申报
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param socialSecurityDeclare 社保申报
     * @param customerService 客服
     * @return void
     * @author liulei
     * @Date 2020/3/11 16:35
     */
    void createSocialSecurityDeclare(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                     SocialSecurityDeclare socialSecurityDeclare, UserModel customerService) throws Exception;

    /**
     * 方法说明：创建公积金申报
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param providentFundDeclare 公积金申报
     * @param customerService 客服
     * @return void
     * @author liulei
     * @Date 2020/3/11 16:36
     */
    void createProvidentFundDeclare(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                    ProvidentFundDeclare providentFundDeclare, UserModel customerService) throws Exception;


    /**
     * 方法说明：根据员工档案的单据号获取社保公积金信息
     * @param employeeFilesId 员工档案的id
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author liulei
     * @Date 2020/2/26 15:24
     */
    Map<String, String> getSocialSecurityFundDetail(String employeeFilesId) throws Exception;

    /**
     * 方法说明：减员提交
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param socialSecurityClose
     * @param providentFundClose
     * @param employeeFiles
     * @param user
     * @return void
     * @author liulei
     * @Date 2020/2/26 15:50
     */
    void deleteEmployeeSubmit(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                              SocialSecurityClose socialSecurityClose, ProvidentFundClose providentFundClose,
                              EmployeeFiles employeeFiles, UserModel user) throws Exception;

    /**
     * 方法说明：减员_客户修改表单审核提交
     * @param deleteEmployee 减员_客户实体
     * @author liulei
     * @Date 2020/2/27 10:45
     */
    void deleteEmployeeUpdateSubmit(DeleteEmployee deleteEmployee) throws Exception;

    /**
     * 方法说明：减员_上海修改表单审核提交
     * @param shDeleteEmployee 减员_上海实体
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:38
     */
    void shDeleteEmployeeUpdateSubmit(ShDeleteEmployee shDeleteEmployee) throws Exception;

    /**
     * 方法说明：减员_全国修改表单审核提交
     * @param nationwideDispatch  减员_全国实体
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:58
     */
    void qgDeleteEmployeeUpdateSubmit(NationwideDispatch nationwideDispatch) throws Exception;

    /**
     * 方法说明：员工档案数据修改审核提交
     * @param employeeFiles 员工档案修改数据
     * @return void
     * @author liulei
     * @Date 2020/2/27 16:48
     */
    void employeeFilesUpdateSubmit(EmployeeFiles employeeFiles) throws Exception;

    /**
     * 方法说明：增员_客户数据修改审核提交
     * @param addEmployee 增员_客户修改数据
     * @param employeeFiles 员工档案修改数据
     * @param employeeOrderForm 员工订单
     * @param socialSecurityDeclare 社保申报
     * @param providentFundDeclare 公积金申报
     * @author liulei
     * @Date 2020/2/28 17:08
     */
    void addEmployeeUpdateSubmit(AddEmployee addEmployee, EmployeeFiles employeeFiles,
                                 EmployeeOrderForm employeeOrderForm, SocialSecurityDeclare socialSecurityDeclare,
                                 ProvidentFundDeclare providentFundDeclare) throws Exception;

    /**
     * 方法说明：增员_上海数据修改审核提交
     * @param shAddEmployee 增员_上海修改数据
     * @param employeeFiles 员工档案修改数据
     * @param employeeOrderForm 员工订单
     * @param socialSecurityDeclare 社保申报
     * @param providentFundDeclare 公积金申报
     * @return void
     * @author liulei
     * @Date 2020/3/2 10:01
     */
    void shAddEmployeeUpdateSubmit(ShAddEmployee shAddEmployee, EmployeeFiles employeeFiles,
                                   EmployeeOrderForm employeeOrderForm, SocialSecurityDeclare socialSecurityDeclare,
                                   ProvidentFundDeclare providentFundDeclare) throws Exception;

    /**
     * 方法说明：增员_全国数据修改审核提交
     * @param nationwideDispatch 增员_全国修改数据
     * @param employeeFiles 员工档案修改数据
     * @param employeeOrderForm 员工订单
     * @param socialSecurityDeclare 社保申报
     * @param providentFundDeclare 公积金申报
     * @return void
     * @author liulei
     * @Date 2020/3/2 11:21
     */
    void qgAddEmployeeUpdateSubmit(NationwideDispatch nationwideDispatch, EmployeeFiles employeeFiles,
                                   EmployeeOrderForm employeeOrderForm, SocialSecurityDeclare socialSecurityDeclare,
                                   ProvidentFundDeclare providentFundDeclare) throws Exception;

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
    void updateOrderFormStatusToPrePoint(String ids, String field) throws Exception;
}
