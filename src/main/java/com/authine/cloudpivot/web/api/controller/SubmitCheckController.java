package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.BaseEntity;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提交检验
 * @author wangyong
 * @time 2020/5/26 15:04
 */
@RestController
@Slf4j
@Api(value = "提交校验", tags = "二次开发：提交校验")
@RequestMapping("/controller/submitCheckout")
public class SubmitCheckController extends BaseController {

    @ApiOperation(value = "增员客户提交校验")
    @GetMapping("/addEmployeeCheck")
    public ResponseResult<Object> addEmployeeCheck(@RequestBody AddEmployeeCheckParams params) {
        String creater = params.getCreater();

        return null;
    }

}
