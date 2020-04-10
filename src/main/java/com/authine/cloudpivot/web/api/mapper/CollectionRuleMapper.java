package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.CollectionRule;
import com.authine.cloudpivot.web.api.entity.CollectionRuleDetails;

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
}
