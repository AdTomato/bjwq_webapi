package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName ContractTerminationInfo
 * @Author:lfh
 * @Date:2020/3/27 9:33
 * @Description: 合同解除终止对应excel
 **/
@Data
public class ContractTerminationInfo  extends BaseEntity{

    /**
     * id
     */
    // private String id;
    /**
     * 单位管理码
     */
    private String unitManagementId;
    /**
     * 姓名
     */
    private String employee_name;

    /**
     *身份证号码
     */
    private String identityNo;

    /**
     * 合同解除时间
     */
    private Date contractTerminationDate;

    /**
     * 合同变更状态 默认解除
     */
    private String contractStatus;

    /**
     * 合同终止原因
     */
    private String contractStopReason;

    /**
     * 合同解除原因
     */
    private String contractTerminateReason;
}
