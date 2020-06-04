package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.Nccps;
import com.authine.cloudpivot.web.api.entity.NccpsProvidentFundRatio;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.NccpsService
 * @Date 2020/5/11 13:37
 **/
public interface NccpsService {

    /**
     * 根据一级客户名称、二级客户名称、城市、福利办理方获取客户征缴个性化里面的公积金比例
     *
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param city             城市
     * @param welfareHandler   福利办理方
     * @return 公积金比例
     * @author wangyong
     */
    List<NccpsProvidentFundRatio> getNccpsProvidentFoundRatioByFirstOrSecondClientName(String firstClientName, String secondClientName, String city, String welfareHandler);

}
