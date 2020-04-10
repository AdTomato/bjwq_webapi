package com.authine.cloudpivot.web.api.controller;


import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.EnquiryReceivableDto;
import com.authine.cloudpivot.web.api.entity.Bill;
import com.authine.cloudpivot.web.api.service.EnquiryReceivableService;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: weiyao
 * @Date: 2020-04-03
 * @Description: 导出外企账单
 */
@RestController
@RequestMapping("/controller/getEnquiryExcel")
public class EnquiryReceivableEcxelController  extends BaseController {

    @Autowired
    EnquiryReceivableService enquiryReceivableService;


    //导出付款通知书和账单明细
    @GetMapping("/getExcel")
    public void getExcel(HttpServletResponse response) throws IOException {
        List<String> idss = Arrays.asList("ad8a699cfc6d457db03a96dce1d1d92a","ad8a699cfc6d457db03a96dce1d1d92b",
                "ad8a699cfc6d457db03a96dce1d1d92c","ad8a699cfc6d457db03a96dce1d1d92d");
        List<EnquiryReceivableDto> enquiryReceivableDtoByIds = enquiryReceivableService.getEnquiryReceivableDtoByIds(idss);

        //付款通知书
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("付款通知书");

        //付款通知书高宽
        sheet.setDefaultRowHeight((short)(480));
        sheet.setDefaultColumnWidth(15);
        //第一行‘北 京 外 企 人 力 资 源 服  务 安 徽 有  限  公  司’
        XSSFRow row = sheet.createRow(0);
        creatRow(row,"北京外企人力资源服务安徽有限公司");
        //单元格合并
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        XSSFRow row2 = sheet.createRow(1);
        creatRow(row2,"Beijing  Foreign  Enterprise  Human  Resources  Service  Anhui  Co.,Ltd");
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));

        XSSFFont f  = workbook.createFont();
        f.setFontHeightInPoints((short) 14);//字号
        f.setBold(true);//加粗
        setCellStyle(workbook,row,f);
        setCellStyle(workbook,row2,f);

        XSSFRow row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("客户名称");

        XSSFRow row4 = sheet.createRow(3);
        String tit4=" 收费通知单";
        row4.createCell(0).setCellValue(tit4);
        row4.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));

        XSSFRow row5 = sheet.createRow(4);
        creatRow5(row5);

        int i=5;
        //账单年月years
        StringBuffer years=new StringBuffer();
        //业务员
        String name="";
        //excel实体
        if(enquiryReceivableDtoByIds!=null && enquiryReceivableDtoByIds.size()>0){

            //付款通知书body
            int hd=creatEnqueryBody(sheet,enquiryReceivableDtoByIds,i,years);
            i=i+hd;
            //设置客户名称
            row3.createCell(1).setCellValue(enquiryReceivableDtoByIds.get(0).getBusinessCustomerName());
            //设置账单年月
            row4.getCell(0).setCellValue(years.toString()+tit4);

            //设置业务员姓名
            if(StringUtils.isNotBlank(enquiryReceivableDtoByIds.get(0).getSalesman())){
                //JSONArray获取以【】开头的字符串
                JSONArray jsonArray = JSONArray.fromObject(enquiryReceivableDtoByIds.get(0).getSalesman());
                UserModel user=this.getOrganizationFacade().getUserById(jsonArray.getJSONObject(0).getString("id"));
                name=user.getName();
            }
     //       name=enquiryReceivableDtoByIds.get(0).getSalesman();

        }
        //尾部
        XSSFRow row7 = sheet.createRow(i);
        creatRow(row7,"注：根据账单明细汇总当月费用，若金额存在异议，请及时与业务员联络核实。");
        sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
        setEnqueryEnd(sheet,i,name);


        //账单明细
        XSSFSheet sheet2 = workbook.createSheet("账单明细");
        //设置高宽
        sheet2.setDefaultRowHeight((short)380);
        sheet2.setDefaultColumnWidth(10);
        XSSFRow row21 = sheet2.createRow(0);
        //账单明细头部
        creatRow21(row21);
        //账单明细Body
        creatDetailBody(sheet2,enquiryReceivableDtoByIds);

        response.addHeader("Content-Disposition", "attachment;filename=" + new String("账单表.xlsx".getBytes("gbk"), "iso8859-1"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * @Author: 魏姚
     * @Date: 2020/4/03
     * @Description: 创建11行
     */
    private void creatRow(XSSFRow row,String name) {
        row.createCell(0).setCellValue(name);
        row.createCell(1);
        row.createCell(2);
        row.createCell(3);
        row.createCell(4);
        row.createCell(5);
        row.createCell(6);
        row.createCell(7);
        row.createCell(8);
        row.createCell(9);
        row.createCell(10);
    }

    private void creatRow5(XSSFRow row) {
        row.createCell(0).setCellValue("账单月");
        row.createCell(1).setCellValue("代收社保合计");
        row.createCell(2).setCellValue("代收付公积金合计");
        row.createCell(3).setCellValue("一次性服务费合计");
        row.createCell(4).setCellValue("福利产品合计");
        row.createCell(5).setCellValue("服务费合计");
        row.createCell(6).setCellValue("增值税税费合计");
        row.createCell(7).setCellValue("风险管理费合计");
        row.createCell(8).setCellValue("外包管理费合计");
        row.createCell(9).setCellValue("营业税税费合计");
        row.createCell(10).setCellValue("总计");
    }

    //创建账单明细 数据体
    private void creatDetailBody( XSSFSheet sheet,List<EnquiryReceivableDto> EnquiryReceivableDtoList) {
        if(EnquiryReceivableDtoList.size()>0 ){
            int i=1;
            for(EnquiryReceivableDto rec :EnquiryReceivableDtoList){
                List<Bill> bilList=rec.getBills();
                if(bilList !=null && bilList.size()>0){
                    for (Bill bill:bilList){
                        //第二行开始创建
                        XSSFRow row2=sheet.createRow(i);
                        creatRow22(row2,bill);
                        i++;
                    }
                }
            }

        }

    }

    //账单明细第一行（119列）
    private void creatRow21(XSSFRow row) {
        row.createCell(0).setCellValue("账单年月");
        row.createCell(1).setCellValue("委托单位");
        row.createCell(2).setCellValue("客户名称");
        row.createCell(3).setCellValue("业务年月");
        row.createCell(4).setCellValue("雇员姓名");
        row.createCell(5).setCellValue("雇员证件号码");
        row.createCell(6).setCellValue("委派地区");
        row.createCell(7).setCellValue("养老企业基数");
        row.createCell(8).setCellValue("养老企业比例");
        row.createCell(9).setCellValue("养老企业附加");
        row.createCell(10).setCellValue("养老企业缴纳");
        row.createCell(11).setCellValue("养老个人基数");
        row.createCell(12).setCellValue("养老个人比例");
        row.createCell(13).setCellValue("养老个人附加");
        row.createCell(14).setCellValue("养老个人缴纳");
        row.createCell(15).setCellValue("养老缴纳小计");
        row.createCell(16).setCellValue("养老支付方式");
        row.createCell(17).setCellValue("医疗企业基数");
        row.createCell(18).setCellValue("医疗企业比例");
        row.createCell(19).setCellValue("医疗企业附加");
        row.createCell(20).setCellValue("医疗企业缴纳");
        row.createCell(21).setCellValue("医疗个人基数");
        row.createCell(22).setCellValue("医疗个人比例");
        row.createCell(23).setCellValue("医疗个人附加");
        row.createCell(24).setCellValue("医疗个人缴纳");
        row.createCell(25).setCellValue("医疗缴纳小计");
        row.createCell(26).setCellValue("医疗支付方式");
        row.createCell(27).setCellValue("失业企业基数");
        row.createCell(28).setCellValue("失业企业比例");
        row.createCell(29).setCellValue("失业企业附加");
        row.createCell(30).setCellValue("失业企业缴纳");
        row.createCell(31).setCellValue("失业个人基数");
        row.createCell(32).setCellValue("失业个人比例");
        row.createCell(33).setCellValue("失业个人附加");
        row.createCell(34).setCellValue("失业个人缴纳");
        row.createCell(35).setCellValue("失业缴纳小计");
        row.createCell(36).setCellValue("失业支付方式");
        row.createCell(37).setCellValue("工伤企业基数");
        row.createCell(38).setCellValue("工伤企业比例");
        row.createCell(39).setCellValue("工伤企业附加");
        row.createCell(40).setCellValue("工伤企业缴纳");
        row.createCell(41).setCellValue("工伤缴纳小计");
        row.createCell(42).setCellValue("工伤支付方式");
        row.createCell(43).setCellValue("生育企业基数");
        row.createCell(44).setCellValue("生育企业比例");
        row.createCell(45).setCellValue("生育企业附加");
        row.createCell(46).setCellValue("生育企业缴纳");
        row.createCell(47).setCellValue("生育缴纳小计");
        row.createCell(48).setCellValue("生育支付方式");
        row.createCell(49).setCellValue("大病医疗企业基数");
        row.createCell(50).setCellValue("大病医疗企业比例");
        row.createCell(51).setCellValue("大病医疗企业附加");
        row.createCell(52).setCellValue("大病医疗企业缴纳");
        row.createCell(53).setCellValue("大病医疗个人基数");
        row.createCell(54).setCellValue("大病医疗个人比例");
        row.createCell(55).setCellValue("大病医疗个人附加");
        row.createCell(56).setCellValue("大病医疗个人缴纳");
        row.createCell(57).setCellValue("大病医疗缴纳小计");
        row.createCell(58).setCellValue("大病支付方式");
        row.createCell(59).setCellValue("综合企业基数");
        row.createCell(60).setCellValue("综合企业比例");
        row.createCell(61).setCellValue("综合企业附加");
        row.createCell(62).setCellValue("综合企业缴纳");
        row.createCell(63).setCellValue("综合个人基数");
        row.createCell(64).setCellValue("综合个人比例");
        row.createCell(65).setCellValue("综合个人附加");
        row.createCell(66).setCellValue("综合个人缴纳");
        row.createCell(67).setCellValue("综合缴纳小计");
        row.createCell(68).setCellValue("综合支付方式");
        row.createCell(69).setCellValue("补充工伤企业基数");
        row.createCell(70).setCellValue("补充工伤企业比例");
        row.createCell(71).setCellValue("补充工伤企业附加");
        row.createCell(72).setCellValue("补充工伤企业缴纳");
        row.createCell(73).setCellValue("补充工伤支付方式");
        row.createCell(74).setCellValue("社保企业缴纳合计");
        row.createCell(75).setCellValue("社保个人缴纳合计");
        row.createCell(76).setCellValue("社保缴纳合计");
        row.createCell(77).setCellValue("公积金企业基数");
        row.createCell(78).setCellValue("公积金企业比例");
        row.createCell(79).setCellValue("公积金企业缴纳");
        row.createCell(80).setCellValue("公积金个人基数");
        row.createCell(81).setCellValue("公积金个人比例");
        row.createCell(82).setCellValue("公积金个人缴纳");
        row.createCell(83).setCellValue("公积金缴纳小计");
        row.createCell(84).setCellValue("住房支付方式");
        row.createCell(85).setCellValue("补充公积金企业基数");
        row.createCell(86).setCellValue("补充公积金企业比例");
        row.createCell(87).setCellValue("补充公积金企业缴纳");
        row.createCell(88).setCellValue("补充公积金个人基数");
        row.createCell(89).setCellValue("补充公积金个人比例");
        row.createCell(90).setCellValue("补充公积金个人缴纳");
        row.createCell(91).setCellValue("补充公积金缴纳小计");
        row.createCell(92).setCellValue("补充住房支付方式");
        row.createCell(93).setCellValue("公积金企业缴纳合计");
        row.createCell(94).setCellValue("公积金个人缴纳合计");
        row.createCell(95).setCellValue("公积金缴纳合计");
        row.createCell(96).setCellValue("一次性收费合计");
        row.createCell(97).setCellValue("一次性收费备注");
        row.createCell(98).setCellValue("一次性社保企业-代收代付");
        row.createCell(99).setCellValue("一次性社保个人-代收代付");
        row.createCell(100).setCellValue("一次性社保合计-代收代付");
        row.createCell(101).setCellValue("一次性公积金企业-代收代付");
        row.createCell(102).setCellValue("一次性公积金个人-代收代付");
        row.createCell(103).setCellValue("一次性公积金合计-代收代付");
        row.createCell(104).setCellValue("一次性社保企业-托收");
        row.createCell(105).setCellValue("一次性社保个人-托收");
        row.createCell(106).setCellValue("一次性社保合计-托收");
        row.createCell(107).setCellValue("一次性公积金企业-托收");
        row.createCell(108).setCellValue("一次性公积金个人-托收");
        row.createCell(109).setCellValue("一次性公积金合计-托收");
        row.createCell(110).setCellValue("社保公积金合计");
        row.createCell(111).setCellValue("服务费");
        row.createCell(112).setCellValue("增值税税费");
        row.createCell(113).setCellValue("福利产品总额");
        row.createCell(114).setCellValue("风险管理费");
        row.createCell(115).setCellValue("外包管理费");
        row.createCell(116).setCellValue("营业税税费");
        row.createCell(117).setCellValue("合计收费");
        row.createCell(118).setCellValue("费用类型");
    }

    //账单明细第二行（119列实体）
    private void creatRow22(XSSFRow row,Bill bill) {
        row.createCell(0).setCellValue(bill.getBillYear());
        row.createCell(1).setCellValue("北京外企人力资源服务安徽有限公司");
        row.createCell(2).setCellValue(StringUtils.trimToNull(bill.getClientName()));
        row.createCell(3).setCellValue(StringUtils.trimToNull(bill.getBusinessYear()) );
        row.createCell(4).setCellValue(StringUtils.trimToNull(bill.getEmployeeName()) );
        row.createCell(5).setCellValue(StringUtils.trimToNull(bill.getIdNo()));
        row.createCell(6).setCellValue(StringUtils.trimToNull(bill.getDelegatedArea()) );
        row.createCell(7).setCellValue(doubleCheckNull(bill.getPensionEnterpriseBase()) );
        row.createCell(8).setCellValue(doubleCheckNull(bill.getPensionEnterpriseRatio()));
        row.createCell(9).setCellValue(doubleCheckNull(bill.getPensionEnterpriseAttach()) );
        row.createCell(10).setCellValue(doubleCheckNull(bill.getPensionEnterprisePay()) );
        row.createCell(11).setCellValue(doubleCheckNull(bill.getPensionPersonalBase()) );
        row.createCell(12).setCellValue(doubleCheckNull(bill.getPensionPersonalRatio()) );
        row.createCell(13).setCellValue(doubleCheckNull(bill.getPensionPersonalAttach()) );
        row.createCell(14).setCellValue(doubleCheckNull(bill.getPensionPersonalPay()) );
        row.createCell(15).setCellValue(doubleCheckNull(bill.getPensionSubtotal()) );//养老缴纳小计
        row.createCell(16).setCellValue(StringUtils.trimToNull(bill.getPensionPaymentMethod()) );
        row.createCell(17).setCellValue(doubleCheckNull(bill.getMedicalEnterpriseBase()) );
        row.createCell(18).setCellValue(doubleCheckNull(bill.getMedicalEnterpriseRatio()) );
        row.createCell(19).setCellValue(doubleCheckNull(bill.getMedicalEnterpriseAttach() ));
        row.createCell(20).setCellValue(doubleCheckNull(bill.getMedicalEnterprisePay()));
        row.createCell(21).setCellValue(doubleCheckNull(bill.getMedicalPersonalBase()) );
        row.createCell(22).setCellValue(doubleCheckNull(bill.getMedicalPersonalRatio() ));
        row.createCell(23).setCellValue(doubleCheckNull(bill.getMedicalPersonalAttach()) );
        row.createCell(24).setCellValue(doubleCheckNull(bill.getMedicalPersonalPay()) );
        row.createCell(25).setCellValue(doubleCheckNull(bill.getMedicalSubtotal()) );//医疗缴纳小计
        row.createCell(26).setCellValue(StringUtils.trimToNull(bill.getMedicalPaymentMethod()) );
        row.createCell(27).setCellValue(doubleCheckNull(bill.getUnempEnterpriseBase()) );
        row.createCell(28).setCellValue(doubleCheckNull(bill.getUnempEnterpriseRatio()) );
        row.createCell(29).setCellValue(doubleCheckNull(bill.getUnempEnterpriseAttach()));
        row.createCell(30).setCellValue(doubleCheckNull(bill.getUnempEnterprisePay()));
        row.createCell(31).setCellValue(doubleCheckNull(bill.getUnempPersonalBase()) );
        row.createCell(32).setCellValue(doubleCheckNull(bill.getUnempPersonalRatio())  );
        row.createCell(33).setCellValue(doubleCheckNull(bill.getUnempPersonalAttach()));
        row.createCell(34).setCellValue(doubleCheckNull(bill.getUnempPersonalPay()) );
        row.createCell(35).setCellValue(doubleCheckNull(bill.getUnempSubtotal()) );//失业缴纳小计
        row.createCell(36).setCellValue(StringUtils.trimToNull(bill.getUnempPaymentMethod()) );
        row.createCell(37).setCellValue(doubleCheckNull(bill.getInjuryEnterpriseBase()) );
        row.createCell(38).setCellValue(doubleCheckNull(bill.getInjuryEnterpriseRatio()));
        row.createCell(39).setCellValue(doubleCheckNull(bill.getInjuryEnterpriseAttach()));
        row.createCell(40).setCellValue(doubleCheckNull(bill.getInjuryEnterprisePay()) );
        row.createCell(41).setCellValue(doubleCheckNull(bill.getInjurySubtotal()));//工伤缴纳小计
        row.createCell(42).setCellValue(StringUtils.trimToNull(bill.getInjuryPaymentMethod()) );
        row.createCell(43).setCellValue(doubleCheckNull(bill.getFertilityEnterpriseBase()));
        row.createCell(44).setCellValue(doubleCheckNull(bill.getFertilityEnterpriseRatio()));
        row.createCell(45).setCellValue(doubleCheckNull(bill.getFertilityEnterpriseAttach()) );
        row.createCell(46).setCellValue(doubleCheckNull(bill.getFertilityEnterprisePay()) );
        row.createCell(47).setCellValue(doubleCheckNull(bill.getFertilitySubtotal()) );//生育缴纳小计
        row.createCell(48).setCellValue(StringUtils.trimToNull(bill.getFertilityPaymentMethod()));
        row.createCell(49).setCellValue(doubleCheckNull(bill.getDMedicalEnterpriseBase()) );
        row.createCell(50).setCellValue(doubleCheckNull(bill.getDMedicalEnterpriseRatio()) );
        row.createCell(51).setCellValue(doubleCheckNull(bill.getDMedicalEnterpriseAttach() ));
        row.createCell(52).setCellValue(doubleCheckNull(bill.getDMedicalEnterprisePay()));
        row.createCell(53).setCellValue(doubleCheckNull(bill.getDMedicalPersonalBase()) );
        row.createCell(54).setCellValue(doubleCheckNull(bill.getDMedicalPersonalRatio() ));
        row.createCell(55).setCellValue(doubleCheckNull(bill.getDMedicalPersonalAttach()) );
        row.createCell(56).setCellValue(doubleCheckNull(bill.getDMedicalPersonalPay()) );
        row.createCell(57).setCellValue(doubleCheckNull(bill.getDMedicalSubtotal()) );//大病医疗缴纳小计
        row.createCell(58).setCellValue(StringUtils.trimToNull(bill.getDMedicalPaymentMethod()) );
        row.createCell(59).setCellValue(doubleCheckNull(bill.getComplexEnterpriseBase() ));
        row.createCell(60).setCellValue(doubleCheckNull(bill.getComplexEnterpriseRatio() ) );
        row.createCell(61).setCellValue(doubleCheckNull(bill.getComplexEnterpriseAttach() ) );
        row.createCell(62).setCellValue(doubleCheckNull(bill.getComplexEnterprisePay() ) );
        row.createCell(63).setCellValue(doubleCheckNull(bill.getComplexPersonalBase() ) );
        row.createCell(64).setCellValue(doubleCheckNull(bill.getComplexPersonalRatio() ) );
        row.createCell(65).setCellValue(doubleCheckNull(bill.getComplexPersonalAttach() ) );
        row.createCell(66).setCellValue(doubleCheckNull(bill.getComplexPersonalPay() ) );
        row.createCell(67).setCellValue(doubleCheckNull(bill.getComplexSubtotal() ) );//综合缴纳小计
        row.createCell(68).setCellValue(StringUtils.trimToNull(bill.getComplexPaymentMethod()) );
        row.createCell(69).setCellValue(doubleCheckNull(bill.getBInjuryEnterpriseBase() ) );
        row.createCell(70).setCellValue(doubleCheckNull(bill.getBInjuryEnterpriseRatio() ) );
        row.createCell(71).setCellValue(doubleCheckNull(bill.getBInjuryEnterpriseAttach() ) );
        row.createCell(72).setCellValue(doubleCheckNull(bill.getBInjuryEnterprisePay() ) );
        row.createCell(73).setCellValue(StringUtils.trimToNull(bill.getBInjuryPaymentMethod() ) );//补充工伤支付方式
        row.createCell(74).setCellValue(doubleCheckNull(bill.getSocialSecurityPersonal() ) );
        row.createCell(75).setCellValue(doubleCheckNull(bill.getSocialSecurityPersonal() ) );
        row.createCell(76).setCellValue(doubleCheckNull(bill.getSocialSecurityTotal() ) );//社保合计
        row.createCell(77).setCellValue(doubleCheckNull(bill.getProvidentEnterpriseBase() ) );
        row.createCell(78).setCellValue(doubleCheckNull(bill.getProvidentEnterpriseRatio() ) );
        row.createCell(79).setCellValue(doubleCheckNull(bill.getProvidentEnterprisePay() ) );
        row.createCell(80).setCellValue(doubleCheckNull(bill.getProvidentPersonalBase() ) );
        row.createCell(81).setCellValue(doubleCheckNull(bill.getProvidentPersonalRatio() ) );
        row.createCell(82).setCellValue(doubleCheckNull(bill.getProvidentPersonalPay() ) );
        row.createCell(83).setCellValue(doubleCheckNull(bill.getProvidentSubtotal() ) );//公积金缴纳小计
        row.createCell(84).setCellValue(StringUtils.trimToNull(bill.getProvidentPaymentMethod()) );
        row.createCell(85).setCellValue(doubleCheckNull(bill.getBProvidentEnterpriseBase() ) );
        row.createCell(86).setCellValue(doubleCheckNull(bill.getBProvidentEnterpriseRatio() ) );
        row.createCell(87).setCellValue(doubleCheckNull(bill.getBProvidentEnterprisePay() ) );
        row.createCell(88).setCellValue(doubleCheckNull(bill.getBProvidentPersonalBase()) );
        row.createCell(89).setCellValue(doubleCheckNull(bill.getBProvidentPersonalRatio() ) );
        row.createCell(90).setCellValue(doubleCheckNull(bill.getBProvidentPersonalPay() ) );
        row.createCell(91).setCellValue(doubleCheckNull(bill.getBProvidentSubtotal() ) );//补充公积金缴纳小计
        row.createCell(92).setCellValue(StringUtils.trimToNull(bill.getBProvidentPaymentMethod() ) );
        row.createCell(93).setCellValue(doubleCheckNull(bill.getProvidentEnterprise() ) );
        row.createCell(94).setCellValue(doubleCheckNull(bill.getProvidentPersonal() ) );
        row.createCell(95).setCellValue(doubleCheckNull(bill.getProvidentTotal() ) );//===公积金缴纳合计
        row.createCell(96).setCellValue(doubleCheckNull(bill.getYTollTotal() ) );
        row.createCell(97).setCellValue(StringUtils.trimToNull(bill.getYTollRemark() ) );
        row.createCell(98).setCellValue(doubleCheckNull(bill.getYSocialEnterpriseD() ) );
        row.createCell(99).setCellValue(doubleCheckNull(bill.getYSocialPersonalD() ) );
        row.createCell(100).setCellValue(doubleCheckNull(bill.getYSocialTotalD() ) );
        row.createCell(101).setCellValue(doubleCheckNull(bill.getYProvidentEnterpriseD() ) );
        row.createCell(102).setCellValue(doubleCheckNull(bill.getYProvidentPersonalD() ) );
        row.createCell(103).setCellValue(doubleCheckNull(bill.getYProvidentTotalD() ) );
        row.createCell(104).setCellValue(doubleCheckNull(bill.getYSocialEnterpriseT() ) );
        row.createCell(105).setCellValue(doubleCheckNull(bill.getYSocialPersonalT() ) );
        row.createCell(106).setCellValue(doubleCheckNull(bill.getYSocialTotalT()) );
        row.createCell(107).setCellValue(doubleCheckNull(bill.getYProvidentEnterpriseT() ) );
        row.createCell(108).setCellValue(doubleCheckNull(bill.getYProvidentPersonalT() ) );
        row.createCell(109).setCellValue(doubleCheckNull(bill.getYProvidentTotalT() ) );
        row.createCell(110).setCellValue(doubleCheckNull(bill.getSocialProvidentTotal() ) );//===社保公积金合计
        row.createCell(111).setCellValue(doubleCheckNull(bill.getServiceFee() ) );
        row.createCell(112).setCellValue(doubleCheckNull(bill.getVatTax() ) );
        row.createCell(113).setCellValue(doubleCheckNull(bill.getTotalWelfareProducts() ) );//福利产品总额
        row.createCell(114).setCellValue(doubleCheckNull(bill.getRiskManageFee() ) );
        row.createCell(115).setCellValue(doubleCheckNull(bill.getOutsourcingManageFee() ) );
        row.createCell(116).setCellValue(doubleCheckNull(bill.getBusinessTax() ) );
        row.createCell(117).setCellValue(doubleCheckNull(bill.getTotalCharge() ) );
        row.createCell(118).setCellValue(StringUtils.trimToNull(bill.getChargeType() ) );
    }

    //创建付款通知书结尾
    private void setEnqueryEnd(XSSFSheet sheet,int i,String name){
        i=i+2;
        XSSFRow row9 = sheet.createRow(i);
        row9.createCell(0).setCellValue("汇入公司名称");
        row9.createCell(1).setCellValue("北京外企人力资源服务安徽有限公司");
        row9.createCell(2);
        row9.createCell(3);
        i++;
        XSSFRow row10 = sheet.createRow(i);
        row10.createCell(0).setCellValue("开户账号：");
        row10.createCell(1).setCellValue("招商银行肥西路支行");
        row10.createCell(2);
        row10.createCell(3);
        i++;
        XSSFRow row11 = sheet.createRow(i);
        row11.createCell(0).setCellValue("开户银行：");
        row11.createCell(1).setCellValue("551903590910602");
        row11.createCell(2);
        row11.createCell(3);
        i++;
        XSSFRow row12 = sheet.createRow(i);
        row12.createCell(0).setCellValue("业务员：");
        row12.createCell(1).setCellValue(name);
        row12.createCell(2);
        row12.createCell(3);
        i++;
        XSSFRow row13 = sheet.createRow(i);
        row13.createCell(0).setCellValue("联系方式：");
        row13.createCell(1).setCellValue("0551-65222899");
        row13.createCell(2);
        row13.createCell(3);
        i++;
    //    sheet.addMergedRegion(new CellRangeAddress(i, i+4, 1, 3));
    }

        //创建数据体
    private int creatEnqueryBody( XSSFSheet sheet,List<EnquiryReceivableDto> EnquiryReceivableDtoList,int i,StringBuffer years) {
        //月份
        String month=new String();
        String year=new String();

        List<EnquiryReceivableDto> recList=new ArrayList<>();
        EnquiryReceivableDto enq=removeNull(EnquiryReceivableDtoList.get(0));

        if(EnquiryReceivableDtoList.size()==1){
            //只有一行
//            String   month1=removeStarZero(enq.getBillYear().substring(4));
//            enq.setBillYear(month1);
            recList.add(enq);
        }
        //同年同月汇总
        for(int j=1;j< EnquiryReceivableDtoList.size();j++) {
                if(enq.getBillYear().equals(EnquiryReceivableDtoList.get(j).getBillYear())){
                    enq=checkAB(enq,EnquiryReceivableDtoList.get(j));
                }else{
                    recList.add(enq);
                    enq=EnquiryReceivableDtoList.get(j);
                }
                if(j==EnquiryReceivableDtoList.size()-1 ){
                recList.add(enq);
            }
        }
        //创建excel
        for(EnquiryReceivableDto list :recList){
                //账单年月
                if (StringUtils.isNotBlank(list.getBillYear()) && list.getBillYear().length() > 4) {
                    //获得年份
                    //年份不相等
                    if (!year.equals(list.getBillYear().substring(0, 4))) {
                        year = list.getBillYear().substring(0, 4);
                        years.append(year).append("年 ");
                        //年分不等肯定累加月份
                        month = removeStarZero(list.getBillYear().substring(4));
                        years.append(month).append("月,");

                    } else {
                        //年份相等，加月份
                        month = removeStarZero(list.getBillYear().substring(4));
                        years.append(month).append("月,");
                    }
                }
            XSSFRow row6 = sheet.createRow(i);
            i++;
            //先创建第六行
            setRow6(row6,list,month);
            }
     return recList.size();
    }

    private  void setRow6(XSSFRow row,EnquiryReceivableDto to,String month){
        //账单月
    //    String   month=removeStarZero(to.getBillYear().substring(4));
        row.createCell(0).setCellValue(month+"月");
        //代收付社保合计
        row.createCell(1).setCellValue(doubleCheckNull(to.getSocialSecurityTotal()));
        //"代收付公积金合计"
        row.createCell(2).setCellValue(doubleCheckNull(to.getAccumulationFundTotal()));
        //"一次性服务费合计"
        row.createCell(3).setCellValue(doubleCheckNull(to.getServiceChargeOnce()));
        //"福利产品合计"
        row.createCell(4).setCellValue(doubleCheckNull(to.getWelfareProductsTotal()));
        //"服务费合计"
        row.createCell(5).setCellValue(doubleCheckNull(to.getServiceCharge()));
        //"增值税税费合计"
        row.createCell(6).setCellValue(doubleCheckNull(to.getValueAddedTaxTotal()));
        //"风险管理费合计"
        row.createCell(7).setCellValue(doubleCheckNull(to.getRiskManagementFee()));
        //"外包管理费合计"
        row.createCell(8).setCellValue(doubleCheckNull(to.getOutsourcingManageFee()));
        //"营业税税费合计"
        row.createCell(9).setCellValue(doubleCheckNull(to.getBusinessTaxesFees()))   ;
        //"总计"
        Double total=
        doubleCheckNull(to.getSocialSecurityTotal())+doubleCheckNull(to.getAccumulationFundTotal())+doubleCheckNull(to.getServiceChargeOnce())+
                doubleCheckNull(to.getWelfareProductsTotal())+doubleCheckNull(to.getServiceCharge())+
                doubleCheckNull(to.getValueAddedTaxTotal())+doubleCheckNull(to.getRiskManagementFee())+
                doubleCheckNull(to.getOutsourcingManageFee())+doubleCheckNull(to.getBusinessTaxesFees());
        row.createCell(10).setCellValue(total);
    }

    //设置边框和居中 ,和字体
    public static void setCellStyle(XSSFWorkbook workbook, XSSFRow row, XSSFFont fon ) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //设置边框
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
        //居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置字体
        cellStyle.setFont(fon);
        row.getCell(0).setCellStyle(cellStyle);
//        for (int i = 0; i < row.getLastCellNum(); i++) {
//            row.getCell(i).setCellStyle(cellStyle);
//        }
    }

    /*
    weiyao
    去除字符串开头的"0"
     */
    private static  String removeStarZero(String month) {
      // 声明一个StringBuffer对象
        StringBuffer sb = new StringBuffer();
     // 设置循环标识
        boolean removeZero = true;
     // 判断入参是否不为空
         if (StringUtils.isNotEmpty(month)) {
     // 对入参进行遍历，将不为0的挨个放入sb中
            int length = month.length();
            for (int i = 0; i < length; i++) {
                char ss = month.charAt(i);
                if (removeZero) {
     // 遇到第一个不为0的字符时，跳出循环
                    if (ss != '0') {
                        sb.append(ss);
                        removeZero = false;
                    }
                } else {
                    sb.append(ss);
                }
            }
        }
        return sb.toString();
    }

    //检验相等
    private EnquiryReceivableDto checkAB(EnquiryReceivableDto A,EnquiryReceivableDto B){
        A.setSocialSecurityTotal(doubleCheckNull(A.getSocialSecurityTotal())+doubleCheckNull(B.getSocialSecurityTotal()));
        A.setAccumulationFundTotal(doubleCheckNull(A.getAccumulationFundTotal())+doubleCheckNull(B.getAccumulationFundTotal()));
        A.setServiceChargeOnce(doubleCheckNull(A.getServiceChargeOnce())+doubleCheckNull(B.getServiceChargeOnce()));
        A.setWelfareProductsTotal(doubleCheckNull(A.getWelfareProductsTotal())+doubleCheckNull(B.getWelfareProductsTotal()));
        A.setServiceCharge(doubleCheckNull(A.getServiceCharge())+doubleCheckNull(B.getServiceCharge()));
        A.setValueAddedTaxTotal(doubleCheckNull(A.getValueAddedTaxTotal())+doubleCheckNull(B.getValueAddedTaxTotal()));
        A.setRiskManagementFee(doubleCheckNull(A.getRiskManagementFee())+doubleCheckNull(B.getRiskManagementFee()));
        A.setOutsourcingManageFee(doubleCheckNull(A.getOutsourcingManageFee())+doubleCheckNull(B.getOutsourcingManageFee()));
        A.setBusinessTaxesFees(doubleCheckNull(A.getBusinessTaxesFees())+doubleCheckNull(B.getBusinessTaxesFees()));
    return  A;
    }

    //去空
    private EnquiryReceivableDto removeNull(EnquiryReceivableDto A){
        A.setSocialSecurityTotal(doubleCheckNull(A.getSocialSecurityTotal()));
        A.setAccumulationFundTotal(doubleCheckNull(A.getAccumulationFundTotal()));
        A.setServiceChargeOnce(doubleCheckNull(A.getServiceChargeOnce()));
        A.setWelfareProductsTotal(doubleCheckNull(A.getWelfareProductsTotal()));
        A.setServiceCharge(doubleCheckNull(A.getServiceCharge()));
        A.setValueAddedTaxTotal(doubleCheckNull(A.getValueAddedTaxTotal()));
        A.setRiskManagementFee(doubleCheckNull(A.getRiskManagementFee()));
        A.setOutsourcingManageFee(doubleCheckNull(A.getOutsourcingManageFee()));
        A.setBusinessTaxesFees(doubleCheckNull(A.getBusinessTaxesFees()));
        return  A;
    }


    private Double doubleCheckNull(Double d) {
        return d == null ? 0D : d;
    }

    private String stringCheckNull(String s) {
        return s == null ? "" : s;
    }

    private Float floatCheckNull(Float f) {
        return f == null ? 0F : f;
    }
}
