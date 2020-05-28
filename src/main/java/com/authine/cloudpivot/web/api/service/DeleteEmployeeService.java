package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.entity.*;

/**
 * @author liulei
 * @Description 减员service
 * @ClassName com.authine.cloudpivot.web.api.service.DeleteEmployeeService
 * @Date 2020/5/15 8:56
 **/
public interface DeleteEmployeeService {
    /**
     * 方法说明：根据id获取减员_客户信息
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.DeleteEmployee
     * @author liulei
     * @Date 2020/5/15 8:43
     */
    DeleteEmployee getDeleteEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_上海实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    ShDeleteEmployee getShDeleteEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_全国实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    NationwideDispatch getQgDeleteEmployeeById(String id) throws Exception;

    void createDeleteEmployeeData(DeleteEmployee delEmployee, EmployeeFiles employeeFilesId, BizObjectFacade bizObjectFacade,
                                  WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    EmployeeFiles getEmployeeFilesByDelEmployeeId(String id) throws Exception;

    SocialSecurityClose getSocialSecurityCloseByDelEmployeeId(String id) throws Exception;

    ProvidentFundClose getProvidentFundCloseByDelEmployeeId(String id) throws Exception;

    void createSocialSecurityClose(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId,
                                   BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    void createProvidentFundClose(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId,
                                   BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    void updateDeleteEmployee(DeleteEmployee deleteEmployee) throws Exception;

    void updateShDeleteEmployee(ShDeleteEmployee shDeleteEmployee) throws Exception;

    void updateQgDeleteEmployee(NationwideDispatch qgDeleteEmployee) throws Exception;
}
