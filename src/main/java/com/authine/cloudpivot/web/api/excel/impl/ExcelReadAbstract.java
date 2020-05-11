package com.authine.cloudpivot.web.api.excel.impl;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.ImportProgress;
import com.authine.cloudpivot.web.api.excel.ExcelRead;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.service.TableService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.SystemDataSetUtils;
import io.swagger.models.auth.In;
import jodd.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author:wangyong
 * @Date:2020/4/2 21:34
 * @Description: excel表格的读取类
 */
public abstract class ExcelReadAbstract implements ExcelRead {

    public static final Map<String, ImportProgress> progress = new HashMap<>();

    public List<String> requiredColumn;

    public String sequenceStatus;

    public String tableName;

    @Autowired
    TableService tableService;

    /**
     * @param userModel:       用户model
     * @param departmentModel: 部门model
     * @param columnCode:      列编码
     * @param ids:             数据id集合
     * @param sheet:           Excel表格的sheet
     * @param fileName:        Excel表格名称
     * @param startIndex:      开始读取位置
     * @param endIndex:        结束读取位置
     * @Author: wangyong
     * @Date: 2020/4/13 15:01
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description:
     */
    @Override
    public List<Map<String, Object>> readExcel(UserModel userModel, DepartmentModel departmentModel, Map<Integer, String> columnCode, List<String> ids, Sheet sheet, String fileName, Integer startIndex, Integer endIndex) {
        List<Map<String, Object>> result = new ArrayList<>();
        ImportProgress ip = progress.get(fileName);

        for (int i = startIndex; i < endIndex; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> rowData = readOneRow(fileName, row, columnCode);
            ip.setSuccessNum(ip.getSuccessNum() + 1);
            if (rowData != null) {
                // 设置基础数据
                SystemDataSetUtils.dataSet(userModel, departmentModel, "", sequenceStatus, rowData);
                // 存储id
                ids.add(rowData.get("id") + "");
                result.add(rowData);
            }
        }
        return result;
    }

    /**
     * @param fileName:   文件名称
     * @param row:        excel的一行数据
     * @param columnCode: 表格编码
     * @Author: wangyong
     * @Date: 2020/4/13 14:27
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @Description: 读取一行数据
     */
    @Override
    public Map<String, Object> readOneRow(String fileName, Row row, Map<Integer, String> columnCode) {
        Map<String, Object> result = new HashMap<>();
        boolean flag = false;  // 用于验证是否为空行
        for (Integer index : columnCode.keySet()) {
            Cell cell = row.getCell(index);
            Object cellValue = null;
            if (cell != null) {
                CellType cellType = cell.getCellType();
                if (CellType.NUMERIC.name().equals(cellType.name())) {
                    // cell类型为数字或者时间
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // cell类型为时间
                        cellValue = cell.getDateCellValue();
                    } else {
                        // cell类型为数字
                        cell.setCellType(CellType.STRING);
                        cellValue = cell.getStringCellValue();
                    }
                } else if (cellType.STRING.name().equals(cellType.name())) {
                    cellValue = cell.getStringCellValue();
                } else {
                    cell.setCellType(CellType.STRING);
                    cellValue = cell.getStringCellValue();
                }
            }
            String codeName = columnCode.get(index);
            if (cellValue == null && requiredColumn != null && requiredColumn.contains(codeName)) {
                // 值不存在，且为必填项
                ImportProgress ip = progress.get(fileName);
                ip.setIsOver(true);
                ip.setFileReason("第" + (ip.getSuccessNum() + 2) + "行");
            }
            if (cellValue != null) {
                flag = true;  // 存在值
            }
        }
        if (flag) {
            // 不是空行，返回这一行的结果
            return result;
        } else {
            // 空行，返回空值
            return null;
        }
    }

    /**
     * @param sheet:     sheet表格
     * @param tableName: 导入的表格名称
     * @Author: wangyong
     * @Date: 2020/4/11 14:45
     * @return: java.util.Map<java.lang.Integer, java.lang.String>
     * @Description: 获取sheet中的每一列的编码
     */
    @Override
    public Map<Integer, String> getColumnCode(Sheet sheet, String tableName) {
        Map<Integer, String> result = new HashMap<>();
        List<String> tableColumn = tableService.getTableColumn(tableName);
        // 获取第一行的内容，第一行用于存储表格编码
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            String columnCode = row.getCell(i).getStringCellValue();
            if (!StringUtil.isEmpty(columnCode) && tableColumn.contains(columnCode)) {
                result.put(i, columnCode);
            }
        }
        return result;
    }

    /**
     * @param fileName:   文件名称
     * @param sheetName:  sheet名称
     * @param sheetIndex: sheet下标
     * @Author: wangyong
     * @Date: 2020/4/11 14:46
     * @return: org.apache.poi.ss.usermodel.Sheet
     * @Description: 加载excel表格
     */
    @Override
    public Sheet loadExcel(String fileName, String sheetName, Integer sheetIndex) {
        ImportProgress ip = null;
        synchronized (ExcelReadAbstract.class) {
            ip = progress.get(fileName);
            if (ip == null) {
                ip = new ImportProgress();
                ip.setIsOver(false);
            } else {
                if (ip.getIsOver()) {
                    progress.remove(fileName);
                    throw new RuntimeException("已经导入完成");
                } else {
                    throw new RuntimeException("正在导入中");
                }
            }
        }
        File excelFile = FileOperateHelper.getFile(fileName);
        if (!excelFile.exists()) {
            throw new RuntimeException("上传的文件不存在");
        }
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = ExcelUtils.getWorkbook(fileName, excelFile);
            if (StringUtil.isEmpty(sheetName)) {
                sheet = workbook.getSheet(sheetName);
            }
            if (sheetIndex != null) {
                sheet = workbook.getSheetAt(sheetIndex);
            }
            if (sheet == null) {
                throw new RuntimeException("sheet不存在,请检查sheet名称或者sheet下标是否存在");
            }
            ip.setAllNum(sheet.getLastRowNum() - 2);
            ip.setFileNum(0);
            ip.setSuccessNum(0);
            ip.setFileReason("");
            ip.setIsOver(false);
            return sheet;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取导入文件失败\n" + e.getMessage());
        }
    }

    /**
     * @param fileName: 文件名
     * @Author: wangyong
     * @Date: 2020/4/2 22:02
     * @return: com.authine.cloudpivot.web.api.entity.ImportProgress
     * @Description: 获取导入进度
     */
    @Override
    public ImportProgress getProgress(String fileName) {
        if (progress.containsKey(fileName)) {
            // 存在
            ImportProgress p = progress.get(fileName);
            if (p.isOver) {
                progress.remove(fileName);
                return p;
            }
        }
        return null;
    }

    protected abstract void setRequiredColumn(List<String> requiredColumn);

    protected abstract void setSequenceStatus(String sequenceStatus);

    protected abstract void setTableName(String tableName);

}
