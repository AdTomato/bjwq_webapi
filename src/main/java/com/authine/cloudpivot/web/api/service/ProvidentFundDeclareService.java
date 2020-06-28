package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.DeclareDto;
import com.authine.cloudpivot.web.api.dto.ProvidentFundDeclareDto;

import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @time 2020/6/23 16:04
 */
public interface ProvidentFundDeclareService {

    List<DeclareDto> getProvidentFundDeclareDtoList(Map conditions);


}
