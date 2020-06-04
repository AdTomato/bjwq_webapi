package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.ClientManagement;
import com.authine.cloudpivot.web.api.entity.ProcessIdentityNo;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn;
import com.authine.cloudpivot.web.api.service.*;
import com.authine.cloudpivot.web.api.utils.DateUtils;
import com.authine.cloudpivot.web.api.utils.SubmitCheckUtils;
import com.authine.cloudpivot.web.api.utils.UnitUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 提交检验
 *
 * @author wangyong
 * @time 2020/5/26 11:25
 */
@RestController
@Slf4j
@Api(value = "提交校验", tags = "二次开发：提交校验")
@RequestMapping("/controller/submitCheckout")
public class SubmitCheckController extends BaseController {

    @Autowired
    ClientManagementService clientManagementService;

    @Autowired
    ClientService clientService;

    @Autowired
    CityTimeNodeService cityTimeNodeService;

    @Autowired
    CollectionRuleService collectionRuleService;

    @Autowired
    UnitService unitService;


    @ApiOperation(value = "增员客户提交校验")
    @RequestMapping("/addEmployeeCheck")
    public ResponseResult<Object> addEmployeeCheck(@RequestBody AddEmployeeCheckParams params) {
        AddEmployeeCheckReturn result = SubmitCheckUtils.addEmployeeCheck(params);
        return this.getErrResponseResult(result, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
