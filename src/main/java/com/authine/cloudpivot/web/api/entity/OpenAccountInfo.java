package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @ClassName OpenAccountInfo
 * @Author:lfh
 * @Date:2020/3/28 14:13
 * @Description: 导出开户信息对应excel实体类
 **/
@Data
public class OpenAccountInfo  extends BaseEntity{
    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 个人姓名
     */
    private String employee_name;

    /**
     * 证件类型
     */
    private String identityNo_type;

    /**
     * 证件号码
     */
    private String identityNo;


    /**
     * 工资总额
     */
    private Double salaryCount;

    /**
     * 个人缴存基数
     */
    private Double personalSaveBase;
    /**
     * 单位缴存比例
     */
    private Double unitSaveProportion;
    /**
     * 个人缴存比例
     */
    private Double personalSaveProportion;
    /**
     * 月缴存总额
     */
    private Double monthSaveAccount;
    /**
     * 单位月缴存总额
     */
    private Double unitMonthSaveAccount;
    /**
     * 个人月缴存总额
     */
    private Double personalMonthSaveCount;

    /**
     * 个人账户状态
     */
    private String personalAccountStatus;

    /**
     * 工资编号(非必输)
     */
    private String salaryNo;
    /**
     * 姓名简拼(非必输)
     */
    private String nameSimpleSpell;
    /**
     * 姓名全拼(非必输)
     */
    private String nameAllSpell;

    /**
     * 固定电话号码(非必输)
     */
    private String fixedPhone;

}
