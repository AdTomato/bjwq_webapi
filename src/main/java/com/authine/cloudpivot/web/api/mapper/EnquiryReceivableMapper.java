package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.entity.Bill;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/4/2 16:16
 * @Description:
 */
public interface EnquiryReceivableMapper {

    /**
     * 根据id集合获取账单查询数据
     *
     * @param ids
     * @return 账单查询
     */
    List<EnquiryReceivableDto> getEnquiryReceivableDtoByIds(List<String> ids);

    /**
     * 根据账单查询id获取关联的账单明细
     *
     * @param id
     * @return 账单明细
     */
    List<Bill> getBillsByEnquirtReceivableId(String id);
}
