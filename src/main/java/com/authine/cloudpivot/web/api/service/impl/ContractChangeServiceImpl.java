package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.ChangeValue;
import com.authine.cloudpivot.web.api.entity.PurchaseContractInfo;
import com.authine.cloudpivot.web.api.entity.SalesContractInfo;
import com.authine.cloudpivot.web.api.mapper.ContractChangeMapper;
import com.authine.cloudpivot.web.api.service.ContractChangeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ContractChangeServiceImpl
 * @Author:lfh
 * @Date:2020/4/11 14:54
 * @Description:
 **/
@Service
public class ContractChangeServiceImpl implements ContractChangeService {

    @Resource
    private ContractChangeMapper contractChangeMapper;

    @Override
    public SalesContractInfo findSalesContractInfo(String id) {
        return contractChangeMapper.findSalesContractInfo(id);
    }

    @Override
    public PurchaseContractInfo findPurchaseInfo(String id) {
        return contractChangeMapper.findPurchaseInfo(id);
    }

    @Override
    public void updateContractChangeDetails(List<ChangeValue> data) {
        contractChangeMapper.updateContractChangeDetails(data);
    }

    @Override
    public String findClientName(String x_client_name) {
        return contractChangeMapper.findClientName(x_client_name);
    }

    @Override
    public String findSupplierName(String c_supplier_name) {
        return contractChangeMapper.findSupplierName(c_supplier_name);
    }


}
