package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.PaymentApplicationService;
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
}
