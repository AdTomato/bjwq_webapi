package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.OrderDetails;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/17 17:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialSecurityDeclareDto extends SocialSecurityDeclare {

    private String operatingType;

    private String reason;

    private String employeeOrderFormId;

    private List<OrderDetails> orderDetailsRemittanceSbList;

    private List<OrderDetails> orderDetailsPayBackSbList;

}
