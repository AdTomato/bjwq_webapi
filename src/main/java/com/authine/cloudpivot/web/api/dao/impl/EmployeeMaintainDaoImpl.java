package com.authine.cloudpivot.web.api.dao.impl;

import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.impl.EmployeeMaintainDaoImpl
 * @Date 2020/2/10 17:18
 **/
@Repository
@Slf4j
public class EmployeeMaintainDaoImpl implements EmployeesMaintainDao {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 方法说明：根据员工档案的单据号获取社保公积金信息
     * @Param sequenceNo
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/10 17:35
     */
    @Override
    public Map <String, String> getSocialSecurityFundDetail(String employeeFilesId) throws Exception {
        // 根据单据号获取
        String sql = " select id, date_format(start_time, '%Y-%m-%d') start_time from i4fvb_employee_order_form where employee_files_id = '" +
                employeeFilesId + "' and is_history = '否'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);

        Map <String, String> ssf = new HashMap <>();
        if (list != null && list.size() > 0) {
            String id = list.get(0).get("id").toString();
            ssf.put("employeeOrderFormId", id);
            ssf.put("startMonth", list.get(0).get("start_time") == null ? "" :
                    list.get(0).get("start_time").toString());
            // 获取社保基数
            String sql1 = "select max(base_num) base_num from i4fvb_social_security_fund_detail " +
                    " where social_security_group like '%社保%' and parentId = '" + id + "' ";
            List <Map <String, Object>> list1 = ConnectionUtils.executeSelectSql(sql1);
            if (list1 != null && list1.size() > 0) {
                ssf.put("socialSecurityBase", list1.get(0).get("base_num") == null ? "" :
                        list1.get(0).get("base_num").toString());
            }
            // 获取公积金基数
            String sql2 = "select base_num,company_money,employee_money, IFNULL(company_money,0) + IFNULL" +
                    "(employee_money,0) total  from i4fvb_social_security_fund_detail detail where social_security_group " +
                    "like '%公积金%' and parentId = '" + id + "' ";
            List <Map <String, Object>> list2 = ConnectionUtils.executeSelectSql(sql2);
            if (list2 != null && list2.size() > 0) {
                ssf.put("providentFundBase", list2.get(0).get("base_num") == null ? "" :
                        list2.get(0).get("base_num").toString());
                ssf.put("enterpriseDeposit", list2.get(0).get("company_money") == null ? "" :
                        list2.get(0).get("company_money").toString());
                ssf.put("personalDeposit", list2.get(0).get("employee_money") == null ? "" :
                        list2.get(0).get("employee_money").toString());
                ssf.put("totalDeposit", list2.get(0).get("total") == null ? "" :
                        list2.get(0).get("total").toString());
            }
        } else {
            throw new Exception("没有获取到对应的员工订单。");
        }
        return ssf;
    }

    /**
     * 方法说明：根据补缴月份是，地区获取入职通知信息
     * @Param monthDifference
     * @Param city
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/12 13:38
     */
    @Override
    public Map <String, String> getEntryNoticeBy(int monthDifference, String city) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append(" select list.record_of_employment, list.social_security, list.provident_fund, leader.social_security_leader ");
        sql.append(" from id34a_provincial_entry_list list");
        sql.append(" left join id34a_operate_leader leader on list.city = leader.city ");
        sql.append(" where list.city = '" + city + "'" );
        sql.append(" and ( pay_back_min_month is null or pay_back_min_month <= " + monthDifference + ")" );
        sql.append(" and ( pay_back_max_month is null or pay_back_min_month >= " + monthDifference + ")" );

        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql.toString());
        Map <String, String> entryNotice = new HashMap <>();
        entryNotice.put("haveEntryNotice", "false");// 没有入职通知
        if (list != null && list.size() > 0) {
            entryNotice.put("haveEntryNotice", "true");// 有入职通知
            entryNotice.put("record_of_employment", list.get(0).get("record_of_employment") == null ? "" :
                    list.get(0).get("record_of_employment").toString());
            entryNotice.put("social_security", list.get(0).get("social_security") == null ? "" : list.get(0).get(
                    "social_security").toString());
            entryNotice.put("provident_fund", list.get(0).get("provident_fund") == null ? "" : list.get(0).get(
                    "provident_fund").toString());
            entryNotice.put("operate_signatory", list.get(0).get("social_security_leader") == null ? "" : list.get(0).get("social_security_leader").toString());
        }
        return entryNotice;
    }

    @Override
    public void createSocialSecurityFundDetail(String parentId, List <Map <String, String>> detail, String code) throws Exception {
        if (StringUtils.isBlank(parentId)) {
            throw new Exception("创建流程出错。");
        }
        if (detail != null && detail.size() > 0) {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append(" INSERT INTO ");
            sqlBuffer.append(" i4fvb_" + code);
            sqlBuffer.append(" (");
            sqlBuffer.append(" product_name, social_security_group, base_num, sum, company_money,");
            sqlBuffer.append(" employee_money, company_ratio, employee_ratio, company_surcharge_value,");
            sqlBuffer.append(" employee_surcharge_value, precollected, pay_cycle, start_charge_time,");
            sqlBuffer.append(" end_charge_time, name_hide, id, parentId");
            sqlBuffer.append(" )");
            sqlBuffer.append(" VALUES ");
            for (int i = 0; i < detail.size(); i++) {
                String productName = StringUtils.isNotBlank(detail.get(i).get("product_name")) ? detail.get(i).get(
                        "product_name") : "";
                String socialSecurityGroup = StringUtils.isNotBlank(detail.get(i).get("social_security_group")) ?
                        detail.get(i).get("social_security_group") : "";
                String precollected = StringUtils.isNotBlank(detail.get(i).get("precollected")) ? detail.get(i).get(
                        "precollected") : "";
                String payCycle = StringUtils.isNotBlank(detail.get(i).get("pay_cycle")) ? detail.get(i).get(
                        "pay_cycle") : "";
                String nameHide = StringUtils.isNotBlank(detail.get(i).get("name_hide")) ? detail.get(i).get(
                        "name_hide") : "";

                sqlBuffer.append(" (");
                sqlBuffer.append(" '" + productName + "',");
                sqlBuffer.append(" '" + socialSecurityGroup + "',");
                if (StringUtils.isNotBlank(detail.get(i).get("base_num"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("base_num") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("sum"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("sum") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("company_money"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("company_money") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("employee_money"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("employee_money") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("company_ratio"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("company_ratio") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("employee_ratio"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("employee_ratio") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("company_surcharge_value"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("company_surcharge_value") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("employee_surcharge_value"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("employee_surcharge_value") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }

                sqlBuffer.append(" '" + precollected + "',");
                sqlBuffer.append(" '" + payCycle + "',");
                if (StringUtils.isNotBlank(detail.get(i).get("start_charge_time"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("start_charge_time") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                if (StringUtils.isNotBlank(detail.get(i).get("end_charge_time"))) {
                    sqlBuffer.append(" '" + detail.get(i).get("end_charge_time") + "',");
                } else {
                    sqlBuffer.append(" NULL,");
                }
                sqlBuffer.append(" '" + nameHide + "',");
                sqlBuffer.append("MD5(UUID()),");
                sqlBuffer.append("'" + parentId + "'");
                sqlBuffer.append(" ),");
            }
            String sql = sqlBuffer.toString();
            ConnectionUtils.executeSql(sql.substring(0, sql.length() - 1));
        }
    }

    /**
     * 方法说明：根据客户名称获取派出单位
     * @Param clientName
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/14 11:05
     */
    @Override
    public String getDispatchUnitByClientName(String clientName) throws Exception {
        String sql = "select entrusting_party from id34a_client_management where client_name = '" + clientName + "'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() > 0) {
            return list.get(0).get("entrusting_party") == null ? "" : list.get(0).get("entrusting_party").toString();
        }
        return "";
    }

    /**
     * 方法说明：根据客户名称和地区获取福利办理方
     * @Param clientName
     * @Param city
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/14 11:16
     */
    @Override
    public String getWelfareHandlerByClientNameAndCity(String clientName, String city) throws Exception {
        String sql = "select welfare_handler from id34a_appointment_sheet where client_name = '" + clientName + "' " +
                "and city = '" + city + "'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() > 0) {
            return list.get(0).get("welfare_handler") == null ? "" : list.get(0).get("welfare_handler").toString();
        }
        return "";
    }

    @Override
    public Map <String, String> getTimeNode(String clientName, String city) throws Exception {

        String sql = "select company_injury_ratio, employee_injury_ratio, company_accumulation_ratio, " +
                "employee_accumulation_ratio, time_node from id34a_ccps where client = '" + clientName + "'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        Map <String, String> map = new HashMap <>();
        if (list != null && list.size() > 0) {
            map.put("company_injury_ratio", list.get(0).get("company_injury_ratio") == null ? "" : list.get(0).get(
                    "company_injury_ratio").toString());
            map.put("employee_injury_ratio", list.get(0).get("employee_injury_ratio") == null ? "" : list.get(0).get(
                    "employee_injury_ratio").toString());
            map.put("company_accumulation_ratio", list.get(0).get("company_accumulation_ratio") == null ? "" :
                    list.get(0).get("company_accumulation_ratio").toString());
            map.put("employee_accumulation_ratio", list.get(0).get("employee_accumulation_ratio") == null ? "" :
                    list.get(0).get("employee_accumulation_ratio").toString());
            map.put("time_node", list.get(0).get("time_node") == null ? "" : list.get(0).get("time_node").toString());
        } else {
            String sql1 = "select time_node from id34a_city_time_node where city = '" + city + "'";
            List <Map <String, Object>> list1 = ConnectionUtils.executeSelectSql(sql1);
            if (list1 != null && list1.size() > 0) {
                map.put("time_node", list1.get(0).get("time_node") == null ? "" :
                        list1.get(0).get("time_node").toString());
            } else {
                throw new Exception("没有获取对应的时间节点。");
            }
        }
        return map;
    }

    @Override
    public String getEmployeeFilesSequenceNoById(String id) throws Exception {
        String sql = "select sequenceNo from i4fvb_employee_files where id = '" + id + "'";
        List <Map <String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        if (list != null && list.size() > 0){
            return list.get(0).get("sequenceNo") == null ? "" : list.get(0).get("sequenceNo").toString();
        }
        return "";
    }

    @Override
    public List <Map <String, Object>> getSocialSecurityFundDetail(String city, Date startMonth, String type,
                                                                   Double setBase) throws Exception {
        List <Map <String, Object>> list = new ArrayList <>();
        if (StringUtils.isBlank(city) || startMonth == null) {
            return list;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String startTime = sdf.format(startMonth);

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT");
        sql.append("   pay.id,");
        sql.append("   pay.product_name,");
        sql.append("   pay.social_security_group,");
        sql.append("   base.employee_max_base_num max_base,");
        sql.append("   base.employee_min_base_num min_base,");
        if (setBase != null) {
            sql.append(" case");
            sql.append("  when base.employee_max_base_num <= " + setBase +  " then base.employee_max_base_num");
            sql.append("  when base.employee_min_base_num >= " + setBase +  " then base.employee_min_base_num");
            sql.append("  else " + setBase +  " end");
            sql.append(" base,");
        }
        sql.append("   pay.company_ratio,");
        sql.append("   pay.employee_ratio,");
        sql.append("   pay.company_rounding_policy,");
        sql.append("   pay.employee_rounding_policy,");
        sql.append("   pay.company_precision,");
        sql.append("   pay.employee_precision,");
        sql.append("   pay.company_surcharge_value,");
        sql.append("   pay.employee_surcharge_value,");
        sql.append("   pay.pay_cycle,");
        sql.append("   rule.pay_month");
        sql.append(" FROM");
        sql.append("   i080j_policy_collect_pay pay");
        sql.append("   LEFT JOIN i080j_product_base_num base ON pay.id = base.parentId");
        sql.append("   LEFT JOIN i4fvb_collection_rule_maintain rule ON pay.id = rule.product_name");
        sql.append(" WHERE");
        sql.append("   pay.social_security_group LIKE '%" + type + "%'");
        sql.append("   AND date_format( base.start_time, '%Y-%m' ) <= '" + startTime + "'");
        sql.append("   AND ( base.end_time IS NULL OR date_format( base.end_time, '%Y-%m' ) >= '" + startTime + "')");

        list = ConnectionUtils.executeSelectSql(sql.toString());

        return list;
    }

    @Override
    public void updateEmployeeOrderFormStatus(String ids, String field, String status) throws Exception {
        String sql = "update i4fvb_employee_order_form set " + field + " = '" + status + "' " +
                "where id in ('" + ids.replaceAll(",", "','") + "') ";
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateEmployeeOrderFormIsHistory(String oldId) throws Exception {
        String sql = "update i4fvb_employee_order_form set is_history = '是' where id = '" + oldId +"'";
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void createSocialSecurityFundDetail(String id, String oldId, String newId, String type) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO ");
        sql.append(" i4fvb_social_security_fund_detail");
        sql.append(" (");
        sql.append(" product_name, social_security_group, base_num, sum, company_money,");
        sql.append(" employee_money, company_ratio, employee_ratio, company_surcharge_value,");
        sql.append(" employee_surcharge_value, precollected, pay_cycle, start_charge_time,");
        sql.append(" end_charge_time,name_hide, id, parentId");
        sql.append(" )");
        sql.append(" select * ");
        sql.append(" from (");
        sql.append(" select detail.product_name, detail.social_security_group, detail.base_num, detail.sum,");
        sql.append(" detail.company_money, detail.employee_money, detail.company_ratio, detail.employee_ratio,");
        sql.append(" detail.company_surcharge_value,detail.employee_surcharge_value, detail.precollected,");
        sql.append(" detail.pay_cycle, detail.start_charge_time, detail.end_charge_time, detail.name_hide, MD5(UUID()), '" + newId + "' ");
        sql.append(" from i4fvb_social_security_fund_detail detail where 1=1");
        if ("social_security".equals(type)) {
            sql.append(" and detail.social_security_group like '%公积金%' ");
        } else if ("provident_fund".equals(type)) {
            sql.append(" and detail.social_security_group like '%社保%' ");
        }
        sql.append(" and detail.parentId = '" + oldId + "' ");

        sql.append(" union all");

        sql.append(" select product_name, social_security_group, base_num, sum, company_money,");
        sql.append(" employee_money, company_ratio, employee_ratio, company_surcharge_value,");
        sql.append(" employee_surcharge_value, precollected, pay_cycle, start_charge_time,");
        sql.append(" end_charge_time, name_hide, MD5(UUID()), '" + newId + "'");
        if ("social_security".equals(type)) {
            sql.append(" from i4fvb_social_security_detail ");
        } else if ("provident_fund".equals(type)) {
            sql.append(" from i4fvb_provident_fund_detail ");
        }
        sql.append(" where parentId = '" + id + "' ");

        sql.append(" ) as a ");

        ConnectionUtils.executeSql(sql.toString());
    }

    @Override
    public List <Map <String, Object>> getSocialSecurityFundDetailByParentId(String parentId) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select product.product_name, detail.sum, detail.company_money, detail.employee_money");
        sql.append(" from i4fvb_social_security_fund_detail detail");
        sql.append(" join i080j_policy_collect_pay product on detail.product_name = product.id ");
        sql.append(" where detail.parentId = '" + parentId + "'");
        return ConnectionUtils.executeSelectSql(sql.toString());
    }

    @Override
    public void updateEmployeeOrderFormInfo(String id, Map <String, String> info) throws Exception {
        String endowment = StringUtils.isBlank(info.get("endowment")) ? "" : info.get("endowment");
        String medical = StringUtils.isBlank(info.get("medical")) ? "" : info.get("medical");
        String unemployment = StringUtils.isBlank(info.get("unemployment")) ? "" : info.get("unemployment");
        String workRelatedInjury = StringUtils.isBlank(info.get("work_related_injury")) ? "" : info.get(
                "work_related_injury");
        String childbirth = StringUtils.isBlank(info.get("childbirth")) ? "" : info.get("childbirth");
        String criticalIllness = StringUtils.isBlank(info.get("critical_illness")) ? "" : info.get("critical_illness");
        String housingAccumulationFunds = StringUtils.isBlank(info.get("housing_accumulation_funds")) ? "" :
                info.get("housing_accumulation_funds");

        StringBuffer sql = new StringBuffer();
        sql.append(" update i4fvb_employee_order_form set");
        if (StringUtils.isNotBlank(info.get("total"))) {

            sql.append(" total = " + info.get("total") + ",");
        }
        if (StringUtils.isNotBlank(info.get("sum"))) {
            sql.append(" sum = " + info.get("sum") + ",");
        }
        sql.append(" endowment = '" + endowment + "',");
        sql.append(" medical = '" + medical + "',");
        sql.append(" unemployment = '"+ unemployment + "',");
        sql.append(" work_related_injury = '"+ workRelatedInjury + "',");
        sql.append(" childbirth = '"+ childbirth + "',");
        sql.append(" critical_illness = '"+ criticalIllness + "',");
        sql.append(" housing_accumulation_funds = '"+ housingAccumulationFunds + "' ");
        sql.append(" where id = '" + id +"'");

        ConnectionUtils.executeSql(sql.toString());
    }

    @Override
    public List <Map <String, Object>> getWorkItemInfo(String userId, String ids, String code, String operateType) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT * from (");
        sql.append(" SELECT ");
        sql.append("    code.id, code.employee_order_form_id, work.id workItemId,");
        sql.append("    work.sourceId, code.is_change, code.sequenceStatus,code.workflowInstanceId");
        sql.append(" FROM");
        sql.append("   i4fvb_" + code + " code");// 表单
        sql.append("   LEFT JOIN biz_workflow_instance instance ON instance.bizObjectId = code.id");
        sql.append("   LEFT JOIN biz_workitem work ON work.instanceId = instance.id ");
        sql.append(" WHERE ");
        sql.append("   code.id in ('" + ids.replaceAll(",", "','") + "')");
        if (Constants.OPERATE_TYPE_REJECT.equals(operateType)) {
            // 驳回操作
            // 填写申请单的节点不可以驳回,已完结的流程没有流程节点
            sql.append("   AND (work.sourceId != 'fill_application' or code.sequenceStatus = 'COMPLETED')");
        } else if (Constants.OPERATE_TYPE_SUBMIT.equals(operateType)){
            // 已完结的流程无法提交
            sql.append("   AND code.sequenceStatus != 'COMPLETED'");
        }
        // 当前用户是流程处理人或是办结流程
        sql.append("   AND (work.participant = '" + userId + "' or code.sequenceStatus = 'COMPLETED')");

        if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code) || Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(code)) {
            // 社保停缴,公积金停缴办结的不可以在操作驳回
            sql.append("   AND code.sequenceStatus != 'COMPLETED'");
        }
        sql.append(" ) as a ");
        return ConnectionUtils.executeSelectSql(sql.toString());
    }

    @Override
    public String getOperateLeaderByCity(String city) throws Exception {
        String sql = " select leader from id34a_operate_leader where city = '" + city + "'";
        List<Map<String, Object>> list = ConnectionUtils.executeSelectSql(sql);
        String leader = "";
        if (list != null && list.size() > 0) {
            leader = list.get(0).get("leader").toString();
        }
        return leader;
    }

    @Override
    public void updateEmployeeFilesWhenDelEmployee(EmployeeFiles employeeFiles) throws Exception {
        StringBuffer updateSql = new StringBuffer();
        updateSql.append(" UPDATE i4fvb_employee_files  SET");
        updateSql.append(getDateSql("report_quit_date", employeeFiles.getReportQuitDate()) + ",");
        updateSql.append(getStringSql("report_severance_officer", employeeFiles.getReportSeveranceOfficer()) + ",");
        updateSql.append(getDateSql("quit_date", employeeFiles.getQuitDate()) + ",");
        updateSql.append(getDateSql("social_security_charge_end", employeeFiles.getSocialSecurityChargeEnd()) + ",");
        updateSql.append(getDateSql("provident_fund_charge_end", employeeFiles.getProvidentFundChargeEnd()) + ",");
        updateSql.append(getStringSql("quit_reason", employeeFiles.getQuitReason()) + ",");
        updateSql.append(getStringSql("quit_remark", employeeFiles.getQuitRemark()));
        updateSql.append(" WHERE");
        updateSql.append(getStringSql("id", employeeFiles.getId()));

        ConnectionUtils.executeSql(updateSql.toString());
    }

    @Override
    public void updateSocialSecurityClose(String clientName, String identityNo, Date chargeEndMonth,
                                          String resignationRemarks) throws Exception {
        StringBuffer updateSql = new StringBuffer();
        updateSql.append(" UPDATE i4fvb_social_security_close  SET");
        updateSql.append(getDateSql("charge_end_month", chargeEndMonth) + ",");
        updateSql.append(getStringSql("resignation_remarks", resignationRemarks));
        updateSql.append(" WHERE");
        updateSql.append(getStringSql("client_name", clientName) + " AND");
        updateSql.append(getStringSql("identityNo", identityNo));

        ConnectionUtils.executeSql(updateSql.toString());
    }

    @Override
    public void updateProvidentFundClose(String clientName, String identityNo, Date chargeEndMonth) throws Exception {
        StringBuffer updateSql = new StringBuffer();
        updateSql.append(" UPDATE i4fvb_provident_fund_close  SET");
        updateSql.append(getDateSql("charge_end_month", chargeEndMonth));
        updateSql.append(" WHERE");
        updateSql.append(getStringSql("client_name", clientName) + " AND");
        updateSql.append(getStringSql("identityNo", identityNo));

        ConnectionUtils.executeSql(updateSql.toString());
    }

    @Override
    public void updateEmployeeFiles(String clientName, String identityNo, Date quitDate, Date socialSecurityChargeEnd,
                                    Date providentFundChargeEnd, String quitReason, String quitRemark) throws Exception {
        StringBuffer updateSql = new StringBuffer();
        updateSql.append(" UPDATE i4fvb_employee_files  SET");
        updateSql.append(getDateSql("quit_date", quitDate) + ",");
        updateSql.append(getDateSql("social_security_charge_end", socialSecurityChargeEnd) + ",");
        updateSql.append(getDateSql("provident_fund_charge_end", providentFundChargeEnd) + ",");
        updateSql.append(getStringSql("quit_reason", quitReason) + ",");
        updateSql.append(getStringSql("quit_remark", quitRemark));
        updateSql.append(" WHERE");
        updateSql.append(getStringSql("client_name", clientName) + " AND");
        updateSql.append(getStringSql("id_no", identityNo));

        ConnectionUtils.executeSql(updateSql.toString());
    }

    @Override
    public void updateDeleteEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception {
        String[] fieldName = new String[]{"client_name", "employee_name", "identityNo_type", "identityNo", "city",
                "leave_reason", "leave_time", "social_security_end_time", "provident_fund_end_time", "remark"};
        String onnectionCondition = "targetTable.id = sourceTable.delete_employee_id";
        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo("i4fvb_delete_employee", targetId,
                "i4fvb_delete_employee_update", sourceId, onnectionCondition, fieldName);
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateShDeleteEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception {
        String[] fieldName = new String[]{"client_name", "identityNo_type", "identityNo", "client_num", "client_name",
                "client_short_name", "OS_initiated_departure_time", "departure_time", "charge_end_time",
                "leave_reason", "leave_remark", "provident_fund_transfer_mode", "backtrack", "GE_leave_reason",
                "customer_num", "weather_leave_E"};
        String onnectionCondition = "targetTable.id = sourceTable.sh_delete_employee_id";

        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo("i4fvb_sh_delete_employee",
                targetId, "i4fvb_sh_delete_employee_update", sourceId, onnectionCondition, fieldName);
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateNationwideDispatchBetweenTwoTables(String sourceId, String targetId, String type) throws Exception {
        String[] fieldName = new String[]{"unique_num", "employee_name", "contact_number", "identityNo_type",
                "identityNo", "national_business_wf_flag", "involved", "business_customer_num",
                "business_customer_name", "business_wf_status", "order_type", "processing_status",
                "contracting_supplier", "contracting_representative", "contracting_party_department",
                "into_pending_date", "task_list_update_date", "entry_date", "employee_internal_num", "employee_email",
                "order_id", "order_start_date", "assignment_date", "entry_procedures_num", "business_tips",
                "order_end_date", "withdrawal_date", "social_insurance_amount", "provident_fund_amount",
                "supple_provident_fund_amount", "revocation_reason", "social_security_stop_reason", "departure_date",
                "departure_remark", "change_details", "change_take_effect_date", "business_unit", "salesman",
                "social_security_pay_method", "provident_fund_pay_method", "supplier_agreement_name",
                "provident_fund_ratio", "supple_provident_fund_ratio", "social_security_standard",
                "s_service_fee_start_date", "s_service_fee_end_date", "g_service_fee_start_date",
                "g_service_fee_end_date", "remark"};
        // 根据源表修改目标表数据 targetTable targetId,sourceTable sourceId, String[] fieldName
        String targetTable = "add".equals(type) ? "i4fvb_nationwide_dispatch" : "i4fvb_nationwide_dispatch_delete";
        String sourceTable = "add".equals(type) ? "i4fvb_nationwide_dispatch_update" : "i4fvb_nation_delete_update";
        String onnectionCondition = "add".equals(type) ? "targetTable.id = sourceTable.nationwide_dispatch_id" :
                "targetTable.id = sourceTable.nationwide_dispatch_del_id";

        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo(targetTable, targetId, sourceTable, sourceId,
                onnectionCondition, fieldName);

        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void UpdateEmployeeFilesBetweenTwoTables(String sourceId, String targetId) throws Exception {
        String[] fieldName = new String[]{"entrusted_unit", "client_name", "salesman", "employee_name", "id_type",
                "id_no", "gender", "birth_date", "employee_nature", "household_register_nature", "mobile", "position"
                , "employee_labels", "email", "labour_contract_start_time", "labour_contract_end_time", "salary",
                "probation_start_time", "probation_end_time", "probation_salary", "social_security_city",
                "provident_fund_city", /*"report_entry_time", "report_recruits", 报入职时间，报入职人不变*/
                "entry_time", "social_security_charge_start", "provident_fund_charge_start", "social_security_area",
                "provident_fund_area", "entry_description", "entry_notice", "health_check", "whether_pay",
                "report_quit_date", "report_severance_officer", "quit_date", "social_security_charge_end",
                "provident_fund_charge_end", "quit_reason", "quit_remark", "bank_account_number", "bank_name",
                "bank_area"};
        String onnectionCondition = "targetTable.id = sourceTable.employee_files_id";

        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo("i4fvb_employee_files", targetId,
                "i4fvb_employee_files_update", sourceId, onnectionCondition, fieldName);

        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateAddEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception {
        String[] fieldName = new String[]{"client_name", "ERP", "employee_name", "identityNo_type", "identityNo",
                "mobile", "email", "family_register_nature", "employee_nature", "entry_time", "contract_start_time",
                "contract_end_time", "contract_salary", "social_security_city", "social_security_start_time",
                "social_security_base", "provident_fund_city", "provident_fund_start_time", "provident_fund_base",
                "company_provident_fund_bl", "employee_provident_fund_bl", "remark"};
        String onnectionCondition = "targetTable.id = sourceTable.add_employee_id";

        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo("i4fvb_add_employee", targetId,
                "i4fvb_add_employee_update", sourceId, onnectionCondition, fieldName);

        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateShAddEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception {
        String[] fieldName = new String[]{"employee_name", "unique_num", "identityNo", "commission_send",
                "identityNo_type", "order_code", "quotation_code", "sys_client_num", "client_name",
                "client_short_name", "project_proposals_name", "provident_fund_pay_direct",
                "social_security_pay_direct", "service_fee", "with_file", "package_involves_procedures", "entry_time",
                "benefit_start_time", "mobile", "provident_fund_start_time", "whether_consistent",
                "social_security_section", "social_security_base", "provident_fund_base",
                "p_supplement_provident_fund", "u_supplement_provident_fund", "supplement_provident_fund_b", "charge_start_date",
                "dispatch_period_start_date", "dispatch_deadline", "start_date_trial", "end_time_trial", "wage_trial"
                , "start_date_positive", "end_date_positive", "wage_positive", "phone", "employee_customer_side_num",
                "contact_address", "postal_code", "Hukou_location", "mail", "bank_name", "bank_account",
                "account_province_name", "account_city_name", "account_name", "bank_category", "city_name",
                "tax_properties", "job_num", "HRO", "business_unit", "employee_status", "synchronous_CSS",
                "Induction_remark", "tax_bureau", "employee_attributes", "social_security_standards", "weather_online",
                "work_system", "weather_Induction_E"};
        String onnectionCondition = "targetTable.id = sourceTable.sh_add_employee_id";

        // 获取sql:根据源表修改目标表数据
        String sql = getUpdateSqlByTargetTableAndSourceTableInfo("i4fvb_sh_add_employee", targetId,
                "i4fvb_sh_add_employee_update", sourceId,onnectionCondition, fieldName);

        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void deleteChildTableDataByTableNameAndParentId(String tableName, String parentId) throws Exception {
        String sql = "delete from " + tableName + " where parentId = '" + parentId + "'";
        ConnectionUtils.executeSql(sql);
    }

    @Override
    public void updateDeclareEmployeeOrderFormId(String oldId, String newId) throws Exception {
        String sql1 = "update i4fvb_social_security_declare set employee_order_form_id = '" + newId + "' where " +
                "employee_order_form_id = '" + oldId + "'";
        String sql2 = "update i4fvb_provident_fund_declare set employee_order_form_id = '" + newId + "' where " +
                "employee_order_form_id = '" + oldId + "'";
        String sqlArr[] = {sql1, sql2};
        ConnectionUtils.executeSql(sqlArr);
    }

    @Override
    public List <Map <String, Object>> getAddOrDelWorkItemId(String ids, String tableName) throws Exception {
       String sql = " SELECT * FROM( SELECT code.id, workitem.id workItemId FROM " + tableName + " code " +
               " JOIN biz_workflow_instance instance ON instance.bizObjectId = code.id" +
               " JOIN biz_workitem workitem ON workitem.instanceId = instance.id" +
               " WHERE code.sequenceStatus != 'COMPLETED' AND code.id IN ('"+ ids.replaceAll(",", "','") +"') ) AS a";
        return ConnectionUtils.executeSelectSql(sql);
    }

    /**
     * 方法说明：获取sql:根据源表修改目标表数据
     * @param targetTable 目标表
     * @param targetId 目标表id
     * @param sourceTable 来源表
     * @param connectionCondition 连接条件
     * @param fieldName 修改的字段
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/27 15:49
     */
    private String getUpdateSqlByTargetTableAndSourceTableInfo(String targetTable, String targetId, String sourceTable,
                                                               String sourceId, String connectionCondition,
                                                               String[] fieldName) throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE " + targetTable + " targetTable");
        sql.append(" JOIN " + sourceTable + " sourceTable ON " + connectionCondition);
        sql.append(" SET ");
        for (int i = 0; i < fieldName.length; i++) {
            sql.append(" targetTable." + fieldName[i] + " = sourceTable." + fieldName[i] + ",");
        }
        // 去除最后一个“,”
        String updateSql = sql.toString();
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        // 添加WHERE条件
        updateSql += " WHERE targetTable.id = '" + targetId + "' and sourceTable.id = '" + sourceId + "'";

        return updateSql;
    }

    /**
     * 方法说明：根据字段名称和修改后的值，获取update字段sql
     * @param fieldName 字段名称
     * @param date 修改后值
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/29 13:09
     */
    private String getDateSql(String fieldName, Date date) {
        String sql = " " + fieldName + " = ";
        if (date == null) {
            sql += "NULL";
        } else {
            sql += "'" + sdf.format(date) + "'";
        }
        return sql;
    }

    /**
     * 方法说明：根据字段名称和修改后的值，获取update字段sql
     * @param fieldName 字段名称
     * @param str 修改后值
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/29 13:10
     */
    private String getStringSql(String fieldName, String str) {
        String sql = " " + fieldName + " = ";
        if (StringUtils.isNotBlank(str)) {
            sql += "'" + str + "'";
        } else {
            sql += "''";
        }
        return sql;
    }

    /**
     * 方法说明：根据字段名称和修改后的值，获取update字段sql
     * @param fieldName 字段名称
     * @param num 修改后值
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/29 13:10
     */
    private String getDoubelSql(String fieldName, Double num) {
        String sql = " " + fieldName + " = ";
        if (num == null) {
            sql += "NULL ";
        } else {
            sql += "'" + num + "'";
        }
        return sql;
    }

    /**
     * 方法说明：针对社保公积金数据，根据来源表数据，生成目标表数据
     * @Param targetTable 目标表
     * @Param sourceTable 来源表
     * @Param targetParentId 目标表parentId
     * @Param sourceParentId 来源表parentId
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/21 10:16
     */
    private String getInsertSocialSecurityFundSql(String targetTable, String sourceTable, String targetParentId,
                                                  String sourceParentId) {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append(" INSERT INTO " + targetTable);
        insertSql.append(" (");
        insertSql.append("  product_name, social_security_group, base_num, sum, company_money,");
        insertSql.append("  employee_money, company_ratio, employee_ratio, company_surcharge_value,");
        insertSql.append("  employee_surcharge_value, precollected, pay_cycle, start_charge_time,");
        insertSql.append("  end_charge_time, id, parentId");
        insertSql.append(" )");
        insertSql.append(" SELECT");
        insertSql.append("  product_name, social_security_group, base_num, sum, company_money,");
        insertSql.append("  employee_money, company_ratio, employee_ratio, company_surcharge_value,");
        insertSql.append("  employee_surcharge_value, precollected, pay_cycle, start_charge_time,");
        insertSql.append("  end_charge_time, MD5(UUID()), '" + targetParentId + "'");
        insertSql.append(" FROM " + sourceTable);
        insertSql.append(" WHERE parentId = '" + sourceParentId + "'");

        return insertSql.toString();
    }
}
