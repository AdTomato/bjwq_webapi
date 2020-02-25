package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.BatchPreDispatch;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 13:45
 * @Description: 批量预派
 */
public interface BatchPreDispatchMapper {

    public void insertBatchPreDispatchDatas(List<BatchPreDispatch> batchPreDispatches);

}
