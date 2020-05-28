package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;

import java.util.Date;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 11:03
 * @Description: 销售合同service接口
 */
public interface SalesContractService {

    /**
     * 根据账单日获取销售合同
     *
     * @param bill 账单日
     * @return 销售合同
     */
    List<SalesContractDto> getSalesContractByBillDay(String bill);

    /**
     * 根据账单生成时间在startDate和endDate之间的销售合同
     *
     * @param startDate 开始时间
     * @return 销售合同
     */
    List<SalesContractDto> getSalesContractByGenerateBillDate(Date startDate);

    /**
     * 根据客户名称，业务类型获取销售合同
     *
     * @param clientName   客户名称
     * @param businessType 业务类型
     * @return 销售合同
     */
    SalesContractDto getSalesContractDto(String clientName, String businessType);

    /**
     * 获取销售合同中的销售单价列表
     *
     * @param clientName   一级客户名称
     * @param businessType 业务类型
     * @param serviceArea  服务地区
     * @param areaDetails  地区详情
     * @return 销售单价列表
     * @author wangyong
     */
    ServiceChargeUnitPrice getServiceChargeUnitPrice(String clientName, String businessType, String serviceArea,
                                                     String areaDetails);


    /**
     * 根据销售id获取销售合同中的销售单单价列表
     *
     * @param id 销售合同id
     * @return 销售单单价列表
     */
    ServiceChargeUnitPrice getServiceChargeUnitPricesById(String id);

    /**
     * 获取销售合同中的服务费
     *
     * @param clientName  客户名称
     * @param staffNature 员工性质
     * @param area        地区
     * @return 服务费
     */
    Double getFee(String clientName, String staffNature, String area);

}
