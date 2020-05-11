package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.AdjustBaseAndRatioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 调基调比Controller
 * @ClassName com.authine.cloudpivot.web.api.controller.AdjustBaseAndRatioController
 * @Date 2020/4/13 17:04
 **/
@RestController
@RequestMapping("/controller/adjustBaseAndRatio")
@Slf4j
public class AdjustBaseAndRatioController extends BaseController {

    @Resource
    private AdjustBaseAndRatioService adjustBaseAndRatioService;

}
