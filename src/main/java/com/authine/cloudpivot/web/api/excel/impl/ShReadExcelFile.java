package com.authine.cloudpivot.web.api.excel.impl;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/4/13 15:09
 * @Description:
 */
public class ShReadExcelFile extends ExcelReadAbstract {
    @Override
    protected void setRequiredColumn(List<String> requiredColumn) {
        this.requiredColumn = requiredColumn;
    }

    @Override
    protected void setSequenceStatus(String sequenceStatus) {
        this.sequenceStatus = sequenceStatus;
    }

    @Override
    protected void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void read(UserModel userModel, DepartmentModel departmentModel, String fileName, String sheetName, Integer sheetIndex) {
        Sheet sheet = loadExcel(fileName, sheetName, sheetIndex);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum <= 2) {
            throw new RuntimeException("为空Excel表格");
        }
        List<Map<String , Object>> result = new ArrayList<>();
        Map<Integer, String> columnCode = getColumnCode(sheet, tableName);
        List<String> ids = new ArrayList<>();
        for (int i = 0; i <= lastRowNum / 1000; i++) {
            if (i == lastRowNum / 1000) {
                // 到达最后一行
                result = readExcel(userModel, departmentModel, columnCode, ids, sheet, fileName, i * 1000 + 2, i * 1000 + lastRowNum % 1000 + 2);
            } else {
                result = readExcel(userModel, departmentModel, columnCode, ids, sheet, fileName, i * 1000 + 2, (i + 1) * 1000 + 2);
            }
        }
    }
}
