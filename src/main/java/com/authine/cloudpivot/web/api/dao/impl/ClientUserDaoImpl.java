package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.ClientUserDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import jodd.util.BCrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.ClientUserDaoImpl
 * @Date 2020/1/3 17:12
 **/
@Repository
public class ClientUserDaoImpl implements ClientUserDao {
    @Override
    public void updateClientUserId(String id, String userId) throws Exception {
        String sql = "update id34a_client_management set  user_id = '" + userId + "'  where id = '" + id + "' ";
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void clientResetPassword(String ids) throws Exception {
        String password = "{bcrypt}" + BCrypt.hashpw("123456", BCrypt.gensalt());
        String sql = "update h_org_user set password = '" + password + "' where id in " +
                "(select user_id from id34a_client_management where id in ('" +
                ids.replaceAll(",", "','") + "'))";
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public List <Map <String, Object>> getPersonalizedSetByNo(String clientNumber) throws Exception {
        String sql = "select time_node, company_injury_ratio, employee_injury_ratio, company_accumulation_ratio, " +
                "employee_accumulation_ratio from id34a_ccps where client_number = '" + clientNumber + "'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        return list;
    }

    /**
     * 方法说明：客户导入员工信息，提交后维护修改截止时间，工伤比例，公积金比例
     * @Param id
     * @Param endTime
     * @Param companyInjuryRatio
     * @Param employeeInjuryRatio
     * @Param companyFundRatio
     * @Param employeeFundRatio
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/19 14:53
     */
    @Override
    public void updateEndTime(String id, String endTime, String companyInjuryRatio, String employeeInjuryRatio,
                              String companyFundRatio, String employeeFundRatio) throws Exception {
        // TODO LIULEI 表名待定
        String sql = "update ib4j1_employee_information set ";
        sql += "end_time = '" + endTime + "' ";
        /*=============个性化客户设置start====================================*/
        if (StringUtils.isNotBlank(companyInjuryRatio)) {
            sql += ",company_injury_ratio_hide = '" + companyInjuryRatio + "' ";
        }
        if (StringUtils.isNotBlank(employeeInjuryRatio)) {
            sql += ",employee_injury_ratio_hide = '" + employeeInjuryRatio + "' ";
        }
        if (StringUtils.isNotBlank(companyFundRatio)) {
            sql += ",company_fund_ratio_hide = '" + companyFundRatio + "' ";
        }
        if (StringUtils.isNotBlank(employeeFundRatio)) {
            sql += ",employee_fund_ratio_hide = '" + employeeFundRatio + "' ";
        }
        /*=============个性化客户设置end======================================*/
        sql += "where id = '" + id + "'";

        ConnectionUtils.executeSql(sql);
    }

    /**
     * 方法说明：根据城市id获取时间节点
     * @Param ids
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/1/19 15:24
     */
    @Override
    public String getTimeNodeByIds(String ids) throws Exception {
        String sql = "select min(time_node) time_node from id34a_city_time_node where id in ('" +
                ids.replaceAll(",", "','") + "')";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() >0) {
            return list.get(0).get("time_node").toString();
        } else {
            throw new Exception("根据城市id获取时间节点失败。");
        }
    }
}
