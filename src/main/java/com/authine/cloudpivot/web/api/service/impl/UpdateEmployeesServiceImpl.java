package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.UpdateEmployeesMapper;
import com.authine.cloudpivot.web.api.service.UpdateEmployeesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 增减员变更 Service实现
 * @ClassName com.authine.cloudpivot.web.api.service.impl.UpdateEmployeeServiceImpl
 * @Date 2020/3/13 16:28
 **/
@Service
public class UpdateEmployeesServiceImpl implements UpdateEmployeesService {
    @Resource
    UpdateEmployeesMapper updateEmployeeMapper;

    @Override
    public AddEmployee getAddEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getAddEmployeeUpdateById(id);
    }

    @Override
    public ShAddEmployee getShAddEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getShAddEmployeeUpdateById(id);
    }

    @Override
    public NationwideDispatch getQgAddEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getQgAddEmployeeUpdateById(id);
    }

    @Override
    public DeleteEmployee getDeleteEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getDeleteEmployeeUpdateById(id);
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getShDeleteEmployeeUpdateById(id);
    }

    @Override
    public NationwideDispatch getQgDeleteEmployeeUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getQgDeleteEmployeeUpdateById(id);
    }

    @Override
    public EmployeeFiles getEmployeeFilesUpdateById(String id) throws Exception {
        return updateEmployeeMapper.getEmployeeFilesUpdateById(id);
    }
}
