package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.ProductBaseNum
 * @Date 2020/4/2 15:32
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBaseNum {
    Date startTime;
    Date endTime;
    Double companyBaseNum;
    Double employeeBaseNum;
    Double avgSalary;
    Double employeeMaxBaseNum;
    Double employeeMinBaseNum;
    Double companyMaxBaseNum;
    Double companyMinBaseNum;

    private String id;
    private String parentId;
    private Double sortKey;

}
