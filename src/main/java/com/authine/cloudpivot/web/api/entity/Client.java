package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @author liulei
 * @Description 客户信息维护时，客户信息
 * @ClassName com.authine.cloudpivot.web.api.entity.Client
 * @Date 2020/1/6 10:56
 **/
@Data
public class Client {
    String id;
    /**
     * 客户名称
     */
    String clientName;
    /**
     * 客户账号用户名称
     */
    String employeeName;
    /**
     * 客户账号手机号
     */
    String moblie;
    /**
     * 对应用户表的id
     */
    String userId;

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", clientName='" + clientName + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", moblie='" + moblie + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
