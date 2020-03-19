package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-03-02 16:33
 * @Description:
 */
public interface ExcelFileService {

    public void insertData(String departmentId, String userId, String workflowCode, boolean b, WorkflowInstanceFacade workflowInstanceFacade, String tableName, List<Map<String, Object>> data);

    void startWorkflow(String departmentId, String userId, String workflowCode, List<Map<String, Object>> data, boolean b, WorkflowInstanceFacade workflowInstanceFacade);
}
