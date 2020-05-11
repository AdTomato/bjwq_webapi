package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.PaymentDetails;
import com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail;

import java.util.List;

/**
 * @author liulei
 * @Description 支付明细mapper
 * @ClassName com.authine.cloudpivot.web.api.mapper.PaymentDetailsMapper
 * @Date 2020/4/10 15:47
 **/
public interface PaymentDetailsMapper {
    List <PaymentDetails> getNeedComparePaymentDetails(String clientName, String idNo, String entrustUnit,
                                                       String insuredArea, String dataType, String billYear);

    List <EmployeeOrderFormDto> getSbEmployeeOrderFormDto(String id, String billYear);

    List<SocialSecurityFundDetail> getSocialSecurityDetailByParentId(String id);

    List <EmployeeOrderFormDto> getGjjEmployeeOrderFormDto(String id, String billYear);

    List<SocialSecurityFundDetail> getFundDetailByParentId(String id);

    void changeWhetherCompareToYes(List<PaymentDetails> changeWhetherCompares);
}
