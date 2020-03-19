package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentUserModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.status.UserStatus;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Client;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.service.ClientUserService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.controller
 * @Date 2019/12/10 16:46
 **/
@RestController
@RequestMapping("/controller/clientUser")
@Slf4j
public class ClientUserController extends BaseController {

    /** 客户部门ID */
    public static final String CLIENT_DEPARTMENT_ID = "402881a16f7412d4016f741a56b60000";

    /** 新增用户时初始化密码 */
    public static final String INITIALIZATION_PASSWORD = "123456";

    @Resource
    private ClientUserService clientUserService;

    /**
     * 方法说明：新增客户用户
     * @Param userModel  客户用户
     * @return java.lang.String
     * @throws
     * @author liulei
     * @Date 2019/12/10 19:37
     */
    @PostMapping("/addClientUser")
    public ResponseResult <String> addClientUser(@RequestBody Client client) {
        /** 客户名称 */
        String clientName = client.getClientName();
        /** 客户账号用户名称 */
        String employeeName = client.getEmployeeName();
        /** 客户账号手机号 */
        String moblie = client.getMoblie();
        /** 对应用户表的id */
        String userId = client.getUserId();

        // 根据手机号判断是否有重复用户
        UserModel user = this.getOrganizationFacade().getUserByMobile(moblie);
        //DepartmentModel dept = this.getOrganizationFacade().getDepartment(CLIENT_DEPARTMENT_ID);
        // 根据用户id获取当前用户
        UserModel newUser = new UserModel();
        if (StringUtils.isNotBlank(userId)) {
            newUser = this.getOrganizationFacade().getUser(userId);
        }
        // 用户名
        newUser.setName(employeeName);
        // 手机号
        newUser.setMobile(moblie);
        // 登录名
        newUser.setUsername(moblie);

        if (StringUtils.isNotBlank(newUser.getId())) {
            // 此时修改
            if (user != null && !user.getId().equals(newUser.getId())) {
                // 用户中已经存在当前手机号，且手机号不是当前用户
                log.info("手机号码“" +moblie + "”重复校验不通过!");
                return this.getOkResponseResult("error", "修改用户失败!");
            }
            try {
                this.getOrganizationFacade().updateUser(newUser);

                log.info("修改用户成功!");
                return this.getOkResponseResult("success", userId);
            } catch (Exception e) {
                log.info("修改用户出错!");
                return this.getOkResponseResult("error", "修改用户出错!");
            }

        } else {
            // 此时新增
            if (user != null) {
                // 用户中已经存在当前手机号，且手机号不是当前用户
                log.info("手机号码“" +moblie + "”重复校验不通过!");
                return this.getOkResponseResult("error", "新增用户失败!");
            }
            // 系统自动赋值
            newUser = autoAssignment(newUser);
            try {
                // 新增用户
                newUser = this.getOrganizationFacade().addUser(newUser);
                // 新增部门用户表数据
                this.addClientDepartmentUserModel(newUser);
                // 更新客户信息表对应的用户id
                clientUserService.updateClientUserId(client.getId(), newUser.getId());

                log.info("新增用户成功!");
                return this.getOkResponseResult("success", newUser.getId());

            } catch (Exception e) {
                log.info("新增用户出错!");
                return this.getOkResponseResult("error", "新增用户出错!");
            }
        }
    }

    /**
     * 方法说明：自动赋值
     * @Param newUser
     * @return com.authine.cloudpivot.engine.api.model.organization.UserModel
     * @throws
     * @author liulei
     * @Date 2020/1/3 17:28
     */
    private UserModel autoAssignment(UserModel newUser) {
        // 部门id 客户管理部门
        newUser.setDepartmentId(CLIENT_DEPARTMENT_ID);
        // 密码
        String password = "{bcrypt}" + BCrypt.hashpw(INITIALIZATION_PASSWORD, BCrypt.gensalt());
        newUser.setPassword(password);
        newUser.setDeleted(false);
        newUser.setCreatedTime(new Date());
        newUser.setModifiedTime(new Date());
        newUser.setActive(true);
        newUser.setAdmin(false);
        newUser.setBoss(false);
        newUser.setLeader(false);
        newUser.setStatus(UserStatus.ENABLE);

        return newUser;
    }

    /**
     * 方法说明：新增部门用户表数据
     * @Param userModel
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/6 12:53
     */
    private void addClientDepartmentUserModel(UserModel userModel) throws Exception{
        DepartmentUserModel  deptUserModel = new DepartmentUserModel();
        deptUserModel.setUserId(userModel.getId());
        deptUserModel.setDeptId(CLIENT_DEPARTMENT_ID);
        deptUserModel.setMain(true);
        deptUserModel.setLeader(false);
        deptUserModel.setDeptSourceId(null);
        deptUserModel.setUserSourceId(null);
        deptUserModel.setCreatedBy(userModel.getModifiedBy());
        deptUserModel.setCreatedTime(userModel.getModifiedTime());
        deptUserModel.setModifiedBy(userModel.getModifiedBy());
        deptUserModel.setModifiedTime(userModel.getModifiedTime());
        deptUserModel.setDeleted(false);

        this.getOrganizationFacade().addDepartmentUser(deptUserModel);
    }

    /**
     * 方法说明：客户用户重置密码
     * @Param ids 用户管理业务id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/14 14:47
     */
    @GetMapping("/clientResetPassword")
    @ResponseBody
    public ResponseResult <String> clientResetPassword(String ids) {
        if (StringUtils.isBlank(ids)) {
            log.info("没有获取到ids,重置密码出错!");
            return this.getOkResponseResult("error", "没有获取到ids,重置密码出错!");
        }
        try {
            clientUserService.clientResetPassword(ids);
            log.info("重置密码成功!");
            return this.getOkResponseResult("success", "重置密码成功!");
        } catch (Exception e) {
            log.info("重置密码出错:" + e.getMessage());
            return this.getOkResponseResult("error", "重置密码出错!");
        }
    }

    /**
     * 方法说明：客户导入员工信息，提交后维护修改截止时间
     * @Param id
     * @Param sbCityId 社保缴纳城市id
     * @Param gjjCityId 公积金缴纳城市id
     * @Param clientNumber 客户编码
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/19 13:43
     */
    @GetMapping("/updateEndTime")
    @ResponseBody
    public ResponseResult <String> updateEndTime(String id, String sbCityId, String gjjCityId, String clientNumber) {
        if (StringUtils.isBlank(id)) {
            log.info("没有获取到id,维护截止时间出错!");
            return this.getOkResponseResult("error", "没有获取到id,维护截止时间出错!");
        }
        if (StringUtils.isBlank(sbCityId)) {
            log.info("没有获取到社保缴纳城市,维护截止时间出错!");
            return this.getOkResponseResult("error", "没有获取到社保缴纳城市,维护截止时间出错!");
        }
        if (StringUtils.isBlank(gjjCityId)) {
            log.info("没有获取到公积金缴纳城市,维护截止时间出错!");
            return this.getOkResponseResult("error", "没有获取到公积金缴纳城市,维护截止时间出错!");
        }
        if (StringUtils.isBlank(clientNumber)) {
            log.info("没有获取到客户编码,维护截止时间出错!");
            return this.getOkResponseResult("error", "没有获取到客户编码,维护截止时间出错!");
        }
        try {
            clientUserService.updateEndTime(id, sbCityId, gjjCityId, clientNumber);
            return this.getOkResponseResult("success", "维护截止时间成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", "维护截止时间出错!");
        }
    }
}
