package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 减员客户校验参数
 * @ClassName com.authine.cloudpivot.web.api.params.DelEmployeeCheckParams
 * @Date 2020/6/1 10:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelEmployeeCheckParams {
    /**
     * 创建人
     */
    @NotNull
    private String creater;

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

    public DelEmployeeCheckParams(DeleteEmployee deleteEmployee) {
        this.creater = deleteEmployee.getCreater();
        this.identityNo = deleteEmployee.getIdentityNo();
        this.identityNoType = deleteEmployee.getIdentityNoType();
    }
}
