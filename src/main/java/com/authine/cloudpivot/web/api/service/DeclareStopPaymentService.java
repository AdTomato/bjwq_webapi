package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.UnsealAndSealInfos;

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
    List<OpenAccountInfo> findOpenAccountInfo(Date startTime, Date endTime, String welfare_handler);

    List<UnsealAndSealInfos> findUnsealInfo(Date startTime, Date endTime, String welfare_handler);

    List<UnsealAndSealInfos> findSealInfo(Date startTime, Date endTime, String welfare_handler);

    String findAccountNum(String identityNo);

    Map<String,BigDecimal> findProportion(String id);
}
