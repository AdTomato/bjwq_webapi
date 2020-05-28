package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.Nccps;
import com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime;
import com.authine.cloudpivot.web.api.entity.NccpsProvidentFundRatio;
import com.authine.cloudpivot.web.api.entity.NccpsWorkInjuryRatio;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.NccpsMapper
 * @Date 2020/5/11 13:37
 **/
public interface NccpsMapper {
    /**
     * 方法说明：根据客户名称查询客户个性化设置
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @return com.authine.cloudpivot.web.api.entity.Nccps
     * @author liulei
     * @Date 2020/5/11 13:40
     */
    Nccps getNccpsByClientName(String firstLevelClientName, String secondLevelClientName);

    /**
     * 方法说明：根据父级id、城市、福利办理方查询城市时间节点数据
     * @param parentId
     * @param city
     * @param welfareHandler
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime>
     * @author liulei
     * @Date 2020/5/11 13:51
     */
    NccpsOrderCutOffTime getNccpsOrderCutOffTimeByParentIdAndCity(String parentId, String city, String welfareHandler);

    /**
     * 方法说明：根据父级id、城市、福利办理方查询公积金比例数据
     * @param parentId
     * @param city
     * @param welfareHandler
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime>
     * @author liulei
     * @Date 2020/5/11 13:51
     */
    List<NccpsProvidentFundRatio> getNccpsProvidentFundRatioByParentIdAndCity(String parentId, String city, String welfareHandler);

    /**
     * 方法说明：根据父级id、城市、福利办理方查询工伤比例数据
     * @param parentId
     * @param city
     * @param welfareHandler
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime>
     * @author liulei
     * @Date 2020/5/11 13:51
     */
    NccpsWorkInjuryRatio getNccpsWorkInjuryRatioByParentIdAndCity(String parentId, String city, String welfareHandler);
}
