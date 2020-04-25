package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.authine.cloudpivot.web.api.entity.LeaveInfo;
import com.authine.cloudpivot.web.api.entity.PreSentInfo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName PreSentLeaveService
 * @Author:lfh
 * @Date:2020/4/8 17:15
 * @Description:
 **/
public interface PreSentLeaveService {
    List<PreSentInfo> findBatchPreSent(Date startTime, Date endTime);

    AddEmployee findEmployeeInfo(String identityNo);

    String findSaleman(String identityNo);

    List<LeaveInfo> findBatchLeave(Date startTime, Date endTime);

    DeleteEmployee findDelEmployeeInfo(String identityNo);
}
