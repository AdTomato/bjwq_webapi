package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.web.api.entity.BatchPreDispatch;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 10:34
 * @Description: 批量预派service接口
 */
public interface BatchPreDispatchService {

    public List<String> addBatchPreDispatchs(String userId, OrganizationFacade organizationFacade, List<BatchPreDispatch> batchPreDispatches);

}
