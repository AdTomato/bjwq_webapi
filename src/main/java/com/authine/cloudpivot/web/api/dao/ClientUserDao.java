package com.authine.cloudpivot.web.api.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dao.ClientUserDao
 * @Date 2020/1/3 17:12
 **/
public interface ClientUserDao {
    /**
     * 方法说明：更新客户信息表对应的用户id
     *
     * @return void
     * @throws
     * @Param id
     * @Param userId
     * @author liulei
     * @Date 2020/1/6 12:57
     */
    void updateClientUserId(String id, String userId) throws Exception;

    void clientResetPassword(String ids) throws Exception;

    /**
     * 方法说明：根据客户编号获取客户自定义设置的数据
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @throws
     * @Param clientNumber 客户编号
     * @author liulei
     * @Date 2020/1/19 14:03
     */
    List<Map<String, Object>> getPersonalizedSetByNo(String clientNumber) throws Exception;

    /**
     * 方法说明：客户导入员工信息，提交后维护修改截止时间，工伤比例，公积金比例
     *
     * @return void
     * @throws
     * @Param id
     * @Param endTime
     * @Param companyInjuryRatio
     * @Param employeeInjuryRatio
     * @Param companyFundRatio
     * @Param employeeFundRatio
     * @author liulei
     * @Date 2020/1/19 14:53
     */
    void updateEndTime(String id, String endTime, String companyInjuryRatio, String employeeInjuryRatio,
                       String companyFundRatio, String employeeFundRatio) throws Exception;

    /**
     * 方法说明：根据城市id获取时间节点
     *
     * @return java.lang.String
     * @throws
     * @Param ids
     * @author liulei
     * @Date 2020/1/19 15:23
     */
    String getTimeNodeByIds(String ids) throws Exception;
}
