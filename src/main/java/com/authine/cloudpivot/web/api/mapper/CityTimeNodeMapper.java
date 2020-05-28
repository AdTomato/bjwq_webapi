package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.CityTimeNodeSwDetails;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.CityTimeNodeMapper
 * @Date 2020/5/8 14:22
 **/
public interface CityTimeNodeMapper {
    /**
     * 方法说明：根据上一个业务年月生成当前业务年月的城市时间节点
     * @param curBusinessYear 当前业务年月
     * @param lastBusinessYear 上一个业务年月
     * @return void
     * @author liulei
     * @Date 2020/5/8 14:41
     */
    void addCurCityTimeNode(String curBusinessYear, String lastBusinessYear);

    /**
     * 方法说明：获取安徽省城市时间节点设置
     * @param city 城市名称
     * @param businessYear 业务年月@return com.authine.cloudpivot.web.api.entity.CityTimeNode
     * @author liulei
     * @Date 2020/5/9 17:10
     */
    CityTimeNode getAnhuiCityTimeNode(String city, String businessYear);

    /**
     * 方法说明：获取省外城市时间节点设置
     * @param city 城市名称
     * @param businessYear 业务年月@return com.authine.cloudpivot.web.api.entity.CityTimeNode
     * @return com.authine.cloudpivot.web.api.entity.CityTimeNodeSwDetails
     * @author liulei
     * @Date 2020/5/9 17:22
     */
    CityTimeNodeSwDetails getCityTimeNodeSwDetails(String city, String businessYear);

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
