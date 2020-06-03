package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description 公共方法类
 * @ClassName com.authine.cloudpivot.web.api.utils.CommonUtils
 * @Date 2020/1/10 15:54
 **/
@Slf4j
public class CommonUtils {

    /**
     * 方法说明：批量生成业务数据
     * @Param bizObjectFacade
     * @Param userId 当前用户id
     * @Param models 运行时业务数据对象
     * @Param queryField 查询字段
     * @return java.util.List<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/4 19:11
     */
    public static List <String> addBizObjects(BizObjectFacade bizObjectFacade, String userId,
                                              List <BizObjectModel> models, String queryField) throws Exception {
        List <String> modelIds = new ArrayList <>();
        if (models != null && models.size() > 0) {
            modelIds = bizObjectFacade.addBizObjects(userId, models, queryField);
        }
        return modelIds;
    }

    /**
     * 方法说明：启动流程节点
     * @Param workflowInstanceFacade
     * @Param userId 当前用户id
     * @Param workflowCode 流程编码
     * @Param modelIds 业务对象id List
     * @Param finishStart 是否结束发起节点
     * @return void
     * @throws
     * @author liulei
     * @Date 2019/12/17 19:56
     */
    public static void startWorkflowInstance(WorkflowInstanceFacade workflowInstanceFacade, String departmentId,
                                             String userId, String workflowCode, List <String> modelIds,
                                             boolean finishStart) throws Exception {
        if (modelIds != null && modelIds.size() > 0) {
            for (int i = 0; i < modelIds.size(); i++) {
                // 启动流程，返回流程实例id
                String workflowId = workflowInstanceFacade.startWorkflowInstance(departmentId, userId, workflowCode,
                        modelIds.get(i), finishStart);
                log.info("启动流程:" + workflowId);
            }
        }
    }

    /**
     * 方法说明：提交流程
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemSuccessIds 待办任务id
     * @Param agree 是否同意
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void submitWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List <String> workItemSuccessIds, boolean agree) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemSuccessIds.size(); i++) {
            boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemSuccessIds.get(i), agree);
            log.info(workItemSuccessIds.get(i) + "提交：" + flag);
        }
    }

    /**
     * 方法说明：驳回流程
     * @Param workflowInstanceFacade
     * @Param userId
     * @Param workItemErrorIds 待办任务id
     * @Param rejectToActivityCode 驳回到指定的节点
     * @Param submitToReject 是否可以直接提交到驳回的节点
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/21 10:51
     */
    public static void rejectWorkItem(WorkflowInstanceFacade workflowInstanceFacade, String userId,
                                      List <String> workItemErrorIds, String rejectToActivityCode,
                                      boolean submitToReject) throws Exception {
        //  提交办理成功的流程
        for (int i = 0; i < workItemErrorIds.size(); i++) {
            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemErrorIds.get(i),
                    rejectToActivityCode, submitToReject);
            log.info(workItemErrorIds.get(i) + "驳回：" + flag);
        }
    }

    /**
     * 方法说明：资金数据处理
     * @param value 处理前的值
     * @param rounding 舍入规则（四舍五入，单边见角进元取整，单边见角舍元取整）
     * @param precision 四舍五入时保留的精度（0,1,2 位小数）
     * @return java.lang.Double
     * @author liulei
     * @Date 2020/3/18 10:04
     */
    public static Double processingData(Double value, String rounding, String precision) {
        if(value == null) {
            return null;
        }
        Double returnValue = 0d;
        if ("四舍五入".equals(rounding)) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            // 如果不需要四舍五入，可以使用RoundingMode.DOWN
            nf.setRoundingMode(RoundingMode.UP);
            if ("0".equals(precision)) {
                // 四舍五入取整
                returnValue = (double)Math.round(value);
            } else if ("1".equals(precision)) {
                // 保留两位小数
                nf.setMaximumFractionDigits(1);
                String companyStr = nf.format(value).trim().replaceAll(",", "");
                returnValue = Double.parseDouble(companyStr);
            } else if ("2".equals(precision)) {
                nf.setMaximumFractionDigits(2);
                String companyStr = nf.format(value).trim().replaceAll(",", "");
                returnValue = Double.parseDouble(companyStr);
            }
        } else if ("单边见角进元取整".equals(rounding)) {
            // 向上取整
            returnValue = Math.ceil(value);
        } else if ("单边见角舍元取整".equals(rounding)) {
            returnValue = Math.floor(value);
        }

        return returnValue;
    }

    /**
     * 方法说明：通过第一行获取导入数据的位置
     * @param list
     * @return java.util.Map<java.lang.String,java.lang.Integer>
     * @author liulei
     * @Date 2020/3/27 13:15
     */
    public static Map<String, Integer> getImportLocation(List<String> list) {
        Map<String, Integer> locationMap = new HashMap <>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                locationMap.put(list.get(i), i + 1);
            }
        }
        return locationMap;
    }

    /**
     * 方法说明：通过判断福利地对基数四舍五入取整
     * @param addEmployee
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/4/16 9:12
     */
    public static AddEmployee needBaseRounding(AddEmployee addEmployee) throws Exception {
        // 目前仅六安市需要四舍五入取整
        if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity()) && "六安市".indexOf(addEmployee.getSocialSecurityCity()) >= 0) {
            addEmployee.setSocialSecurityBase(processingData(addEmployee.getSocialSecurityBase(), "四舍五入", "0"));
        }
        if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity()) && "六安市".indexOf(addEmployee.getProvidentFundCity()) >= 0) {
            addEmployee.setProvidentFundBase(processingData(addEmployee.getProvidentFundBase(), "四舍五入", "0"));
        }
        return addEmployee;
    }

    /**
     * 方法说明：增员客户处理身份证号码
     * @param addEmployee
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/4/17 8:57
     */
    public static AddEmployee processingIdentityNo(AddEmployee addEmployee) throws Exception {
        if(checkIdentityNo(addEmployee.getIdentityNoType(), addEmployee.getIdentityNo())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(addEmployee.getIdentityNo());
            addEmployee.setGender(processIdentityNo.getGender());
            addEmployee.setBirthday(processIdentityNo.getBirthday());
        }
        return addEmployee;
    }

    /**
     * 方法说明：增员上海处理身份证号码
     * @param shAddEmployee
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/4/17 8:57
     */
    public static ShAddEmployee processingIdentityNo(ShAddEmployee shAddEmployee)  throws Exception{
        if(checkIdentityNo(shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(shAddEmployee.getIdentityNo());
            shAddEmployee.setGender(processIdentityNo.getGender());
            shAddEmployee.setBirthday(processIdentityNo.getBirthday());
        }
        return shAddEmployee;
    }

    /**
     * 方法说明：增员全国处理身份证号码
     * @param nationwideDispatch
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/4/17 8:57
     */
    public static NationwideDispatch processingIdentityNo(NationwideDispatch nationwideDispatch)  throws Exception{
        if(checkIdentityNo(nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(nationwideDispatch.getIdentityNo());
            nationwideDispatch.setGender(processIdentityNo.getGender());
            nationwideDispatch.setBirthday(processIdentityNo.getBirthday());
        }
        return nationwideDispatch;
    }

    public static DeleteEmployee processingIdentityNo(DeleteEmployee delEmployee) throws Exception{
        if(checkIdentityNo(delEmployee.getIdentityNoType(), delEmployee.getIdentityNo())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(delEmployee.getIdentityNo());
            delEmployee.setGender(processIdentityNo.getGender());
            delEmployee.setBirthday(processIdentityNo.getBirthday());
        }
        return delEmployee;
    }

    public static ShDeleteEmployee processingIdentityNo(ShDeleteEmployee delEmployee)  throws Exception{
        if(checkIdentityNo(delEmployee.getIdentityNoType(), delEmployee.getIdentityNo())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(delEmployee.getIdentityNo());
            delEmployee.setGender(processIdentityNo.getGender());
            delEmployee.setBirthday(processIdentityNo.getBirthday());
        }
        return delEmployee;
    }

    public static boolean checkIdentityNo(String identityNoType, String identityNo) throws Exception {
        if ("身份证".equals(identityNoType)) {
            if (StringUtils.isNotBlank(identityNo) && (identityNo.length() == 18 || identityNo.length() == 15)) {
                return true;
            } else {
                log.error("身份证号码格式不正确。");
                throw new RuntimeException("身份证号码格式不正确。");
            }
        }
        return false;
    }

    /**
     * 方法说明：根据表头第一行数据，获取每一行数据
     * @param rowArr 当前行
     * @param fields 表头信息
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author liulei
     * @Date 2020/4/23 14:29
     */
    public static Map<String, Object> getExcelRowData(String[] rowArr, List<String> fields) throws Exception{
        Map<String, Object> data = new HashMap <>();
        List <String> list = Arrays.asList(rowArr);
        for(int j = 0; j < fields.size(); j++) {
            data.put(fields.get(j), list.get(j));
        }
        return data;
    }

    /**
     * 方法说明：随机生成编码，时间戳+随机数
     * @return java.lang.String
     * @author liulei
     * @Date 2020/4/26 10:47
     */
    public static String randomGenerateCode() throws Exception{
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String temp = sf.format(new Date());
        int random=(int) (Math.random()*10000);
        return temp + random;
    }
}
