package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.service.ExcelFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-03-02 16:22
 * @Description:
 */
@Component
@Slf4j
public class ExcelFileUtils {

    @Autowired
    ExcelFileService excelFileService;

    public void insertData(String fileName, String userId, String departmentId, BizObjectFacade bizObjectFacade, OrganizationFacade organizationFacade, WorkflowInstanceFacade workflowInstanceFacade, ReadExcelFile readExcelFile) {
        File excelFile = FileOperateHelper.getFile(fileName);
        if (!excelFile.exists()) {
            throw new RuntimeException("上传的文件不存在");
        }

        Workbook workbook = null;
        try {
            workbook = ExcelUtils.getWorkbook(fileName, excelFile);
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个sheet
            int rowNum = sheet.getLastRowNum();
            if (rowNum == 0) {
                throw new RuntimeException("文件为空");
            }
            Map<String, String> tableColumnComment = readExcelFile.getTableColumnComment(readExcelFile.getTableName());
            Map<Integer, String> cellMapRelationship = readExcelFile.getDefineMapRelationship(sheet.getRow(0), tableColumnComment);
            List<Map<String, Object>> models = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            UserModel user = organizationFacade.getUser(userId);
            DepartmentModel department = organizationFacade.getDepartment(user.getDepartmentId());
            Set<String> required = new HashSet<>();  // 必填项
            // 分批读取excel表格
            for (int i = 0; i <= rowNum / 1000; i++) {
                if (i == rowNum / 1000) {
                    models = readExcelFile.readFile(user, department, Constants.DRAFT_STATUS, sheet, i * 1000 + 1, i * 1000 + rowNum % 1000 + 1,  cellMapRelationship, tableColumnComment, ids, required);
                } else {
                    models = readExcelFile.readFile(user, department, Constants.DRAFT_STATUS, sheet, i * 1000 + 1, (i + 1) * 1000 + 1, cellMapRelationship, tableColumnComment, ids, required);
                }
                // 开启流程
                if (null != models && 0 != models.size()) {
                    // 新增数据
                    log.info("保存数据：" + models);
                    excelFileService.insertData(departmentId, userId, readExcelFile.getWorkflowCode(), false, workflowInstanceFacade, readExcelFile.getTableName(), models);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取导入文件失败\n" + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("格式转换错误\n" + e.getMessage());
        }
    }

}
