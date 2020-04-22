package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Attachment;
import com.authine.cloudpivot.web.api.entity.BaseInfoCollection;
import com.authine.cloudpivot.web.api.entity.StartCollect;
import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.params.BaseControllerGetBaseNunInfo;
import com.authine.cloudpivot.web.api.params.CollectParams;
import com.authine.cloudpivot.web.api.service.AttachmentService;
import com.authine.cloudpivot.web.api.service.BaseCollectionService;
import com.authine.cloudpivot.web.api.utils.*;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName BaseCollectionController
 * @Author:lfh
 * @Date:2020/3/12 14:00
 * @Description: 基数采集控制类
 **/

@RestController
@RequestMapping("/controller/baseCollection")
@Slf4j
public class BaseCollectionController extends BaseController {


    @Autowired
    private BaseCollectionService baseCollectionService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * @Author: lfh
     * @Date: 2020/3/24 11:32
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description:
     */
    @PostMapping("/getBaseNunInfo")
    public ResponseResult<Object> getBaseNumInfo(@RequestBody BaseControllerGetBaseNunInfo baseControllerGetBaseNunInfo) throws IOException {
        String bizObjectId = baseControllerGetBaseNunInfo.getBizObjectId();
        String welfareOperator = baseControllerGetBaseNunInfo.getWelfareOperator();

        Attachment attachment = attachmentService.getFileName(bizObjectId, "start_collect", "collect_template");

        if (attachment != null && StringUtils.isBlank(attachment.getName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        String fileName = attachment.getName();

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        String filePath = "D:\\upload\\";
        String realName = new StringBuilder().append(filePath).append(attachment.getRefId()).append(fileName).toString();
        File file = new File(realName);
        List<ExcelHead> excelHeads = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        //让表头和javabean的属性对应
        String[] entityNameGjj = {"serialNum", "entrustedUnit", "clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum", "nowBaseNum", "remarks"};
        String[] entityNameSb = {"serialNum", "entrustedUnit", "clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum", "nowBaseNum", "providentFundProportion", "remarks"};
        //动态从excel中获取表头数据
        List<String> headNameList = ParseExcelUtils.getHeadName(realName, fis);
        fis.close();
        String[] headName = new String[headNameList.size()];
        headName = headNameList.toArray(headName);
        ExcelHead excelHead = null;
        for (int i = 0; i < headName.length; i++) {
            if (headName.length == 10) {
                excelHead = new ExcelHead(headName[i], entityNameGjj[i]);
            }
            if (headName.length == 11) {
                excelHead = new ExcelHead(headName[i], entityNameSb[i]);
            }
            excelHeads.add(excelHead);
        }
        List<BaseInfoCollection> baseInfoCollectionList = null;
        FileInputStream is = new FileInputStream(file);
        try {
            //读取excel中的数据到实体类
            baseInfoCollectionList = ParseExcelUtils.readExcelToEntity(BaseInfoCollection.class, is, realName, excelHeads);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        // 获取发起基数采集数据
        StartCollect startCollect = baseCollectionService.getStartCollectById(bizObjectId);

        if (null == startCollect) {
            log.info("查询发起基数采集数据为空");
            return getErrResponseResult(null, 404L, "查询发起基数采集数据为空");
        }

        log.info("开始获取需要采集的信息");

        log.info("创建基数采集数据");

        UserModel userModel = this.getOrganizationFacade().getUserById(UserUtils.getUserId(getUserId()));
        Map<String, List<BaseInfoCollection>> baseInfoCollectionMap = new HashMap<>();
        List<BaseInfoCollection> collectClients = null;
        //将excel查到的数据按公司名分组
        if (baseInfoCollectionList != null && baseInfoCollectionList.size() > 0) {
            for (BaseInfoCollection baseInfoCollection : baseInfoCollectionList) {

                if (baseInfoCollectionMap.containsKey(baseInfoCollection.getClientName())) {
                    baseInfoCollectionMap.get(baseInfoCollection.getClientName()).add(baseInfoCollection);
                } else {
                    collectClients = new ArrayList<>();
                    collectClients.add(baseInfoCollection);
                    baseInfoCollectionMap.put(baseInfoCollection.getClientName(), collectClients);
                }
            }
        }
        for (String clientName : baseInfoCollectionMap.keySet()) {
            List<BaseInfoCollection> baseInfoCollections = baseInfoCollectionMap.get(clientName);
            if (collectClients != null && collectClients.size() > 0) {
                log.info("将信息采集信息导出到excel中");
                //通过发起基数采集的id查询基数采集对应客户的id
                String userId = baseCollectionService.findCompanyName(clientName);
                if (userId == null) {
                    userId = baseCollectionService.findSecondCompanyName(clientName);
                }

                BizObjectModel model = new BizObjectModel();
                model.setSequenceStatus(Constants.PROCESSING_STATUS);
                model.setSchemaCode(Constants.SUBMIT_COLLECT_SCHEMA);
                Map data = new HashMap();
                data.put("title", startCollect.getTitle());
                data.put("end_time", startCollect.getEndTime());

                if (userId != null) {
                    Unit unit = new Unit();
                    unit.setId(userId);
                    unit.setType(Constants.USER_TYPE + "");
                    data.put("client", JSON.toJSONString(Arrays.asList(unit)));
                    String salesman = baseCollectionService.findSalesman(clientName);
                    if (salesman == null) {
                        salesman = baseCollectionService.findSalesmanFromSecondClient(clientName);
                    }
                    data.put("salesman", salesman);
                    data.put("welfare_operator", welfareOperator);
                    data.put("remarks", startCollect.getRemarks());
                    data.put("start_collect_id", bizObjectId);

                    model.put(data);
                    log.info("创建基数采集数据，客户名称为：" + baseInfoCollections.get(0).getClientName());
                    String clientId = this.getBizObjectFacade().saveBizObjectModel(UserUtils.getUserId(getUserId()), model, "id");
                    log.info("创建基数采集数据，客户Id为：");
                    log.info("发起基数采集流程，客户名称为：" + baseInfoCollections.get(0).getClientName() + "数据id：" + clientId);
                    this.getWorkflowInstanceFacade().startWorkflowInstance(userModel.getDepartmentId(), userModel.getId(), "collect", clientId, true);
                    try {
                        //导出到excel
                        String id = UUID.randomUUID().toString().replace("-", "");
                        String pathName = "D:\\upload\\" + id + fileName;
                        File excelFile = new File(pathName);
                        if (!excelFile.exists()) {
                            excelFile.createNewFile();
                        }
                        Workbook workbook = null;
                        if (pathName.endsWith(".xlsx")) {
                            workbook = new XSSFWorkbook();
                        }
                        if (pathName.endsWith(".xls")) {
                            workbook = new HSSFWorkbook();
                        }
                        Sheet sheet = workbook.createSheet();
                        Row firstRow = sheet.createRow(0);//第一行表头
                        //获取对象属性有10个 则是社保申报
                        if (baseInfoCollections.get(0).getClass().getDeclaredFields().length == 10) {
                            for (int i = 0; i < headName.length; i++) {
                                Cell cell = firstRow.createCell(i);
                                cell.setCellValue(headName[i]);
                                cell.setCellStyle(ExportExcel.createHeadCellStyle(workbook));
                            }
                        }
                        //对象属性有11个，则是公积金申报
                        if (baseInfoCollections.get(0).getClass().getDeclaredFields().length == 11) {
                            for (int i = 0; i < headName.length; i++) {
                                Cell cell = firstRow.createCell(i);
                                cell.setCellValue(headName[i]);
                                cell.setCellStyle(ExportExcel.createHeadCellStyle(workbook));
                            }
                        }
                        //设置表头行高度
                        firstRow.setHeight((short) ((22.22 * 20)));
                        Integer serialNum = 1;
                        for (BaseInfoCollection baseInfoCollection : baseInfoCollections) {
                            baseInfoCollection.setSerialNum(serialNum++);
                            fillExcelDate(baseInfoCollection, sheet, workbook, 0);
                        }
                        //设置自适应宽度，有些问题
                        ExportExcel.setSizeColumn(sheet, sheet.getLastRowNum());
                        //设置具体列的宽度
                        sheet.setColumnWidth(1, (int) (40.33 + 0.71) * 256);
                        sheet.setColumnWidth(2, (int) (31.44 + 0.71) * 256);
                        sheet.setColumnWidth(4, (int) (24.33 + 0.71) * 256);
                        OutputStream out = null;
                        try {
                            out = new FileOutputStream(excelFile);
                            workbook.write(out);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //将生成的excel导入到附件表
                        //创建数据的引擎类
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", UUID.randomUUID().toString().replace("-", ""));
                        map.put("bizObjectId", clientId);
                        map.put("bizPropertyCode", "collect_template");
                        map.put("fileSize", excelFile.length());
                        if (fileName.endsWith(".xls")) {
                            map.put("mimeType", "application/xls");
                        } else {
                            map.put("mimeType", "application/xlsx");
                        }
                        // map.put("creater", userId);
                        map.put("creater", UserUtils.getUserId(getUserId()));
                        map.put("createdTime", new Date());
                        map.put("name", fileName);
                        map.put("refId", id);
                        map.put("schemaCode", "submit_collect");
                        //插入附件表
                        baseCollectionService.insertAttachment(map);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                return this.getOkResponseResult("error", "excel数据为空");
            }
        }
        return this.getOkResponseResult("success", "成功");
    }

    /**
     * 更新汇总标表
     * @param collectParams 封装前段传参
     * @return
     * @throws IOException
     */
    @PostMapping("/updateBaseNumInfo")
    public ResponseResult<Object> updateBaseNumInfo(@RequestBody CollectParams collectParams) throws IOException {
        log.info("更新汇总基数采集");
        Attachment attachment = null;
        if (collectParams.getFormType().equals("基数采集")) {
            attachment = attachmentService.getFileName(collectParams.getSubmit_collect(), "submit_collect", "collect_data");
        }
        if (collectParams.getFormType().equals("基数采集信息修改")) {
            attachment = attachmentService.getFileName(collectParams.getId(), "collect_update", "update_collect");
        }
        log.info("带个更新的附件：" + attachment);
        if (attachment == null || StringUtils.isBlank(attachment.getName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }
        log.info("fileName" + attachment.getName());
        log.info("refId:" + attachment.getRefId());
        String fileName = attachment.getName();
        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        String pathName = "D:\\upload\\" + attachment.getRefId() + fileName;
        File file = new File(pathName);
        if (!file.exists()) {
            return this.getErrResponseResult(null, 404L, pathName + "为空");
        }
        FileInputStream fis = new FileInputStream(file);
        List<ExcelHead> excelHeads = new ArrayList<>();
        String[] entityNameGjj = {"serialNum", "entrustedUnit", "clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum", "nowBaseNum", "remarks"};
        String[] entityNameSb = {"serialNum", "entrustedUnit", "clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum", "nowBaseNum", "providentFundProportion", "remarks"};
        //动态获取表头信息
        List<String> headNameList = ParseExcelUtils.getHeadName(fileName, fis);

        String[] headName = new String[headNameList.size()];
        headName = headNameList.toArray(headName);
        ExcelHead excelHead = null;
        for (int i = 0; i < headName.length; i++) {
            if (headName.length == 10) {
                excelHead = new ExcelHead(headName[i], entityNameGjj[i]);
            }
            if (headName.length == 11) {
                excelHead = new ExcelHead(headName[i], entityNameSb[i]);
            }
            excelHeads.add(excelHead);
        }
        List<BaseInfoCollection> clientBaseNumInfo = null;
        FileInputStream is = new FileInputStream(file);
        try {
            clientBaseNumInfo = ParseExcelUtils.readExcelToEntity(BaseInfoCollection.class, is, pathName, excelHeads);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        if (clientBaseNumInfo.isEmpty() || clientBaseNumInfo == null){
            return this.getOkResponseResult("error", "excel映射的对象集合为空");
        }
        for (BaseInfoCollection baseInfoCollection : clientBaseNumInfo) {
            baseInfoCollection.setId(UUID.randomUUID().toString().replace("-", ""));
            baseInfoCollection.setStart_collect_id(collectParams.getStart_collect_id());
        }
        //基数采集执行
        if (collectParams.getFormType().equals("submit_collect")) {
            log.info("将excel中导入信息添加到数据库中");
            //将excel信息添加到数据库中
            baseCollectionService.insertCollectInfo(clientBaseNumInfo);
            log.info("excel中信息添加完成");
        }
        //基数采集信息修改执行
        if (collectParams.getFormType().equals("collect_update")) {
            //通过身份证号查汇总表的数据
            log.info("开始通过身份证号查询汇总表中的数据");
            List<BaseInfoCollection> collectInTotalInfo = baseCollectionService.findBaseCollectInfoFromTotalInfo(clientBaseNumInfo, collectParams.getStart_collect_id());
            log.info("查询的结果为" + collectInTotalInfo);
            log.info("开始删除在汇总表中查询的数据");
            //删除在汇总表中查到的数据
            if (collectInTotalInfo != null && collectInTotalInfo.size() > 0) {
                baseCollectionService.deleteFoundCollectInfo(collectInTotalInfo);
            }
            log.info("删除成功");
            //将修改信息批量插入到汇总表
            log.info("开始插入需要修改的数据，数据为" + clientBaseNumInfo);
            baseCollectionService.insertCollectInfo(clientBaseNumInfo);
            return this.getOkResponseResult("success", "更新成功");
        }

        return this.getOkResponseResult("success", "成功");
    }

    public <T> void fillExcelDate(T clazz, Sheet sheet, Workbook workbook, int rowNum) {
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
        int cluNum = sheet.getRow(rowNum).getLastCellNum();
        for (int i = 0; i < cluNum; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            cell.setCellValue(value);
            CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
            cell.setCellStyle(contentCellStyle);
        }
    }
}
