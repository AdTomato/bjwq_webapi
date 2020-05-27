package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 一级客户中的联系人信息子表
 *
 * @author wangyong
 * @time 2020/5/20 15:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfo {

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
     * 联系人
     */
    private String contactPerson;

    /**
     * 部门
     */
    private String department;

    /**
     * 职务
     */
    private String position;

    /**
     * 手机号
     */
    private String telephoneNum;

    /**
     * 微信号
     */
    private String wxCode;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 办公地址
     */
    private String officeAddress;

    /**
     * 出生日期
     */
    private Date dateOfBirth;

    /**
     * 家庭地址
     */
    private String homeAddress;

    /**
     * 对接人详细信息
     */
    private String dockingPeopleInfo;

    /**
     * 查询人
     */
    private String inquirer;

}
