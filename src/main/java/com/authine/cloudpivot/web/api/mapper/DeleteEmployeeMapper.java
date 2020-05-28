package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;

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

    EmployeeOrderFormDetails getGjjEmployeeOrderFormDetails(String parentId);

    EmployeeFiles getEmployeeFilesByDelEmployeeId(String delEmployeeId);

    SocialSecurityClose getSocialSecurityCloseByDelEmployeeId(String delEmployeeId);

    ProvidentFundClose getProvidentFundCloseByDelEmployeeId(String delEmployeeId);

    void updateDeleteEmployee(DeleteEmployee del);

    void updateShDeleteEmployee(ShDeleteEmployee del);

    void updateQgDeleteEmployee(NationwideDispatch del);
}
