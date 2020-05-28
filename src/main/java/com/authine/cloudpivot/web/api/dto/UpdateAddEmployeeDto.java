package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.dto.UpdateAddEmployeeDto
 * @Date 2020/5/19 11:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAddEmployeeDto {
    // 员工订单数据
    EmployeeOrderForm orderForm;
    // 社保申报数据
    SocialSecurityDeclare sDeclare;
    // 公积金申报数据
    ProvidentFundDeclare pDeclare;
    EntryNotice entryNotice;
}
