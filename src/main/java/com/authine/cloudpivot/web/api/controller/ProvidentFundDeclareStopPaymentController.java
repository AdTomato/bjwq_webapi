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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.*;
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
    public void openAccount(Date startTime, Date endTime, String welfare_handler, HttpServletResponse response) throws IOException, ParseException {
        if (welfare_handler == null) {
            throw new RuntimeException("福利办理方未填写");
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
            throw new RuntimeException("开始时间和结束时间存在空值");
        }
        if (openAccountInfos.isEmpty()) {
            throw new RuntimeException("未查到开户信息");
        }

        Integer serialNum = 1;
        for (OpenAccountInfo openAccountInfo : openAccountInfos) {
            openAccountInfo.setSerialNumber(serialNum);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date start_month = sdf.parse(openAccountInfo.getStart_month());
            if (start_month == null || "".equals(start_month)) {
                throw new RuntimeException("起始月为空！");
            }
            String start_monthString = sdf.format(start_month);
            openAccountInfo.setStart_month(start_monthString);
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
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        int rowNum = 0;
        //第二行表头
        String[] header1 = {"序号", "个人姓名", "证件号码", "缴存基数", "缴存比例", "公积金起缴月"};
        //第三行
        Row row2 = sheet.createRow(rowNum++);
        row2.setHeightInPoints(35.3f);
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
            fillExcelDate(openAccountInfo, sheet, workbook, 0);
        }
        //设置自适应宽度，有些问题
        ExportExcel.setSizeColumn(sheet, sheet.getLastRowNum());
        //设置具体列的宽度
        sheet.setColumnWidth(1, (int) (8.11 + 0.71) * 256);
        sheet.setColumnWidth(2, (int) (20 + 0.71) * 256);
        workbook.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath, response, workbook);
    }

    //启封封存导出
    @GetMapping("/unsealAndSeal")
    public void openAccount(Date startTime, Date endTime, String welfare_handler, String formStatus, HttpServletResponse response) throws IOException, ParseException {
        if (welfare_handler == null) {
            throw new RuntimeException("福利办理方为填写");
        }
        List<UnsealAndSealInfos> unsealAndSealInfos = null;
        if (startTime != null && endTime != null) {
            unsealAndSealInfos = declareStopPaymentService.findSealInfo(startTime, endTime, welfare_handler);
        } else if (startTime == null && endTime == null) {
            int timeNode = socialSecurityDeclareService.findTimeNode(welfare_handler);
            Map<String, Date> lastAndNowTimeNode = DateUtils.getLastAndNowTimeNode(timeNode);
            Date lastTimeNode = lastAndNowTimeNode.get("lastTimeNode");
            Date nowTimeNode = lastAndNowTimeNode.get("nowTimeNode");
            unsealAndSealInfos = declareStopPaymentService.findSealInfo(lastTimeNode, nowTimeNode, welfare_handler);
        } else {
            throw new RuntimeException("开始时间和结束时间存在空值");
        }

        if (unsealAndSealInfos.isEmpty()) {
            throw new RuntimeException("未查到封存启封信息");
        }

        int serialNum = 1;
        for (UnsealAndSealInfos unsealAndSealInfo : unsealAndSealInfos) {
            unsealAndSealInfo.setSerialNumber(serialNum);
            switch (unsealAndSealInfo.getIdentityNo_type()){
                case "身份证":
                    unsealAndSealInfo.setIdentityNo_type(01+"");
                    break;
                case "军官证":
                    unsealAndSealInfo.setIdentityNo_type(02+"");
                    break;
                case "护照":
                    unsealAndSealInfo.setIdentityNo_type(03+"");
                    break;
                case "外国人永久居留证":
                    unsealAndSealInfo.setIdentityNo_type(04+"");
                    break;
                case "港澳居民来往内地通行证":
                    unsealAndSealInfo.setIdentityNo_type(70+"");
                    break;
                case "台湾居民来往大陆通行证":
                    unsealAndSealInfo.setIdentityNo_type(71+"");
                    break;
            }
            //从员工档案查询个人账户
            // String accountNum = declareStopPaymentService.findAccountNum(unsealAndSealInfo.getIdentityNo());
            unsealAndSealInfo.setPersonalAccountNum("");

            DecimalFormat df = new DecimalFormat("#.00");
            Map<String, BigDecimal> proportion = declareStopPaymentService.findProportion(unsealAndSealInfo.getIdentityNo());
            if (proportion == null || proportion.size() == 0) {
                throw new RuntimeException("比例为空");
            }
            BigDecimal employee_ratio = proportion.get("employee_ratio");
            unsealAndSealInfo.setPersonalSaveProportion(Double.parseDouble(df.format(employee_ratio)));
            log.info("个人缴存比例：" + employee_ratio);
            unsealAndSealInfo.setChangeReason("1");
            serialNum++;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date charge_end_month = sdf.parse(unsealAndSealInfo.getCharge_end_month());
            if (charge_end_month == null || "".equals(charge_end_month)) {
                throw new RuntimeException("截止月为空！");
            }
            String charge_end_monthString = sdf.format(charge_end_month);
            unsealAndSealInfo.setCharge_end_month(charge_end_monthString);
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        String fileName = "";

        fileName = id + "停缴.xls";

        String realPath = new StringBuilder().append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        if (!file.exists()) {
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
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        //创建第二行表头
        String[] header = {"序号", "个人姓名", "证件类型","证件号码","个人账号", "变更原因","缴存比例", "公积金截止月" };
        Row rowHeader = sheet.createRow(rowNum++);
        for (int i = 0; i < header.length; i++) {
            Cell cell = rowHeader.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(ExportExcel.createHeadCellStyle(workbook, 10));
        }
        for (UnsealAndSealInfos unsealAndSealInfo : unsealAndSealInfos) {
            fillExcelDate(unsealAndSealInfo, sheet, workbook, 1);
        }
        workbook.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath, response, workbook);

    }

    public <T> void fillExcelDate(T clazz, Sheet sheet, Workbook workbook, int beginRow) {
        Field[] declaredFields = clazz.getClass().getDeclaredFields();
        String[] values = new String[declaredFields.length];
        try {
            Field.setAccessible(declaredFields, true);
            for (int i = 0; i < declaredFields.length; i++) {
                if (null == declaredFields[i].get(clazz)) {
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
