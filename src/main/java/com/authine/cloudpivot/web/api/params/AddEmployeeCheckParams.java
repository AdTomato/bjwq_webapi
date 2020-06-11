package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.AddEmployee;
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
     * 数据id
     */
    String id;

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
     * 社保基数
     */
    private Double socialSecurityBase;

    /**
     * 公积金基数
     */
    @NotNull
    private Double providentFundBase;

    /**
     * 公积金单位比例
     */
    @NotNull
    private Double companyProvidentFundBl;

    public AddEmployeeCheckParams(AddEmployee addEmployee) {
        this.creater = addEmployee.getCreater();
        this.businessType = addEmployee.getEmployeeNature();
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            this.welfare = addEmployee.getSocialSecurityCity();
            this.welfareHandler = addEmployee.getSWelfareHandler();
        } else {
            this.welfare = addEmployee.getProvidentFundCity();
            this.welfareHandler = addEmployee.getGWelfareHandler();
        }
        this.identityNoType = addEmployee.getIdentityNoType();
        this.identityNo = addEmployee.getIdentityNo();
        this.providentFundBase = addEmployee.getProvidentFundBase();
        this.companyProvidentFundBl = addEmployee.getCompanyProvidentFundBl();
    }
}
