package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 二级客户里面的联系人子表
 *
 * @author wangyong
 * @time 2020/5/21 13:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondContactInfo {

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
     * 职位
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

}
