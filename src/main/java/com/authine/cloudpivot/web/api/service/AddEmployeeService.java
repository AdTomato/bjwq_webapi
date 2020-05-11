package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.AddEmployeeService
 * @Date 2020/2/28 14:33
 **/
public interface AddEmployeeService {
    /**
     * 方法说明：根据id获取增员_客户实体
     * @param id
     * @author liulei
     * @Date 2020/2/28 10:10
     */
    AddEmployee getAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_上海实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    ShAddEmployee getShAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_全国实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    NationwideDispatch getNationwideDispatchById(String id) throws Exception;

    /**
     * 方法说明：根据客户名称，证件号码获取社保申报实体
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
     * @author liulei
     * @Date 2020/2/28 15:01
     */
    SocialSecurityDeclare getSocialSecurityDeclareByClientNameAndIdentityNo(String clientName, String identityNo) throws Exception;

    /**
     * 方法说明：根据客户名称，证件号码获取公积金申报实体
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @return com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare
     * @author liulei
     * @Date 2020/2/28 15:03
     */
    ProvidentFundDeclare getProvidentFundDeclareByClientNameAndIdentityNo(String clientName, String identityNo) throws Exception;

    /**
     * 方法说明：根据id获取员工订单实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/2/28 15:03
     */
    EmployeeOrderForm getEmployeeOrderFormById(String id) throws Exception;

    EmployeeOrderForm getEmployeeOrderFormByEmployeeFilesId(String id) throws Exception;

    SocialSecurityDeclare getSocialSecurityDeclareByOrderFormId(String id) throws Exception;

    ProvidentFundDeclare getProvidentFundDeclareByOrderFormId(String id) throws Exception;

    SocialSecurityClose getSocialSecurityCloseByOrderFormId(String id) throws Exception;

    ProvidentFundClose getProvidentFundCloseByOrderFormId(String id) throws Exception;
}
