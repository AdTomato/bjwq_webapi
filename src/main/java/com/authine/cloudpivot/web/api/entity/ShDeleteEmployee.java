package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 减员_上海实体
 * @ClassName com.authine.cloudpivot.web.api.entity.ShDeleteEmployee
 * @Date 2020/2/25 15:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShDeleteEmployee extends BaseEntity {
    String shDeleteEmployeeId;
    /** 姓名*/
    String employeeName;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 客户编号*/
    String clientNum;

    /** 客户名称*/
    String clientName;

    /** 客户简称*/
    String clientShortName;

    /** OS发起离职时间*/
    Date osInitiatedDepartureTime;

    /** 离职日期*/
    Date departureTime;

    /** 收费结束时间*/
    Date chargeEndTime;

    /** 离职原因*/
    String leaveReason;

    /** 离职备注*/
    String leaveRemark;

    /** 公积金转移方式*/
    String providentFundTransferMode;

    /** 退档地*/
    String backtrack;

    /** GE离职原因*/
    String geLeaveReason;

    /** 客户方编号*/
    String customerNum;

    /** 是否离职E化*/
    String weatherLeaveE;

    public ShDeleteEmployee(String employeeName, String identityNoType, String identityNo, String clientNum,
                            String clientName, String clientShortName, Date osInitiatedDepartureTime,
                            Date departureTime, Date chargeEndTime, String leaveReason, String leaveRemark,
                            String providentFundTransferMode, String backtrack, String geLeaveReason,
                            String customerNum, String weatherLeaveE, String id, String creater) {
        super();
        this.employeeName = employeeName;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.clientNum = clientNum;
        this.clientName = clientName;
        this.clientShortName = clientShortName;
        this.osInitiatedDepartureTime = osInitiatedDepartureTime;
        this.departureTime = departureTime;
        this.chargeEndTime = chargeEndTime;
        this.leaveReason = leaveReason;
        this.leaveRemark = leaveRemark;
        this.providentFundTransferMode = providentFundTransferMode;
        this.backtrack = backtrack;
        this.geLeaveReason = geLeaveReason;
        this.customerNum = customerNum;
        this.weatherLeaveE = weatherLeaveE;
        setId(id);
        setCreater(creater);
    }
}
