package com.authine.cloudpivot.web.api.mapper;


import com.authine.cloudpivot.web.api.dto.FirstLevelClientDto;
import com.authine.cloudpivot.web.api.dto.SecondLevelClientDto;
import com.authine.cloudpivot.web.api.entity.ContactInfo;
import com.authine.cloudpivot.web.api.entity.FlcSalesman;
import com.authine.cloudpivot.web.api.entity.SecondContactInfo;

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
     * 根据客户名称获取一级客户联的业务员
     *
     * @param companyName 公司名称
     * @return 业务员
     */
    public List<String> getFirstLevelClientSalesmanByCompanyName(String companyName);

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
     * 根据客户名称获取二级客户里面的业务员
     *
     * @param companyName 客户名称
     * @return 业务员
     */
    public List<String> getSecondLevelClientSalesmanByCompanyName(String companyName);

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


    /**修改后的代码*/
    /**
     * 根据一级客户名称获取一级客户全部信息
     *
     * @param firstClientName 一级客户名称
     * @return 一级客户全部信息
     * @author wangyong
     */
    FirstLevelClientDto getFirstLevelClientDtoByClientName(String firstClientName);

    /**
     * 根据父id获取一级客户里面的业务员信息子表
     *
     * @param parentId 父id
     * @return 业务员信息子表
     * @author wangyong
     */
    List<FlcSalesman> getFlcSalesmanListByParentId(String parentId);

    /**
     * 根据父id获取一级客户里面的联系人子表
     *
     * @param parentId 父id
     * @return 联系人子表
     * @author wangyong
     */
    List<ContactInfo> getContactInfoListByParentId(String parentId);

    /**
     * 根据二级客户名称获取二级客户全部信息
     *
     * @param secondClientName 二级客户名称
     * @return 二级客户全部信息
     * @author wangyong
     */
    SecondLevelClientDto getSecondLevelClientDtoByClientName(String secondClientName);

    /**
     * 根据父id获取二级客户里面的联系人子表
     *
     * @param parentId 父id
     * @return 联系人子表
     * @author wangyong
     */
    SecondContactInfo getSecondContractInfoListByParentId(String parentId);
}
