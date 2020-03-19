package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.SalesContractBasic;
import com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 0:30
 * @Description: 销售合同
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesContractDto extends SalesContractBasic {

    private List<ServiceChargeUnitPrice> serviceChargeUnitPrices;

}
