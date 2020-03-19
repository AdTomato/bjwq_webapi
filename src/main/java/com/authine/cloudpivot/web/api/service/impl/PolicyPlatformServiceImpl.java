package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.controller.PolicyPlatformController;
import com.authine.cloudpivot.web.api.mapper.PolicyPlatformMapper;
import com.authine.cloudpivot.web.api.service.PolicyPlatformService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @Author:wangyong
 * @Date:2020/3/13 20:31
 * @Description: 政策平台service
 */
@Service
public class PolicyPlatformServiceImpl implements PolicyPlatformService {

    @Resource
    PolicyPlatformMapper policyPlatformMapper;

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市工伤保险政策的id
     */
    @Override
    public String getInjuryPolicyObjectIdByCity(String city) {
        return policyPlatformMapper.getInjuryPolicyObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市生育保险政策的id
     */
    @Override
    public String getMaternityInsuranceObjectIdByCity(String city) {
        return policyPlatformMapper.getMaternityInsuranceObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市失业保险政策的id
     */
    @Override
    public String getUnemploymentInsuranceObjectIdByCity(String city) {
        return policyPlatformMapper.getUnemploymentInsuranceObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市养老保险政策的id
     */
    @Override
    public String getPensionObjectIdByCity(String city) {
        return policyPlatformMapper.getPensionObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市医疗保险政策的id
     */
    @Override
    public String getMedicalInsuranceObjectIdByCity(String city) {
        return policyPlatformMapper.getMedicalInsuranceObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市省直公积金政策的id
     */
    @Override
    public String getProvincialDirectProvidenObjectIdByCity(String city) {
        return policyPlatformMapper.getProvincialDirectProvidenObjectIdByCity(city);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/13 20:55
     * @param city: 城市
     * @return: java.lang.String
     * @Description: 获取一个城市市直公积金政策id
     */
    @Override
    public String getMunicipalProvidentFundObjectIdByCity(String city) {
        return policyPlatformMapper.getMunicipalProvidentFundObjectIdByCity(city);
    }
}
