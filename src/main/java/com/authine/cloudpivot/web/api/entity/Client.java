package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 客户信息维护时，客户信息
 * @ClassName com.authine.cloudpivot.web.api.entity.Client
 * @Date 2020/1/6 10:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    String id;
    /** 客户名称 */
    String clientName;
    /** 客户账号手机号 */
    String moblie;
    /** 对应用户表的id */
    String userId;
}
