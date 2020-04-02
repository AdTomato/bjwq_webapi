package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.ClientMapper;
import com.authine.cloudpivot.web.api.service.ClientService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:26
 * @Description: 客户service
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Resource
    ClientMapper clientMapper;

    @Autowired
    SalesContractService salesContractService;

    @Autowired
    Unit unit;

    /**
     * @param parentId : 父id
     * @param area     : 地区
     * @return : java.lang.Integer
     * @Author: wangyong
     * @Date: 2020/2/12 9:43
     * @Description: 获取一级客户里面的服务费
     */
    @Override
    public Integer getFirstLevelClientFee(String parentId, String area) {
        List<Integer> firstLevelClientFee = clientMapper.getFirstLevelClientFee(parentId, area);
        return null == firstLevelClientFee ? null : firstLevelClientFee.get(0);
    }

    /**
     * @param parentId    :父id
     * @param area        :地区
     * @param staffNature :员工性质
     * @return : java.util.List<com.authine.cloudpivot.web.api.entity.Unit>
     * @Author: wangyong
     * @Date: 2020/2/6 9:27
     * @Description: 根据客户名称获取一级客户中的业务员
     */
    @Override
    public String getFirstLevelClientSalesman(String parentId, String area, String staffNature) {
        List<String> result = clientMapper.getFirstLevelClientSalesman(parentId, area, staffNature);
        return null == result || result.size() > 0 ? null : result.get(0);
    }

    /**
     * @param companyName: 客户名称
     * @Author: wangyong
     * @Date: 2020/3/18 16:21
     * @return: java.lang.String
     * @Description: 根据客户名称获取一级客户里面的业务员
     */
    @Override
    public String getFirstLevelClientSalesmanByCompanyName(String companyName) {
        List<String> result = clientMapper.getFirstLevelClientSalesmanByCompanyName(companyName);
        return null == result || result.size() > 0 ? null : result.get(0);
    }

    /**
     * @param clientName    : 公司名称
     * @param entrustedUnit : 委托单位
     * @return : java.lang.String
     * @Author: wangyong
     * @Date: 2020/2/11 16:53
     * @Description: 根据委托单位，公司名称获取一级客户id
     */
    @Override
    public String getFirstLevelClientId(String clientName, String entrustedUnit) {
        List<String> firstLevelClientId = clientMapper.getFirstLevelClientId(clientName, entrustedUnit);
        return null == firstLevelClientId || firstLevelClientId.size() > 0 ? null : firstLevelClientId.get(0);
    }

    /**
     * @param parentId : 父id
     * @param area     : 地区
     * @return : java.lang.Integer
     * @Author: wangyong
     * @Date: 2020/2/12 9:45
     * @Description: 获取二级客户里面的服务费
     */
    @Override
    public Integer getSecondLevelClientFee(String parentId, String area) {
        List<Integer> secondLevelClientFee = clientMapper.getSecondLevelClientFee(parentId, area);
        return null == secondLevelClientFee || secondLevelClientFee.size() > 0 || secondLevelClientFee.size() > 0 ? null : secondLevelClientFee.get(0);
    }

    /**
     * @param parentId    :父id
     * @param area        :地区
     * @param staffNature :员工性质
     * @return : java.util.List<com.authine.cloudpivot.web.api.entity.Unit>
     * @Author: wangyong
     * @Date: 2020/2/6 9:34
     * @Description: 根据客户名称获取二级客户中的业务员
     */
    @Override
    public String getSecondLevelClientSalesman(String parentId, String area, String staffNature) {
        List<String> result = clientMapper.getSecondLevelClientSalesman(parentId, area, staffNature);
        return null == result || result.size() > 0 ? null : result.get(0);
    }

    /**
     * @param companyName: 客户名称
     * @Author: wangyong
     * @Date: 2020/3/18 16:22
     * @return: java.lang.String
     * @Description: 根据客户名称获取二级客户里面的业务员
     */
    @Override
    public String getSecondLevelClientSalesmanByCompanyName(String companyName) {
        List<String> result = clientMapper.getSecondLevelClientSalesmanByCompanyName(companyName);
        return null == result || result.size() > 0 ? null : result.get(0);
    }

    /**
     * @param clientName    : 公司名称
     * @param entrustedUnit : 委托单位
     * @return : java.lang.String
     * @Author: wangyong
     * @Date: 2020/2/11 16:53
     * @Description: 根据公司名称，委托单位获取二级客户名称
     */
    @Override
    public String getSecondLevelClientId(String clientName, String entrustedUnit) {
        List<String> secondLevelClientId = clientMapper.getSecondLevelClientId(clientName, entrustedUnit);
        return null == secondLevelClientId || secondLevelClientId.size() > 0 ?  null : secondLevelClientId.get(0);
    }


    /**
     * @param clientName    : 公司名称
     * @param entrustedUnit : 委托单位(可为空)
     * @param area          : 地区
     * @param staffNature   : 员工性质
     * @return : java.util.Map
     * @Author: wangyong
     * @Date: 2020/2/11 16:58
     * @Description: 根据公司名称，委托单位，地区，员工性质获取业务员和服务费
     */
    @Override
    public Map<String, Object> getClientSalesmanAndFee(String clientName, String entrustedUnit, String area, String staffNature) {
        Map<String, Object> result = new HashMap<>();
        String salesman = getFirstLevelClientSalesmanByCompanyName(clientName);
        if (Strings.isEmpty(salesman)) {
            // 为空，以及客户中没有查找到
            salesman = getSecondLevelClientSalesmanByCompanyName(clientName);
            if (Strings.isEmpty(salesman)) {
                throw new RuntimeException("销售人员没有找到");
            }
        }
        result.put("salesman", salesman);
        SalesContractDto salesContractDto = salesContractService.getSalesContractDto(clientName, staffNature);
        if (null != salesContractDto) {
            result.put("fee", getFee(salesContractDto, area));
        } else {
            result.put("fee", 0D);
        }
        return result;
    }

    /**
     * @param salesContractDto: 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 21:33
     * @return: java.lang.Double
     * @Description: 获取销售合同中该地区的服务费
     */
    public Double getFee(SalesContractDto salesContractDto, String area) {
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
