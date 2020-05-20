package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 销售合同中关联协议
 *
 * @author wangyong
 * @time 2020/5/14 9:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationAgreement {

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
     * 客户名称
     */
    private String clientName;

}
