package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.ChangeValue;
import com.authine.cloudpivot.web.api.entity.PurchaseContractInfo;
import com.authine.cloudpivot.web.api.entity.SalesContractInfo;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.params.ContractChangeInfo;
import com.authine.cloudpivot.web.api.service.ContractChangeService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName ContractChangeController
 * @Author:lfh
 * @Date:2020/4/11 13:49
 * @Description: 合同变更接口控制层
 **/
@RestController
@RequestMapping("/controller/contractChange")
@Slf4j
public class ContractChangeController extends BaseController {

    @Autowired
    private ContractChangeService contractChangeService;
    @RequestMapping("/contractUpdate")
    public ResponseResult<Object> contractUpdate(@RequestBody   ContractChangeInfo contractChangeInfo ) throws IllegalAccessException {
        if (contractChangeInfo == null || "".equals(contractChangeInfo)){
            return this.getOkResponseResult("error", "未传入变更数据");
        }
        List<ChangeValue> data = new ArrayList<>();
       if ("销售合同".equals(contractChangeInfo.getChange_type())) {
             String id = contractChangeInfo.getSales_contract();
            //查询销售合同数据
            SalesContractInfo beforeSalesContractInfo = contractChangeService.findSalesContractInfo(id);
           if (beforeSalesContractInfo.getX_quantity() == null){
               beforeSalesContractInfo.setX_quantity(0);
           }
           if (beforeSalesContractInfo.getX_unit_price() == null){
               beforeSalesContractInfo.setX_unit_price(0D);
           }
           if (beforeSalesContractInfo.getX_total_amount() == null){
               beforeSalesContractInfo.setX_total_amount(0D);
           }
            checkUnit(beforeSalesContractInfo.getX_sales_person(), JSON.toJSONString(contractChangeInfo.getX_sales_person()), "销售人员", contractChangeInfo.getParentId(),3, data);
            checkUnit(beforeSalesContractInfo.getX_salesman(), JSON.toJSONString(contractChangeInfo.getX_sales_person()), "业务员", contractChangeInfo.getParentId(), 3,data);
            checkString(beforeSalesContractInfo.getX_contract_num(), contractChangeInfo.getX_contract_num(), "合同编码", contractChangeInfo.getParentId(), data);
            //checkUnit(beforeSalesContractInfo.getX_client_name(), contractChangeInfo.getX_client_name(), "客户名称", parentId,1, data);
           if (!beforeSalesContractInfo.getX_client_name().equals(contractChangeInfo.getX_client_name())){
               String beforeClientName = contractChangeService.findClientName(beforeSalesContractInfo.getX_client_name());
               String afterClientName= contractChangeService.findClientName(contractChangeInfo.getX_client_name());
               data.add(getChangeData("客户名称",beforeClientName ,afterClientName , contractChangeInfo.getParentId()));
           }
            checkDouble(beforeSalesContractInfo.getX_unit_price(), contractChangeInfo.getX_unit_price(), "单价", contractChangeInfo.getParentId(), data);
            checkInteger(beforeSalesContractInfo.getX_quantity(), contractChangeInfo.getX_quantity(), "数量", contractChangeInfo.getParentId(), data);
            checkDouble(beforeSalesContractInfo.getX_total_amount(), contractChangeInfo.getX_total_amount(), "金额", contractChangeInfo.getParentId(), data);
            checkDate(beforeSalesContractInfo.getX_contract_signing_date(), contractChangeInfo.getX_contract_signing_date(), "合同签订日", contractChangeInfo.getParentId(), data);
            checkDate(beforeSalesContractInfo.getX_contract_expiry_date(), contractChangeInfo.getX_contract_expiry_date(), "合同到期日", contractChangeInfo.getParentId(), data);
            checkDate(beforeSalesContractInfo.getX_contract_renewal_date(), contractChangeInfo.getX_contract_renewal_date(), "续签合同签订日", contractChangeInfo.getParentId(), data);
            checkDate(beforeSalesContractInfo.getX_renewal_contract_end_date(), contractChangeInfo.getX_renewal_contract_end_date(), "续签合同到期日", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_agreed_repayment_date(), contractChangeInfo.getX_agreed_repayment_date(), "约定回款日", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_bill_type(), contractChangeInfo.getX_bill_type(), "账单类型", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_bill_cycle(), contractChangeInfo.getX_bill_cycle(), "账单周期", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_bill_day(), contractChangeInfo.getX_bill_day(), "账单日", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_fees_invoic(), contractChangeInfo.getX_fees_invoic(), "费用及开票形式", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_remark(), contractChangeInfo.getX_remark(), "备注", contractChangeInfo.getParentId(), data);
            checkString(beforeSalesContractInfo.getX_whether_end(),contractChangeInfo.getX_whether_end() ,"是否终止" ,contractChangeInfo.getParentId() , data);
        }
        if ("采购合同".equals(contractChangeInfo.getChange_type())) {
            String id = contractChangeInfo.getPurchase_contract();
            PurchaseContractInfo beforePurchaseContractInfo = contractChangeService.findPurchaseInfo(id);

            if (beforePurchaseContractInfo.getC_quantity() == null){
                beforePurchaseContractInfo.setC_quantity(0);
            }
            if (beforePurchaseContractInfo.getC_unit_price() == null){
                beforePurchaseContractInfo.setC_unit_price(0D);
            }
            if (beforePurchaseContractInfo.getC_total_amount() == null){
                beforePurchaseContractInfo.setC_total_amount(0D);
            }
            checkUnit(beforePurchaseContractInfo.getBuyer() ,JSON.toJSONString(contractChangeInfo.getBuyer()) ,"采购员" ,contractChangeInfo.getParentId(),3,data );
            checkString(beforePurchaseContractInfo.getC_contract_num(), contractChangeInfo.getC_contract_num(), "合同编码", contractChangeInfo.getParentId(), data);
            // checkUnit(beforePurchaseContractInfo.getC_supplier_name(),contractChangeInfo.getC_contract_num() ,"供应商名称" , parentId,1,data );
            if (!beforePurchaseContractInfo.getC_supplier_name().equals(contractChangeInfo.getC_supplier_name())){
                String beforeSupplierName = contractChangeService.findSupplierName(beforePurchaseContractInfo.getC_supplier_name());
                String afterSupplierName = contractChangeService.findSupplierName(contractChangeInfo.getC_supplier_name());
                data.add(getChangeData("供应商名称",beforeSupplierName ,afterSupplierName , contractChangeInfo.getParentId()));
            }
            checkString(beforePurchaseContractInfo.getC_business_type(),contractChangeInfo.getC_business_type(), "业务类型",contractChangeInfo.getParentId() ,data  );
            checkString(beforePurchaseContractInfo.getC_product_service(),contractChangeInfo.getC_product_service() ,"产品服务",contractChangeInfo.getParentId() ,data );
            checkDouble(beforePurchaseContractInfo.getC_unit_price(), contractChangeInfo.getC_unit_price(), "单价", contractChangeInfo.getParentId(), data);
            checkInteger(beforePurchaseContractInfo.getC_quantity(), contractChangeInfo.getC_quantity(),"数量" , contractChangeInfo.getParentId(), data);
            checkDouble(beforePurchaseContractInfo.getC_total_amount(),contractChangeInfo.getC_total_amount() ,"总额" , contractChangeInfo.getParentId(),data );
            checkDate(beforePurchaseContractInfo.getC_contract_signing_date(), contractChangeInfo.getC_contract_signing_date(), "合同签订日",contractChangeInfo.getParentId() ,data );
            checkDate(beforePurchaseContractInfo.getC_contract_expiry_date(), contractChangeInfo.getC_contract_expiry_date() ,"合同到期日",contractChangeInfo.getParentId() ,data );
            checkDate(beforePurchaseContractInfo.getC_contract_renewal_date(), contractChangeInfo.getC_contract_renewal_date() ,"续签合同签订日" ,contractChangeInfo.getParentId() ,data );
            checkDate(beforePurchaseContractInfo.getC_renewal_contract_end_date(), contractChangeInfo.getC_renewal_contract_end_date() ,"续签合同到期日" ,contractChangeInfo.getParentId() ,data );
            checkString(beforePurchaseContractInfo.getC_remark(),contractChangeInfo.getC_remark() , "备注",contractChangeInfo.getParentId() , data);
            checkString(beforePurchaseContractInfo.getC_whether_end(),contractChangeInfo.getC_whether_end() ,"是否终止" ,contractChangeInfo.getParentId() ,data );

        }
        if (data.size() > 0) {
            contractChangeService.updateContractChangeDetails(data);
        }
        return this.getOkResponseResult("success", "变更合同信息成功");
    }

    @GetMapping("/buyOrSaleContractInfo")
    public ResponseResult<Object> buyOrSaleContractInfo(@RequestParam("id") String id,@RequestParam("formStatus") String formStatus){
        if (id == null){
            return this.getOkResponseResult("error", "请选择变更合同");
        }
        if (formStatus.equals("buy")){
            PurchaseContractInfo purchaseInfo = contractChangeService.findPurchaseInfo(id);
            return this.getOkResponseResult(purchaseInfo, "返回采购合同信息");
        } else if (formStatus.equals("sale")) {
            SalesContractInfo salesContractInfo = contractChangeService.findSalesContractInfo(id);
            return this.getOkResponseResult(salesContractInfo, "返回销售合同信息");
        }
        return this.getErrResponseResult("error",404L,"无合同信息");
    }

    /**
     * 检查用户，部门是否修改
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param type         部门，用户类型
     * @param data         数据
     */
    private void checkUnit(String beforeModify, String nextModify, String field, String parentId, Integer type, List<ChangeValue> data) {
        Unit before = null;
        Unit next = null;
        OrganizationFacade organizationFacade = this.getOrganizationFacade();
        if (null == beforeModify && null == nextModify){
            return;
        }
        if (beforeModify.equals(nextModify)) {
            // 无变化
            return;
        }

        if ((!StringUtil.isEmpty(beforeModify) && StringUtil.isEmpty(nextModify)) || (StringUtil.isEmpty(beforeModify) && !StringUtil.isEmpty(nextModify))) {
            // 存在不同
            if (!StringUtil.isEmpty(beforeModify)) {
                before = ((List<Unit>) JSON.parse(beforeModify)).get(0);
            }
            if (!StringUtil.isEmpty(nextModify)) {
                next = ((List<Unit>) JSON.parse(nextModify)).get(0);
            }
            if (null == before) {
                // 旧值为空
                if (type == 1) {
                    // 部门
                    DepartmentModel departmentModel = organizationFacade.getDepartment(next.getId());
                    data.add(getChangeData(field, null, departmentModel.getName(), parentId));
                } else {
                    // 人员
                    UserModel userModel = organizationFacade.getUser(next.getId());
                    data.add(getChangeData(field, null, userModel.getName(), parentId));
                }
            } else {
                // 旧值为空
                if (type == 1) {
                    // 部门
                    DepartmentModel departmentModel = organizationFacade.getDepartment(before.getId());
                    data.add(getChangeData(field, null, departmentModel.getName(), parentId));
                } else {
                    // 人员
                    UserModel userModel = organizationFacade.getUser(before.getId());
                    data.add(getChangeData(field, null, userModel.getName(), parentId));
                }
            }
        } else {
            Object parse = JSON.parse(beforeModify);
            // 旧值为空

            before = JSON.parseObject(beforeModify, new TypeReference<ArrayList<Unit>>() {}).get(0);
            next =JSON.parseObject(nextModify, new TypeReference<ArrayList<Unit>>() {}).get(0);
            if (type == 1) {
                // 部门
                DepartmentModel oldDepartment = organizationFacade.getDepartment(before.getId());
                DepartmentModel newDepartment = organizationFacade.getDepartment(next.getId());
                data.add(getChangeData(field, oldDepartment.getName(), newDepartment.getName(), parentId));
            } else {
                // 人员
                UserModel oldUser = organizationFacade.getUser(before.getId());
                UserModel newUser = organizationFacade.getUser(next.getId());
                data.add(getChangeData(field, oldUser.getName(), newUser.getName(), parentId));
            }
        }
    }

    /**
     * 检查字符串是否修改
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkString(String beforeModify, String nextModify, String field, String parentId, List<ChangeValue> data) {
        if (null == beforeModify && null == nextModify) {
            return;
        }
        if (null == beforeModify || null == nextModify) {
            data.add(getChangeData(field, beforeModify, nextModify, parentId));
        } else if (!beforeModify.equals(nextModify)) {
            data.add(getChangeData(field, beforeModify, nextModify, parentId));
        }
    }
    /**
     * 检查数字是否改变
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkDouble(Double beforeModify, Double nextModify, String field, String parentId, List<ChangeValue> data) {

        if (beforeModify == null && nextModify == null) {
            return;
        }

        if (beforeModify == null || nextModify == null) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        } else if (beforeModify.doubleValue() != nextModify.doubleValue()) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        }
    }

    private void checkInteger(Integer beforeModify, Integer nextModify, String field, String parentId, List<ChangeValue> data) {

        if (beforeModify == null && nextModify == null) {
            return;
        }

        if (beforeModify == null || nextModify == null) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        } else if (beforeModify.intValue() != nextModify.intValue()) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        }
    }

    /**
     * 检查日期是否改变
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkDate(Date beforeModify, Date nextModify, String field, String parentId, List<ChangeValue> data) {
        if (beforeModify == null && nextModify == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        // 存在修改
        if (beforeModify == null || nextModify == null) {
            if (beforeModify == null) {
                calendar.setTime(nextModify);
                String time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, null, time, parentId));
            } else {
                calendar.setTime(beforeModify);
                String time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, time, null, parentId));
            }
        } else {
            if (beforeModify.getYear() != nextModify.getYear() || beforeModify.getMonth() != nextModify.getMonth() || beforeModify.getDate() != nextModify.getDate()) {
                calendar.setTime(beforeModify);
                String time1 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                calendar.setTime(nextModify);
                String time2 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, time1, time2, parentId));
            }
        }
    }

    private ChangeValue getChangeData(String field, String oldValue, String newValue, String parentId) {
        ChangeValue data = new ChangeValue();
        data.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        data.setParentId(parentId);
        data.setUpdateField(field);
        data.setUpdateBeforeValue(oldValue);
        data.setUpdateAfterValue(newValue);
        return data;
    }


}
