package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.service.UnitService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/4 13:35
 */
@RestController
@Slf4j
@RequestMapping("/controller/unit")
@Api(value = "组织机构", tags = "二次开发：组织机构")
public class UnitController extends BaseController {

    @Autowired
    UnitService unitService;

    @GetMapping("/getDeptByUserIds")
    public ResponseResult<Object> getDeptByUserIds(@RequestBody(required = true) List<String> users) {
        log.info("获取部门组织");
        if (users == null || users.isEmpty()) {
            return this.getErrResponseResult(null, 407L, "参数不能为空");
        }
        List<Unit> depts = unitService.getDeptUnitByUserIds(users);
        if (depts == null || depts.isEmpty()) {
            return this.getErrResponseResult(null, 408L, "获取的结果为空");
        }
        return this.getErrResponseResult(depts, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
