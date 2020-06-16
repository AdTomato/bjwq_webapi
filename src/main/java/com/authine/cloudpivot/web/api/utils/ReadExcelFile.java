package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.ColumnComment;
import com.authine.cloudpivot.web.api.service.TableService;
import lombok.Data;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:08
 * @Description: 上海(增员)读取excel
 */
@Data
public abstract class ReadExcelFile {

    private String tableName;
    private String workflowCode;

    @Autowired
    TableService tableService;

    /**
     * @param sheet               : 需要读取的sheet
     * @param startRowNum         : 开始行
     * @param endRowNum           : 结束行
     * @param cellMapRelationship : 列与数据库字段的映射关系
     * @param required            : 必填项
     * @return : java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: wangyong
     * @Date: 2020/2/5 13:25
     * @Description: 将excel中的需要读取的sheet转换为List对象
     */
    public List<Map<String, Object>> readFile(UserModel user, DepartmentModel department, String sequenceStatus, Sheet sheet, Integer startRowNum, Integer endRowNum, Map<Integer, String> cellMapRelationship, Map<String, String> tableColumnComment, List<String> ids, Set<String> required) throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = startRowNum; i < endRowNum; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> map = getRowData(user, department, sequenceStatus, row, cellMapRelationship, tableColumnComment, required);
            if (map != null) {
                // 不是空行，存储
                ids.add(map.get("id") + "");
                result.add(map);
            }
        }
        return result;
    }


    /**
     * @param row                 : 读取的行
     * @param cellMapRelationship : 列与数据库映射关系
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     * @Author: wangyong
     * @Date: 2020/2/5 13:27
     * @Description: 读取excel一行数据
     */
    private Map<String, Object> getRowData(UserModel user, DepartmentModel department, String sequenceStatus, Row row, Map<Integer, String> cellMapRelationship, Map<String, String> tableColumnComment, Set<String> required) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        copyKey(result, tableColumnComment);
        int cellNum = row.getLastCellNum();
        int nullNum = 0;
        Boolean flag = true;  // 用于标记是否存在必填项没有填，默认不存在
        for (int j = 0; j < cellNum; j++) {
            Cell cell = row.getCell(j);
            String cellValue = "";
            if (null != cell) {
                // 不为空
                int cellType = cell.getCellType();

                if (CellType.NUMERIC.getCode() == cellType) {
                    // 数字类型
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 是时间
                        Date date = cell.getDateCellValue();
                        if (null != date) {
                            cellValue = DateFormatUtils.format(date, "yyyy-MM-dd");
                        }
                    } else {
                        cell.setCellType(CellType.STRING);
                        cellValue = cell.getStringCellValue();
                    }
                } else {
                    cellValue = cell.getStringCellValue();
                }

                if (StringUtils.isEmpty(cellValue)) {
                    nullNum++;
                    if (required.contains(cellMapRelationship.get(j))) {
                        // 该列是必填项，且必填项为空
                        flag = false;
                        break;
                    }
                }
            }

            result.put(cellMapRelationship.get(j), conversion(cellMapRelationship.get(j), cellValue));
        }
        if (nullNum == cellNum) {
            // 空行，不存储
            result = null;
        }
        if (!flag) {
            throw new RuntimeException("第：" + row.getRowNum() + "行存在必填项没有填写");
        }
        SystemDataSetUtils.dataSet(user, department, "", sequenceStatus, result);
        return result;
    }


    /**
     * @param row : 第一行内容
     * @return : java.util.Map<java.lang.Integer,java.lang.String>
     * @Author: wangyong
     * @Date: 2020/2/4 13:05
     * @Description: 数据库编码和列数进行映射
     */
    public Map<Integer, String> getDefineMapRelationship(Row row, Map<String, String> columnComment) {
        Map<Integer, String> result = new HashMap<>();

        int cellNum = row.getLastCellNum();
        for (int i = 0; i < cellNum; i++) {
            String cellName = row.getCell(i).getStringCellValue().replace(" ", "");
            String cellCode = getCode(cellName, columnComment);
            if (null != cellCode) {
                result.put(i, cellCode);
            } else {
                throw new RuntimeException("列名“" + cellName + "”不存在");
            }
        }
        return result;
    }

    /**
     * @param celleName     : 列名
     * @param columnComment : 列名和编码对应关系
     * @return : java.lang.String
     * @Author: wangyong
     * @Date: 2020/2/6 15:10
     * @Description: 获取当前列名对应的编码
     */
    private String getCode(String celleName, Map<String, String> columnComment) {

        if (columnComment.containsKey(celleName)) {
            return columnComment.get(celleName);
        } else {
            return null;
        }
    }

    /**
     * @param tableName : 表格名称
     * @return : java.util.Map<java.lang.String,java.lang.String>
     * @Author: wangyong
     * @Date: 2020/2/6 15:11
     * @Description: 获取当前表的列名和列编码对应关系
     */
    public Map<String, String> getTableColumnComment(String tableName) {
        Map<String, String> result = new HashMap<>();

        List<ColumnComment> tableColumnComment = tableService.getTableColumnComment(tableName);
        for (ColumnComment columnComment : tableColumnComment) {
            if (!StringUtils.isEmpty(columnComment.getColumnComment())) {
                result.put(columnComment.getColumnComment(), columnComment.getColumnName());
            }
        }
        return result;
    }

    /**
     * @param tableName: 表格名称
     * @Author: wangyong
     * @Date: 2020/3/23 10:45
     * @return: java.util.List<java.lang.String>
     * @Description: 数据库表格列名
     */

    public List<String> getTableColumn(String tableName) {
        return tableService.getTableColumn(tableName);
    }

    private void copyKey(Map<String, Object> result, Map<String, String> cellMapRelationship) {
        for (String value : cellMapRelationship.values()) {
            result.put(value, null);
        }
    }


    /**
     * 需要转换的列
     *
     * @param key   : 列编码
     * @param value : 值
     * @return 转换过后的值
     * @throws ParseException
     */
    protected abstract Object conversion(String key, Object value) throws ParseException;

}
