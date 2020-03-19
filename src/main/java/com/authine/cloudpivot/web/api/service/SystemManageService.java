package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.OrgUser;
import com.authine.cloudpivot.web.api.entity.SmsHistory;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:59
 * @Description: 系统管理service接口
 */
public interface SystemManageService {

    /**
     * 根据城市名称获取时间节点
     *
     * @param cityName
     * @return 时间节点
     */
    public Integer getTimeNodeByCity(String cityName);

    /**
     * 根据手机号码获取用户
     *
     * @param mobile 手机号码
     * @return 用户实体类
     */
    public OrgUser getOrgUserByMobile(String mobile);

    /**
     * 根据id获取手机历史验证码
     *
     * @param id id
     * @return 手机历史验证码数据
     */
    SmsHistory getSmsHistoryById(String id);
}
