package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.List;

/**
 * @author liulei
 * @Description 减员service
 * @ClassName com.authine.cloudpivot.web.api.service.DeleteEmployeeService
 * @Date 2020/5/15 8:56
 **/
public interface DeleteEmployeeService {
    /**
     * 方法说明：根据id获取减员_客户信息
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.DeleteEmployee
     * @author liulei
     * @Date 2020/5/15 8:43
     */
    DeleteEmployee getDeleteEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_上海实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    ShDeleteEmployee getShDeleteEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_全国实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    NationwideDispatch getQgDeleteEmployeeById(String id) throws Exception;

    /**
     * 方法说明：修改退回原因
     * @param id 表单id
     * @param returnReason 退回原因
     * @param tableName 表名
     * @author liulei
     * @Date 2020/6/15 10:13
     */
    void updateDelEmployeeReturnReason(String id, String returnReason, String tableName) throws Exception;

    /**
     * 方法说明：减员客户提交
     * @param delEmployees 减员数据
     * @param userId
     * @param organizationFacade
     * @author liulei
     * @Date 2020/6/15 10:15
     */
    void delSubmit(List <DeleteEmployee> delEmployees, String userId, OrganizationFacade organizationFacade) throws Exception;

    /**
     * 方法说明：减员上海提交
     * @param delEmployees 减员数据
     * @author liulei
     * @Date 2020/6/15 10:15
     */
    void shDelSubmit(List<ShDeleteEmployee> delEmployees) throws Exception;

    /**
     * 方法说明：减员上海提交
     * @param delEmployees 减员数据
     * @author liulei
     * @Date 2020/6/15 10:15
     */
    void qgDelSubmit(List<NationwideDispatch> delEmployees) throws Exception;

    /**
     * 方法说明：减员客户变更提交
     * @param id 变更表单id
     * @return void
     * @author liulei
     * @Date 2020/6/15 17:06
     */
    void delUpdateSubmit(String id, String userId, OrganizationFacade organizationFacade) throws Exception;

    /**
     * 方法说明：减员上海变更提交
     * @param id 变更表单id
     * @return void
     * @author liulei
     * @Date 2020/6/15 17:06
     */
    void shDelUpdateSubmit(String id) throws Exception;

    /**
     * 方法说明：减员全国变更提交
     * @param id 变更表单id
     * @return void
     * @author liulei
     * @Date 2020/6/15 17:06
     */
    void qgDelUpdateSubmit(String id) throws Exception;

    /**
     * 方法说明：减员取派
     * @param id 减员_客户表单id
     * @author liulei
     * @Date 2020/6/15 17:06
     */
    void cancelDispatch(String id) throws Exception;

    /**
     * 方法说明：停缴提交
     * @param ids 表单idList
     * @param schemaCode 表单编码
     * @return void
     * @author liulei
     * @Date 2020/6/16 11:11
     */
    void closeSubmit(List<String> ids, String schemaCode) throws Exception;

    /**
     * 方法说明：停缴驳回操作
     * @param ids 表单idList
     * @param returnReason 退回原因
     * @param schemaCode 表单编码
     * @return void
     * @author liulei
     * @Date 2020/6/16 11:32
     */
    void closeReject(List <String> ids, String returnReason, String schemaCode) throws Exception;
}
