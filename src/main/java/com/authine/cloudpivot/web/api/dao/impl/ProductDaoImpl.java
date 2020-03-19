package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.ProductDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import org.springframework.stereotype.Repository;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.ProductDaoImpl
 * @Date 2020/1/16 15:53
 **/
@Repository
public class ProductDaoImpl implements ProductDao {
    /**
     * 方法说明：修改征缴产品时更新对应的征缴政策里的数据
     * @Param id 征缴产品id
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/16 15:52
     */
    @Override
    public void updateProduct(String id) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE i080j_product_base_number a ");
        sql.append("JOIN (SELECT * FROM i080j_policy_collect_pay WHERE id = '" + id + "') b ");
        sql.append("ON a.product_name = b.id ");
        sql.append("SET a.policy_enjoy_superclass = b.policy_enjoy_superclass, ");
        sql.append("a.city = b.city, ");
        sql.append("a.collect_cycle = b.collect_cycle, ");
        sql.append("a.pay_cycle = b.pay_cycle, ");
        sql.append("a.close_date = b.close_date, ");
        sql.append("a.company_ratio = b.company_ratio, ");
        sql.append("a.employee_ratio = b.employee_ratio, ");
        sql.append("a.company_surcharge_value = b.company_surcharge_value, ");
        sql.append("a.employee_surcharge_value = b.employee_surcharge_value, ");
        sql.append("a.company_rounding_policy = b.company_rounding_policy, ");
        sql.append("a.employee_rounding_policy = b.employee_rounding_policy, ");
        sql.append("a.company_precision = b.company_precision, ");
        sql.append("a.employee_precision = b.employee_precision, ");
        sql.append("a.company_amount = b.company_amount, ");
        sql.append("a.employee_amount = b.employee_amount, ");
        sql.append("a.product_description = b.product_description ");

        ConnectionUtils.executeSql(sql.toString());
    }
}
