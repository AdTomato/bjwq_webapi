package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.service.ClientService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 09:43
 * @Description: 客户档案管理controller
 */
@RestController
@RequestMapping("/controller/clientController")
@Slf4j
public class ClientFileController extends BaseController {

    @Autowired
    ClientService clientService;

    /**
     * @param clientName : 客户名称
     * @return : com.authine.cloudpivot.web.api.view.ResponseResult<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: wangyong
     * @Date: 2020/2/6 9:53
     * @Description: 根据客户名称获取业务员
     */
    @GetMapping("/getClientSalesman")
    public ResponseResult<Map<String, Object>> getClientSalesman(String clientName, String entrustedUnit, String area, String staffNature) {
//        String firstLevelClientId = clientService.getFirstLevelClientId("测试", "测试");
        Map clientSalesmanAndFree = clientService.getClientSalesmanAndFee(clientName, entrustedUnit, area, staffNature);
        return this.getOkResponseResult(null, "没有获取该客户的业务员");
    }

}
