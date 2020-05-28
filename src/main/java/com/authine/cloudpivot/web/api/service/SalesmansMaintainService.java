package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.QueryInfo;
import com.authine.cloudpivot.web.api.entity.SalesmansMaintain;
import com.authine.cloudpivot.web.api.entity.Unit;

import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/4/15 15:05
 * @Description: 业务员维护
 */
public interface SalesmansMaintainService {

    Map<String, String> getSalesmansAndDepartmentS(String firstLevelClientName, String secondLevelClientName, String businessType, String welfare, String welfareOperator);

    Map<String, Unit> getSalesmansAndDepartmentU(String firstLevelClientName, String secondLevelClientName, String businessType, String welfare, String welfareOperator);

    QueryInfo getOperatorAndInquirer(QueryInfo queryInfo, String sCity, String sWelfareHandler, String gCity,
                                     String gWelfareHandler, String businessType, String firstLevelClientName,
                                     String secondLevelClientName) throws Exception;

    SalesmansMaintain getSalesmansMaintain(String firstLevelClientName, String secondLevelClientName,
                                           String businessType, String sCity, String sWelfareHandler) throws Exception;
}
