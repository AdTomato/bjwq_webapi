package com.authine.cloudpivot.web.api.dao;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao
 * @Date 2020/2/10 17:17
 **/
public interface EmployeesMaintainDao {

    List<Map<String, Object>> getAddOrDelWorkItemId(String ids, String table) throws Exception;
}
