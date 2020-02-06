package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.ColumnComment;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 14:06
 * @Description: 数据库表格service接口
 */
public interface TableService {


    /**
     * 获取数据库表格备注和列名的对应关系
     *
     * @param tableName : 表格名称
     * @return : 数据库表格备注和列名的对应关系
     */
    public List<ColumnComment> getTableColumnComment(String tableName);

}
