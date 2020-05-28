package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.CollectionRule;

/**
 * @author liulei
 * @Description 征缴规则Service
 * @ClassName com.authine.cloudpivot.web.api.service.CollectionRuleService
 * @Date 2020/4/2 13:42
 **/
public interface CollectionRuleService {

    /**
     * 方法说明：将有效终止月<当前时间的汇缴规则数据转移到历史数据中
     * @return void
     * @author liulei
     * @Date 2020/5/9 16:51
     */
    void moveCollectionRuleDataToHistory() throws Exception;

    /**
     * 根据城市、公司公积金比例查询最新的有效起始月中是否有满足条件的数据
     *
     * @param city         城市
     * @param companyRadio 公司比例
     * @return true 存在， false，不存在
     * @author wangyong
     */
    boolean isHaveCompanyRatioInMaxStartMonth(String city, Double companyRadio);
}
