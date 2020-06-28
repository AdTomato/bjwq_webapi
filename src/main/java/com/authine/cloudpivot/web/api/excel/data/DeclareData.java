package com.authine.cloudpivot.web.api.excel.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author wangyong
 * @time 2020/6/23 16:45
 */
@Data
public class DeclareData {


    @ExcelProperty(value = {"id值"}, index = 0)
    private String id;

    @ExcelProperty(value = {"姓名"}, index = 1)
    private String employeeName;

    @ExcelProperty(value = {"身份证"}, index = 2)
    private String identityNo;

    @ExcelProperty(value = {"一级客户名称"}, index = 3)
    private String firstLevelClientName;

    @ExcelProperty(value = {"二级客户名称"}, index = 4)
    private String secondLevelClientName;

    @ExcelProperty(value = {"操作类型"}, index = 5)
    private String operatingType;

    @ExcelProperty(value = {"驳回原因"}, index = 6)
    private String reason;


    @ExcelProperty(value = {"汇缴订单明细", "产品名称"}, index = 7)
    private String productNameH;

    @ExcelProperty(value = {"汇缴订单明细", "收费开始时间"}, index = 8)
    private String startChargeTimeH;

    @ExcelProperty(value = {"汇缴订单明细", "收费结束时间"}, index = 9)
    private String endChargeTimeH;

    @ExcelProperty(value = {"汇缴订单明细", "企业基数"}, index = 10)
    private Double companyBaseH;

    @ExcelProperty(value = {"汇缴订单明细", "个人基数"}, index = 11)
    private Double employeeBaseH;

    @ExcelProperty(value = {"汇缴订单明细", "企业比例"}, index = 12)
    private Double companyRatioH;

    @ExcelProperty(value = {"汇缴订单明细", "个人比例"}, index = 13)
    private Double employeeRatioH;

    @ExcelProperty(value = {"汇缴订单明细", "企业附加金额"}, index = 14)
    private Double companySurchargeValueH;

    @ExcelProperty(value = {"汇缴订单明细", "个人附加金额"}, index = 15)
    private Double employeeSurchargeValueH;

    @ExcelProperty(value = {"汇缴订单明细", "总金额"}, index = 16)
    private Double sumH;

    @ExcelProperty(value = {"汇缴订单明细", "企业金额"}, index = 17)
    private Double companyMoneyH;

    @ExcelProperty(value = {"汇缴订单明细", "个人金额"}, index = 18)
    private Double employeeMoneyH;


    @ExcelProperty(value = {"补缴订单明细", "产品名称"}, index = 19)
    private String productNameB;

    @ExcelProperty(value = {"补缴订单明细", "收费开始时间"}, index = 20)
    private String startChargeTimeB;

    @ExcelProperty(value = {"补缴订单明细", "收费结束时间"}, index = 21)
    private String endChargeTimeB;

    @ExcelProperty(value = {"补缴订单明细", "企业基数"}, index = 22)
    private Double companyBaseB;

    @ExcelProperty(value = {"补缴订单明细", "个人基数"}, index = 23)
    private Double employeeBaseB;

    @ExcelProperty(value = {"补缴订单明细", "企业比例"}, index = 24)
    private Double companyRatioB;

    @ExcelProperty(value = {"补缴订单明细", "个人比例"}, index = 25)
    private Double employeeRatioB;

    @ExcelProperty(value = {"补缴订单明细", "企业附加金额"}, index = 26)
    private Double companySurchargeValueB;

    @ExcelProperty(value = {"补缴订单明细", "个人附加金额"}, index = 27)
    private Double employeeSurchargeValueB;

    @ExcelProperty(value = {"补缴订单明细", "总金额"}, index = 28)
    private Double sumB;

    @ExcelProperty(value = {"补缴订单明细", "企业金额"}, index = 29)
    private Double companyMoneyB;

    @ExcelProperty(value = {"补缴订单明细", "个人金额"}, index = 30)
    private Double employeeMoneyB;

}
