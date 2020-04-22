package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 员工订单表单实体
 * @ClassName com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
 * @Date 2020/2/21 10:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeOrderForm extends BaseEntity {
    /** 员工档案id*/
    String employeeFilesId;
    String firstLevelClientName;
    String secondLevelClientName;
    String businessType;
    String idType;
    String identityNo;
    /** 详细*/
    String detail;

    /** 是否历史订单*/
    String isHistory;

    /** 总金额*/
    Double total;

    /** 社保福利地*/
    String socialSecurityCity;

    String sWelfareHandler;

    /** 公积金福利地*/
    String providentFundCity;

    String gWelfareHandler;

    /** 养老保险*/
    String endowment;

    /** 医疗保险*/
    String medical;

    /** 失业保险*/
    String unemployment;

    /** 工伤保险*/
    String workRelatedInjury;

    /** 生育保险*/
    String childbirth;

    /** 大病保险*/
    String criticalIllness;

    /** 住房公积金*/
    String housingAccumulationFunds;

    /** 服务费*/
    Double serviceFee;

    /** 开始时间*/
    Date startTime;

    /**  结束数据*/
    Date endTime;

    /**  输入时间*/
    Date inputTime;

    /**  金额*/
    Double sum;

    /**  社保状态*/
    String socialSecurityStatus;

    /**  公积金状态*/
    String providentFundStatus;

    /**  社保申报详细*/
    List <Map <String, String>> socialSecurityDetail;
    /**  公积金申报详细*/
    List <Map <String, String>> providentFundDetail;
}
