package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.BatchEvacuation;
import com.authine.cloudpivot.web.api.mapper.BatchEvacuationMapper;
import com.authine.cloudpivot.web.api.service.BatchEvacuationService;
import com.authine.cloudpivot.web.api.utils.SystemDataSetUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 15:14
 * @Description:
 */
@Service
public class BatchEvacuationServiceImpl implements BatchEvacuationService {

    @Resource
    BatchEvacuationMapper batchEvacuationMapper;

    @Override
    public List<String> addBatchEvacuationDatas(String userId, OrganizationFacade organizationFacade, List<BatchEvacuation> batchEvacuations) {
        if (null == batchEvacuations || 0 == batchEvacuations.size()) {
            return null;
        }

        List<BatchEvacuation> adds = new ArrayList<>();
        List<String> objectIds = new ArrayList<>();
        int insertNum = 0;
        UserModel user = organizationFacade.getUser(userId);
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());

        for (BatchEvacuation batchEvacuation : batchEvacuations) {
            SystemDataSetUtils.dataSet(user, department, batchEvacuation.getEmployeeName(), Constants.COMPLETED_STATUS,  batchEvacuation);
            objectIds.add(batchEvacuation.getId());
            adds.add(batchEvacuation);
            insertNum++;
            if (insertNum == Constants.MAX_INSERT_NUM) {
                batchEvacuationMapper.insertBatchEvacuationDatas(adds);
                adds.clear();
                insertNum = 0;
            }
        }
        if (0 != adds.size()) {
            batchEvacuationMapper.insertBatchEvacuationDatas(adds);
            adds.clear();
        }
        return objectIds;
    }
}
