package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.AdjustBaseAndRatioMapper
 * @Date 2020/4/13 17:07
 **/
public interface AdjustBaseAndRatioMapper {
    AdjustBaseRatioTask getAdjustBaseRatioTaskById(String id);

    void addAdjustBaseRatioDetails(List<Map<String, Object>> list);

    void updateAdjustBaseRatioDetailsOwner(List<Map<String, Object>> details);

    List<AdjustBaseRatioDetails> getAdjustBaseRatioDetailsByTaskId(String taskId);

    EmployeeOrderFormDto getEmployeeOrderForm(String firstLevelClientName, String secondLevelClientName,
                                              String identityNo, String city, String welfareHandler, String adjustType);

    List<ProductBaseNum> getProductBaseNum(String productId, String startTime);

    void addEmployeeOrderForm(EmployeeOrderFormDto formDto);

    void updateOldEmployeeOrderForm(String id, Date endTime);

    void addSocialSecurityFundDetail(List<SocialSecurityFundDetail> socialSecurityFundDetails);

    void updateSocialSecurityFundDetail(String parentId, Date endTime);

    /**
     * 方法说明：更新收费明细的是否比对为否
     * @return void
     * @author liulei
     * @Date 2020/4/24 10:00
     */
    void updateBillDetailsWhetherCompareToNo(String billYear, String firstLevelClientName,
                                             String secondLevelClientName, String identityNo);

    /**
     * 方法说明：更新支付明细的是否比对为否
     * @return void
     * @author liulei
     * @Date 2020/4/24 10:00
     */
    void updatePaymentDetailsWhetherCompareToNo(String billYear, String firstLevelClientName,
                                                String secondLevelClientName, String identityNo, String adjustType);

    void updateTaskAdjustInfo(String id);
}
