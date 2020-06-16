package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.List;

/**
 * @author liulei
 * @Description 增员Service
 * @ClassName com.authine.cloudpivot.web.api.service.AddEmployeeService
 * @Date 2020/6/12 9:55
 **/
public interface AddEmployeeService {
    /**
     * 方法说明：根据客户名称，人员id,查询是否存在员工档案
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @param identityNo
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/5/14 11:08
     */
    EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName, String secondLevelClientName,
                                                            String identityNo) throws Exception;

    /**
     * 根据客户名称以及证件号码查询增员客户
     * @param firstClientName 一级客户名称
     * @param secondClientName 二级客户名称
     * @param identityNo 证件号码
     * @return 增员客户数据
     * @author wangyong
     */
    List<AddEmployee> getAddEmployeeByClientNameAndIdCard(String id, String firstClientName, String secondClientName, String identityNo);

    /**
     * 方法说明：根据id获取增员_客户实体
     * @param id
     * @author liulei
     * @Date 2020/2/28 10:10
     */
    AddEmployee getAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：增员客户修改退回原因
     * @param id id
     * @param returnReason 退回原因
     * @author liulei
     * @Date 2020/6/12 10:56
     */
    void updateAddEmployeeReturnReason(String id, String returnReason, String tableName)throws Exception;

    /**
     * 方法说明：根据id获取增员_上海实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    ShAddEmployee getShAddEmployeeById(String id) throws Exception;

    /**
     * 方法说明：根据id获取增员_全国实体
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.ShAddEmployee
     * @author liulei
     * @Date 2020/3/2 8:46
     */
    NationwideDispatch getQgAddEmployeeById(String id) throws Exception;
    /**
     * 方法说明：增员_客户提交操作
     * @param addEmployees 表单List
     * @param organizationFacade
     * @param userId
     * @author liulei
     * @Date 2020/6/12 10:22
     */
    List <EntryNotice> addSubmit(List <AddEmployee> addEmployees, OrganizationFacade organizationFacade, String userId) throws Exception;

    /**
     * 方法说明： 增员_上海提交操作
     * @param addEmployees 表单List
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void shAddSubmit(List <ShAddEmployee> addEmployees)throws Exception;

    /**
     * 方法说明： 增员_全国提交操作
     * @param addEmployees 表单List
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void qgAddSubmit(List <NationwideDispatch> addEmployees)throws Exception;

    /**
     * 方法说明： 增员_客户变更提交操作
     * @param id 表单id
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void addUpdateSubmit(String id, String userId, BizObjectFacade bizObjectFacade,
                         OrganizationFacade organizationFacade) throws Exception;

    /**
     * 方法说明： 增员_上海变更提交操作
     * @param id 表单id
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void addShUpdateSubmit(String id) throws Exception;

    /**
     * 方法说明： 增员_全国变更提交操作
     * @param id 表单id
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void addQgUpdateSubmit(String id) throws Exception;

    /**
     * 方法说明： 员工档案变更提交操作
     * @param id 表单id
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void employeeFilesUpdateSubmit(String id) throws Exception;

    /**
     * 方法说明： 增员_客户取派操作
     * @param id 增员_客户表单id
     * @author liulei
     * @Date: 2020/6/12 10:22
     */
    void cancelDispatch(String id, String userId, BizObjectFacade bizObjectFacade) throws Exception;

    /**
     * 方法说明：申报提交
     * @param ids 表单idList
     * @param billYear 账单年月
     * @param schemaCode 表单编码
     * @return void
     * @author liulei
     * @Date 2020/6/16 9:23
     */
    void declareSubmit(List <String> ids, String billYear, String schemaCode) throws Exception;

    /**
     * 方法说明：申报驳回
     * @param ids 表单idList
     * @param returnReason 退回原因
     * @param schemaCode 表单编码
     * @param userId 用户id
     * @param bizObjectFacade 业务对象服务类
     * @return void
     * @author liulei
     * @Date 2020/6/16 10:24
     */
    void declareReject(List <String> ids, String returnReason, String schemaCode, String userId,
                       BizObjectFacade bizObjectFacade) throws Exception;
}
