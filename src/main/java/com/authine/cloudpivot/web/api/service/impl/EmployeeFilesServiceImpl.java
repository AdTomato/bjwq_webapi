package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 09:54
 * @Description: 员工档案service
 */
@Service
public class EmployeeFilesServiceImpl implements EmployeeFilesService {

    @Resource
    EmployeeFilesMapper employeeFilesMapper;

    /**
     * @param idNo     : 身份证
     * @param clientName : 客户名称
     * @return : com.authine.cloudpivot.web.api.entity.EmployeeFilesDto
     * @Author: wangyong
     * @Date: 2020/2/17 9:56
     * @Description: 根据员工身份证，客户名称查询员工档案
     */
    @Override
    public EmployeeFiles getEmployeeFilesByIdNoOrClientName(@NotNull String idNo, String clientName) {
        return employeeFilesMapper.getEmployeeFilesByIdNoOrClientName(idNo, clientName);
    }

    @Override
    public AddEmployee getAddEmployeeData(String id) {
        return employeeFilesMapper.getAddEmployeeData(id);
    }

    @Override
    public AddEmployee getAddEmployeeUpdateData(String id) {
        return employeeFilesMapper.getAddEmployeeUpdateData(id);
    }

    @Override
    public DeleteEmployee getDeleteEmployeeData(String id) {
        return employeeFilesMapper.getDeleteEmployeeData(id);
    }

    @Override
    public DeleteEmployee getDeleteEmployeeUpdateData(String id) {
        return employeeFilesMapper.getDeleteEmployeeUpdateData(id);
    }

    @Override
    public ShAddEmployee getShAddEmployeeData(String id) {
        return employeeFilesMapper.getShAddEmployeeData(id);
    }

    @Override
    public ShAddEmployee getShAddEmployeeUpdateData(String id) {
        return employeeFilesMapper.getShAddEmployeeUpdateData(id);
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeData(String id) {
        return employeeFilesMapper.getShDeleteEmployeeData(id);
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeUpdateData(String id) {
        return employeeFilesMapper.getShDeleteEmployeeUpdateData(id);
    }

    @Override
    public NationwideDispatch getNationwideAddEmployeeData(String id) {
        return employeeFilesMapper.getNationwideAddEmployeeData(id);
    }

    @Override
    public NationwideDispatch getNationwideAddEmployeeUpdateData(String id) {
        return employeeFilesMapper.getNationwideAddEmployeeUpdateData(id);
    }

    @Override
    public NationwideDispatch getNationwideDeleteEmployeeData(String id) {
        return employeeFilesMapper.getNationwideDeleteEmployeeData(id);
    }

    @Override
    public NationwideDispatch getNationwideDeleteEmployeeUpdateData(String id) {
        return employeeFilesMapper.getNationwideDeleteEmployeeUpdateData(id);
    }

    @Override
    public EmployeeFiles getEmployeeFilesData(String id) {
        return employeeFilesMapper.getEmployeeFilesData(id);
    }

    @Override
    public EmployeeFiles getEmployeeFilesUpdateData(String id) {
        return employeeFilesMapper.getEmployeeFilesUpdateData(id);
    }

    @Override
    public void addEmployeeUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.addEmployeeUpdateDetail(data);
    }

    @Override
    public void shAddEmployeeUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.shAddEmployeeUpdateDetail(data);
    }

    @Override
    public void nationAddUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.nationAddUpdateDetail(data);
    }

    @Override
    public void delEmployeeUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.delEmployeeUpdateDetail(data);
    }

    @Override
    public void shDelEmployeeUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.shDelEmployeeUpdateDetail(data);
    }

    @Override
    public void nationDelUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.nationDelUpdateDetail(data);
    }

    @Override
    public void employeeFilesUpdateDetail(List<ChangeValue> data) {
        employeeFilesMapper.employeeFilesUpdateDetail(data);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/18 9:32
     * @param clientName: 客户名称
     * @param employeeNature: 员工性质
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.EmployeeFilesDto>
     * @Description: 获取该客户名称下面该员工性质的员工档案
     */
    @Override
    public List<EmployeeFilesDto> getEmployeeFilesCanGenerateBillByClientName(String clientName, String employeeNature) {
        return employeeFilesMapper.getEmployeeFilesCanGenerateBillByClientName(clientName, employeeNature);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/18 9:33
     * @param id: 员工档案id
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto>
     * @Description: 根据员工档案id获取该员工的订单信息
     */
    @Override
    public List<EmployeeOrderFormDto> getEmployeeOrderFormByEmployeeFilesId(String id) {
        return employeeFilesMapper.getEmployeeOrderFormByEmployeeFilesId(id);
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/18 9:34
     * @param id: 根据员工订单id获取员工订单信息
     * @return: java.util.List<com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail>
     * @Description: 员工订单详情
     */
    @Override
    public List<SocialSecurityFundDetail> getSocialSecurityFundDetailByParentId(String id) {
        return employeeFilesMapper.getSocialSecurityFundDetailByParentId(id);
    }
}
