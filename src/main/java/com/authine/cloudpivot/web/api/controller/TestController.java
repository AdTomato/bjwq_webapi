package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.AppManagementFacade;
import com.authine.cloudpivot.engine.api.model.bizform.BizFormHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryHeaderModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.TableMapper;
import com.authine.cloudpivot.web.api.service.BatchEvacuationService;
import com.authine.cloudpivot.web.api.service.BatchPreDispatchService;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.utils.MD5;
import com.authine.cloudpivot.web.api.utils.SendSmsUtils;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.tomcat.jni.BIOCallback;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 14:13
 * @Description:
 */
@RestController
@RequestMapping("/controller/test")
public class TestController extends BaseController {

    @Resource
    TableMapper tableMapper;

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
        int i = SendSmsUtils.sendSms("您的验证码为：12345，有效时间为3分钟", "13084042075");
        return i;
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

        for (SalesContractDto salesContractDto : salesContractByBillDay) {
            String clientName = salesContractDto.getClientName();  // 客户名称
            String businessType = salesContractDto.getBusinessType();  // 业务类型
            String billType = salesContractDto.getBillType();  // 账单类型
            String employeeNature = getEmployeeNature(businessType);  // 员工性质
            List<EmployeeFilesDto> employeeFilesDto = employeeFilesService.getEmployeeFilesCanGenerateBillByClientName(clientName, employeeNature);
            generateBill(billType, employeeFilesDto, salesContractDto);  // 生成账单数据
        }
        return null;
    }

    /**
     * @param billType          :         账单类型 预收，预估，实收
     * @param employeeFilesDto  : 员工档案信息
     * @param salesContractDto: 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:01
     * @return: void
     * @Description: 生成员工账单
     */
    private void generateBill(String billType, List<EmployeeFilesDto> employeeFilesDto, SalesContractDto salesContractDto) {
        switch (billType) {
            case "预收":
                generateAdvanceReceiptBillData(employeeFilesDto, salesContractDto);
                break;
            case "预估":
                generateEstimateBillData(employeeFilesDto, salesContractDto);
                break;
        }

    }

    /**
     * @param employeeFilesDtos : 员工档案
     * @param salesContractDto  : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:04
     * @return: void
     * @Description: 生成预估数据
     */
    private void generateEstimateBillData(List<EmployeeFilesDto> employeeFilesDtos, SalesContractDto salesContractDto) {
        // 预估下月的数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String billYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1); // 账单年月
        calendar.add(Calendar.MONTH, 1);
        String businessYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);  // 业务年月
        for (EmployeeFilesDto employeeFilesDto : employeeFilesDtos) {
            Bill bill = new Bill();
            setBillBasicData(bill, employeeFilesDto, billYear, businessYear);
            setBillOtherData(bill, employeeFilesDto, salesContractDto, calendar);
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
            } else if (productName.contains("公积金") && !productName.contains("补充")) {
                // 生成公积金数据
                generateProvident(bill, socialSecurityFundDetail, calendar);
            } else if (productName.contains("公积金") && productName.contains("补充")) {
                // 生成补充公积金数据
                generateBProvident(bill, socialSecurityFundDetail, calendar);
            }
        }
        generateOtherData(bill);
    }

    private void generateOtherData(Bill bill) {
        bill.setSocialSecurityTotal(bill.getSocialSecurityEnterprise() + bill.getSocialSecurityPersonal());
        bill.setProvidentTotal(bill.getProvidentEnterprise() + bill.getProvidentPersonal());
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
//        Date end = socialSecurityFundDetail.getEndChargeTime();
//        Calendar endTime = Calendar.getInstance();
//        endTime.setTime(end);
//        endTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), 1);
//        if (null != endTime && !canGenerateData(startTime, endTime)) {
//            // 无需判断
//            return 1;
//        }
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
     * @param startTime: 开始时间
     * @param endTime:   结束时间
     * @Author: wangyong
     * @Date: 2020/3/19 0:11
     * @return: boolean
     * @Description: 判断能否生成数据
     */
    private boolean canGenerateData(Calendar startTime, Calendar endTime) {
        return endTime.getTimeInMillis() - startTime.getTimeInMillis() >= 0 ? true : false;
    }

    /**
     * @param year:  年
     * @param month: 月
     * @Author: wangyong
     * @Date: 2020/3/18 23:29
     * @return: java.lang.String
     * @Description: 将年月组合成字符串
     */
    private String getYearAndMonth(Integer year, Integer month) {
        return month < 10 ? year.intValue() + "0" + month.intValue() : year.intValue() + "" + month.intValue();
    }

    /**
     * @param bill:             账单
     * @param employeeFilesDto: 员工档案
     * @param billYear:         账单年月
     * @param businessYear:     业务年月
     * @Author: wangyong
     * @Date: 2020/3/18 23:26
     * @return: void
     * @Description: 设置账单基本信息
     */
    private void setBillBasicData(Bill bill, EmployeeFilesDto employeeFilesDto, String billYear, String businessYear) {
        bill.setBillYear(billYear);  // 账单年月
        bill.setBusinessYear(businessYear);  // 业务年月
        bill.setEmployeeName(employeeFilesDto.getEmployeeName());  // 雇员姓名
        bill.setIdNo(employeeFilesDto.getIdNo());  // 证件号
        bill.setDelegatedArea(employeeFilesDto.getSocialSecurityArea());  // 委派地区
    }


    /**
     * @param employeeFilesDtos : 员工档案
     * @param salesContractDto  : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:03
     * @return: void
     * @Description: 生成预收数据
     */
    private void generateAdvanceReceiptBillData(List<EmployeeFilesDto> employeeFilesDtos, SalesContractDto salesContractDto) {
        // 预收从当月开始
        String billCycle = salesContractDto.getBillCycle();
        int monthNum = getMonthNum(billCycle);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String billYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1); // 账单年月

        for (EmployeeFilesDto employeeFilesDto : employeeFilesDtos) {
            calendar.setTime(new Date());
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
            for (int i = 1; i <= monthNum; i++) {
                String businessYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + i);  // 业务年月
                Bill bill = new Bill();
                setBillBasicData(bill, employeeFilesDto, billYear, businessYear);
                setBillOtherData(bill, employeeFilesDto, salesContractDto, calendar);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + i, 1);
            }
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
}
