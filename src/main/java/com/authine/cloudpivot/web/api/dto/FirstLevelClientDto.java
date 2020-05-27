package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.ContactInfo;
import com.authine.cloudpivot.web.api.entity.FirstLevelClient;
import com.authine.cloudpivot.web.api.entity.FlcSalesman;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 一级客户扩展
 *
 * @author wangyong
 * @time 2020/5/20 16:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirstLevelClientDto extends FirstLevelClient {

    /**
     * 业务员信息
     */
    private List<FlcSalesman> flcSalesmanList;

    /**
     * 联系人信息
     */
    private List<ContactInfo> contactInfoList;

}
