package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 减员_客户实体
 * @ClassName com.authine.cloudpivot.web.api.entity.DeleteEmployee
 * @Date 2020/2/25 15:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEmployee extends BaseEntity {
    String deleteEmployeeId;

    /** 客户名称*/
    String clientName;

    /** 姓名*/
    String employeeName;

    /** 证件类型*/
    String identityNoType;

    /** 证件号码*/
    String identityNo;

    /** 地区*/
    String city;

    /** 离职原因*/
    String leaveReason;

    /** 离职日期*/
    Date leaveTime;

    /** 社保终止时间*/
    Date socialSecurityEndTime;

    /** 公积金终止时间*/
    Date providentFundEndTime;

    /** 备注*/
    String remark;

    public DeleteEmployee(String clientName, String employeeName, String identityNoType, String identityNo,
                          String city, String leaveReason, Date leaveTime, Date socialSecurityEndTime,
                          Date providentFundEndTime, String remark, String id, String creater, Date createdTime) {
        super();
        this.clientName = clientName;
        this.employeeName = employeeName;
        this.identityNoType = identityNoType;
        this.identityNo = identityNo;
        this.city = city;
        this.leaveReason = leaveReason;
        this.leaveTime = leaveTime;
        this.socialSecurityEndTime = socialSecurityEndTime;
        this.providentFundEndTime = providentFundEndTime;
        this.remark = remark;
        setId(id);
        setCreater(creater);
        setCreatedTime(createdTime);
    }
}
