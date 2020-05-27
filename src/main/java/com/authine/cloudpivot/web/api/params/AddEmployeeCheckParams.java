package com.authine.cloudpivot.web.api.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 增员客户校验参数
 *
 * @author wangyong
 * @time 2020/5/26 15:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeCheckParams {

    /**
     * 创建人
     */
    private String creater;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 福利地
     */
    private String welfare;

    /**
     * 福利办理方
     */
    private String welfareHandler;

    /**
     * 证件类型
     */
    private String identityNoType;

    /**
     * 证件号
     */
    private String identityNo;

    /**
     * 公积金基数
     */
    private String providentFundBase;

    /**
     * 公积金单位比例
     */
    private Double companyProvidentFundBl;

    /**
     * 公积金个人比例
     */
    private Double employeeProvidentFundBl;

}
