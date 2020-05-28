package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.authine.cloudpivot.web.api.entity.QueryInfo;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.entity.WelfareSet;
import com.authine.cloudpivot.web.api.service.BusinessInsuranceService;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

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
    private BusinessInsuranceService businessInsuranceService;

    @Resource
    private EmployeeFilesService employeeFilesService;

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
     * 方法说明：商保导入
     * @param fileName 导入文件名称
     * @param code 流程编码（增员导入：add_business_insurance_wf；减员导入：delete_business_insurance_wf；变更导入：update_business_insurance_wf）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/13 17:12
     */
    @GetMapping("/importData")
    @ResponseBody
    public ResponseResult <String> importData(String fileName, String code) {
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
            businessInsuranceService.importData(fileName, user, dept, code, this.getWorkflowInstanceFacade());
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：根据证件号码,客户名称查询相关信息
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

            queryInfo = businessInsuranceService.getQueryInfo(identityNo);
            // 根据证件号码查询员工档案数据
            EmployeeFiles employeeFiles = employeeFilesService.getEmployeeFilesByIdentityNo(identityNo);
            if (employeeFiles == null) {
                return this.getOkResponseResult(queryInfo, "没有查询到对应的员工档案！");
            }
            queryInfo.setFirstLevelClientName(employeeFiles.getFirstLevelClientName());
            queryInfo.setSecondLevelClientName(employeeFiles.getSecondLevelClientName());


            log.info(queryInfo.toString());
            return this.getOkResponseResult(queryInfo, "success");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getOkResponseResult(queryInfo, e.getMessage());
        }
    }
}
