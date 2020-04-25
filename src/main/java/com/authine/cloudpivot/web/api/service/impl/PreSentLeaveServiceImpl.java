package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.authine.cloudpivot.web.api.entity.LeaveInfo;
import com.authine.cloudpivot.web.api.entity.PreSentInfo;
import com.authine.cloudpivot.web.api.mapper.PreSentLeaveMapper;
import com.authine.cloudpivot.web.api.service.PreSentLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName PreSentLeaveServiceImpl
 * @Author:lfh
 * @Date:2020/4/8 17:16
 * @Description:
 **/
@Service
public class PreSentLeaveServiceImpl implements PreSentLeaveService {

    @Resource
    private PreSentLeaveMapper preSentLeaveMapper;
    @Override
    public List<PreSentInfo> findBatchPreSent(Date startTime, Date endTime) {
        return preSentLeaveMapper.findBatchSent(startTime,endTime);
    }

    @Override
    public AddEmployee findEmployeeInfo(String identityNo) {
        return preSentLeaveMapper.findEmployeeInfo(identityNo);
    }

    @Override
    public String findSaleman(String identityNo) {
        return preSentLeaveMapper.findSaleman(identityNo);
    }

    @Override
    public List<LeaveInfo> findBatchLeave(Date startTime, Date endTime) {
        return preSentLeaveMapper.findBatchLeave(startTime,endTime);
    }

    @Override
    public DeleteEmployee findDelEmployeeInfo(String identityNo) {
        return preSentLeaveMapper.findDelEmployeeInfo(identityNo);
    }
}
