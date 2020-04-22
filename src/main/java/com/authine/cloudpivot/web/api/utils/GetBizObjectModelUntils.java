package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liulei
 * @Description 获取业务数据对象公共类
 * @ClassName com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils
 * @Date 2020/4/16 10:18
 **/
@Slf4j
public class GetBizObjectModelUntils {

    /**
     * 方法说明：业务对象基本信息插入
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/20 8:43
     */
    public static BizObjectModel getBizObjectModel(String schemaCode, String sequenceStatus, String owner,
                                                    String ownerDeptId, String ownerDeptQueryCode, String creater,
                                                    String createdDeptId, Date createdTime) {
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode(schemaCode);
        model.setSequenceStatus(sequenceStatus);
        model.setCreater(creater);
        model.setCreatedDeptId(createdDeptId);
        model.setCreatedTime(createdTime);
        // 业务员
        model.setOwner(owner);
        model.setOwnerDeptId(ownerDeptId);
        model.setOwnerDeptQueryCode(ownerDeptQueryCode);

        return model;
    }

    /**
     * 方法说明：创建入职通知业务对象
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/16 17:26
     */
    public static BizObjectModel getEntryNotice(String sequenceStatus, String creater, String createdDeptId,
                                                Date createdTime, String owner, String ownerDeptId,
                                                String ownerDeptQueryCode, String employeeName, String identityNo,
                                                String socialSecurity, String providentFund, String submissionMethod,
                                                String operateSignatory, String operateSignFor,
                                                String unitProvidentFundNum, String personalProvidentFundNum,
                                                String employmentRegisterNum, String entryContactRemark,
                                                String subordinateDepartment, String firstLevelClientName,
                                                String secondLevelClientName, String mobile,
                                                String socialSecurityCity, String providentFundCity,
                                                String isRetiredSoldier, String isDisabled, String isPoorArchivists,
                                                String recordOfEmployment, String feedback, String status) {
        BizObjectModel model = getBizObjectModel(Constants.ENTRY_NOTICE_SCHEMA, sequenceStatus, owner, ownerDeptId,
                ownerDeptQueryCode, creater, createdDeptId , createdTime);
        Map <String, Object> data = new HashMap <>();
        data.put("employee_name", employeeName);
        data.put("identityNo", identityNo);
        data.put("social_security", socialSecurity);
        data.put("provident_fund", providentFund);
        data.put("submission_method", submissionMethod);
        data.put("operate_signatory", operateSignatory);
        data.put("operate_sign_for", operateSignFor);
        data.put("unit_provident_fund_num", unitProvidentFundNum);
        data.put("personal_provident_fund_num", personalProvidentFundNum);
        data.put("employment_register_num", employmentRegisterNum);
        data.put("entry_contact_remark", entryContactRemark);
        data.put("subordinate_department", subordinateDepartment);
        data.put("first_level_client_name", firstLevelClientName);
        data.put("second_level_client_name", secondLevelClientName);
        data.put("mobile", mobile);
        data.put("social_security_city", socialSecurityCity);
        data.put("provident_fund_city", providentFundCity);
        data.put("is_retired_soldier", isRetiredSoldier);
        data.put("is_disabled", isDisabled);
        data.put("is_poor_archivists", isPoorArchivists);
        data.put("record_of_employment", recordOfEmployment);
        data.put("feedback", feedback);
        data.put("status", status);

        model.put(data);
        return model;
    }

    /**
     * 方法说明：创建员工档案业务对象
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/16 17:26
     */
    public static BizObjectModel getEmployeeFilesModel(EmployeeFiles employeeFiles) throws Exception{
        BizObjectModel model = getBizObjectModel(Constants.EMPLOYEE_FILES_SCHEMA, employeeFiles.getSequenceStatus(),
                employeeFiles.getOwner(), employeeFiles.getOwnerDeptId(), employeeFiles.getOwnerDeptQueryCode(),
                employeeFiles.getCreater(), employeeFiles.getCreatedDeptId(), employeeFiles.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        // 员工姓名
        data.put("employee_name", employeeFiles.getEmployeeName());
        // 证件类型
        data.put("id_type", employeeFiles.getIdType());
        // 证件号码
        data.put("id_no", employeeFiles.getIdNo());
        // 性别
        data.put("gender", employeeFiles.getGender());
        // 出生年月
        data.put("birth_date", employeeFiles.getBirthDate());
        // 员工性质
        data.put("employee_nature", employeeFiles.getEmployeeNature());
        // 户籍性质
        data.put("household_register_nature", employeeFiles.getHouseholdRegisterNature());
        // 联系电话
        data.put("mobile", employeeFiles.getMobile());
        // 社保福利地
        data.put("social_security_city", employeeFiles.getSocialSecurityCity());
        // 公积金福利地
        data.put("provident_fund_city", employeeFiles.getProvidentFundCity());
        // 报入职时间
        data.put("report_entry_time", employeeFiles.getReportEntryTime());
        // 报入职人
        data.put("report_recruits", employeeFiles.getReportRecruits());
        // 入职日期
        data.put("entry_time", employeeFiles.getEntryTime());
        // 社保收费开始
        data.put("social_security_charge_start", employeeFiles.getSocialSecurityChargeStart());
        // 公积金收费开始
        data.put("provident_fund_charge_start", employeeFiles.getProvidentFundChargeStart());
        // 入职备注
        data.put("entry_description", employeeFiles.getEntryDescription());
        // 报离职时间
        data.put("report_quit_date", employeeFiles.getReportQuitDate());
        // 报离职人
        data.put("report_severance_officer", employeeFiles.getReportSeveranceOfficer());
        // 离职日期
        data.put("quit_date", employeeFiles.getQuitDate());
        // 社保收费截止
        data.put("social_security_charge_end", employeeFiles.getSocialSecurityChargeEnd());
        // 公积金收费截止
        data.put("provident_fund_charge_end", employeeFiles.getProvidentFundChargeEnd());
        // 离职原因
        data.put("quit_reason", employeeFiles.getQuitReason());
        // 离职备注
        data.put("quit_remark", employeeFiles.getQuitRemark());
        // 是否入职通知
        data.put("entry_notice", employeeFiles.getEntryNotice());
        // 是否体检
        data.put("health_check", employeeFiles.getHealthCheck());
        // 是否发薪
        data.put("whether_pay", employeeFiles.getWhetherPay());
        // 职位
        data.put("position", employeeFiles.getPosition());
        // 邮箱
        data.put("email", employeeFiles.getEmail());
        // 员工标签
        data.put("employee_labels", employeeFiles.getEmployeeLabels());
        // 停止生成账单
        data.put("stop_generate_bill", employeeFiles.getStopGenerateBill() == null || employeeFiles.getStopGenerateBill() == 0 ? "否" : "是");
        // 是否老员工
        data.put("is_old_employee", employeeFiles.getIsOldEmployee() == null || employeeFiles.getIsOldEmployee() == 0 ? "否" : "是");
        // 社保账号
        data.put("social_security_num", employeeFiles.getSocialSecurityNum());
        // 公积金账号
        data.put("provident_fund_num", employeeFiles.getProvidentFundNum());
        // 社保支付账单年月
        data.put("s_payment_application", employeeFiles.getSPaymentApplication());
        // 公积金支付账单年月
        data.put("g_payment_application", employeeFiles.getGPaymentApplication());
        // 所属部门
        data.put("subordinate_department", employeeFiles.getSubordinateDepartment());
        // 一级客户名称
        data.put("first_level_client_name", employeeFiles.getFirstLevelClientName());
        // 二级客户名称
        data.put("second_level_client_name", employeeFiles.getSecondLevelClientName());
        // 户籍备注
        data.put("household_register_remarks", employeeFiles.getHouseholdRegisterRemarks());
        // 社保基数
        data.put("social_security_base", employeeFiles.getSocialSecurityBase());
        // 公积金基数
        data.put("provident_fund_base", employeeFiles.getProvidentFundBase());
        // 社保福利办理方
        data.put("s_welfare_handler", employeeFiles.getSWelfareHandler());
        // 公积金福利办理方
        data.put("g_welfare_handler", employeeFiles.getGWelfareHandler());
        // 退役士兵
        data.put("is_retired_soldier", employeeFiles.getIsRetiredSoldier());
        // 贫困建档人员
        data.put("is_poor_archivists", employeeFiles.getIsPoorArchivists());
        // 残疾人
        data.put("is_disabled", employeeFiles.getIsDisabled());

        model.put(data);

        return model;
    }

    /**
     * 方法说明：创建社保申报业务对象
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/16 17:26
     */
    public static BizObjectModel getSocialSecurityDeclare(SocialSecurityDeclare sDeclare) throws Exception{
        BizObjectModel model = getBizObjectModel(Constants.SOCIAL_SECURITY_DECLARE_SCHEMA,
                sDeclare.getSequenceStatus(), sDeclare.getOwner(), sDeclare.getOwnerDeptId(),
                sDeclare.getOwnerDeptQueryCode(), sDeclare.getCreater(), sDeclare.getCreatedDeptId(),
                sDeclare.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        data.put("start_month", sDeclare.getStartMonth());
        data.put("employee_name", sDeclare.getEmployeeName());
        data.put("gender", sDeclare.getGender());
        data.put("identityNo", sDeclare.getIdentityNo());
        data.put("contract_signing_date", sDeclare.getContractSigningDate());
        data.put("contract_deadline", sDeclare.getContractDeadline());
        data.put("positive_salary", sDeclare.getPositiveSalary());
        data.put("base_pay", sDeclare.getBasePay());
        data.put("mobile", sDeclare.getMobile());
        data.put("welfare_handler", sDeclare.getWelfareHandler());
        data.put("birthday", sDeclare.getBirthday());
        data.put("identityNo_type", sDeclare.getIdentityNoType());
        data.put("operate_leader", sDeclare.getOperateLeader());
        data.put("employee_order_form_id", sDeclare.getEmployeeOrderFormId());
        data.put("status", sDeclare.getStatus());
        data.put("first_level_client_name", sDeclare.getFirstLevelClientName());
        data.put("second_level_client_name", sDeclare.getSecondLevelClientName());
        data.put("subordinate_department", sDeclare.getSubordinateDepartment());
        data.put("city", sDeclare.getCity());
        data.put("remark", sDeclare.getRemark());
        model.put(data);
        return model;
    }

    /**
     * 方法说明：创建公积金申报业务对象
     * @return com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @author liulei
     * @Date 2020/4/16 17:26
     */
    public static BizObjectModel getProvidentFundDeclare(ProvidentFundDeclare gDeclare) {
        BizObjectModel model = getBizObjectModel(Constants.PROVIDENT_FUND_DECLARE_SCHEMA,
                gDeclare.getSequenceStatus(), gDeclare.getOwner(), gDeclare.getOwnerDeptId(),
                gDeclare.getOwnerDeptQueryCode(), gDeclare.getCreater(), gDeclare.getCreatedDeptId(),
                gDeclare.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        data.put("employee_name", gDeclare.getEmployeeName());
        data.put("gender", gDeclare.getGender());
        data.put("identityNo", gDeclare.getIdentityNo());
        data.put("start_month", gDeclare.getStartMonth());
        data.put("provident_fund_base", gDeclare.getProvidentFundBase());
        data.put("corporate_payment", gDeclare.getCorporatePayment());
        data.put("personal_deposit", gDeclare.getPersonalDeposit());
        data.put("total_deposit", gDeclare.getTotalDeposit());
        data.put("welfare_handler", gDeclare.getWelfareHandler());
        data.put("birthday", gDeclare.getBirthday());
        data.put("identityNo_type", gDeclare.getIdentityNoType());
        data.put("operate_leader", gDeclare.getOperateLeader());
        data.put("employee_order_form_id", gDeclare.getEmployeeOrderFormId());
        data.put("status", gDeclare.getStatus());
        data.put("first_level_client_name", gDeclare.getFirstLevelClientName());
        data.put("second_level_client_name", gDeclare.getSecondLevelClientName());
        data.put("subordinate_department", gDeclare.getSubordinateDepartment());
        data.put("city", gDeclare.getCity());
        model.put(data);
        return model;
    }

    public static BizObjectModel getSocialSecurityClose(SocialSecurityClose sClose) {
        BizObjectModel model = getBizObjectModel(Constants.SOCIAL_SECURITY_CLOSE_SCHEMA, sClose.getSequenceStatus(), sClose.getOwner(),
                sClose.getOwnerDeptId(), sClose.getOwnerDeptQueryCode(), sClose.getCreater(), sClose.getCreatedDeptId(), sClose.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        data.put("employee_name", sClose.getEmployeeName());
        data.put("gender", sClose.getGender());
        data.put("identityNo", sClose.getIdentityNo());
        data.put("birthday", sClose.getBirthday());
        data.put("welfare_handler", sClose.getWelfareHandler());
        data.put("start_month", sClose.getStartMonth());
        data.put("charge_end_month", sClose.getChargeEndMonth());
        data.put("resignation_remarks", sClose.getResignationRemarks());
        data.put("social_security_base", sClose.getSocialSecurityBase());
        data.put("identityNo_type", sClose.getIdentityNoType());
        data.put("operate_leader", sClose.getOperateLeader());
        data.put("employee_order_form_id", sClose.getEmployeeOrderFormId());
        data.put("status", sClose.getStatus());
        data.put("first_level_client_name", sClose.getFirstLevelClientName());
        data.put("second_level_client_name", sClose.getSecondLevelClientName());
        data.put("subordinate_department", sClose.getSubordinateDepartment());
        data.put("city", sClose.getCity());
        model.put(data);
        return model;
    }

    public static BizObjectModel getProvidentFundClose(ProvidentFundClose gClose) {
        BizObjectModel model = getBizObjectModel(Constants.PROVIDENT_FUND_CLOSE_SCHEMA, gClose.getSequenceStatus(),
                gClose.getOwner(), gClose.getOwnerDeptId(), gClose.getOwnerDeptQueryCode(), gClose.getCreater(),
                gClose.getCreatedDeptId(), gClose.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        data.put("employee_name", gClose.getEmployeeName());
        data.put("gender", gClose.getGender());
        data.put("identityNo", gClose.getIdentityNo());
        data.put("birthday", gClose.getBirthday());
        data.put("welfare_handler", gClose.getWelfareHandler());
        data.put("start_month", gClose.getStartMonth());
        data.put("charge_end_month", gClose.getChargeEndMonth());
        data.put("provident_fund_base", gClose.getProvidentFundBase());
        data.put("enterprise_deposit",gClose.getEnterpriseDeposit	());
        data.put("personal_deposit",gClose.getPersonalDeposit	());
        data.put("total_deposit",gClose.getTotalDeposit	());
        data.put("identityNo_type", gClose.getIdentityNoType());
        data.put("operate_leader", gClose.getOperateLeader());
        data.put("employee_order_form_id", gClose.getEmployeeOrderFormId());
        data.put("status", gClose.getStatus());
        data.put("first_level_client_name", gClose.getFirstLevelClientName());
        data.put("second_level_client_name", gClose.getSecondLevelClientName());
        data.put("subordinate_department", gClose.getSubordinateDepartment());
        data.put("city", gClose.getCity());
        model.put(data);
        return model;
    }
}
