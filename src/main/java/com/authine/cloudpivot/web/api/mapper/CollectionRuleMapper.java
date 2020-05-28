package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.CollectionRule;
import com.authine.cloudpivot.web.api.entity.CollectionRuleDetails;
import com.authine.cloudpivot.web.api.entity.CrmCollectionRule;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper
 * @Date 2020/4/2 13:55
 **/
public interface CollectionRuleMapper {
    List<CollectionRule> getCollectionRulesByCity(String city);

    List<CollectionRuleDetails> getCollectionRuleDetailsByParentId(String id);

    List<CollectionRule> getSbCollectionRulesByCity(String city);

    List<CollectionRuleDetails> getSbCollectionRuleDetailsByParentId(String id);

    List<CollectionRule> getGjjCollectionRulesByCity(String city);

    List<CollectionRuleDetails> getGjjCollectionRuleDetailsByParentId(String id);

    /**
     * 根据城市、公司公积金比例查询最新的有效起始月中是否有满足条件的数据
     *
     * @param city         城市
     * @param companyRadio 公司比例
     * @return 满足条件的汇缴数据
     * @author wangyong
     */
    List<CrmCollectionRule> isHaveCompanyRatioInMaxStartMonth(String city, Double companyRadio);
}
