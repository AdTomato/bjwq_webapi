package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.CityTimeNodeSwDetails;

/**
 * @author liulei
 * @Description 城市时间节点设置
 * @ClassName com.authine.cloudpivot.web.api.service.CityTimeNodeService
 * @Date 2020/5/8 14:07
 **/
public interface CityTimeNodeService {
    /**
     * 方法说明：根据上一业务年月生成当月的城市时间节点设置数据
     * @return void
     * @author liulei
     * @Date 2020/5/8 14:09
     */
    String insertCityTimeNode() throws Exception;

    /**
     * 方法说明：获取安徽省城市时间节点设置
     * @param city 城市名称
     * @param businessYear 业务年月
     * @return com.authine.cloudpivot.web.api.entity.CityTimeNode
     * @author liulei
     * @Date 2020/5/9 17:09
     */
    CityTimeNode getAnhuiCityTimeNode(String city, String businessYear) throws Exception;

    CityTimeNodeSwDetails getCityTimeNodeSwDetails(String city, String businessYear) throws Exception;

    /**
     * 根据业务年月、城市获取城市时间节点
     *
     * @param businessYear 业务年月
     * @param city         城市
     * @return 城市时间节点
     * @author wangyong
     */
    CityTimeNode getCityTimeNodeByBusinessYearAndCity(String businessYear, String city);
}
