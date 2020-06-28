package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.OrderDetails;
import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import lombok.Data;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/23 15:36
 */
@Data
public class ProvidentFundDeclareDto extends ProvidentFundDeclare {

    List<OrderDetails> orderDetailsRemittanceGjjList;

    List<OrderDetails> orderDetailsPayBackGjjList;

}
