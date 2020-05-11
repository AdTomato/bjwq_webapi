package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.CollectionRule;
import com.authine.cloudpivot.web.api.entity.CollectionRuleDetails;
import com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper;
import com.authine.cloudpivot.web.api.service.CollectionRuleService;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public CollectionRule getCollectionRuleByCity(String socialSecurityCity, String providentFundCity) throws Exception {
        CollectionRule collectionRule = new CollectionRule();
        if (StringUtils.isNotBlank(socialSecurityCity)) {
            if (socialSecurityCity.equals(providentFundCity)) {
                // 社保公积金福利地相同
                List<CollectionRule> collectionRules = collectionRuleMapper.getCollectionRulesByCity(socialSecurityCity);
                return collectionRules != null && collectionRules.size() > 0 ? collectionRules.get(0) : null;
            } else if (StringUtils.isNotBlank(providentFundCity)) {
                // 社保公积金福利地不相同
                List<CollectionRule> sRules = collectionRuleMapper.getSbCollectionRulesByCity(socialSecurityCity);
                if (sRules != null && sRules.size() > 0) {
                    collectionRule = sRules.get(0);
                }
                List<CollectionRule> gRules = collectionRuleMapper.getGjjCollectionRulesByCity(socialSecurityCity);
                if (gRules != null && gRules.size() > 0) {
                    CollectionRule gRule = gRules.get(0);
                    collectionRule.setProvidentFund(gRule.getProvidentFund());
                    List<CollectionRuleDetails> details = collectionRule.getCollectionRuleDetails();
                    if (details != null && details.size() > 0) {
                        List<CollectionRuleDetails> gDetails = gRule.getCollectionRuleDetails();
                        if (gDetails != null && gDetails.size() > 0) {
                            details.addAll(gDetails);
                            collectionRule.setCollectionRuleDetails(details);
                        }
                    } else {
                        collectionRule.setCollectionRuleDetails(gRule.getCollectionRuleDetails());
                    }
                }
            } else {
                // 公积金福利地为空
                List<CollectionRule> collectionRules = collectionRuleMapper.getSbCollectionRulesByCity(socialSecurityCity);
                return collectionRules != null && collectionRules.size() > 0 ? collectionRules.get(0) : null;
            }
        } else if (StringUtils.isNotBlank(providentFundCity)){
            List<CollectionRule> collectionRules = collectionRuleMapper.getGjjCollectionRulesByCity(socialSecurityCity);
            return collectionRules != null && collectionRules.size() > 0 ? collectionRules.get(0) : null;
        }
        return collectionRule;
    }
}
