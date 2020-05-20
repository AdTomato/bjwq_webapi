package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;
import com.authine.cloudpivot.web.api.mapper.SalesContractMapper;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangyong
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
     * @param startDate: 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 22:24
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.SalesContractDto>
     * @Description: 根据账单生成时间在startDate和endDate之间的销售合同
     */
    @Override
    public List<SalesContractDto> getSalesContractByGenerateBillDate(Date startDate) {
        return salesContractMapper.getSalesContractByGenerateBillDate(startDate);
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
     * 获取销售合同中的销售单价列表
     *
     * @param clientName   客户名称
     * @param businessType 业务类型
     * @param serviceArea  服务地区
     * @param areaDetails  地区详情
     * @return 销售单价列表
     * @author wangyong
     */
    @Override
    public ServiceChargeUnitPrice getServiceChargeUnitPrice(String clientName, String businessType, String serviceArea, String areaDetails) {
        SalesContractDto salesContractDto = getSalesContractDto(clientName, businessType);
        if (salesContractDto != null) {
            List<ServiceChargeUnitPrice> serviceChargeUnitPrices = salesContractDto.getServiceChargeUnitPrices();
            if (serviceChargeUnitPrices != null && serviceChargeUnitPrices.size() != 0) {
                List<ServiceChargeUnitPrice> nextLevelData = new ArrayList<>();
                for (ServiceChargeUnitPrice serviceChargeUnitPrice : serviceChargeUnitPrices) {
                    if (StringUtils.isEmpty(serviceChargeUnitPrice.getAreaDetails())) {
                        nextLevelData.add(serviceChargeUnitPrice);
                    } else {
                        if (serviceChargeUnitPrice.getAreaDetails().contains(areaDetails)) {
                            if ("预收".equals(salesContractDto.getBillType())) {
                                serviceChargeUnitPrice.setPrecollected("是");
                            }
                            serviceChargeUnitPrice.setPayCycle(salesContractDto.getBillCycle());
                            return serviceChargeUnitPrice;
                        }
                    }
                }
                List<ServiceChargeUnitPrice> allNullData = new ArrayList<>();
                if (!nextLevelData.isEmpty()) {
                    for (ServiceChargeUnitPrice serviceChargeUnitPrice : nextLevelData) {
                        if (StringUtils.isEmpty(serviceChargeUnitPrice.getServiceArea())) {
                            allNullData.add(serviceChargeUnitPrice);
                        } else {
                            if (serviceChargeUnitPrice.getServiceArea().contains(serviceArea)) {
                                if ("预收".equals(salesContractDto.getBillType())) {
                                    serviceChargeUnitPrice.setPrecollected("是");
                                }
                                serviceChargeUnitPrice.setPayCycle(salesContractDto.getBillCycle());
                                return serviceChargeUnitPrice;
                            }
                        }
                    }
                }
                if (!allNullData.isEmpty()) {
                    ServiceChargeUnitPrice serviceChargeUnitPrice = allNullData.get(0);
                    if ("预收".equals(salesContractDto.getBillType())) {
                        serviceChargeUnitPrice.setPrecollected("是");
                    }
                    serviceChargeUnitPrice.setPayCycle(salesContractDto.getBillCycle());
                    return serviceChargeUnitPrice;
                }
            }
        }
        return null;
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

    /**
     * 获取销售合同中的服务费
     *
     * @param clientName  客户名称
     * @param staffNature 员工性质
     * @param area        地区
     * @return 服务费
     */
    @Override
    public Double getFee(String clientName, String staffNature, String area) {
        SalesContractDto salesContractDto = getSalesContractDto(clientName, staffNature);
        if (salesContractDto == null) {
            return 0D;
        }
        List<ServiceChargeUnitPrice> serviceChargeUnitPrices = salesContractDto.getServiceChargeUnitPrices();
        boolean isAnhuiCity = AreaUtils.isAnhuiCity(area);
        String flag = "";
        if (isAnhuiCity) {
            flag = "省内";
        } else {
            flag = "省外";
        }
        if (null == serviceChargeUnitPrices || 0 == serviceChargeUnitPrices.size()) {
            return 0D;
        }
        if (serviceChargeUnitPrices.size() == 1) {
            return serviceChargeUnitPrices.get(0).getServiceChargeUnitPrice();
        }
        int level = 0;
        Double result = 0D;
        for (ServiceChargeUnitPrice serviceChargeUnitPrice : serviceChargeUnitPrices) {
            if (flag.equals(serviceChargeUnitPrice.getServiceArea())) {
                level = 1;
                if (serviceChargeUnitPrice.getAreaDetails().length() > area.length()) {
                    if (serviceChargeUnitPrice.getAreaDetails().contains(area)) {
                        level = 2;
                        result = serviceChargeUnitPrice.getServiceChargeUnitPrice();
                    }
                } else {
                    if (area.contains(serviceChargeUnitPrice.getAreaDetails())) {
                        level = 2;
                        result = serviceChargeUnitPrice.getServiceChargeUnitPrice();
                    }
                }
            }
            if (level == 2) {
                // 达到最终筛选条件了
                break;
            }
        }
        return result;
    }
}
