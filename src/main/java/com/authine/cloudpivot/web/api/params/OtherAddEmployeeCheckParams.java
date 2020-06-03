package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.NationwideDispatch;
import com.authine.cloudpivot.web.api.entity.ShAddEmployee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyong
 * @time 2020/5/30 11:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherAddEmployeeCheckParams {

    private String identityNo;

    private String identityNoType;

    private String firstLevelClientName;

    private String secondLevelClientName;

    private String businessType;

    private String welfare;

    private String welfareHandler;


    public OtherAddEmployeeCheckParams(ShAddEmployee shAddEmployee) {
        this.identityNo = shAddEmployee.getIdentityNo();
        this.identityNoType = shAddEmployee.getIdentityNoType();
        this.firstLevelClientName = shAddEmployee.getFirstLevelClientName();
        this.secondLevelClientName = shAddEmployee.getSecondLevelClientName();
        this.businessType = "代理";
        this.welfare = shAddEmployee.getCityName();
        this.welfareHandler = shAddEmployee.getWelfareHandler();
    }

    public OtherAddEmployeeCheckParams(NationwideDispatch qgAddEmployee) {
        this.identityNo = qgAddEmployee.getIdentityNo();
        this.identityNoType = qgAddEmployee.getIdentityNoType();
        this.firstLevelClientName = qgAddEmployee.getFirstLevelClientName();
        this.secondLevelClientName = qgAddEmployee.getSecondLevelClientName();
        this.businessType = "代理";
        this.welfare = qgAddEmployee.getInvolved();
        this.welfareHandler = qgAddEmployee.getWelfareHandler();
    }
}
