package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 14:41
 * @Description: 全国派单service接口
 */
public interface NationalDeliveryService {

    void importEmployee(String fileName, String userId, String departmentId, BizObjectFacade bizObjectFacade, OrganizationFacade organizationFacade, WorkflowInstanceFacade workflowInstanceFacade);


}
