package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省外城市节点
 *
 * @author wangyong
 * @time 2020/5/28 8:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityTimeNodeSw extends BaseEntity {

    /**
     * 业务年月
     */
    private String businessYear;

}
