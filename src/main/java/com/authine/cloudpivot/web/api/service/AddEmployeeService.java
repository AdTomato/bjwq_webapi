package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.dto.UpdateAddEmployeeDto;
import com.authine.cloudpivot.web.api.entity.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.AddEmployeeService
 * @Date 2020/2/28 14:33
 **/
public interface AddEmployeeService {
    /**
     * 方法说明：根据id获取增员_客户实体
     * @param id
     * @author liulei
     * @Date 2020/2/28 10:10
     */
    AddEmployee getAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_上海实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    ShAddEmployee getShAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_全国实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    NationwideDispatch getQgAddEmployeeById(String id) throws Exception;

    EmployeeOrderForm getEmployeeOrderFormByEmployeeFilesId(String id) throws Exception;

    /**
     * 方法说明：创建增员相关数据
     * @param addEmployee 增员实体
     * @param employeeFilesId 员工档案id
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @return void
     * @author liulei
     * @Date 2020/5/13 8:49
     */
    ServiceChargeUnitPrice createAddEmployeeData(AddEmployee addEmployee, String employeeFilesId, BizObjectFacade bizObjectFacade,
                               WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：根据客户名称，人员id,查询是否存在员工档案
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @param identityNo
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/5/14 11:08
     */
    EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName, String secondLevelClientName,
                                                            String identityNo) throws Exception;

    /**
     * 方法说明：增加增员相关数据
     * @param addEmployee
     * @param employeeFiles
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @return ServiceChargeUnitPrice
     * @author liulei
     * @Date 2020/5/14 13:26
     */
    ServiceChargeUnitPrice addAddEmployeeData(AddEmployee addEmployee, EmployeeFiles employeeFiles, BizObjectFacade bizObjectFacade,
                            WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    /**
     * 方法说明：更新员工档案数据
     * @param employeeFiles
     * @return void
     * @author liulei
     * @Date 2020/5/15 9:17
     */
    void updateEmployeeFiles(EmployeeFiles employeeFiles) throws Exception;

    EmployeeFiles getEmployeeFilesByAddEmployeeId(String id) throws Exception;

    SocialSecurityDeclare getSocialSecurityDeclareByAddEmployeeId(String id) throws Exception;

    ProvidentFundDeclare getProvidentFundDeclareByAddEmployeeId(String id)throws Exception;

    UpdateAddEmployeeDto getAddEmployeeData(AddEmployee updateAddEmployee, String employeeFilesId) throws Exception;

    void createSocialSecurityDeclare(SocialSecurityDeclare sDeclare, BizObjectFacade bizObjectFacade,
                                     WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    void createProvidentFundDeclare(ProvidentFundDeclare pDeclare, BizObjectFacade bizObjectFacade,
                                     WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    String createEmployeeOrderForm(EmployeeOrderForm orderForm, BizObjectFacade bizObjectFacade) throws Exception;

    String getHsLevyHandler(String city, String welfareHandler, String type, String secondLevelClientName) throws Exception;

    void updateAddEmployee(AddEmployee addEmployee) throws Exception;

    void updateShAddEmployee(ShAddEmployee shAddEmployee) throws Exception;

    void updateQgAddEmployee(NationwideDispatch nationwideDispatch) throws Exception;
}
