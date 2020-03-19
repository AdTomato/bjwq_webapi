package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.SendMessageDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.SendMessageDaoImpl
 * @Date 2020/1/7 13:33
 **/
@Repository
public class SendMessageDaoImpl implements SendMessageDao {
    @Override
    public List <Map <String, String>> getHaveTimeOutInfo() throws Exception{

        String sql = " select CONCAT(employee_name, '社保卡办理') as message,salesman, operator, '1' type from " +
                "iewep_social_security_card where end_time is not null and date_format(end_time, '%Y-%m-%d') < " +
                "date_format(sysdate(), '%Y-%m-%d') ";
        sql += " union all ";
        sql += " select CONCAT(employee_name, '五险一金享受办理') as message, owner salesman, operator, '2' type from " +
                "i3bme_insurance_and_housing_fund where end_time is not null and date_format(end_time, '%Y-%m-%d') < " +
                "date_format(sysdate(), '%Y-%m-%d')";

        List<Map<String, String>> result = ConnectionUtils.executeSelectSql1(sql);
        return result;
    }

}
