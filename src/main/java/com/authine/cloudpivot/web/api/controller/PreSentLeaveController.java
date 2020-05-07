package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.authine.cloudpivot.web.api.entity.LeaveInfo;
import com.authine.cloudpivot.web.api.entity.PreSentInfo;
import com.authine.cloudpivot.web.api.service.PreSentLeaveService;
import com.authine.cloudpivot.web.api.utils.ExportExcel;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName PreSentLeaveController
 * @Author:lfh
 * @Date:2020/4/8 14:39
 * @Description: 批量预派撤离控制层
 **/
@RestController
@RequestMapping("/controller/preSentLeave")
@Slf4j
public class PreSentLeaveController extends BaseController {

    @Autowired
    private PreSentLeaveService preSentLeaveService;

    //批量预派
    @GetMapping("/batchPreSent")
    public void batchPreSent(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime, HttpServletResponse response) throws IOException, ParseException {
        if (startTime == null || endTime == null) {
            throw new RuntimeException("开始或结束时间为空");
        }
        List<PreSentInfo> preSentInfos = null;
        log.info("开始查询批量派遣数据");
        //查询批量派遣信息
        preSentInfos = preSentLeaveService.findBatchPreSent(startTime, endTime);
        if (preSentInfos.isEmpty()) {
            throw new RuntimeException("未查到批量派遣人员信息");
        }
        Integer serialNum = 1;
        List<PreSentInfo> copyPrenSentList= new ArrayList<>();
        // DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < preSentInfos.size(); i++) {
            PreSentInfo preSent = new PreSentInfo();
            PreSentInfo preSentInfo = preSentInfos.get(i);
            String identityNo = preSentInfo.getIdentityNo();
            //查询增员中的信息
            AddEmployee addEmployee = preSentLeaveService.findEmployeeInfo(identityNo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //设置业务员
            String salesman = preSentLeaveService.findSaleman(identityNo);
            preSentInfo.setSalesman(salesman);
            Date createdTime = preSentInfo.getCreatedTime();
            String dispatchDate = sdf.format(createdTime);
            //设置派单日期
            preSentInfo.setDispatchDate(dispatchDate);
            //设置入职日期
            String entryTime = preSentInfo.getEntryTime();
            entryTime= entryTime.split(" ")[0];
            preSentInfo.setEntryTime(entryTime);
            //设置客户名称
            preSentInfo.setClientName(addEmployee.getSecondLevelClientName());
            //设置户口性质
            preSentInfo.setHouseholdRegisterNature(addEmployee.getFamilyRegisterNature());
            //设置备注
            preSentInfo.setRemarks("");
            //社保和公积金福利地不一致 ，复制对象 生成两条数据
            if (!addEmployee.getProvidentFundCity().equals(addEmployee.getSocialSecurityCity())) {

                BeanUtils.copyProperties(preSentInfo, preSent);
                preSentInfo.setSerialNum(serialNum++);
                //设置公积金起做时间
                preSentInfo.setProvidentFundStartTime(sdf.format(addEmployee.getProvidentFundStartTime()));
                //设置公积金福利地
                preSentInfo.setWelfarePlaces(addEmployee.getProvidentFundCity());
                //设置社保起做时间
                preSentInfo.setSocialSecurityStartTime("");
                //设置社保基数
                preSentInfo.setSocialInsuranceAmount(Double.parseDouble(String.format("%.2f",0D)));
                //设置公积金比例
                preSentInfo.setProvidentFundProportion(Double.parseDouble(String.format("%.2f",addEmployee.getCompanyProvidentFundBl())) + "+" + Double.parseDouble(String.format("%.2f",addEmployee.getEmployeeProvidentFundBl())));
                preSent.setSerialNum(serialNum++);
                preSent.setProvidentFundStartTime("");
                preSent.setWelfarePlaces(addEmployee.getSocialSecurityCity());
                preSent.setSocialSecurityStartTime(sdf.format(addEmployee.getSocialSecurityStartTime()));
                preSent.setProvidentFundAmount(Double.parseDouble(String.format("%.2f",(0D))));
                preSent.setProvidentFundProportion("");
                copyPrenSentList.add(preSent);
                // preSentInfo.setProvidentFundAmount(Double.valueOf(df.format()));
            } else {
                preSentInfo.setSerialNum(serialNum++);
                preSentInfo.setWelfarePlaces(addEmployee.getProvidentFundCity());
                preSentInfo.setProvidentFundStartTime(sdf.format(addEmployee.getProvidentFundStartTime()));
                preSentInfo.setSocialSecurityStartTime(sdf.format(addEmployee.getSocialSecurityStartTime()));
                preSentInfo.setProvidentFundProportion(Double.parseDouble(String.format("%.2f",addEmployee.getCompanyProvidentFundBl())) + "+" + Double.parseDouble(String.format("%.2f",addEmployee.getEmployeeProvidentFundBl())));
                preSentInfo.setProvidentFundAmount(Double.parseDouble(String.format("%.2f",preSentInfo.getProvidentFundAmount())));
                preSentInfo.setSocialInsuranceAmount(Double.parseDouble(String.format("%.2f",preSentInfo.getSocialInsuranceAmount())));
            }

        }
        String id = UUID.randomUUID().toString().replace("-","" );
        String fileName= id + "批量预派.xls";
        String realPath = new StringBuilder().append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        if (!file.exists()){
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        String[] header = {"序号","业务员","派单日期","客户名称","姓名","身份证号码","联系电话","邮箱","户籍性质","福利地","入职日期",
                "社保起做时间\n" + "(**年-**月-**日，如2017-08-01)","社保基数","公积金起做时间\n" + "(**年-**月-**日，如2017-08-01)","公积金基数","公积金比例","备注"};
        int rowNum = 0;
        Row rowHeader = sheet.createRow(rowNum++);
        for (int i = 0; i < header.length; i++) {
            Cell cell = rowHeader.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(ExportExcel.createHeadCellStyle(workbook, 10));
        }
        preSentInfos.addAll(copyPrenSentList);
        Collections.sort(preSentInfos, (a,b) ->a.getSerialNum().compareTo(b.getSerialNum()));
        for (PreSentInfo present: preSentInfos) {
            ExportExcel.fillExcelDate(present,sheet,workbook,sheet.getRow(0).getLastCellNum());
        }
        workbook.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, workbook);

    }

    @GetMapping("/batchLeave")
    public void batchLeave(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date startTime,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,HttpServletResponse response) throws IOException {
        if (startTime == null || endTime == null) {
            throw new RuntimeException( "未传入开始时间和结束时间");
        }
        List<LeaveInfo> leaveInfos = null;
        log.info("开始查询批量撤离数据");
        leaveInfos =preSentLeaveService.findBatchLeave(startTime,endTime);
        if (leaveInfos.isEmpty()){
            throw new RuntimeException("未查到批量撤离人员信息" );
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Integer serialNum = 1;
        for (LeaveInfo leaveInfo : leaveInfos) {
            leaveInfo.setSerialNum(serialNum++);
            leaveInfo.setDispatchDate(sdf.format(leaveInfo.getCreatedTime()));
            //查询减员信息
            DeleteEmployee deleteEmployee =preSentLeaveService.findDelEmployeeInfo(leaveInfo.getIdentityNo());
            //查询员工对应的业务员
            String saleman = preSentLeaveService.findSaleman(leaveInfo.getIdentityNo());
            leaveInfo.setSalesman(saleman);
            leaveInfo.setWelfarePlaces(deleteEmployee.getSWelfareHandler());
            leaveInfo.setClientName(deleteEmployee.getSecondLevelClientName());
            leaveInfo.setDepartureDate(sdf.format(deleteEmployee.getLeaveTime()));
            leaveInfo.setDepartureReason(deleteEmployee.getLeaveReason());
            leaveInfo.setProvidentFundEndTime(leaveInfo.getProvidentFundEndTime().split(" ")[0]);
            leaveInfo.setSocialSecurityEndTime(leaveInfo.getSocialSecurityEndTime().split(" ")[0]);

            leaveInfo.setRemarks("");
        }
        String id = UUID.randomUUID().toString().replace("-","" );
        String fileName= id + "批量撤离.xls";
        String realPath = new StringBuilder().append("D://upload//").append(fileName).toString();
        File file = new File(realPath);
        if (!file.exists()){
            file.createNewFile();
        }
        Workbook workbook = null;
        FileOutputStream fos = new FileOutputStream(file);
        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        String[] header = {"序号","业务员","派单日期","客户名称","姓名","身份证号码","福利地","离职日期" + "(****年**月**日)","离职原因",
                "社保终止时间" + "最后缴费月" + "**年-**月-**日，如2017-08-31)","公积金终止时间" + "最后缴费月" + "**年-**月-**日，如2017-08-31)","备注"};
        int rowNum = 0;
        Row rowHeader = sheet.createRow(rowNum++);
        for (int i = 0; i < header.length; i++) {
            Cell cell = rowHeader.createCell(i);
            cell.setCellValue(header[i]);
            // sheet.autoSizeColumn((short)i,true);
            CellStyle headCellStyle = ExportExcel.createHeadCellStyle(workbook, 10);
            cell.setCellStyle(headCellStyle);
        }
        for (LeaveInfo leaveInfo: leaveInfos) {
            ExportExcel.fillExcelDate(leaveInfo,sheet,workbook,sheet.getRow(0).getLastCellNum());
        }
        /**
         * 设置自适应宽度 sheet：表  sheet.getlastRowRum 实际行数
         *  sheet.setColumnWidth(2, (int)(12+0.71)*256); 调整特定列宽度 12为模块列的宽度
         */
        ExportExcel.setSizeColumn(sheet,sheet.getLastRowNum() );
        sheet.setColumnWidth(2, (int)(12+0.71)*256);
        sheet.setColumnWidth(7, (int) (12+0.71)*256);
        sheet.setColumnWidth(9, (int)(16.67+0.71)*256);
        sheet.setColumnWidth(10, (int)(16.67+0.71)*256);
        workbook.write(fos);
        fos.close();
        ExportExcel.outputToWeb(realPath,response, workbook);
    }
}
