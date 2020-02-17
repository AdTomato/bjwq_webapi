package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: wan gyong
 * @Date: 2020-02-17 09:54
 * @Description: 员工档案service
 */
@Service
public class EmployeeFilesServiceImpl implements EmployeeFilesService {

    @Resource
    EmployeeFilesMapper employeeFilesMapper;

    /**
     * @param idNo     : 身份证
     * @param clientName : 客户名称
     * @return : com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @Author: wangyong
     * @Date: 2020/2/17 9:56
     * @Description: 根据员工身份证，客户名称查询员工档案
     */
    @Override
    public EmployeeFiles getEmployeeFilesByIdNoOrClientName(@NotNull String idNo, String clientName) {
        return employeeFilesMapper.getEmployeeFilesByIdNoOrClientName(idNo, clientName);
    }
}
