package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ColumnComment;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 13:46
 * @Description:
 */
public interface TableMapper {

    /**
     * 获取数据库表格备注和列名的对应关系
     *
     * @param tableName : 表格名称
     * @return : 数据库表格备注和列名的对应关系
     */
    public List<ColumnComment> getTableColumnComment(String tableName);

    /**
     * 获取和数据库表格列名
     *
     * @param tableName 表格名称
     * @return 数据库表格列名
     */
    public List<String> getTableColumn(String tableName);


}
