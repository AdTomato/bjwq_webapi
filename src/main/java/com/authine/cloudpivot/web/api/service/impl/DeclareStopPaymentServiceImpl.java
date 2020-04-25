package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.UnsealAndSealInfos;
import com.authine.cloudpivot.web.api.mapper.DeclareStopPaymentMapper;
import com.authine.cloudpivot.web.api.service.DeclareStopPaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DeclareStopPaymentServiceImpl
 * @Author:lfh
 * @Date:2020/3/28 14:09
 * @Description: 公积金申报停缴service
 **/
@Service
public class DeclareStopPaymentServiceImpl implements DeclareStopPaymentService {
    @Resource
    private DeclareStopPaymentMapper declareStopPaymentMapper;

    @Override
    public List<OpenAccountInfo> findOpenAccountInfo(Date startTime, Date endTime, String welfare_handler) {
        return declareStopPaymentMapper.findOpenAccountInfo(startTime,endTime,welfare_handler);
    }

    @Override
    public List<UnsealAndSealInfos> findUnsealInfo(Date startTime, Date endTime, String welfare_handler) {
        return declareStopPaymentMapper.findUnsealInfo(startTime,endTime,welfare_handler);
    }

    @Override
    public List<UnsealAndSealInfos> findSealInfo(Date startTime, Date endTime, String welfare_handler) {
        return declareStopPaymentMapper.findSealInfo(startTime,endTime,welfare_handler);
    }

    @Override
    public String findAccountNum(String identityNo) {
        return declareStopPaymentMapper.findAccountNum(identityNo);
    }

    @Override
    public Map<String, BigDecimal> findProportion(String id) {
        return declareStopPaymentMapper.findProportion(id);
    }
}
