package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.utils.*;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: wangyong
 * @Date: 2020-02-04 08:50
 * @Description: 增员(上海)controller
 */
@RestController
@RequestMapping("/controller/shAddEmployee")
public class ShAddEmployeeController extends BaseController {

    @Resource
    ExcelFileUtils excelFileUtils;

    @Resource
    ShReadAddExcelFile shReadAddExcelFile;

    @PostMapping("/importEmployee")
    public ResponseResult<String> importEmployee(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }

        OrganizationFacade organizationFacade = this.getOrganizationFacade();
        String userId = UserUtils.getUserId(this.getUserId());
        UserModel user = organizationFacade.getUser(userId);

        shReadAddExcelFile.setTableName(Constants.SH_ADD_EMPLOYEE_TABLE_NAME);
        shReadAddExcelFile.setWorkflowCode(Constants.SH_ADD_EMPLOYEE_SCHEMA_WF);

        excelFileUtils.insertData(fileName, userId, user.getDepartmentId(), this.getBizObjectFacade(), this.getOrganizationFacade(), this.getWorkflowInstanceFacade(), shReadAddExcelFile);


        return this.getOkResponseResult("success", "导入成功");
    }

}
