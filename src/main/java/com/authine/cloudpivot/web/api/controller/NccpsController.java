package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.ClientManagement;
import com.authine.cloudpivot.web.api.entity.NccpsProvidentFundRatio;
import com.authine.cloudpivot.web.api.params.NccpsGetProvidentFundRatioReturn;
import com.authine.cloudpivot.web.api.service.ClientManagementService;
import com.authine.cloudpivot.web.api.service.NccpsService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/4 15:09
 */
@RestController
@RequestMapping("/controller/nccps")
@Slf4j
@Api(value = "客户征缴个性化设置", tags = "客户征缴个性化设置")
public class NccpsController extends BaseController {

    @Autowired
    NccpsService nccpsService;

    @Autowired
    ClientManagementService clientManagementService;

    @GetMapping("/getProvidentFundRatio")
    @ApiOperation(value = "获取公积金比例")
    public ResponseResult<Object> getProvidentFundRatio(@ApiParam(value = "创建人", required = true) String creater,
                                                        @ApiParam(value = "城市", required = true) String city,
                                                        @ApiParam(value = "福利办理方", required = true) String welfareHandler) {
        log.info("获取客户个性化设置中的公积金比例");
        if (StringUtils.isEmpty(creater) || StringUtils.isEmpty(city) || StringUtils.isEmpty(welfareHandler)) {
            return this.getErrResponseResult(null, 407L, "参数不能为空");
        }

        ClientManagement clientManagement = clientManagementService.getClientNameByUserId(creater);
        if (clientManagement == null) {
            return this.getErrResponseResult(null, 407L, "没有获取到一级客户、二级客户名称");
        }

        String firstClientName = clientManagement.getFirstLevelClientName();
        String secondClientName = clientManagement.getSecondLevelClientName();

        List<NccpsProvidentFundRatio> nccpsProvidentFundRatioList = nccpsService.getNccpsProvidentFoundRatioByFirstOrSecondClientName(firstClientName, secondClientName, city, welfareHandler);
        if (nccpsProvidentFundRatioList == null || nccpsProvidentFundRatioList.isEmpty()) {
            return this.getErrResponseResult(null, 408L, "获取的结果为空");
        }

        NccpsGetProvidentFundRatioReturn result = new NccpsGetProvidentFundRatioReturn();
        result.setProvidentFundUnitRatio(new ArrayList<>());
        result.setProvidentFundIndividualRatio(new ArrayList<>());
        for (NccpsProvidentFundRatio nccpsProvidentFundRatio : nccpsProvidentFundRatioList) {
            Double providentFundUnitRatio = nccpsProvidentFundRatio.getProvidentFundUnitRatio();
            Double providentFundIndividualRatio = nccpsProvidentFundRatio.getProvidentFundIndividualRatio();
            if (providentFundUnitRatio != null && providentFundUnitRatio != 0) {
                if (!result.getProvidentFundUnitRatio().contains(providentFundUnitRatio)) {
                    result.getProvidentFundUnitRatio().add(providentFundUnitRatio);
                }
            }
            if (providentFundIndividualRatio != null && providentFundIndividualRatio != 0) {
                if (!result.getProvidentFundIndividualRatio().contains(providentFundIndividualRatio)) {
                    result.getProvidentFundIndividualRatio().add(providentFundIndividualRatio);
                }
            }
        }

        Collections.sort(result.getProvidentFundUnitRatio());
        Collections.sort(result.getProvidentFundIndividualRatio());

        return this.getErrResponseResult(result, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
