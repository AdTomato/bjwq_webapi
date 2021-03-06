package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.SalesmansMaintain;

/**
 * @Author:wangyong
 * @Date:2020/4/15 14:52
 * @Description:
 */
public interface SalesmansMaintainMapper {

    /**
     * 获取客户对应的业务员以及部门名称
     *
     * @param firstLevelClientName  一级客户名称
     * @param secondLevelClientName 二级客户名称
     * @param businessType          业务类型
     * @param welfare               福利地
     * @param welfareOperator       福利办理方
     * @return 业务员/部门名称
     */
    SalesmansMaintain getSalesmansAndDepartment(String firstLevelClientName, String secondLevelClientName, String businessType, String welfare, String welfareOperator);

    /**
     * 方法说明：根据一级客户，二级客户，业务类型，福利地，福利办理方查询
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @param businessType
     * @param city
     * @param welfareHandler
     * @return com.authine.cloudpivot.web.api.entity.SalesmansMaintain
     * @author liulei
     * @Date 2020/5/12 16:42
     */
    SalesmansMaintain getSalesmansMaintain(String firstLevelClientName, String secondLevelClientName,
                                           String businessType, String city, String welfareHandler);
}
