package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 10:01
 * @Description: 员工档案Controller
 */
@RestController
@RequestMapping("/controller/employeeFiles")
public class EmployeeFilesController extends BaseController {

    @Autowired
    EmployeeFilesService employeeFilesService;

    @GetMapping("/getEmployeeFiles")
    public ResponseResult<Object> getEmployeeFilesByIdNoOrClientName(@RequestParam(required = true) String idNo, @RequestParam(required = false) String clientName) {
        EmployeeFiles employeeFiles = employeeFilesService.getEmployeeFilesByIdNoOrClientName(idNo, clientName);
        return this.getErrResponseResult(employeeFiles, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
