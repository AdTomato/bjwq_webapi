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
     * 方法说明：根据城市名称查询征缴规则
     * @param city
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.CollectionRule>
     * @author liulei
     * @Date 2020/4/2 13:54
     */
    CollectionRule getCollectionRuleByCity(String city) throws Exception;

    /**
     * 方法说明：根据福利地查询征缴规则
     * @param socialSecurityCity
     * @param providentFundCity
     * @return com.authine.cloudpivot.web.api.entity.CollectionRule
     * @author liulei
     * @Date 2020/4/16 13:45
     */
    CollectionRule getCollectionRuleByCity(String socialSecurityCity, String providentFundCity) throws Exception;
}
