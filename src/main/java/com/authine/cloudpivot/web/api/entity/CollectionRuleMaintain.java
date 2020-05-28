package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CollectionRuleMaintain
 * @Date 2020/5/11 17:18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRuleMaintain extends BaseEntity{
    /**
     * 省份
     */
    String province;
    /**
     * 城市
     */
    String city;
    /**
     * 福利办理方
     */
    String welfareHandler;
    /**
     * 省平均工资
     */
    Double provincialAverageSalary = 0d;
    /**
     * 市平均工资
     */
    Double cityAverageSalary = 0d;
    /**
     * 最低工资标准
     */
    Double minimumWageStandard = 0d;
    /**
     * 用工备案申报材料
     */
    String employmentFilingMaterials;
    /**
     * 社保申报材料
     */
    String socialSecurityDeclarationMaterials;
    /**
     * 公积金申报材料
     */
    String providentFundDeclarationMaterials;
    /**
     * 汇缴规则
     */
    List <CollectionRule> collectionRules;
    /**
     * 补缴规则
     */
    List <PaymentRules> paymentRules;
}
