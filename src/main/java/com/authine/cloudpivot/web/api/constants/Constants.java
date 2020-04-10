package com.authine.cloudpivot.web.api.constants;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.constants.Constants
 * @Date 2019/12/18 15:34
 **/
public class Constants {

    /**
     * 安徽省所有城市名称
     */
    public static final String ALL_CITIES_IN_ANHUI_PROVINCE = "合肥市、宿州市、淮北市、亳州市、阜阳市、蚌埠市、淮南市、滁州市、六安市、马鞍山市、安庆市、芜湖市、铜陵市、宣城市、池州市、黄山市";

    /**
     * 社保卡办理模型code: social_security_card
     */
    public static final String SOCIAL_SECURITY_CARD = "social_security_card";

    /**
     * 社保卡办理流程code: social_security_card_process
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS = "social_security_card_process";

    /**
     * 办理社保卡流程>>运行办卡节点CODE:process_social_security_card
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_PROCESS = "process_social_security_card";

    /**
     * 办理社保卡流程>>业务发卡节点CODE:give_out
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_GIVE_OUT = "give_out";

    /**
     * 办理社保卡流程>>业务递交办卡材料或办理失败时跟进节点CODE:track_records
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_UPLOAD_INFO = "upload_info";

    /**
     * 业务员角色id
     */
    public static final String SALESMAN_ROLE_ID = "";// TODO 根据业务角色获取所有业务员信息,角色id待定

    /**
     * 上海增员表单Schema
     */
    public static final String SH_ADD_EMPLOYEE_SCHEMA = "sh_add_employee";

    /**
     * 上海增员表单流程
     */
    public static final String SH_ADD_EMPLOYEE_SCHEMA_WF = "sh_add_employee_wf";

    /**
     * 上海减员表单Schema
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA = "sh_delete_employee";

    /**
     * 上海减员表单流程
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA_WF = "sh_delete_employee_wf";

    /**
     * 全国派单表单Schema
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA = "nationwide_dispatch";

    /**
     * 全国派单增员流程
     */
    public static final String NATIONWIDE_DISPATCH_WF = "nationwide_dispatch_wf";


    /**
     * 全国派单表单流程
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA_WF = "nationwide_dispatch_wf";

    /**
     * 全国派单减员名称
     */
    public static final String NATIONWIDE_DISPATCH_DELETE_SCHEMA = "nationwide_dispatch_delete";

    /**
     * 全国派单减员流程
     */
    public static final String DEL_NATIONWIDE_DISPATCH_WF = "del_nationwide_dispatch_wf";

    /**
     * 上海增员的表格名称
     */
    public static final String SH_ADD_EMPLOYEE_TABLE_NAME = "i4fvb_sh_add_employee";

    /**
     * 上海减员数据库名称
     */
    public static final String SH_DELETE_EMPLOYEE_TABLE_NAME = "i4fvb_sh_delete_employee";

    /**
     * 全国派单增员数据库名称
     */
    public static final String NATIONWIDE_DISPATCH_TABLE_NAME = "i4fvb_nationwide_dispatch";


    /**
     * 全国派单减员数据库名称
     */
    public static final String NATIONWIDE_DISPATCH_DELETE_TABLE_NAME = "i4fvb_nationwide_dispatch_delete";

    public static final String NATIONWIDE_DISPATCH = "nationwide_dispatch";

    /**
     * 批量预派表单名称
     */
    public static final String BATCH_PRE_DISPATCH_SCHEMA = "batch_pre_dispatch";

    /**
     * 批量撤离表单名称
     */
    public static final String BATCH_EVACUATION_SCHEMA = "batch_evacuation";

    /**
     * 草稿状态
     */
    public static final String DRAFT_STATUS = "DRAFT";

    /**
     * 生效状态
     */
    public static final String COMPLETED_STATUS = "COMPLETED";

    /**
     * 运行中状态
     */
    public static final String PROCESSING_STATUS = "PROCESSING";

    /**
     * 用户type
     */
    public static final Integer USER_TYPE = 3;

    /**
     * 部门type
     */
    public static final Integer DEPARTMENT_TYPE = 1;

    public static final Integer MAX_INSERT_NUM = 1000;

    /**
     * 增员表单Schema
     */
    public static final String ADD_EMPLOYEE_SCHEMA = "add_employee";

    /**
     * 增员表单流程
     */
    public static final String ADD_EMPLOYEE_SCHEMA_WF = "add_employee_wf";

    /**
     * 减员表单Schema
     */
    public static final String DELETE_EMPLOYEE_SCHEMA = "delete_employee";

    /**
     * 减员表单流程
     */
    public static final String DELETE_EMPLOYEE_SCHEMA_WF = "delete_employee_wf";

    /**
     * 社保停缴表单Schema
     */
    public static final String SOCIAL_SECURITY_CLOSE_SCHEMA = "social_security_close";

    /**
     * 社保停缴表单流程
     */
    public static final String SOCIAL_SECURITY_CLOSE_SCHEMA_WF = "social_security_close_wf";

    /**
     * 公积金停缴表单Schema
     */
    public static final String PROVIDENT_FUND_CLOSE_SCHEMA = "provident_fund_close";

    /**
     * 公积金停缴表单流程
     */
    public static final String PROVIDENT_FUND_CLOSE_SCHEMA_WF = "provident_fund_close_wf";

    /**
     * 入职通知表单Schema
     */
    public static final String ENTRY_NOTICE_SCHEMA = "entry_notice";

    /**
     * 入职通知流程
     */
    public static final String ENTRY_NOTICE_SCHEMA_WF = "entry_notice_wf";

    /**
     * 员工订单表单Schema
     */
    public static final String EMPLOYEE_ORDER_FORM_SCHEMA = "employee_order_form";

    /**
     * 社保申报表单Schema
     */
    public static final String SOCIAL_SECURITY_DECLARE_SCHEMA = "social_security_declare";

    /**
     * 社保申报表单流程
     */
    public static final String SOCIAL_SECURITY_DECLARE_SCHEMA_WF = "social_security_declare_wf";

    /**
     * 公积金申报表单Schema
     */
    public static final String PROVIDENT_FUND_DECLARE_SCHEMA = "provident_fund_declare";

    /**
     * 公积金申报表单流程
     */
    public static final String PROVIDENT_FUND_DECLARE_SCHEMA_WF = "provident_fund_declare_wf";

    /**
     * 员工订单表单》“社保公积金”子表绑定数据
     */
    public static final String SOCIAL_SECURITY_FUND_DETAIL = "social_security_fund_detail";

    /**
     * 社保申报表单》“社保详细”子表绑定数据
     */
    public static final String SOCIAL_SECURITY_DETAIL = "social_security_detail";

    /**
     * 公积金申报表单》“公积金详细”子表绑定数据
     */
    public static final String PROVIDENT_FUND_DETAIL = "provident_fund_detail";

    /**
     * 员工档案表单Schema
     */
    public static final String EMPLOYEE_FILES_SCHEMA = "employee_files";

    /**
     * 操作类型：提交submit
     */
    public static final String OPERATE_TYPE_SUBMIT = "submit";

    /**
     * 操作类型：驳回reject
     */
    public static final String OPERATE_TYPE_REJECT = "reject";

    /**
     * 社保：social_security
     */
    public static final String SOCIAL_SECURITY = "social_security";

    /**
     * 公积金：provident_fund
     */
    public static final String PROVIDENT_FUND = "provident_fund";

    public static final String[] PARSE_PATTERNS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "HH:mm:ss"};

    public static final String ADMIN_ID = "2c9280a26706a73a016706a93ccf002b";

    /**
     * 验证码历史记录表单code
     */
    public static final String SMS_HISTORY_SCHEMA = "sms_history";

    public static final String PAY_METHOD = "代收代付";

    /**
     * 发起基数采集SCHEMA_CODE
     */
    public static final String SUBMIT_COLLECT_SCHEMA = "submit_collect";

}
