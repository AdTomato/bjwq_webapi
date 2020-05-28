package com.authine.cloudpivot.web.api.service;

import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:26
 * @Description: 客户service接口
 */
public interface ClientService {

    /**
     * 获取一级客户里面的服务费
     *
     * @param parentId 父id
     * @param area     地区
     * @return 服务费
     */
    public Integer getFirstLevelClientFee(String parentId, String area);

    /**
     * 获取一级客户里面的业务员
     *
     * @param parentId    主表id
     * @param area        地区
     * @param staffNature 员工性质
     * @return 业务员
     */
    public String getFirstLevelClientSalesman(String parentId, String area, String staffNature);

    /**
     * 根据客户名称获取一级客户里面的业务员
     *
     * @param companyName 客户名称
     * @return 业务员
     */
    public String getFirstLevelClientSalesmanByCompanyName(String companyName);

    /**
     * 根据公司名称，委托单位获取一级客户id
     *
     * @param clientName    公司名称
     * @param entrustedUnit 委托单位
     * @return 获取一级客户id
     */
    String getFirstLevelClientId(String clientName, String entrustedUnit);

    /**
     * 获取二级客户里面的服务费
     *
     * @param parentId 父id
     * @param area     地区
     * @return 服务费
     */
    public Integer getSecondLevelClientFee(String parentId, String area);

    /**
     * 获取二级客户里面的业务员
     *
     * @param parentId    主表id
     * @param area        地区
     * @param staffNature 员工性质
     * @return 业务员
     */
    public String getSecondLevelClientSalesman(String parentId, String area, String staffNature);

    /**
     * 根据客户名称获取二级客户里面的业务员
     *
     * @param companyName 客户名称
     * @return 业务员
     */
    public String getSecondLevelClientSalesmanByCompanyName(String companyName);

    /**
     * 根据公司名称，委托单位获取二级客户id
     *
     * @param clientName    公司名称
     * @param entrustedUnit 委托单位
     * @return 获取二级客户id
     */
    String getSecondLevelClientId(String clientName, String entrustedUnit);


    /**
     * 获取业务员和服务费
     *
     * @param clientName    公司名称
     * @param entrustedUnit 委托单位
     * @param area          地区
     * @param staffNature   员工性质
     * @return 业务员和服务费
     */
    public Map<String, Object> getClientSalesmanAndFee(String clientName, String entrustedUnit, String area,
                                                       String staffNature);


    /**
     * 获取客户里面的操作人以及查看人
     *
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param welfare          福利地
     * @param welfareHandler   福利办理方
     * @return 操作人、查看人
     * @author wangyong
     */
    Map<String, String> getLookAndEditPerson(String firstClientName, String secondClientName, String businessType, String welfare, String welfareHandler);
}
