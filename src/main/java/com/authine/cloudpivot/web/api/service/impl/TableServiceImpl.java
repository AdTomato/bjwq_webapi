package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.ColumnComment;
import com.authine.cloudpivot.web.api.mapper.TableMapper;
import com.authine.cloudpivot.web.api.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 14:06
 * @Description: 数据库表格service
 */
@Service
public class TableServiceImpl implements TableService {

    @Resource
    TableMapper tableMapper;

    /**
     * @param tableName : 表格名称
     * @return : java.util.List<com.authine.cloudpivot.web.api.entity.ColumnComment>
     * @Author: wangyong
     * @Date: 2020/2/6 14:13
     * @Description: 获取数据库表格备注和列名的对应关系
     */
    @Override
    public List<ColumnComment> getTableColumnComment(String tableName) {
        return tableMapper.getTableColumnComment(tableName);
    }

    /**
     * @param tableName: 表格名称
     * @Author: wangyong
     * @Date: 2020/3/23 10:43
     * @return: java.util.List<java.lang.String>
     * @Description: 获取数据库表格的列名
     */
    @Override
    public List<String> getTableColumn(String tableName) {
        return tableMapper.getTableColumn(tableName);
    }
}
