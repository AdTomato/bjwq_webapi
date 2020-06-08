package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.UpdateEmployeesMapper;
import com.authine.cloudpivot.web.api.service.UpdateEmployeeService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description 增减员变更 Service实现
 * @ClassName com.authine.cloudpivot.web.api.service.impl.UpdateEmployeeServiceImpl
 * @Date 2020/3/13 16:28
 **/
@Service
public class UpdateEmployeeServiceImpl implements UpdateEmployeeService {
    @Resource
    private UpdateEmployeesMapper updateEmployeeMapper;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    @Override
    public AddEmployee getAddEmployeeUpdateById(String id) throws Exception {
        AddEmployee updateAddEmployee = updateEmployeeMapper.getAddEmployeeUpdateById(id);
        if (updateAddEmployee == null) {
            throw new RuntimeException("没有获取到增员变更数据！");
        }
        // 判断是否是六安，是基数四舍五入取整
        updateAddEmployee = CommonUtils.needBaseRounding(updateAddEmployee);
        return updateAddEmployee;
    }

    @Override
    public ShAddEmployee getShAddEmployeeUpdateById(String id) throws Exception {
        ShAddEmployee updateAddEmployee = updateEmployeeMapper.getShAddEmployeeUpdateById(id);
        if (updateAddEmployee == null) {
            throw new RuntimeException("没有获取到增员变更数据！");
        }
        return updateAddEmployee;
    }

    @Override
    public NationwideDispatch getQgAddEmployeeUpdateById(String id) throws Exception {
        NationwideDispatch updateAddEmployee = updateEmployeeMapper.getQgAddEmployeeUpdateById(id);
        if (updateAddEmployee == null) {
            throw new RuntimeException("没有获取到增员变更数据！");
        }
        return updateAddEmployee;
    }

    @Override
    public DeleteEmployee getDeleteEmployeeUpdateById(String id) throws Exception {
        DeleteEmployee updateDelEmployee = updateEmployeeMapper.getDeleteEmployeeUpdateById(id);
        if (updateDelEmployee == null) {
            throw new RuntimeException("没有获取到减员变更数据！");
        }
        return updateDelEmployee;
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeUpdateById(String id) throws Exception {
        ShDeleteEmployee updateDelEmployee = updateEmployeeMapper.getShDeleteEmployeeUpdateById(id);
        if (updateDelEmployee == null) {
            throw new RuntimeException("没有获取到减员变更数据！");
        }
        return updateDelEmployee;
    }

    @Override
    public NationwideDispatch getQgDeleteEmployeeUpdateById(String id) throws Exception {
        NationwideDispatch updateDelEmployee = updateEmployeeMapper.getQgDeleteEmployeeUpdateById(id);
        if (updateDelEmployee == null) {
            throw new RuntimeException("没有获取到减员变更数据！");
        }
        return updateDelEmployee;
    }

    @Override
    public EmployeeFiles getEmployeeFilesUpdateById(String id) throws Exception {
        EmployeeFiles updateEmployeeFiles = updateEmployeeMapper.getEmployeeFilesUpdateById(id);
        if (updateEmployeeFiles == null) {
            throw new RuntimeException("没有获取到员工档案变更数据！");
        }
        return updateEmployeeFiles;
    }

    @Override
    public void upateEmployeeOrderForm(EmployeeOrderForm orderForm) throws Exception {
        addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
    }

    @Override
    public void delEmployeeOrderFormDetails(String parentId, String type) throws Exception {
        updateEmployeeMapper.delEmployeeOrderFormDetails(parentId, type, "i4fvb_order_details_pay_back");
        updateEmployeeMapper.delEmployeeOrderFormDetails(parentId, type, "i4fvb_order_details_remittance");
    }

    @Override
    public void addEmployeeOrderFormDetails(String parentId, List <EmployeeOrderFormDetails> payBackList,
                                            List <EmployeeOrderFormDetails> remittanceList) throws Exception {
        if (payBackList != null && payBackList.size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(payBackList, parentId, "i4fvb_order_details_pay_back");
        }
        if (remittanceList != null && remittanceList.size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(remittanceList, parentId, "i4fvb_order_details_remittance");
        }
    }

    @Override
    public void addEmployeeOrderFormDetails(ServiceChargeUnitPrice price, String orderFormId) throws Exception {
        createEmployeeOrderFormDetails(price, orderFormId, "i4fvb_order_details_pay_back");
        createEmployeeOrderFormDetails(price, orderFormId, "i4fvb_order_details_remittance");
    }

    private void createEmployeeOrderFormDetails(ServiceChargeUnitPrice price, String orderFormId, String tableName) throws Exception {
        // 固定值数据
        List<EmployeeOrderFormDetails> details = new ArrayList <>();
        // 查询补缴数据开始时间，结束时间，最大sortKey
        List <Integer> startChargeTimes = updateEmployeeMapper.getStartChargeTime(tableName, orderFormId);
        if (startChargeTimes == null || startChargeTimes.size() == 0) {
            return;
        }
        List <Integer> endChargeTimes = updateEmployeeMapper.getEndChargeTime(tableName, orderFormId);
        if (endChargeTimes == null || endChargeTimes.size() == 0) {
            return;
        }
        Double sortKey = updateEmployeeMapper.getMaxSortKey(tableName, orderFormId);
        if (sortKey == null) {
            sortKey = 0d;
        }

        Date minStartChargeTime = startChargeTimes.get(0) == null ? null : sdf.parse(String.valueOf(startChargeTimes.get(0)));
        Date maxEndChargeTime = endChargeTimes.get(endChargeTimes.size() -1) == null ? null : sdf.parse(String.valueOf(endChargeTimes.get(endChargeTimes.size() -1)));
        if (price.getServiceChargeUnitPrice() - 0d > 0d) {
            // 服务费
            sortKey += 1;
            EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "服务费",
                    minStartChargeTime, maxEndChargeTime, price.getServiceChargeUnitPrice());
            if ("i4fvb_order_details_pay_back".equals(tableName)) {
                price.setServiceMaxPayBack(endChargeTimes.get(endChargeTimes.size() -1) == null ? null : endChargeTimes.get(endChargeTimes.size() -1));
            } else if (startChargeTimes.get(0) == null ? null : startChargeTimes.get(0) <= price.getServiceMaxPayBack()){
                detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getServiceMaxPayBack()))));
            }
            details.add(detail);
        }
        if (price.getTotalWelfareProducts() - 0d > 0d) {
            // 福利产品
            sortKey += 1;
            EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "福利产品",
                    minStartChargeTime, maxEndChargeTime, price.getTotalWelfareProducts());
            if ("i4fvb_order_details_pay_back".equals(tableName)) {
                price.setWelfareMaxPayBack(endChargeTimes.get(endChargeTimes.size() -1) == null ? null : endChargeTimes.get(endChargeTimes.size() -1));
            } else if (startChargeTimes.get(0) == null ? null : startChargeTimes.get(0) <= price.getWelfareMaxPayBack()){
                detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getWelfareMaxPayBack()))));
            }
            details.add(detail);
        }
        Double riskManagementFee = price.getRiskManagementFee();
        if (riskManagementFee - 0d > 0d && riskManagementFee.intValue() < 1) {
            // 风险管理费有值 && 风险管理费<1
            List<StartAndEndTime> timeRanges = getTimeRanges(startChargeTimes, endChargeTimes);
            if (timeRanges != null && timeRanges.size() > 0) {
                for (int i = 0; i < timeRanges.size(); i++) {
                    StartAndEndTime timeRange = timeRanges.get(i);
                    String start = String.valueOf(timeRange.getStartChargeTime());
                    String end = null;
                    if (timeRange.getEndChargeTime() != Integer.MAX_VALUE && timeRange.getEndChargeTime() != null) {
                        end = String.valueOf(timeRange.getEndChargeTime());
                    }
                    Double total = updateEmployeeMapper.getSumInTimeRange(tableName, start, end, orderFormId);
                    if (total == null || total == 0d) {
                        continue;
                    }
                    sortKey += 1;
                    EmployeeOrderFormDetails riskManagementFeeDetail = new EmployeeOrderFormDetails(orderFormId,
                            sortKey, "风险管理费", sdf.parse(start), end == null ? null : sdf.parse(end),
                            riskManagementFee * total);
                    details.add(riskManagementFeeDetail);

                    Double vatTaxes = price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee*total);
                    if (vatTaxes - 0d > 0d) {
                        sortKey += 1;
                        EmployeeOrderFormDetails vatTaxesDetails = new EmployeeOrderFormDetails(orderFormId, sortKey,
                                "增值税费", sdf.parse(start), end == null ? null : sdf.parse(end), vatTaxes);
                        details.add(vatTaxesDetails);
                    }
                }
            }
        } else {
            if (riskManagementFee - 0d > 0d) {
                // 风险管理费有固定值
                sortKey += 1;
                EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "风险管理费",
                        minStartChargeTime, maxEndChargeTime, riskManagementFee);
                if ("i4fvb_order_details_pay_back".equals(tableName)) {
                    price.setRiskMaxPayBack(endChargeTimes.get(endChargeTimes.size() -1) == null ? null : endChargeTimes.get(endChargeTimes.size() -1));
                } else if (startChargeTimes.get(0) == null ? null : startChargeTimes.get(0) <= price.getRiskMaxPayBack()){
                    detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getRiskMaxPayBack()))));
                }
                details.add(detail);
            }
            Double vatTaxes = price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee);
            if (vatTaxes - 0d > 0d) {
                sortKey += 1;
                EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "增值税费",
                        minStartChargeTime, maxEndChargeTime, vatTaxes);
                if ("i4fvb_order_details_pay_back".equals(tableName)) {
                    price.setVatMaxPayBack(endChargeTimes.get(endChargeTimes.size() -1) == null ? null : endChargeTimes.get(endChargeTimes.size() -1));
                } else if (startChargeTimes.get(0) == null ? null : startChargeTimes.get(0) <= price.getVatMaxPayBack()){
                    detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getVatMaxPayBack()))));
                }

                details.add(detail);
            }
        }
        if (details != null && details.size() > 0) {
            updateEmployeeMapper.addEmployeeOrderFormDetails(details, tableName);
        }
    }

    @Override
    public void updateSocialSecurityDeclare(SocialSecurityDeclare newSDeclare, SocialSecurityDeclare sDeclare) throws Exception {
        newSDeclare.setId(sDeclare.getId());
        updateEmployeeMapper.updateSocialSecurityDeclare(newSDeclare);
        updateEmployeeMapper.delEmployeeOrderFormDetails(sDeclare.getId(), "3", "i4fvb_order_details_pay_back_sb");
        updateEmployeeMapper.delEmployeeOrderFormDetails(sDeclare.getId(), "3", "i4fvb_order_details_remittance_sb");
        if (newSDeclare.getPayBackList() != null && newSDeclare.getPayBackList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(newSDeclare.getPayBackList(), newSDeclare.getId(),
                    "i4fvb_order_details_pay_back_sb");
        }
        if (newSDeclare.getRemittanceList() != null && newSDeclare.getRemittanceList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(newSDeclare.getRemittanceList(), newSDeclare.getId(),
                    "i4fvb_order_details_remittance_sb");
        }
    }

    @Override
    public void updateProvidentFundDeclare(ProvidentFundDeclare newPDeclare, ProvidentFundDeclare pDeclare) throws Exception {
        newPDeclare.setId(pDeclare.getId());
        updateEmployeeMapper.updateProvidentFundDeclare(newPDeclare);
        updateEmployeeMapper.delEmployeeOrderFormDetails(pDeclare.getId(), "3", "i4fvb_order_details_pay_back_gjj");
        updateEmployeeMapper.delEmployeeOrderFormDetails(pDeclare.getId(), "3", "i4fvb_order_details_remittance_gjj");
        if (newPDeclare.getPayBackList() != null && newPDeclare.getPayBackList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(newPDeclare.getPayBackList(), newPDeclare.getId(),
                    "i4fvb_order_details_pay_back_gjj");
        }
        if (newPDeclare.getRemittanceList() != null && newPDeclare.getRemittanceList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(newPDeclare.getRemittanceList(), newPDeclare.getId(),
                    "i4fvb_order_details_remittance_gjj");
        }
    }

    @Override
    public void updateDeleteEmployee(DeleteEmployee del) throws Exception {
        updateEmployeeMapper.updateDeleteEmployee(del);
    }

    @Override
    public void updateShDeleteEmployee(ShDeleteEmployee del) throws Exception {
        updateEmployeeMapper.updateShDeleteEmployee(del);
    }

    @Override
    public void updateQgDeleteEmployee(NationwideDispatch del) throws Exception {
        updateEmployeeMapper.updateQgDeleteEmployee(del);
    }

    @Override
    public EmployeeFiles getEmployeeFilesById(String id) throws Exception {
        EmployeeFiles updateEmployeeFiles = updateEmployeeMapper.getEmployeeFilesById(id);
        if (updateEmployeeFiles == null) {
            throw new RuntimeException("没有获取到员工档案变更数据！");
        }
        return updateEmployeeFiles;
    }

    @Override
    public void updateAddEmployee(AddEmployee add) throws Exception {
        updateEmployeeMapper.updateAddEmployee(add);
    }

    @Override
    public void updateShAddEmployee(ShAddEmployee add) throws Exception {
        updateEmployeeMapper.updateShAddEmployee(add);
    }

    @Override
    public void updateQgAddEmployee(NationwideDispatch add) throws Exception {
        updateEmployeeMapper.updateQgAddEmployee(add);
    }

    @Override
    public void updateSocialSecurityClose(SocialSecurityClose close) throws Exception {
        updateEmployeeMapper.updateSocialSecurityClose (close);
    }

    @Override
    public void updateProvidentFundClose(ProvidentFundClose close) throws Exception {
        updateEmployeeMapper.updateProvidentFundClose(close);
    }

    private List<StartAndEndTime> getTimeRanges(List<Integer> startChargeTimes, List<Integer> endChargeTimes) throws Exception{
        List<StartAndEndTime> times = new ArrayList <>();
        if (startChargeTimes.size() == 1 && endChargeTimes.size()==1) {
            StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(0), endChargeTimes.get(0) == null ?
                    Integer.MAX_VALUE : endChargeTimes.get(0), null);
            times.add(time);
        } else if (startChargeTimes.size() == 1 && endChargeTimes.size()>1) {
            for (int i = 0; i < endChargeTimes.size(); i++) {
                if (i == 0) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(0), endChargeTimes.get(0), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(CommonUtils.getNextMonth(endChargeTimes.get(i - 1)),
                            endChargeTimes.get(i) == null ? Integer.MAX_VALUE : endChargeTimes.get(i), null);
                    times.add(time);
                }
            }
        } else if (startChargeTimes.size() > 0 && endChargeTimes.size()==1) {
            for (int i = 0; i < startChargeTimes.size(); i++) {
                if (i < startChargeTimes.size() -1) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i), CommonUtils.getLastMonth(startChargeTimes.get(i+1)), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i),
                            endChargeTimes.get(0) == null ? Integer.MAX_VALUE : endChargeTimes.get(0), null);
                    times.add(time);
                }
            }
        } else {
            for (int i = 0; i < startChargeTimes.size(); i++) {
                if (i < startChargeTimes.size() -1) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i), CommonUtils.getLastMonth(startChargeTimes.get(i+1)), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i),
                            endChargeTimes.get(endChargeTimes.size() - 1) == null ? Integer.MAX_VALUE :
                                    endChargeTimes.get(endChargeTimes.size() - 1), null);
                    times.add(time);
                }
            }
            for (int i = 0; i < endChargeTimes.size() -1; i++) {
                Integer endChargeTime = endChargeTimes.get(i);
                for (int j = 0; j < times.size(); j++) {
                    StartAndEndTime time = times.get(j);
                    if (endChargeTime > time.getEndChargeTime()) {
                        continue;
                    } else if (endChargeTime == time.getStartChargeTime() && endChargeTime == time.getEndChargeTime()) {
                        break;
                    } else if (endChargeTime >= time.getStartChargeTime() && endChargeTime <= time.getEndChargeTime()){
                        StartAndEndTime nextTime = new StartAndEndTime(CommonUtils.getNextMonth(endChargeTime), time.getEndChargeTime(), null);
                        times.get(j).setEndChargeTime(endChargeTime);
                        times.add(j+1, nextTime);

                    }
                }
            }
        }
        /*List <Map <String, Date>> timeRanges = new ArrayList <>();
        Map <String, Date> map = null;
        for (int i = 0; i < times.size(); i++) {
            StartAndEndTime time = times.get(i);
            map.put("startChargeTime", sdf.parse(String.valueOf(time.getStartChargeTime())));
            map.put("endChargeTime", time.getEndChargeTime() == Integer.MAX_VALUE ? null : sdf.parse(String.valueOf(time.getEndChargeTime())));
        }*/
        return times;
    }
}
