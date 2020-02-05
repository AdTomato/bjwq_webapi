package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.ParseException;
import java.util.*;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:08
 * @Description: 上海(增员)读取excel
 */
public abstract class ShReadExcelFile {

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
    public List<Map<String, Object>> readFile(Sheet sheet, Integer startRowNum, Integer endRowNum, Map<Integer, String> cellMapRelationship, Set<String> required) throws ParseException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = startRowNum; i < endRowNum; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> map = getRowData(row, cellMapRelationship, required);
            if (map != null) {
                // 不是空行，存储
                result.add(map);
            }
        }
        return result;
    }

    /**
     * @param sheet               : 需要读取的sheet
     * @param startRowNum         : 开始行
     * @param endRowNum           : 结束行
     * @param cellMapRelationship : 每列和数据库字段映射关系
     * @param schemaCode          : 表单编码
     * @param sequenceStatus      : 表单状态
     * @param required            : 必填项
     * @return : java.util.List<com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel>
     * @Author: wangyong
     * @Date: 2020/2/5 13:22
     * @Description: 将需要读取的excel中的sheet每一行转换成model
     */
    public List<BizObjectModel> readFileToModel(Sheet sheet, Integer startRowNum, Integer endRowNum, Map<Integer, String> cellMapRelationship, String schemaCode, String sequenceStatus, Set<String> required) throws ParseException {

        List<BizObjectModel> result = new ArrayList<>();
        for (int i = startRowNum; i < endRowNum; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> map = getRowData(row, cellMapRelationship, required);
            if (map != null) {
                // 不是空行，存储
                result.add(getModel(schemaCode, sequenceStatus, map));
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
    public Map<String, Object> getRowData(Row row, Map<Integer, String> cellMapRelationship, Set<String> required) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        int cellNum = row.getLastCellNum();
        int nullNum = 0;
        Boolean flag = true;  // 用于标记是否存在必填项没有填，默认不存在
        for (int j = 0; j < cellNum; j++) {
            String cellValue = row.getCell(j).getStringCellValue();
            if (StringUtils.isEmpty(cellValue)) {
                nullNum++;
                if (required.contains(cellMapRelationship.get(j))) {
                    // 该列是必填项，且必填项为空
                    flag = false;
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
        return result;
    }

    /**
     * @param row : 第一行内容
     * @return : java.util.Map<java.lang.Integer,java.lang.String>
     * @Author: wangyong
     * @Date: 2020/2/4 13:05
     * @Description: 数据库编码和列数进行映射
     */
    public Map<Integer, String> getDefineMapRelationship(Row row) {
        Map<Integer, String> result = new HashMap<>();

        int cellNum = row.getLastCellNum();
        for (int i = 0; i < cellNum; i++) {
            result.put(i, getCode(row.getCell(i).getStringCellValue().replace(" ", "")));
        }

        return result;
    }

    /**
     * @param map : model内部数据
     * @return : com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel
     * @Author: wangyong
     * @Date: 2020/2/4 14:12
     * @Description: 创建BizObjectModel
     */
    public BizObjectModel getModel(String schemaCode, String sequenceStatus, Map<String, Object> map) throws ParseException {

        BizObjectModel result = new BizObjectModel();
        result.setSchemaCode(schemaCode);
        // 设置草稿状态
        result.setSequenceStatus(sequenceStatus);
        result.put(map);

        return result;
    }

    protected abstract Object conversion(String key, Object value) throws ParseException;

    protected abstract String getCode(String celleName);

}
