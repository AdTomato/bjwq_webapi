package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.AppManagementFacade;
import com.authine.cloudpivot.engine.api.model.bizform.BizFormHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryHeaderModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 14:13
 * @Description:
 */
@RestController
@RequestMapping("/controller/test")
public class TestController extends BaseController {

    @RequestMapping("/getProperty")
    public void getProperty() {
        AppManagementFacade appManagementFacade = this.getAppManagementFacade();

        List<BizQueryHeaderModel> bizQueryHeaders = appManagementFacade.getBizQueryHeaders(Constants.SH_ADD_EMPLOYEE_SCHEMA);

        List<BizFormHeaderModel> bizForms = appManagementFacade.getBizForms(Constants.SH_ADD_EMPLOYEE_SCHEMA);

        BizSchemaModel bizSchemaBySchemaCode = appManagementFacade.getBizSchemaBySchemaCode(Constants.SH_ADD_EMPLOYEE_SCHEMA);

        BizSchemaModel bizSchemaBySchemaCode1 = appManagementFacade.getBizSchemaBySchemaCode(Constants.SH_ADD_EMPLOYEE_SCHEMA, true);

        List<BizSchemaModel> allPublishBizSchema = appManagementFacade.getAllPublishBizSchema();

        List<BizPropertyModel> bizPropertyListByCode = appManagementFacade.getBizPropertyListByCode(Constants.SH_ADD_EMPLOYEE_SCHEMA);

    }

}
