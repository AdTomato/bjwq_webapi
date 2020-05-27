package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.NccpsProvidentFundRatio
 * @Date 2020/5/11 13:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NccpsProvidentFundRatio {
    String id;
    String parentId;
    Double sortKey;
    String province;
    String city;
    String welfareHandler;
    Double providentFundUnitRatio;
    Double providentFundIndividualRatio;
}
