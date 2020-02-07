package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.SystemManageService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:07
 * @Description: 系统管理controller
 */
@RestController
@RequestMapping("/controller/systemManage")
public class SystemManageController extends BaseController {

    @Autowired
    SystemManageService systemManageService;

    @GetMapping("/getTimeNode")
    public ResponseResult<Map<String, Integer>> getTimeNode(String cityName) {
        Integer timeNode = systemManageService.getTimeNodeByCity(cityName);
        Map<String, Integer> result = new HashMap<>();
        result.put("timeNode", timeNode);
        return this.getOkResponseResult(result, "成功");
    }

}
