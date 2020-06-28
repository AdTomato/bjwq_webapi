package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.DeclareDto;
import com.authine.cloudpivot.web.api.dto.ProvidentFundDeclareDto;

import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @time 2020/6/23 16:06
 */
public interface ProvidentFundDeclareMapper {

    List<DeclareDto> getProvidentFundDeclareDtoList(Map conditions);

}
