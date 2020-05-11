package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;
import com.authine.cloudpivot.web.api.params.ImportCondition;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SocialSecurityDeclareMapper
 * @Author:lfh
 * @Date:2020/3/24 9:56
 * @Description:
 **/
public interface SocialSecurityDeclareMapper {
    List<ContractImportInfo> findContractInfo(ImportCondition importCondition);

    List<ContractTerminationInfo> findContractTerminationInfo(ImportCondition importCondition);

    Date findDeleteEmployeeDate(String identityNo);

    Integer findTimeNode(String welfare_handler);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfo(ImportCondition importCondition);

    Map<String,Object> findEmployeeContractInfo(String identityNo);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfoFromStopPayment(ImportCondition importCondition);


}

