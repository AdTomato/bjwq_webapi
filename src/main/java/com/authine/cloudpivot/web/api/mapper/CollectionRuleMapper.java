package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper
 * @Date 2020/4/2 13:55
 **/
public interface CollectionRuleMapper {
    /**
     * 方法说明：将有效终止月<当前时间的汇缴规则数据转移到历史数据中
     * @return void
     * @author liulei
     * @Date 2020/5/12 14:55
     */
    void moveCollectionRuleDataToHistory();

    /**
     * 方法说明：将有效终止月<当前时间的汇缴规则数据删除
     * @return void
     * @author liulei
     * @Date 2020/5/12 14:55
     */
    void deleteOvertimeCollectionRuleData();

    /**
     * 方法说明：根据城市和福利办理方查询征缴规则
     * @param city 城市
     * @param welfareHandler 福利办理方
     * @return com.authine.cloudpivot.web.api.entity.CollectionRuleMaintain
     * @author liulei
     * @Date 2020/5/12 14:56
     */
    CollectionRuleMaintain getCollectionRuleMaintain(String city, String welfareHandler);

    List<PaymentRules> getSbPaymentRules(String parentId, String startMonth);

    List<PaymentRules> getGjjPaymentRules(String parentId, String startMonth, String productName, Double companyRatio, Double employeeRatio);

    List<CollectionRule> getSbCollectionRules(String parentId, String startMonth);

    List<CollectionRule> getGjjCollectionRules(String parentId, String startMonth, String productName, Double companyRatio, Double employeeRatio);

    /**
     * 根据城市、公司公积金比例查询最新的有效起始月中是否有满足条件的数据
     *
     * @param city         城市
     * @param companyRadio 公司比例
     * @return 满足条件的汇缴数据
     * @author wangyong
     */
    List<CrmCollectionRule> isHaveCompanyRatioInMaxStartMonth(String city, Double companyRadio, String welfareHandler);
}
