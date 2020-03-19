package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dao.ClientUserDao;
import com.authine.cloudpivot.web.api.service.ClientUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.ClientUserServiceImpl
 * @Date 2020/1/3 17:10
 **/
@Service
public class ClientUserServiceImpl implements ClientUserService {

    @Resource
    private ClientUserDao clientUserDao;

    /**
     * 方法说明：更新客户信息表对应的用户id
     * @Param id
     * @Param userId
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/6 12:57
     */
    @Override
    public void updateClientUserId(String id, String userId) throws Exception {
        clientUserDao.updateClientUserId(id, userId);
    }

    /**
     * 方法说明：客户用户重置密码
     * @Param ids
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/14 15:09
     */
    @Override
    public void clientResetPassword(String ids) throws Exception{
        clientUserDao.clientResetPassword(ids);
    }

    /**
     * 方法说明：客户导入员工信息，提交后维护修改截止时间
     * @Param id
     * @Param sbCityId 社保缴纳城市id
     * @Param gjjCityId 公积金缴纳城市id
     * @Param clientNumber 客户编码
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/19 13:51
     */
    @Override
    public void updateEndTime(String id, String sbCityId, String gjjCityId, String clientNumber) throws Exception {
        // 判断当前客户是否是个性化客户，是返回个性化客户设置的数据
        List <Map <String, Object>> setList = clientUserDao.getPersonalizedSetByNo(clientNumber);
        if (setList != null && setList.size() > 0) {
            String timeNode = setList.get(0).get("time_node").toString();
            String companyInjuryRatio = setList.get(0).get("company_injury_ratio").toString();
            String employeeInjuryRatio = setList.get(0).get("employee_injury_ratio").toString();
            String companyFundRatio = setList.get(0).get("company_accumulation_ratio").toString();
            String employeeFundRatio = setList.get(0).get("employee_accumulation_ratio").toString();

            String endTime = this.getEndTime(Integer.parseInt(timeNode));

            clientUserDao.updateEndTime(id, endTime, companyInjuryRatio, employeeInjuryRatio, companyFundRatio,
                    employeeFundRatio);
        } else {
            // 根据社保缴纳城市id和公积金缴纳城市id获取最小的一个时间节点
            String timeNode = clientUserDao.getTimeNodeByIds(sbCityId + "," +gjjCityId);
            String endTime = this.getEndTime(Integer.parseInt(timeNode));

            clientUserDao.updateEndTime(id, endTime, null, null, null, null);
        }

    }

    private String getEndTime(int node) {
        String endTime = "";

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DATE) + 1;

        String endTimeMonth = "";
        String endTimeDay = "";
        if (node <= curDay) {
            // 节点在当前时间之前，截止时间在下个月
            curMonth++;
        }
        if (curMonth > 9) {
            endTimeMonth = String.valueOf(curMonth);
        } else {
            endTimeMonth = "0" + curMonth;
        }
        if (node > 9) {
            endTimeDay = String.valueOf(node);
        } else {
            endTimeDay = "0" + node;
        }

        endTime = curYear + "-" + endTimeMonth + "-" + endTimeDay + " 23:59:59";
        return endTime;
    }


}
