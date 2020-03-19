package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.service.SocialSecurityCardService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private SocialSecurityCardService socialSecurityCard;

    /**
     * 方法说明：办理社保卡，根据办理人员信息批量开启流程
     * @Param fileName 导入文件的名称
     * @return om.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2019/12/16 10:31
     */
    @PostMapping("/socialSecurityCardProcess")
    @ResponseBody
    @CustomizedOrigin(level = 1)
    public ResponseResult<String> socialSecurityCardProcess(String fileName){
        String userId = "402881c16f63e980016f798408060d3f";
        //String userId = getUserId();

        fileName = "社保卡办理进度表.xlsx";
        if (StringUtils.isBlank(fileName)) {
            return this.getOkResponseResult("error", "没有获取上传文件!");
        }

        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(userId);
            // 导入
            socialSecurityCard.socialSecurityCardProcess(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), fileName, userId, user.getDepartmentId());
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：导入社保卡办理进度信息
     * @Param fileName
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2019/12/16 16:58
     */
    @PostMapping("/importProcessFeedBack")
    @ResponseBody
    @CustomizedOrigin(level = 1)
    public ResponseResult<String> importProcessFeedBack(String fileName){
        String userId = "402881c16f63e980016f79840a1a0d43";
        //String userId = getUserId();

        fileName = "社保卡办理表.xlsx";
        if (StringUtils.isBlank(fileName)) {
            return this.getOkResponseResult("error", "没有获取上传文件!");
        }

        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            socialSecurityCard.importProcessFeedBack(this.getWorkflowInstanceFacade(), fileName, userId);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return this.getOkResponseResult("error", "导入失败!");
        }
    }

    /**
     * 方法说明：导入发卡进度信息
     * @Param fileName
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2019/12/18 16:44
     */
    @PostMapping("/importIssueFeedBack")
    @ResponseBody
    @CustomizedOrigin(level = 1)
    public ResponseResult<String> importIssueFeedBack(String fileName) throws Exception {
        String userId = "402881c16f63e980016f798408060d3f";
        //String userId = getUserId();

        fileName = "社保卡发卡进度表.xlsx";
        if (StringUtils.isBlank(fileName)) {
            return this.getOkResponseResult("error", "没有获取上传文件!");
        }

        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            socialSecurityCard.importIssueFeedBack(this.getWorkflowInstanceFacade(), fileName, userId);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return this.getOkResponseResult("error", "导入失败!");
        }
    }
}
