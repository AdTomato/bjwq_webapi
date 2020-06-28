package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.OrderDetails;
import lombok.Data;

import java.util.List;

@Data
public class DeclareDto {

    /**
     * id值
     */
    private String id;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 证件号码
     */
    private String identityNo;

    /**
     * 一级客户名称
     */
    private String firstLevelClientName;

    /**
     * 二级客户名称
     */
    private String secondLevelClientName;

    /**
     * 汇缴
     */
    List<OrderDetails> orderDetailsRemittanceList;

    /**
     * 补缴
     */
    List<OrderDetails> orderDetailsPayBackList;

}
