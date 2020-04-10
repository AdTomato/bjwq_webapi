package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.Ccps
 * @Date 2020/4/2 14:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ccps {
    String clienNumber;
    int timeNode;
    String client;
    Double companyInjuryRatio;
    Double employeeInjuryRatio;
    Double companyAccumulationRatio;
    Double employeeAccumulationRatio;
}
