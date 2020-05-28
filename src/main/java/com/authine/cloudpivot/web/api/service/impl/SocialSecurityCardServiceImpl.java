package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.ImportSocialSecurityCardTemp;
import com.authine.cloudpivot.web.api.entity.SocialSecurityCard;
import com.authine.cloudpivot.web.api.mapper.InsuranceAndHousingFundMapper;
import com.authine.cloudpivot.web.api.mapper.SocialSecurityCardMapper;
import com.authine.cloudpivot.web.api.service.SocialSecurityCardService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 社保卡service接口实现类
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.service.impl.SocialSecurityCardServiceImpl
 * @Date 2019/12/16 10:21
 **/
@Service
@Slf4j
public class SocialSecurityCardServiceImpl implements SocialSecurityCardService {

    @Resource
    private SocialSecurityCardMapper socialSecurityCardMapper;

    @Resource
    private InsuranceAndHousingFundMapper insuranceAndHousingFundMapper;

    @Override
    public void importAddData(String fileName, UserModel user, DepartmentModel dept, String code,
                                         WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
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
                if (Constants.SOCIAL_SECURITY_CARD_PROCESS.equals(code)) {
                    socialSecurityCardMapper.batchInsertSocialSecurityCard(newList);
                } else if (Constants.INSURANCE_AND_HOUSING_FUND.equals(code)) {
                    socialSecurityCardMapper.batchInsertInsuranceAndHousingFund(newList);
                }
            }
            // 激活流程
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                    code, ids, false);
        }
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

    @Override
    public void importUpdateData(String fileName, UserModel user, DepartmentModel dept, String type,
                                      WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            List<Map<String, Object>> datas = new ArrayList <>();
            List<String> fields = Arrays.asList(fileList.get(0));
            for (int i = 2; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                Map<String, Object> data = new HashMap <>();
                for(int j = 0; j < fields.size(); j++) {
                    data.put(fields.get(j), list.get(j));
                }
                data.put("source_id", sourceId);
                datas.add(data);
            }
            for (int i = 0; i < datas.size(); i += 500) {
                int toIndex = 500;
                if (i + 500 > datas.size()) {
                    toIndex =  datas.size() - i;
                }
                List<Map<String, Object>> newList = datas.subList(i, i + toIndex);
                if ("1_2".equals(type) || "1_3".equals(type)) {
                    // 数据插入至社保卡临时表
                    socialSecurityCardMapper.insertTempData(newList);
                } else if ("2_2".equals(type) || "2_3".equals(type)) {
                    // 数据插入至五险一金临时表
                    insuranceAndHousingFundMapper.insertTempData(newList);
                }
            }
            if ("1_2".equals(type)) {
                // 社保卡导入办理记录
                socialSecurityCardMapper.updateProcessByTempTable(sourceId);
                socialSecurityCardMapper.updateProcessFeedbackByTempTable(sourceId);
            } else if ("1_3".equals(type)){
                //社保卡导入发卡记录
                socialSecurityCardMapper.updateIssueByTempTable(sourceId);
                socialSecurityCardMapper.updateIssueFeedbackByTempTable(sourceId);
            }  else if ("2_2".equals(type)){
                //五险一金享受：导入办理记录
                insuranceAndHousingFundMapper.updateEnjoyProgressByTempTable(sourceId);
                insuranceAndHousingFundMapper.updateProcessFeedbackByTempTable(sourceId);
            } else if ("2_3".equals(type)){
                //五险一金享受：业务反馈
                insuranceAndHousingFundMapper.updateEnjoyFeedbackByTempTable(sourceId);
            }

            if ("1_2".equals(type) || "1_3".equals(type)) {
                // 删除临时表数据
                socialSecurityCardMapper.deleteTempData(sourceId, "import_social_security_card_temp");
            } else if ("2_2".equals(type) || "2_3".equals(type)) {
                // 删除临时表数据
                socialSecurityCardMapper.deleteTempData(sourceId, "import_insurance_and_housing_fund_temp");
            }
        }
        /*List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 2, 5);
        if (fileList != null && fileList.size() > 0) {
            List<ImportSocialSecurityCardTemp> datas = new ArrayList <>();
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                ImportSocialSecurityCardTemp data = new ImportSocialSecurityCardTemp(sourceId, list.get(0),
                        list.get(1), list.get(2), list.get(3));
                datas.add(data);
            }
            // 修改已经生成的数据
            for (int i = 0; i < datas.size(); i += 500) {
                int toIndex = 500;
                if (i + 500 > datas.size()) {
                    toIndex =  datas.size() - i;
                }
                List<ImportSocialSecurityCardTemp> newList = datas.subList(i, i + toIndex);
                socialSecurityCardMapper.insertTempData(newList);
            }
            socialSecurityCardMapper.updateProcessByTempTable(sourceId);
            socialSecurityCardMapper.updateProcessFeedbackByTempTable(sourceId);

            List<String> successIds = socialSecurityCardMapper.getWorkItemIdsWhenProcessSuccess(sourceId, user.getId());
            if (successIds != null && successIds.size() > 0) {
                //  提交办理成功的流程
                CommonUtils.submitWorkItem(workflowInstanceFacade, user.getId(), successIds, true);
            }

            List<String> errorIds = socialSecurityCardMapper.getWorkItemIdsWhenProcessError(sourceId, user.getId());
            if (errorIds != null && errorIds.size() > 0) {
                // 驳回办理失败的流程
                CommonUtils.rejectWorkItem(workflowInstanceFacade, user.getId(), errorIds,
                        "apply", false);
            }
            // 删除临时表数据
            socialSecurityCardMapper.deleteTempData(sourceId);
        }*/
    }
}
