package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.UnsealAndSealInfos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DeclareStopPaymentMapper
 * @Author:lfh
 * @Date:2020/3/28 14:10
 * @Description:
 **/
public interface DeclareStopPaymentMapper {
    List<OpenAccountInfo> findOpenAccountInfo(Date startTime, Date endTime, String welfare_handler);

    List<UnsealAndSealInfos> findUnsealInfo(Date startTime, Date endTime, String welfare_handler);

    List<UnsealAndSealInfos> findSealInfo(Date startTime, Date endTime, String welfare_handler);

    String findAccountNum(String identityNo);


    Map<String,BigDecimal> findProportion(String id);
}
