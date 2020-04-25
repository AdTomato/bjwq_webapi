package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.ChangeValue;
import com.authine.cloudpivot.web.api.entity.PurchaseContractInfo;
import com.authine.cloudpivot.web.api.entity.SalesContractInfo;

import java.util.List;

/**
 * @ClassName ContractChangeService
 * @Author:lfh
 * @Date:2020/4/11 14:53
 * @Description:
 **/
public interface ContractChangeService {
    /* * @Author lfh
     * @Description 根据id查询销售合同数据
     * @Date 2020/4/11 17:37
     * @Param [id]
     * @return com.authine.cloudpivot.web.api.entity.SalesContractInfo
     **/
    SalesContractInfo findSalesContractInfo(String id);

    /* * @Author lfh
     * @Description 根据id查询采购合同数据
     * @Date 2020/4/11 17:42
     * @Param [id]
     * @return com.authine.cloudpivot.web.api.entity.PurchaseContractInfo
     **/
    PurchaseContractInfo findPurchaseInfo(String id);

    /* * @Author lfh
     * @Description 更新合同变更数据
     * @Date 2020/4/11 17:44
     * @Param [data]
     * @return void
     **/
    void updateContractChangeDetails(List<ChangeValue> data);

    String findClientName(String x_client_name);

    String findSupplierName(String c_supplier_name);
}
