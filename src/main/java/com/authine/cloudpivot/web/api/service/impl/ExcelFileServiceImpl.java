package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.service.ExcelFileService;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-03-02 16:34
 * @Description:
 */
@Service
public class ExcelFileServiceImpl implements ExcelFileService {

    @Resource
    EmployeeFilesMapper employeeFilesMapper;

    @Override
    public void insertData(String departmentId, String userId, String workflowCode, boolean b, WorkflowInstanceFacade workflowInstanceFacade, String tableName, List<Map<String, Object>> data) {
        switch (tableName) {
            case Constants.SH_ADD_EMPLOYEE_TABLE_NAME:
                // 上海增
                shAddEmployee(data);
                startWorkflow(departmentId, userId, workflowCode, data, b, workflowInstanceFacade);
                break;
            case Constants.SH_DELETE_EMPLOYEE_TABLE_NAME:
                // 上海减
                shDeleteEmployee(data);
                startWorkflow(departmentId, userId, workflowCode, data, b, workflowInstanceFacade);
                break;
            case Constants.NATIONWIDE_DISPATCH:
                // 全国
                nationwideEmployee(departmentId, userId, workflowCode, data, b, workflowInstanceFacade);
                break;
        }
    }

    @Override
    public void startWorkflow(String departmentId, String userId, String workflowCode, List<Map<String, Object>> data, boolean b, WorkflowInstanceFacade workflowInstanceFacade) {
        for (Map<String, Object> datum : data) {
            if (datum.containsKey("id") && StringUtils.isEmpty(datum.get("id") + "")) {
                workflowInstanceFacade.startWorkflowInstance(departmentId, userId, workflowCode, datum.get("id") + "", b);
            }
        }
    }

    private void nationwideEmployee(String departmentId, String userId, String workflowCode, List<Map<String, Object>> data, boolean b, WorkflowInstanceFacade workflowInstanceFacade) {
        List<Map<String, Object>> add = new ArrayList<>();
        List<Map<String, Object>> delete = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            if (StringUtils.isEmpty(datum.get("s_service_fee_end_date") + "")) {
                // 为空，增员
                add.add(datum);
            } else {
                // 不为空，减员
                delete.add(datum);
            }
        }
        if (0 != add.size()) {
            nationwideAddEmployee(add);
            startWorkflow(departmentId, userId, Constants.NATIONWIDE_DISPATCH_WF, add, b, workflowInstanceFacade);
        }
        if (0 != delete.size()) {
            nationwideDeleteEmployee(delete);
            startWorkflow(departmentId, userId, Constants.DEL_NATIONWIDE_DISPATCH_WF, delete, b, workflowInstanceFacade);
        }
    }

    /**
     * @param data : 增员数据
     * @return : void
     * @Author: wangyong
     * @Date: 2020/3/2 17:32
     * @Description: 上海增员
     */
    private void shAddEmployee(List<Map<String, Object>> data) {
        employeeFilesMapper.insertShAddEmployeeData(data);

    }

    /**
     * @param data : 减员数据
     * @return : void
     * @Author: wangyong
     * @Date: 2020/3/2 17:33
     * @Description: 上海减员
     */
    private void shDeleteEmployee(List<Map<String, Object>> data) {
        employeeFilesMapper.insertShDeleteEmployeeData(data);
    }

    /**
     * @param data : 增员数据
     * @return : void
     * @Author: wangyong
     * @Date: 2020/3/2 17:33
     * @Description: 全国增员
     */
    private void nationwideAddEmployee(List<Map<String, Object>> data) {
        employeeFilesMapper.insertNationwideAddEmployee(data);
    }

    /**
     * @param data : 减员数据
     * @return : void
     * @Author: wangyong
     * @Date: 2020/3/2 17:33
     * @Description: 全国减员
     */
    private void nationwideDeleteEmployee(List<Map<String, Object>> data) {
        employeeFilesMapper.insertNationwideDeleteEmployee(data);
    }
}
