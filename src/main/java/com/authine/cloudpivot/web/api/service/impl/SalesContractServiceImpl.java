package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;
import com.authine.cloudpivot.web.api.mapper.SalesContractMapper;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 11:04
 * @Description:
 */
@Service
public class SalesContractServiceImpl implements SalesContractService {

    @Resource
    SalesContractMapper salesContractMapper;

    /**
     * @param bill: 账单日
     * @Author: wangyong
     * @Date: 2020/3/18 11:04
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.SalesContractDto>
     * @Description: 根据账单日获取账单日是派遣业务或专项外包或代理业务的销售合同信息
     */
    @Override
    public List<SalesContractDto> getSalesContractByBillDay(String bill) {
        return salesContractMapper.getSalesContractByBillDay(bill);
    }

    /**
     * @param clientName:   客户名称
     * @param businessType: 业务类型
     * @Author: wangyong
     * @Date: 2020/3/18 21:28
     * @return: com.authine.cloudpivot.web.api.dto.SalesContractDto
     * @Description: 根据客户名称以及业务类型获取销售合同
     */
    @Override
    public SalesContractDto getSalesContractDto(String clientName, String businessType) {
        return salesContractMapper.getSalesContractDto(clientName, businessType);
    }

    /**
     * @param id: 销售合同id
     * @Author: wangyong
     * @Date: 2020/3/18 21:27
     * @return: com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice
     * @Description: 根据销售合同id获取销售合同中的销售单单价列表
     */
    @Override
    public ServiceChargeUnitPrice getServiceChargeUnitPricesById(String id) {
        return salesContractMapper.getServiceChargeUnitPricesById(id);
    }
}
