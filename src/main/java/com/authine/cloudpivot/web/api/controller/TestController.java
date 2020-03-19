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
        List<SalesContractDto> salesContractByBillDay = salesContractService.getSalesContractByBillDay(bill);

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
        String billYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)); // 账单年月
        calendar.add(Calendar.MONTH, 1);
        String businessYear = getYearAndMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));  // 业务年月
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
        EmployeeOrderFormDto employeeOrderFormDto = employeeFilesDto.getEmployeeOrderFormDtos().get(0);

        // 社保公积金详情
        List<SocialSecurityFundDetail> socialSecurityFundDetails = employeeOrderFormDto.getSocialSecurityFundDetails();

        for (SocialSecurityFundDetail socialSecurityFundDetail : socialSecurityFundDetails) {
            String productName = socialSecurityFundDetail.getNameHide();
            if (productName.contains("养老")) {
                // 生成养老保险数据
                generatePensionEnterprise(bill, socialSecurityFundDetail, calendar);
            } else if (productName.contains("医疗") && !productName.contains("大病")) {
                // 生成医疗保险数据
            } else if (productName.contains("失业")) {
                // 生成失业保险数据
            } else if (productName.contains("工伤") && !productName.contains("补充")) {
                // 生成工伤保险数据
            } else if (productName.contains("医疗") && productName.contains("大病")) {
                // 生成大病医疗保险数据
            } else if (productName.contains("综合")) {
                // 生成综合保险数据
            } else if (productName.contains("工伤") && productName.contains("补充")) {
                // 生成补充工伤保险数据
            } else if (productName.contains("公积金") && !productName.contains("补充")) {
                // 生成公积金数据
            } else if (productName.contains("公积金") && productName.contains("补充")) {
                // 生成补充公积金数据
            }
        }
    }

    /**
     * @param bill                     : 账单
     * @param socialSecurityFundDetail : 养老保险明细
     * @param startTime                : 开始时间
     * @Author: wangyong
     * @Date: 2020/3/18 23:53
     * @return: int
     * @Description: 生成养老保险数据
     */
    private int generatePensionEnterprise(Bill bill, SocialSecurityFundDetail socialSecurityFundDetail, Calendar startTime) {
        Date end = socialSecurityFundDetail.getEndChargeTime();
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        endTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), 1);
        if (null != endTime && !canGenerateData(startTime, endTime)) {
            // 无需判断
            return 1;
        }
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
        return 0;
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
     * @param employeeFilesDto : 员工档案
     * @param salesContractDto : 销售合同
     * @Author: wangyong
     * @Date: 2020/3/18 23:03
     * @return: void
     * @Description: 生成预收数据
     */
    private void generateAdvanceReceiptBillData(List<EmployeeFilesDto> employeeFilesDto, SalesContractDto salesContractDto) {

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
