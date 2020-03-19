package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.dao.OperatorDao;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运行人员接口实现类
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.OperatorDaoImpl
 * @Date 2019/12/18 14:13
 **/
@Repository
@Slf4j
public class OperatorDaoImpl implements OperatorDao {

    @Override
    public Map <String, String> getOperatorByArea(String area) throws Exception {
        // 返回值
        Map <String, String> result = new HashMap <>();

        String sql = "select id, operator from id34a_operate_area  where city like '%" + area + "%' ";
        List<Map<String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() >0) {
            result.put("id", list.get(0).get("id").toString());
            result.put("operator", list.get(0).get("operator").toString());
        }
        return result;
    }
}
