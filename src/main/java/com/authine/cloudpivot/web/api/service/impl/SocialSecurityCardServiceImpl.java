package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.ImportSocialSecurityCardTemp;
import com.authine.cloudpivot.web.api.entity.SocialSecurityCard;
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
@Transactional
public class SocialSecurityCardServiceImpl implements SocialSecurityCardService {

    @Resource
    private SocialSecurityCardMapper socialSecurityCardMapper;

    @Override
    public void importSocialSecurityCard(String fileName, UserModel user, DepartmentModel dept,
                                                  WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 0, 6);
        if (fileList != null && fileList.size() > 0) {
            // 保存创建成功的id值，用于后面激活流程使用
            List<String> ids = new ArrayList <>();
            String id = "";
            List<SocialSecurityCard> socialSecurityCards = new ArrayList <>();
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                id = UUID.randomUUID().toString().replaceAll("-", "");
                SocialSecurityCard socialSecurityCard = new SocialSecurityCard(id, user, dept, "DRAFT", list.get(6),
                        list.get(0), list.get(1), list.get(3), list.get(2), list.get(4), list.get(5));
                socialSecurityCards.add(socialSecurityCard);
            }
            // 插入数据
            // 修改已经生成的数据
            for (int i = 0; i < socialSecurityCards.size(); i += 500) {
                int toIndex = 500;
                if (i + 500 > socialSecurityCards.size()) {
                    toIndex =  socialSecurityCards.size() - i;
                }
                List<SocialSecurityCard> newList = socialSecurityCards.subList(i, i + toIndex);
                socialSecurityCardMapper.batchInsertSocialSecurityCard(newList);
            }
            // 修改已经生成的数据
            for (int i = 0; i < ids.size(); i += 500) {
                int toIndex = 500;
                if (i + 500 > ids.size()) {
                    toIndex =  ids.size() - i;
                }
                List<String> newList = ids.subList(i, i + toIndex);
                socialSecurityCardMapper.updateSocialSecurityCardWhenInsert(newList);
            }
            // 激活流程
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                    Constants.SOCIAL_SECURITY_CARD_PROCESS, ids, false);
        }
    }

    @Override
    public void importProcessFeedback(String fileName, UserModel user, DepartmentModel dept,
                                      WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 2, 5);
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
                        Constants.SOCIAL_SECURITY_CARD_PROCESS_NODE_UPLOAD_INFO, false);
            }
        }
    }

    @Override
    public void importIssueFeedback(String fileName, UserModel user, DepartmentModel dept,
                                    WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 2, 4);
        if (fileList != null && fileList.size() > 0) {
            List<ImportSocialSecurityCardTemp> datas = new ArrayList <>();
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                ImportSocialSecurityCardTemp data = new ImportSocialSecurityCardTemp(sourceId, list.get(0),
                        list.get(1), list.get(2));
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
            socialSecurityCardMapper.updateIssueByTempTable(sourceId);
            socialSecurityCardMapper.updateIssueFeedbackByTempTable(sourceId);

            List<String> successIds = socialSecurityCardMapper.getWorkItemIdsWhenIssueSuccess(sourceId, user.getId());
            if (successIds != null && successIds.size() > 0) {
                //  提交办理成功的流程
                CommonUtils.submitWorkItem(workflowInstanceFacade, user.getId(), successIds, true);
            }
        }
    }
}
