package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.AppointmentSheet;
import com.authine.cloudpivot.web.api.entity.OperateLeader;
import com.authine.cloudpivot.web.api.entity.OrgUser;
import com.authine.cloudpivot.web.api.entity.SmsHistory;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:09
 * @Description: 系统管理mapper
 */
public interface SystemManageMapper {

    /**
     * 根据城市名称获取时间节点
     * @param cityName
     * @return 时间节点
     */
    public int getTimeNodeByCity(String cityName);

    /**
     * 方法说明：根据客户名称，城市名称查询委托单位
     * @param clientName 客户名称
     * @param city 城市名称
     * @return com.authine.cloudpivot.web.api.entity.AppointmentSheet
     * @author liulei
     * @Date 2020/3/11 13:34
     */
    List <AppointmentSheet> getWelfareHandlerByClientNameAndCity(String clientName, String city);

    /**
     * @param mobile: 手机号码
     * @Author: wangyong
     * @Date: 2020/3/15 23:11
     * @return: com.authine.cloudpivot.web.api.entity.OrgUser
     * @Description: 成员实体类
     */
    OrgUser getOrgUserByMobile(String mobile);

    /**
     * @param id: id
     * @Author: wangyong
     * @Date: 2020/3/15 23:36
     * @return: com.authine.cloudpivot.web.api.entity.SmsHistory
     * @Description: 手机历史验证码
     */
    SmsHistory getSmsHistoryById(String id);

    /**
     * 方法说明：根据地区和福利办理方获取社保，公积金经办人
     * @param city
     * @param welfareHandler
     * @return com.authine.cloudpivot.web.api.entity.OperateLeader
     * @author liulei
     * @Date 2020/3/18 16:35
     */
    List<OperateLeader> getOperateLeaderByCityAndWelfareHandler(String city, String welfareHandler);
}
