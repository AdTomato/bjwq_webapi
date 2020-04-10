package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.EntryNotice
 * @Date 2020/2/26 11:13
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryNotice extends BaseEntity {
    String employeeName;
    String identityNo;
    String area;
    String clientName;
    String operateSignatory;
    String socialSecurity;
    String providentFund;
    String submissionMethod;
    String employmentRegisterNum;
    String unitProvidentFundNum;
    String personalProvidentFundNum;
    String operateSignFor;


    public EntryNotice(String employeeName, String identityNo, String area, String clientName) {
        this.employeeName = employeeName;
        this.identityNo = identityNo;
        this.area = area;
        this.clientName = clientName;
    }
}
