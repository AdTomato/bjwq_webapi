package com.authine.cloudpivot.web.api.excel;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.ImportProgress;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/4/11 13:51
 * @Description: 读取excel表格
 */
public interface ExcelRead {

    Sheet loadExcel(String fileName, String sheetName, Integer sheetIndex);

    Map<Integer, String> getColumnCode(Sheet sheet, String tableName);

    List<Map<String, Object>> readExcel(UserModel userModel, DepartmentModel departmentModel, Map<Integer, String> columnCode, List<String> ids, Sheet sheet, String fileName, Integer startIndex, Integer endIndex);

    ImportProgress getProgress(String fileName);

    Map<String, Object> readOneRow(String fileName, Row row, Map<Integer, String> columnCode);

    void read(UserModel userModel, DepartmentModel departmentModel, String fileName, String sheetName, Integer sheetIndex);

}
