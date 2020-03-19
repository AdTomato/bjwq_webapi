package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    SalesContractService salesContractService;

    @GetMapping("/getEmployeeFiles")
    public ResponseResult<Object> getEmployeeFilesByIdNoOrClientName(@RequestParam(required = true) String idNo, @RequestParam(required = false) String clientName) {
        EmployeeFiles employeeFiles = employeeFilesService.getEmployeeFilesByIdNoOrClientName(idNo, clientName);
        return this.getErrResponseResult(employeeFiles, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PostMapping
    public ResponseResult<Object> calculationBill(String bill) {

        List<SalesContractDto> salesContractByBillDay = salesContractService.getSalesContractByBillDay(bill);

        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
