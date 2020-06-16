package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.DeleteEmployeeMapper
 * @Date 2020/5/15 9:00
 **/
public interface DeleteEmployeeMapper {
    DeleteEmployee getDeleteEmployeeById(String id);

    ShDeleteEmployee getShDeleteEmployeeById(String id);

    NationwideDispatch getQgDeleteEmployeeById(String id);

    void updateReturnReason(String id, String returnReason, String tableName);

    void addSocialSecurityClose(SocialSecurityClose close);

    EmployeeOrderFormDetails getGjjEmployeeOrderFormDetails(String parentId);

    void addProvidentFundClose(ProvidentFundClose close);

    void updateEmployeeOrderFromChargeEndTime(EmployeeOrderForm orderForm);

    void updateDeleteEmployee(DeleteEmployee del);

    void updateShDeleteEmployee(ShDeleteEmployee del);

    void updateQgDeleteEmployee(NationwideDispatch del);

    DeleteEmployee getUpdateDelEmployeeById(String id);

    ShDeleteEmployee getUpdateShDelEmployeeById(String id);

    NationwideDispatch getUpdateQgDelEmployeeById(String id);

    EmployeeFiles getEmployeeFilesByDelEmployeeId(String id);

    void updateDeleteEmployeeByUpdate(DeleteEmployee update);

    void updateShDeleteEmployeeByUpdate(ShDeleteEmployee update);

    void updateQgDeleteEmployeeByUpdate(NationwideDispatch update);

    SocialSecurityClose getSocialSecurityCloseByDelEmployeeId(String delEmployeeId);

    ProvidentFundClose getProvidentFundCloseByDelEmployeeId(String delEmployeeId);

    void deleteCloseDataByDelEmployeeId(String delEmployeeId, String tableName);

    void deleteBatchEvacuationByDeleteEmployeeId(String delEmployeeId);

    List <SocialSecurityClose> listSocialSecurityClose(List<String> ids);

    List<ProvidentFundClose> listProvidentFundClose(List<String> ids);

    void updateEmployeeOrderFromStatus(String employeeOrderFormId, String field, String status);

    void updateDeleteEmployeeStatus(String delEmployeeId, String field, String status, String returnReason);
}
