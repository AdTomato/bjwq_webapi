package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.authine.cloudpivot.web.api.entity.QueryInfo;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.entity.WelfareSet;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.service.SocialSecurityCardService;
import com.authine.cloudpivot.web.api.service.SystemManageService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 社保卡Controller
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.controller.SocialSecurityCardController
 * @Date 2019/12/16 10:12
 **/
@RestController
@RequestMapping("/controller/socialSecurityCard")
@Slf4j
public class SocialSecurityCardController extends BaseController {

    @Resource
    private SocialSecurityCardService socialSecurityCardService;

    @Resource
    private EmployeeFilesService employeeFilesService;

    @Resource
    private SystemManageService systemManageService;

    /**
     * 方法说明：社保卡导入
     * @param fileName 导入文件名称
     * @param type 导入类型（社保卡：新增导入，激活流程：1_1；导入办理记录：1_2；导入发卡记录：1_3
     *                      五险一金享受：新增导入，激活流程：2_1；导入办理记录：2_2；导入发卡记录：2_3）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/7 9:02
     */
    @GetMapping("/importData")
    @ResponseBody
    public ResponseResult<String> importData(String fileName, String type){
        String userId = getUserId();
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(userId);
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());

            if ("1_1".equals(type)) {
                //社保卡新增导入，激活流程
                socialSecurityCardService.importAddData(fileName, user, dept, Constants.SOCIAL_SECURITY_CARD_PROCESS,
                        this.getWorkflowInstanceFacade());
            } else if ("1_2".equals(type)){
                //社保卡导入办理记录/** 姓名,身份证号码,单据号,办理状态,办理反馈,社保卡号*/
                socialSecurityCardService.importUpdateData(fileName, user, dept, type,
                        this.getWorkflowInstanceFacade());
            } else if ("1_3".equals(type)){
                //社保卡导入发卡记录/** 姓名,身份证号码,单据号,发卡状态,发卡反馈*/
                socialSecurityCardService.importUpdateData(fileName, user, dept, type,
                        this.getWorkflowInstanceFacade());
            } else if ("2_1".equals(type)){
                //五险一金享受：新增导入，激活流程
                socialSecurityCardService.importAddData(fileName, user, dept, Constants.INSURANCE_AND_HOUSING_FUND,
                        this.getWorkflowInstanceFacade());
            } else if ("2_2".equals(type)){
                //五险一金享受：导入办理记录
                socialSecurityCardService.importUpdateData(fileName, user, dept, type,
                        this.getWorkflowInstanceFacade());
            } else if ("2_3".equals(type)){
                //五险一金享受：业务反馈
                socialSecurityCardService.importUpdateData(fileName, user, dept, type,
                        this.getWorkflowInstanceFacade());
            } else {
                return this.getErrResponseResult("error", 404l, "导入类型不正确!");
            }

            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：根据证件号码查询相关信息,返回一级，二级客户名称，原商保信息
     * @param identityNo 证件号码
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<com.authine.cloudpivot.web.api.entity.QueryInfo>
     * @author liulei
     * @Date 2020/5/6 15:12
     */
    @GetMapping("/queryInfo")
    @ResponseBody
    public ResponseResult<QueryInfo> queryInfo(String identityNo) {
        QueryInfo queryInfo = new QueryInfo();
        if (StringUtils.isBlank(identityNo)) {
            log.error("证件号码为空！");
            return this.getOkResponseResult(queryInfo, "证件号码为空！");
        }
        try {
            // 根据证件号码查询员工档案数据
            EmployeeFiles employeeFiles = employeeFilesService.getEmployeeFilesByIdentityNo(identityNo);
            if (employeeFiles == null) {
                log.error("没有查询到对应的员工档案！");
                return this.getOkResponseResult(queryInfo, "没有查询到对应的员工档案！");
            }
            Unit owner = new Unit();
            owner.setId(employeeFiles.getOwner());
            owner.setType(3);
            // 业务员
            //queryInfo.setOwner(owner);
            // 一级
            queryInfo.setFirstLevelClientName(employeeFiles.getFirstLevelClientName());
            queryInfo.setSecondLevelClientName(employeeFiles.getSecondLevelClientName());
            // 城市
            queryInfo.setCity(employeeFiles.getSocialSecurityCity());
            // 福利办理方
            queryInfo.setWelfareHandler(employeeFiles.getSWelfareHandler());
            // 根据城市查询对应省份
            if (StringUtils.isNotBlank(employeeFiles.getSocialSecurityCity())) {
                WelfareSet welfareSet = systemManageService.getWelfareSet(employeeFiles.getSocialSecurityCity());
                queryInfo.setProvince(welfareSet.getProvince());
            }
            if ("VIP员工".equals(employeeFiles.getEmployeeLabels())) {
                queryInfo.setIsVip("是");
            } else {
                queryInfo.setIsVip("否");
            }

            // 所属部门
            queryInfo.setSubordinateDepartment(employeeFiles.getSubordinateDepartment());
            /*String subordinateDepartment = employeeFiles.getSubordinateDepartment();
            if (StringUtils.isNotBlank(subordinateDepartment)) {
                Unit subordinateDepartmentU = JSON.parseObject(subordinateDepartment,
                        new TypeReference <ArrayList <Unit>>() {
                        }).get(0);
                // 所属部门
                queryInfo.setSubordinateDepartment(subordinateDepartment);
            }*/
            log.info(queryInfo.toString());
            return this.getOkResponseResult(queryInfo, "success");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult(queryInfo, e.getMessage());
        }
    }
}
