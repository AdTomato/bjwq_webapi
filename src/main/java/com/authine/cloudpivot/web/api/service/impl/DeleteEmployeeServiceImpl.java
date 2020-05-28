package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.DeleteEmployeeMapper;
import com.authine.cloudpivot.web.api.service.DeleteEmployeeService;
import com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liulei
 * @Description 减员serviceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.DeleteEmployeeServiceImpl
 * @Date 2020/5/15 8:56
 **/
@Service
public class DeleteEmployeeServiceImpl implements DeleteEmployeeService {

    @Resource
    private DeleteEmployeeMapper deleteEmployeeMapper;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Override
    public DeleteEmployee getDeleteEmployeeById(String id) throws Exception {
        DeleteEmployee delEmployee = deleteEmployeeMapper.getDeleteEmployeeById(id);
        if (delEmployee == null) {
            throw new RuntimeException("没有获取到减员数据！");
        }
        return delEmployee;
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeById(String id) throws Exception {
        ShDeleteEmployee delEmployee = deleteEmployeeMapper.getShDeleteEmployeeById(id);
        if (delEmployee == null) {
            throw new RuntimeException("没有获取到减员数据！");
        }
        return delEmployee;
    }

    @Override
    public NationwideDispatch getQgDeleteEmployeeById(String id) throws Exception {
        NationwideDispatch delEmployee = deleteEmployeeMapper.getQgDeleteEmployeeById(id);
        if (delEmployee == null) {
            throw new RuntimeException("没有获取到减员数据！");
        }
        return delEmployee;
    }

    @Override
    public void createDeleteEmployeeData(DeleteEmployee delEmployee, EmployeeFiles employeeFiles,
                                         BizObjectFacade bizObjectFacade,
                                         WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 员工订单实体
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
        if (orderForm != null) {
            if (delEmployee.getSocialSecurityEndTime() != null && employeeFiles.getSocialSecurityBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getSocialSecurityCity()) >= 0) {
                // 有社保停缴
                createSocialSecurityClose(delEmployee, employeeFiles, orderForm.getId(), bizObjectFacade,
                        workflowInstanceFacade);
                orderForm.setSocialSecurityChargeEnd(delEmployee.getSocialSecurityEndTime());
            }

            if (delEmployee.getProvidentFundEndTime() != null && employeeFiles.getProvidentFundBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getProvidentFundCity()) >= 0) {
                // 有公积金停缴
                createProvidentFundClose(delEmployee, employeeFiles, orderForm.getId(), bizObjectFacade,
                        workflowInstanceFacade);
                orderForm.setProvidentFundChargeEnd(delEmployee.getProvidentFundEndTime());
            }
            addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
        } else {
            throw new RuntimeException("没有查询到员工订单信息！");
        }
    }

    @Override
    public void createSocialSecurityClose(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId,
                                          BizObjectFacade bizObjectFacade,
                                          WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        SocialSecurityClose close = new SocialSecurityClose(delEmployee, employeeFiles, orderFormId);
        String handler = addEmployeeMapper.getHsLevyHandler(close.getCity(), close.getWelfareHandler(),
                "社保", close.getSecondLevelClientName());
        close.setOperateLeader(handler);
        BizObjectModel model = GetBizObjectModelUntils.getSocialSecurityClose(close);
        String id = bizObjectFacade.saveBizObjectModel(delEmployee.getCreater(), model, "id");
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(delEmployee.getCreatedDeptId(),
                delEmployee.getCreater(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA_WF, id, true);
        System.out.println("创建社保停缴业务对象成功：" + id + "; 启动社保停缴流程成功:" + modelWfId);
    }

    @Override
    public void createProvidentFundClose(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId,
                                          BizObjectFacade bizObjectFacade,
                                          WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        ProvidentFundClose close = new ProvidentFundClose(delEmployee, employeeFiles, orderFormId);
        // 查询公积金的企业，个人，合计存缴额
        EmployeeOrderFormDetails details = deleteEmployeeMapper.getGjjEmployeeOrderFormDetails(orderFormId);
        if (details != null) {
            close.setPersonalDeposit(details.getEmployeeMoney());
            close.setEnterpriseDeposit(details.getCompanyMoney());
            close.setTotalDeposit(details.getSum());
        }
        String handler = addEmployeeMapper.getHsLevyHandler(close.getCity(), close.getWelfareHandler(),
                "公积金", close.getSecondLevelClientName());
        close.setOperateLeader(handler);
        BizObjectModel model =  GetBizObjectModelUntils.getProvidentFundClose(close);
        String id = bizObjectFacade.saveBizObjectModel(delEmployee.getCreater(), model, "id");
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(delEmployee.getCreatedDeptId(),
                delEmployee.getCreater(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA_WF, id, true);
        System.out.println("创建公积金停缴业务对象成功：" + id + "; 启动公积金停缴流程成功:" + modelWfId);
    }

    @Override
    public void updateDeleteEmployee(DeleteEmployee del) throws Exception {
        deleteEmployeeMapper.updateDeleteEmployee(del);
    }

    @Override
    public void updateShDeleteEmployee(ShDeleteEmployee del) throws Exception {
        deleteEmployeeMapper.updateShDeleteEmployee(del);
    }

    @Override
    public void updateQgDeleteEmployee(NationwideDispatch del) throws Exception {
        deleteEmployeeMapper.updateQgDeleteEmployee(del);
    }

    @Override
    public EmployeeFiles getEmployeeFilesByDelEmployeeId(String delEmployeeId) throws Exception {
        EmployeeFiles employeeFiles = deleteEmployeeMapper.getEmployeeFilesByDelEmployeeId(delEmployeeId);
        if (employeeFiles == null) {
            throw new RuntimeException("根据减员表单id,没有查询到对应的员工档案信息！");
        }
        return employeeFiles;
    }

    @Override
    public SocialSecurityClose getSocialSecurityCloseByDelEmployeeId(String id) throws Exception {
        return deleteEmployeeMapper.getSocialSecurityCloseByDelEmployeeId(id);
    }

    @Override
    public ProvidentFundClose getProvidentFundCloseByDelEmployeeId(String id) throws Exception {
        return deleteEmployeeMapper.getProvidentFundCloseByDelEmployeeId(id);
    }
}
