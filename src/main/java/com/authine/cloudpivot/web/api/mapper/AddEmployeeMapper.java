package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: 刘磊
 * @Date: 2020-03-13 10:07
 * @Description: 增员修改使用接口
 */
@Mapper
public interface AddEmployeeMapper {
    /**
     * 方法说明：根据id获取员工订单实体
     *
     * @param id 主键id
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/3/13 10:16
     */
    EmployeeOrderForm getEmployeeOrderFormById(String id);

    /**
     * 根据客户名称以及证件号码查询增员客户
     *
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param identityNo       证件号码
     * @return 增员客户数据
     * @author wangyong
     */
    List<AddEmployee> getAddEmployeeByClientNameAndIdCard(String id, String firstClientName, String secondClientName, String identityNo);

    /**
     * 方法说明：根据id查询增员_客户数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    AddEmployee getAddEmployeeById(String id);

    /**
     * 方法说明：根据id查询增员_上海数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    ShAddEmployee getShAddEmployeeById(String id);

    /**
     * 方法说明：根据id查询增员_全国数据
     *
     * @param id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/5/15 8:53
     */
    NationwideDispatch getQgAddEmployeeById(String id);

    /**
     * 方法说明：根据一级客户名称，二级客户名称，身份证号码查询员工档案
     *
     * @param firstLevelClientName  一级客户名称
     * @param secondLevelClientName 二级客户名称
     * @param identityNo            身份证号码
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/6/12 10:46
     */
    EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName, String secondLevelClientName,
                                                            String identityNo);

    /**
     * 方法说明：增员客户修改退回原因
     *
     * @param id           id
     * @param returnReason 退回原因
     * @author liulei
     * @Date 2020/6/12 10:56
     */
    void updateReturnReason(String id, String returnReason, String tableName);

    /**
     * 方法说明：获取经办人设置
     *
     * @param city           城市
     * @param welfareHandler 福利办理方
     * @param businessType   社保/公积金
     * @param clientName     二级客户名称
     * @return java.lang.String
     * @author liulei
     * @Date 2020/6/12 11:41
     */
    String getHsLevyHandler(String city, String welfareHandler, String businessType, String clientName);

    /**
     * 方法说明：新增员工档案
     *
     * @param employeeFiles 员工档案
     * @return void
     * @author liulei
     * @Date 2020/6/12 11:43
     */
    void addEmployeeFiles(EmployeeFiles employeeFiles);

    /**
     * 方法说明：新增入职通知
     *
     * @param entryNotice 入职通知
     * @return void
     * @author liulei
     * @Date 2020/6/12 11:43
     */
    void addEntryNotice(EntryNotice entryNotice);

    /**
     * 方法说明：插入社保申报数据
     *
     * @param declare 社保申报
     * @return void
     * @author liulei
     * @Date 2020/6/12 15:13
     */
    void addSocialSecurityDeclare(SocialSecurityDeclare declare);

    /**
     * 方法说明：插入公积金申报数据
     *
     * @param declare 公积金申报
     * @return void
     * @author liulei
     * @Date 2020/6/12 15:13
     */
    void addProvidentFundDeclare(ProvidentFundDeclare declare);

    /**
     * 方法说明：插入补缴，汇缴子表数据
     *
     * @param list      子表数据
     * @param parentId  父级id
     * @param tableName 表名
     * @return void
     * @author liulei
     * @Date 2020/6/12 15:11
     */
    void createEmployeeOrderFormDetails(List<EmployeeOrderFormDetails> list, String parentId, String tableName);

    /**
     * 方法说明：插入员工订单
     *
     * @param orderForm 员工订单
     * @return void
     * @author liulei
     * @Date 2020/6/12 15:28
     */
    void addEmployeeOrderForm(EmployeeOrderForm orderForm);

    List<Integer> getStartChargeTime(String tableName, String parentId);

    List<Integer> getEndChargeTime(String tableName, String parentId);

    Double getMaxSortKey(String tableName, String parentId);

    Double getSumInTimeRange(String tableName, String startChargeTime, String endChargeTime, String parentId);

    void addEmployeeOrderFormFee(List<EmployeeOrderFormDetails> list, String tableName);

    void delOrderFormServiceChargeUnitPrice(String parentId, String tableName);

    void updateEmployeeOrderFrom(EmployeeOrderForm orderForm);

    EmployeeOrderForm getEmployeeOrderFormByEmployeeFilesId(String id);

    void updateEmployeeFiles(EmployeeFiles employeeFiles);

    void updateAddEmployee(AddEmployee addEmployee);

    void updateShAddEmployee(ShAddEmployee shAddEmployee);

    void updateQgAddEmployee(NationwideDispatch nationwideDispatch);

    AddEmployee getUpdateAddEmployeeById(String id);

    ShAddEmployee getUpdateShAddEmployeeById(String id);

    NationwideDispatch getUpdateQgAddEmployeeById(String id);

    EmployeeFiles getEmployeeFilesByAddEmployeeId(String id);

    SocialSecurityDeclare getSocialSecurityDeclareByAddEmployeeId(String addEmployeeId);

    ProvidentFundDeclare getProvidentFundDeclareByAddEmployeeId(String addEmployeeId);

    void updateAddEmployeeByUpdate(AddEmployee update);

    void updateShAddEmployeeByUpdate(ShAddEmployee update);

    void updateQgAddEmployeeByUpdate(NationwideDispatch update);

    void delOrderFormSbDetails(String parentId, String tableName);

    void delOrderFormGjjDetails(String parentId, String tableName);

    EmployeeFiles getUpdateEmployeeFilesById(String id);

    EmployeeFiles getEmployeeFilesById(String id);

    void deleteBatchPreDispatchByAddEmployeeId(String addEmployeeId);

    void updateStatusToCancelDispatch(String id, String tableName);

    List<SocialSecurityDeclare> listSocialSecurityDeclare(List<String> ids);

    List<ProvidentFundDeclare> listProvidentFundDeclare(List<String> ids);

    void addOrderFormDetailsFromDeclare(String declareTableName, String declareId, String orderFormTableName,
                                        String orderFormId);

    void updateDeclareSubmitInfo(String id, String tableName, String status, String billYear);

    void updateAddEmployeeStatus(String addEmployeeId, String field, String status, String returnReason);
}
