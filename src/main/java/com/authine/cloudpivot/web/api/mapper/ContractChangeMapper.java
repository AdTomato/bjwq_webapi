package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ChangeValue;
import com.authine.cloudpivot.web.api.entity.PurchaseContractInfo;
import com.authine.cloudpivot.web.api.entity.SalesContractInfo;

import java.util.List;

/**
 * @ClassName ContractChangeMapper
 * @Author:lfh
 * @Date:2020/4/11 14:54
 * @Description:
 **/
public interface ContractChangeMapper {
    SalesContractInfo findSalesContractInfo(String id);

    PurchaseContractInfo findPurchaseInfo(String id);

    void updateContractChangeDetails(List<ChangeValue> data);

    String findClientName(String x_client_name);

    String findSupplierName(String c_supplier_name);
}
