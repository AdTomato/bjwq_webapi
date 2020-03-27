package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author liulei
 * @Description 支付申请实体
 * @ClassName com.authine.cloudpivot.web.api.entity.PaymentApplication
 * @Date 2020/3/20 14:09
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApplication extends BaseEntity {
    /**
     * 支付类型(一对一，多对一)
     */
    String paymentType;
    /**
     * 委托单位
     */
    String entrustUnit;
    /**
     * 投保地
     */
    String insuredArea;
    /**
     * 账单年月
     */
    String billYear;
    /**
     * 支付供应商名称
     */
    String supplierId;
    /**
     * 公司税号
     */
    String companyTaxNumber;
    /**
     * 支付账号
     */
    String bankAccount;
    /**
     * 开户行
     */
    String openingBank;
    /**
     * 开户地
     */
    String openingPlace;
    /**
     * 供应商分类（外部，内部）
     */
    String supplierType;
    /**
     * 供应商代码
     */
    String supplierCode;
    /**
     * 备注
     */
    String remarks;
    /**
     * 支付总金额
     */
    Double paidAmountTotal;
    /**
     * 社保总额
     */
    Double socialSecurityTotal;
    /**
     * 公积金总额
     */
    Double accumulationFundTotal;
    /**
     * 工资总额
     */
    Double wagesTotal;
    /**
     * 福利产品总额
     */
    Double welfareProductsTotal;
    /**
     * 服务费总额
     */
    Double serviceFeeTotal;
    /**
     * 增值税总额
     */
    Double addedTaxTotal;
    /**
     * 代付合计
     */
    Double paidInLieuTotal;
    /**
     * 成本合计
     */
    Double costTotal;
    /**
     * 凭证号
     */
    String voucherNumber;
    /**
     * 是否生成凭证（是，否）
     */
    String createVoucher;
    /**
     * 审批时间
     */
    Date approvalTime;

    /**
     * 数据类型，判断是社保或公积金
     * */
    String dataType;

    String sourceId;

    public PaymentApplication(List <PaymentApplication> pas, Supplier supplier, UserModel user,
                              DepartmentModel dept) {
        /*==================== 基础数据:start ====================*/
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = user.getName();
        this.creater = user.getId();
        this.createdDeptId = dept.getId();
        this.owner = user.getId();
        this.ownerDeptId = dept.getId();
        this.createdTime = new Date();
        this.modifier = user.getId();
        this.modifiedTime = new Date();
        this.sequenceStatus = Constants.DRAFT_STATUS;
        this.ownerDeptQueryCode = dept.getQueryCode();
        /*==================== 基础数据:end =====================*/

        /*================= 供应商数据:start =================*/
        this.supplierId = supplier.getId();
        this.companyTaxNumber = supplier.getCompanyTaxNumber();
        this.bankAccount = supplier.getBankAccount();
        this.openingBank = supplier.getOpeningBank();
        this.openingPlace = supplier.getOpeningPlace();
        this.supplierType = supplier.getSupplierType();
        this.supplierCode = supplier.getSupplierCode();
        /*================= 供应商数据:end ==================*/

        /*========================================== 金额数据:start ==========================================*/
        Double paidAmountTotal = 0.0, socialSecurityTotal = 0.0, accumulationFundTotal = 0.0, wagesTotal = 0.0,
                welfareProductsTotal = 0.0, serviceFeeTotal = 0.0, addedTaxTotal = 0.0, paidInLieuTotal = 0.0,
                costTotal = 0.0;
        String dataType = "";
        for (int i = 0; i < pas.size(); i++) {
            PaymentApplication pa = pas.get(i);
            // 数据处理
            if (StringUtils.isBlank(dataType)) {
                dataType = pa.getDataType();
            } else if (!"社保公积金".equals(dataType) && dataType.equals(pa.getDataType())) {
                dataType = "社保公积金";
            }

            paidAmountTotal += pa.getPaidAmountTotal() == null ? 0.0 : pa.getPaidAmountTotal();
            socialSecurityTotal += pa.getSocialSecurityTotal() == null ? 0.0 : pa.getSocialSecurityTotal();
            accumulationFundTotal += pa.getAccumulationFundTotal() == null ? 0.0 : pa.getAccumulationFundTotal();
            wagesTotal += pa.getWagesTotal() == null ? 0.0 : pa.getWagesTotal();
            welfareProductsTotal += pa.getWelfareProductsTotal() == null ? 0.0 : pa.getWelfareProductsTotal();
            serviceFeeTotal += pa.getServiceFeeTotal() == null ? 0.0 : pa.getServiceFeeTotal();
            addedTaxTotal += pa.getAddedTaxTotal() == null ? 0.0 : pa.getAddedTaxTotal();
            paidInLieuTotal += pa.getPaidInLieuTotal() == null ? 0.0 : pa.getPaidInLieuTotal();
            costTotal += pa.getCostTotal() == null ? 0.0 : pa.getCostTotal();
        }

        this.paidAmountTotal = paidAmountTotal;
        this.socialSecurityTotal = socialSecurityTotal;
        this.accumulationFundTotal = accumulationFundTotal;
        this.wagesTotal = wagesTotal;
        this.welfareProductsTotal = welfareProductsTotal;
        this.serviceFeeTotal = serviceFeeTotal;
        this.addedTaxTotal = addedTaxTotal;
        this.paidInLieuTotal = paidInLieuTotal;
        this.costTotal = costTotal;
        /*========================================== 金额数据:end ===========================================*/

        /*=================== 表单其他信息:start ==================*/
        this.paymentType = "多对一";
        if (StringUtils.isNotBlank(pas.get(0).getEntrustUnit())) {
            // 委托单位不为空，此时均是省外数据，委托单位一致
            this.entrustUnit = pas.get(0).getEntrustUnit();
        }
        if (StringUtils.isNotBlank(pas.get(0).getInsuredArea())) {
            // 投保地不为空，此时均是省内数据，投保地一致
            this.insuredArea = pas.get(0).getInsuredArea();
        }
        this.billYear = pas.get(0).getBillYear();
        this.dataType = dataType;
        /*=================== 表单其他信息:end ===================*/
    }
}
