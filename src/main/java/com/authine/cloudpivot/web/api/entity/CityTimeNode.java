package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 城市时间节点
 *
 * @author wangyong
 * @time 2020/5/27 15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityTimeNode extends BaseEntity {

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 时间节点
     */
    private Integer timeNode;

    /**
     * 业务年月
     */
    private String businessYear;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
