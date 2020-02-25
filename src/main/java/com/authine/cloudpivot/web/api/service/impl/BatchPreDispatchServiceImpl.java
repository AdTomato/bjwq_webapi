package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.BatchPreDispatch;
import com.authine.cloudpivot.web.api.mapper.BatchPreDispatchMapper;
import com.authine.cloudpivot.web.api.service.BatchPreDispatchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 10:35
 * @Description: 批量预派service
 */
@Service
public class BatchPreDispatchServiceImpl implements BatchPreDispatchService {

    @Resource
    BatchPreDispatchMapper batchPreDispatchMapper;

    /**
     * @param userId             : 当前操作人id
     * @param organizationFacade : 用于创建数据的类
     * @param batchPreDispatches : 数据实体类集合
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/25 10:37
     * @Description: 批量添加批量预派数据
     */
    @Override
    public List<String> addBatchPreDispatchs(String userId, OrganizationFacade organizationFacade, List<BatchPreDispatch> batchPreDispatches) {
        if (null == batchPreDispatches || 0 == batchPreDispatches.size()) {
            // 创建的数据集合为空，不创建数据
            return null;
        }

        List<BatchPreDispatch> adds = new ArrayList<>();
        List<String> objectIds = new ArrayList<>();
        int insertNum = 0;
        UserModel user = organizationFacade.getUser(userId);
        DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());
        for (BatchPreDispatch batchPreDispatch : batchPreDispatches) {
            dataSet(user, department, batchPreDispatch);
            objectIds.add(batchPreDispatch.getId());
            adds.add(batchPreDispatch);
            insertNum++;
            if (insertNum == Constants.MAX_INSERT_NUM) {
                insertNum = 0;
                batchPreDispatchMapper.insertBatchPreDispatchDatas(adds);
                adds.clear();
            }
        }
        if (0 != adds.size()) {
            batchPreDispatchMapper.insertBatchPreDispatchDatas(adds);
        }
        return objectIds;
    }

    /**
     * @param user             : 用户类
     * @param department       : 部门类
     * @param batchPreDispatch : 设置数据
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/25 13:43
     * @Description: 用于设置系统数据
     */
    private void dataSet(UserModel user, DepartmentModel department, BatchPreDispatch batchPreDispatch) {
        batchPreDispatch.setId(UUID.randomUUID().toString().replace("-", ""));
        batchPreDispatch.setName(batchPreDispatch.getEmployeeName());
        batchPreDispatch.setCreater(user.getId());
        batchPreDispatch.setCreatedDeptId(user.getDepartmentId());
        batchPreDispatch.setOwner(user.getId());
        batchPreDispatch.setOwnerDeptId(user.getDepartmentId());
        batchPreDispatch.setCreatedTime(new Date());
        batchPreDispatch.setModifier(user.getId());
        batchPreDispatch.setModifiedTime(new Date());
        batchPreDispatch.setSequenceStatus(Constants.COMPLETED_STATUS);
        batchPreDispatch.setOwnerDeptQueryCode(department.getQueryCode());
    }

}
