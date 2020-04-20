package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.OrgUser;
import com.authine.cloudpivot.web.api.entity.SmsHistory;
import com.authine.cloudpivot.web.api.mapper.SystemManageMapper;
import com.authine.cloudpivot.web.api.service.SystemManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 16:00
 * @Description:
 */
@Service
public class SystemManageServiceImpl implements SystemManageService {

    @Resource
    SystemManageMapper systemManageMapper;

    /**
     * @param cityName : 城市名称
     * @return : java.lang.Integer
     * @Author: wangyong
     * @Date: 2020/2/7 16:01
     * @Description: 根据城市名称获取时间节点
     */
    @Override
    public Integer getTimeNodeByCity(String cityName) {
        return systemManageMapper.getTimeNodeByCity(cityName);
    }

    /**
     * @param mobile: 手机号码
     * @Author: wangyong
     * @Date: 2020/3/15 23:11
     * @return: com.authine.cloudpivot.web.api.entity.OrgUser
     * @Description: 成员实体类
     */
    @Override
    public OrgUser getOrgUserByMobile(String mobile) {
        return systemManageMapper.getOrgUserByMobile(mobile);
    }

    /**
     * @param id: id
     * @Author: wangyong
     * @Date: 2020/3/15 23:36
     * @return: com.authine.cloudpivot.web.api.entity.SmsHistory
     * @Description: 手机历史验证码
     */
    @Override
    public SmsHistory getSmsHistoryById(String id) {
        return systemManageMapper.getSmsHistoryById(id);
    }

    /**
     * @param mobile: 手机号码
     * @Author: wangyong
     * @Date: 2020/4/16 17:02
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Description: 根据手机号码获取这个手机号码所对应的客户名称以及用户名
     */
    @Override
    public List<Map<String, String>> getNameAndUserNameByMobile(String mobile) {

        return systemManageMapper.getNameAndUserNameByMobile(mobile);
    }
}
