package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 13:50
 * @Description: 表列名和字段名
 */
@Data
public class ColumnComment {

    private String columnName;
    private String columnComment;

}
