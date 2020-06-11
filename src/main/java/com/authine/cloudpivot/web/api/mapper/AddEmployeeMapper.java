package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: 刘磊
 * @Date: 2020-03-13 10:07
 * @Description: 增员修改使用接口
 */
@Mapper
public interface AddEmployeeMapper {
    /**
     * 方法说明：根据id获取员工订单实体
     *
     * @param id 主键id
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/3/13 10:16
     */
    EmployeeOrderForm getEmployeeOrderFormById(String id);

    void updateEmployeeFiles(EmployeeFiles employeeFiles);

    EmployeeOrderForm getEmployeeOrderFormByEmployeeFilesId(String id);

    void addEmployeeImportData(List<Map<String, Object>> list);

    void deleteEmployeeImportData(List<Map<String, Object>> list);

    /**
     * 方法说明：根据id查询增员_客户数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    AddEmployee getAddEmployeeById(String id);

    /**
     * 方法说明：根据id查询增员_上海数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    ShAddEmployee getShAddEmployeeById(String id);

    /**
     * 方法说明：根据id查询增员_全国数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    NationwideDispatch getQgAddEmployeeById(String id);

    void createEmployeeOrderFormDetails(List<EmployeeOrderFormDetails> list, String parentId, String tableName);

    String getHsLevyHandler(String city, String welfareHandler, String businessType, String clientName);

    EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName, String secondLevelClientName,
                                                            String identityNo);

    void updateEmployeeOrderFrom(EmployeeOrderForm orderForm);

    SocialSecurityDeclare getSocialSecurityDeclareByAddEmployeeId(String addEmployeeId);

    ProvidentFundDeclare getProvidentFundDeclareByAddEmployeeId(String addEmployeeId);

    EmployeeFiles getEmployeeFilesByAddEmployeeId(String id);

    void updateAddEmployee(AddEmployee addEmployee);

    void updateShAddEmployee(ShAddEmployee shAddEmployee);

    void updateQgAddEmployee(NationwideDispatch nationwideDispatch);

    void delOrderFormServiceChargeUnitPrice(String parentId, String tableName);

    void updateWorkflowInstanceId(String workflowInstanceId, String id, String tableName);

    String getWorkItemIdByInstanceIdAndUserId(String instanceId, String userId);

    /**
     * 根据客户名称以及证件号码查询增员客户
     *
     * @param id               数据id
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param identityNo       证件号码
     * @return 增员客户数据
     * @author wangyong
     */
    List<AddEmployee> getAddEmployeeByClientNameAndIdCard(String id, String firstClientName, String secondClientName, String identityNo);
}
