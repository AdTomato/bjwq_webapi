package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户账号维护
 *
 * @author wangyong
 * @time 2020/5/27 13:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientManagement extends BaseEntity {

    /**
     * 一级客户名称
     */
    private String firstLevelClientName;

    /**
     * 二级客户名称
     */
    private String secondLevelClientName;

    /**
     * 对接人姓名
     */
    private String contactName;

    /**
     * 手机号码
     */
    private String moblie;

    /**
     * 用户id
     */
    private String userId;

}
