package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.UnsealAndSealInfos;
import com.authine.cloudpivot.web.api.params.ImportCondition;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DeclareStopPaymentService
 * @Author:lfh
 * @Date:2020/3/28 14:08
 * @Description: 公积金申报停缴service
 **/
public interface DeclareStopPaymentService {
    List<OpenAccountInfo> findOpenAccountInfo(ImportCondition importCondition);


    List<UnsealAndSealInfos> findSealInfo(ImportCondition importCondition);


    Map<String,BigDecimal> findProportion(String identityNo);

    String findOwnerById(String owner);
}
