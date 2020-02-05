package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.service.ShAddEmployeeService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ShReadAddExcelFile;
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
 * @Date: 2020-02-04 09:35
 * @Description:
 */
@Service
public class ShAddEmployeeServiceImpl implements ShAddEmployeeService {

    @Resource
    ShReadAddExcelFile shReadAddExcelFile;

    /**
     * @param userId             : 导入人id
     * @param bizObjectFacade    : 操作数据的工具类
     * @param organizationFacade : 组织架构工具类
     * @return : void
     * @Author: wangyong
     * @Date: 2020/2/4 9:35
     * @Description: 导入上海增员信息，数据状态置位草稿状态
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
            Map<Integer, String> cellMapRelationship = shReadAddExcelFile.getDefineMapRelationship(sheet.getRow(0));
            List<BizObjectModel> models;
            Set<String> required = new HashSet<>();  // 必填项
            // 分批读取excel表格
            for (int i = 0; i <= rowNum / 1000; i++) {
                if (i == rowNum / 1000) {
                    models = shReadAddExcelFile.readFileToModel(sheet, i * 1000 + 1, i * 1000 + rowNum % 1000 + 1, cellMapRelationship, Constants.SH_ADD_EMPLOYEE_SCHEMA, Constants.DRAFT_STATUS, required);
                } else {
                    models = shReadAddExcelFile.readFileToModel(sheet, i * 1000 + 1, (i + 1) * 1000 + 1, cellMapRelationship, Constants.SH_ADD_EMPLOYEE_SCHEMA, Constants.DRAFT_STATUS, required);
                }
                // 开启流程
                if (null != models && 0 != models.size())
                    shReadAddExcelFile.startShAddWorkflow(userId, departmentId, models, bizObjectFacade, workflowInstanceFacade);
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
