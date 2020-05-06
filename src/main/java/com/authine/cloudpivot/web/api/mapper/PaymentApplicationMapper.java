package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.PaymentApplication;
import com.authine.cloudpivot.web.api.entity.PaymentClientDetails;
import com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 支付申请Mapper
 * @ClassName com.authine.cloudpivot.web.api.mapper.PaymentApplicationMapper
 * @Date 2020/3/20 15:05
 **/
public interface PaymentApplicationMapper {
    /**
     * 方法说明：获取“草稿”状态的数据个数
     * @param ids 表单id
     * @return int
     * @author liulei
     * @Date 2020/3/20 15:17
     */
    int getNotDraftAmount(List<String> ids);

    List<PaymentApplication> getPaymentApplicationByIds(List<String> ids);

    PaymentApplication getPaymentApplicationById(String id);
    /**
     * 方法说明：生成支付申请
     * @param paymentApplication
     * @return void
     * @author liulei
     * @Date 2020/3/23 10:59
     */
    void insertPaymentApplication(PaymentApplication paymentApplication);

    /**
     * 方法说明：根据父id，分组查询出新生成的数据
     * @param parentIds
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.PaymentClientDetails>
     * @author liulei
     * @Date 2020/3/23 13:09
     */
    List<PaymentClientDetails> getPaymentClientDetailsByParentIds(List<String> parentIds);

    /**
     * 方法说明：生成客户明细数据
     * @param details
     * @param parentId
     * @return void
     * @author liulei
     * @Date 2020/3/23 13:45
     */
    void insertPaymentClientDetails(@Param("details") List<PaymentClientDetails> details,
                                    @Param("parentId") String parentId);

    /**
     * 方法说明：根据parentId删除客户明细数据
     * @param parentIds
     * @return void
     * @author liulei
     * @Date 2020/3/23 13:46
     */
    void deletePaymentClientDetailsByParentIds(List<String> parentIds);

    void updatePaymentDetailsPaymentApplicationId(@Param("id") String id, @Param("ids") List<String> ids);

    List<EmployeeFilesDto> getPaymentApplicationAccumulationFundData(String billYear);

    List<EmployeeOrderFormDto> getEmployeeOrderFormByEmployeeFilesId(String id);

    List<SocialSecurityFundDetail> getSocialSecurityFundDetailByParentId(String id);

    List<EmployeeFilesDto> getPaymentApplicationSocialSecurityData(String billYear);

    List<EmployeeOrderFormDto> getEmployeeOrderFormByEmployeeFilesId1(String id);

    List<SocialSecurityFundDetail> getSocialSecurityFundDetailByParentId1(String id);

    void batchInsertPaymentDetailsTemp(List<Map<String, Object>> paymentDetailIds);

    void updatePaymentDetailsByTemp(String sourceId);

    void insertPaymentDetailsByTemp(String sourceId);

    void insertPaymentClientDetailsBySourceId(String sourceId);

    List <PaymentApplication> getSnPaymentApplicationBySourceId(String sourceId);

    List <PaymentApplication> getSwPaymentApplicationBySourceId(String sourceId);

    void batchInsertPaymentApplication(List<PaymentApplication> newList);

    void updatePaymentClientDetailsParentIdBySourceId(String sourceId);

    void updatePaymentDetailsByClientDetails(String sourceId);

    List<Map<String, Object>> getRepeatPaymentApplicationData(String sourceId);

    void deletePaymentDetailsTempBySourceId(String sourceId);

    void updatePaymentDetailsSourceIdToNull(String sourceId);

    void updatePaymentClientDetailsSourceIdToNull(String sourceId);

    void updatePaymentApplicationSourceIdToNull(String sourceId);
}
