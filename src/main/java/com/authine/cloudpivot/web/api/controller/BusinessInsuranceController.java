package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.BusinessInsuranceService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 商保
 * @ClassName com.authine.cloudpivot.web.api.controller.BusinessInsuranceController
 * @Date 2020/3/6 17:25
 **/
@RestController
@RequestMapping("/controller/businessInsurance")
@Slf4j
public class BusinessInsuranceController extends BaseController {

    @Resource
    BusinessInsuranceService businessInsuranceService;

    /**
     * 方法说明：商保增员提交时生成人员信息表单
     * @param ids 商保增员表单id,多个id使用“,”隔开
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/9 10:10
     */
    @GetMapping("/addBusinessInsurance")
    @ResponseBody
    public ResponseResult <String> addBusinessInsurance(String ids) {
        if (StringUtils.isBlank(ids)) {
            return this.getOkResponseResult("error", "没有获取到增员表单id,操作失败！");
        }
        try {
            businessInsuranceService.addBusinessInsurance(ids);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：商保减员提交时修改对应人员信息表单的福利截止时间，备注
     * @param ids 商保减员表单id,多个id使用“,”隔开
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/9 10:10
     */
    @GetMapping("/delBusinessInsurance")
    @ResponseBody
    public ResponseResult <String> delBusinessInsurance(String ids) {
        if (StringUtils.isBlank(ids)) {
            return this.getOkResponseResult("error", "没有获取到减员表单id,操作失败！");
        }
        try {
            businessInsuranceService.delBusinessInsurance(ids);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：商保变更提交时修改人员信息表单的商保信息（商保套餐等级，服务费，生效日，套餐内容）
     * @param ids 商保变更表单id,多个id使用“,”隔开
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/9 13:40
     */
    @GetMapping("/updateBusinessInsuranceInfo")
    @ResponseBody
    public ResponseResult <String> updateBusinessInsuranceInfo(String ids) {
        if (StringUtils.isBlank(ids)) {
            return this.getOkResponseResult("error", "没有获取到变更表单id,操作失败！");
        }
        try {
            businessInsuranceService.updateBusinessInsuranceInfo(ids);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult("error", e.getMessage());
        }
    }

    /**
     * 方法说明：商保增员导入
     *  导入模板：公司；姓名；身份证号码；银行卡号；开户行；账号所有人姓名；被保险人手机号；商保套餐等级；商保服务费；
     *            商保生效日；商保套餐内容；子女姓名；子女证件号码
     * @param fileName 导入文件名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/13 17:12
     */
    @GetMapping("/addImport")
    @ResponseBody
    public ResponseResult <String> addImport(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            businessInsuranceService.addImport(this.getWorkflowInstanceFacade(), fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：商保增员导入
     *  导入模板：公司；姓名；身份证号码；福利截止时间；备注
     * @param fileName 导入文件名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/13 17:12
     */
    @GetMapping("/deleteImport")
    @ResponseBody
    public ResponseResult <String> deleteImport(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser("402881c16f63e980016f798408060d3f");
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            businessInsuranceService.deleteImport(this.getWorkflowInstanceFacade(), fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }
}
