package com.authine.cloudpivot.web.api.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.authine.cloudpivot.web.api.dto.SocialSecurityDeclareDto;
import com.authine.cloudpivot.web.api.entity.OrderDetails;
import com.authine.cloudpivot.web.api.excel.data.SocialSecurityDeclareData;
import com.authine.cloudpivot.web.api.service.EmployeeOrderFormService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangyong
 * @time 2020/6/18 15:39
 */
public class SocialSecurityDeclareDataListener extends AnalysisEventListener<SocialSecurityDeclareData> {

    @Autowired
    EmployeeOrderFormService employeeOrderFormService;

    List<SocialSecurityDeclareData> list = new ArrayList<>();
    List<SocialSecurityDeclareDto> updateList = new ArrayList<>();
    List<SocialSecurityDeclareDto> deleteList = new ArrayList<>();

    private static final int MAX_NUM = 50;

    @SneakyThrows
    @Override
    public void invoke(SocialSecurityDeclareData data, AnalysisContext context) {
        if (!list.isEmpty() && !StringUtils.isEmpty(data.getId())) {
            SocialSecurityDeclareDto socialSecurityDeclareDto = new SocialSecurityDeclareDto();
            socialSecurityDeclareDto.setOrderDetailsRemittanceSbList(new ArrayList<>());
            socialSecurityDeclareDto.setOrderDetailsPayBackSbList(new ArrayList<>());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Double i1 = 0D;
            Double i2 = 0D;
            for (SocialSecurityDeclareData excelData : list) {
                if (!StringUtils.isEmpty(excelData.getId())) {
                    socialSecurityDeclareDto.setId(excelData.getId());
                    socialSecurityDeclareDto.setReason(excelData.getReason());
                    socialSecurityDeclareDto.setOperatingType(excelData.getOperatingType());
                }

                if (!StringUtils.isEmpty(excelData.getProductNameH())) {
                    // 汇缴不为空
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setId(UUID.randomUUID().toString().replace("-", ""));
                    orderDetails.setSortKey(i1);
                    orderDetails.setProductName(excelData.getProductNameH());
                    orderDetails.setStartChargeTime(format.parse(excelData.getStartChargeTimeH() + " 00:00:00"));
                    orderDetails.setEndChargeTime(format.parse(excelData.getEndChargeTimeH() + " 00:00:00"));
                    orderDetails.setCompanyBase(excelData.getCompanyBaseH());
                    orderDetails.setEmployeeBase(excelData.getEmployeeBaseH());
                    orderDetails.setCompanyRatio(excelData.getCompanyRatioH());
                    orderDetails.setEmployeeRatio(excelData.getEmployeeRatioH());
                    orderDetails.setCompanyMoney(excelData.getCompanyMoneyH());
                    orderDetails.setEmployeeMoney(excelData.getEmployeeMoneyH());
                    orderDetails.setCompanySurchargeValue(excelData.getCompanySurchargeValueH());
                    orderDetails.setEmployeeSurchargeValue(excelData.getEmployeeSurchargeValueH());
                    socialSecurityDeclareDto.getOrderDetailsRemittanceSbList().add(orderDetails);
                    i1++;
                }

                if (!StringUtils.isEmpty(excelData.getProductNameB())) {
                    // 补缴不为空
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setProductName(excelData.getProductNameB());
                    orderDetails.setId(UUID.randomUUID().toString().replace("-", ""));
                    orderDetails.setSortKey(i2);
                    orderDetails.setStartChargeTime(format.parse(excelData.getStartChargeTimeB() + " 00:00:00"));
                    orderDetails.setEndChargeTime(format.parse(excelData.getEndChargeTimeB() + " 00:00:00"));
                    orderDetails.setCompanyBase(excelData.getCompanyBaseB());
                    orderDetails.setEmployeeBase(excelData.getEmployeeBaseB());
                    orderDetails.setCompanyRatio(excelData.getCompanyRatioB());
                    orderDetails.setEmployeeRatio(excelData.getEmployeeRatioB());
                    orderDetails.setCompanyMoney(excelData.getCompanyMoneyB());
                    orderDetails.setEmployeeMoney(excelData.getEmployeeMoneyB());
                    orderDetails.setCompanySurchargeValue(excelData.getCompanySurchargeValueB());
                    orderDetails.setEmployeeSurchargeValue(excelData.getEmployeeSurchargeValueB());
                    socialSecurityDeclareDto.getOrderDetailsPayBackSbList().add(orderDetails);
                    i2++;
                }
            }
            if ("更新".equals(socialSecurityDeclareDto.getOperatingType())) {
                // 更新操作
                updateList.add(socialSecurityDeclareDto);
            }

            if ("驳回".equals(socialSecurityDeclareDto.getOperatingType())) {
                // 驳回操作
                deleteList.add(socialSecurityDeclareDto);
            }

            if (updateList.size() >= MAX_NUM) {
                // 更新
                update();
            }
            if (deleteList.size() >= MAX_NUM) {
                // 删除
                delete();
            }
            list.clear();
        }
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!updateList.isEmpty()) {
            update();
        }
        if (!deleteList.isEmpty()) {
            delete();
        }
    }

    private void update() {
        Map<String, SocialSecurityDeclareDto> data = new HashMap<>();
        // 社保id集合
        List<String> declareIds = new ArrayList<>();
        // 订单id集合
        List<String> orderIds = new ArrayList<>();
        for (SocialSecurityDeclareDto dto : updateList) {
            data.put(dto.getId(), dto);
            declareIds.add(dto.getId());
        }
        List<Map<String, String>> orderFormIdBySocialSecurityDeclareId = employeeOrderFormService.getOrderFormIdBySocialSecurityDeclareId(declareIds);
        for (Map<String, String> orderFormSecurityDeclareId : orderFormIdBySocialSecurityDeclareId) {
            SocialSecurityDeclareDto socialSecurityDeclareDto = data.get(orderFormSecurityDeclareId.get("securityDeclareId"));
            if (socialSecurityDeclareDto != null) {
                socialSecurityDeclareDto.setEmployeeOrderFormId(orderFormSecurityDeclareId.get("orderFormId"));
            }
            orderIds.add(orderFormSecurityDeclareId.get("orderFormId"));
        }

        // 删除社保申报补缴数据
        employeeOrderFormService.deleteOrderDetailsPayBackSbsByParentId(declareIds);
        // 删除社保申报汇缴数据
        employeeOrderFormService.deleteOrderDetailsRemittanceSbsByParentId(declareIds);
        // 删除员工订单汇缴数据
        employeeOrderFormService.deleteOrderDetailsRemittancesById(orderIds);
        // 删除员工订单补缴数据
        employeeOrderFormService.deleteOrderDetailsPayBacksById(orderIds);


    }

    private void delete() {

    }
}
