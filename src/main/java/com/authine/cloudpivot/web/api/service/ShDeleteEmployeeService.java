package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 10:52
 * @Description: 减员(上海)service接口
 */
public interface ShDeleteEmployeeService {

    void importEmployee(String fileName, String userId, String departmentId, BizObjectFacade bizObjectFacade, OrganizationFacade organizationFacade, WorkflowInstanceFacade workflowInstanceFacade);


}
