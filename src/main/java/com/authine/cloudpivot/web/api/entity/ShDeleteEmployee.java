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
    String firstLevelClientName;
    String secondLevelClientName;
    String subordinateDepartment;
    String gender;
    Date birthday;

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

    String returnReason;

    String operator;
    String inquirer;
}
