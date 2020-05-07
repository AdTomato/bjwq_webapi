package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.BaseInfoCollection;
import com.authine.cloudpivot.web.api.entity.OpenAccountInfo;
import com.authine.cloudpivot.web.api.entity.UnsealAndSealInfos;
import com.authine.cloudpivot.web.api.service.DeclareStopPaymentService;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import com.authine.cloudpivot.web.api.utils.DateUtils;
import com.authine.cloudpivot.web.api.utils.DoubleUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ProvidentFundDeclareStopPaymentController
 * @Author:lfh
 * @Date:2020/3/28 14:04
 * @Description: 公积金申报停缴
 **/

@RestController
@RequestMapping("/controller/provident")
@Slf4j
public class ProvidentFundDeclareStopPaymentController extends BaseController {
    @Autowired
    private DeclareStopPaymentService declareStopPaymentService;

    @Autowired
    private SocialSecurityDeclareService socialSecurityDeclareService;

    //公积金开户
    @GetMapping("/openAccount")
    public ResponseResult<Object> openAccount(Date startTime, Date endTime, String welfare_handler) throws IOException {
        if (welfare_handler == null) {
            return this.getOkResponseResult("error", "福利办理方未填写");
        }
        List<OpenAccountInfo> openAccountInfos = null;
        if (startTime != null && endTime != null) {
            openAccountInfos = declareStopPaymentService.findOpenAccountInfo(startTime, endTime, welfare_handler);

        } else if (startTime == null && endTime == null) {
            //查询当前城市的时间节点
            log.info("查看当前城市时间节点");
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            //查询上月节点时间和这月节点时间封装
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            openAccountInfos = declareStopPaymentService.findOpenAccountInfo(lastTimeNode, nowTimeNode, welfare_handler);
        } else {
            return this.getOkResponseResult("error", "开始时间和结束时间存在空值");
        }
        if (openAccountInfos.isEmpty()) {
            return this.getOkResponseResult("error", "未查到开户信息");
        }

        Integer serialNum = 1;
        for (OpenAccountInfo openAccountInfo : openAccountInfos) {
            openAccountInfo.setSerialNumber(serialNum);
            openAccountInfo.setIdentityNo_type("01");
            double unitMonthAccount = Double.parseDouble(String.valueOf(openAccountInfo.getPersonalSaveBase() * openAccountInfo.getUnitSaveProportion()));
            openAccountInfo.setUnitMonthSaveAccount(unitMonthAccount);
            double personalMonthSaveCount = Double.parseDouble(String.valueOf(openAccountInfo.getPersonalSaveBase() * openAccountInfo.getPersonalSaveProportion()));
            openAccountInfo.setPersonalMonthSaveCount(personalMonthSaveCount);
            openAccountInfo.setMonthSaveAccount(personalMonthSaveCount + unitMonthAccount);
            serialNum++;
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = id + "公积金增员.xls";
        StringBuilder stringBuilder = new StringBuilder();
        String realPath = stringBuilder.append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String date = sdf.format(openAccountInfos.get(0).getCreatedTime());*/
        // String[] title = {"个人开户批量导入","","","","","","","","","","","","单位账号","","业务季度","" };
        // String title = "个人开户批量导入";
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        int rowNum = 0;
        // 创建第一页的第一行，索引从0开始
        // Row row0 = sheet.createRow(rowNum++);
        // String title1[] = {"单位账号", "", "业务季度", date};
        /*for (int i = 0; i < title.length; i++) {
            // row0.createCell(row0.getLastCellNum()+ 1).setCellValue(title[i]);
            // row0.setHeight((short) 600);// 设置行高
            Cell cell = row0.createCell(i);
            cell.setCellValue(title[i]);
            CellStyle cellStyle = ExportExcel.createTitleCellStyle(workbook, "黑体", 20);
            if (i ==0) {
                cell.setCellStyle(cellStyle);
                cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
                cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
                cellStyle.setBorderTop(BorderStyle.THIN);//上边框
                cellStyle.setBorderRight(BorderStyle.THIN);//右边框
                cell.setCellStyle(cellStyle);
            }
            if (i >=12){
                CellStyle cs = workbook.createCellStyle();
                cs.setBorderBottom(BorderStyle.THIN); //下边框
                cs.setBorderLeft(BorderStyle.THIN); //左边框
                cs.setBorderRight(BorderStyle.THIN); //右边框
                cs.setBorderTop(BorderStyle.THIN); //上边框
                cell.setCellStyle(cs);
            }
        }*/
        // sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
        //第二行表头
        String[] header1 = {"序号", "姓名", "证件类型", "证件号码", "个人缴存基数", "单位缴存比例", "个人缴存比例", "月缴存总额", "单位月缴存额", "月缴存额"};
        // CellStyle headerStyle = ExportExcel.createHeadCellStyle(workbook, 12);
        //第三行
        Row row2 = sheet.createRow(rowNum++);
        row2.setHeightInPoints(35.3f);
        // row2.setHeight((short) 500);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框
        Font headerFont = workbook.createFont(); // 创建字体样式
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式

        for (int i = 0; i < header1.length; i++) {
            Cell tempCell = row2.createCell(i);
            tempCell.setCellValue(header1[i]);
            tempCell.setCellStyle(cellStyle);
        }
        for (OpenAccountInfo openAccountInfo : openAccountInfos) {
            fillExcelDate(openAccountInfo, sheet, workbook,0);
        }
        //设置自适应宽度，有些问题
        ExportExcel.setSizeColumn(sheet, sheet.getLastRowNum());
        //设置具体列的宽度
        sheet.setColumnWidth(3, (int) (20 + 0.71) * 256);
        workbook.write(fos);
        fos.close();
        return this.getOkResponseResult("success", "公积金增员导出成功");
    }

    //启封封存导出
    @GetMapping("/unsealAndSeal")
    public ResponseResult<Object> openAccount(Date startTime, Date endTime, String welfare_handler, String formStatus) throws IOException {
        if (welfare_handler == null) {
            return this.getOkResponseResult("error", "福利办理方为填写");
        }
        List<UnsealAndSealInfos> unsealAndSealInfos = null;
        if (startTime !=null && endTime != null){
           /* if (formStatus.equals("申报")) {
                unsealAndSealInfos = declareStopPaymentService.findUnsealInfo(startTime, endTime, welfare_handler);
            }*/
            // if (formStatus.equals("停缴")){
                unsealAndSealInfos = declareStopPaymentService.findSealInfo(startTime,endTime,welfare_handler);
            // }
        }else if (startTime == null && endTime == null){
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            /*if (formStatus.equals("申报")) {
                unsealAndSealInfos = declareStopPaymentService.findUnsealInfo(lastTimeNode, nowTimeNode, welfare_handler);
            }*/
            // if (formStatus.equals("停缴")){
                unsealAndSealInfos = declareStopPaymentService.findSealInfo(lastTimeNode,nowTimeNode,welfare_handler);
            // }
        }else {
            return this.getOkResponseResult("error", "开始时间和结束时间存在空值");
        }

        if (unsealAndSealInfos.isEmpty()){
            return this.getOkResponseResult("error","未查到封存启封信息" );
        }
        int serialNum = 1;
        for (UnsealAndSealInfos unsealAndSealInfo : unsealAndSealInfos) {
            unsealAndSealInfo.setSerialNumber(serialNum);
            // unsealAndSealInfo.setBusinessMonth("");
            //从员工档案查询个人账户
            String accountNum = declareStopPaymentService.findAccountNum(unsealAndSealInfo.getIdentityNo());
            if (accountNum == null){
                accountNum = "";
            }
            unsealAndSealInfo.setPersonalAccountNum(accountNum);
          /*  if (formStatus.equals("申报")){
                unsealAndSealInfo.setChangeType("01");
            }*/
            // if (formStatus.equals("停缴")){
                unsealAndSealInfo.setChangeType("02");
            // }
            unsealAndSealInfo.setIdentityNo_type("01");
            DecimalFormat df = new DecimalFormat("#.00");
            Map<String,BigDecimal> proportion = declareStopPaymentService.findProportion(unsealAndSealInfo.getId());
            if (proportion == null || proportion.size() == 0){
                return this.getOkResponseResult("error", "比例为空");
            }
            BigDecimal employee_ratio = proportion.get("employee_ratio");
            BigDecimal company_ratio = proportion.get("company_ratio");
            unsealAndSealInfo.setPersonalSaveProportion(Double.parseDouble(df.format(employee_ratio)));
            unsealAndSealInfo.setUnitSaveProportion(Double.parseDouble(df.format(company_ratio)));
            log.info("个人缴存比例："+employee_ratio);
            log.info("单位缴存比例："+company_ratio);
            //单位月缴存额
            Double unitMonthAccount = unsealAndSealInfo.getPersonalSaveBase()*unsealAndSealInfo.getUnitSaveProportion();
            unitMonthAccount = Double.parseDouble(df.format(unitMonthAccount));

            unsealAndSealInfo.setUnitMonthSaveAccount(unitMonthAccount);

            //个人月缴存额
            double personalMonthSaveCount = unsealAndSealInfo.getPersonalSaveBase() * unsealAndSealInfo.getPersonalSaveProportion();

            personalMonthSaveCount = Double.parseDouble(df.format(personalMonthSaveCount));
            unsealAndSealInfo.setPersonalMonthSaveCount(personalMonthSaveCount);
            Double monthSaveCount = unitMonthAccount+personalMonthSaveCount;
            //月缴存额
            monthSaveCount = Double.parseDouble(df.format(monthSaveCount));
            unsealAndSealInfo.setMonthSaveAccount(monthSaveCount);
          /*  if (formStatus.equals("申报")){
                unsealAndSealInfo.setChangeReason("9");
            }
            if (formStatus.equals("停缴")){*/
                unsealAndSealInfo.setChangeReason("1");
            // }
            serialNum++;

        }
        String id = UUID.randomUUID().toString().replace("-","" );
        String fileName= "";
        /*if (formStatus.equals("申报")) {
            fileName = id + "启封.xls";
        }*/
        // if (formStatus.equals("停缴")){
            fileName = id + "停缴.xls";
        // }
        String realPath = new StringBuilder().append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        if (!file.exists()){
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        String title = "启封封存批量导入";
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        int rowNum = 0;
        Row rowTitle = sheet.createRow(rowNum++);
        //创建第一行数据表头
        Cell cell00 = rowTitle.createCell(0);
        cell00.setCellValue(title);
        cell00.setCellStyle(ExportExcel.createHeadCellStyle(workbook, 10));
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,13));

        //创建第二行表头
        String[] header = {"序号","业务月度","个人账号","姓名","证件类型","证件号码","变更类型","个人缴存基数","单位缴存比例","个人缴存比例","单位月缴存额","个人月缴存额","月缴存额","变更原因"};
        Row rowHeader = sheet.createRow(rowNum++);
        for (int i = 0; i < header.length; i++) {
            Cell cell = rowHeader.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(ExportExcel.createHeadCellStyle(workbook, 10));
        }
        for (UnsealAndSealInfos unsealAndSealInfo : unsealAndSealInfos) {
            fillExcelDate(unsealAndSealInfo,sheet,workbook,1);
        }
        workbook.write(fos);
        fos.close();
        return this.getOkResponseResult("success", "启封封存成功");

    }
    public <T> void fillExcelDate(T clazz,Sheet sheet,Workbook workbook,int beginRow){
        Field[] declaredFields = clazz.getClass().getDeclaredFields();
        String[] values = new String[declaredFields.length];
        try {
            Field.setAccessible(declaredFields, true);
            for (int i = 0; i < declaredFields.length; i++) {
                if (null == declaredFields[i].get(clazz)){
                    declaredFields[i].set(clazz, "");
                }
                 values[i] = declaredFields[i].get(clazz).toString();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //追加数据的开始行
        int start = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(start);
        int cluNum = sheet.getRow(beginRow).getLastCellNum();
        for (int i = 0; i < cluNum; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            // String value = ExcelUtils.getValue(cell);
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            cell.setCellValue(value);
            CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
            cell.setCellStyle(contentCellStyle);
        }
    }

}
