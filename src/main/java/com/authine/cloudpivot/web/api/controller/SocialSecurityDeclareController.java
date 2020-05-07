package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import com.authine.cloudpivot.web.api.utils.DateUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.view.ResponseResult;
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

    //合同新签导入
    @PostMapping("/importNewContract")
    public void importNewContract(Date startTime, Date endTime, String welfare_handler, HttpServletResponse response) throws IOException {
        if (welfare_handler == null) {
            throw  new RuntimeException("福利办理方未填写");
        }

        List<ContractImportInfo> ContractInfos = null;
        if (startTime != null && endTime != null) {
            //传入的参数有开始时间和结束时间
            ContractInfos = socialSecurityDeclareService.findContractInfo(startTime, endTime, welfare_handler);
        }else if (startTime == null && endTime == null) {
            //查询当前城市的时间节点
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            //查询上月节点时间和这月节点时间封装
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            /* * @Author lfh
             * @Description //查询获取用户选选中的公积金申报信息
             * @Date 2020/3/25 10:10
             * @Param lastTimeNodeDate, nowTimeNodeDate, welfare_handler 开始时间，结束时间  福利办理方
             * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
             **/
            ContractInfos = socialSecurityDeclareService.findContractInfo(lastTimeNode, nowTimeNode, welfare_handler);
        }else {
            throw new RuntimeException("开始时间和结束时间存在空值");
        }
        if (ContractInfos.isEmpty()) {
            throw new RuntimeException("未查到新签合同信息");
        }
        if (!ContractInfos.isEmpty()) {
            for (ContractImportInfo contractInfo : ContractInfos) {
                contractInfo.setUnitManagementId("201230");
                contractInfo.setNation("汉族");
                contractInfo.setAccountCharacter("本地非农业户口（本地城镇）");
                contractInfo.setDomicilePlace("安徽省");
                contractInfo.setRegisteredResidence("安徽省合肥市庐阳区");
                contractInfo.setResidentialArea("庐阳区");
                contractInfo.setPlaceOfResidence("安徽省合肥市庐阳区");
                contractInfo.setPhoneNumber("65222899");
                contractInfo.setPersonCategory("新就业");
                contractInfo.setEducationDegree("其他");
                contractInfo.setWorkType("办事人员");
                contractInfo.setContractTermType("有固定期限");
                contractInfo.setWorkForm("全日制用工");
                contractInfo.setPositive_salary(Double.parseDouble(String.valueOf(contractInfo.getPositive_salary())));
                contractInfo.setUnitImploymentStartTime(contractInfo.getContract_deadline());
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
        String[] headers = {"单位管理码", "姓　名", "性别", "民族", "出生日期", "身份证号码", "户口性质", "户口所在地区划", "户口所在地", "居住所在地区划", "居住所在地", "联系电话", "人员类别", "文化程度", "工种", "合同起始日期", "合同终止日期", "合同期限类型", "工资", "用工形式", "就业登记时间", "单位就业起始时间"};
        Workbook book = ExportExcel.exportExcel("sheet1", headers, ContractInfos, realPath, "yyyyMMdd", workbook);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.GENDER, 1, book.getSheetAt(0).getLastRowNum(), 2, 2);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.NATION, 1, book.getSheetAt(0).getLastRowNum(), 3, 3);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.ACCOUNT_CHARACTER, 1, book.getSheetAt(0).getLastRowNum(), 6, 6);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.DOMICILE_PLACE, 1, book.getSheetAt(0).getLastRowNum(), 7, 7);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.DOMICILE_PLACE, 1, book.getSheetAt(0).getLastRowNum(), 9, 9);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.PERSON_CATEGORY, 1, book.getSheetAt(0).getLastRowNum(), 12, 12);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.EDUCATION_DEGREE, 1, book.getSheetAt(0).getLastRowNum(), 13, 13);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_TERM_TYPE, 1, book.getSheetAt(0).getLastRowNum(), 17, 17);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.WORK_FORM, 1, book.getSheetAt(0).getLastRowNum(), 19, 19);
        ExportExcel.getDataValidationList4Col(book.getSheet("sheet1"), 1, 14, book.getSheetAt(0).getLastRowNum(), 14, Arrays.asList(Constants.WORK_TYPE), book);
        book.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, workbook);
    }

    //合同解除终止
    @GetMapping("/contractTermination")
    public void contractTermination(Date startTime, Date endTime, String welfare_handler,HttpServletResponse response) throws IOException, ParseException {
        if (welfare_handler == null) {
            throw new RuntimeException("福利办理方未填写");
        }

        /* * @Author lfh
         * @Description //获取用户选中的停缴信息
         * @Date 2020/3/27 10:15
         * @Param [ids]
         * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
         **/
        List<ContractTerminationInfo> contractTerminationInfos = null;

        if (startTime != null && endTime != null) {
            //传入的参数有开始时间和结束时间
            contractTerminationInfos = socialSecurityDeclareService.findContractTerminationInfo(startTime, endTime, welfare_handler);
        }else if (startTime == null && endTime == null) {
            //查询当前城市的时间节点
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            //查询上月节点时间和这月节点时间封装
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            contractTerminationInfos = socialSecurityDeclareService.findContractTerminationInfo(lastTimeNode, nowTimeNode, welfare_handler);
        } else {
            throw new RuntimeException("开始时间和结束时间存在空值");
        }
        if (contractTerminationInfos.isEmpty()) {
            throw new RuntimeException("未查到停缴人员信息");
        }

        for (ContractTerminationInfo contractTerminationInfo : contractTerminationInfos) {
            contractTerminationInfo.setUnitManagementId("201230");
            contractTerminationInfo.setContractStatus("解除");
            contractTerminationInfo.setContractStopReason("劳动合同期满");
            contractTerminationInfo.setContractTerminateReason("劳动者提前30天书面通知解除或试用期提案前3天通知解除");
            // 合同解除时间 ：对应提交减员时的系统时间
            Date deleteEmployeeDate = socialSecurityDeclareService.findDeleteEmployeeDate(contractTerminationInfo.getIdentityNo());
            if (deleteEmployeeDate != null) {
                contractTerminationInfo.setContractTerminationDate(deleteEmployeeDate);
            }
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
        String[] headers = {"单位管理码", "姓　名", "身份证号码", "合同解除时间", "合同变更状态", "合同终止原因", "合同解除原因"};
        Workbook book = ExportExcel.exportExcel("sheet1", headers, contractTerminationInfos, realPath, "yyyyMMdd", workbook);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_STATUS, 1, book.getSheetAt(0).getLastRowNum(), 4, 4);
        ExportExcel.setHSSFValidation(book.getSheet("sheet1"), Constants.CONTRACT_STOP_REASON, 1, book.getSheetAt(0).getLastRowNum(), 5, 5);
        ExportExcel.getDataValidationList4Col(book.getSheet("sheet1"), 1, 6, book.getSheetAt(0).getLastRowNum(), 6, Arrays.asList(Constants.CONTRACT_TERMINATE_REASON), book);
        book.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, workbook);
    }

    //登记申请表
    @GetMapping("/registerDeclareSheet")
    public void registerDeclareSheet(Date startTime, Date endTime, String welfare_handler,String formStatus,HttpServletResponse response) throws IOException, ParseException {
        if (welfare_handler == null) {
            throw new RuntimeException("福利办理方未填写");
        }
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
        FileOutputStream fos = new FileOutputStream(file);
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
        if (startTime != null && endTime != null) {
            //传入的参数有开始时间和结束时间
            if (formStatus.equals("申报")) {
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfo(startTime, endTime, welfare_handler);
            }
            if (formStatus.equals("停缴")){
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfoFromStopPayment(startTime, endTime, welfare_handler);
            }
        } else if (startTime == null && endTime == null) {
            //查询当前城市的时间节点
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            //查询上月节点时间和这月节点时间封装
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            if (formStatus.equals("申报")){
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfo(lastTimeNode, nowTimeNode, welfare_handler);
            }
            if (formStatus.equals("停缴")){
                registerDeclareSheetInfos = socialSecurityDeclareService.findRegisterDeclareInfo(lastTimeNode, nowTimeNode, welfare_handler);
            }
        } else {
            throw new RuntimeException("开始时间和结束时间存在空值");
        }
        if (registerDeclareSheetInfos.isEmpty()){
            throw new RuntimeException("未查到登记表信息" );
        }
        int serialNum = 1;
        for (RegisterDeclareSheetInfo registerDeclareSheetInfo : registerDeclareSheetInfos) {
            registerDeclareSheetInfo.setSerialNumber(serialNum);
            registerDeclareSheetInfo.setWorkType("办事人员");
            registerDeclareSheetInfo.setInsuranceJobInjury("√");
            registerDeclareSheetInfo.setInsuranceMedical("√");
            registerDeclareSheetInfo.setInsurancePension("√");
            registerDeclareSheetInfo.setInsuranceUnemployment("√");
            registerDeclareSheetInfo.setDomicileLocation("合肥");
            registerDeclareSheetInfo.setEmployeeForm("全日制用工");
            registerDeclareSheetInfo.setRemarks("");
            //查合同起始日期和合同终止日期 从员工劳动合同查

            //social_security_charge_start, social_security_charge_end
            Map<String, Object> employeeContractInfo = socialSecurityDeclareService.findEmployeeContractInfo(registerDeclareSheetInfo.getIdentityNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date beginDate = (Date) employeeContractInfo.get("social_security_charge_start");
            String beginDateString  = sdf.format(beginDate);
            registerDeclareSheetInfo.setContractBeginDate(beginDateString);

            Date endDate = (Date) employeeContractInfo.get("social_security_charge_end");
            String endDateString = sdf.format(endDate);
            registerDeclareSheetInfo.setContractEndDate(endDateString);
            int result = 0;
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(beginDate);
            c2.setTime(endDate);
            result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
            registerDeclareSheetInfo.setContractTerm(String.valueOf(Math.abs(result)));
            serialNum++;
        }

        for (RegisterDeclareSheetInfo registerDeclareSheetInfo : registerDeclareSheetInfos) {
            Field[] declaredFields = registerDeclareSheetInfo.getClass().getDeclaredFields();
            String[] values = new String[declaredFields.length];
            try {
                Field.setAccessible(declaredFields, true);
                for (int i = 0; i < declaredFields.length; i++) {
                    values[i] =(declaredFields[i].get(registerDeclareSheetInfo)).toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //追加数据的开始行
            int start = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(start);
            int cluNum = sheet.getRow(3).getLastCellNum();
            for (int i = 0; i < cluNum; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                String value = ExcelUtils.getValue(cell);
                cell.setCellValue(value);
                cell.setCellStyle(ExportExcel.createContentCellStyle(workbook));
            }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy  年  MM   月   dd日");
            if (i == 0){
                if (formStatus.equals("申报")){
                    Date formCreatedTime = new Date();
                    String dateFormat = sdf.format(formCreatedTime);
                    footInfo[i] = "此次登记为：新签（√）续订（　）变更（　）解除（　）终止（　） 劳动关系。                      填表日期："+dateFormat;
                }
                if (formStatus.equals("停缴")){
                    Date formCreatedTime = new Date();
                    String dateFormat = sdf.format(formCreatedTime);
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
        workbook.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, workbook);
    }


}
