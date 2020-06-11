package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.authine.cloudpivot.web.api.dto.AddEmployeeDto;
import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.mapper.UnitMapper;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.utils.UnitUtils;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 09:54
 * @Description: 员工档案service
 */
@Service
@Slf4j
public class EmployeeFilesServiceImpl implements EmployeeFilesService {

    @Resource
    EmployeeFilesMapper employeeFilesMapper;

    @Resource
    UnitMapper unitMapper;

    /**
     * @param idNo       : 身份证
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
    public AddEmployeeDto getAddEmployeeDtoData(String id) {
        AddEmployeeDto addEmployeeDtoData = employeeFilesMapper.getAddEmployeeDtoData(id);
        String operator = addEmployeeDtoData.getOperator();  // 操作人
        List<String> ids = new ArrayList<>();
        if (!StringUtils.isEmpty(operator)) {
            ids.addAll(UnitUtils.getUnitIds(operator));
            if (!ids.isEmpty()) {
                addEmployeeDtoData.setOperatorList(unitMapper.getOrgUnitByIds(ids));
            }
        }
        String inquirer = addEmployeeDtoData.getInquirer();  // 查询人
        ids.clear();
        if (!StringUtils.isEmpty(inquirer)) {
            ids.addAll(UnitUtils.getUnitIds(inquirer));
            if (!ids.isEmpty()) {
                addEmployeeDtoData.setInquirerList(unitMapper.getOrgUnitByIds(ids));
            }
        }

        String subordinateDepartment = addEmployeeDtoData.getSubordinateDepartment();  // 所属部门
        ids.clear();
        if (!StringUtils.isEmpty(subordinateDepartment)) {
            ids.addAll(UnitUtils.getUnitIds(subordinateDepartment));
            if (!ids.isEmpty()) {
                addEmployeeDtoData.setSubordinateDepartmentList(unitMapper.getDeptUnitByIds(ids));
            }
        }
        return employeeFilesMapper.getAddEmployeeDtoData(id);
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
     * @param clientName:     客户名称
     * @param employeeNature: 员工性质
     * @Author: wangyong
     * @Date: 2020/3/18 9:32
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.EmployeeFilesDto>
     * @Description: 获取该客户名称下面该员工性质的员工档案
     */
    @Override
    public List<EmployeeFilesDto> getEmployeeFilesCanGenerateBillByClientName(String clientName, String employeeNature) {
        return employeeFilesMapper.getEmployeeFilesCanGenerateBillByClientName(clientName, employeeNature);
    }

    /**
     * @param id: 员工档案id
     * @Author: wangyong
     * @Date: 2020/3/18 9:33
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto>
     * @Description: 根据员工档案id获取该员工的订单信息
     */
    @Override
    public List<EmployeeOrderFormDto> getEmployeeOrderFormByEmployeeFilesId(String id) {
        return employeeFilesMapper.getEmployeeOrderFormByEmployeeFilesId(id);
    }

    /**
     * @param id: 根据员工订单id获取员工订单信息
     * @Author: wangyong
     * @Date: 2020/3/18 9:34
     * @return: java.util.List<com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail>
     * @Description: 员工订单详情
     */
    @Override
    public List<SocialSecurityFundDetail> getSocialSecurityFundDetailByParentId(String id) {
        return employeeFilesMapper.getSocialSecurityFundDetailByParentId(id);
    }

    /**
     * @param idNo: 证件号
     * @Author: wangyong
     * @Date: 2020/4/1 13:32
     * @return: java.util.List<com.authine.cloudpivot.web.api.entity.Bill>
     * @Description: 根据员工证件号获取该员工没有对比的账单
     */
    @Override
    public List<Bill> getNoCompareBills(String idNo) {
        return employeeFilesMapper.getNoCompareBills(idNo);
    }

    /**
     * @param billYear: 账单年月
     * @param idNo:     证件号码
     * @Author: wangyong
     * @Date: 2020/4/2 13:22
     * @return: com.authine.cloudpivot.web.api.entity.PayrollBill
     * @Description:
     */
    @Override
    public PayrollBill getPayrollBill(String billYear, String idNo) {
        List<PayrollBill> payrollBills = employeeFilesMapper.getPayrollBills(billYear, idNo);
        return payrollBills == null || payrollBills.size() == 0 ? null : payrollBills.get(0);
    }

    /**
     * @param employeeFilesDto:
     * @Author: wangyong
     * @Date: 2020/4/8 11:26
     * @return: void
     * @Description: 更新员工档案
     */
    @Override
    public void updateEmployee(List<EmployeeFilesDto> employeeFilesDto) {
        employeeFilesMapper.updateEmployee(employeeFilesDto);
    }

    @Override
    public void insertBills(List<Bill> bills) {
        if (bills != null && bills.size() > 0)
            employeeFilesMapper.insertBills(bills);
    }

    @Override
    public EmployeeFiles getEmployeeFilesByIdentityNo(String identityNo) throws Exception {
        return employeeFilesMapper.getEmployeeFilesByIdentityNo(identityNo);
    }
}
