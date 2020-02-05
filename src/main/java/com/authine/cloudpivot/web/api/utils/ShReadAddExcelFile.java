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
 * @Date: 2020-02-05 11:10
 * @Description:
 */
@Component
public class ShReadAddExcelFile extends ShReadExcelFile {

    @Override
    protected Object conversion(String key, Object value) throws ParseException {

        Object result = value;
        if (StringUtils.isEmpty(result + "")) {
            return null;
        }
        switch (key) {
            case "service_fee":
            case "with_file":
            case "social_security_section":
            case "social_security_base":
            case "provident_fund_base":
            case "supplement_provident_fund_p":
            case "supplement_provident_fund_b":
            case "wage_trial":
            case "wage_positive":
                // 转换成double
                result = Double.parseDouble(result + "");
                break;
            case "entry_time":
            case "benefit_start_time":
            case "provident_fund_start_time":
            case "charge_start_date":
            case "dispatch_period_start_date":
            case "dispatch_deadline":
            case "start_date_trial":
            case "end_time_trial":
            case "start_date_positive":
            case "end_date_positive":
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
            case "员工姓名":
                code = "employee_name";
                break;
            case "唯一号":
                code = "unique_num";
                break;
            case "证件号":
                code = "identityNo";
                break;
            case "委托/派遣":
                code = "commission_send";
                break;
            case "证件号类型":
                code = "identityNo_type";
                break;
            case "委派单代码":
                code = "order_code";
                break;
            case "报价单代码":
                code = "quotation_code";
                break;
            case "系统客户编号":
                code = "sys_client_num";
                break;
            case "客户名称":
                code = "client_name";
                break;
            case "客户简称":
                code = "client_short_name";
                break;
            case "项目书名称":
                code = "project_proposals_name";
                break;
            case "社保支付方向":
                code = "social_security_pay_direct";
                break;
            case "公积金支付方向":
                code = "provident_fund_pay_direct";
                break;
            case "服务费":
                code = "service_fee";
                break;
            case "含档":
                code = "with_file";
                break;
            case "套餐涉及手续":
                code = "package_involves_procedures";
                break;
            case "入职时间":
                code = "entry_time";
                break;
            case "福利起始时间":
                code = "benefit_start_time";
                break;
            case "手机":
                code = "mobile";
                break;
            case "公积金开始时间":
                code = "provident_fund_start_time";
                break;
            case "是否一致":
            case "福利起始时间与公积金开始时间是否一致":
                code = "whether_consistent";
                break;
            case "社保组":
                code = "social_security_section";
                break;
            case "社会保险基数":
                code = "social_security_base";
                break;
            case "住房公积金基数":
                code = "provident_fund_base";
                break;
            case "补充住房公积金比例":
                code = "supplement_provident_fund_p";
                break;
            case "补充住房公积金基数":
                code = "supplement_provident_fund_b";
                break;
            case "收费起始日":
                code = "charge_start_date";
                break;
            case "派遣期限起始日期":
                code = "dispatch_period_start_date";
                break;
            case "派遣期限截至日期":
                code = "dispatch_deadline";
                break;
            case "开始日期(试)":
                code = "start_date_trial";
                break;
            case "结束日期(试)":
                code = "end_time_trial";
                break;
            case "工资(试)":
                code = "wage_trial";
                break;
            case "开始日期(正)":
                code = "start_date_positive";
                break;
            case "结束日期(正)":
                code = "end_date_positive";
                break;
            case "工资(正)":
                code = "wage_positive";
                break;
            case "电话":
                code = "phone";
                break;
            case "员工客户方编号":
                code = "employee_customer_side_num";
                break;
            case "联系地址":
                code = "contact_address";
                break;
            case "邮政编码":
                code = "postal_code";
                break;
            case "户口所在地":
                code = "Hukou_location";
                break;
            case "电子邮件":
                code = "mail";
                break;
            case "开户银名称":
                code = "bank_name";
                break;
            case "帐号":
                code = "bank_account";
                break;
            case "帐号省名":  // 帐号省名
                code = "account_province_name";
                break;
            case "帐号市区名":
                code = "account_city_name";
                break;
            case "帐户名":
                code = "account_name";
                break;
            case "银行类别":
                code = "bank_category";
                break;
            case "城市名称":
                code = "city_name";
                break;
            case "报税属性":
                code = "tax_properties";
                break;
            case "工号":
                code = "job_num";
                break;
            case "HRO":
                code = "HRO";
                break;
            case "业务部门":
                code = "business_unit";
                break;
            case "员工状态":
                code = "employee_status";
                break;
            case "同步CSS":
                code = "synchronous_CSS";
                break;
            case "入职备注":
                code = "Induction_remark";
                break;
            case "报税税局":
                code = "tax_bureau";
                break;
            case "员工属性":
                code = "employee_attributes";
                break;
            case "社保标准":
                code = "social_security_standards";
                break;
            case "是否线上":
                code = "weather_online";
                break;
            case "工作制":
                code = "work_system";
                break;
            case "是否入职E化":
                code = "weather_Induction_E";
                break;
            default:
                throw new RuntimeException("列名“" + cellName + "”不存在");
        }
        return code;
    }

    /**
     * @param userId                 : 当前导入人id
     * @param departmentId           : 当前导入人部门id
     * @param models                 : 数据
     * @param bizObjectFacade        : 用于保存数据的工具类
     * @param workflowInstanceFacade : 用于开启流程的工具类
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/4 16:13
     * @Description: 创建增员数据
     */
    public void startShAddWorkflow(String userId, String departmentId, List<BizObjectModel> models, BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) {
        List<String> ids = bizObjectFacade.addBizObjects(userId, models, "id");
        for (String id : ids) {
            workflowInstanceFacade.startWorkflowInstance(departmentId, userId, Constants.SH_ADD_EMPLOYEE_SCHEMA_WF, id, false);
        }
    }
}
