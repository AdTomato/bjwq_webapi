package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.CollectionRule;
import com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper;
import com.authine.cloudpivot.web.api.service.CollectionRuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liulei
 * @Description 征缴规则ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.CollectionRuleServiceImpl
 * @Date 2020/4/2 13:42
 **/
@Service
public class CollectionRuleServiceImpl implements CollectionRuleService {

    @Resource
    private CollectionRuleMapper collectionRuleMapper;

    @Override
    public CollectionRule getCollectionRuleByCity(String city) throws Exception {
        List<CollectionRule> collectionRules = collectionRuleMapper.getCollectionRulesByCity(city);
        return collectionRules != null && collectionRules.size() > 0 ? collectionRules.get(0) : null;
    }
}
