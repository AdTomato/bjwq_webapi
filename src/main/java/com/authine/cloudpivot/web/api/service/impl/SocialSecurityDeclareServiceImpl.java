package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;
import com.authine.cloudpivot.web.api.mapper.SocialSecurityDeclareMapper;
import com.authine.cloudpivot.web.api.params.ImportCondition;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SocialSecurityDeclareServiceImpl
 * @Author:lfh
 * @Date:2020/3/24 9:56
 * @Description:
 **/
@Service
public class SocialSecurityDeclareServiceImpl implements SocialSecurityDeclareService {

    @Resource
    private SocialSecurityDeclareMapper socialSecurityDeclareMapper;

    @Override
    /* * @Author lfh
     * @Description //查询获取用户选选中的公积金申报信息
     * @Date 2020/3/25 10:10
     * @Param [ids]  用户id数组
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    public List<ContractImportInfo> findContractInfo(ImportCondition importCondition) {
        return socialSecurityDeclareMapper.findContractInfo(importCondition);
    }

    /* * @Author lfh
     * @Description //获取用户选中的停缴信息
     * @Date 2020/3/27 10:15
     * @Param [ids]
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    @Override
    public List<ContractTerminationInfo> findContractTerminationInfo(ImportCondition importCondition) {
        return socialSecurityDeclareMapper.findContractTerminationInfo(importCondition);
    }

    /* * @Author lfh
     * @Description 查询提交减员时的时间
     * @Date 2020/3/27 10:30
     * @Param [ids]
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    @Override
    public Date findDeleteEmployeeDate(String identityNo) {
        return socialSecurityDeclareMapper.findDeleteEmployeeDate(identityNo);
    }

    @Override
    public Integer findTimeNode(String welfare_handler) {

        return socialSecurityDeclareMapper.findTimeNode(welfare_handler);
    }

    @Override
    public List<RegisterDeclareSheetInfo> findRegisterDeclareInfo(ImportCondition importCondition) {
        return socialSecurityDeclareMapper.findRegisterDeclareInfo(importCondition);
    }

    @Override
    public Map<String, Object> findEmployeeContractInfo(String identityNo) {
        return socialSecurityDeclareMapper.findEmployeeContractInfo(identityNo);
    }

    @Override
    public List<RegisterDeclareSheetInfo> findRegisterDeclareInfoFromStopPayment(ImportCondition importCondition) {
        return socialSecurityDeclareMapper.findRegisterDeclareInfoFromStopPayment(importCondition);
    }
}
