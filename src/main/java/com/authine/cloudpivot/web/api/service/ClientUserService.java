package com.authine.cloudpivot.web.api.service;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.ClientUserService
 * @Date 2020/1/3 17:10
 **/
public interface ClientUserService {

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

    /**
     * 方法说明：客户用户重置密码
     *
     * @return void
     * @throws
     * @Param ids
     * @author liulei
     * @Date 2020/1/14 15:09
     */
    void clientResetPassword(String ids) throws Exception;

    /**
     * 方法说明：客户导入员工信息，提交后维护修改截止时间
     *
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @Param id
     * @Param sbCityId 社保缴纳城市id
     * @Param gjjCityId 公积金缴纳城市id
     * @Param clientNumber 客户编码
     * @author liulei
     * @Date 2020/1/19 13:51
     */
    void updateEndTime(String id, String sbCityId, String gjjCityId, String clientNumber) throws Exception;
}
