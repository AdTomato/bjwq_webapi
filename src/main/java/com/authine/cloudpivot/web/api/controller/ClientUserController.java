package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentUserModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.status.UserStatus;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Client;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.service.ClientUserService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.MD5;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.controller
 * @Date 2019/12/10 16:46
 **/
@RestController
@RequestMapping("/controller/clientUser")
@Slf4j
public class ClientUserController extends BaseController {

    /**
     * 客户部门ID
     */
    public static final String CLIENT_DEPARTMENT_ID = "402881a16f7412d4016f741a56b60000";

    /**
     * 新增用户时初始化密码
     */
    public static final String INITIALIZATION_PASSWORD = "123456";

    @Resource
    private ClientUserService clientUserService;

    /**
     * 方法说明：新增客户用户
     *
     * @return java.lang.String
     * @throws
     * @Param userModel  客户用户
     * @author liulei
     * @Date 2019/12/10 19:37
     */
    @PostMapping("/addClientUser")
    public ResponseResult<String> addClientUser(@RequestBody Client client) {
        /** 对应用户表的id */
        String userId = client.getUserId();
        // 根据手机号判断是否有重复用户
        //UserModel user = this.getOrganizationFacade().getUserByMobile(moblie);
        // 根据用户id获取当前用户
        UserModel newUser = new UserModel();
        if (StringUtils.isNotBlank(userId)) {
            newUser = this.getOrganizationFacade().getUser(userId);
        }
        // 用户名
        newUser.setName(client.getSecondLevelClientName());
        // 手机号
        newUser.setMobile(client.getMoblie());

        try {
            if (StringUtils.isNotBlank(newUser.getId())) {
                // 此时修改
                this.getOrganizationFacade().updateUser(newUser);
                log.info("修改用户成功!");
            } else {
                // 随机生成编码作为登录名
                newUser.setUsername(CommonUtils.randomGenerateCode());
                log.info("随机生成登录名：" + newUser.getUsername() + "!");
                // 系统自动赋值
                newUser = autoAssignment(newUser);
                // 新增用户
                newUser = this.getOrganizationFacade().addUser(newUser);
                // 新增部门用户表数据
                this.addClientDepartmentUserModel(newUser);
                // 更新客户信息表对应的用户id
                clientUserService.updateClientUserId(client.getId(), newUser.getId());
                log.info("新增用户成功!");
            }
            return this.getOkResponseResult("success", newUser.getId());
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：自动赋值
     *
     * @return com.authine.cloudpivot.engine.api.model.organization.UserModel
     * @throws
     * @Param newUser
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
     *
     * @return void
     * @throws
     * @Param userModel
     * @author liulei
     * @Date 2020/1/6 12:53
     */
    private void addClientDepartmentUserModel(UserModel userModel) throws Exception {
        DepartmentUserModel deptUserModel = new DepartmentUserModel();
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

}
