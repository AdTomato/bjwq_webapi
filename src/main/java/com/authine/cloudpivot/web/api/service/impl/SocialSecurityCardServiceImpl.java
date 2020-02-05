package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.OperatorDao;
import com.authine.cloudpivot.web.api.dao.SocialSecurityCardDao;
import com.authine.cloudpivot.web.api.service.SocialSecurityCardService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 社保卡service接口实现类
 *
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.service.impl.SocialSecurityCardServiceImpl
 * @Date 2019/12/16 10:21
 **/
@Service
@Slf4j
@Transactional
public class SocialSecurityCardServiceImpl implements SocialSecurityCardService {

    @Resource
    private OperatorDao operatorDao;

    @Resource
    private SocialSecurityCardDao socialSecurityCardDao;

    // 业务员Map
    private Map<String, String> salesmanMap = new HashMap<>();

    // 业务list
    private List<BizObjectModel> models = new ArrayList<>();

    // 地区运行list
    private List<Map<String, String>> operatorList = new ArrayList<>();

    private Map<String, Integer> operatorAndList = new HashMap<>();

    /**
     * 方法说明：办理社保卡，根据办理人员信息批量开启流程
     *
     * @return void
     * @throws
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param fileName
     * @Param userId
     * @Param departmentId
     * @author liulei
     * @Date 2019/12/16 10:35
     */
    @Override
    public void socialSecurityCardProcess(BizObjectFacade bizObjectFacade,
                                          WorkflowInstanceFacade workflowInstanceFacade, String fileName,
                                          String userId, String departmentId) throws Exception {
        // 读取文件信息
        List<String[]> fileList = ExcelUtils.readFile(fileName, 2, 0, 8);
        if (fileList != null && fileList.size() > 0) {
            models = new ArrayList<>();
            operatorList = new ArrayList<>();
            operatorAndList = new HashMap<>();
            // 获取业务员名称和json数据
            salesmanMap = getSalesmanMap();
            // 开始生成数据
            if (fileList.size() > 1000) {
                int i = fileList.size() / 1000;
                for (int j = 0; j <= i; j++) {
                    if (j * 1000 == fileList.size()) {
                        break;
                    }
                    if (j == i) {
                        this.createSocialSecurityCardBizObjectModel(fileList, j * 1000, fileList.size());
                    } else {
                        this.createSocialSecurityCardBizObjectModel(fileList, j * 1000, (j + 1) * 1000);
                    }
                }
            } else {
                this.createSocialSecurityCardBizObjectModel(fileList, 0, fileList.size());
            }

            // 批量生成业务数据
            List<String> modelIds = new ArrayList<>();
            if (models != null && models.size() > 0) {
                modelIds = bizObjectFacade.addBizObjects(userId, models, "id");
            }

            // 启动流程实例，不结束发起节点
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, departmentId, userId,
                    Constants.SOCIAL_SECURITY_CARD_PROCESS, modelIds, false);

        }
    }

    /**
     * 方法说明：获取业务员信息
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/1 17:07
     */
    private Map<String, String> getSalesmanMap() throws Exception {
        Map<String, String> salesmanMap = new HashMap<>();
        // TODO 根据业务角色获取所有业务员信息,角色id待定
        //List <UserModel> userModels = organizationFacade.getUsersByRoleId(Constants.SALESMAN_ROLE_ID);
        salesmanMap.put("刘磊", "[{\"id\":\"402881c16f63e980016f798408060d3f\",\"type\":3}]");
        /*if (userModels != null && userModels.size() > 0) {
            for (UserModel userModel : userModels) {
                salesmanMap.put(userModel.getName(), "[{\"id\":\"" + userModel.getId() + "\",\"type\":3}]");
            }
        }*/
        return salesmanMap;
    }

    /**
     * 方法说明：生成办理社保卡BizObjectModel数据
     *
     * @return void
     * @throws
     * @Param fileList 文件信息
     * @Param startNo for循环开始 从0开始
     * @Param endNo for循环结束 最大为fileList.size()
     * @author liulei
     * @Date 2019/12/16 17:00
     */
    private void createSocialSecurityCardBizObjectModel(List<String[]> fileList, int startNo, int endNo) throws Exception {
        // 生成BizObjectModel
        for (int i = startNo; i < endNo; i++) {
            List<String> list = Arrays.asList(fileList.get(i));
            BizObjectModel model = new BizObjectModel();
            // 插入模型编码
            model.setSchemaCode("social_security_card");

            Map<String, Object> data = getBizObjectModelData(list, salesmanMap);
            model.put(data);

            models.add(model);
        }
    }

    /**
     * 方法说明：根据每一行数据生成BizObjectModel的data
     *
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @throws
     * @Param list
     * @Param salesmanMap
     * @author liulei
     * @Date 2019/12/27 11:31
     */
    private Map<String, Object> getBizObjectModelData(List<String> list, Map<String, String> salesmanMap)
            throws Exception {
        Map<String, Object> data = new HashMap<>();
        // 业务员名称
        String salesmanName = list.get(3);
        String salesman = "";
        if (salesmanMap.get(salesmanName) == null) {
            throw new Exception("没有对应业务员“" + salesmanName + "”。");
        } else {
            salesman = salesmanMap.get(salesmanName);

        }
        // 地区
        String area = list.get(8);
        //根据地区获取对应运行人员
        Map<String, String> operatorMap = new HashMap<>();
        if (operatorAndList.get(area) != null) {
            operatorMap = operatorList.get(operatorAndList.get(area));
        } else {
            operatorMap = operatorDao.getOperatorByArea(area);
            operatorList.add(operatorMap);
            operatorAndList.put(area, operatorList.size() - 1);
        }
        String operator = operatorMap.get("operator");
        String city = operatorMap.get("city");
        if (StringUtils.isBlank(operator)) {
            throw new Exception("地区:“" + area + "”没有对应运行人员。");
        }
        // 业务员
        data.put("salesman", salesman);
        // 办理地点
        data.put("city", city);
        // 运行人员
        data.put("operator", operator);
        // 是否紧急交办
        data.put("is_emerg", StringUtils.isBlank(list.get(1)) ? "否" : list.get(2));
        // 是否vip
        data.put("is_vip", StringUtils.isBlank(list.get(2)) ? "否" : list.get(3));
        // 公司名称
        data.put("company_name", list.get(4));
        // 姓名<办理社保卡人员姓名>
        data.put("employee_name", list.get(5));
        // 身份证号码
        data.put("identityNo", list.get(6));
        // 员工状态
        data.put("employee_status", list.get(7));
        // 是否上传办卡材料 线下提交材料，直接到运行办卡
        /*data.put("upload_info", "否");*/
        // 线下递交资料清单
       /* String submissionList = list.get(9);
        data.put("submission_list", submissionList);
        // 是否有线下递交材料
        if (StringUtils.isNotBlank(submissionList)) {
            data.put("have_submission_list", "是");
        } else {
            data.put("have_submission_list", "否");
        }*/
        return data;
    }

    @Override
    public void importProcessFeedBack(WorkflowInstanceFacade workflowInstanceFacade, String fileName, String userId)
            throws Exception {
        // 读取文件信息
        List<String[]> fileList = ExcelUtils.readFile(fileName, 2, 0, 6);
        // 办理成功的任务id
        List<String> workItemSuccessIds = new ArrayList();
        // 办理失败的任务id
        List<String> workItemErrorIds = new ArrayList();
        String errorStr = "";
        if (fileList != null && fileList.size() > 0) {
            // 生成办理进度sql
            StringBuffer insertSql = new StringBuffer();
            // 当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String curTime = sdf.format(new Date());

            for (int i = 0; i < fileList.size(); i++) {
                List<String> list = Arrays.asList(fileList.get(i));
                // 序号
                String num = list.get(0);
                // 单据号
                String sequenceNo = list.get(1);
                // 办理状态
                String processStatus = list.get(4);
                // 办理反馈
                String processFeedback = StringUtils.isBlank(list.get(5)) ? "" : list.get(5);
                // 社保卡号
                String socialSecurityCardNumber = StringUtils.isBlank(list.get(6)) ? "" : list.get(6);
                // 导入字段校验
                String checkResult = checkImport(num, sequenceNo, processStatus, processFeedback, socialSecurityCardNumber);
                if (StringUtils.isNotBlank(checkResult)) {
                    errorStr += checkResult;
                    continue;
                }

                // 根据单据号，当前节点code获取表单信息
                // 节点为运行办卡
                Map<String, Object> ssc = socialSecurityCardDao.getSocialSecurityCardInfo(sequenceNo, userId,
                        Constants.SOCIAL_SECURITY_CARD_PROCESS_NODE_PROCESS);
                if (ssc == null) {
                    errorStr += "序号：" + num + "的办理流程没有查询到。";
                    continue;
                }

                String sql = getUpdateProcessInfoSql(ssc.get("id").toString(), curTime, processStatus, processFeedback,
                        socialSecurityCardNumber);
                insertSql.append(sql);

                if ("办理成功".equals(processStatus)) {
                    // 办理成功
                    workItemSuccessIds.add(ssc.get("workItemId").toString());
                } else if ("办理失败".equals(processStatus)) {
                    // 办理失败
                    workItemErrorIds.add(ssc.get("workItemId").toString());
                }
            }
            socialSecurityCardDao.executeSql(insertSql.toString());
        }

        if (workItemSuccessIds != null && workItemSuccessIds.size() > 0) {
            //  提交办理成功的流程
            CommonUtils.submitWorkItem(workflowInstanceFacade, userId, workItemSuccessIds, true);
        }
        if (workItemErrorIds != null && workItemErrorIds.size() > 0) {
            // 驳回办理失败的流程
            CommonUtils.rejectWorkItem(workflowInstanceFacade, userId, workItemErrorIds,
                    Constants.SOCIAL_SECURITY_CARD_PROCESS_NODE_UPLOAD_INFO, false);
        }
    }

    /**
     * 方法说明：获取更新办理状态sql
     *
     * @return java.lang.String
     * @throws
     * @Param id
     * @Param curTime
     * @Param processStatus
     * @Param processFeedback
     * @Param socialSecurityCardNumber
     * @author liulei
     * @Date 2020/1/21 10:23
     */
    private String getUpdateProcessInfoSql(String id, String curTime, String processStatus, String processFeedback,
                                           String socialSecurityCardNumber) {
        // 修改表单数据
        String sql = "update ivdlf_social_security_card process_select = '" + processStatus + "', process_detail = '" +
                processFeedback + "', sin_card = '" + socialSecurityCardNumber + "' where id = '" + id + "';";
        // 修改子表数据
        String feedback = curTime + ":" + processFeedback + ";" + (StringUtils.isBlank(socialSecurityCardNumber) ?
                "\n" : socialSecurityCardNumber + ";\n");
        sql += "update ivdlf_process process_feedback = concat(process_feedback, '" + feedback + "') where parentId =" +
                " '" + id + "' and process_status = '" + processStatus + "';";

        return sql;
    }

    /**
     * 方法说明：导入办理状态时导入信息校验
     *
     * @return java.lang.String
     * @throws
     * @Param num 序号
     * @Param sequenceNo 单据号
     * @Param processStatus 办理状态
     * @Param processFeedback 办理反馈
     * @Param socialSecurityCardNumber 社保卡号
     * @author liulei
     * @Date 2020/1/21 9:45
     */
    private String checkImport(String num, String sequenceNo, String processStatus, String processFeedback,
                               String socialSecurityCardNumber) {
        String errorStr = "";
        if (StringUtils.isBlank(sequenceNo)) {
            // 单据号为空不导入
            errorStr += "序号：" + num + "单据号为空不导入;";
        }

        if (StringUtils.isBlank(processStatus)) {
            // 办理状态为空不导入
            errorStr += "序号：" + num + "办理状态为空不导入;";
        }
        if (!"未办理".equals(processStatus) && "办理中".equals(processStatus) && !"办理失败".equals(processStatus) && !
                "办理成功".equals(processStatus)) {
            // 办理状态格式不正确
            errorStr += "序号：" + num + "办理状态格式不正确;";
        }

        if (StringUtils.isBlank(processFeedback)) {
            // 办理进度反馈为空不导入
            errorStr += "序号：" + num + "办理进度反馈为空不导入;";
        }

        if ("办理成功".equals(processStatus) && StringUtils.isBlank(socialSecurityCardNumber)) {
            // 办理成功时，社保卡号为空不导入
            errorStr += "序号：" + num + "办理成功时，社保卡号为空不导入;";
        }

        return errorStr;
    }

    @Override
    public void importIssueFeedBack(WorkflowInstanceFacade workflowInstanceFacade, String fileName, String userId)
            throws Exception {
        // 读取文件信息
        List<String[]> fileList = ExcelUtils.readFile(fileName, 2, 0, 5);
        // 发卡成功的流程id
        List<String> workItemSuccessIds = new ArrayList();
        String errorStr = "";
        if (fileList != null && fileList.size() > 0) {
            // 生成办理进度sql
            StringBuffer insertSql = new StringBuffer();
            // 当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String curTime = sdf.format(new Date());

            for (int i = 0; i < fileList.size(); i++) {
                List<String> list = Arrays.asList(fileList.get(i));
                // 序号
                String num = list.get(0);
                // 单据号
                String sequenceNo = list.get(1);
                // 发卡状态
                String issueStatus = list.get(4);
                // 发卡反馈
                String issueFeedBack = StringUtils.isBlank(list.get(5)) ? "" : list.get(5);
                // 导入字段校验
                String checkResult = checkImport(num, sequenceNo, issueStatus, issueFeedBack);
                if (StringUtils.isNotBlank(checkResult)) {
                    errorStr += checkResult;
                    continue;
                }
                // 根据单据号，当前节点code获取表单信息
                // 当前节点为业务发卡
                Map<String, Object> ssc = socialSecurityCardDao.getSocialSecurityCardInfo(sequenceNo, userId,
                        Constants.SOCIAL_SECURITY_CARD_PROCESS_NODE_GIVE_OUT);
                if (ssc == null) {
                    errorStr += "序号：" + num + "的办理流程没有查询到。";
                    continue;
                }

                String sql = getUpdateIssueInfoSql(ssc.get("id").toString(), curTime, issueStatus, issueFeedBack);
                insertSql.append(sql);

                if ("发卡成功".equals(issueStatus)) {
                    // 发卡成功
                    workItemSuccessIds.add(ssc.get("workItemId").toString());
                }
            }
            socialSecurityCardDao.executeSql(insertSql.toString());
        }

        if (workItemSuccessIds != null && workItemSuccessIds.size() > 0) {
            //  发卡成功结束流程,提交办理成功的流程
            CommonUtils.submitWorkItem(workflowInstanceFacade, userId, workItemSuccessIds, true);
        }
    }

    /**
     * 方法说明：获取更新发卡进度sql
     *
     * @return java.lang.String
     * @throws
     * @Param id
     * @Param curTime
     * @Param issueStatus
     * @Param issueFeedBack
     * @author liulei
     * @Date 2020/1/21 10:58
     */
    private String getUpdateIssueInfoSql(String id, String curTime, String issueStatus, String issueFeedBack) {
        // 修改表单数据
        String sql = "update ivdlf_social_security_card issue_select = '" + issueStatus + "issue_detail = '" +
                issueFeedBack + "' where id = '" + id + "';";
        // 修改子表数据
        String feedback = curTime + ":" + issueFeedBack + ";\n";
        sql += "update ivdlf_issue issue_feedback = concat(issue_feedback, '" + feedback + "') where parentId =" +
                " '" + id + "' and issue_status = '" + issueStatus + "';";
        return sql;
    }

    /**
     * 方法说明：导入发卡状态时导入信息校验
     *
     * @return java.lang.String
     * @throws
     * @Param num
     * @Param sequenceNo
     * @Param issueStatus
     * @Param issueFeedBack
     * @author liulei
     * @Date 2020/1/21 10:32
     */
    private String checkImport(String num, String sequenceNo, String issueStatus, String issueFeedBack) {
        String errorStr = "";
        if (StringUtils.isBlank(sequenceNo)) {
            // 单据号为空不导入
            errorStr += "序号：" + num + "单据号为空不导入;";
        }

        if (StringUtils.isBlank(issueStatus)) {
            // 发卡状态为空不导入
            errorStr += "序号：" + num + "发卡状态为空不导入;";
        }
        if (!"未发卡".equals(issueStatus) && "发卡中".equals(issueStatus) && !"发卡成功".equals(issueStatus)) {
            // 发卡状态格式不正确
            errorStr += "序号：" + num + "发卡状态格式不正确;";
        }

        if (StringUtils.isBlank(issueFeedBack)) {
            // 发卡进度反馈为空不导入
            errorStr += "序号：" + num + "发卡进度反馈为空不导入;";
        }

        return errorStr;
    }

    /**
     * 测试使用
     */
    @Override
    public void submit(WorkflowInstanceFacade workflowInstanceFacade, String userId) throws Exception {
        String objId = "bca557b204484ec1a7cd002b4c5e0d33";
        // 插入数据
        String sql = "update i3bme_social_security_card set issue_select = '发卡成功', issue_detail = '1234567' " +
                " where id = 'bca557b204484ec1a7cd002b4c5e0d33'";
        ConnectionUtils.executeSql(sql);

        // 插入子表数据
        /*String sql1 = "update i3bme_process set process_feedback = '1234567;111111111111' where " +
                "parentId = 'bca557b204484ec1a7cd002b4c5e0d33' " +
                "and process_status = '办理成功' ";
        ConnectionUtils.executeSql(sql1);*/

        boolean flag = workflowInstanceFacade.submitWorkItem(userId, "55bbe0961b1a423880ad25f4f54ba96f", true);
        System.out.println(objId + "提交：" + flag);

    }

    /**
     * 测试使用
     */
    @Override
    public void reject(WorkflowInstanceFacade workflowInstanceFacade, String userId) throws Exception {
        String objId = "f8463d166d47428e8444b0f51f1e93b7";
        // 插入数据
        String sql = "update i3bme_social_security_card set process_select = '办理失败', process_detail = '7654321', " +
                "sin_card = '111111111111' where id = 'bca557b204484ec1a7cd002b4c5e0d33'";
        ConnectionUtils.executeSql(sql);

        // 插入子表数据
        String sql1 = "update i3bme_process set process_feedback = '7654321;22222222' where " +
                "parentId = 'bca557b204484ec1a7cd002b4c5e0d33' " +
                "and process_status = '办理失败' ";
        ConnectionUtils.executeSql(sql1);

        boolean flag = workflowInstanceFacade.rejectWorkItem(userId, "e8bced0ff6104bbcbf68c94029a2ae91",
                Constants.SOCIAL_SECURITY_CARD_PROCESS_NODE_UPLOAD_INFO, false);
        System.out.println(objId + "驳回：" + flag);
    }
}
