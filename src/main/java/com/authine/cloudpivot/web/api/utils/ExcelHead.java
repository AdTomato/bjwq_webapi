package com.authine.cloudpivot.web.api.utils;

import lombok.Data;

/**
 * @ClassName ExcelHead
 * @Author:lfh
 * @Date:2020/3/13 10:34
 * @Description:
 **/
@Data
public class ExcelHead {

    private String excelName;             //Excel名
    private String entityName;            //实体类属性名
    private boolean required=false;      //值必填

    public ExcelHead(String excelName, String entityName) {
        this.excelName = excelName;
        this.entityName = entityName;
    }
}
