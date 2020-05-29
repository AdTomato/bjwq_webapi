package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.ClientManagement;
import com.authine.cloudpivot.web.api.entity.ProcessIdentityNo;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn;
import com.authine.cloudpivot.web.api.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 提交校验工具类
 *
 * @author wangyong
 * @time 2020/5/29 8:53
 */
@Component
public class SubmitCheckUtils {

    @Autowired
    ClientManagementService clientManagementServiceOne;

    static ClientManagementService clientManagementService;

    @Autowired
    ClientService clientServiceOne;

    static ClientService clientService;

    @Autowired
    CityTimeNodeService cityTimeNodeServiceOne;

    static CityTimeNodeService cityTimeNodeService;

    @Autowired
    CollectionRuleService collectionRuleServiceOne;

    static CollectionRuleService collectionRuleService;

    @Autowired
    UnitService unitServiceOne;

    static UnitService unitService;

    @PostConstruct
    public void init() {
        clientManagementService = clientManagementServiceOne;
        clientService = clientServiceOne;
        cityTimeNodeService = cityTimeNodeServiceOne;
        collectionRuleService = collectionRuleServiceOne;
        unitService = unitServiceOne;
    }

    /**
     * 增员客户提交校验
     *
     * @param params 所需参数
     * @return 检验结果
     */
    public static AddEmployeeCheckReturn addEmployeeCheck(AddEmployeeCheckParams params) {
        String creater = params.getCreater();
        ClientManagement clientManagement = clientManagementService.getClientNameByUserId(creater);
        AddEmployeeCheckReturn result = new AddEmployeeCheckReturn();
        result.setIsCanSubmit(true);

        if ("身份证".equals(params.getIdentityNoType())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(params.getIdentityNo());
            result.setBirthday(processIdentityNo.getBirthday());
            result.setGender(processIdentityNo.getGender());
        }

        if (clientManagement != null && !StringUtils.isEmpty(clientManagement.getFirstLevelClientName()) && !StringUtils.isEmpty(clientManagement.getSecondLevelClientName())) {
            // 一级客户名称
            String firstLevelClientName = clientManagement.getFirstLevelClientName();
            firstLevelClientName = firstLevelClientName.trim();

            // 二级客户名称
            String secondLevelClientName = clientManagement.getSecondLevelClientName();
            secondLevelClientName = secondLevelClientName.trim();


            result.setFirstLevelClientName(firstLevelClientName);
            result.setSecondLevelClientName(secondLevelClientName);
            // 业务类型
            String businessType = params.getBusinessType();

            // 福利地
            String welfare = params.getWelfare();

            // 福利办理方
            String welfareHandler = params.getWelfareHandler();

            // 查看人、操作人、所属部门
            Map<String, String> lookAndEditPerson = clientService.getLookAndEditPerson(firstLevelClientName, secondLevelClientName, businessType, welfare, welfareHandler);
            result.setLookStr(lookAndEditPerson.get("look"));
            List<String> lookIds = UnitUtils.getUnitIds(lookAndEditPerson.get("look"));
            if (!lookIds.isEmpty()) {
                result.setLook(unitService.getOrgUnitByIds(lookIds));
            } else {
                result.setLook(null);
            }
            result.setEditStr(lookAndEditPerson.get("edit"));
            List<String> editIds = UnitUtils.getUnitIds(lookAndEditPerson.get("edit"));
            if (!editIds.isEmpty()) {
                result.setEdit(unitService.getOrgUnitByIds(editIds));
            } else {
                result.setEdit(null);
            }
            result.setDeptStr(lookAndEditPerson.get("dept"));
            List<String> deptIds = UnitUtils.getUnitIds(lookAndEditPerson.get("dept"));
            if (!deptIds.isEmpty()) {
                result.setDept(unitService.getDeptUnitByIds(deptIds));
            } else {
                result.setDept(null);
            }

            Calendar calendar = Calendar.getInstance();
            Date notTime = new Date();
            calendar.setTime(notTime);
            String businessYear = calendar.get(Calendar.MONTH) < 10 ? calendar.get(Calendar.YEAR) + "0" + calendar.get(calendar.get(Calendar.MONTH)) : calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
            CityTimeNode cityTimeNode = cityTimeNodeService.getCityTimeNodeByBusinessYearAndCity(businessYear, welfare);
            if (cityTimeNode != null) {
                if (cityTimeNode.getStartTime() != null && cityTimeNode.getEndTime() != null) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTime(cityTimeNode.getStartTime());
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(cityTimeNode.getEndTime());
                    endTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DATE), 23, 59, 59);
                    if (!(notTime.getTime() > startTime.getTimeInMillis() && notTime.getTime() < endTime.getTimeInMillis())) {
                        result.setIsCanSubmit(false);
                        result.setMessage(StringUtils.isEmpty(result.getMessage()) ?
                                welfare + "的申报时间范围为：" + DateUtils.getYearMonthDate(startTime) + "至" + DateUtils.getYearMonthDate(endTime) + ",不在申报时间范围内，无法申报\n" :
                                result.getMessage() + welfare + "的申报时间范围为：" + DateUtils.getYearMonthDate(startTime) + "至" + DateUtils.getYearMonthDate(endTime) + ",不在申报时间范围内，无法申报\n");
                    }
                }
            }
            Double companyRadio = params.getCompanyProvidentFundBl();
            if (companyRadio != 0) {
                boolean isHave = collectionRuleService.isHaveCompanyRatioInMaxStartMonth(welfare, companyRadio);
                if (!isHave) {
                    result.setIsCanSubmit(false);
                    result.setMessage(StringUtils.isEmpty(result.getMessage()) ? "公积金单位比例：" + companyRadio + "在" + welfare + "无法申报\n" : result.getMessage() + "公积金单位比例：" + companyRadio + "在" + welfare + "无法申报\n");
                }
            }
        } else {
            result.setIsCanSubmit(false);
            result.setMessage("一级客户名称/二级客户名称未查询到");
        }
        return result;
    }


}
