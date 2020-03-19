package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.web.api.entity.BatchEvacuation;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 15:01
 * @Description:
 */
public interface BatchEvacuationService {

    public List<String> addBatchEvacuationDatas(String userId, OrganizationFacade organizationFacade, List<BatchEvacuation> batchEvacuations);

}
