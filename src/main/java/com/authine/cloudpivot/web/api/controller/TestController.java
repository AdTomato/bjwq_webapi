package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.AppManagementFacade;
import com.authine.cloudpivot.engine.api.model.bizform.BizFormHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryHeaderModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.BatchPreDispatch;
import com.authine.cloudpivot.web.api.entity.ColumnComment;
import com.authine.cloudpivot.web.api.mapper.TableMapper;
import com.authine.cloudpivot.web.api.service.BatchPreDispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 14:13
 * @Description:
 */
@RestController
@RequestMapping("/controller/test")
public class TestController extends BaseController {

    @Resource
    TableMapper tableMapper;

    @Autowired
    BatchPreDispatchService batchPreDispatchService;

    @RequestMapping("/getProperty")
    public void getProperty() {
//        List<ColumnComment> shAddEmployeeTableColumnComment = tableMapper.getShAddEmployeeTableColumnComment();

        List<BatchPreDispatch> batchPreDispatches = new ArrayList<>();
        String userId = "402881c16eed73b5016eeea6921b0357";
        BatchPreDispatch b1 = new BatchPreDispatch();
        b1.setEmployeeName("王勇");
        BatchPreDispatch b2 = new BatchPreDispatch();
        b2.setEmployeeName("黄河");
        batchPreDispatches.add(b1);
        batchPreDispatches.add(b2);
        batchPreDispatchService.addBatchPreDispatchs(userId, this.getOrganizationFacade(), batchPreDispatches);

    }

}
