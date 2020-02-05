package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.service.ShDeleteEmployeeService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ShReadDeleteExcelFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 10:53
 * @Description: 减员(上海)service
 */
@Service
public class ShDeleteEmployeeServiceImpl implements ShDeleteEmployeeService {

    @Resource
    ShReadDeleteExcelFile shReadDeleteExcelFile;

    /**
     * @param fileName               : 导入的文件名
     * @param userId                 : 操作人的id
     * @param departmentId           : 操作人的部门id
     * @param bizObjectFacade        : 系统创建数据工具类
     * @param organizationFacade     : 系统组织架构工具类
     * @param workflowInstanceFacade : 系统流程实例工具类
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/5 10:53
     * @Description: 导入减员信息开启流程，流程状态设置成草稿状态
     */
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
            if (rowNum == 0) {
                throw new RuntimeException("文件为空");
            }
            Map<Integer, String> cellMapRelationship = shReadDeleteExcelFile.getDefineMapRelationship(sheet.getRow(0));
            List<BizObjectModel> models;
            Set<String> required = new HashSet<>();
            for (int i = 0; i <= rowNum / 1000; i++) {
                if (i == rowNum / 1000) {
                    models = shReadDeleteExcelFile.readFileToModel(sheet, i * 1000, i * 1000 + rowNum % 1000, cellMapRelationship, Constants.SH_DELETE_EMPLOYEE_SCHEMA, Constants.DRAFT_STATUS, required);
                } else {
                    models = shReadDeleteExcelFile.readFileToModel(sheet, i * 1000, i * 1000 + rowNum % 1000, cellMapRelationship, Constants.SH_DELETE_EMPLOYEE_SCHEMA, Constants.DRAFT_STATUS, required);
                }
                if (null != models && 0 != models.size()) {
                    shReadDeleteExcelFile.startShDeleteWorkflow(userId, departmentId, models, bizObjectFacade, workflowInstanceFacade);
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
