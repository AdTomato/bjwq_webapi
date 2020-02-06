package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.service.NationalDeliveryService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ReadExcelFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 14:42
 * @Description:
 */
@Service
public class NationalDeliveryServiceImpl implements NationalDeliveryService {

    @Resource
    ReadExcelFile nationalDeliveryReadExcelFile;

    @Override
    public void importEmployee(String fileName, String userId, String departmentId, BizObjectFacade bizObjectFacade, OrganizationFacade organizationFacade, WorkflowInstanceFacade workflowInstanceFacade) {
        File excelFile = FileOperateHelper.getFile(fileName);
        if (!excelFile.exists()) {
            throw new RuntimeException("上传的文件不存在");
        }
        Workbook workbook = null;
        try {
            workbook = ExcelUtils.getWorkbook(fileName, excelFile);

            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个sheet

            int rowNum = sheet.getLastRowNum();

            if (rowNum == 0)
                throw new RuntimeException("文件为空");

            Map<String, String> tableColumnComment = nationalDeliveryReadExcelFile.getTableColumnComment(Constants.NATIONWIDE_DISPATCH_TABLE_NAME);

            Map<Integer, String> cellMapRelationship = nationalDeliveryReadExcelFile.getDefineMapRelationship(sheet.getRow(0), tableColumnComment);
            List<BizObjectModel> models;
            Set<String> required = new HashSet<>();
            for (int i = 0; i <= rowNum / 1000; i++) {
                if (i == rowNum / 1000) {
                    models = nationalDeliveryReadExcelFile.readFileToModel(sheet, i * 1000 + 1, i * 1000 + rowNum % 1000 + 1, cellMapRelationship, Constants.NATIONWIDE_DISPATCH_SCHEMA, Constants.DRAFT_STATUS, required);
                } else {
                    models = nationalDeliveryReadExcelFile.readFileToModel(sheet, i * 1000 + 1, (i + 1) * 1000 + 1, cellMapRelationship, Constants.NATIONWIDE_DISPATCH_SCHEMA_WF, Constants.DRAFT_STATUS, required);
                }
                if (null != models && 0 != models.size()) {
                    nationalDeliveryReadExcelFile.startWorkflow(userId, departmentId, Constants.NATIONWIDE_DISPATCH_SCHEMA_WF, models, bizObjectFacade, workflowInstanceFacade);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件读取失败");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("格式转化失败");
        }
    }
}
