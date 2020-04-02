package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/4/2 16:26
 * @Description: 账单查询service接口
 */
public interface EnquiryReceivableService {

    /**
     * 根据id集合获取账单查询数据
     *
     * @param ids
     * @return 账单查询
     */
    List<EnquiryReceivableDto> getEnquiryReceivableDtoByIds(List<String> ids);

}
