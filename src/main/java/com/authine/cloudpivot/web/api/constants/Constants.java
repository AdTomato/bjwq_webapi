package com.authine.cloudpivot.web.api.constants;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.constants.Constants
 * @Date 2019/12/18 15:34
 **/
public class Constants {

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
     * 上海增员的表格名称
     */
    public static final String SH_ADD_EMPLOYEE_TABLE_NAME = "i4fvb_sh_add_employee";

    /**
     * 上海增员表单流程
     */
    public static final String SH_ADD_EMPLOYEE_SCHEMA_WF = "sh_add_employee_wf";

    /**
     * 上海减员表单Schema
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA = "sh_delete_employee";

    /**
     * 上海减员表格名称
     */
    public static final String SH_DELETE_EMPLOYEE_TABLE_NAME = "i4fvb_sh_delete_employee";

    /**
     * 上海减员表单流程
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA_WF = "sh_delete_employee_wf";

    /**
     * 全国派单表单Schema
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA = "nationwide_dispatch";

    /**
     * 全国派单表格名称
     */
    public static final String NATIONWIDE_DISPATCH_TABLE_NAME = "i4fvb_nationwide_dispatch";

    /**
     * 全国派单表单流程
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA_WF = "nationwide_dispatch_wf";

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
     * 用户type
     */
    public static final Integer USER_TYPE = 3;

    /**
     * 部门type
     */
    public static final Integer DEPARTMENT_TYPE = 1;

    public static final Integer MAX_INSERT_NUM = 1000;


}
