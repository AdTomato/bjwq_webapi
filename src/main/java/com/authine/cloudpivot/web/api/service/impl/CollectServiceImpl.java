package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.dao.CollectDao;
import com.authine.cloudpivot.web.api.service.CollectService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.CollectServiceImpl
 * @Date 2020/1/10 14:03
 **/
@Service
@Slf4j
public class CollectServiceImpl implements CollectService {

    @Resource
    private CollectDao collectDao;

    /**
     * 方法说明：激活提交采集流程
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param id 发起采集流程业务id
     * @Param userId
     * @Param departmentId
     * @return void
     * @throws Exception
     * @author liulei
     * @Date 2020/1/10 14:08
     */
    @Override
    public void startCollect(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                             OrganizationFacade organizationFacade, String id, String refIds, String userId,
                             String departmentId) throws Exception {
        // 根据id，获取发起采集流程业务数据
        Map<String, String> data = collectDao.getBizObjectById(id);
        if (data == null) {
            log.info("没有获取到业务数据！");
            throw new RuntimeException("没有获取到业务数据！");
        }
        // 创建业务对象
        List <BizObjectModel> models = createBizObjectModel(data, organizationFacade);

        // 批量生成业务数据
        List<String> modelIds = new ArrayList <>();
        if (models != null && models.size() > 0) {
            modelIds = bizObjectFacade.addBizObjects(userId, models, "id");
        }
        // 生成对应的采集信息模板
        collectDao.addCollectTemplate(id, refIds, modelIds);
        // 启动流程实例
        CommonUtils.startWorkflowInstance(workflowInstanceFacade, departmentId, userId, "collect", modelIds, true);
    }

    /**
     * 方法说明：生成业务数据模型
     * @Param data
     * @return java.util.List<com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel>
     * @throws
     * @author liulei
     * @Date 2020/1/10 15:51
     */
    private List<BizObjectModel> createBizObjectModel(Map<String, String> data, OrganizationFacade organizationFacade)
            throws Exception {
        List <BizObjectModel> models = new ArrayList <>();
        // 主题
        String title = data.get("title");
        if (StringUtils.isBlank(title)) {
            return models;
        }
        // 截止时间
        String endTime = data.get("end_time");
        if (StringUtils.isBlank(endTime)) {
            return models;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(endTime);
        // 客户
        String client = data.get("client");
        client = client.substring(1, client.length() - 1);
        client = client.replaceAll("},\\{", "}#\\{");
        String[] clientArr = client.split("#");
        // 备注
        String remarks = data.get("remarks");
        // 对应发起采集的流程业务id
        String startCollectId = data.get("start_collect_id");

        if (clientArr.length > 0) {
            for (int i = 0; i < clientArr.length; i++) {
                Map<String, Object> objData = new HashMap <>();
                objData.put("title", title);
                objData.put("end_time", date);
                /*String[] userIds = clientArr[i].split(",");
                String userId = userIds[0].substring(7, userIds[0].length() - 1);
                UserModel user = organizationFacade.getUser(userId);
                objData.put("client", JSON.toJSONString(Arrays.asList(user)));*/
                objData.put("client", "[" + clientArr[i] + "]");
                objData.put("remarks", remarks);
                objData.put("start_collect_id", startCollectId);

                BizObjectModel obj = new BizObjectModel();
                obj.setSchemaCode("submit_collect");
                obj.put(objData);

                models.add(obj);
            }

        }
        return models;
    }

    /**
     * 方法说明：基数采集回写附件到发起采集中去
     * @Param id 基数采集业务id
     * @Param refIds 基数采集附件refId
     * @Param startCollectId 发起采集业务id
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/13 15:36
     */
    @Override
    public void returnCollect(String id, String refIds, String startCollectId) throws Exception {
        collectDao.returnCollect(id, refIds, startCollectId);
    }

    /**
     * 方法说明：过节点申请修改截止时间
     * @Param id 基数采集业务id
     * @Param endTime
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/13 21:48
     */
    @Override
    public void updateEndTime(String id, String endTime) throws Exception {
        collectDao.updateEndTime(id, endTime);
    }
}
