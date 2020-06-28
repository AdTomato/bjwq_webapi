package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.DeclareDto;
import com.authine.cloudpivot.web.api.dto.ProvidentFundDeclareDto;
import com.authine.cloudpivot.web.api.mapper.ProvidentFundDeclareMapper;
import com.authine.cloudpivot.web.api.service.ProvidentFundDeclareService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @time 2020/6/23 16:05
 */
@Service
public class ProvidentFundDeclareServiceImpl implements ProvidentFundDeclareService {

    @Resource
    ProvidentFundDeclareMapper providentFundDeclareMapper;

    @Override
    public List<DeclareDto> getProvidentFundDeclareDtoList(Map conditions) {
        return providentFundDeclareMapper.getProvidentFundDeclareDtoList(conditions);
    }
}
