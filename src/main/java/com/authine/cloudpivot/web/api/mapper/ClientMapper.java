package com.authine.cloudpivot.web.api.mapper;


import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:06
 * @Description: 客户mapper
 */
public interface ClientMapper {

    /**
     * 获取一级客户里面的业务员
     *
     * @param parentId    主表id
     * @param area        地区
     * @param staffNature 员工性质
     * @return 业务员
     */
    public List<String> getFirstLevelClientSalesman(String parentId, String area, String staffNature);

    /**
     * 根据公司名称，委托单位获取一级客户id
     *
     * @param clientName    公司名称
     * @param entrustedUnit 委托单位
     * @return 获取一级客户id
     */
    public List<String> getFirstLevelClientId(String clientName, String entrustedUnit);

    /**
     * 获取一级客户里面的服务费
     *
     * @param parentId 父id
     * @param area     地区
     * @return 服务费
     */
    public List<Integer> getFirstLevelClientFee(String parentId, String area);

    /**
     * 获取二级客户里面的业务员
     *
     * @param parentId    主表id
     * @param area        地区
     * @param staffNature 员工性质
     * @return 业务员
     */
    public List<String> getSecondLevelClientSalesman(String parentId, String area, String staffNature);

    /**
     * 根据公司名称，委托单位获取二级客户id
     *
     * @param clientName    公司名称
     * @param entrustedUnit 委托单位
     * @return 获取二级客户id
     */
    public List<String> getSecondLevelClientId(String clientName, String entrustedUnit);

    /**
     * 获取二级客户里面的服务费
     *
     * @param parentId 父id
     * @param area     地区
     * @return 服务费
     */
    public List<Integer> getSecondLevelClientFee(String parentId, String area);
}
