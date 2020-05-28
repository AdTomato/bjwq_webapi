package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.List;

/**
 * @author liulei
 * @Description 增减员变更 Service
 * @ClassName com.authine.cloudpivot.web.api.service.UpdateEmployeeService
 * @Date 2020/3/13 16:27
 **/
public interface UpdateEmployeeService {

    /**
     * 方法说明：获取增员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    AddEmployee getAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取增员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShAddEmployee getShAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取增员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    DeleteEmployee getDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShDeleteEmployee getShDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取员工档案修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    EmployeeFiles getEmployeeFilesUpdateById(String id) throws Exception;

    void upateEmployeeOrderForm(EmployeeOrderForm orderForm) throws Exception;

    /**
     * 方法说明：删除员工订单的子表数据
     * @param parentId 员工订单的id
     * @param type 删除子表数据类型（1：社保数据，2：公积金数据，3：社保公积金数据）
     * @return void
     * @author liulei
     * @Date 2020/5/19 14:54
     */
    void delEmployeeOrderFormDetails(String parentId, String type) throws Exception;

    void addEmployeeOrderFormDetails(String parentId, List <EmployeeOrderFormDetails> payBackList,
                                     List <EmployeeOrderFormDetails> remittanceList) throws Exception;

    void addEmployeeOrderFormDetails(ServiceChargeUnitPrice price, String id) throws Exception;

    void updateSocialSecurityDeclare(SocialSecurityDeclare newSDeclare, SocialSecurityDeclare sDeclare) throws Exception;

    void updateProvidentFundDeclare(ProvidentFundDeclare newPDeclare, ProvidentFundDeclare pDeclare) throws Exception;

    void updateDeleteEmployee(DeleteEmployee delUpdate) throws Exception;

    void updateShDeleteEmployee(ShDeleteEmployee delUpdate) throws Exception;

    void updateQgDeleteEmployee(NationwideDispatch delUpdate) throws Exception;

    EmployeeFiles getEmployeeFilesById(String employeeFilesId) throws Exception;

    void updateAddEmployee(AddEmployee updateAddEmployee) throws Exception;

    void updateShAddEmployee(ShAddEmployee updateAddEmployee) throws Exception;

    void updateQgAddEmployee(NationwideDispatch updateAddEmployee) throws Exception;

    void updateSocialSecurityClose(SocialSecurityClose sClose) throws Exception;

    void updateProvidentFundClose(ProvidentFundClose sClose) throws Exception;

}
