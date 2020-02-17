package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.ClientMapper;
import com.authine.cloudpivot.web.api.service.ClientService;
import org.apache.commons.lang3.StringUtils;
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
        return null == result ? null : result.get(0);
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
        return null == firstLevelClientId ? null : firstLevelClientId.get(0);
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
        return null == secondLevelClientFee ? null : secondLevelClientFee.get(0);
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
        return null == result ? null : result.get(0);
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
        return null == secondLevelClientId ? null : secondLevelClientId.get(0);
    }


    /**
     * @param clientName    : 公司名称
     * @param entrustedUnit : 委托单位
     * @param area          : 地区
     * @param staffNature   : 员工性质
     * @return : java.util.Map
     * @Author: wangyong
     * @Date: 2020/2/11 16:58
     * @Description: 根据公司名称，委托单位，地区，员工性质获取业务员和服务费
     */
    @Override
    public Map getClientSalesmanAndFee(String clientName, String entrustedUnit, String area, String staffNature) {
        Map<String, Object> result = new HashMap<>();
        // 获取一级客户的id值
        String id = getFirstLevelClientId(clientName, entrustedUnit);
        if (StringUtils.isEmpty(id)) {
            // 为空，一级客户不存在
            id = getSecondLevelClientId(clientName, entrustedUnit);
            if (StringUtils.isEmpty(id)) {
                // 为空，二级客户不存在
                // 返回空值
                return null;
            } else {
                // 不为空，从二级客户里面获取业务员和服务费
                String salesman = getSecondLevelClientSalesman(id, area, staffNature);
                Integer fee = getSecondLevelClientFee(id, area);
                result.put("salesman", salesman);
                result.put("fee", fee);
            }
        } else {
            // 不为空，从一级客户里面获取业务员和服务费
            String salesman = getFirstLevelClientSalesman(id, area, staffNature);
            Integer fee = getFirstLevelClientFee(id, area);
            result.put("salesman", salesman);
            result.put("fee", fee);
        }
        return result;
    }
}
