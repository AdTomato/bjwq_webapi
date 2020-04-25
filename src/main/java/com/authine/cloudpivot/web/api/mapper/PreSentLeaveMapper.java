package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.authine.cloudpivot.web.api.entity.LeaveInfo;
import com.authine.cloudpivot.web.api.entity.PreSentInfo;
import com.authine.cloudpivot.web.api.service.PreSentLeaveService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @ClassName PreSentLeaveMapper
 * @Author:lfh
 * @Date:2020/4/8 17:16
 * @Description:
 **/
public interface PreSentLeaveMapper {


    List<PreSentInfo> findBatchSent(Date startTime, Date endTime);

    AddEmployee findEmployeeInfo(String identityNo);

    String findSaleman(String identityNo);

    List<LeaveInfo> findBatchLeave(Date startTime, Date endTime);

    DeleteEmployee findDelEmployeeInfo(String identityNo);
}
