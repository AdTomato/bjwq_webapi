package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.StartAndEndTime
 * @Date 2020/6/4 14:15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartAndEndTime {
    Integer startChargeTime = 0;
    Integer endChargeTime = Integer.MAX_VALUE;
    String productName;
}
