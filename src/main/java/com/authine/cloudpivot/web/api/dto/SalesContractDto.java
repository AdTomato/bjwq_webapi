package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.AssociationAgreement;
import com.authine.cloudpivot.web.api.entity.SalesContractBasic;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 销售合同
 * @author wangyong
 * @Date:2020/3/18 0:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesContractDto extends SalesContractBasic {

    /**
     * 销售单价列表
     */
    private List<ServiceChargeUnitPrice> serviceChargeUnitPrices;

    /**
     * 关联协议
     */
    private List<AssociationAgreement> associationAgreements;

}
