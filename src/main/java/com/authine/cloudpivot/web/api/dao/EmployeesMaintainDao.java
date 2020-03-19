package com.authine.cloudpivot.web.api.dao;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao
 * @Date 2020/2/10 17:17
 **/
public interface EmployeesMaintainDao {
    /**
     * 方法说明：根据员工档案的单据号获取社保公积金信息
     * @param employeeFilesId
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/10 17:34
     */
    Map<String, String> getSocialSecurityFundDetail(String employeeFilesId) throws Exception;

    /**
     * 方法说明：根据补缴月份是，地区获取入职通知信息
     * @param monthDifference
     * @param city
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/12 13:37
     */
    Map<String, String> getEntryNoticeBy(int monthDifference, String city) throws Exception;

    /**
     * 方法说明：创建社保公积金子表数据
     * @param parentId 子表父id
     * @param detail 数据
     * @param code 子表编码
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/12 16:48
     */
    void createSocialSecurityFundDetail(String parentId, List<Map<String, String>> detail, String code) throws Exception;

    /**
     * 方法说明：根据客户名称获取派出单位
     * @param clientName
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/14 11:05
     */
    String getDispatchUnitByClientName(String clientName) throws Exception;

    /**
     * 方法说明：根据客户名称和地区获取福利办理方
     * @param clientName
     * @param city
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/14 11:16
     */
    String getWelfareHandlerByClientNameAndCity(String clientName, String city) throws Exception;

    /**
     * 方法说明：获取当前时间节点,如果是个性化客户，获取个性化设置
     * @param clientName
     * @param city
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/14 14:04
     */
    Map<String, String> getTimeNode(String clientName, String city) throws Exception;

    /**
     * 方法说明：根据id获取员工档案的单据号
     * @param id
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2020/2/14 15:27
     */
    String getEmployeeFilesSequenceNoById(String id) throws Exception;

    /**
     * 方法说明：获取征缴产品数据
     * @param city
     * @param startMonth
     * @param type
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @throws
     * @author liulei
     * @Date 2020/2/14 17:03
     */
    List <Map <String, Object>> getSocialSecurityFundDetail(String city, Date startMonth, String type,
                                                            Double setBase) throws Exception;

    /**
     * 方法说明：修改员工订单状态
     * @param ids 员工订单id,多个id用“,”隔开
     * @param field 修改的字段
     * @param status 修改后的状态
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 9:42
     */
    void updateEmployeeOrderFormStatus(String ids, String field, String status) throws Exception;

    /**
     * 方法说明：订单改为历史表单
     * @param oldId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 17:08
     */
    void updateEmployeeOrderFormIsHistory(String oldId) throws Exception;

    /**
     * 方法说明：办理成功后更新订单数据
     * @param id 办理通过提交的表单id
     * @param oldId 原订单id
     * @param newId 新订单id
     * @param type 类型
     * @author liulei
     * @Date 2020/2/17 17:12
     */
    void createSocialSecurityFundDetail(String id, String oldId, String newId, String type) throws Exception;

    /**
     * 方法说明：获取员工订单的社保公积金详情
     * @param parentId
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @throws
     * @author liulei
     * @Date 2020/2/17 17:28
     */
    List <Map <String, Object>> getSocialSecurityFundDetailByParentId(String parentId) throws Exception;

    /**
     * 方法说明：根据子表的数据统计修改订单数据
     * @param id
     * @param info
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 17:55
     */
    void updateEmployeeOrderFormInfo(String id, Map<String, String> info) throws Exception;

    /**
     * 方法说明：获取流程任务数据
     * @param userId
     * @param ids
     * @param code 表单编码
     * @param operateType
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @throws
     * @author liulei
     * @Date 2020/2/19 14:16
     */
    List<Map<String, Object>> getWorkItemInfo(String userId, String ids, String code, String operateType) throws Exception;

    /**
     * 方法说明：根据地区获取运行负责人
     * @param city
     * @return java.lang.String
     * @author liulei
     * @Date 2020/2/21 14:15
     */
    String getOperateLeaderByCity(String city) throws Exception;

    /**
     * 方法说明：减员时修改员工订单数据
     * @param employeeFiles
     * @return void
     * @author liulei
     * @Date 2020/2/26 16:16
     */
    void updateEmployeeFilesWhenDelEmployee(EmployeeFiles employeeFiles) throws Exception;

    /**
     * 方法说明：根据客户名称，证件号码查询社保停缴数据，更新其收费截止月，离职备注
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @param chargeEndMonth 收费截止月
     * @param resignationRemarks 离职备注
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:05
     */
    void updateSocialSecurityClose(String clientName, String identityNo, Date chargeEndMonth,
                                   String resignationRemarks) throws Exception;

    /**
     * 方法说明：根据客户名称，证件号码查询公积金停缴数据，更新其收费截止月
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @param chargeEndMonth 收费截止月
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:14
     */
    void updateProvidentFundClose(String clientName, String identityNo, Date chargeEndMonth) throws Exception;

    /**
     * 方法说明：根据客户名称，证件号码查询员工档案数据
     *      更新其离职日期，社保收费截止，公积金收费截止，离职原因，离职备注
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @param quitDate 离职日期
     * @param socialSecurityChargeEnd 社保收费截止
     * @param providentFundChargeEnd 公积金收费截止
     * @param quitReason
     * @param quitRemark
     * @return void
     * @author liulei
     * @Date 2020/2/27 14:57
     */
    void updateEmployeeFiles(String clientName, String identityNo, Date quitDate, Date socialSecurityChargeEnd,
                             Date providentFundChargeEnd, String quitReason, String quitRemark) throws Exception;

    /**
     * 方法说明：(2表之间)根据修改后的数据,更新减员_客户数据
     * @param sourceId 减员_客户修改表中id
     * @param targetId 减员_客户表中需要修改数据的id
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:18
     */
    void updateDeleteEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception;

    /**
     * 方法说明：(2表之间)根据修改后的数据更新减员_上海数据
     * @param sourceId 减员修改表中id
     * @param targetId 减员_上海表中需要修改数据的id
     * @return void
     * @author liulei
     * @Date 2020/2/27 13:42
     */
    void updateShDeleteEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception;

    /**
     * 方法说明：(2表之间)根据修改后的数据更新减员_全国/增员_全国数据
     * @param sourceId 减员修改表中id
     * @param targetId 减员_全国/增员_全国表中需要修改数据的id
     * @param type 类型（del：减员；add：增员）
     * @return void
     * @author liulei
     * @Date 2020/2/27 14:11
     */
    void updateNationwideDispatchBetweenTwoTables(String sourceId, String targetId, String type) throws Exception;

    /**
     * 方法说明：2表之间)员工档案数据修改审核提交
     * @param sourceId 修改表中id
     * @param targetId 员工档案表中需要修改数据的id
     * @return void
     * @author liulei
     * @Date 2020/2/27 16:49
     */
    void UpdateEmployeeFilesBetweenTwoTables(String sourceId, String targetId) throws Exception;

    /**
     * 方法说明：更新增员_客户数据
     * @param sourceId 修改表中id
     * @param targetId 增员_客户表中需要修改数据的id
     * @return void
     * @author liulei
     * @Date 2020/2/28 17:12
     */
    void updateAddEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception;

    /**
     * 方法说明：更新增员_上海数据
     * @param sourceId 修改表中id
     * @param targetId 增员_上海表中需要修改数据的id
     * @return void
     * @author liulei
     * @Date 2020/3/2 10:05
     */
    void updateShAddEmployeeBetweenTwoTables(String sourceId, String targetId) throws Exception;

    /**
     * 方法说明：根据表名和父id删除子表数据
     * @param tableName 表名
     * @param parentId 父id
     * @return void
     * @author liulei
     * @Date 2020/2/29 13:05
     */
    void deleteChildTableDataByTableNameAndParentId(String tableName, String parentId) throws Exception;

    /**
     * 方法说明：修改申报表单订单id
     * @param oldId  原订单id
     * @param newId  新订单id
     * @return void
     * @author liulei
     * @Date 2020/3/3 14:18
     */
    void updateDeclareEmployeeOrderFormId(String oldId, String newId) throws Exception;
}
