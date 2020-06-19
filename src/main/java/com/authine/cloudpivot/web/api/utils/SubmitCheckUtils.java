package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.web.api.dto.SalesContractDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.params.*;
import com.authine.cloudpivot.web.api.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
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
    SalesContractService salesContractServiceOne;

    static SalesContractService salesContractService;

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

    @Autowired
    AddEmployeeService addEmployeeServiceOne;

    static AddEmployeeService addEmployeeService;

    @Autowired
    EmployeeOrderFormService employeeOrderFormServiceOne;

    static EmployeeOrderFormService employeeOrderFormService;

    @PostConstruct
    public void init() {
        clientManagementService = clientManagementServiceOne;
        clientService = clientServiceOne;
        cityTimeNodeService = cityTimeNodeServiceOne;
        collectionRuleService = collectionRuleServiceOne;
        unitService = unitServiceOne;
        addEmployeeService = addEmployeeServiceOne;
        salesContractService = salesContractServiceOne;
        employeeOrderFormService = employeeOrderFormServiceOne;
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

        String idCard = params.getIdentityNo();  // 身份证

        if ("身份证".equals(params.getIdentityNoType())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(idCard);
            result.setBirthday(processIdentityNo.getBirthday());
            result.setGender(processIdentityNo.getGender());
        }

        if (clientManagement != null && !StringUtils.isEmpty(clientManagement.getFirstLevelClientName()) && !StringUtils.isEmpty(clientManagement.getSecondLevelClientName())) {
            String id = params.getId();

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

            // 社保基数
            Double socialSecurityBase = params.getSocialSecurityBase();
            boolean isHaveSocialSecurityBase = socialSecurityBase == null || socialSecurityBase == 0D ? false : true;

            // 公积金基数
            Double providentFundBase = params.getProvidentFundBase();
            boolean isHaveProvidentFundBase = providentFundBase == null || providentFundBase == 0D ? false : true;

            // 判断社保是否重复申报
            if (isHaveSocialSecurityBase && employeeOrderFormService.isHaveSocialSecurityInOrderFormByIdCard(idCard, firstLevelClientName, secondLevelClientName)) {
                result.setMessage("社保已经申报过，无法重复申报");
                result.setIsCanSubmit(false);
                return result;
            }

            // 判断公积金是否重复申报
            if (isHaveProvidentFundBase && employeeOrderFormService.isHaveProvidentFundInOrderFormByIdCard(idCard, firstLevelClientName, secondLevelClientName)) {
                result.setMessage("公积金已经申报过，无法重复申报");
                result.setIsCanSubmit(false);
                return result;
            }

            // 判断销售合同是否存在
            SalesContractDto salesContractDto = salesContractService.getSalesContractDto(firstLevelClientName, businessType);
            if (salesContractDto == null) {
                result.setIsCanSubmit(false);
                result.setMessage("员工性质不存在");
                return result;
            }

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

            // 城市时间节点在不在范围内
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date notTime = new Date();
            String businessYear = sdf.format(notTime);
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

            // 公积金比例是否存在于征缴规则中
            Double companyRadio = params.getCompanyProvidentFundBl();
            if (companyRadio != null && companyRadio != 0) {
                boolean isHave = collectionRuleService.isHaveCompanyRatioInMaxStartMonth(welfare, companyRadio, welfareHandler);
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

    /**
     * 其他增员校验
     *
     * @param params
     * @return
     */
    public static AddEmployeeCheckReturn employeeCheck(OtherAddEmployeeCheckParams params) {
        AddEmployeeCheckReturn result = new AddEmployeeCheckReturn();
        result.setIsCanSubmit(true);

        if ("身份证".equals(params.getIdentityNoType())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(params.getIdentityNo());
            result.setBirthday(processIdentityNo.getBirthday());
            result.setGender(processIdentityNo.getGender());
        }
        if (params != null && !StringUtils.isEmpty(params.getFirstLevelClientName()) && !StringUtils.isEmpty(params.getSecondLevelClientName())) {
            // 一级客户名称
            String firstLevelClientName = params.getFirstLevelClientName();
            firstLevelClientName = firstLevelClientName.trim();

            // 二级客户名称
            String secondLevelClientName = params.getSecondLevelClientName();
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
        } else {
            result.setIsCanSubmit(false);
            result.setMessage("一级客户名称/二级客户名称未查询到");
        }
        return result;
    }

    /**
     * 方法说明：减员客户提交校验
     *
     * @param params
     * @return com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn
     * @author liulei
     * @Date 2020/6/1 11:06
     */
    public static AddEmployeeCheckReturn delEmployeeCheck(DelEmployeeCheckParams params) throws Exception {
        AddEmployeeCheckReturn result = new AddEmployeeCheckReturn();
        result.setIsCanSubmit(true);

        if ("身份证".equals(params.getIdentityNoType())) {
            if ("身份证".equals(params.getIdentityNoType())) {
                ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(params.getIdentityNo());
                result.setBirthday(processIdentityNo.getBirthday());
                result.setGender(processIdentityNo.getGender());
            }
        }

        String creater = params.getCreater();
        ClientManagement clientManagement = clientManagementService.getClientNameByUserId(creater);
        if (clientManagement != null && !StringUtils.isEmpty(clientManagement.getFirstLevelClientName()) && !StringUtils.isEmpty(clientManagement.getSecondLevelClientName())) {
            result = getDelEmployeeCheckReturn(result, clientManagement.getFirstLevelClientName(),
                    clientManagement.getSecondLevelClientName(), params.getIdentityNo(), null);
        } else {
            result.setIsCanSubmit(false);
            result.setMessage("一级客户名称/二级客户名称未查询到");
            return result;
        }


        return result;
    }

    /**
     * 方法说明：其他减员提交校验
     *
     * @param params
     * @return com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn
     * @author liulei
     * @Date 2020/6/1 11:06
     */
    public static AddEmployeeCheckReturn otherDelEmployeeCheck(OtherDelEmployeeCheckParams params) throws Exception {
        AddEmployeeCheckReturn result = new AddEmployeeCheckReturn();
        result.setIsCanSubmit(true);

        if ("身份证".equals(params.getIdentityNoType())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(params.getIdentityNo());
            result.setBirthday(processIdentityNo.getBirthday());
            result.setGender(processIdentityNo.getGender());
        }

        result = getDelEmployeeCheckReturn(result, params.getFirstLevelClientName(),
                params.getSecondLevelClientName(), params.getIdentityNo(), "other");
        return result;
    }

    private static AddEmployeeCheckReturn getDelEmployeeCheckReturn(AddEmployeeCheckReturn result,
                                                                    String firstLevelClientName,
                                                                    String secondLevelClientName, String identityNo,
                                                                    String type) throws Exception {
        EmployeeFiles employeeFiles = addEmployeeService.getEmployeeFilesByClientNameAndIdentityNo(firstLevelClientName,
                secondLevelClientName, identityNo);
        if (employeeFiles == null) {
            result.setIsCanSubmit(false);
            result.setMessage("没有获取到员工档案数据");
            return result;
        }
        result.setFirstLevelClientName(firstLevelClientName);
        result.setSecondLevelClientName(secondLevelClientName);
        result.setLookStr(employeeFiles.getInquirer());
        List<String> lookIds = UnitUtils.getUnitIds(employeeFiles.getInquirer());
        if (!lookIds.isEmpty()) {
            result.setLook(unitService.getOrgUnitByIds(lookIds));
        } else {
            result.setLook(null);
        }
        result.setEditStr(employeeFiles.getOperator());
        List<String> editIds = UnitUtils.getUnitIds(employeeFiles.getOperator());
        if (!editIds.isEmpty()) {
            result.setEdit(unitService.getOrgUnitByIds(editIds));
        } else {
            result.setEdit(null);
        }
        result.setDeptStr(employeeFiles.getSubordinateDepartment());
        List<String> deptIds = UnitUtils.getUnitIds(employeeFiles.getSubordinateDepartment());
        if (!deptIds.isEmpty()) {
            result.setDept(unitService.getDeptUnitByIds(deptIds));
        } else {
            result.setDept(null);
        }
        if ("other".equals(type)) {
            // 其他增员校验不需要进行时间节点校验
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date notTime = new Date();
        String businessYear = sdf.format(notTime);
        String welfare = StringUtils.isNotBlank(employeeFiles.getSocialSecurityCity()) ?
                employeeFiles.getSocialSecurityCity() : employeeFiles.getProvidentFundCity();
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
                    return result;
                }
            }
        }
        return result;
    }
}
