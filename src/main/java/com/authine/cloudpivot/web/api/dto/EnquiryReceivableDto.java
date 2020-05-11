package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.Bill;
import com.authine.cloudpivot.web.api.entity.EnquiryReceivable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/4/2 16:12
 * @Description: 账单查询DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnquiryReceivableDto extends EnquiryReceivable {

    private List<Bill> bills;

}
