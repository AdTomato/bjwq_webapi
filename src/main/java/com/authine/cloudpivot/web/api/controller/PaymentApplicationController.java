package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.PaymentApplicationService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author liulei
 * @Description 支付申请Controler
 * @ClassName com.authine.cloudpivot.web.api.controller.PaymentApplicationController
 * @Date 2020/3/20 14:40
 **/
@RestController
@RequestMapping("/controller/paymentApplication")
@Slf4j
public class PaymentApplicationController extends BaseController {

    @Resource
    private PaymentApplicationService paymentApplicationService;

    /**
     * 方法说明：合并支付申请数据
     * @param ids 支付申请id,多个id使用“,”隔开
     * @param supplierId 供应商表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/20 14:47
     */
    @GetMapping("/mergeDatas")
    @ResponseBody
    public ResponseResult <String> mergeDatas(String ids, String supplierId) {
        if (StringUtils.isBlank(ids)) {
            log.info("获取支付申请id出错!");
            return this.getErrResponseResult("error", 404l, "获取支付申请id出错!");
        }
        if (StringUtils.isBlank(supplierId)) {
            log.info("获取支付申请id出错!");
            return this.getErrResponseResult("error", 404l, "获取供应商信息出错!");
        }

        String idArr[] = ids.split(",");
        List <String> idList = Arrays.asList(idArr);
        if (idList == null || idList.size() <= 1) {
            log.info("获取支付申请id出错!");
            return this.getErrResponseResult("error", 404l, "获取支付申请id出错!");
        }

        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());

            paymentApplicationService.mergeData(idList, supplierId, user, dept, this.getWorkflowInstanceFacade(),
                    this.getBizObjectFacade());

            return this.getOkResponseResult("success", "操作成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：创建支付明细账单和支付申请数据
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/23 14:10
     */
    @GetMapping("/createPaymentApplicationData")
    public ResponseResult <String> createPaymentApplicationData(){
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());

            paymentApplicationService.createPaymentApplicationData(user, dept, this.getBizObjectFacade(), this.getWorkflowInstanceFacade());

            return this.getOkResponseResult("success", "操作成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：支付明细一次性费用导入
     * @param fileName 文件名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/30 13:57
     */
    @GetMapping("/importOneTimeFee")
    @ResponseBody
    public ResponseResult <String> importOneTimeFee(String fileName){
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            paymentApplicationService.importOneTimeFee(fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：省内导入临时收费数据
     * @param fileName 文件名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/30 13:57
     */
    @GetMapping("/importTemporaryCharge")
    @ResponseBody
    public ResponseResult <String> importTemporaryCharge(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            paymentApplicationService.importTemporaryCharge(fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：导入支付明细，生成支付申请
     * @param fileName 文件名称
     * @param systemType 系统类型（互为系统：mutual_system; 全国系统：national_system）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/30 16:17
     */
    @GetMapping("/importPaymentDetails")
    @ResponseBody
    public ResponseResult <String> importPaymentDetails(String fileName, String systemType){
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            UserModel user = this.getOrganizationFacade().getUser(getUserId());
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            paymentApplicationService.importPaymentDetails(fileName, user, dept, systemType,
                    this.getWorkflowInstanceFacade());
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }
}
