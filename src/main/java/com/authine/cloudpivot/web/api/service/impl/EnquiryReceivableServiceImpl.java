package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.mapper.EnquiryReceivableMapper;
import com.authine.cloudpivot.web.api.service.EnquiryReceivableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/4/2 16:27
 * @Description:
 */
@Service
@Slf4j
public class EnquiryReceivableServiceImpl implements EnquiryReceivableService {

    @Resource
    EnquiryReceivableMapper enquiryReceivableMapper;

    /**
     * @param ids: 账单查询id
     * @Author: wangyong
     * @Date: 2020/4/2 16:29
     * @return: java.util.List<com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto>
     * @Description: 根据id集合获取账单查询数据
     */
    @Override
    public List<EnquiryReceivableDto> getEnquiryReceivableDtoByIds(List<String> ids) {
        return enquiryReceivableMapper.getEnquiryReceivableDtoByIds(ids);
    }
}
