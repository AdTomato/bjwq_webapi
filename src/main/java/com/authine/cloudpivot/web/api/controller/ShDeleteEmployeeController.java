package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.utils.ExcelFileUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ShReadDeleteExcelFile;
import com.authine.cloudpivot.web.api.utils.UserUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 10:42
 * @Description: 减员(上海)controller
 */
@RestController
@RequestMapping("/controller/shDeleteEmployee")
public class ShDeleteEmployeeController extends BaseController {


    @Resource
    ExcelFileUtils excelFileUtils;

    @Resource
    ShReadDeleteExcelFile shReadDeleteExcelFile;

    @PostMapping("/importEmployee")
    public ResponseResult<String> importEmployee(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            return this.getOkResponseResult("error", "文件名不能为空");
        }

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }

        OrganizationFacade organizationFacade = this.getOrganizationFacade();
        BizObjectFacade bizObjectFacade = this.getBizObjectFacade();
        WorkflowInstanceFacade workflowInstanceFacade = this.getWorkflowInstanceFacade();
        String userId = UserUtils.getUserId(this.getUserId());
        UserModel user = organizationFacade.getUser(userId);

        shReadDeleteExcelFile.setTableName(Constants.SH_DELETE_EMPLOYEE_TABLE_NAME);
        shReadDeleteExcelFile.setWorkflowCode(Constants.SH_DELETE_EMPLOYEE_SCHEMA_WF);

        excelFileUtils.insertData(fileName, userId, user.getDepartmentId(), bizObjectFacade, organizationFacade, workflowInstanceFacade, shReadDeleteExcelFile);
        return this.getOkResponseResult("success", "导入成功");
    }

}
