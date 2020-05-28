package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.BusinessInsuranceMapper;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.service.BusinessInsuranceService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.BusinessInsuranceServiceImpl
 * @Date 2020/3/9 10:23
 **/
@Service
public class BusinessInsuranceServiceImpl implements BusinessInsuranceService {

    @Resource
    BusinessInsuranceMapper businessInsuranceMapper;

    @Resource
    private EmployeeFilesMapper employeeFilesMapper;

    @Override
    public void addBusinessInsurance(String ids) throws Exception {
        String idArr[] = ids.split(",");
        List<AddBusinessInsurance> addInsurances = businessInsuranceMapper.getAddBusinessInsurance(idArr);
        if (addInsurances != null && addInsurances.size() > 0) {
            for (int i = 0; i < addInsurances.size(); i++) {
                AddBusinessInsurance insurance = addInsurances.get(i);
                if ("附属保险人".equals(insurance.getEmployeeType())) {
                    // 插入到子表数据中
                    // 根据主保险人信息查询人员表id
                    BusinessInsuranceInfo info  = businessInsuranceMapper.getBusinessInsuranceInfoId(insurance.getMainIdentityNo(),
                            insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                    if (info == null) {
                        throw new RuntimeException("没有获取到对应的主保险人信息！");
                    }
                    // 生成子表数据
                    BusinessInsuranceInfoChild child = new BusinessInsuranceInfoChild(info.getId(), insurance);

                    businessInsuranceMapper.addBusinessInsuranceInfoChild(child);
                } else {
                    String employeeFilesId = "";
                    // 根据员工信息查询员工档案数据
                    List<EmployeeFiles> employeeFiles =
                            employeeFilesMapper.getEmployeeFilesByIdNoAndClientName(insurance.getIdentityNo(),
                                    insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                    if (employeeFiles != null && employeeFiles.size() > 0) {
                        employeeFilesId = employeeFiles.get(0).getId();
                    } else {
                        throw new RuntimeException("没有获取到对应的员工档案信息！");
                    }
                    BusinessInsuranceInfo info = new BusinessInsuranceInfo(employeeFilesId, insurance);
                    businessInsuranceMapper.addBusinessInsuranceInfo(info);
                }
            }
        } else {
            throw new RuntimeException("没有获取到商保增员信息！");
        }
    }

    @Override
    public void delBusinessInsurance(String ids) throws Exception {
        String idArr[] = ids.split(",");
        List<DeleteBusinessInsurance> delInsurances = businessInsuranceMapper.getDeleteBusinessInsurance(idArr);
        if (delInsurances != null && delInsurances.size() > 0) {
            for (int i = 0; i < delInsurances.size(); i++) {
                DeleteBusinessInsurance insurance = delInsurances.get(i);
                // 根据保险人信息查询对应的人员信息id
                BusinessInsuranceInfo info = businessInsuranceMapper.getBusinessInsuranceInfoId(insurance.getIdentityNo(),
                        insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                if (info == null) {
                    // 没有查询到人员信息，可能是附属保险人
                    BusinessInsuranceInfoChild child = businessInsuranceMapper.getChildrenInfoId(insurance.getIdentityNo(),
                            insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                    if (child == null) {
                        throw new RuntimeException("没有获取到该人员的商保信息！");
                    }
                    child.setBenefitDeadline(insurance.getBenefitDeadline());
                    businessInsuranceMapper.updateChildrenInfo(child);
                }
                info.setBenefitDeadline(insurance.getBenefitDeadline());
                info.setRemarks(insurance.getRemarks());
                // 更新人员信息表数据
                businessInsuranceMapper.updateBusinessInsuranceInfo(info);
            }
        } else {
            throw new RuntimeException("没有获取到商保减员信息！");
        }

    }

    @Override
    public void updateBusinessInsuranceInfo(String ids) throws Exception {
        String idArr[] = ids.split(",");
        List<UpdateBusinessInsurance> insurances = businessInsuranceMapper.getUpdateBusinessInsurance(idArr);
        if (insurances != null && insurances.size() > 0) {
            for (int i = 0; i < insurances.size(); i++) {
                UpdateBusinessInsurance insurance = insurances.get(i);
                // 根据保险人信息查询对应的人员信息id
                BusinessInsuranceInfo info = businessInsuranceMapper.getBusinessInsuranceInfoId(insurance.getIdentityNo(),
                        insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                if (info == null) {
                    // 没有查询到人员信息，可能是附属保险人
                    BusinessInsuranceInfoChild child = businessInsuranceMapper.getChildrenInfoId(insurance.getIdentityNo(),
                            insurance.getFirstLevelClientName(), insurance.getSecondLevelClientName());
                    if (child == null) {
                        throw new RuntimeException("没有获取到该人员的商保信息！");
                    }
                    child.setLevel(insurance.getAfterLevel());
                    child.setServiceFee(insurance.getAfterServiceFee());
                    child.setEffectiveDate(insurance.getAfterEffectiveDate());
                    businessInsuranceMapper.updateChildrenInfo(child);
                }
                info.setLevel(insurance.getAfterLevel());
                info.setServiceFee(insurance.getAfterServiceFee());
                info.setEffectiveDate(insurance.getAfterEffectiveDate());
                info.setContent(insurance.getAfterContent());
                // 更新人员信息表数据
                businessInsuranceMapper.updateBusinessInsuranceInfo(info);
            }
        } else {
            throw new RuntimeException("没有获取到商保变更信息！");
        }

    }

    @Override
    public void updateEmployeeStatus(String firstLevelClientName, String secondLevelClientName, String identityNo,
                                     String status) throws Exception {
        if (StringUtils.isBlank(status)) {
            throw new RuntimeException("没有获取到修改后的员工状态，更新补充福利员工状态失败");
        }
        if (StringUtils.isBlank(identityNo)) {
            throw new RuntimeException("没有获取到证件号码，更新补充福利员工状态为：”" + status + "“失败");
        }
        if (StringUtils.isBlank(firstLevelClientName)) {
            throw new RuntimeException("没有获取到一级客户名称，更新补充福利员工状态为：”" + status + "“失败");
        }
        if (StringUtils.isBlank(secondLevelClientName)) {
            throw new RuntimeException("没有获取到二级客户名称，更新补充福利员工状态为：”" + status + "“失败");
        }
        /*// 修改员工补充福利的员工状态
        businessInsuranceMapper.updateWelfareSupplementState(secondLevelClientName, identityNo, status);*/
        // 修改商保人员信息的员工状态
        businessInsuranceMapper.updateBusinessInsuranceInfoEmployeeStatus(firstLevelClientName, secondLevelClientName,
                identityNo, status);
        // 更新附属人员工状态
        businessInsuranceMapper.updateBusinessInsuranceInfoChildEmployeeStatus(firstLevelClientName, secondLevelClientName,
                identityNo, status);
    }

    @Override
    public void importData(String fileName, UserModel user, DepartmentModel dept, String wfCode,
                           WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 业务员&&部门
        String salesman = "[{\"id\":\"" + user.getId() + "\",\"type\":3}]";
        String department = "[{\"id\":\"" + dept.getId() + "\",\"type\":1}]";
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            List<String> ids = new ArrayList <>();
            List<Map<String, Object>> dataList = new ArrayList <>();
            List<String> fields = Arrays.asList(fileList.get(0));
            for (int i = 2; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                Map<String, Object> data = new HashMap <>();
                for(int j = 0; j < fields.size(); j++) {
                    data.put(fields.get(j), list.get(j));
                }
                data.put("status", "待办理");
                data.put("salesman", salesman);
                data.put("department", department);
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                data = setData(data, id, user, dept, "DRAFT");
                dataList.add(data);
                ids.add(id);
            }
            for (int i = 0; i < dataList.size(); i += 500) {
                int toIndex = 500;
                if (i + 500 > dataList.size()) {
                    toIndex =  dataList.size() - i;
                }
                List<Map<String, Object>> newList = dataList.subList(i, i + toIndex);
                // 插入数据
                if ("add_business_insurance_wf".equals(wfCode)) {
                    businessInsuranceMapper.batchInsertAddBusinessInsurance(newList);
                } else if ("delete_business_insurance_wf".equals(wfCode)) {
                    businessInsuranceMapper.batchInsertDeleteBusinessInsurance(newList);
                }  else if ("update_business_insurance_wf".equals(wfCode)) {
                    businessInsuranceMapper.batchInsertUpdateBusinessInsurance(newList);
                }
            }
            // 激活流程
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                    wfCode, ids, false);
        }
    }

    @Override
    public QueryInfo getQueryInfo(String identityNo) throws Exception {
        QueryInfo queryInfo = new QueryInfo();
        // 根据证件号码查询员工档案数据
        EmployeeFiles employeeFiles = employeeFilesMapper.getEmployeeFilesByIdentityNo(identityNo);
        if (employeeFiles == null) {
            throw new RuntimeException("没有获取到员工档案数据！");
        }
        queryInfo.setFirstLevelClientName(employeeFiles.getFirstLevelClientName());
        queryInfo.setSecondLevelClientName(employeeFiles.getSecondLevelClientName());

        // 根据保险人信息查询对应的人员信息id
        BusinessInsuranceInfo info = businessInsuranceMapper.getBusinessInsuranceInfoId(identityNo,
                employeeFiles.getFirstLevelClientName(), employeeFiles.getSecondLevelClientName());
        if (info == null) {
            // 没有查询到人员信息，可能是附属保险人
            BusinessInsuranceInfoChild child = businessInsuranceMapper.getChildrenInfoId(identityNo,
                    employeeFiles.getFirstLevelClientName(), employeeFiles.getSecondLevelClientName());
            if (child == null) {
                throw new RuntimeException("没有获取到该人员的商保信息！");
            }
            queryInfo.setBeforeLevel(child.getLevel());
            queryInfo.setBeforeServiceFee(child.getServiceFee());
            queryInfo.setBeforeEffectiveDate(child.getEffectiveDate());
        }
        queryInfo.setBeforeLevel(info.getLevel());
        queryInfo.setBeforeServiceFee(info.getServiceFee());
        queryInfo.setBeforeEffectiveDate(info.getEffectiveDate());
        queryInfo.setBeforeContent(info.getContent());

        return queryInfo;
    }

    private Map <String, Object> setData(Map <String, Object> data, String id, UserModel user, DepartmentModel dept,
                                         String sequenceStatus) {
        data.put("id", id);
        data.put("creater", user.getId());
        data.put("createdDeptId", dept.getId());
        data.put("owner", user.getId());
        data.put("ownerDeptId", dept.getId());
        data.put("createdTime", new Date());
        data.put("modifier", user.getId());
        data.put("modifiedTime", new Date());
        data.put("sequenceStatus", sequenceStatus);
        data.put("ownerDeptQueryCode", dept.getQueryCode());

        return data;
    }
}
