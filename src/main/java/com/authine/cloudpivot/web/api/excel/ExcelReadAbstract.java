package com.authine.cloudpivot.web.api.excel;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/4/2 21:34
 * @Description: excel表格的读取类
 */
public abstract class ExcelReadAbstract {

    public static final Map<String, ImportProgress> progress = new HashMap<>();

//    public

    private List<Map<String, Object>> readExcel(UserModel userModel, DepartmentModel departmentModel) {

        return null;
    }

    /**
     * @param fileName: 文件名
     * @Author: wangyong
     * @Date: 2020/4/2 22:02
     * @return: com.authine.cloudpivot.web.api.excel.ImportProgress
     * @Description: 获取导入进度
     */
    public static ImportProgress getProgress(String fileName) {
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

}
