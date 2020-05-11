package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.entity.EnquiryReceivable;

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

    /**
     * 获取账单查询数据
     *
     * @param billYear
     * @param customerName
     * @param employeeNature
     * @return 账单查询
     */
    EnquiryReceivable getEnquiryReceivable(String billYear, String customerName, String employeeNature);

    /**
     * 更新账单查询
     *
     * @param updateEnquiry
     */
    void updateEnquiryReceivable(List<EnquiryReceivable> updateEnquiry);

    /**
     * 创建账单查询
     *
     * @param createEnquiry
     */
    void createEnquiryReceivable(List<EnquiryReceivable> createEnquiry);

}
