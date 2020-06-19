package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.SocialSecurityDeclareDto;
import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;
import com.authine.cloudpivot.web.api.params.ImportCondition;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SocialSecurityDeclareService
 * @Author:lfh
 * @Date:2020/3/24 9:56
 * @Description:
 **/

public interface SocialSecurityDeclareService {

    /* * @Author lfh
     * @Description //查询获取用户选选中的公积金申报信息
     * @Date 2020/3/25 10:10
     * @Param [ids]  用户id数组
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    List<ContractImportInfo> findContractInfo(ImportCondition importCondition);

    /* * @Author lfh
     * @Description //获取用户选中的停缴信息
     * @Date 2020/3/27 10:15
     * @Param [ids]
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    List<ContractTerminationInfo> findContractTerminationInfo(ImportCondition importCondition);

    /* * @Author lfh
     * @Description 查询提交减员时的时间
     * @Date 2020/3/27 10:30
     * @Param identityNo
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     **/
    Date findDeleteEmployeeDate(String identityNo);

    Integer findTimeNode(String welfare_handler);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfo(ImportCondition importCondition);

    Map<String,Object> findEmployeeContractInfo(String identityNo);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfoFromStopPayment(ImportCondition importCondition);

    List<SocialSecurityDeclareDto> getSocialSecurityDeclareDtoList(Map conditions);

}
