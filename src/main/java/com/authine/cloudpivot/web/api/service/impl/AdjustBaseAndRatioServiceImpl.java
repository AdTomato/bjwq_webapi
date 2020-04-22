package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.AdjustBaseAndRatioMapper;
import com.authine.cloudpivot.web.api.service.AdjustBaseAndRatioService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 调基调比ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.AdjustBaseAndRatioServiceImpl
 * @Date 2020/4/13 17:06
 **/
@Service
public class AdjustBaseAndRatioServiceImpl implements AdjustBaseAndRatioService {

    @Resource
    private AdjustBaseAndRatioMapper adjustBaseAndRatioMapper;

}
