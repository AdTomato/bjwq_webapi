package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;
import com.authine.cloudpivot.web.api.params.ImportCondition;
import com.authine.cloudpivot.web.api.service.DeclareStopPaymentService;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import com.authine.cloudpivot.web.api.utils.DateUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.sun.jersey.core.spi.component.ProviderServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.apache.http.annotation.Contract;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.util.ElementScanner6;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName SocialSecurityDeclareController
 * @Author:lfh
 * @Date:2020/3/24 9:54
 * @Description: 社保申报控制类
 **/

@RestController
@RequestMapping("/controller/socialSecurityDeclare")
@Slf4j
public class SocialSecurityDeclareController extends BaseController {

    @Autowired
    private SocialSecurityDeclareService socialSecurityDeclareService;
    @Autowired
    private DeclareStopPaymentService declareStopPaymentService;

    //合同新签导入
    @PostMapping("/importNewContract")
    public void importNewContract(@RequestBody ImportCondition importCondition, HttpServletResponse response) throws IOException, ParseException {
        List<ContractImportInfo> ContractInfos = null;
        ContractInfos = socialSecurityDeclareService.findContractInfo(importCondition);

        if (ContractInfos.isEmpty()) {
            throw new RuntimeException("未查到新签合同信息");
        }
        if (!ContractInfos.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            List<String> timeList = new ArrayList<>();
            for (ContractImportInfo contractInfo : ContractInfos) {
                contractInfo.setNational("汉");
                String contract_start_time = contractInfo.getContract_start_time();
                if (contract_start_time != null) {
                    Date contract_start_timeDate = sdf.parse(contract_start_time);
                    contract_start_time = sdf.format(contract_start_timeDate);
                    contractInfo.setContract_start_time(contract_start_time);
                }
                String contract_end_time = contractInfo.getContract_end_time();
                if (contract_end_time !=null) {
                    Date contract_end_timeDate = sdf.parse(contract_end_time);
                    contract_end_time = sdf.format(contract_end_timeDate);
                    contractInfo.setContract_end_time(contract_end_time);
                }
                String birthday = contractInfo.getBirthday();
                if (birthday != null) {
                    Date birthdayDate = sdf.parse(birthday);
                    birthday = sdf.format(birthdayDate);
                    contractInfo.setBirthday(birthday);
                }
                String start_month = contractInfo.getStart_month();
                if (start_month!=null) {
                    Date start_monthDate = sdf.parse(start_month);
                    start_month = sdf.format(start_monthDate);
                    contractInfo.setStart_month(start_month);
                }
                String owner = declareStopPaymentService.findOwnerById(contractInfo.getOwner());
                contractInfo.setOwner(owner);
            }
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "合同新签导入.xls";
        StringBuilder stringBuilder = new StringBuilder();
        String realPath = stringBuilder.append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        // 不需要标题
        if (!file.exists()) {
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        String[] headers = {"社保福利地", "社保福利办理方", "一级客户名称", "二级客户名称", "业务员","姓名","性别","民族","出生日期","证件号码","户籍性质","学历", "劳动合同合同起始时间", "劳动合同合同终止时间", "社保起做时间","社保基数","用工形式","联系电话","现住址"};
        Workbook book = ExportExcel.exportExcel("sheet1", headers, ContractInfos, realPath, "yyyyMMdd", workbook);
       /* ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.GENDER, 1, book.getSheetAt(0).getLastRowNum(), 2, 2);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.NATION, 1, book.getSheetAt(0).getLastRowNum(), 3, 3);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.ACCOUNT_CHARACTER, 1, book.getSheetAt(0).getLastRowNum(), 6, 6);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.DOMICILE_PLACE, 1, book.getSheetAt(0).getLastRowNum(), 7, 7);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.DOMICILE_PLACE, 1, book.getSheetAt(0).getLastRowNum(), 9, 9);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.PERSON_CATEGORY, 1, book.getSheetAt(0).getLastRowNum(), 12, 12);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.EDUCATION_DEGREE, 1, book.getSheetAt(0).getLastRowNum(), 13, 13);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_TERM_TYPE, 1, book.getSheetAt(0).getLastRowNum(), 17, 17);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.WORK_FORM, 1, book.getSheetAt(0).getLastRowNum(), 19, 19);
        ExportExcel.getDataValidationList4Col(book.getSheet("sheet1"), 1, 14, book.getSheetAt(0).getLastRowNum(), 14, Arrays.asList(Constants.WORK_TYPE), book);*/
        book.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, book);
    }

    //合同解除终止
    @PostMapping("/contractTermination")
    public void contractTermination(@RequestBody ImportCondition importCondition, HttpServletResponse response) throws IOException, ParseException {

        List<ContractTerminationInfo> contractTerminationInfos = null;

        contractTerminationInfos = socialSecurityDeclareService.findContractTerminationInfo(importCondition);

        if (contractTerminationInfos.isEmpty() || contractTerminationInfos == null) {
            throw new RuntimeException("未查到停缴人员信息");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        for (ContractTerminationInfo contractTerminationInfo : contractTerminationInfos) {
            String contract_start_time = contractTerminationInfo.getContract_start_time();
            if (contract_start_time != null) {
                Date contract_start_timeDate = sdf.parse(contract_start_time);
                contract_start_time = sdf.format(contract_start_timeDate);
                contractTerminationInfo.setContract_start_time(contract_start_time);
            }
            String charge_end_month = contractTerminationInfo.getCharge_end_month();
            if (charge_end_month != null) {
                Date charge_end_monthDate = sdf.parse(charge_end_month);
                charge_end_month = sdf.format(charge_end_monthDate);
                contractTerminationInfo.setContract_start_time(charge_end_month);
            }
            String contract_end_time = contractTerminationInfo.getContract_end_time();
            if (contract_end_time != null) {
                Date contract_end_timeDate = sdf.parse(contract_end_time);
                contract_end_time = sdf.format(contract_end_timeDate);
                contractTerminationInfo.setContract_start_time(contract_end_time);
            }
            String quit_date = contractTerminationInfo.getQuit_date();
            if (quit_date != null) {
                Date quit_dateDate = sdf.parse(quit_date);
                quit_date = sdf.format(quit_dateDate);
                contractTerminationInfo.setContract_start_time(quit_date);
            }
            String owner = declareStopPaymentService.findOwnerById(contractTerminationInfo.getOwner());
            contractTerminationInfo.setOwner(owner);
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "合同解除终止.xls";
        StringBuilder stringBuilder = new StringBuilder();
        String realPath = stringBuilder.append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        // 不需要标题
        if (!file.exists()) {
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        String[] headers = {"社保福利地", "社保福利办理方", "一级客户名称", "二级客户名称", "业务员","姓名","性别","证件号码", "劳动合同合同起始时间", "劳动合同合同终止时间", "离职时间","社保终止时间","社保基数","用工形式","社保账号","离职原因","联系电话"};
        Workbook book = ExportExcel.exportExcel("sheet1", headers, contractTerminationInfos, realPath, "yyyyMMdd", workbook);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_STATUS, 1, book.getSheetAt(0).getLastRowNum(), 4, 4);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_STOP_REASON, 1, book.getSheetAt(0).getLastRowNum(), 5, 5);
        ExportExcel.getDataValidationList4Col(book.getSheet("sheet1"), 1, 6, book.getSheetAt(0).getLastRowNum(), 6, Arrays.asList(Constants.CONTRACT_TERMINATE_REASON), book);
        book.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, book);
    }

    //登记申请表
    @PostMapping("/registerDeclareSheet")
    public void registerDeclareSheet(@RequestBody ImportCondition importCondition,String formStatus,HttpServletResponse response) throws IOException, ParseException {

        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "合肥市就业失业登记、劳动用工备案、社会保险登记申请表.xls";
        StringBuilder stringBuilder = new StringBuilder();
        String realPath = stringBuilder.append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        // 不需要标题
        if (!file.exists()) {
            file.createNewFile();
        }
        Workbook workbook = null;
        //第一行表头字段，合并单元格时字段跨几列就将该字段重复几次
        String title = "合肥市就业失业登记、劳动用工备案、社会保险登记申请表";
        String secondRow = "单位管理码：201230        单位名称：北京外企人力资源服务安徽有限公司 （盖章）　  统一社会信用代码：9134010056             联系人：            联系电话：                  ";
        String[] header1 = {"序号", "姓名", "性别", "身法证号", "现工种或职务", "合同起始日期", "合同终止日期", "合同期限", "月工资收入(元)", "参加保险险种", "", "", "", "用工形式", "户籍所在地", "备注"};
        String[] header2 = {"", "", "", "", "", "", "", "", "", "养老", "失业", "医疗", "工伤", "", "", ""};
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        /** 第三步，设置样式以及字体样式*/
        CellStyle titleStyle = ExportExcel.createTitleCellStyle(workbook);
        CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook);
        /** 第四步，创建标题 ,合并标题单元格 */
        // 行号
        int rowNum = 0;
        // 创建第一页的第一行，索引从0开始
        Row row0 = sheet.createRow(rowNum++);
        row0.setHeight((short) 800);// 设置行高
        Cell c00 = row0.createCell(0);
        c00.setCellValue(title);
        c00.setCellStyle(titleStyle);
        // 合并单元格，参数依次为起始行，结束行，起始列，结束列 （索引0开始）
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));//标题合并单元格操作，6为总列数

        // 第二行
        Row row1 = sheet.createRow(rowNum++);
        row1.setHeight((short) 500);
        Cell c10 = row1.createCell(0);
        c10.setCellValue(secondRow);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));

        //第三行
        Row row2 = sheet.createRow(rowNum++);
        row2.setHeight((short) 500);

        for (int i = 0; i < header1.length; i++) {
            Cell tempCell = row2.createCell(i);
            tempCell.setCellValue(header1[i]);
            tempCell.setCellStyle(headerStyle);
        }
        // 合并
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 5, 5));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 6, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 7, 7));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 8, 8));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 9, 12));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 13, 13));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 14, 14));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 15, 15));
        //第四行
        Row row3 = sheet.createRow(rowNum++);
        row3.setHeight((short) 700);

        for (int i = 0; i < header2.length; i++) {
            Cell tempCell = row3.createCell(i);
            tempCell.setCellValue(header2[i]);
            tempCell.setCellStyle(headerStyle);
        }
        List<RegisterDeclareSheetInfo> registerDeclareSheetInfos = null;
            if (formStatus.equals("declare")) {
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfo(importCondition);
            }
            if (formStatus.equals("close")){
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfoFromStopPayment(importCondition);
            }


        if (registerDeclareSheetInfos.isEmpty()){
            throw new RuntimeException("未查到登记表信息" );
        }
        int serialNum = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        for (RegisterDeclareSheetInfo registerDeclareSheetInfo : registerDeclareSheetInfos) {
            registerDeclareSheetInfo.setSerialNumber(serialNum);
            registerDeclareSheetInfo.setWorkType("办事人员");
            registerDeclareSheetInfo.setInsuranceJobInjury("√");
            registerDeclareSheetInfo.setInsuranceMedical("√");
            registerDeclareSheetInfo.setInsurancePension("√");
            registerDeclareSheetInfo.setInsuranceUnemployment("√");
           /* registerDeclareSheetInfo.setDomicileLocation("合肥");
            registerDeclareSheetInfo.setEmployeeForm("全日制用工");*/
            registerDeclareSheetInfo.setRemarks("");
            //查合同起始日期和合同终止日期 从员工劳动合同查

          /*  //social_security_charge_start, social_security_charge_end
            Map<String, Object> employeeContractInfo = socialSecurityDeclareService.findEmployeeContractInfo(registerDeclareSheetInfo.getIdentityNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date beginDate = (Date) employeeContractInfo.get("social_security_charge_start");
            String beginDateString  = sdf.format(beginDate);
            registerDeclareSheetInfo.setContractBeginDate(beginDateString);

            Date endDate = (Date) employeeContractInfo.get("social_security_charge_end");
            String endDateString = sdf.format(endDate);
            registerDeclareSheetInfo.setContractEndDate(endDateString);*/
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            String contract_start_time = registerDeclareSheetInfo.getContractBeginDate();
            if (contract_start_time != null) {
                Date contract_start_timeDate = sdf.parse(contract_start_time);
                contract_start_time = sdf.format(contract_start_timeDate);
                registerDeclareSheetInfo.setContractBeginDate(contract_start_time);
                c1.setTime(contract_start_timeDate);
            }
            String contract_end_time = registerDeclareSheetInfo.getContractEndDate();
            if (contract_end_time !=null) {
                Date contract_end_timeDate = sdf.parse(contract_end_time);
                contract_end_time = sdf.format(contract_end_timeDate);
                registerDeclareSheetInfo.setContractEndDate(contract_end_time);
                c2.setTime(contract_end_timeDate);
            }
            int result = 0;


            if (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR)){
                result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
                if (result == 0){
                    registerDeclareSheetInfo.setContractTerm("");
                }else {
                    registerDeclareSheetInfo.setContractTerm(Math.abs(result) + "个月");
                }
            }else{
                result = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
                if (result == 0){
                    registerDeclareSheetInfo.setContractTerm("");
                } else {
                    registerDeclareSheetInfo.setContractTerm(Math.abs(result) + "年");
                }
            }
            serialNum++;
        }

        for (RegisterDeclareSheetInfo registerDeclareSheetInfo : registerDeclareSheetInfos) {
            ExportExcel.fillExcelDate(registerDeclareSheetInfo, sheet, workbook, sheet.getRow(3).getLastCellNum());

        }

        //创建页脚
        String[] footInfo = {
                "此次登记为：新签（　）续订（　）变更（　）解除（　）终止（　） 劳动关系。                      填表日期：　　　　年　　　月　　　日",
                "合肥市人力资源和社会保障局印制",
                "说明： 1、本名册由用人单位填写，一式一份，就业管理机构留存一份。",
                "　　  　2、用人单位应自录用之日起30日内持录用人员身份证（复印件，失业职工应持《就业创业证》或《失业证》到就业管理机构办理录用备案手续。",
                "　　 　 3、用工形式：⑴、全日制 ⑵、非全日制 。",
                "　 　　 4、此次登记为：新签、续订、变更、解除、终止劳动关系其中一项，多选无效。",
                "　 　　 5、“参加保险险种”栏可对应选项打“√”。"

        };
        //页脚1
        int lastRow = sheet.getLastRowNum() + 1;
        for (int i = 0; i < footInfo.length; i++) {
            Row footRow = sheet.createRow(lastRow);
            Cell footCell = footRow.createCell(0);
            Font font = workbook.createFont();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy  年  MM   月   dd日");
            if (i == 0){
                if (formStatus.equals("declare")){
                    Date formCreatedTime = new Date();
                    String dateFormat = sdf1.format(formCreatedTime);
                    footInfo[i] = "此次登记为：新签（√）续订（　）变更（　）解除（　）终止（　） 劳动关系。                      填表日期："+dateFormat;
                }
                if (formStatus.equals("close")){
                    Date formCreatedTime = new Date();
                    String dateFormat = sdf1.format(formCreatedTime);
                    footInfo[i] = "此次登记为：新签（）续订（　）变更（　）解除（√）终止（　） 劳动关系。                      填表日期："+dateFormat;
                }
            }
            if (i == 1) {
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                footCell.setCellStyle(cellStyle);
            } else if (i == 2) {
                font.setBold(true);
                //部分字体加粗
                RichTextString rts = new HSSFRichTextString(footInfo[i]);
                footCell.setCellValue(rts);
                rts.applyFont(0, 2, font);
            }
            footRow.setHeight((short) 500);
            if (i != 2) {
                footCell.setCellValue(footInfo[i]);
            }
            sheet.addMergedRegion(new CellRangeAddress(lastRow, lastRow, 0, 15));
            //设置字体大小
            font.setFontHeightInPoints((short) 10.5);
            lastRow++;
        }
        FileOutputStream fos = null;
        try {
            fos= new FileOutputStream(file);
            workbook.write(fos);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            fos.close();
        }


        ExportExcel.outputToWeb(realPath,response, workbook);
    }


}
