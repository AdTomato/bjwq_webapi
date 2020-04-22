package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.AdjustBaseAndRatioMapper
 * @Date 2020/4/13 17:07
 **/
public interface AdjustBaseAndRatioMapper {

    /**
     * 方法说明：更新收费明细的是否比对为否
     * @author liulei
     * @Date 2020/4/13 17:20
     */
    void updateBillDetailsWhetherCompareToNo(String billYear, List <EmployeeFilesDto> list);

    /**
     * 方法说明：更新支付明细的是否比对为否
     * @author liulei
     * @Date 2020/4/13 17:20
     */
    void updatePaymentDetailsWhetherCompareToNo(String billYear, List <EmployeeFilesDto> list);
}
