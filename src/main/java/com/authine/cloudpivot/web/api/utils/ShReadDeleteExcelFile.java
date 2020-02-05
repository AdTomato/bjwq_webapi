package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:11
 * @Description:
 */
@Component
public class ShReadDeleteExcelFile extends ShReadExcelFile {

    /**
     * @param userId                 : 当前导入人id
     * @param departmentId           : 当前导入人部门id
     * @param models                 : 数据
     * @param bizObjectFacade        : 用于保存数据的工具类
     * @param workflowInstanceFacade : 用于开启流程的工具类
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/5 14:08
     * @Description: 创建上海增员减员数据
     */
    public void startShDeleteWorkflow(String userId, String departmentId, List<BizObjectModel> models, BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) {
        List<String> ids = bizObjectFacade.addBizObjects(userId, models, "id");
        for (String id : ids) {
            workflowInstanceFacade.startWorkflowInstance(departmentId, userId, Constants.SH_DELETE_EMPLOYEE_SCHEMA_WF, id, false);
        }
    }

    @Override
    protected Object conversion(String key, Object value) throws ParseException {
        Object result = value;
        if (StringUtils.isEmpty(value + "")) {
            return result;
        }
        switch (key) {
            case "OS_initiated_departure_time":
            case "departure_time":
            case "charge_end_time":
                // 转换成时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.parse(result + "");
                break;
        }
        return result;
    }

    @Override
    protected String getCode(String cellName) {
        String code = null;
        switch (cellName) {
            case "客户名称":
                code = "client_name";
                break;
            case "姓名":
                code = "employee_name";
                break;
            case "身份证号":
                code = "identityNo";
                break;
            case "地区":
                code = "city";
                break;
            case "离职原因":
                code = "leave_reason";
                break;
            case "离职日期":
                code = "leave_time";
                break;
            case "社保终止时间":
                code = "social_security_end_time";
                break;
            case "公积金终止时间":
                code = "fund_end_time";
                break;
            case "备注":
                code = "remark";
                break;
            default:
                throw new RuntimeException("列名“" + cellName + "”不存在");
        }
        return code;
    }
}
