package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.service.*;
import com.authine.cloudpivot.web.api.service.impl.ClientServiceImpl;
import com.authine.cloudpivot.web.api.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 14:13
 * @Description:
 */
@RestController
@RequestMapping("/controller/test")
public class TestController extends BaseController {

    @Autowired
    ClientServiceImpl clientService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BatchPreDispatchService batchPreDispatchService;

    @Autowired
    BatchEvacuationService batchEvacuationService;

    @Autowired
    EmployeeFilesService employeeFilesService;

    @Autowired
    SalesContractService salesContractService;

    @Autowired
    EnquiryReceivableService enquiryReceivableService;

    @RequestMapping("/getProperty")
    public void getProperty() {
//        List<ColumnComment> shAddEmployeeTableColumnComment = tableMapper.getShAddEmployeeTableColumnComment();

        List<BatchPreDispatch> batchPreDispatches = new ArrayList<>();
        String userId = "402881c16eed73b5016eeea6921b0357";
        BatchPreDispatch b1 = new BatchPreDispatch();
        b1.setEmployeeName("王勇");
        BatchPreDispatch b2 = new BatchPreDispatch();
        b2.setEmployeeName("黄河");
        batchPreDispatches.add(b1);
        batchPreDispatches.add(b2);
        List<String> objectIds1 = batchPreDispatchService.addBatchPreDispatchs(userId, this.getOrganizationFacade(), batchPreDispatches);


        List<BatchEvacuation> batchEvacuations = new ArrayList<>();
        BatchEvacuation b3 = new BatchEvacuation();
        b3.setEmployeeName("曾松");
        BatchEvacuation b4 = new BatchEvacuation();
        b4.setEmployeeName("刘磊");
        batchEvacuations.add(b3);
        batchEvacuations.add(b4);

        batchEvacuationService.addBatchEvacuationDatas(userId, this.getOrganizationFacade(), batchEvacuations);
    }

    @GetMapping("/test")
    public Object test() throws IOException {
//        int i = SendSmsUtils.sendSms("您的验证码为：12345，有效时间为3分钟", "13084042075");
        List<String> ids = Arrays.asList("ad8a699cfc6d457db03a96dce1d1d92c");
        List<EnquiryReceivableDto> enquiryReceivableDtoByIds = enquiryReceivableService.getEnquiryReceivableDtoByIds(ids);

        return enquiryReceivableDtoByIds;
    }

    @GetMapping("/getEmployeeFiles")
    public Object getEmployeeFiles(String clientName, String employeeNature) {
        List<EmployeeFilesDto> employeeFilesDto = employeeFilesService.getEmployeeFilesCanGenerateBillByClientName(clientName, employeeNature);
        return employeeFilesDto;
    }

    @GetMapping("/calculationBill")
    public Object calculationBill(String bill) {
        // 根据账单日获取销售合同，用于生成账单
//        List<SalesContractDto> salesContractByBillDay = salesContractService.getSalesContractByBillDay(bill);
        List<SalesContractDto> salesContractByBillDay = salesContractService.getSalesContractByGenerateBillDate(null, null);
        UserModel user = this.getOrganizationFacade().getUser(UserUtils.getUserId(getUserId()));
        DepartmentModel department = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
        for (SalesContractDto salesContractDto : salesContractByBillDay) {
            String clientName = salesContractDto.getClientName();  // 客户名称
            String businessType = salesContractDto.getBusinessType();  // 业务类型
            String billType = salesContractDto.getBillType();  // 账单类型
            String employeeNature = getEmployeeNature(businessType);  // 员工性质
            List<EmployeeFilesDto> employeeFilesDto = employeeFilesService.getEmployeeFilesCanGenerateBillByClientName(clientName, employeeNature);
            generateBill(user, department, billType, employeeFilesDto, salesContractDto);  // 生成账单数据
        }
        return null;
    }

    /**
     * @param user             : 人员
     * @param department       : 部门
     * @param billType         : 账单类型 预收，预估，实收
     * @param employeeFilesDto : 员工档案信息
     * @param salesContractDto : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:01
     * @return: void
     * @Description: 生成员工账单
     */
    private void generateBill(UserModel user, DepartmentModel department, String billType, List<EmployeeFilesDto> employeeFilesDto, SalesContractDto salesContractDto) {
        List<Bill> bills = null;
        switch (billType) {
            case "预收":
                bills = generateAdvanceReceiptBillData(user, department, employeeFilesDto, salesContractDto);
                break;
            case "预估":
                bills = generateEstimateBillData(user, department, employeeFilesDto, salesContractDto);
                break;
        }

    }

    /**
     * @param user
     * @param department
     * @param employeeFilesDtos : 员工档案
     * @param salesContractDto  : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:03
     * @return: void
     * @Description: 生成预收数据
     */
    private List<Bill> generateAdvanceReceiptBillData(UserModel user, DepartmentModel department, List<EmployeeFilesDto> employeeFilesDtos, SalesContractDto salesContractDto) {
        // 预收从当月开始
        String billCycle = salesContractDto.getBillCycle();
        // 获取预收月数
        int monthNum = getMonthNum(billCycle);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 账单起始月
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String billYear = getYearAndMonth(calendar); // 账单年月



        // 账单结束月
        calendar.add(Calendar.MONTH, monthNum);
        billYear += getYearAndMonth(calendar);
        List<Bill> result = new ArrayList<>();
        for (EmployeeFilesDto employeeFilesDto : employeeFilesDtos) {
            PayrollBill payrollBill = employeeFilesService.getPayrollBill(billYear, employeeFilesDto.getIdNo());
            calendar.setTime(new Date());
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
            if (employeeFilesDto.getIsOldEmployee() == 0) {
                // 不是老员工，是新员工，需要生成之前没有生成的数据
                if (employeeFilesDto.getSocialSecurityChargeStart() != null || employeeFilesDto.getProvidentFundChargeStart() != null) {
                    // 社保起缴时间或公积金起缴时间不为空
                    Calendar socialSecurityChargeStart = Calendar.getInstance();
                    int socialSecurityChargeNum = 0;
                    Calendar providentFundChargeStart = Calendar.getInstance();
                    int providentFundChargeNum = 0;
                    if (employeeFilesDto.getSocialSecurityChargeStart() != null) {
                        socialSecurityChargeStart.setTime(employeeFilesDto.getSocialSecurityChargeStart());
                        socialSecurityChargeNum = (calendar.get(Calendar.YEAR) - socialSecurityChargeStart.get(Calendar.YEAR)) * 12 + calendar.get(Calendar.MONTH) - socialSecurityChargeStart.get(Calendar.MONTH);
                    }
                    if (employeeFilesDto.getProvidentFundChargeStart() != null) {
                        providentFundChargeStart.setTime(employeeFilesDto.getProvidentFundChargeStart());
                        providentFundChargeNum = (calendar.get(Calendar.YEAR) - providentFundChargeStart.get(Calendar.YEAR)) * 12 + calendar.get(Calendar.MONTH) - providentFundChargeStart.get(Calendar.MONTH);
                    }
                    if (socialSecurityChargeNum > 0 || providentFundChargeNum > 0) {
                        // 需要生成之前没有生成的数据
                        result.addAll(createOldBillData(user, department, employeeFilesDto, socialSecurityChargeNum, providentFundChargeNum, billYear, 1));
                    }
                }
            }
            for (int i = 1; i <= monthNum; i++) {
                String businessYear = getYearAndMonth(calendar);  // 业务年月
                Bill bill = new Bill();
                setBillBasicData(bill, employeeFilesDto, billYear, businessYear, 0, 0, 0, 0);
                setBillOtherData(bill, employeeFilesDto, salesContractDto, calendar);
                ServiceChargeUnitPrice serviceChargeUnitPrice = getServiceChargeUnitPrice(salesContractDto, employeeFilesDto.getSocialSecurityArea());
                setServiceChargeData(bill, serviceChargeUnitPrice, payrollBill);
                calendar.add(Calendar.MONTH, i);
                SystemDataSetUtils.dataSet(user, department, employeeFilesDto.getEmployeeName(), Constants.COMPLETED_STATUS, bill);
                result.add(bill);
            }
            if (result.size() != 0) {
                List<Bill> noCompareBills = employeeFilesService.getNoCompareBills(result.get(0).getIdNo());
                if (noCompareBills != null && noCompareBills.size() != 0) {
                    // 没有对比的账单不为空
                    result.addAll(compareBills(user, department, result.get(0), noCompareBills));
                }
            }
        }

        return result;
    }


    /**
     * @param user
     * @param department
     * @param employeeFilesDtos : 员工档案
     * @param salesContractDto  : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:04
     * @return: void
     * @Description: 生成预估数据
     */
    private List<Bill> generateEstimateBillData(UserModel user, DepartmentModel department, List<EmployeeFilesDto> employeeFilesDtos, SalesContractDto salesContractDto) {
        // 预估下月的数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String billYear = getYearAndMonth(calendar); // 账单年月
        calendar.add(Calendar.MONTH, 1);
        String businessYear = getYearAndMonth(calendar);  // 业务年月
        List<Bill> bills = new ArrayList<>();
        for (EmployeeFilesDto employeeFilesDto : employeeFilesDtos) {
            if (employeeFilesDto.getIsOldEmployee() == 0) {
                // 是老员工
                // 不是老员工，是新员工，需要生成之前没有生成的数据
                if (employeeFilesDto.getSocialSecurityChargeStart() != null || employeeFilesDto.getProvidentFundChargeStart() != null) {
                    // 社保起缴时间或公积金起缴时间不为空
                    Calendar socialSecurityChargeStart = Calendar.getInstance();
                    int socialSecurityChargeNum = 0;
                    Calendar providentFundChargeStart = Calendar.getInstance();
                    int providentFundChargeNum = 0;
                    if (employeeFilesDto.getSocialSecurityChargeStart() != null) {
                        socialSecurityChargeStart.setTime(employeeFilesDto.getSocialSecurityChargeStart());
                        socialSecurityChargeNum = (calendar.get(Calendar.YEAR) - socialSecurityChargeStart.get(Calendar.YEAR)) * 12 + (calendar.get(Calendar.MONTH) - socialSecurityChargeStart.get(Calendar.MONTH) + 1);
                    }
                    if (employeeFilesDto.getProvidentFundChargeStart() != null) {
                        providentFundChargeStart.setTime(employeeFilesDto.getProvidentFundChargeStart());
                        providentFundChargeNum = (calendar.get(Calendar.YEAR) - providentFundChargeStart.get(Calendar.YEAR)) * 12 + (calendar.get(Calendar.MONTH) - providentFundChargeStart.get(Calendar.MONTH) + 1);
                    }
                    if (socialSecurityChargeNum > 0 || providentFundChargeNum > 0) {
                        // 需要生成之前没有生成的数据
                        bills.addAll(createOldBillData(user, department, employeeFilesDto, socialSecurityChargeNum, providentFundChargeNum, billYear, 0));
                    }
                }
                employeeFilesDto.setIsOldEmployee(1);
            }

            if (employeeFilesDto.getQuitDate() != null) {
                // 离职
                // 离职日期
                Calendar quitCalendar = Calendar.getInstance();
                quitCalendar.setTime(employeeFilesDto.getQuitDate());
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setTime(new Date());
                nowCalendar.add(Calendar.MONTH, 1);  // 账单生成日
                int monthNum = (nowCalendar.get(Calendar.YEAR) - quitCalendar.get(Calendar.YEAR)) * 12 + (nowCalendar.get(Calendar.MONTH) - nowCalendar.get(Calendar.MONTH));
                if (monthNum <= 0) {
                    // 当前时间在离职时间之内
                    // 判断社保截止时间和公积金截止时间在不在账单期之内
                    // 社保截止时间
                    int socialSecurityEndMonthNum = -1;
                    if (employeeFilesDto.getSocialSecurityChargeEnd() != null) {
                        Calendar socialSecurityEndCalendar = Calendar.getInstance();
                        socialSecurityEndCalendar.setTime(employeeFilesDto.getSocialSecurityChargeEnd());
                        // 获取社保截止时间和下个账期之间的月份差
                        socialSecurityEndMonthNum = (socialSecurityEndCalendar.get(Calendar.YEAR) - nowCalendar.get(Calendar.YEAR)) * 12 - (socialSecurityEndCalendar.get(Calendar.MONTH) - nowCalendar.get(Calendar.MONTH));
                    }

                    // 公积金截止时间
                    int providentFundEndMonthNum = -1;
                    if (employeeFilesDto.getProvidentFundChargeEnd() != null) {
                        Calendar providentFundEndCalendar = Calendar.getInstance();
                        providentFundEndCalendar.setTime(employeeFilesDto.getProvidentFundChargeEnd());
                        // 获取公积金截止时间和下个账期之间的月份差
                        providentFundEndMonthNum = (providentFundEndCalendar.get(Calendar.YEAR) - nowCalendar.get(Calendar.YEAR)) * 12 - (providentFundEndCalendar.get(Calendar.MONTH) - nowCalendar.get(Calendar.MONTH));
                    }
                    if (socialSecurityEndMonthNum == -1 && providentFundEndMonthNum == -1) {
                        // 无需在生成账单
                        // 停止以后生成账单
                        employeeFilesDto.setStopGenerateBill(1);
                        continue;
                    }
                    if ((socialSecurityEndMonthNum == 0 && providentFundEndMonthNum == 0) || (socialSecurityEndMonthNum == -1 && providentFundEndMonthNum == 0) && (socialSecurityEndMonthNum == 0 && providentFundEndMonthNum == -1)) {
                        // 停止下月生成账单
                        employeeFilesDto.setStopGenerateBill(1);
                    }
                    // 生成离职订单
                    bills.addAll(createQuitBillData(user, department, employeeFilesDto, salesContractDto, socialSecurityEndMonthNum, providentFundEndMonthNum, billYear, nowCalendar));
                    continue;
                }

            }

            Bill bill = new Bill();
            setBillBasicData(bill, employeeFilesDto, billYear, businessYear, 0, 0, 0, 0);
            setBillOtherData(bill, employeeFilesDto, salesContractDto, calendar);
            // 设置系统数据
            SystemDataSetUtils.dataSet(user, department, bill.getName(), Constants.COMPLETED_STATUS, bill);
            List<Bill> noCompareBills = employeeFilesService.getNoCompareBills(bill.getIdNo());
            if (noCompareBills != null && noCompareBills.size() != 0) {
                // 没有对比的账单不为空
                bills.addAll(compareBills(user, department, bill, noCompareBills));
            }
            bills.add(bill);
        }
        return bills;
    }

    /**
     * @param employeeFilesDto:          员工档案
     * @param socialSecurityEndMonthNum: 社保需要生成的月份
     * @param providentFundEndMonthNum:  公积金需要生成的月份
     * @Author: wangyong
     * @Date: 2020/4/2 9:49
     * @return: java.util.List<com.authine.cloudpivot.web.api.entity.Bill>
     * @Description: 生成离职账单
     */
    private List<Bill> createQuitBillData(UserModel user, DepartmentModel department, EmployeeFilesDto employeeFilesDto, SalesContractDto salesContractDto, int socialSecurityEndMonthNum, int providentFundEndMonthNum, String billYear, Calendar businessCalendar) {
        List<Bill> bills = new LinkedList<>();
        int maxMonthNum = Math.max(socialSecurityEndMonthNum, providentFundEndMonthNum);
        int num = 0;
        List<SocialSecurityFundDetail> socialSecurityFundDetails = employeeFilesDto.getEmployeeOrderFormDto().getSocialSecurityFundDetails();
        for (int i = 0; i < maxMonthNum; i++) {
            PayrollBill payrollBill = employeeFilesService.getPayrollBill(billYear, employeeFilesDto.getIdNo());
            businessCalendar.add(Calendar.MONTH, i);
            Bill bill = new Bill();
            String businessYear = getYearAndMonth(businessCalendar);
            setBillBasicData(bill, employeeFilesDto, billYear, businessYear, 0, 0, 0, 0);
            SystemDataSetUtils.dataSet(user, department, employeeFilesDto.getEmployeeName(), Constants.COMPLETED_STATUS, bill);
            for (SocialSecurityFundDetail socialSecurityFundDetail : socialSecurityFundDetails) {
                if (i < socialSecurityEndMonthNum) {
                    // 需要生成社保
                    generateSocialSecurityData(socialSecurityFundDetail, bill, null);
                }
                if (i < providentFundEndMonthNum) {
                    // 需要生成公积金
                    generateProvidentFund(socialSecurityFundDetail, bill,  null);
                }
            }
            setBillOtherData(bill, employeeFilesDto, salesContractDto, null);
            ServiceChargeUnitPrice serviceChargeUnitPrice = getServiceChargeUnitPrice(salesContractDto, employeeFilesDto.getSocialSecurityArea());
            setServiceChargeData(bill, serviceChargeUnitPrice, payrollBill);
            bills.add(bill);
        }

        return bills;
    }

    /**
     * @param user
     * @param department
     * @param bill           :           当前账单
     * @param noCompareBills : 历史账单
     * @Author: wangyong
     * @Date: 2020/4/1 13:37
     * @return: void
     * @Description: 对比历史账单，查看是否有差异，返回有差异的订单
     */
    private List<Bill> compareBills(UserModel user, DepartmentModel department, Bill bill, List<Bill> noCompareBills) {
        List<Bill> result = new ArrayList<>();
        if (noCompareBills != null || noCompareBills.size() != 0) {
            for (Bill noCompareBill : noCompareBills) {
                Bill b = compare(user, department, bill, noCompareBill);
                if (b != null) {
                    result.add(b);
                }
            }
        }
        return result;
    }

    /**
     * @param user
     * @param department
     * @param nowBill       :       当前账单
     * @param noCompareBill : 历史账单
     * @Author: wangyong
     * @Date: 2020/4/1 16:01
     * @return: com.authine.cloudpivot.web.api.entity.Bill
     * @Description: 对比账单产生差异
     */
    private Bill compare(UserModel user, DepartmentModel department, Bill nowBill, Bill noCompareBill) {
        Bill bill = new Bill();
        boolean flag = false;

        BeanUtils.copyProperties(nowBill, bill);
        // 设置系统数据
        SystemDataSetUtils.dataSet(user, department, bill.getName(), Constants.COMPLETED_STATUS, bill);

        // 业务年月
        bill.setBusinessYear(noCompareBill.getBusinessYear());

        // 社保企业缴纳
        Double socialSecurityEnterprise = 0D;
        // 社保个人缴纳
        Double socialSecurityPersonal = 0D;

        // 养老企业缴纳
        bill.setPensionEnterprisePay(DoubleUtils.getDifference(nowBill.getPensionEnterprisePay(), noCompareBill.getPensionEnterprisePay()));
        socialSecurityEnterprise += bill.getPensionEnterprisePay();
        if (bill.getPensionPersonalPay() != 0D)
            flag = true;
        // 养老个人缴纳
        bill.setPensionPersonalPay(DoubleUtils.getDifference(nowBill.getPensionPersonalPay(), noCompareBill.getPensionPersonalPay()));
        socialSecurityPersonal += bill.getPensionPersonalPay();
        if (bill.getPensionPersonalPay() != 0D)
            flag = true;
        // 养老缴纳小计
        bill.setPensionSubtotal(bill.getPensionPersonalPay() + bill.getPensionPersonalPay());

        // 医疗企业缴纳
        bill.setMedicalEnterprisePay(DoubleUtils.getDifference(nowBill.getMedicalEnterprisePay(), noCompareBill.getMedicalEnterprisePay()));
        socialSecurityEnterprise += bill.getMedicalEnterprisePay();
        if (bill.getMedicalEnterprisePay() != 0D)
            flag = true;
        // 医疗个人缴纳
        bill.setMedicalPersonalPay(DoubleUtils.getDifference(nowBill.getMedicalPersonalPay(), noCompareBill.getMedicalPersonalPay()));
        socialSecurityPersonal += bill.getMedicalPersonalPay();
        if (bill.getMedicalPersonalPay() != 0D)
            flag = true;
        // 医疗缴纳小计
        bill.setMedicalSubtotal(bill.getMedicalEnterprisePay() + bill.getMedicalPersonalPay());

        // 失业企业缴纳
        bill.setUnempEnterprisePay(DoubleUtils.getDifference(nowBill.getUnempEnterprisePay(), noCompareBill.getUnempEnterprisePay()));
        socialSecurityEnterprise += bill.getUnempEnterprisePay();
        if (bill.getUnempEnterprisePay() != 0D)
            flag = true;
        // 失业个人缴纳
        bill.setUnempPersonalPay(DoubleUtils.getDifference(nowBill.getUnempPersonalPay(), noCompareBill.getUnempPersonalPay()));
        socialSecurityPersonal += bill.getUnempPersonalPay();
        if (bill.getUnempPersonalPay() != 0D)
            flag = true;
        // 失业缴纳小计
        bill.setUnempSubtotal(bill.getUnempEnterprisePay() + bill.getUnempPersonalPay());

        // 工伤企业缴纳
        bill.setInjuryEnterprisePay(DoubleUtils.getDifference(nowBill.getInjuryEnterprisePay(), noCompareBill.getInjuryEnterprisePay()));
        socialSecurityEnterprise += bill.getInjuryEnterprisePay();
        if (bill.getInjuryEnterprisePay() != 0D)
            flag = true;
        // 工伤缴纳小计
        bill.setInjurySubtotal(bill.getInjuryEnterprisePay());

        // 生育企业缴纳
        bill.setFertilityEnterprisePay(DoubleUtils.getDifference(nowBill.getFertilityEnterprisePay(), noCompareBill.getFertilityEnterprisePay()));
        socialSecurityEnterprise += bill.getFertilityEnterprisePay();
        if (bill.getFertilityEnterprisePay() != 0D)
            flag = true;
        bill.setFertilitySubtotal(bill.getFertilityEnterprisePay());

        // 大病医疗企业缴纳
        bill.setDMedicalEnterprisePay(DoubleUtils.getDifference(nowBill.getDMedicalEnterprisePay(), noCompareBill.getDMedicalEnterprisePay()));
        socialSecurityEnterprise += bill.getDMedicalEnterprisePay();
        if (bill.getDMedicalEnterprisePay() != 0D)
            flag = true;
        //大病医疗个人缴纳
        bill.setDMedicalPersonalPay(DoubleUtils.getDifference(nowBill.getDMedicalPersonalPay(), noCompareBill.getDMedicalPersonalPay()));
        socialSecurityPersonal += bill.getDMedicalPersonalPay();
        if (bill.getDMedicalPersonalPay() != 0D)
            flag = true;
        // 大病医疗缴纳小计
        bill.setDMedicalSubtotal(bill.getDMedicalEnterprisePay() + bill.getDMedicalPersonalPay());

        // 综合企业缴纳
        bill.setComplexEnterprisePay(DoubleUtils.getDifference(nowBill.getComplexEnterprisePay(), noCompareBill.getComplexEnterprisePay()));
        socialSecurityEnterprise += bill.getComplexEnterprisePay();
        if (bill.getComplexEnterprisePay() != 0D)
            flag = true;
        // 综合个人缴纳
        bill.setComplexPersonalPay(DoubleUtils.getDifference(nowBill.getComplexPersonalPay(), noCompareBill.getComplexPersonalPay()));
        socialSecurityPersonal += bill.getComplexPersonalPay();
        if (bill.getComplexPersonalPay() != 0D)
            flag = true;
        // 综合缴纳小计
        bill.setComplexSubtotal(bill.getComplexEnterprisePay() + bill.getComplexPersonalPay());

        // 补充工伤企业缴纳
        bill.setBInjuryEnterprisePay(DoubleUtils.getDifference(nowBill.getBInjuryEnterprisePay(), noCompareBill.getBInjuryEnterprisePay()));
        socialSecurityEnterprise += bill.getBInjuryEnterprisePay();
        if (bill.getBInjuryEnterprisePay() != 0D)
            flag = true;
        // 补充工伤缴纳小计
        bill.setBInjurySubtotal(bill.getBInjuryEnterprisePay());

        //社保企业缴纳合计
        bill.setSocialSecurityEnterprise(socialSecurityEnterprise);
        // 社保个人缴纳合计
        bill.setSocialSecurityPersonal(socialSecurityPersonal);
        // 社保缴纳小计
        bill.setSocialSecurityTotal(socialSecurityEnterprise + socialSecurityPersonal);

        Double providentEnterprise = 0D;
        Double providentPersonal = 0D;


        // 公积金企业缴纳
        bill.setProvidentEnterprisePay(DoubleUtils.getDifference(nowBill.getProvidentEnterprisePay(), noCompareBill.getPensionEnterprisePay()));
        providentEnterprise += bill.getProvidentEnterprisePay();
        if (bill.getProvidentEnterprisePay() != 0D)
            flag = true;
        // 公积金个人缴纳
        bill.setProvidentPersonalPay(DoubleUtils.getDifference(nowBill.getPensionPersonalPay(), noCompareBill.getPensionPersonalPay()));
        providentPersonal += bill.getProvidentPersonalPay();
        if (bill.getPensionPersonalPay() != 0D)
            flag = true;
        // 公积金缴纳小计
        bill.setProvidentSubtotal(bill.getProvidentEnterprisePay() + bill.getPensionPersonalPay());

        // 补充公积金企业缴纳
        bill.setBProvidentEnterprisePay(DoubleUtils.getDifference(nowBill.getBProvidentEnterprisePay(), noCompareBill.getBProvidentEnterprisePay()));
        providentEnterprise += bill.getBProvidentEnterprisePay();
        if (bill.getBProvidentEnterprisePay() != 0D)
            flag = false;
        // 补充公积金个人缴纳
        bill.setBProvidentPersonalPay(DoubleUtils.getDifference(nowBill.getBProvidentPersonalPay(), noCompareBill.getBProvidentPersonalPay()));
        providentPersonal += bill.getBProvidentPersonalPay();
        if (bill.getBProvidentPersonalPay() != 0D)
            flag = false;
        // 补充公积金缴纳小计
        bill.setBProvidentSubtotal(bill.getBProvidentEnterprisePay() + bill.getBProvidentPersonalPay());

        // 公积金企业缴纳合计
        bill.setProvidentEnterprise(providentEnterprise);
        // 公积金个人缴纳合计
        bill.setProvidentPersonal(providentPersonal);
        // 公积金缴纳合计
        bill.setProvidentTotal(providentEnterprise + providentPersonal);

        // 增值税税费
        bill.setVatTax(0D);
        // 福利产品总额
        bill.setTotalWelfareProducts(0D);
        // 风险管理费
        bill.setRiskManageFee(0D);
        // 外包管理费
        bill.setOutsourcingManageFee(0D);
        // 营业税税费
        bill.setBusinessTax(0D);
        // 社保公积金合计
        bill.setSocialProvidentTotal(bill.getSocialSecurityTotal() + bill.getProvidentTotal());
        // 合计收费
        bill.setTotalCharge(bill.getSocialProvidentTotal());
        bill.setWhetherDefine(1);
        bill.setWhetherDifferenceData(1);
        bill.setWhetherCompare(1);
        bill.setIsLock(1);
        if (flag) {
            // 有差异，返回当前的差异账单
            return bill;
        } else {
            return null;
        }
    }


    /**
     * @param bill             : 账单
     * @param employeeFilesDto : 员工档案
     * @param salesContractDto : 销售合同
     * @param calendar         : 日期
     * @Author: wangyong
     * @Date: 2020/3/18 23:31
     * @return: void
     * @Description: 生成员工账单其他信息
     */
    private void setBillOtherData(Bill bill, EmployeeFilesDto employeeFilesDto, SalesContractDto salesContractDto, Calendar calendar) {
        // 员工订单
        EmployeeOrderFormDto employeeOrderFormDto = employeeFilesDto.getEmployeeOrderFormDto();

        // 社保公积金详情
        List<SocialSecurityFundDetail> socialSecurityFundDetails = employeeOrderFormDto.getSocialSecurityFundDetails();

        // 生成五险一金的明细数据
        for (SocialSecurityFundDetail socialSecurityFundDetail : socialSecurityFundDetails) {
            // 生成社保数据
            generateSocialSecurityData(socialSecurityFundDetail, bill, calendar);

            // 生成公积金数据
            generateProvidentFund(socialSecurityFundDetail, bill, calendar);
        }
    }

    private void setServiceChargeData(Bill bill, ServiceChargeUnitPrice serviceChargeUnitPrice, PayrollBill payrollBill) {
        // 社保缴纳合计
        bill.setSocialSecurityTotal(bill.getSocialSecurityEnterprise() + bill.getSocialSecurityPersonal());
        // 公积金缴纳合计
        bill.setProvidentTotal(bill.getProvidentEnterprise() + bill.getProvidentPersonal());
        // 社保公积金合计
        bill.setSocialProvidentTotal(bill.getSocialSecurityTotal() + bill.getProvidentTotal());

        if (serviceChargeUnitPrice != null) {
            // 服务费
            bill.setServiceFee(DoubleUtils.nullToDouble(serviceChargeUnitPrice.getServiceChargeUnitPrice()));
            // 增值税
            double vatTaxes = DoubleUtils.nullToDouble(serviceChargeUnitPrice.getVatTaxes());
            if (vatTaxes < 1) {
                bill.setVatTax(DoubleUtils.doubleRound(vatTaxes * bill.getServiceFee(), 2));
            } else {
                bill.setVatTax(serviceChargeUnitPrice.getVatTaxes());
            }
            // 福利产品总额
            double totalWelfareProducts = DoubleUtils.nullToDouble(serviceChargeUnitPrice.getTotalWelfareProducts());
            bill.setTotalWelfareProducts(totalWelfareProducts);

            // 风险管理费
            double riskManagementFee = DoubleUtils.nullToDouble(serviceChargeUnitPrice.getRiskManagementFee());
            if (riskManagementFee < 1) {
                double payable = 0D; // 应发工资
                if (payrollBill != null) {
                    payable = DoubleUtils.nullToDouble(payable);
                }
                double socialSecurityEnterprise = DoubleUtils.nullToDouble(bill.getSocialSecurityEnterprise());  // 社保企业缴纳合计
                double socialSecurityPersonal = DoubleUtils.nullToDouble(bill.getSocialSecurityPersonal());  // 社保个人缴纳合计
                bill.setRiskManageFee((payable + socialSecurityEnterprise + socialSecurityPersonal) * riskManagementFee);
            } else {
                bill.setRiskManageFee(riskManagementFee);  // 风险管理费
            }
            // 外包管理费
            bill.setOutsourcingManageFee(DoubleUtils.nullToDouble(serviceChargeUnitPrice.getOutsourcingManagementFee()));

            // 营业税税费
            double businessTax = DoubleUtils.nullToDouble(serviceChargeUnitPrice.getBusinessTax());
            if (businessTax < 1) {
                double payable = 0D; // 应发工资
                if (payrollBill != null) {
                    payable = DoubleUtils.nullToDouble(payable);
                }
                double socialSecurityEnterprise = DoubleUtils.nullToDouble(bill.getSocialSecurityEnterprise());  // 社保企业缴纳合计
                double socialSecurityPersonal = DoubleUtils.nullToDouble(bill.getSocialSecurityPersonal());  // 社保个人缴纳合计
                double riskManagementFee2 = DoubleUtils.nullToDouble(bill.getRiskManageFee());
                double outsourcingManageFee = DoubleUtils.nullToDouble(bill.getOutsourcingManageFee());
                bill.setBusinessTax((payable + socialSecurityEnterprise + socialSecurityPersonal + riskManagementFee2 + outsourcingManageFee) * businessTax);
            } else {
                bill.setBusinessTax(businessTax);
            }
//            bill.
        } else {

        }


    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 补充公积金明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 23:48
     * @return: void
     * @Description: 生成补充公积金数据
     */
    private void generateBProvident(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {

        bill.setBProvidentEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 补充公积金企业基数
        bill.setBProvidentEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 补充公积金企业比例
        bill.setBProvidentEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 补充公积金企业缴纳
        bill.setBProvidentPersonalBase(socialSecurityFundDetail.getBaseNum());  // 补充公积金个人基数
        bill.setBProvidentPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 补充公积金个人比例
        bill.setBProvidentPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 补充公积金个人缴纳
        bill.setBProvidentSubtotal(socialSecurityFundDetail.getSum());  // 补充公积金缴纳小计
        bill.setBProvidentPaymentMethod(Constants.PAY_METHOD);  // 补充公积金支付方式
        bill.setProvidentEnterprise(bill.getProvidentEnterprise() + bill.getBProvidentEnterprisePay());
        bill.setProvidentPersonal(bill.getProvidentPersonal() + bill.getBProvidentPersonalPay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 公积金明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 23:43
     * @return: void
     * @Description: 生成公积金数据
     */
    private void generateProvident(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setProvidentEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 公积金企业基数
        bill.setProvidentEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 公积金企业比例
        bill.setProvidentEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 公积金企业缴纳
        bill.setProvidentPersonalBase(socialSecurityFundDetail.getBaseNum());  // 公积金个人基数
        bill.setProvidentPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 公积金个人比例
        bill.setProvidentPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 公积金个人缴纳
        bill.setProvidentSubtotal(socialSecurityFundDetail.getSum());  // 公积金缴纳小计
        bill.setProvidentPaymentMethod(Constants.PAY_METHOD);  // 公积金支付方式
        bill.setProvidentEnterprise(bill.getProvidentEnterprise() + bill.getProvidentEnterprisePay());
        bill.setProvidentPersonal(bill.getProvidentPersonal() + bill.getProvidentPersonalPay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 补充工伤保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 23:29
     * @return: void
     * @Description: 生成补充工伤保险数据
     */
    private void generateBInjury(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setBInjuryEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 补充工伤企业基数
        bill.setBInjuryEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 补充工伤企业比例
        bill.setBInjuryEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 补充工伤企业附加
        bill.setBInjuryEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 补充工伤企业缴纳
        bill.setBInjurySubtotal(socialSecurityFundDetail.getSum());  // 补充工伤缴纳小计
        bill.setBInjuryPaymentMethod(Constants.PAY_METHOD);  // 补充工伤支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getBInjuryEnterprisePay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 综合保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 23:24
     * @return: void
     * @Description: 生成综合保险数据
     */
    private void generateComplex(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setComplexEnterpriseBase(socialSecurityFundDetail.getBaseNum());
        bill.setComplexEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());
        bill.setComplexEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());
        bill.setComplexEnterprisePay(socialSecurityFundDetail.getCompanyMoney());
        bill.setComplexPersonalBase(socialSecurityFundDetail.getBaseNum());
        bill.setComplexPersonalRatio(socialSecurityFundDetail.getCompanyRatio());
        bill.setComplexPersonalAttach(socialSecurityFundDetail.getEmployeeSurchargeValue());
        bill.setComplexPersonalPay(socialSecurityFundDetail.getEmployeeMoney());
        bill.setComplexSubtotal(socialSecurityFundDetail.getSum());
        bill.setComplexPaymentMethod(Constants.PAY_METHOD);
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getComplexEnterprisePay());
        bill.setSocialSecurityPersonal(bill.getSocialSecurityPersonal() + bill.getComplexPersonalPay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 大病医疗保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 23:05
     * @return: void
     * @Description: 生成大病医疗保险数据
     */
    private void generateDMedical(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setDMedicalEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 大病医疗企业基数
        bill.setDMedicalEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 大病医疗企业比例
        bill.setDMedicalEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 大病医疗企业附加
        bill.setDMedicalEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 大病医疗企业缴纳
        bill.setDMedicalPersonalBase(socialSecurityFundDetail.getBaseNum());  // 大病医疗个人基数
        bill.setDMedicalPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 大病医疗个人比例
        bill.setDMedicalPersonalAttach(socialSecurityFundDetail.getEmployeeSurchargeValue());  // 大病医疗个人附加
        bill.setDMedicalPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 大病医疗个人缴纳
        bill.setDMedicalSubtotal(socialSecurityFundDetail.getSum());  // 大病医疗缴纳小计
        bill.setDMedicalPaymentMethod(Constants.PAY_METHOD);  // 大病医疗支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getDMedicalEnterprisePay());
        bill.setSocialSecurityPersonal(bill.getSocialSecurityPersonal() + bill.getDMedicalPersonalPay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 生育保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 22:58
     * @return: void
     * @Description: 生成生育保险数据
     */
    private void generateFertility(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setFertilityEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 生育企业基数
        bill.setFertilityEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 生育企业比例
        bill.setFertilityEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 生育企业附加
        bill.setFertilityEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 生育企业缴纳
        bill.setFertilitySubtotal(socialSecurityFundDetail.getSum());  // 生育缴纳小计
        bill.setFertilityPaymentMethod(Constants.PAY_METHOD);  // 生育支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getFertilityEnterprisePay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 工伤保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 22:54
     * @return: void
     * @Description: 生成工伤保险数据
     */
    private void generateInjury(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setInjuryEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 工伤企业基数
        bill.setInjuryEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 工伤企业比例
        bill.setInjuryEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 工伤企业附加
        bill.setInjuryEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 工伤企业缴纳
        bill.setInjurySubtotal(socialSecurityFundDetail.getSum());  // 工伤缴纳小计
        bill.setInjuryPaymentMethod(Constants.PAY_METHOD);  // 工伤支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getInjuryEnterprisePay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 失业保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 22:47
     * @return: void
     * @Description: 生成失业保险数据
     */
    private void generateUnemp(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setUnempEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 失业企业基数
        bill.setUnempEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 失业企业比例
        bill.setUnempEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 失业企业附加
        bill.setUnempEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 失业企业缴纳
        bill.setUnempPersonalBase(socialSecurityFundDetail.getBaseNum());  // 失业个人基数
        bill.setUnempPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 失业个人比例
        bill.setUnempPersonalAttach(socialSecurityFundDetail.getEmployeeSurchargeValue());  // 失业个人附加
        bill.setUnempPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 失业个人缴纳
        bill.setUnempSubtotal(socialSecurityFundDetail.getSum());  // 失业缴纳小计
        bill.setUnempPaymentMethod(Constants.PAY_METHOD);  // 失业支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getUnempEnterprisePay());
        bill.setSocialSecurityPersonal(bill.getSocialSecurityPersonal() + bill.getUnempPersonalPay());
    }

    /**
     * @param bill:                     账单
     * @param socialSecurityFundDetail: 医疗保险明细
     * @param calendar:                 开始时间
     * @Author: wangyong
     * @Date: 2020/3/24 22:43
     * @return: void
     * @Description: 生成医疗保险数据
     */
    private void generateMedical(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar calendar) {
        bill.setMedicalEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 医疗企业基数
        bill.setMedicalEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 医疗企业比例
        bill.setMedicalEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 医疗企业附加
        bill.setMedicalEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 医疗企业缴纳
        bill.setMedicalPersonalBase(socialSecurityFundDetail.getBaseNum());  // 医疗个人基数
        bill.setMedicalPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 医疗个人比例
        bill.setMedicalPersonalAttach(socialSecurityFundDetail.getEmployeeSurchargeValue());  // 医疗个人附加
        bill.setMedicalPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 医疗个人缴纳
        bill.setMedicalSubtotal(socialSecurityFundDetail.getSum());  // 医疗缴纳小计
        bill.setMedicalPaymentMethod(Constants.PAY_METHOD);  // 医疗支付方式
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getMedicalEnterprisePay());
        bill.setSocialSecurityPersonal(bill.getSocialSecurityPersonal() + bill.getMedicalPersonalPay());
    }

    /**
     * @param bill                     : 账单
     * @param socialSecurityFundDetail : 养老保险明细
     * @param startTime                : 开始时间
     * @Author: wangyong
     * @Date: 2020/3/18 23:53
     * @Description: 生成养老保险数据
     */
    private void generatePension(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar startTime) {

        bill.setPensionEnterpriseBase(socialSecurityFundDetail.getBaseNum());  // 养老企业基数
        bill.setPensionEnterpriseRatio(socialSecurityFundDetail.getCompanyRatio());  // 养老企业比例
        bill.setPensionEnterpriseAttach(socialSecurityFundDetail.getCompanySurchargeValue());  // 养老企业附加
        bill.setPensionEnterprisePay(socialSecurityFundDetail.getCompanyMoney());  // 养老企业缴纳
        bill.setPensionPersonalBase(socialSecurityFundDetail.getBaseNum());  // 养老个人基数
        bill.setPensionPersonalRatio(socialSecurityFundDetail.getEmployeeRatio());  // 养老个人比例
        bill.setPensionPersonalAttach(socialSecurityFundDetail.getEmployeeSurchargeValue());  // 养老个人附加
        bill.setPensionPersonalPay(socialSecurityFundDetail.getEmployeeMoney());  // 养老个人缴纳
        bill.setPensionSubtotal(socialSecurityFundDetail.getSum());  // 养老缴纳小计
        bill.setPensionPaymentMethod(Constants.PAY_METHOD);
        bill.setSocialSecurityEnterprise(bill.getSocialSecurityEnterprise() + bill.getPensionEnterprisePay());
        bill.setSocialSecurityPersonal(bill.getSocialSecurityPersonal() + bill.getPensionPersonalPay());

//        return 0;
    }


    /**
     * @param calendar: 日期
     * @Author: wangyong
     * @Date: 2020/3/18 23:29
     * @return: java.lang.String
     * @Description: 将年月组合成字符串
     */
    private String getYearAndMonth(Calendar calendar) {
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer year = calendar.get(Calendar.YEAR);
        return month < 10 ? year.intValue() + "0" + month.intValue() : year.intValue() + "" + month.intValue();
    }

    /**
     * @param bill:                  账单
     * @param employeeFilesDto:      员工档案
     * @param billYear:              账单年月
     * @param businessYear:          业务年月
     * @param whetherDefine:         是否确定
     * @param whetherCompare:        是否对比
     * @param isLock:                是否锁定
     * @param whetherDifferenceData: 是否差异数据
     * @Author: wangyong
     * @Date: 2020/3/18 23:26
     * @return: void
     * @Description: 设置账单基本信息
     */
    private void setBillBasicData(Bill bill, EmployeeFilesDto employeeFilesDto, String billYear, String businessYear, Integer whetherCompare, Integer whetherDefine, Integer isLock, Integer whetherDifferenceData) {
        bill.setBillYear(billYear);  // 账单年月
        bill.setBusinessYear(businessYear);  // 业务年月
        bill.setEmployeeName(employeeFilesDto.getEmployeeName());  // 雇员姓名
        bill.setIdNo(employeeFilesDto.getIdNo());  // 证件号
        bill.setDelegatedArea(employeeFilesDto.getSocialSecurityArea());  // 委派地区
        bill.setWhetherCompare(whetherCompare);  // 是否对比
        bill.setWhetherDefine(whetherDefine);  // 是否确定
        bill.setIsLock(isLock);  // 是否锁定
        bill.setWhetherDifferenceData(whetherDifferenceData);
        bill.setRelationEmployeeFiles(employeeFilesDto.getId());
    }


    /**
     * @param employeeFilesDto:
     * @param socialSecurityChargeNum:
     * @param providentFundChargeNum:
     * @param billYear:
     * @Author: wangyong
     * @Date: 2020/4/1 16:42
     * @return: java.util.Collection<? extends com.authine.cloudpivot.web.api.entity.Bill>
     * @Description: 生成补缴数据
     */
    private Collection<? extends Bill> createOldBillData(UserModel user, DepartmentModel departmentModel, EmployeeFilesDto employeeFilesDto, int socialSecurityChargeNum, int providentFundChargeNum, String billYear, Integer start) {
        List<Bill> result = new ArrayList<>();

        int num = 0;
        int maxNum = Math.max(socialSecurityChargeNum, providentFundChargeNum);
        List<SocialSecurityFundDetail> socialSecurityFundDetails = employeeFilesDto.getEmployeeOrderFormDto().getSocialSecurityFundDetails();
        if (socialSecurityFundDetails.isEmpty()) {
            // 为空
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = start; i <= maxNum; i++) {
            num++;
            Bill bill = new Bill();
            SystemDataSetUtils.dataSet(user, departmentModel, employeeFilesDto.getEmployeeName(), Constants.COMPLETED_STATUS, bill);
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
            calendar.add(Calendar.MONTH, -i);
            String businessYear = getYearAndMonth(calendar);
            setBillBasicData(bill, employeeFilesDto, billYear, businessYear, 0, 0, 0, 0);
            for (SocialSecurityFundDetail socialSecurityFundDetail : socialSecurityFundDetails) {

                if (num <= socialSecurityChargeNum) {
                    // 生成社保数据
                    generateSocialSecurityData(socialSecurityFundDetail, bill, null);
                }
                if (num <= providentFundChargeNum) {
                    // 生成公积金数据
                    generateProvidentFund(socialSecurityFundDetail, bill, null);
                }
            }
            SystemDataSetUtils.dataSet(user, departmentModel, employeeFilesDto.getEmployeeName(), Constants.COMPLETED_STATUS, bill);
            result.add(bill);
        }
        return result;
    }

    /**
     * @param socialSecurityFundDetail:
     * @param bill:
     * @param calendar:
     * @Author: wangyong
     * @Date: 2020/3/31 13:13
     * @return: void
     * @Description: 生成社保数据
     */
    private void generateSocialSecurityData(SocialSecurityFundDetail socialSecurityFundDetail, Bill bill, Calendar calendar) {

        String productName = socialSecurityFundDetail.getNameHide();
        if (productName.contains("养老")) {
            // 生成养老保险数据
            generatePension(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("医疗") && !productName.contains("大病")) {
            // 生成医疗保险数据
            generateMedical(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("失业")) {
            // 生成失业保险数据
            generateUnemp(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("工伤") && !productName.contains("补充")) {
            // 生成工伤保险数据
            generateInjury(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("生育")) {
            // 生成生育保险数据
            generateFertility(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("医疗") && productName.contains("大病")) {
            // 生成大病医疗保险数据
            generateDMedical(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("综合")) {
            // 生成综合保险数据
            generateComplex(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("工伤") && productName.contains("补充")) {
            // 生成补充工伤保险数据
            generateBInjury(bill, socialSecurityFundDetail, calendar);
        }
    }

    /**
     * @param socialSecurityFundDetail:
     * @param bill:
     * @param calendar:
     * @Author: wangyong
     * @Date: 2020/3/31 13:14
     * @return: void
     * @Description: 生成公积金数据
     */
    private void generateProvidentFund(SocialSecurityFundDetail socialSecurityFundDetail, Bill bill, Calendar calendar) {
        String productName = socialSecurityFundDetail.getNameHide();
        if (productName.contains("公积金") && !productName.contains("补充")) {
            // 生成公积金数据
            generateProvident(bill, socialSecurityFundDetail, calendar);
        } else if (productName.contains("公积金") && productName.contains("补充")) {
            // 生成补充公积金数据
            generateBProvident(bill, socialSecurityFundDetail, calendar);
        }
    }


    /**
     * @param billCycle: 账单周期
     * @Author: wangyong
     * @Date: 2020/3/25 12:37
     * @return: int
     * @Description: 根据账单周期获取对应的月份
     */
    private int getMonthNum(String billCycle) {
        int result = 0;

        switch (billCycle) {
            case "月度":
                result = 1;
                break;
            case "季度":
                result = 3;
                break;
            case "半年":
                result = 6;
                break;
            case "一年":
                result = 12;
                break;
        }

        return result;
    }

    /**
     * @param businessType: 业务类型
     * @Author: wangyong
     * @Date: 2020/3/18 22:58
     * @return: java.lang.String
     * @Description: 根据业务类型获取相应的员工的员工性质
     */
    private String getEmployeeNature(String businessType) {
        String result;
        switch (businessType) {
            case "代理业务":
                result = "代理";
                break;
            case "专项外包":
                result = "外包";
                break;
            case "派遣业务":
                result = "派遣";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    private ServiceChargeUnitPrice getServiceChargeUnitPrice(SalesContractDto salesContractDto, String area) {
        List<ServiceChargeUnitPrice> serviceChargeUnitPrices = salesContractDto.getServiceChargeUnitPrices();
        boolean isAnhuiCity = AreaUtils.isAnhuiCity(area);
        String flag = "";
        if (isAnhuiCity) {
            flag = "省内";
        } else {
            flag = "省外";
        }
        if (null == serviceChargeUnitPrices || 0 == serviceChargeUnitPrices.size()) {
            return null;
        }
        if (serviceChargeUnitPrices.size() == 1) {
            return serviceChargeUnitPrices.get(0);
        }
        int level = 0;
        ServiceChargeUnitPrice result = null;
        for (ServiceChargeUnitPrice serviceChargeUnitPrice : serviceChargeUnitPrices) {
            if (flag.equals(serviceChargeUnitPrice.getServiceArea())) {
                level = 1;
                if (serviceChargeUnitPrice.getAreaDetails().length() > area.length()) {
                    if (serviceChargeUnitPrice.getAreaDetails().contains(area)) {
                        level = 2;
                        result = serviceChargeUnitPrice;
                    }
                } else {
                    if (area.contains(serviceChargeUnitPrice.getAreaDetails())) {
                        level = 2;
                        result = serviceChargeUnitPrice;
                    }
                }
            }
            if (level == 2) {
                // 达到最终筛选条件了
                break;
            }
        }
        return result;
    }

}
