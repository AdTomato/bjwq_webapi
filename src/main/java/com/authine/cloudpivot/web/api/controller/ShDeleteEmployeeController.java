package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.ShDeleteEmployeeService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 10:42
 * @Description: 减员(上海)controller
 */
@RestController
@RequestMapping("/controller/shDeleteEmployee")
public class ShDeleteEmployeeController extends BaseController {

    @Autowired
    ShDeleteEmployeeService shDeleteEmployeeService;

    @PostMapping("/importEmployee")
    public ResponseResult<String> importEmployee(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            return this.getOkResponseResult("error", "文件名不能为空");
        }

        if (ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }

        OrganizationFacade organizationFacade = this.getOrganizationFacade();
        BizObjectFacade bizObjectFacade = this.getBizObjectFacade();
        WorkflowInstanceFacade workflowInstanceFacade = this.getWorkflowInstanceFacade();
        String userId = UserUtils.getUserId(this.getUserId());
        UserModel user = organizationFacade.getUser(userId);
        shDeleteEmployeeService.importEmployee(fileName,userId, user.getDepartmentId(), bizObjectFacade, organizationFacade, workflowInstanceFacade);
        return this.getOkResponseResult("success", "导入成功");
    }

}
