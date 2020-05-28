package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.UpdateEmployeesMapper;
import com.authine.cloudpivot.web.api.service.UpdateEmployeeService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
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
            details.add(detail);
        }
        if (price.getTotalWelfareProducts() - 0d > 0d) {
            // 福利产品
            sortKey += 1;
            EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "福利产品",
                    minStartChargeTime, maxEndChargeTime, price.getTotalWelfareProducts());
            details.add(detail);
        }
        Double riskManagementFee = price.getRiskManagementFee();
        if (riskManagementFee - 0d > 0d && riskManagementFee.intValue() < 1) {
            // 风险管理费有值 && 风险管理费<1
            List<Map <String, Date>> timeRanges = getTimeRanges(startChargeTimes, endChargeTimes);
            if (timeRanges != null && timeRanges.size() > 0) {
                for (int i = 0; i < timeRanges.size(); i++) {
                    Map <String, Date> timeRange = timeRanges.get(i);
                    String start = timeRange.get("startChargeTime") == null ? null : sdf.format(timeRange.get("startChargeTime"));
                    String end = timeRange.get("endChargeTime") == null ? null : sdf.format(timeRange.get("endChargeTime"));
                    Double total = updateEmployeeMapper.getSumInTimeRange(tableName, start, end, orderFormId);
                    if (total == null || total == 0d) {
                        continue;
                    }
                    sortKey += 1;
                    EmployeeOrderFormDetails riskManagementFeeDetail = new EmployeeOrderFormDetails(orderFormId, sortKey, "风险管理费",
                            timeRange.get("startChargeTime"), timeRange.get("endChargeTime"), riskManagementFee*total);
                    details.add(riskManagementFeeDetail);

                    Double vatTaxes = price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee*total);
                    if (vatTaxes - 0d > 0d) {
                        sortKey += 1;
                        EmployeeOrderFormDetails vatTaxesDetails = new EmployeeOrderFormDetails(orderFormId, sortKey, "增值税费",
                                timeRange.get("startChargeTime"), timeRange.get("endChargeTime"), vatTaxes);
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
                details.add(detail);
            }
            Double vatTaxes = price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee);
            if (vatTaxes - 0d > 0d) {
                sortKey += 1;
                EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "增值税费",
                        minStartChargeTime, maxEndChargeTime, vatTaxes);
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

    private List<Map<String, Date>> getTimeRanges(List<Integer> startChargeTimes, List<Integer> endChargeTimes) throws Exception{
        List<Map<String, Date>> timeRanges = new ArrayList <>();
        int j = 0;
        int min = 0;
        int max = 0;
        Integer startTime = startChargeTimes.get(0), endTime = 0;
        for (int i = 0; i < startChargeTimes.size(); i++) {
            if (endTime == null) {
                // endTime 是null,结束
                break;
            }
            if(endTime > 0){
                startTime = getStartTime(endTime);
            }
            min = startChargeTimes.get(i);
            if (startTime > min) {
                // 当前数据预设的开始时间 > 查询到的开始时间
                continue;
            }
            if (i + 1 < startChargeTimes.size()) {
                // 开始时间未遍历结束
                min = startChargeTimes.get(i + 1);
                max = endChargeTimes.get(j) == null ? Integer.MAX_VALUE : endChargeTimes.get(j);
                if (min < max) {
                    endTime = min;
                } else {
                    if (j < endChargeTimes.size() -1) {
                        j++;
                    }
                    endTime = max == Integer.MAX_VALUE ? null : max;
                }
            } else {
                max = endChargeTimes.get(j) == null ? Integer.MAX_VALUE : endChargeTimes.get(j);
                if (j < endChargeTimes.size() -1) {
                    j++;
                }
                endTime = max;
            }
            Map<String, Date> map = new HashMap <>();
            map.put("startChargeTime", sdf.parse(String.valueOf(startTime)));
            if (endTime < Integer.MAX_VALUE) {
                map.put("endChargeTime", sdf.parse(String.valueOf(endTime)));
            } else {
                endTime = null;
            }
            timeRanges.add(map);
        }
        if (j < endChargeTimes.size()) {
            for (int i = j; i < endChargeTimes.size(); i++) {
                if (endTime == null) {
                    // endTime 是null,结束
                    break;
                }
                if(endTime > 0){
                    startTime = getStartTime(endTime);
                }
                min = endChargeTimes.get(i)== null ? Integer.MAX_VALUE : endChargeTimes.get(i);
                if (startTime > min) {
                    continue;
                } else {
                    endTime = min == Integer.MAX_VALUE ? null : min;
                }
                if (i + 1 < endChargeTimes.size()) {
                    max = endChargeTimes.get(i+1) == null ? Integer.MAX_VALUE : endChargeTimes.get(j);
                    if (max < startTime){
                        continue;
                    }
                    endTime = max == Integer.MAX_VALUE ? null : max;
                }
                Map<String, Date> map = new HashMap <>();
                map.put("startChargeTime", sdf.parse(String.valueOf(startTime)));
                map.put("endChargeTime", endTime == null  ? null : sdf.parse(String.valueOf(endTime)));
                timeRanges.add(map);
            }
        }
        return timeRanges;
    }

    private Integer getStartTime(Integer endTime) {
        int year = endTime/100;
        int month = endTime%100;
        // 此时endTime有值,即有上一条数据，此时开始时间在上一数据结束时间的基础上加一个月
        if(month == 12) {
            year++;
            month = 1;
        } else {
            month++;
        }
        return year*100 + month;
    }
}
