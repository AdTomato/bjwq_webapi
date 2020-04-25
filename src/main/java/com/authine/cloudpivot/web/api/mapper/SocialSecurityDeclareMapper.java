package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ContractImportInfo;
import com.authine.cloudpivot.web.api.entity.ContractTerminationInfo;
import com.authine.cloudpivot.web.api.entity.RegisterDeclareSheetInfo;

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
    List<ContractImportInfo> findContractInfo(Date startTime, Date endTime, String welfare_handler);

    List<ContractTerminationInfo> findContractTerminationInfo(Date startTime, Date endTime, String welfare_handler);

    Date findDeleteEmployeeDate(String identityNo);

    Integer findTimeNode(String welfare_handler);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfo(Date lastTimeNodeDate, Date nowTimeNodeDate, String welfare_handler);

    Map<String,Object> findEmployeeContractInfo(String identityNo);

    List<RegisterDeclareSheetInfo> findRegisterDeclareInfoFromStopPayment(Date startTime, Date endTime, String welfare_handler);


}

