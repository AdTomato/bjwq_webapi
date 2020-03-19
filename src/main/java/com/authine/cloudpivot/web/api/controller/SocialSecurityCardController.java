package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.service.SocialSecurityCardService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/importSocialSecurityCard")
    @ResponseBody
    public ResponseResult<String> importSocialSecurityCard(String fileName){
        String userId = "402881c16f63e980016f798408060d3f";
        //String userId = getUserId();
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(userId);
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());

            if (fileName.indexOf("办理申请") >= 0) {
                /** 紧急交办,业务员,公司名称,姓名,身份证号码,员工状态,地区*/
                socialSecurityCardService.importSocialSecurityCard(fileName, user, dept,
                        this.getWorkflowInstanceFacade());
            } else if (fileName.indexOf("办理记录") >=0){
                /** 姓名,身份证号码,单据号,办理状态,办理反馈,社保卡号*/
                socialSecurityCardService.importProcessFeedback(fileName, user, dept,
                        this.getWorkflowInstanceFacade());
            } else if (fileName.indexOf("发卡记录") >=0){
                /** 姓名,身份证号码,单据号,发卡状态,发卡反馈*/
                socialSecurityCardService.importIssueFeedback(fileName, user, dept,
                        this.getWorkflowInstanceFacade());
            } else {
                return this.getErrResponseResult("error", 404l, "上传文件命名不正确!");
            }

            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }
}
