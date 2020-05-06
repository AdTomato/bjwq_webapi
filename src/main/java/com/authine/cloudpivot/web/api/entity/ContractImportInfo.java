package com.authine.cloudpivot.web.api.entity;

// import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ContractImportInfo
 * @Author:lfh
 * @Date:2020/3/24 10:15
 * @Description: 合同导入信息
 **/
@Data
public class ContractImportInfo extends BaseEntity {

    private String unitManagementId;

    private String employee_name;

    private String gender;

    private String nation;

    private Date birthday;

    private String identityNo;

    private String accountCharacter;

    private String domicilePlace;

    private String registeredResidence;

    private String residentialArea;

    private String placeOfResidence;

    private String phoneNumber;

    private String personCategory;

    private String educationDegree;

    private String workType;

    private Date contract_signing_date;

    private Date contract_deadline;

    private String contractTermType;

    private Double positive_salary;

    private String workForm;

    private Date employmentRegistrationTime;

    //就业起始时间对应合同里面的 劳动合同起始时间
    private Date unitImploymentStartTime;

}