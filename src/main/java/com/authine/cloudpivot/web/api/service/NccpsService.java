package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.Nccps;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.NccpsService
 * @Date 2020/5/11 13:37
 **/
public interface NccpsService {
    /**
     * 方法说明：查询客户个性化设置
     * @param firstLevelClientName 一级客户名称
     * @param secondLevelClientName 二级客户名称
     * @param sCity 社保城市名称
     * @param sWelfareHandler 社保福利办理方
     * @param gCity 公积金城市名称
     * @param gWelfareHandler 公积金福利办理方
     * @return com.authine.cloudpivot.web.api.entity.Nccps
     * @author liulei
     * @Date 2020/5/11 13:55
     */
    Nccps getNccps(String firstLevelClientName, String secondLevelClientName, String sCity,
                               String sWelfareHandler, String gCity, String gWelfareHandler) throws Exception;
}
