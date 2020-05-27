package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.NccpsWorkInjuryRatio
 * @Date 2020/5/11 13:35
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NccpsWorkInjuryRatio {
    String id;
    String parentId;
    Double sortKey;
    String province;
    String city;
    String welfareHandler;
    Double workInjuryUnitRatio;
}
