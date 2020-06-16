package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.api.model.runtime.WorkItemModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.*;
import com.authine.cloudpivot.web.api.service.DeleteEmployeeService;
import com.authine.cloudpivot.web.api.service.EmployeeMaintainService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.service.UpdateEmployeeService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.EmployeeMaintainServiceImpl
 * @Date 2020/2/4 17:35
 **/
@Service
@Slf4j
public class EmployeeMaintainServiceImpl implements EmployeeMaintainService {

    @Resource
    private EmployeesMaintainDao employeesMaintainDao;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Resource
    private ReturnReasonAlreadyMapper returnReasonAlreadyMapper;

    @Resource
    private EmployeesMaintainMapper employeesMaintainMapper;

    @Resource
    private UpdateEmployeeService updateEmployeeService;

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private DeleteEmployeeMapper deleteEmployeeMapper;

    @Override
    public void addEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                            DepartmentModel dept) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            importData(workflowInstanceFacade, fileList, user.getId(), dept.getId(), dept.getQueryCode(),
                    Constants.ADD_EMPLOYEE_SCHEMA, Constants.ADD_EMPLOYEE_SCHEMA_WF);
        }
    }

    @Override
    public void deleteEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                               DepartmentModel dept) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            importData(workflowInstanceFacade, fileList, user.getId(), dept.getId(), dept.getQueryCode(),
                    Constants.DELETE_EMPLOYEE_SCHEMA, Constants.DELETE_EMPLOYEE_SCHEMA_WF);
        }
    }

    /**
     * 方法说明：插入数据
     * @param workflowInstanceFacade 流程实例接口
     * @param fileList 导入的数据
     * @param userId
     * @param deptId
     * @param queryCode
     * @param schemaCode
     * @param schemaWfCode
     * @return void
     * @author liulei
     * @Date 2020/4/20 17:06
     */
    private void importData(WorkflowInstanceFacade workflowInstanceFacade, List <String[]> fileList, String userId,
                            String deptId, String queryCode, String schemaCode, String schemaWfCode) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList <>();
        List<String> idList = new ArrayList <>();
        List<String> fields = Arrays.asList(fileList.get(0));
        for (int i = 2; i < fileList.size(); i++) {
            List <String> list = Arrays.asList(fileList.get(i));
            Map<String, Object> data = new HashMap <>();
            for(int j = 0; j < fields.size(); j++) {
                data.put(fields.get(j), list.get(j));
            }
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            data.put("id", id);
            data.put("creater", userId);
            data.put("createdDeptId", deptId);
            data.put("owner", userId);
            data.put("ownerDeptId", deptId);
            data.put("createdTime", new Date());
            data.put("modifier", userId);
            data.put("modifiedTime", new Date());
            data.put("sequenceStatus", "DRAFT");
            data.put("ownerDeptQueryCode", queryCode);

            dataList.add(data);
            idList.add(id);
        }

        for (int j = 0; j < dataList.size(); j += 500) {
            int size = dataList.size();
            int toPasIndex = 500;
            if (j + 500 > size) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                toPasIndex = size - j;
            }
            List <Map<String, Object>> datas = dataList.subList(j, j + toPasIndex);
            List <String> ids = idList.subList(j, j + toPasIndex);
            // 新增数据
            if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)){
                // 客户增员导入
                addEmployeeMapper.addEmployeeImportData(datas);
                /*// 更新拥有者，业务员，等数据
                addEmployeeMapper.updateAddEmployeeOwner(datas);*/
            } else if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                // 减员客户导入
                addEmployeeMapper.deleteEmployeeImportData(datas);
               /* // 更新拥有者，业务员，等数据
                addEmployeeMapper.updateDeleteEmployeeOwner(datas);*/
            }
            // 启动流程实例，不结束发起节点
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, deptId, userId, schemaWfCode, ids, false);
        }
    }

    @Override
    public void batchSubmit(WorkflowInstanceFacade workflowInstanceFacade, String userId, List <String> idList,
                            String code, String billYear) throws Exception {
        String message = "";
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code)) {
            // 社保申报提交
            for (int i = 0; i < idList.size(); i++) {
                SocialSecurityDeclare declare = employeesMaintainMapper.getSocialSecurityDeclareById(idList.get(i));
                message += submitDeclare(declare.getId(), declare.getAddEmployeeId(), declare.getEmployeeOrderFormId(),
                        declare.getEmployeeName(), declare.getStatus(), "社保", declare.getWorkflowInstanceId(),
                        userId, billYear, workflowInstanceFacade);
            }
        } else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(code)) {
            // 公积金申报提交
            for (int i = 0; i < idList.size(); i++) {
                ProvidentFundDeclare declare = employeesMaintainMapper.getProvidentFundDeclareById(idList.get(i));
                message += submitDeclare(declare.getId(), declare.getAddEmployeeId(), declare.getEmployeeOrderFormId(),
                        declare.getEmployeeName(), declare.getStatus(), "公积金", declare.getWorkflowInstanceId(),
                        userId, billYear, workflowInstanceFacade);
            }
        } else if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code)) {
            // 社保停缴提交
            for (int i = 0; i < idList.size(); i++) {
                SocialSecurityClose close = employeesMaintainMapper.getSocialSecurityCloseById(idList.get(i));
                message += submitClose(close.getId(), close.getDelEmployeeId(), close.getEmployeeOrderFormId(),
                        close.getEmployeeName(), close.getStatus(), "社保", close.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade);
            }
        } else if (Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(code)) {
            // 公积金停缴提交
            for (int i = 0; i < idList.size(); i++) {
                ProvidentFundClose close = employeesMaintainMapper.getProvidentFundCloseById(idList.get(i));
                message += submitClose(close.getId(), close.getDelEmployeeId(), close.getEmployeeOrderFormId(),
                        close.getEmployeeName(), close.getStatus(), "公积金", close.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade);
            }
        }
        if (StringUtils.isNotBlank(message)) {
            log.info(message);
        }
    }


    /**
     * 方法说明：提交申报数据
     * @param id 申报表单id
     * @param addEmployeeId 对应增员id
     * @param employeeOrderFormId 员工档案id
     * @param employeeName 员工姓名
     * @param status 提交之前的申报状态
     * @param type 申报类型（社保；公积金）
     * @param workflowInstanceId 流程实例id
     * @param userId 当前用户id
     * @param billYear 账单年月
     * @param workflowInstanceFacade 流程实例接口
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/25 10:17
     */
    private String submitDeclare(String id, String addEmployeeId, String employeeOrderFormId, String employeeName,
                                 String status, String type, String workflowInstanceId, String userId, String billYear,
                                 WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        String message = "";
        if ("在缴".equals(status)) {
            message += "表单" + employeeName + "状态是在缴，无法提交;";
            return message;
        }
        if (StringUtils.isBlank(workflowInstanceId)) {
            message += "表单" + employeeName + "没有获取到流程实例id;";
            return message;
        }
        // 查询当前流程的待办任务
        String workItemId = getWorkItemId(workflowInstanceId, true, userId, workflowInstanceFacade);
        if (StringUtils.isBlank(workItemId)) {
            message += "表单" + employeeName + "没有获取到待办任务数据;";
        } else {
            if ("待办".equals(status)) {
                status = "在办";
            } else if ("驳回".equals(status)) {
                status = "在办";
            } else if ("在办".equals(status)) {
                status = "在缴";
            } else if ("预点".equals(status)) {
                status = "在缴";
            }
            if ("在缴".equals(status)) {
                // 更新员工档案数据
                recreateEmployeeOrderFormDetails(employeeOrderFormId);
            }
            // 提交任务
            workflowInstanceFacade.submitWorkItem(userId, workItemId, true);
            if ("社保".equals(type)) {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_social_security_declare", "status", status, billYear, null);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(addEmployeeId, "i4fvb_add_employee", "sb_status",
                        "在办".equals(status) ? "待办" : status, null, null);
                employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                        "social_security_status", "在办".equals(status) ? "待办" : status, null, null);
            } else {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_provident_fund_declare", "status", status, billYear, null);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(addEmployeeId, "i4fvb_add_employee", "gjj_status",
                        "在办".equals(status) ? "待办" : status, null, null);
                employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                        "provident_fund_status", "在办".equals(status) ? "待办" : status, null, null);
            }
        }
        return message;
    }

    /**
     * 方法说明：提交停缴数据
     * @param id 停缴表单id
     * @param delEmployeeId 对应减员id
     * @param employeeOrderFormId 员工档案id
     * @param employeeName 员工姓名
     * @param status 提交之前的申报状态
     * @param type 申报类型（社保；公积金）
     * @param workflowInstanceId 流程实例id
     * @param userId 当前用户id
     * @param workflowInstanceFacade 流程实例接口
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/25 10:17
     */
    private String submitClose(String id, String delEmployeeId, String employeeOrderFormId, String employeeName,
                               String status, String type, String workflowInstanceId, String userId,
                               WorkflowInstanceFacade workflowInstanceFacade) throws Exception{
        String message = "";
        if ("停缴".equals(status)) {
            message += "表单" + employeeName + "状态是停缴，无法提交;";
            return message;
        }
        if (StringUtils.isBlank(workflowInstanceId)) {
            message += "表单" + employeeName + "没有获取到流程实例id;";
            return message;
        }
        String workItemId = getWorkItemId(workflowInstanceId, true, userId, workflowInstanceFacade);
        if (StringUtils.isBlank(workItemId)) {
            message += "表单" + employeeName + "没有获取到待办任务数据;";
        } else {
            if ("待办".equals(status)) {
                status = "在办";
            } else if ("驳回".equals(status)) {
                status = "在办";
            } else if ("在办".equals(status)) {
                status = "停缴";
            }/* else if ("预点".equals(status)) {
                status = "停缴";
            }*/
            // 提交任务
            workflowInstanceFacade.submitWorkItem(userId, workItemId, true);
            if ("社保".equals(type)) {
                // 更新当前停缴数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_social_security_close", "status", status, null, null);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(delEmployeeId, "i4fvb_delete_employee", "sb_status",
                        "在办".equals(status) ? "待办" : status, null, null);
                if ("停缴".equals(status)) {
                    employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                            "social_security_status", "停缴", null, null);
                }
            } else {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_provident_fund_close", "status", status, null, null);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(delEmployeeId, "i4fvb_delete_employee", "gjj_status",
                        "在办".equals(status) ? "待办" : status, null, null);
                if ("停缴".equals(status)) {
                    employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                            "provident_fund_status", "停缴", null, null);
                }
            }
        }
        return message;
    }

    /**
     * 方法说明：获取当前流程对应当前用户的待办任务id
     * @param workflowInstanceId 流程实例id
     * @param isFinish 是否已办（如果isFinish为true，查询待办任务；为false，查询已办任务.）
     * @param userId 当前用户
     * @param workflowInstanceFacade 流程实例接口
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/25 10:36
     */
    private String getWorkItemId(String workflowInstanceId, boolean isFinish, String userId,
                                 WorkflowInstanceFacade workflowInstanceFacade) {
        /*List <WorkItemModel> models = workflowInstanceFacade.getWorkItems(workflowInstanceId, true);*/
        String workItemId = addEmployeeMapper.getWorkItemIdByInstanceIdAndUserId(workflowInstanceId, userId);
        /*if (models != null && models.size() > 0) {
            for (int j = 0; j < models.size(); j++) {
                WorkItemModel model = models.get(j);
                if (userId.equals(model.getParticipant())) {
                    // 当前人是待办人，即有提交权限
                    workItemId = model.getId();
                }
            }
        }*/
        return workItemId;
    }

    @Override
    public void batchReject(WorkflowInstanceFacade workflowInstanceFacade, String userId, List <String> idList,
                            String code, String returnReasonAlready) throws Exception {
        String message = "";
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code)) {
            // 社保申报
            for (int i = 0; i < idList.size(); i++) {
                SocialSecurityDeclare declare = employeesMaintainMapper.getSocialSecurityDeclareById(idList.get(i));
                message += rejectWorkflow(declare.getId(), declare.getAddEmployeeId(), declare.getEmployeeOrderFormId(),
                        declare.getEmployeeName(), declare.getStatus(), "社保", declare.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade, "declare", returnReasonAlready);
            }
        } else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(code)) {
            // 公积金申报
            for (int i = 0; i < idList.size(); i++) {
                ProvidentFundDeclare declare = employeesMaintainMapper.getProvidentFundDeclareById(idList.get(i));
                message += rejectWorkflow(declare.getId(), declare.getAddEmployeeId(), declare.getEmployeeOrderFormId(),
                        declare.getEmployeeName(), declare.getStatus(), "公积金", declare.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade, "declare", returnReasonAlready);
            }
        } else if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code)) {
            // 社保停缴
            for (int i = 0; i < idList.size(); i++) {
                SocialSecurityClose close = employeesMaintainMapper.getSocialSecurityCloseById(idList.get(i));
                message += rejectWorkflow(close.getId(), close.getDelEmployeeId(), close.getEmployeeOrderFormId(),
                        close.getEmployeeName(), close.getStatus(), "社保", close.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade, "close", null);
            }
        } else if (Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(code)) {
            // 公积金停缴
            for (int i = 0; i < idList.size(); i++) {
                ProvidentFundClose close = employeesMaintainMapper.getProvidentFundCloseById(idList.get(i));
                message += rejectWorkflow(close.getId(), close.getDelEmployeeId(), close.getEmployeeOrderFormId(),
                        close.getEmployeeName(), close.getStatus(), "公积金", close.getWorkflowInstanceId(),
                        userId, workflowInstanceFacade, "close", null);
            }
        }
        if (StringUtils.isNotBlank(message)) {
            log.info(message);
        }
    }

    /**
     * 方法说明：驳回申报，停缴流程
     * @param id 申报，停缴id
     * @param employeeId 增减员id
     * @param employeeOrderFormId  员工档案id
     * @param employeeName 员工姓名
     * @param status 驳回前状态
     * @param type 社保，公积金类型
     * @param workflowInstanceId 流程实例id
     * @param userId 当前用户
     * @param workflowInstanceFacade 流程实例接口
     * @param workflowType 流程类型（申报：declare;停缴：close。）
     * @param returnReasonAlready 退回原因
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/25 10:58
     */
    private String rejectWorkflow(String id, String employeeId, String employeeOrderFormId, String employeeName,
                                  String status, String type, String workflowInstanceId, String userId,
                                  WorkflowInstanceFacade workflowInstanceFacade, String workflowType,
                                  String returnReasonAlready) throws Exception {
        String message = "";
        if ("待办".equals(status)) {
            message += "表单" + employeeName + "状态是待办，无法驳回;";
            return message;
        }
        if ("驳回".equals(status)) {
            message += "表单" + employeeName + "状态是驳回，无法驳回;";
            return message;
        }
        if (StringUtils.isBlank(workflowInstanceId)) {
            message += "表单" + employeeName + "没有获取到流程实例id;";
            return message;
        }
        if ("在办".equals(status) || "预点".equals(status)) {   // 驳回流程到填写申请单
            // 查询当前流程的待办任务
            String workItemId = getWorkItemId(workflowInstanceId, true, userId, workflowInstanceFacade);
            if (StringUtils.isBlank(workItemId)) {
                message += "表单" + employeeName + "没有获取到当前用户的待办任务数据;";
            }
            // 当前节点是停缴，驳回至填写申请单节点
            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemId, "fill_application", true);
            if (flag) {
                // 驳回时，更新状态
                updateStatusToReject(type, id, employeeId, employeeOrderFormId, workflowType, returnReasonAlready);
            } else {
                message += "表单" + employeeName + "驳回失败;";
            }
        } else if ("停缴".equals(status) || "在缴".equals(status)) {
            boolean flag = workflowInstanceFacade.activateActivity(userId, workflowInstanceId,"fill_application");
            if (flag) {
                // 驳回时，更新状态
                updateStatusToReject(type, id, employeeId, employeeOrderFormId, workflowType, returnReasonAlready);
            } else {
                message += "表单" + employeeName + "驳回失败;";
            }
        }
        return message;
    }

    /**
     * 方法说明：驳回时更新表单状态
     * @param type 社保，公积金类型
     * @param id 申报，停缴表单id
     * @param employeeId 增减员表单id
     * @param employeeOrderFormId 员工订单id
     * @param workflowType 流程类型（申报：declare;停缴：close。）
     * @return void
     * @author liulei
     * @Date 2020/5/25 11:23
     */
    private void updateStatusToReject(String type, String id, String employeeId, String employeeOrderFormId,
                                      String workflowType, String returnReasonAlready) throws Exception {
        if ("社保".equals(type)) {
            if ("declare".equals(workflowType)) {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_social_security_declare", "status", "驳回", null, returnReasonAlready);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(employeeId, "i4fvb_add_employee", "sb_status", "驳回", null, null);
            } else {
                // 更新当前停缴数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_social_security_close", "status", "驳回", null, null);
                // 更新减员表单社保状态
                employeesMaintainMapper.updateStatus(employeeId, "i4fvb_delete_employee", "sb_status", "驳回", null, null);
            }
            employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                    "social_security_status", "驳回", null, null);
        } else {
            if ("declare".equals(workflowType)) {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_provident_fund_declare", "status", "驳回", null, returnReasonAlready);
                // 更新增员表单社保状态
                employeesMaintainMapper.updateStatus(employeeId, "i4fvb_add_employee", "gjj_status", "驳回", null, null);
            } else {
                // 更新当前申报数据状态
                employeesMaintainMapper.updateStatus(id, "i4fvb_provident_fund_close", "status", "驳回", null, null);
                // 更新减员表单社保状态
                employeesMaintainMapper.updateStatus(employeeId, "i4fvb_delete_employee", "gjj_status", "驳回", null, null);
            }
            // 更新员工订单状态
            employeesMaintainMapper.updateStatus(employeeOrderFormId, "i4fvb_employee_order_form",
                    "provident_fund_status", "驳回", null, null);
        }
    }

    @Override
    public List <Map<String, Object>> getAddOrDelWorkItemId(String ids, String tableName) throws Exception {
        return employeesMaintainDao.getAddOrDelWorkItemId(ids, "i4fvb_" + tableName);
    }

    @Override
    public void rejectImport(String fileName, String code, String userId,
                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);

        if (fileList != null && fileList.size() > 2) {
            Map<String, Integer> locationMap = CommonUtils.getImportLocation(Arrays.asList(fileList.get(0)));
            int sequenceNoInt = 0, returnReasonAlreadyInt = 0;
            if (locationMap.get("sequenceNo") != null && locationMap.get("sequenceNo") != 0 && locationMap.get(
                    "return_reason_already") != null && locationMap.get("return_reason_already") != 0) {
                sequenceNoInt = locationMap.get("sequenceNo") - 1;
                returnReasonAlreadyInt = locationMap.get("return_reason_already") - 1;
            } else {
                throw new RuntimeException("导入文件表头信息错误！");
            }
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            List<Map<String, Object>> dataList = new ArrayList <>();
            Map<String, Object> data = new HashMap <>();
            for (int i = 2; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                data = new HashMap <>();
                data.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
                data.put("sourceId", sourceId);
                data.put("sequenceNo", list.get(sequenceNoInt));
                data.put("returnReasonAlready", list.get(returnReasonAlreadyInt));

                dataList.add(data);
            }
            if (dataList != null && dataList.size() > 0) {
                int listSize = dataList.size();
                int toIndex = 500;
                for (int j = 0; j < dataList.size(); j += 500) {
                    if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                        toIndex = listSize - j;
                    }
                    List<Map<String, Object>> newList = dataList.subList(j, j + toIndex);
                    returnReasonAlreadyMapper.insertReturnReasonAlready(newList);
                }

                // 更新已有返回数据
                returnReasonAlreadyMapper.updateReturnReasonAlready("i4fvb_" + code, sourceId);
                // 查询需要驳回数据流程，代办信息
                List <Map <String, Object>> workItemList = returnReasonAlreadyMapper.getWorkItemInfo("i4fvb_" + code,
                        sourceId);
                if (workItemList != null && workItemList.size() > 0) {
                    List<String> employeeOrderFormIds = new ArrayList <>();
                    List<String> sequenceNos = new ArrayList <>();
                    String field = "provident_fund_status";
                    String status = "驳回";
                    if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code)) {
                        field = "social_security_status";
                    }
                    for (int i = 0; i < workItemList.size(); i++) {
                        // 当前流程节点
                        String activityCode = workItemList.get(i).get("sourceId") == null ? "" :
                                workItemList.get(i).get("sourceId").toString();
                        String workItemId = workItemList.get(i).get("id") == null ? "" :
                                workItemList.get(i).get("id").toString();
                        String sequenceStatus = workItemList.get(i).get("sequenceStatus") == null ? "" :
                                workItemList.get(i).get("sequenceStatus").toString();
                        String employeeOrderFormId = workItemList.get(i).get("employee_order_form_id") == null ? "" :
                                workItemList.get(i).get("employee_order_form_id").toString();
                        String workflowInstanceId = workItemList.get(i).get("workflowInstanceId") == null ? "" :
                                workItemList.get(i).get("workflowInstanceId").toString();
                        String sequenceNo = workItemList.get(i).get("sequenceNo") == null ? "" :
                                workItemList.get(i).get("sequenceNo").toString();
                        if ("COMPLETED".equals(sequenceStatus) && StringUtils.isNotBlank(workflowInstanceId)) {
                            // 办结，此时是申报驳回操作,需要更新订单状态
                            boolean flag = workflowInstanceFacade.activateActivity(userId, workflowInstanceId,
                                    "fill_application");
                            if (flag) {
                                employeeOrderFormIds.add(employeeOrderFormId);
                                sequenceNos.add(sequenceNo);
                            }
                        } else if ("declare".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                            // 当前节点是申报，驳回至填写申请单节点,需要更新订单状态
                            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemId, "fill_application", true);
                            if (flag) {
                                employeeOrderFormIds.add(employeeOrderFormId);
                                sequenceNos.add(sequenceNo);
                            }
                        }
                    }
                    if (employeeOrderFormIds != null && employeeOrderFormIds.size() > 0) {
                        returnReasonAlreadyMapper.updateEmployeeOrderFormStatus(employeeOrderFormIds, field, status);
                    }
                    if (sequenceNos != null && sequenceNos.size() > 0) {
                        returnReasonAlreadyMapper.updateDeclareStatus(sequenceNos, "i4fvb_" + code, status);
                    }
                }
            }

            returnReasonAlreadyMapper.deleteTempDataBySourceId(sourceId);
        }
    }

    @Override
    @Transactional
    public void updateStatus(String ids, String schemaCode, String status, String userId, BizObjectFacade bizObjectFacade,
                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(schemaCode) || StringUtils.isBlank(status)) {
            throw new RuntimeException("传参出错！{ids:" + ids + ",schemaCode:" + schemaCode + ",status:" + status + "}");
        }
        List <String> idList = Arrays.asList(ids.split(","));
        if (schemaCode.indexOf("employee") >= 0 && status.equals("取派")) {
            // 此时是增减员的取派
            cancelDispatch(idList, schemaCode, status, userId, bizObjectFacade);
        }
        if (schemaCode.indexOf("declare") >= 0) {
            // 申报流程提交操作
            declareWorkflow(idList, schemaCode, status);
        }
        if (schemaCode.indexOf("close") >= 0) {
            // 停缴流程提交操作
            closeWorkflow(idList, schemaCode, status);
        }
    }

    /**
     * 方法说明：停缴流程中提交或者驳回操作时更新订单和增减员表单状态
     * @param ids
     * @param schemaCode
     * @param status
     * @return void
     * @author liulei
     * @Date 2020/5/22 15:04
     */
    private void closeWorkflow(List<String> ids, String schemaCode, String status) throws Exception {

        List <String> orderFromIds = new ArrayList <>();
        List <String> delIds = new ArrayList <>();

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(schemaCode)) {
                SocialSecurityClose close = employeesMaintainMapper.getSocialSecurityCloseById(id);
                orderFromIds.add(close.getEmployeeOrderFormId());
                delIds.add(id);
            } else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(schemaCode)) {
                ProvidentFundClose close = employeesMaintainMapper.getProvidentFundCloseById(id);
                orderFromIds.add(close.getEmployeeOrderFormId());
                delIds.add(id);
            }
        }
        if (orderFromIds != null && orderFromIds.size() > 0) {
            employeesMaintainMapper.updateTableStatus(orderFromIds, "i4fvb_employee_order_form",
                    Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(schemaCode) ? "social_security_status" :
                            "provident_fund_status", "在办".equals(status) ? "待办" : status);
        }
        if (delIds != null && delIds.size() > 0) {
            employeesMaintainMapper.updateTableStatus(delIds, "i4fvb_delete_employee",
                    Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(schemaCode) ? "sb_status" : "gjj_status", "在办".equals(status) ? "待办" : status);
        }
    }

    /**
     * 方法说明：申报流程中提交或者驳回操作时更新订单和增减员表单状态
     * @param ids
     * @param schemaCode
     * @param status
     * @return void
     * @author liulei
     * @Date 2020/5/22 13:59
     */
    private void declareWorkflow(List <String> ids, String schemaCode, String status) throws Exception{
        List <String> orderFromIds = new ArrayList <>();
        List <String> addIds = new ArrayList <>();
        // 社保申报
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            String curStatus = "";
            String employeeName = "";
            String orderFormId = "";
            String addId = "";
            if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(schemaCode)) {
                SocialSecurityDeclare sDeclare = employeesMaintainMapper.getSocialSecurityDeclareById(id);
                curStatus = sDeclare.getStatus();
                employeeName = sDeclare.getEmployeeName();
                orderFormId = sDeclare.getEmployeeOrderFormId();
                addId = sDeclare.getAddEmployeeId();
            }else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(schemaCode)) {
                ProvidentFundDeclare pDeclare = employeesMaintainMapper.getProvidentFundDeclareById(id);
                curStatus = pDeclare.getStatus();
                employeeName = pDeclare.getEmployeeName();
                orderFormId = pDeclare.getEmployeeOrderFormId();
                addId = pDeclare.getAddEmployeeId();
            }
            if ("预点".equals(status) && !"在办".equals(curStatus)) {
                // 只有在办才可以预点
                throw new RuntimeException(employeeName + "的办理状态不是在办，无法预点！");
            }
            orderFromIds.add(orderFormId);
            addIds.add(addId);
        }

        if (orderFromIds != null && orderFromIds.size() > 0) {
            employeesMaintainMapper.updateTableStatus(orderFromIds, "i4fvb_employee_order_form",
                    Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(schemaCode) ? "social_security_status" :
                            "provident_fund_status", "在办".equals(status) ? "待办" : status);
            if ("在缴".equals(status)) {
                for (int i = 0; i < orderFromIds.size(); i++) {
                    recreateEmployeeOrderFormDetails(orderFromIds.get(i));
                }
            }
        }
        if (addIds != null && addIds.size() > 0) {
            employeesMaintainMapper.updateTableStatus(addIds, "i4fvb_add_employee",
                    Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(schemaCode) ? "sb_status" : "gjj_status", "在办".equals(status) ? "待办" : status);
        }
        if ("预点".equals(status)) {
            employeesMaintainMapper.updateTableStatus(ids, "i4fvb_" + schemaCode, "status", "预点");
        }
    }

    /**
     * 方法说明：重新生成订单的汇缴，补缴数据
     * @param orderFromId 订单id
     * @return void
     * @author liulei
     * @Date 2020/5/25 9:58
     */
    private void recreateEmployeeOrderFormDetails(String orderFromId) throws Exception{
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(orderFromId);
        // 删除订单的补缴，汇缴数据
        updateEmployeeService.delEmployeeOrderFormDetails(orderFromId, "3");
        employeesMaintainMapper.insertEmployeeOrderFormDetailsFromSb(orderFromId, "i4fvb_order_details_pay_back");
        employeesMaintainMapper.insertEmployeeOrderFormDetailsFromSb(orderFromId, "i4fvb_order_details_remittance");
        employeesMaintainMapper.insertEmployeeOrderFormDetailsFromGjj(orderFromId, "i4fvb_order_details_pay_back");
        employeesMaintainMapper.insertEmployeeOrderFormDetailsFromGjj(orderFromId, "i4fvb_order_details_remittance");

        String city = StringUtils.isNotBlank(orderForm.getSocialSecurityCity()) ? orderForm.getSocialSecurityCity() :
                orderForm.getProvidentFundCity();
        ServiceChargeUnitPrice price =
                salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                        orderForm.getBusinessType(), !AreaUtils.isAnhuiCity(city) ? "省外" : "省内", city);
        if (price != null) {
            updateEmployeeService.addEmployeeOrderFormDetails(price, orderFromId);
        }
    }

    /**
     * 方法说明：取派
     * @param ids
     * @param schemaCode
     * @return void
     * @author liulei
     * @Date 2020/5/22 10:08
     */
    private void cancelDispatch(List <String> ids, String schemaCode, String status, String userId, BizObjectFacade bizObjectFacade) throws Exception{
        List <String> successIds = new ArrayList <>();
        if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) {
            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                AddEmployee addEmployee = addEmployeeMapper.getAddEmployeeById(id);
                if (addEmployee == null) {
                    throw new RuntimeException("表单：" + id + "没有查询到对应的增员数据！");
                }
                if ("草稿".equals(addEmployee.getSbStatus()) || "草稿".equals(addEmployee.getGjjStatus())) {
                    successIds.add(id);
                    continue;
                }
                EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(id);
                if (employeeFiles == null) {
                    // 此时是草稿状态
                    successIds.add(id);
                    continue;
                }
                // 社保申报数据
                SocialSecurityDeclare sDeclare = addEmployeeMapper.getSocialSecurityDeclareByAddEmployeeId(id);
                // 公积金申报数据
                ProvidentFundDeclare pDeclare = addEmployeeMapper.getProvidentFundDeclareByAddEmployeeId(id);

                if (sDeclare == null && pDeclare == null) {
                    successIds.add(id);
                } else if (sDeclare != null && pDeclare != null) {
                    if (!sDeclare.getEmployeeFilesId().equals(pDeclare.getEmployeeFilesId())) {
                        throw new RuntimeException("表单：" + id + " 对应社保，公积金申报的员工档案不一致");
                    }
                    if (!sDeclare.getEmployeeOrderFormId().equals(pDeclare.getEmployeeOrderFormId())) {
                        throw new RuntimeException("表单：" + id + " 对应社保，公积金申报的员工订单不一致");
                    }
                    // 移除社保，公积金，员工订单的数据
                    bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, sDeclare.getId());
                    bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA, pDeclare.getId());
                    bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                            sDeclare.getEmployeeOrderFormId());
                } else if (sDeclare != null) {
                    EmployeeOrderForm orderForm =
                            addEmployeeMapper.getEmployeeOrderFormById(sDeclare.getEmployeeOrderFormId());
                    if (orderForm == null) {
                        throw new RuntimeException("表单：" + id + " 对应的员工订单没有查询到");
                    }
                    ServiceChargeUnitPrice price =
                            salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                                    orderForm.getBusinessType(),
                                    !AreaUtils.isAnhuiCity(orderForm.getSocialSecurityCity()) ? "省外" : "省内",
                                    orderForm.getSocialSecurityCity());
                    if (price != null) {
                        orderForm.setPrecollected(price.getPrecollected());
                        orderForm.setPayCycle(price.getPayCycle());
                    }
                    bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA,
                            sDeclare.getId());
                    if (StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId()) && orderForm.getProvidentFundBase() - 0d > 0d) {
                        // 员工档案没有公积金申报,即员工订单只有社保数据
                        bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                                sDeclare.getEmployeeOrderFormId());
                    } else {
                        orderForm.setSocialSecurityStatus(null);
                        orderForm.setSocialSecurityCity(null);
                        orderForm.setSWelfareHandler(null);
                        orderForm.setSocialSecurityBase(null);
                        orderForm.setSocialSecurityChargeStart(null);
                        updateEmployeeService.delEmployeeOrderFormDetails(sDeclare.getEmployeeOrderFormId(), "1");
                        addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
                        if (price != null) {
                            updateEmployeeService.addEmployeeOrderFormDetails(price, orderForm.getId());
                        }
                    }
                } else if (pDeclare != null) {
                    EmployeeOrderForm orderForm =
                            addEmployeeMapper.getEmployeeOrderFormById(pDeclare.getEmployeeOrderFormId());
                    ServiceChargeUnitPrice price =
                            salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                                    orderForm.getBusinessType(),
                                    !AreaUtils.isAnhuiCity(orderForm.getProvidentFundCity()) ? "省外" : "省内",
                                    orderForm.getProvidentFundCity());
                    if (price != null) {
                        orderForm.setPrecollected(price.getPrecollected());
                        orderForm.setPayCycle(price.getPayCycle());
                    }
                    bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA,
                            pDeclare.getId());
                    if (StringUtils.isBlank(employeeFiles.getSbAddEmployeeId()) && orderForm.getSocialSecurityBase() - 0d > 0d) {
                        // 员工档案没有社保申报,即员工订单只有公积金数据
                        bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                                sDeclare.getEmployeeOrderFormId());
                    } else {
                        orderForm.setProvidentFundStatus(null);
                        orderForm.setProvidentFundCity(null);
                        orderForm.setGWelfareHandler(null);
                        orderForm.setProvidentFundBase(null);
                        orderForm.setProvidentFundChargeStart(null);
                        updateEmployeeService.delEmployeeOrderFormDetails(sDeclare.getEmployeeOrderFormId(), "2");
                        updateEmployeeService.upateEmployeeOrderForm(orderForm);
                        if (price != null) {
                            updateEmployeeService.addEmployeeOrderFormDetails(price, orderForm.getId());
                        }
                    }
                }

                if ((StringUtils.isBlank(employeeFiles.getSbAddEmployeeId()) || id.equals(employeeFiles.getSbAddEmployeeId())) &&
                        (StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId()) || id.equals(employeeFiles.getGjjAddEmployeeId())) ){
                    // 员工档案只关联当前增员
                    bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_FILES_SCHEMA, employeeFiles.getId());
                } else if (StringUtils.isBlank(employeeFiles.getSbAddEmployeeId()) || id.equals(employeeFiles.getSbAddEmployeeId())) {
                    employeeFiles.setSbAddEmployeeId(null);
                    employeeFiles.setSocialSecurityBase(null);
                    employeeFiles.setSWelfareHandler(null);
                    employeeFiles.setSocialSecurityCity(null);
                    employeeFiles.setSocialSecurityChargeStart(null);
                    addEmployeeMapper.updateEmployeeFiles(employeeFiles);
                } else if (StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId()) || id.equals(employeeFiles.getGjjAddEmployeeId())) {
                    employeeFiles.setGjjAddEmployeeId(null);
                    employeeFiles.setProvidentFundBase(null);
                    employeeFiles.setGWelfareHandler(null);
                    employeeFiles.setProvidentFundCity(null);
                    employeeFiles.setProvidentFundChargeStart(null);
                    addEmployeeMapper.updateEmployeeFiles(employeeFiles);
                }
                successIds.add(id);
            }
            employeesMaintainMapper.updateAddOrDelEmployeeStatus(successIds, "i4fvb_add_employee", status);
        } else if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                DeleteEmployee deleteEmployee = deleteEmployeeMapper.getDeleteEmployeeById(id);
                if (deleteEmployee == null) {
                    throw new RuntimeException("表单：" + id + "没有查询到对应的减员数据！");
                }
                if ("草稿".equals(deleteEmployee.getSbStatus()) || "草稿".equals(deleteEmployee.getGjjStatus())) {
                    successIds.add(id);
                    continue;
                }
                EmployeeFiles employeeFiles = deleteEmployeeMapper.getEmployeeFilesByDelEmployeeId(id);
                if (employeeFiles == null) {
                    throw new RuntimeException("表单：" + id + "没有查询到对应的员工档案不一致！");
                }
                SocialSecurityClose sClose = deleteEmployeeMapper.getSocialSecurityCloseByDelEmployeeId(id);
                if(sClose != null && !sClose.getEmployeeFilesId().equals(employeeFiles.getId())) {
                    throw new RuntimeException("表单：" + id + "社保停缴对应的员工档案和减员对应的员工档案不一致！");
                }
                ProvidentFundClose gClose = deleteEmployeeMapper.getProvidentFundCloseByDelEmployeeId(id);
                if(gClose != null && !gClose.getEmployeeFilesId().equals(employeeFiles.getId())) {
                    throw new RuntimeException("表单：" + id + "社保停缴对应的员工档案和减员对应的员工档案不一致！");
                }
                if (sClose != null) {
                    bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_CLOSE_SCHEMA, sClose.getId());
                }
                if (gClose != null) {
                    bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_CLOSE_SCHEMA, gClose.getId());
                }
                EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
                if ((StringUtils.isBlank(employeeFiles.getSbDelEmployeeId()) || id.equals(employeeFiles.getSbDelEmployeeId())) &&
                        (StringUtils.isBlank(employeeFiles.getGjjDelEmployeeId()) || id.equals(employeeFiles.getGjjDelEmployeeId()))) {
                    // 原工档案对应减员的社保，公积金
                    employeeFiles.setReportQuitDate(null);
                    employeeFiles.setReportSeveranceOfficer(null);
                    employeeFiles.setQuitDate(null);
                    employeeFiles.setQuitReason(null);
                    employeeFiles.setQuitRemark(null);
                    if (orderForm != null) {
                        orderForm.setSocialSecurityChargeEnd(null);
                        orderForm.setProvidentFundChargeEnd(null);
                    }
                }
                if (StringUtils.isBlank(employeeFiles.getSbDelEmployeeId()) || id.equals(employeeFiles.getSbDelEmployeeId())) {
                    employeeFiles.setSbDelEmployeeId(null);
                    employeeFiles.setSocialSecurityChargeEnd(null);
                    if (orderForm != null) {
                        orderForm.setSocialSecurityChargeEnd(null);
                    }
                }
                if (StringUtils.isBlank(employeeFiles.getGjjDelEmployeeId()) || id.equals(employeeFiles.getGjjDelEmployeeId())) {
                    employeeFiles.setGjjDelEmployeeId(null);
                    employeeFiles.setProvidentFundChargeEnd(null);
                    if (orderForm != null) {
                        orderForm.setProvidentFundChargeEnd(null);
                    }
                }
                if (orderForm != null) {
                    addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
                }
                addEmployeeMapper.updateEmployeeFiles(employeeFiles);
            }
            employeesMaintainMapper.updateAddOrDelEmployeeStatus(successIds, "i4fvb_delete_employee", status);
        }
    }
}
