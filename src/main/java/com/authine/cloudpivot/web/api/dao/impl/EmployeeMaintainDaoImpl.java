package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.EmployeeMaintainDaoImpl
 * @Date 2020/2/10 17:18
 **/
@Repository
@Slf4j
public class EmployeeMaintainDaoImpl implements EmployeesMaintainDao {

    @Override
    public List <Map <String, Object>> getAddOrDelWorkItemId(String ids, String tableName) throws Exception {
       String sql = " SELECT * FROM( SELECT code.id, workitem.id workItemId FROM " + tableName + " code " +
               " JOIN biz_workflow_instance instance ON instance.bizObjectId = code.id" +
               " JOIN biz_workitem workitem ON workitem.instanceId = instance.id" +
               " WHERE code.sequenceStatus != 'COMPLETED' AND code.id IN ('"+ ids.replaceAll(",", "','") +"') ) AS a";
        return ConnectionUtils.executeSelectSql(sql);
    }
}
