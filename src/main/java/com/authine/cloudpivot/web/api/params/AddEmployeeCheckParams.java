package com.authine.cloudpivot.web.api.params;

import com.sun.istack.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
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
    @NotNull
    private String creater;

    /**
     * 业务类型
     */
    @NotNull
    private String businessType;

    /**
     * 福利地
     */
    @NotNull
    private String welfare;

    /**
     * 福利办理方
     */
    @NotNull
    private String welfareHandler;

    /**
     * 证件类型
     */
    @NotNull
    private String identityNoType;

    /**
     * 证件号
     */
    @NotNull
    private String identityNo;

    /**
     * 公积金基数
     */
    @NotNull
    private String providentFundBase;

    /**
     * 公积金单位比例
     */
    @NotNull
    private Double companyProvidentFundBl;

}
