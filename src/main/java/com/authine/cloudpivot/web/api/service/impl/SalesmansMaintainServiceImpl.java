package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.web.api.entity.SalesmansMaintain;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.SalesmansMaintainMapper;
import com.authine.cloudpivot.web.api.service.SalesmansMaintainService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyong
 * @Date:2020/4/15 15:08
 * @Description:
 */
@Service
public class SalesmansMaintainServiceImpl implements SalesmansMaintainService {

    @Resource
    SalesmansMaintainMapper salesmansMaintainMapper;

    /**
     * 获取业务员及其所属部门
     *
     * @param firstLevelClientName  一级客户名称
     * @param secondLevelClientName 二级客户名称
     * @param businessType          业务类型
     * @param welfare               福利地
     * @param welfareOperator       福利办理方
     * @return 业务员及其所属部门
     * @author wangyong
     */
    @Override
    public Map<String, String> getSalesmansAndDepartmentS(String firstLevelClientName, String secondLevelClientName, String businessType, String welfare, String welfareOperator) {
        SalesmansMaintain salesmansAndDepartment = salesmansMaintainMapper.getSalesmansAndDepartment(firstLevelClientName, secondLevelClientName, businessType, welfare, welfareOperator);
        if (salesmansAndDepartment == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        result.put("salesman", salesmansAndDepartment.getSalesman());
        result.put("department", salesmansAndDepartment.getDepartment());
        return result;
    }

    /**
     * 获取业务员及其所属部门
     *
     * @param firstLevelClientName  一级客户名称
     * @param secondLevelClientName 二级客户名称
     * @param businessType          业务类型
     * @param welfare               福利地
     * @param welfareOperator       福利办理方
     * @return 业务员及其所属部门
     * @author wangyong
     */
    @Override
    public Map<String, Unit> getSalesmansAndDepartmentU(String firstLevelClientName, String secondLevelClientName, String businessType, String welfare, String welfareOperator) {
        SalesmansMaintain salesmansAndDepartment = salesmansMaintainMapper.getSalesmansAndDepartment(firstLevelClientName, secondLevelClientName, businessType, welfare, welfareOperator);
        if (salesmansAndDepartment == null) {
            return null;
        }
        Map<String, Unit> result = new HashMap<>();
        String salesman = salesmansAndDepartment.getSalesman();
        Unit salesmanUnit = null;
        if (salesman != null) {
            salesmanUnit = JSON.parseObject(salesman, new TypeReference<ArrayList<Unit>>() {
            }).get(0);
        }
        String department = salesmansAndDepartment.getDepartment();
        Unit departmentUnit = null;
        if (Strings.isEmpty(department)) {
            departmentUnit = JSON.parseObject(department, new TypeReference<ArrayList<Unit>>() {
            }).get(0);
        }
        result.put("salesman", salesmanUnit);
        result.put("department", departmentUnit);
        return result;
    }
}
