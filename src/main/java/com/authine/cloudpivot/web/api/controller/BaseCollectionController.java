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
import com.authine.cloudpivot.web.api.service.AttachmentService;
import com.authine.cloudpivot.web.api.service.BaseCollectionService;
import com.authine.cloudpivot.web.api.utils.*;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    @PostMapping("/getBaseNunInfo")
    public ResponseResult<Object> getBaseNumInfo(@RequestBody BaseControllerGetBaseNunInfo baseControllerGetBaseNunInfo) throws IOException {
        String bizObjectId = baseControllerGetBaseNunInfo.getBizObjectId();
        List<String> client = baseControllerGetBaseNunInfo.getClient();

        Attachment attachment = attachmentService.getFileName(bizObjectId, "start_collect", "collect_template");
        if (attachment != null && StringUtils.isBlank(attachment.getName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        String fileName = attachment.getName();

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        String filePath = "D:\\upload\\";
        File file = new File(filePath + attachment.getRefId() + fileName);
        List<ExcelHead> excelHeads = new ArrayList<>();
        //让表头和javabean的属性对应
        String[] headName = {"客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)"};
        String[] entityName = {"clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum"};
        for (int i = 0; i < headName.length; i++) {
            ExcelHead excelHead = new ExcelHead(headName[i], entityName[i]);
            excelHeads.add(excelHead);
        }
        List<BaseInfoCollection> baseInfoCollectionList = null;
        try {
            baseInfoCollectionList = ParseExcelUtils.readExcelToEntity(BaseInfoCollection.class, new FileInputStream(file), fileName, excelHeads);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StartCollect startCollect = baseCollectionService.getStartCollectById(bizObjectId);

        if (null == startCollect) {
            log.info("查询发起基数采集数据为空");
            return getErrResponseResult(null, 404L, "查询发起基数采集数据为空");
        }

        log.info("开始获取需要采集的信息");

        log.info("创建基数采集数据");

        UserModel userModel = this.getOrganizationFacade().getUserById(UserUtils.getUserId(getUserId()));
        Map<String, List<BaseInfoCollection>> baseInfoCollectionMap = new HashMap<>();
        // for (String userId : client) {
        //     String clientName = this.getOrganizationFacade().getUserById(userId).getName();
        //拿到公司名称， 查客户账号表中是否有这个公司账号，如果账号不存在，从客户档案表中查业务员（业务员存在多个）
        /**
         *
         */
        List<BaseInfoCollection> collectClients = null;
        if (baseInfoCollectionList != null && baseInfoCollectionList.size() > 0) {
            for (BaseInfoCollection baseInfoCollection : baseInfoCollectionList) {
                /*if (clientName.equals(baseInfoCollection.getClientName())) {
                    collectClients.add(baseInfoCollection);
                }*/
                if (baseInfoCollectionMap.containsKey(baseInfoCollection.getClientName())) {
                    baseInfoCollectionMap.get(baseInfoCollection.getClientName()).add(baseInfoCollection);
                } else {
                    collectClients = new ArrayList<>();
                    collectClients.add(baseInfoCollection);
                    baseInfoCollectionMap.put(baseInfoCollection.getClientName(), collectClients);
                }
                baseInfoCollectionMap.put(baseInfoCollection.getClientName(), collectClients);
            }
        }
        for (String clientName : baseInfoCollectionMap.keySet()) {

            List<BaseInfoCollection> baseInfoCollections = baseInfoCollectionMap.get(clientName);
            if (collectClients != null && collectClients.size() > 0) {
                log.info("将信息采集信息导出到excel中");
                //通过发起基数采集的id查询基数采集对应客户的id
                // String companyName = collectClients.get(0).getClientName();
                String userId = baseCollectionService.findCompanyName(clientName);

                BizObjectModel model = new BizObjectModel();
                model.setSequenceStatus(Constants.PROCESSING_STATUS);
                model.setSchemaCode(Constants.SUBMIT_COLLECT_SCHEMA);
                Map data = new HashMap();
                data.put("title", startCollect.getTitle());
                data.put("end_time", startCollect.getEndTime());
                if (userId != null) {
                    // 客户
                    Unit unit = new Unit();
                    unit.setId(userId);
                    unit.setType(Constants.USER_TYPE + "");
                    data.put("client", JSON.toJSONString(Arrays.asList(unit)));
                }else {
                    //客户在客户表里不存在 查询一级客户表中客户对应的业务员
                    String salesman =baseCollectionService.findSalesman(clientName);
                    data.put("client", salesman);
                }
                data.put("remarks", startCollect.getRemarks());
                data.put("start_collect_id", bizObjectId);

                model.put(data);
                log.info("创建基数采集数据，客户名称为：" + baseInfoCollections.get(0).getClientName());

                String clientId = this.getBizObjectFacade().saveBizObjectModel(UserUtils.getUserId(getUserId()), model, "id");
                log.info("创建基数采集数据，客户Id为：");
                log.info("发起基数采集流程，客户名称为：" + baseInfoCollections.get(0).getClientName() + "数据id：" + clientId);
                this.getWorkflowInstanceFacade().startWorkflowInstance(userModel.getDepartmentId(), userModel.getId(), "collect", clientId, true);

//              String clientId = baseCollectionService.findClientIds(bizObjectId, companyName);
                //excel标题
                String[] title = {"客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)", "新基数", "新比例"};
                String header = "社保";
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
                    // ExportExcel.exportExcel(header, title, collectClients, new FileOutputStream(excelFile), "");
                    Sheet sheet = workbook.createSheet();
                    Row firstRow = sheet.createRow(0);//第一行表头
                    for (int i = 0; i < title.length; i++) {
                        firstRow.createCell(i).setCellValue(title[i]);
                    }

                    for (BaseInfoCollection baseInfoCollection : baseInfoCollections) {
                        Field[] declaredFields = baseInfoCollection.getClass().getDeclaredFields();
                        String[] values = new String[declaredFields.length];
                        try {
                            Field.setAccessible(declaredFields, true);
                            for (int i = 0; i < declaredFields.length; i++) {
                                values[i] = (String) declaredFields[i].get(baseInfoCollection);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        //追加数据的开始行
                        int start = sheet.getLastRowNum() + 1;
                        Row row = sheet.createRow(start);
                        int cluNum = sheet.getRow(0).getLastCellNum();
                        for (int i = 0; i < cluNum; i++) {
                            // row.createCell(i).setCellValue(values[i]);
                            Cell cell = row.createCell(i);
                            cell.setCellValue(values[i]);
                            String value = ExcelUtils.getValue(cell);
                            cell.setCellValue(value);
                        }
                    }

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
            }else {
                return this.getOkResponseResult("error", "excel数据为空");
            }
        }


        //  }

        return this.getOkResponseResult("success", "成功");

    }
    @PostMapping("/updateBaseNumInfo")
    public ResponseResult<Object> updateBaseNumInfo(@RequestParam String bizObjectId, @RequestParam String startCollectId) throws IOException {
        log.info("更新汇总基数采集");
        Attachment attachment = attachmentService.getFileName(bizObjectId, "submit_collect", "collect_data");
        log.info("带个更新的附件：" + attachment);
        if (attachment == null || StringUtils.isBlank(attachment.getName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        String fileName = attachment.getName();

        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        log.info("fileName" + attachment.getName());
        log.info("refId:" + attachment.getRefId());
        String pathName = "D:\\upload\\" + attachment.getRefId() + fileName;
        File file = new File(pathName);

        if (!file.exists()) {
            return this.getErrResponseResult(null, 404L, pathName + "为空");
        }

        List<ExcelHead> excelHeads = new ArrayList<>();
        String[] headName = {"客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)", "新基数", "新比例"};
        String[] entityName = {"clientName", "employeeName", "identityNo", "welfareHandler", "salesman", "primaryBaseNum", "nowBaseNum", "newProportion"};
        for (int i = 0; i < headName.length; i++) {
            ExcelHead excelHead = new ExcelHead(headName[i], entityName[i]);
            excelHeads.add(excelHead);
        }
        List<BaseInfoCollection> clientBaseNumInfo = null;
        try {
            clientBaseNumInfo = ParseExcelUtils.readExcelToEntity(BaseInfoCollection.class, new FileInputStream(file), pathName, excelHeads);
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (BaseCollectionController.class) {
            //判断附件表是否已经生成数据
            Attachment totalAttachment = attachmentService.getFileName(startCollectId, "start_collect", "collect_data");
//            String attachmentId = baseCollectionService.findAttachment(bizObjectId);
            Workbook workbook = null;
            Sheet sheet = null;
            //第一次加入数据，进行创建excel
            File totalFile = null;
            String attachmentId;
            if (totalAttachment == null) {

                String excelName = "基数采集汇总.xls";
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                String totalPathName = "D:\\upload\\" + id + excelName;
                totalFile = new File(totalPathName);
                totalFile.createNewFile();
                workbook = new HSSFWorkbook();

                //excel标题
                String[] title = {"客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)", "新基数", "新比例"};
                sheet = workbook.createSheet();
                Row firstRow = sheet.createRow(0);//第一行表头
                for (int i = 0; i < title.length; i++) {
                    firstRow.createCell(i).setCellValue(title[i]);
                }

                //导出到excel
                //将生成的excel导入到附件表
                Map<String, Object> map = new HashMap<>();
                attachmentId = UUID.randomUUID().toString().replace("-", "");
                map.put("id", attachmentId);
                map.put("bizObjectId", startCollectId);
                map.put("bizPropertyCode", "collect_data");
                map.put("fileSize", totalFile.length());
                if (excelName.endsWith("xls")) {
                    map.put("mimeType", "application/xls");
                } else {
                    map.put("mimeType", "application/xlsx");
                }
                map.put("creater", UserUtils.getUserId(getUserId()));
                map.put("createdTime", new Date());
                map.put("name", excelName);
                map.put("refId", id);
                map.put("schemaCode", "start_collect");
                baseCollectionService.insertAttachment(map);
            } else {
                //已经创建附件 ，通过id 查询 文件名 ，对附件进行追加
                String excelName = "D:\\upload\\" + totalAttachment.getRefId() + totalAttachment.getName();
                totalFile = new File(excelName);
                attachmentId = totalAttachment.getId();
                try {
                    workbook = new XSSFWorkbook(new FileInputStream(totalFile));
                } catch (Exception e) {
                    workbook = new HSSFWorkbook(new FileInputStream(totalFile));
                }

                sheet = workbook.getSheetAt(0);
            }


            for (BaseInfoCollection baseInfoCollection : clientBaseNumInfo) {
                Field[] declaredFields = baseInfoCollection.getClass().getDeclaredFields();
                String[] values = new String[declaredFields.length];
                try {
                    Field.setAccessible(declaredFields, true);
                    for (int i = 0; i < declaredFields.length; i++) {
                        values[i] = (String) declaredFields[i].get(baseInfoCollection);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //追加数据的开始行
                int start = sheet.getLastRowNum() + 1;
                Row row = sheet.createRow(start);
                int cluNum = sheet.getRow(0).getLastCellNum();
                for (int i = 0; i < cluNum; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(values[i]);
                    String value = ExcelUtils.getValue(cell);
                    cell.setCellValue(value);
                }
            }

            OutputStream out = null;
            try {
                out = new FileOutputStream(totalFile);
                workbook.write(out);
                out.close();

                if (attachmentId != null && attachmentId.length() > 0) {
                    //更新附件表文件大小
                    baseCollectionService.updateFileSize(attachmentId, totalFile.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.getOkResponseResult("success", "成功");
    }
}
