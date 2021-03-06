package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.utils.ExcelFileUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.NationalDeliveryReadExcelFile;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 13:31
 * @Description: 全国派单controller
 */
@RestController
@RequestMapping("/controller/nationalDelivery")
public class NationalDeliveryController extends BaseController {

    @Resource
    ExcelFileUtils excelFileUtils;

    @Resource
    NationalDeliveryReadExcelFile nationalDeliveryReadExcelFile;

    @PostMapping("/importDeliveryData")
    public ResponseResult<String> importDeliveryData(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }

        BizObjectFacade bizObjectFacade = this.getBizObjectFacade();
        WorkflowInstanceFacade workflowInstanceFacade = this.getWorkflowInstanceFacade();
        OrganizationFacade organizationFacade = this.getOrganizationFacade();

        String userId = UserUtils.getUserId(this.getUserId());
        UserModel user = organizationFacade.getUser(userId);

        nationalDeliveryReadExcelFile.setTableName(Constants.NATIONWIDE_DISPATCH_TABLE_NAME);
        nationalDeliveryReadExcelFile.setWorkflowCode(null);

        excelFileUtils.insertData(fileName, userId, user.getDepartmentId(), bizObjectFacade, organizationFacade, workflowInstanceFacade, nationalDeliveryReadExcelFile);

//        PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456");

//        nationalDeliveryService.importEmployee(fileName, userId, user.getDepartmentId(), bizObjectFacade, organizationFacade, workflowInstanceFacade);

        return this.getOkResponseResult("success", "导入成功");
    }

}
