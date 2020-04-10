package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.entity.Bill;
import com.authine.cloudpivot.web.api.entity.EnquiryReceivable;

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

    /**
     * 获取账单明细
     *
     * @param billYear
     * @param customerName
     * @param employeeNature
     * @return 账单明细
     */
    EnquiryReceivable getEnquiryReceivable(String billYear, String customerName, String employeeNature);

    /**
     * 更新查询账单
     * @param updateEnquiry
     */
    void updateEnquiryReceivable(List<EnquiryReceivable> updateEnquiry);

    /**
     * 创建查询账单
     * @param createEnquiry
     */
    void createEnquiryReceivable(List<EnquiryReceivable> createEnquiry);
}
