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
    public void moveCollectionRuleDataToHistory() throws Exception {
        // 将有效终止月<当前时间的汇缴规则数据转移到历史数据中
        collectionRuleMapper.moveCollectionRuleDataToHistory();
        collectionRuleMapper.deleteOvertimeCollectionRuleData();
    }

    /**
     * 根据城市、公司公积金比例查询最新的有效起始月中是否有满足条件的数据
     *
     * @param city         城市
     * @param companyRadio 公司比例
     * @return true 存在， false，不存在
     * @author wangyong
     */
    @Override
    public boolean isHaveCompanyRatioInMaxStartMonth(String city, Double companyRadio) {
        return collectionRuleMapper.isHaveCompanyRatioInMaxStartMonth(city, companyRadio).isEmpty() ? false : true;
    }
}
