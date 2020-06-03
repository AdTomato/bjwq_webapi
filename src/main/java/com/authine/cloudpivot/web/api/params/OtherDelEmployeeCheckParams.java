package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.NationwideDispatch;
import com.authine.cloudpivot.web.api.entity.ShDeleteEmployee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.params.OtherDelEmployeeCheckParams
 * @Date 2020/6/1 10:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherDelEmployeeCheckParams {
    private String identityNoType;

    private String identityNo;

    private String firstLevelClientName;

    private String secondLevelClientName;

    public OtherDelEmployeeCheckParams(ShDeleteEmployee shDeleteEmployee) {
        this.identityNoType = shDeleteEmployee.getIdentityNoType();
        this.identityNo = shDeleteEmployee.getIdentityNo();
        this.firstLevelClientName = shDeleteEmployee.getFirstLevelClientName();
        this.secondLevelClientName = shDeleteEmployee.getSecondLevelClientName();
    }

    public OtherDelEmployeeCheckParams(NationwideDispatch qgDeleteEmployee) {
        this.identityNoType = qgDeleteEmployee.getIdentityNoType();
        this.identityNo = qgDeleteEmployee.getIdentityNo();
        this.firstLevelClientName = qgDeleteEmployee.getFirstLevelClientName();
        this.secondLevelClientName = qgDeleteEmployee.getSecondLevelClientName();
    }
}
