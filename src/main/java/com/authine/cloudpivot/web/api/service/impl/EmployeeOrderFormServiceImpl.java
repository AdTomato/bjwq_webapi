package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.EmployeeOrderFormMapper;
import com.authine.cloudpivot.web.api.service.EmployeeOrderFormService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @time 2020/6/18 16:40
 */
@Service
public class EmployeeOrderFormServiceImpl implements EmployeeOrderFormService {

    @Resource
    EmployeeOrderFormMapper employeeOrderFormMapper;

    @Override
    public int deleteOrderDetailsRemittanceSbsByParentId(List<String> parentId) {
        return employeeOrderFormMapper.deleteOrderDetailsRemittanceSbsByParentId(parentId);
    }

    @Override
    public int deleteOrderDetailsRemittanceSbByParentId(String parentId) {
        return employeeOrderFormMapper.deleteOrderDetailsRemittanceSbByParentId(parentId);
    }

    @Override
    public int deleteOrderDetailsPayBackSbsByParentId(List<String> parentId) {
        return employeeOrderFormMapper.deleteOrderDetailsPayBackSbsByParentId(parentId);
    }

    @Override
    public int deleteOrderDetailsPayBackSbByParentId(String parentId) {
        return employeeOrderFormMapper.deleteOrderDetailsPayBackSbByParentId(parentId);
    }

    @Override
    public int deleteOrderDetailsRemittancesById(List<String> socialSecurityDeclareId) {
        return employeeOrderFormMapper.deleteOrderDetailsRemittancesById(socialSecurityDeclareId);
    }

    @Override
    public int deleteOrderDetailsRemittanceById(String socialSecurityDeclareId) {
        return employeeOrderFormMapper.deleteOrderDetailsRemittanceById(socialSecurityDeclareId);
    }

    @Override
    public int deleteOrderDetailsPayBacksById(List<String> id) {
        return employeeOrderFormMapper.deleteOrderDetailsPayBacksById(id);
    }

    @Override
    public int deleteOrderDetailsPayBackById(String id) {
        return employeeOrderFormMapper.deleteOrderDetailsPayBackById(id);
    }

    @Override
    public boolean isHaveSocialSecurityInOrderForm(String id) {
        return employeeOrderFormMapper.isHaveSocialSecurityInOrderForm(id) != 0 ? true : false;
    }

    @Override
    public boolean isHaveSocialSecurityInOrderFormByIdCard(String idCard, String firstLevelName, String secondLevelName) {
        return employeeOrderFormMapper.isHaveSocialSecurityInOrderFormByIdCard(idCard, firstLevelName, secondLevelName) == 0 ? false : true;
    }

    @Override
    public boolean isHaveProvidentFundInOrderForm(String id) {
        return employeeOrderFormMapper.isHaveProvidentFundInOrderForm(id) != 0 ? true : false;
    }

    @Override
    public boolean isHaveProvidentFundInOrderFormByIdCard(String idCard, String firstLevelName, String secondLevelName) {
        return employeeOrderFormMapper.isHaveProvidentFundInOrderFormByIdCard(idCard, firstLevelName, secondLevelName) == 0 ? false : true;
    }

    @Override
    public List<Map<String, String>> getOrderFormIdBySocialSecurityDeclareId(List<String> socialSecurityDeclareId) {
        return employeeOrderFormMapper.getOrderFormIdBySocialSecurityDeclareId(socialSecurityDeclareId);
    }
}
