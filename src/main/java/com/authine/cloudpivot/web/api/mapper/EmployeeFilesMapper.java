package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.sun.istack.NotNull;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 09:47
 * @Description: 员工档案mapper
 */
public interface EmployeeFilesMapper {

    /**
     * 根据员工证件号码（必填）和客户名称（非必填，可为空）获取员工档案
     *
     * @param idNo     证件号码（必填）
     * @param clientName 客户名称（非必填，可为空）
     * @return 员工档案实体
     */
    public EmployeeFiles getEmployeeFilesByIdNoOrClientName(@NotNull String idNo, String clientName);

}
