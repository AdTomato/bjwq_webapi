package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.BatchEvacuation;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 14:53
 * @Description: 批量撤离mapper
 */
public interface BatchEvacuationMapper{

    public void insertBatchEvacuationDatas(List<BatchEvacuation> batchEvacuations);

}
