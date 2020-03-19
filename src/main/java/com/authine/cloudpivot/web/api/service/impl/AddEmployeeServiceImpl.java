package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.service.AddEmployeeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.AddEmployeeServiceImpl
 * @Date 2020/2/28 14:33
 **/
@Service
public class AddEmployeeServiceImpl implements AddEmployeeService {

    @Resource
    private EmployeeFilesMapper employeeFilesMapper;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Override
    public AddEmployee getAddEmployeeById(String id) throws Exception {
        return employeeFilesMapper.getAddEmployeeData(id);
    }

    @Override
    public ShAddEmployee getShAddEmployeeById(String id) throws Exception {
        return employeeFilesMapper.getShAddEmployeeData(id);
    }

    @Override
    public NationwideDispatch getNationwideDispatchById(String id) throws Exception {
        return employeeFilesMapper.getNationwideAddEmployeeData(id);
    }

    @Override
    public SocialSecurityDeclare getSocialSecurityDeclareByClientNameAndIdentityNo(String clientName,
                                                                                   String identityNo) throws Exception {
        return addEmployeeMapper.getSocialSecurityDeclareByClientNameAndIdentityNo(clientName, identityNo);
    }

    @Override
    public ProvidentFundDeclare getProvidentFundDeclareByClientNameAndIdentityNo(String clientName,
                                                                                 String identityNo) throws Exception {
        return addEmployeeMapper.getProvidentFundDeclareByClientNameAndIdentityNo(clientName, identityNo);
    }

    @Override
    public EmployeeOrderForm getEmployeeOrderFormById(String id) throws Exception {
        return addEmployeeMapper.getEmployeeOrderFormById(id);
    }
}
