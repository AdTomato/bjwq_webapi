package com.authine.cloudpivot.web.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.web.api.entity.QueryInfo;
import com.authine.cloudpivot.web.api.entity.SalesmansMaintain;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.SalesmansMaintainMapper;
import com.authine.cloudpivot.web.api.service.SalesmansMaintainService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public QueryInfo getOperatorAndInquirer(QueryInfo queryInfo, String sCity, String sWelfareHandler, String gCity,
                                            String gWelfareHandler, String businessType, String firstLevelClientName,
                                            String secondLevelClientName) throws Exception {
        if (StringUtils.isNotBlank(sCity)) {
            SalesmansMaintain sMaintain = this.getSalesmansMaintain(firstLevelClientName, secondLevelClientName,
                    businessType, sCity, sWelfareHandler);
            queryInfo.setOperator(sMaintain.getOperator());
            queryInfo.setInquirer(sMaintain.getInquirer());
            queryInfo.setSubordinateDepartment(sMaintain.getSubordinateDepartment());
        } else if (StringUtils.isNotBlank(gCity)) {
            SalesmansMaintain gMaintain = salesmansMaintainMapper.getSalesmansMaintain(firstLevelClientName,
                    secondLevelClientName, businessType, gCity, gWelfareHandler);
            queryInfo.setOperator(gMaintain.getOperator());
            queryInfo.setInquirer(gMaintain.getInquirer());
            queryInfo.setSubordinateDepartment(gMaintain.getSubordinateDepartment());
        } else {
            throw new RuntimeException("福利地为空;");
        }
        return queryInfo;
    }

    private String getAllInfo(String jsonStr1, String jsonStr2) {
        List <Unit> jsonStrList1 = JSON.parseObject(jsonStr1, new TypeReference <ArrayList <Unit>>() {
        });
        List <Unit> jsonStrList2 = JSON.parseObject(jsonStr2, new TypeReference <ArrayList <Unit>>() {
        });
        for (int i = 0; i < jsonStrList2.size(); i++) {
            Unit operator = jsonStrList2.get(i);
            if (jsonStrList1.indexOf(operator.getId()) < 0) {
                jsonStrList1.add(operator);
            }
        }
        return JSONArray.toJSONString(jsonStrList1);
    }

    @Override
    public SalesmansMaintain getSalesmansMaintain(String firstLevelClientName, String secondLevelClientName,
                                                  String businessType, String city, String welfareHandler) throws Exception {
        SalesmansMaintain maintain = salesmansMaintainMapper.getSalesmansMaintain(firstLevelClientName,
                secondLevelClientName, businessType, city, welfareHandler);
        if (maintain == null) {
            throw new RuntimeException("没有查询到操作人，查询人信息（查询条件：一级客户名称：" +
                    firstLevelClientName + ";二级客户名称：" + secondLevelClientName + ";业务类型:" + businessType +
                    ";福利地:" + city + "福利办理方;" + welfareHandler + "）;");
        }
        return maintain;
    }
}
