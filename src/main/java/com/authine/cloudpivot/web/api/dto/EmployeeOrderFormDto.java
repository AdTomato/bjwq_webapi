package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.EmployeeOrderForm;
import com.authine.cloudpivot.web.api.entity.SocialSecurityFundDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 8:44
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeOrderFormDto extends EmployeeOrderForm {

    private List<SocialSecurityFundDetail> socialSecurityFundDetails;

}
