package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 时间截点
 * @ClassName com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime
 * @Date 2020/5/11 13:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NccpsOrderCutOffTime {
    String id;
    String parentId;
    Double sortKey;
    String province;
    String city;
    String welfareHandler;
    Integer timeFrame;
}
