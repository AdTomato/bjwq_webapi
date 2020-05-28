package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省外城市节点中时间节点详情
 *
 * @author wangyong
 * @time 2020/5/28 9:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityTimeNodeSwDetails {

    /**
     * id
     */
    private String id;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 排序字段
     */
    private Double sortKey;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 供应商
     */
    private String supplierName;

    /**
     * 社保时间节点
     */
    private Integer sTimeNode;

    /**
     * 公积金时间节点
     */
    private Integer gTimeNode;

}
