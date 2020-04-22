package com.authine.cloudpivot.web.api.controller;


import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;
import com.authine.cloudpivot.web.api.service.ProvidentAndSocialDeclareService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: weiyao
 * @Date: 2020-04-21
 * @Description: 导出外企 社保和公积金 excel
 */
@RestController
@RequestMapping("/controller/exportExcel")
public class ProvidentAndSocialExcelController extends BaseController{

    @Resource
    ProvidentAndSocialDeclareService providentAndSocialDeclareService;

    //导出公积金excel
    @GetMapping("/getProvidentExcel")
    public void getProvidentExcel(HttpServletResponse response) throws IOException {
        String welfareHandler="六";
        List<ProvidentFundDeclare> proList=providentAndSocialDeclareService.getProvidentFundDeclare(welfareHandler);

        //公积金
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        sheet.setDefaultRowHeight((short)(480));
        sheet.setDefaultColumnWidth(15);
        XSSFRow row = sheet.createRow(0);
        creatRow1ByG(row);
        //表头字体
        XSSFCellStyle cellStyle = setFontStyle(workbook);
    //    row.getCell(0).setCellStyle(cellStyle);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(cellStyle);
       }

        if(proList !=null && proList.size()>0){
            String userName="";
            int i=1;
            for (ProvidentFundDeclare po:proList){
                XSSFRow rowi = sheet.createRow(i);
                rowi.createCell(0).setCellValue(i);
                rowi.createCell(1).setCellValue(StringUtils.trimToEmpty(po.getFirstLevelClientName()));
                rowi.createCell(2).setCellValue(StringUtils.trimToEmpty(po.getSecondLevelClientName()));
                rowi.createCell(3).setCellValue(StringUtils.trimToEmpty(po.getEmployeeName()));
                rowi.createCell(4).setCellValue(StringUtils.trimToEmpty(po.getIdentityNo()));
                rowi.createCell(5).setCellValue(StringUtils.trimToEmpty(po.getWelfareHandler()));
                //设置业务员姓名
                if(StringUtils.isNotBlank(po.getOwner())){
                    UserModel user=this.getOrganizationFacade().getUserById(po.getOwner());
                    userName=user.getName();
                }
                rowi.createCell(6).setCellValue(StringUtils.trimToEmpty(userName));//拥有者（业务员）
                rowi.createCell(7).setCellValue(doubleCheckNull(po.getProvidentFundBase()));
                 i++;
            }
        }

        response.addHeader("Content-Disposition", "attachment;filename=" + new String("运行发布基数采集模板-公积金.xlsx".getBytes("gbk"), "iso8859-1"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

    }

    //导出社保excel
    @GetMapping("/getSocialExcel")
    public void getSocialExcel(HttpServletResponse response) throws IOException {
        String welfareHandler="六";
        List<SocialSecurityDeclare> socList=providentAndSocialDeclareService.getSocialSecurityDeclare(welfareHandler);

        //公积金
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        sheet.setDefaultRowHeight((short)(480));
        sheet.setDefaultColumnWidth(15);
        XSSFRow row = sheet.createRow(0);
        creatRow1ByS(row);
        //表头字体
        XSSFCellStyle cellStyle = setFontStyle(workbook);
   //     row.getCell(0).setCellStyle(cellStyle);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(cellStyle);
        }
        if(socList !=null && socList.size()>0){
            String userName="";
            int i=1;
            for (SocialSecurityDeclare po:socList){
                XSSFRow rowi = sheet.createRow(i);
                rowi.createCell(0).setCellValue(i);
                rowi.createCell(1).setCellValue(StringUtils.trimToEmpty(po.getFirstLevelClientName()));
                rowi.createCell(2).setCellValue(StringUtils.trimToEmpty(po.getSecondLevelClientName()));
                rowi.createCell(3).setCellValue(StringUtils.trimToEmpty(po.getEmployeeName()));
                rowi.createCell(4).setCellValue(StringUtils.trimToEmpty(po.getIdentityNo()));
                rowi.createCell(5).setCellValue(StringUtils.trimToEmpty(po.getWelfareHandler()));
                //设置业务员姓名
                if(StringUtils.isNotBlank(po.getOwner())){
                    UserModel user=this.getOrganizationFacade().getUserById(po.getOwner());
                    userName=user.getName();
                }
                rowi.createCell(6).setCellValue(StringUtils.trimToEmpty(userName));//拥有者（业务员）
                rowi.createCell(7).setCellValue(doubleCheckNull(po.getBasePay()));
                i++;
            }
        }

        response.addHeader("Content-Disposition", "attachment;filename=" + new String("运行发布基数采集模板-社保.xlsx".getBytes("gbk"), "iso8859-1"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    //公积金第一行
    private void creatRow1ByG(XSSFRow row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("委托方");
        row.createCell(2).setCellValue("客户名称");
        row.createCell(3).setCellValue("员工姓名");
        row.createCell(4).setCellValue("证件号码");
        row.createCell(5).setCellValue("福利办理方");
        row.createCell(6).setCellValue("业务员");
        row.createCell(7).setCellValue("原基数");
        row.createCell(8).setCellValue("新基数");
        row.createCell(9).setCellValue("公积金比例");
        row.createCell(10).setCellValue("备注");
    }

    //社保第一行
    private void creatRow1ByS(XSSFRow row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("委托方");
        row.createCell(2).setCellValue("客户名称");
        row.createCell(3).setCellValue("员工姓名");
        row.createCell(4).setCellValue("证件号码");
        row.createCell(5).setCellValue("福利办理方");
        row.createCell(6).setCellValue("业务员");
        row.createCell(7).setCellValue("原基数");
        row.createCell(8).setCellValue("新基数");
        row.createCell(9).setCellValue("备注");
    }

    //设置表头字体
    private XSSFCellStyle setFontStyle(XSSFWorkbook workbook){
        XSSFFont f  = workbook.createFont();
        f.setFontHeightInPoints((short) 12);//字号
        f.setBold(true);//加粗

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置字体
        cellStyle.setFont(f);
        return cellStyle;
    }

    private Float floatCheckNull(Float f) {
        return f == null ? 0F : f;
    }

    private Double doubleCheckNull(Double d) {
        return d == null ? 0D : d;
    }
}
