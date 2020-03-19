package com.authine.cloudpivot.web.api.service;

/**
 * @Author:wangyong
 * @Date:2020/3/13 20:30
 * @Description: 政策平台service接口
 */
public interface PolicyPlatformService {

    /**
     * 获取一个城市工伤保险政策的id
     *
     * @param city 城市
     * @return 工伤保险政策id
     */
    public String getInjuryPolicyObjectIdByCity(String city);

    /**
     * 获取一个城市生育保险政策的id
     *
     * @param city 城市
     * @return 生育保险政策id
     */
    public String getMaternityInsuranceObjectIdByCity(String city);

    /**
     * 获取一个城市失业保险政策的id
     *
     * @param city 城市
     * @return 失业保险政策id
     */
    public String getUnemploymentInsuranceObjectIdByCity(String city);

    /**
     * 获取一个城市养老保险政策的id
     *
     * @param city 城市
     * @return 养老保险政策id
     */
    public String getPensionObjectIdByCity(String city);

    /**
     * 获取一个城市医疗保险政策的id
     *
     * @param city 城市
     * @return 医疗保险政策id
     */
    public String getMedicalInsuranceObjectIdByCity(String city);

    /**
     * 获取一个城市省直公积金政策的id
     *
     * @param city 城市
     * @return 省直公积金政策id
     */
    public String getProvincialDirectProvidenObjectIdByCity(String city);

    /**
     * 获取一个城市市直公积金政策id
     *
     * @param city 城市
     * @return 市直公积金政策id
     */
    public String getMunicipalProvidentFundObjectIdByCity(String city);

}
