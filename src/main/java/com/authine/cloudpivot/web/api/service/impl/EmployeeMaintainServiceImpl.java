package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.dao.impl.EmployeeMaintainDaoImpl;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.PolicyCollectPayMapper;
import com.authine.cloudpivot.web.api.mapper.ReturnReasonAlreadyMapper;
import com.authine.cloudpivot.web.api.mapper.SystemManageMapper;
import com.authine.cloudpivot.web.api.service.EmployeeMaintainService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.EmployeeMaintainServiceImpl
 * @Date 2020/2/4 17:35
 **/
@Service
@Slf4j
public class EmployeeMaintainServiceImpl implements EmployeeMaintainService {

    @Resource
    private EmployeesMaintainDao employeesMaintainDao;

    @Resource
    private SystemManageMapper systemManageMapper;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Resource
    private ReturnReasonAlreadyMapper returnReasonAlreadyMapper;

    @Resource
    private PolicyCollectPayMapper policyCollectPayMapper;

    // 业务list
    private List <BizObjectModel> models = new ArrayList <>();

    @Override
    public void addEmployee(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                            String fileName, String userId, String departmentId) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 0, 21);

        if (fileList != null && fileList.size() > 0) {
            models = new ArrayList <>();
            // 生成BizObjectModel
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                BizObjectModel model = new BizObjectModel();
                // 插入模型编码
                model.setSchemaCode(Constants.ADD_EMPLOYEE_SCHEMA);
                model.setSequenceStatus("DRAFT");

                Map <String, Object> data = getBizObjectModelData(list, Constants.ADD_EMPLOYEE_SCHEMA);
                model.put(data);
                models.add(model);
            }

            // 批量生成业务数据
            List<String> modelIds = CommonUtils.addBizObjects(bizObjectFacade, userId, models, "id");

            // 启动流程实例，不结束发起节点
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, departmentId, userId,
                    Constants.ADD_EMPLOYEE_SCHEMA_WF, modelIds, false);
        }
    }

    @Override
    public void deleteEmployee(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                               String fileName, String userId, String departmentId) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 0, 9);

        if (fileList != null && fileList.size() > 0) {
            models = new ArrayList <>();
            // 生成BizObjectModel
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                BizObjectModel model = new BizObjectModel();
                // 插入模型编码
                model.setSchemaCode(Constants.DELETE_EMPLOYEE_SCHEMA);
                model.setSequenceStatus("DRAFT");

                Map <String, Object> data = getBizObjectModelData(list, Constants.DELETE_EMPLOYEE_SCHEMA);
                model.put(data);

                models.add(model);
            }

            // 批量生成业务数据
            List<String> modelIds = CommonUtils.addBizObjects(bizObjectFacade, userId, models, "id");

            // 启动流程实例，不结束发起节点
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, departmentId, userId,
                    Constants.DELETE_EMPLOYEE_SCHEMA_WF, modelIds, false);
        }
    }

    @Override
    public void updateEmployeeOrderFormStatus(String ids, String field, String status) throws Exception {
        employeesMaintainDao.updateEmployeeOrderFormStatus(ids, field, status);
    }

    @Override
    public void createNewEmployeeOrderForm(BizObjectFacade bizObjectFacade, String id, String employeeOrderFormId,
                                           String type) throws Exception {
        // 获取员工订单的数据
        EmployeeOrderForm employeeOrderForm = addEmployeeMapper.getEmployeeOrderFormById(employeeOrderFormId);
        if (employeeOrderForm == null) {
            throw new RuntimeException("没有获取到订单数据！");
        }

        String createrId = employeeOrderForm.getCreater();
        if (StringUtils.isBlank(createrId)) {
            throw new Exception("没有creater。");
        }
        // 生成员工订单BizObjectModel
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode(Constants.EMPLOYEE_ORDER_FORM_SCHEMA);
        model.setSequenceStatus("COMPLETED");

        Map <String, Object> data = new HashMap <>();
        // 员工档案单据号
        data.put("employee_files_id", employeeOrderForm.getEmployeeFilesId());
        // 详细
        data.put("detail", employeeOrderForm.getDetail());
        // 是否历史订单
        data.put("is_history", "否");
        // 社保福利地
        data.put("social_security_city", employeeOrderForm.getSocialSecurityCity());
        // 公积金福利地
        data.put("provident_fund_city", employeeOrderForm.getProvidentFundCity());
        // 服务费
        data.put("service_fee", employeeOrderForm.getServiceFee());
        // 起始时间
        data.put("start_time", employeeOrderForm.getStartTime());
        // 结束时间
        data.put("end_time",employeeOrderForm.getEndTime());
        // 输入时间
        data.put("input_time", new Date());
        if (Constants.SOCIAL_SECURITY.equals(type)) {
            // 社保社保办理成功提交
            // 社保状态
            data.put("social_security_status", "在缴");
            // 公积金状态
            data.put("provident_fund_status", employeeOrderForm.getProvidentFundStatus());
        } else if (Constants.PROVIDENT_FUND.equals(type)) {
            // 公积金申报办理成功提交
            // 社保状态
            data.put("social_security_status", employeeOrderForm.getSocialSecurityStatus());
            // 公积金状态
            data.put("provident_fund_status", "在缴");
        }

        model.put(data);

        // 创建业务对象模型
        String modelId = bizObjectFacade.saveBizObjectModel(createrId, model, "id");
        log.info("创建员工订单业务对象模型:" + modelId);

        updateEmployeeOrderFormInfo(id, employeeOrderFormId, modelId, type,
                employeeOrderForm.getServiceFee() == null ? 0.0 : employeeOrderForm.getServiceFee());

        // 更新订单的起始时间为子表中开始时间，结束时间
        addEmployeeMapper.updateEmployeeOrderFromTime(modelId);
        // 更新订单的起始时间为子表中开始时间，结束时间
        addEmployeeMapper.updateEmployeeOrderFromTime(employeeOrderFormId);
    }

    @Override
    public String getDispatchUnitByClientName(String clientName) throws Exception {
        return employeesMaintainDao.getDispatchUnitByClientName(clientName);
    }

    @Override
    public AppointmentSheet getWelfareHandlerByClientNameAndCity(String clientName, String city) throws Exception {
        List<AppointmentSheet> list = systemManageMapper.getWelfareHandlerByClientNameAndCity(clientName, city);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public String getOperateLeaderByCity(String city) throws Exception {
        return employeesMaintainDao.getOperateLeaderByCity(city);
    }

    @Override
    public OperateLeader getOperateLeaderByCityAndWelfareHandler(String city, String welfareHandler) throws Exception {
        List<OperateLeader> list = systemManageMapper.getOperateLeaderByCityAndWelfareHandler(city, welfareHandler);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public EmployeeOrderForm getEmployeeOrderForm(String socialSecurityCity, Date socialSecurityStartTime,
                                                  Double socialSecurityBase, String providentFundCity,
                                                  Date providentFundStartTime, Double providentFundBase,
                                                  Double gcompanyRatio, Double gemployeeRatio, int sMonth, int gMonth,
                                                  Ccps ccps) throws Exception {
        EmployeeOrderForm employeeOrderForm = new EmployeeOrderForm();
        employeeOrderForm.setStartTime(socialSecurityStartTime);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

        List<PolicyCollectPay> sPolicyCollectPays = new ArrayList <>();
        List<PolicyCollectPay> gPolicyCollectPays = new ArrayList <>();
        List <Map <String, String>> socialSecurityDetail = new ArrayList <>();
        List <Map <String, String>> providentFundDetail = new ArrayList <>();
        Map <String, String> map = new HashMap <>();
        Double baseMax = 0.0, baseMin = 0.0;
        Double total = 0.0;
        // 社保
        if (StringUtils.isNotBlank(socialSecurityCity) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(socialSecurityCity) >= 0 && socialSecurityStartTime != null) {
            sPolicyCollectPays = policyCollectPayMapper.getPolicyCollectPaysByCity(socialSecurityCity);
            if (sPolicyCollectPays != null && sPolicyCollectPays.size() > 0) {
                for (int i = 0; i< sPolicyCollectPays.size(); i++) {
                    PolicyCollectPay policyCollectPay = sPolicyCollectPays.get(i);
                    if (StringUtils.isBlank(policyCollectPay.getSocialSecurityGroup()) || policyCollectPay.getSocialSecurityGroup().indexOf("社保") < 0) {
                        continue;
                    }
                    List <ProductBaseNum> productBaseNums = policyCollectPay.getProductBaseNums();
                    if (productBaseNums == null || productBaseNums.size() == 0) {
                        continue;
                    }
                    int payMonth = policyCollectPay.getPayMonth() == null ? 0 : policyCollectPay.getPayMonth();
                    calendar.setTime(socialSecurityStartTime);
                    if (payMonth < sMonth) {
                        calendar.add(Calendar.MONTH, sMonth - payMonth);
                    }
                    String startChargeTime = sdf1.format(calendar.getTime());
                    int startCharge = Integer.parseInt(sdf.format(calendar.getTime()));
                    for (int j = 0; j < productBaseNums.size(); j++) {
                        ProductBaseNum productBaseNum = productBaseNums.get(j);
                        Date startTime = productBaseNum.getStartTime();
                        if (startTime == null) {
                            continue;
                        }
                        Date endTime = productBaseNum.getEndTime();
                        int startStr = Integer.parseInt(sdf.format(startTime));
                        int endStr = endTime == null ? 100000000 : Integer.parseInt(sdf.format(endTime));
                        if (startCharge >= startStr && startStr <= endStr) {
                            String productName = policyCollectPay.getProductName();
                            Double companyRatio = policyCollectPay.getCompanyRatio() == null ? 0.0 :
                                    policyCollectPay.getCompanyRatio();
                            Double employeeRatio = policyCollectPay.getEmployeeRatio() == null ? 0.0 :
                                    policyCollectPay.getEmployeeRatio();
                            Double companySurchargeValue = policyCollectPay.getCompanySurchargeValue() == null ? 0.0 : policyCollectPay.getCompanySurchargeValue();
                            Double employeeSurchargeValue = policyCollectPay.getEmployeeSurchargeValue() == null ? 0.0 : policyCollectPay.getEmployeeSurchargeValue();
                            String payCycle = policyCollectPay.getPayCycle();
                            // 公司舍入原则和公司保留精度
                            String companyRounding = StringUtils.isBlank(policyCollectPay.getCompanyRoundingPolicy()) ? "四舍五入" :
                                    policyCollectPay.getCompanyRoundingPolicy();
                            String companyPrecision = StringUtils.isBlank(policyCollectPay.getCompanyPrecision()) ? "四舍五入" :
                                    policyCollectPay.getCompanyPrecision();
                            // 个人舍入原则和公司保留精度
                            String employeeRounding = StringUtils.isBlank(policyCollectPay.getEmployeeRoundingPolicy()) ? "四舍五入" :
                                    policyCollectPay.getEmployeeRoundingPolicy();
                            String employeePrecision = StringUtils.isBlank(policyCollectPay.getEmployeePrecision()) ? "四舍五入" :
                                    policyCollectPay.getEmployeePrecision();
                            baseMax = productBaseNum.getCompanyMaxBaseNum();
                            baseMin = productBaseNum.getCompanyMinBaseNum();
                            Double curBase = socialSecurityBase;
                            if (curBase > baseMax) {
                                curBase = baseMax;
                            }
                            if (curBase < baseMin) {
                                curBase = baseMin;
                            }
                            if (policyCollectPay.getProductName().indexOf("工伤") >= 0) {
                                // 个性化设置
                                companyRatio = ccps.getCompanyInjuryRatio() == null ||
                                        (ccps.getCompanyInjuryRatio() > -0.0000001 && ccps.getCompanyInjuryRatio() < 0.0000001) ? companyRatio : ccps.getCompanyInjuryRatio();
                                employeeRatio = ccps.getEmployeeInjuryRatio() == null ||
                                        (ccps.getEmployeeInjuryRatio() > -0.0000001 && ccps.getEmployeeInjuryRatio() < 0.0000001) ? employeeRatio : ccps.getEmployeeInjuryRatio();
                            }

                            Double companyMoney = curBase * companyRatio + companySurchargeValue;
                            Double employeeMoney = curBase * employeeRatio + employeeSurchargeValue;
                            // 数据按照规则处理
                            companyMoney = CommonUtils.processingData(companyMoney, companyRounding, companyPrecision);
                            employeeMoney = CommonUtils.processingData(employeeMoney, employeeRounding, employeePrecision);

                            Double sum = companyMoney + employeeMoney;
                            total = total + sum;

                            map = new HashMap <>();
                            map.put("product_name", policyCollectPay.getId());
                            map.put("social_security_group", policyCollectPay.getSocialSecurityGroup());
                            map.put("base_num", curBase == null ? "" : String.valueOf(curBase));
                            map.put("sum", sum == null ? "" : String.valueOf(sum));
                            map.put("company_money", companyMoney == null ? "" : String.valueOf(companyMoney));
                            map.put("employee_money", employeeMoney == null ? "" : String.valueOf(employeeMoney));
                            map.put("company_ratio", companyRatio == null ? "" : String.valueOf(companyRatio));
                            map.put("employee_ratio", employeeRatio == null ? "" : String.valueOf(employeeRatio));
                            map.put("company_surcharge_value", companySurchargeValue == null ? "" : String.valueOf(companySurchargeValue));
                            map.put("employee_surcharge_value", employeeSurchargeValue == null ? "" : String.valueOf(employeeSurchargeValue));
                            map.put("pay_cycle", payCycle);
                            map.put("start_charge_time", startChargeTime);
                            map.put("name_hide", productName);

                            if (productName.indexOf("养老") >= 0) {
                                employeeOrderForm.setEndowment(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            } else if (productName.indexOf("医疗") >= 0 && productName.indexOf("大病") < 0) {
                                employeeOrderForm.setMedical(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            } else if (productName.indexOf("失业") >= 0) {
                                employeeOrderForm.setUnemployment(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            } else if (productName.indexOf("工伤") >= 0 && productName.indexOf("补充") < 0) {
                                employeeOrderForm.setWorkRelatedInjury(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            } else if (productName.indexOf("生育") >= 0) {
                                employeeOrderForm.setChildbirth(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            } else if (productName.indexOf("大病") >= 0) {
                                employeeOrderForm.setCriticalIllness(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            }

                            socialSecurityDetail.add(map);
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        // 公积金
        if (StringUtils.isNotBlank(providentFundCity) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(providentFundCity) >= 0 && providentFundStartTime != null) {
            if (providentFundCity.equals(socialSecurityCity)) {
                gPolicyCollectPays = sPolicyCollectPays;
            } else {
                gPolicyCollectPays = policyCollectPayMapper.getPolicyCollectPaysByCity(providentFundCity);
            }
            if (gPolicyCollectPays != null && gPolicyCollectPays.size() > 0) {
                for (int i = 0; i< gPolicyCollectPays.size(); i++) {
                    PolicyCollectPay policyCollectPay = gPolicyCollectPays.get(i);
                    if (StringUtils.isBlank(policyCollectPay.getSocialSecurityGroup()) || policyCollectPay.getSocialSecurityGroup().indexOf("公积金") < 0) {
                        continue;
                    }
                    List <ProductBaseNum> productBaseNums = policyCollectPay.getProductBaseNums();
                    if (productBaseNums == null || productBaseNums.size() == 0) {
                        continue;
                    }
                    int payMonth =  policyCollectPay.getPayMonth() == null ? 0 : policyCollectPay.getPayMonth();
                    calendar.setTime(socialSecurityStartTime);
                    if (payMonth < sMonth) {
                        calendar.add(Calendar.MONTH, sMonth - payMonth);
                    }
                    String startChargeTime = sdf1.format(calendar.getTime());
                    int startCharge = Integer.parseInt(sdf.format(calendar.getTime()));
                    for (int j = 0; j < productBaseNums.size(); j++) {
                        ProductBaseNum productBaseNum = productBaseNums.get(j);
                        Date startTime = productBaseNum.getStartTime();
                        if (startTime == null) {
                            continue;
                        }
                        Date endTime = productBaseNum.getEndTime();
                        int startStr = Integer.parseInt(sdf.format(startTime));
                        int endStr = endTime == null ? 100000000 : Integer.parseInt(sdf.format(endTime));
                        if (startCharge >= startStr && startStr <= endStr) {
                            String productName = policyCollectPay.getProductName();
                            Double companySurchargeValue = policyCollectPay.getCompanySurchargeValue() == null ? 0.0 : policyCollectPay.getCompanySurchargeValue();
                            Double employeeSurchargeValue = policyCollectPay.getEmployeeSurchargeValue() == null ? 0.0 : policyCollectPay.getEmployeeSurchargeValue();

                            String payCycle = policyCollectPay.getPayCycle();
                            // 公司舍入原则和公司保留精度
                            String companyRounding = StringUtils.isBlank(policyCollectPay.getCompanyRoundingPolicy()) ? "四舍五入" :
                                    policyCollectPay.getCompanyRoundingPolicy();
                            String companyPrecision = StringUtils.isBlank(policyCollectPay.getCompanyPrecision()) ? "四舍五入" :
                                    policyCollectPay.getCompanyPrecision();
                            // 个人舍入原则和公司保留精度
                            String employeeRounding = StringUtils.isBlank(policyCollectPay.getEmployeeRoundingPolicy()) ? "四舍五入" :
                                    policyCollectPay.getEmployeeRoundingPolicy();
                            String employeePrecision = StringUtils.isBlank(policyCollectPay.getEmployeePrecision()) ? "四舍五入" :
                                    policyCollectPay.getEmployeePrecision();
                            baseMax = productBaseNum.getCompanyMaxBaseNum();
                            baseMin = productBaseNum.getCompanyMinBaseNum();
                            Double curBase = providentFundBase;
                            if (curBase > baseMax) {
                                curBase = baseMax;
                            }
                            if (curBase < baseMin) {
                                curBase = baseMin;
                            }

                            Double companyMoney = curBase * gcompanyRatio + companySurchargeValue;
                            Double employeeMoney = curBase * gemployeeRatio + employeeSurchargeValue;

                            // 数据按照规则处理
                            companyMoney = CommonUtils.processingData(companyMoney, companyRounding, companyPrecision);
                            employeeMoney = CommonUtils.processingData(employeeMoney, employeeRounding, employeePrecision);

                            Double sum = companyMoney + employeeMoney;
                            total = total + sum;

                            map = new HashMap <>();
                            map.put("product_name", policyCollectPay.getId());
                            map.put("social_security_group", policyCollectPay.getSocialSecurityGroup());
                            map.put("base_num", curBase == null ? "" : String.valueOf(curBase));
                            map.put("sum", sum == null ? "" : String.valueOf(sum));
                            map.put("company_money", companyMoney == null ? "" : String.valueOf(companyMoney));
                            map.put("employee_money", employeeMoney == null ? "" : String.valueOf(employeeMoney));
                            map.put("company_ratio", gcompanyRatio == null ? "" : String.valueOf(gcompanyRatio));
                            map.put("employee_ratio", gemployeeRatio == null ? "" : String.valueOf(gemployeeRatio));
                            map.put("company_surcharge_value", companySurchargeValue == null ? "" : String.valueOf(companySurchargeValue));
                            map.put("employee_surcharge_value", employeeSurchargeValue == null ? "" : String.valueOf(employeeSurchargeValue));
                            map.put("pay_cycle", payCycle);
                            map.put("start_charge_time", startChargeTime);
                            map.put("name_hide", productName);

                            if (productName.indexOf("补充") < 0) {
                                employeeOrderForm.setHousingAccumulationFunds(sum + "(" + companyMoney + "+" + employeeMoney + ")");
                            }

                            providentFundDetail.add(map);
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }

        employeeOrderForm.setSocialSecurityDetail(socialSecurityDetail);
        employeeOrderForm.setProvidentFundDetail(providentFundDetail);
        employeeOrderForm.setSum(total);

        employeeOrderForm.setSocialSecurityCity(socialSecurityCity);
        employeeOrderForm.setProvidentFundCity(providentFundCity);
        return employeeOrderForm;
    }

    @Override
    public Ccps getTimeNode(String clientName, String city) throws Exception {
        Ccps ccps = new Ccps();
        List <Ccps> ccpsList = systemManageMapper.getCcpsByClientName(clientName);
        if (ccpsList != null && ccpsList.size() > 0) {
            ccps = ccpsList.get(0);
        } else {
            // 获取城市时间节点设置
            int timeNode = systemManageMapper.getTimeNodeByCity(city);
            ccps.setTimeNode(timeNode);
        }
        return ccps;
    }

    @Override
    public Map <String, String> getEntryNoticeInfo(int monthDifference, String city) throws Exception {
        return employeesMaintainDao.getEntryNoticeBy(monthDifference, city);
    }

    @Override
    public Map <String, String> getSocialSecurityFundDetail(String employeeFilesId) throws Exception {
        return employeesMaintainDao.getSocialSecurityFundDetail(employeeFilesId);
    }

    @Override
    public void deleteEmployeeSubmit(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                     SocialSecurityClose socialSecurityClose, ProvidentFundClose providentFundClose,
                                     EmployeeFiles employeeFiles, UserModel user) throws Exception {
        if(socialSecurityClose.getChargeEndMonth() != null && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) >= 0) {
            createSocialSecurityClose(bizObjectFacade, workflowInstanceFacade, socialSecurityClose, user);
        }
        if(providentFundClose.getChargeEndMonth() != null && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getProvidentFundCity()) >= 0) {
            createProvidentFundClose(bizObjectFacade, workflowInstanceFacade, providentFundClose, user);
        }
        // 减员时修改员工档案数据
        employeesMaintainDao.updateEmployeeFilesWhenDelEmployee(employeeFiles);
    }

    @Override
    public void deleteEmployeeUpdateSubmit(DeleteEmployee deleteEmployee) throws Exception {
        // 根据客户名称，证件号码查询社保停缴数据，更新其收费截止月，离职备注
        employeesMaintainDao.updateSocialSecurityClose(deleteEmployee.getClientName(), deleteEmployee.getIdentityNo(),
                deleteEmployee.getSocialSecurityEndTime(), deleteEmployee.getLeaveReason());

        // 根据客户名称，证件号码查询公积金停缴数据，更新其收费截止月
        employeesMaintainDao.updateProvidentFundClose(deleteEmployee.getClientName(), deleteEmployee.getIdentityNo(),
                deleteEmployee.getProvidentFundEndTime());

        // 根据客户名称，证件号码查询员工档案数据，更新其离职日期，社保收费截止，公积金收费截止，离职原因，离职备注
        employeesMaintainDao.updateEmployeeFiles(deleteEmployee.getClientName(), deleteEmployee.getIdentityNo(),
                deleteEmployee.getLeaveTime(), deleteEmployee.getSocialSecurityEndTime(),
                deleteEmployee.getProvidentFundEndTime(), deleteEmployee.getLeaveReason(), deleteEmployee.getRemark());

        // 根据修改后的数据,更新减员_客户数据
        employeesMaintainDao.updateDeleteEmployeeBetweenTwoTables(deleteEmployee.getId(),
                deleteEmployee.getDeleteEmployeeId());
    }

    @Override
    public void shDeleteEmployeeUpdateSubmit(ShDeleteEmployee shDeleteEmployee) throws Exception {
        // 根据客户名称，证件号码查询社保停缴数据，更新其收费截止月，离职备注
        employeesMaintainDao.updateSocialSecurityClose(shDeleteEmployee.getClientName(), shDeleteEmployee.getIdentityNo(),
                shDeleteEmployee.getChargeEndTime(), shDeleteEmployee.getLeaveReason());

        // 根据客户名称，证件号码查询公积金停缴数据，更新其收费截止月
        employeesMaintainDao.updateProvidentFundClose(shDeleteEmployee.getClientName(), shDeleteEmployee.getIdentityNo(),
                shDeleteEmployee.getChargeEndTime());

        // 根据客户名称，证件号码查询员工档案数据，更新其离职日期，社保收费截止，公积金收费截止，离职原因，离职备注
        employeesMaintainDao.updateEmployeeFiles(shDeleteEmployee.getClientName(), shDeleteEmployee.getIdentityNo(),
                shDeleteEmployee.getDepartureTime(), shDeleteEmployee.getChargeEndTime(),
                shDeleteEmployee.getChargeEndTime(), shDeleteEmployee.getLeaveReason(),
                shDeleteEmployee.getLeaveRemark());

        // 根据修改后的数据更新减员_上海数据
        employeesMaintainDao.updateShDeleteEmployeeBetweenTwoTables(shDeleteEmployee.getId(),
                shDeleteEmployee.getShDeleteEmployeeId());
    }

    @Override
    public void qgDeleteEmployeeUpdateSubmit(NationwideDispatch nationwideDispatch) throws Exception {
        // 根据客户名称，证件号码查询社保停缴数据，更新其收费截止月，离职备注
        employeesMaintainDao.updateSocialSecurityClose(nationwideDispatch.getBusinessCustomerName(),
                nationwideDispatch.getIdentityNo(), nationwideDispatch.getSServiceFeeEndDate(),
                nationwideDispatch.getSocialSecurityStopReason() + nationwideDispatch.getDepartureRemark());

        // 根据客户名称，证件号码查询公积金停缴数据，更新其收费截止月
        employeesMaintainDao.updateProvidentFundClose(nationwideDispatch.getBusinessCustomerName(),
                nationwideDispatch.getIdentityNo(), nationwideDispatch.getSServiceFeeEndDate());

        // 根据客户名称，证件号码查询员工档案数据，更新其离职日期，社保收费截止，公积金收费截止，离职原因，离职备注
        employeesMaintainDao.updateEmployeeFiles(nationwideDispatch.getBusinessCustomerName(),
                nationwideDispatch.getIdentityNo(), nationwideDispatch.getDepartureDate(),
                nationwideDispatch.getSServiceFeeEndDate(), nationwideDispatch.getGServiceFeeEndDate(),
                nationwideDispatch.getSocialSecurityStopReason(), nationwideDispatch.getDepartureRemark());

        // 根据修改后的数据更新减员_全国数据
        employeesMaintainDao.updateNationwideDispatchBetweenTwoTables(nationwideDispatch.getId(),
                nationwideDispatch.getNationwideDispatchDelId(), "del");
    }

    @Override
    public void employeeFilesUpdateSubmit(EmployeeFiles employeeFiles) throws Exception {
        employeesMaintainDao.UpdateEmployeeFilesBetweenTwoTables(employeeFiles.getId(),
                employeeFiles.getEmployeeFilesId());
    }

    @Override
    public void addEmployeeUpdateSubmit(AddEmployee addEmployee, EmployeeFiles employeeFiles,
                                        EmployeeOrderForm employeeOrderForm,
                                        SocialSecurityDeclare socialSecurityDeclare,
                                        ProvidentFundDeclare providentFundDeclare) throws Exception {
        // 数据变更时修改被影响的数据
        this.updateImpactDataWhenAddEmployeeUpdate(employeeFiles, employeeOrderForm, socialSecurityDeclare,
                providentFundDeclare);
        // 更新增员数据
        employeesMaintainDao.updateAddEmployeeBetweenTwoTables(addEmployee.getId(), addEmployee.getAddEmployeeId());
    }

    @Override
    public void shAddEmployeeUpdateSubmit(ShAddEmployee shAddEmployee, EmployeeFiles employeeFiles,
                                          EmployeeOrderForm employeeOrderForm,
                                          SocialSecurityDeclare socialSecurityDeclare,
                                          ProvidentFundDeclare providentFundDeclare) throws Exception {
        // 数据变更时修改被影响的数据
        this.updateImpactDataWhenAddEmployeeUpdate(employeeFiles, employeeOrderForm, socialSecurityDeclare,
                providentFundDeclare);
        // 更新增员数据
        employeesMaintainDao.updateShAddEmployeeBetweenTwoTables(shAddEmployee.getId(),
                shAddEmployee.getShAddEmployeeId());
    }

    @Override
    public void qgAddEmployeeUpdateSubmit(NationwideDispatch nationwideDispatch, EmployeeFiles employeeFiles,
                                          EmployeeOrderForm employeeOrderForm,
                                          SocialSecurityDeclare socialSecurityDeclare,
                                          ProvidentFundDeclare providentFundDeclare) throws Exception {
        // 数据变更时修改被影响的数据
        this.updateImpactDataWhenAddEmployeeUpdate(employeeFiles, employeeOrderForm, socialSecurityDeclare,
                providentFundDeclare);
        // 更新增员数据
        employeesMaintainDao.updateNationwideDispatchBetweenTwoTables(nationwideDispatch.getId(),
                nationwideDispatch.getNationwideDispatchId(), "add");
    }

    @Override
    public void batchSubmit(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                            String userId, String ids, String code) throws Exception {
        String idArr[] = ids.split(",");
        int success = 0;
        // 需要更新状态的订单id
        String employeeOrderFormIds = "";
        // 生成新的订单类型
        String type = Constants.PROVIDENT_FUND;
        String field = "provident_fund_status";
        String status = "在缴";
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code)) {
            type = Constants.SOCIAL_SECURITY;
        }
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code) || Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code)) {
            field = "social_security_status";
        }
        if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code) || Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(code)) {
            status = "停缴";
        }
        // 提交之后需要更新当前表单状态
        String successIds = "";
        // 判断是否有操作权限
        List <Map <String, Object>> list = employeesMaintainDao.getWorkItemInfo(userId, ids, code,
                Constants.OPERATE_TYPE_SUBMIT);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                // 当前流程节点
                String activityCode = list.get(i).get("sourceId") == null ? "" :
                        list.get(i).get("sourceId").toString();
                String id = list.get(i).get("id") == null ? "" : list.get(i).get("id").toString();
                String employeeOrderFormId = list.get(i).get("employee_order_form_id") == null ? "" :
                        list.get(i).get("employee_order_form_id").toString();
                String workItemId = list.get(i).get("workItemId") == null ? "" :
                        list.get(i).get("workItemId").toString();
                String isChange = list.get(i).get("is_change") == null ? "" :
                        list.get(i).get("is_change").toString();
                if ("fill_application".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                    // 当前流程节点是"填写申请单"，仅仅提交操作
                    boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemId, true);
                    if (flag) {
                        success++;
                    }
                } else if ("declare".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                    // 当前节点是申报，需要判断是否重新生成订单
                    boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemId, true);
                    if (flag) {
                        if ("是".equals(isChange)) {
                            // 有变化，直接创建新的订单
                            this.createNewEmployeeOrderForm(bizObjectFacade, id,  employeeOrderFormId, type);
                        } else {
                            employeeOrderFormIds += employeeOrderFormId + ",";
                        }
                        successIds += id + ",";
                        success++;
                    }
                } else if ("close".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                    // 当前节点是停缴，需要更新订单状态
                    boolean flag = workflowInstanceFacade.submitWorkItem(userId, workItemId, true);
                    if (flag) {
                        employeeOrderFormIds += employeeOrderFormId + ",";
                        successIds += id + ",";
                        success++;
                    }
                }
            }
            if (StringUtils.isNotBlank(employeeOrderFormIds)) {
                employeeOrderFormIds = employeeOrderFormIds.substring(0, employeeOrderFormIds.length() -1);
                this.updateEmployeeOrderFormStatus(employeeOrderFormIds, field, status);
            }
            if (StringUtils.isNotBlank(successIds)) {
                successIds = successIds.substring(0, successIds.length() -1);
                String successIdArr[] = successIds.split(",");
                addEmployeeMapper.updateDeclareOrCloseStatus(successIdArr, "i4fvb_" + code, status);
            }
            if (idArr.length == list.size()) {
                if (success > 0) {
                    if (list.size() > success) {
                        throw new RuntimeException("共选择" + list.size() + "条数据，操作成功" + success + "条！");
                    }
                } else {
                    throw new RuntimeException("操作失败！");
                }
            } else if (idArr.length > list.size()) {
                throw new RuntimeException("共选择" + idArr.length + "条数据，可操作数据" + list.size() + "条，操作成功" + success + "条！");
            }
        } else {
            throw new RuntimeException("当前选中的数据中没有可以操作的数据！");
        }
    }

    @Override
    public void batchReject(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                            String userId, String ids, String code) throws Exception {
        String idArr[] = ids.split(",");
        int success = 0;
        // 需要更新状态的订单id
        String employeeOrderFormIds = "";
        String field = "provident_fund_status";
        String status = "驳回";
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code) || Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(code)) {
            field = "social_security_status";
        }
        // 提交之后需要更新当前表单状态
        String successIds = "";
        // 判断是否有操作权限
        List <Map <String, Object>> list = employeesMaintainDao.getWorkItemInfo(userId, ids, code,
                Constants.OPERATE_TYPE_REJECT);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                // 当前流程节点
                String activityCode = list.get(i).get("sourceId") == null ? "" :
                        list.get(i).get("sourceId").toString();
                String id = list.get(i).get("id") == null ? "" : list.get(i).get("id").toString();
                String sequenceStatus = list.get(i).get("sequenceStatus") == null ? "" :
                        list.get(i).get("sequenceStatus").toString();
                String employeeOrderFormId = list.get(i).get("employee_order_form_id") == null ? "" :
                        list.get(i).get("employee_order_form_id").toString();
                String workItemId = list.get(i).get("workItemId") == null ? "" :
                        list.get(i).get("workItemId").toString();
                String workflowInstanceId = list.get(i).get("workflowInstanceId") == null ? "" :
                        list.get(i).get("workflowInstanceId").toString();
                if ("COMPLETED".equals(sequenceStatus) && StringUtils.isNotBlank(workflowInstanceId)) {
                    // 办结，此时是申报驳回操作,需要更新订单状态
                    boolean flag = workflowInstanceFacade.activateActivity(userId, workflowInstanceId,
                            "fill_application");
                    if (flag) {
                        success++;
                        employeeOrderFormIds += employeeOrderFormId + ",";
                        successIds += id + ",";
                    }
                } else if ("declare".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                    // 当前节点是申报，驳回至填写申请单节点,需要更新订单状态
                    boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemId, "fill_application", true);
                    if (flag) {
                        employeeOrderFormIds += employeeOrderFormId + ",";
                        successIds += id + ",";
                        success++;
                    }
                } else if ("close".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                    // 当前节点是停缴，驳回至填写申请单节点
                    boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemId, "fill_application", true);
                    if (flag) {
                        success++;
                        successIds += id + ",";
                    }
                }
            }
            if (StringUtils.isNotBlank(employeeOrderFormIds)) {
                employeeOrderFormIds = employeeOrderFormIds.substring(0, employeeOrderFormIds.length() -1);
                this.updateEmployeeOrderFormStatus(employeeOrderFormIds, field, status);
            }
            if (StringUtils.isNotBlank(successIds)) {
                successIds = successIds.substring(0, successIds.length() -1);
                String successIdArr[] = successIds.split(",");
                addEmployeeMapper.updateDeclareOrCloseStatus(successIdArr, "i4fvb_" + code, status);
            }
            if (idArr.length == list.size()) {
                if (success > 0) {
                    if (list.size() > success) {
                        throw new RuntimeException("共选择" + list.size() + "条数据，操作成功" + success + "条！");
                    }
                } else {
                    throw new RuntimeException("操作失败！");
                }
            } else if (idArr.length > list.size()) {
                throw new RuntimeException("共选择" + idArr.length + "条数据，可操作数据" + list.size() + "条，操作成功" + success + "条！");
            }
        } else {
            throw new RuntimeException("当前选中的数据中没有可以操作的数据！");
        }
    }

    @Override
    public void updateStatusToPrePoint(String ids, String field) throws Exception {
        String idArr[] = ids.split(",");
        addEmployeeMapper.updateOrderFormStatusToPrePoint(idArr, field);
    }

    @Override
    public List <Map<String, Object>> getAddOrDelWorkItemId(String ids, String tableName) throws Exception {
        return employeesMaintainDao.getAddOrDelWorkItemId(ids, "i4fvb_" + tableName);
    }

    @Override
    public void rejectImport(String fileName, String code, String userId,
                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);

        if (fileList != null && fileList.size() > 2) {
            Map<String, Integer> locationMap = CommonUtils.getImportLocation(Arrays.asList(fileList.get(0)));
            int sequenceNoInt = 0, returnReasonAlreadyInt = 0;
            if (locationMap.get("sequenceNo") != null && locationMap.get("sequenceNo") != 0 && locationMap.get(
                    "return_reason_already") != null && locationMap.get("return_reason_already") != 0) {
                sequenceNoInt = locationMap.get("sequenceNo") - 1;
                returnReasonAlreadyInt = locationMap.get("return_reason_already") - 1;
            } else {
                throw new RuntimeException("导入文件表头信息错误！");
            }
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            List<Map<String, Object>> dataList = new ArrayList <>();
            Map<String, Object> data = new HashMap <>();
            for (int i = 2; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                data = new HashMap <>();
                data.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
                data.put("sourceId", sourceId);
                data.put("sequenceNo", list.get(sequenceNoInt));
                data.put("returnReasonAlready", list.get(returnReasonAlreadyInt));

                dataList.add(data);
            }
            if (dataList != null && dataList.size() > 0) {
                int listSize = dataList.size();
                int toIndex = 500;
                for (int j = 0; j < dataList.size(); j += 500) {
                    if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                        toIndex = listSize - j;
                    }
                    List<Map<String, Object>> newList = dataList.subList(j, j + toIndex);
                    returnReasonAlreadyMapper.insertReturnReasonAlready(newList);
                }

                // 更新已有返回数据
                returnReasonAlreadyMapper.updateReturnReasonAlready("i4fvb_" + code, sourceId);
                // 查询需要驳回数据流程，代办信息
                List <Map <String, Object>> workItemList = returnReasonAlreadyMapper.getWorkItemInfo("i4fvb_" + code,
                        sourceId);
                if (workItemList != null && workItemList.size() > 0) {
                    List<String> employeeOrderFormIds = new ArrayList <>();
                    List<String> sequenceNos = new ArrayList <>();
                    String field = "provident_fund_status";
                    String status = "驳回";
                    if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(code)) {
                        field = "social_security_status";
                    }
                    for (int i = 0; i < workItemList.size(); i++) {
                        // 当前流程节点
                        String activityCode = workItemList.get(i).get("sourceId") == null ? "" :
                                workItemList.get(i).get("sourceId").toString();
                        String workItemId = workItemList.get(i).get("id") == null ? "" :
                                workItemList.get(i).get("id").toString();
                        String sequenceStatus = workItemList.get(i).get("sequenceStatus") == null ? "" :
                                workItemList.get(i).get("sequenceStatus").toString();
                        String employeeOrderFormId = workItemList.get(i).get("employee_order_form_id") == null ? "" :
                                workItemList.get(i).get("employee_order_form_id").toString();
                        String workflowInstanceId = workItemList.get(i).get("workflowInstanceId") == null ? "" :
                                workItemList.get(i).get("workflowInstanceId").toString();
                        String sequenceNo = workItemList.get(i).get("sequenceNo") == null ? "" :
                                workItemList.get(i).get("sequenceNo").toString();
                        if ("COMPLETED".equals(sequenceStatus) && StringUtils.isNotBlank(workflowInstanceId)) {
                            // 办结，此时是申报驳回操作,需要更新订单状态
                            boolean flag = workflowInstanceFacade.activateActivity(userId, workflowInstanceId,
                                    "fill_application");
                            if (flag) {
                                employeeOrderFormIds.add(employeeOrderFormId);
                                sequenceNos.add(sequenceNo);
                            }
                        } else if ("declare".equals(activityCode) && StringUtils.isNotBlank(workItemId)) {
                            // 当前节点是申报，驳回至填写申请单节点,需要更新订单状态
                            boolean flag = workflowInstanceFacade.rejectWorkItem(userId, workItemId, "fill_application", true);
                            if (flag) {
                                employeeOrderFormIds.add(employeeOrderFormId);
                                sequenceNos.add(sequenceNo);
                            }
                        }
                    }
                    if (employeeOrderFormIds != null && employeeOrderFormIds.size() > 0) {
                        returnReasonAlreadyMapper.updateEmployeeOrderFormStatus(employeeOrderFormIds, field, status);
                    }
                    if (sequenceNos != null && sequenceNos.size() > 0) {
                        returnReasonAlreadyMapper.updateDeclareStatus(sequenceNos, "i4fvb_" + code, status);
                    }
                }
            }

            returnReasonAlreadyMapper.deleteTempDataBySourceId(sourceId);
        }
    }

    /**
     * 方法说明：增员数据变更时修改被影响的数据，员工档案，员工订单，社保申报，公积金申报
     * @param employeeFiles 员工档案修改数据
     * @param employeeOrderForm 员工订单
     * @param socialSecurityDeclare 社保申报
     * @param providentFundDeclare 公积金申报
     * @return void
     * @author liulei
     * @Date 2020/3/2 11:27
     */
    private void updateImpactDataWhenAddEmployeeUpdate(EmployeeFiles employeeFiles,
                                                       EmployeeOrderForm employeeOrderForm,
                                                       SocialSecurityDeclare socialSecurityDeclare,
                                                       ProvidentFundDeclare providentFundDeclare) throws Exception{
        // 更新员工档案数据
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        // 更新员工订单数据,申报提交会更新订单数据，此时不更新
        //employeesMaintainDao.updateEmployeeOrderForm(employeeOrderForm);
        // 更新社保申报数据
        if (StringUtils.isNotBlank(socialSecurityDeclare.getId()) && socialSecurityDeclare.getStartMonth() != null
                && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) >= 0) {
            addEmployeeMapper.updateSocialSecurityDeclare(socialSecurityDeclare);
            // 删除社保申报中社保数据
            employeesMaintainDao.deleteChildTableDataByTableNameAndParentId("i4fvb_" + Constants.SOCIAL_SECURITY_DETAIL,
                    socialSecurityDeclare.getId());
            if (socialSecurityDeclare.getSocialSecurityDetail() != null) {
                if (socialSecurityDeclare.getSocialSecurityDetail().size() > 0) {
                    // 创建公积金子表数据
                    employeesMaintainDao.createSocialSecurityFundDetail(socialSecurityDeclare.getId(),
                            socialSecurityDeclare.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
                }
            }
        }
        // 更新公积金申报数据
        if (StringUtils.isNotBlank(providentFundDeclare.getId()) && providentFundDeclare.getStartMonth() != null
                && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getProvidentFundCity()) >= 0) {
            addEmployeeMapper.updateProvidentFundDeclare(providentFundDeclare);
            if (socialSecurityDeclare.getSocialSecurityDetail() != null) {
                // 删除公积金申报中公积金数据
                employeesMaintainDao.deleteChildTableDataByTableNameAndParentId("i4fvb_" + Constants.PROVIDENT_FUND_DETAIL,
                        providentFundDeclare.getId());
                if (socialSecurityDeclare.getSocialSecurityDetail().size() > 0) {
                    // 创建公积金子表数据
                    employeesMaintainDao.createSocialSecurityFundDetail(providentFundDeclare.getId(),
                            providentFundDeclare.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
                }
            }
        }
    }

    /**
     * 方法说明：创建公积金停缴流程数据
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param providentFundClose
     * @param user
     * @return void
     * @author liulei
     * @Date 2020/2/26 15:56
     */
    private void createProvidentFundClose(BizObjectFacade bizObjectFacade,
                                          WorkflowInstanceFacade workflowInstanceFacade,
                                          ProvidentFundClose providentFundClose, UserModel user) throws Exception {
        BizObjectModel bizObjectModel = new BizObjectModel();
        bizObjectModel.setSchemaCode(Constants.PROVIDENT_FUND_CLOSE_SCHEMA);
        bizObjectModel.setSequenceStatus("DRAFT");

        Map<String, Object> data = new HashMap <>();
        // 姓名
        data.put("employee_name", providentFundClose.getEmployeeName());
        // 性别
        data.put("gender", providentFundClose.getGender());
        // 证件类型
        data.put("identityNo_type", providentFundClose.getIdentityNoType());
        // 身份证号码
        data.put("identityNo", providentFundClose.getIdentityNo());
        // 出生日期
        data.put("birthday", providentFundClose.getBirthday());
        // 派出单位
        data.put("dispatch_unit", providentFundClose.getDispatchUnit());
        // 客户名称
        data.put("client_name", providentFundClose.getClientName());
        // 客服
        data.put("customer_service", providentFundClose.getCustomerService());
        // 申请人
        data.put("applicant", providentFundClose.getApplicant());
        // 申请日期
        data.put("application_time", providentFundClose.getApplicationTime());
        // 起始月
        data.put("start_month", providentFundClose.getStartMonth());
        // 员工订单
        data.put("employee_order_form_id", providentFundClose.getEmployeeOrderFormId());
        // 公积金基数
        data.put("provident_fund_base", providentFundClose.getProvidentFundBase());
        //企业缴存额
        data.put("enterprise_deposit", providentFundClose.getEnterpriseDeposit());
        //个人缴存额
        data.put("personal_deposit", providentFundClose.getPersonalDeposit());
        //缴存总额
        data.put("total_deposit", providentFundClose.getTotalDeposit());
        // 收费截止月
        data.put("charge_end_month", providentFundClose.getChargeEndMonth());
        // 福利办理方
        data.put("welfare_handler", providentFundClose.getWelfareHandler());
        // 运行负责人
        data.put("operate_leader", providentFundClose.getOperateLeader());

        data.put("status", providentFundClose.getStatus());

        bizObjectModel.put(data);

        // 创建业务对象模型
        String providentFundId = bizObjectFacade.saveBizObjectModel(user.getId(), bizObjectModel, "id");
        // 启动流程实例，不结束发起节点
        // 启动流程，返回流程实例id
        String providentFundWfId = workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(),
                user.getId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA_WF, providentFundId, false);
        log.info("启动停缴社保流程:" + providentFundWfId);
    }

    /**
     * 方法说明：创建社保停缴流程数据
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param socialSecurityClose
     * @param user
     * @return void
     * @author liulei
     * @Date 2020/2/26 15:56
     */
    private void createSocialSecurityClose(BizObjectFacade bizObjectFacade,
                                           WorkflowInstanceFacade workflowInstanceFacade,
                                           SocialSecurityClose socialSecurityClose, UserModel user) throws Exception {
        BizObjectModel bizObjectModel = new BizObjectModel();
        bizObjectModel.setSchemaCode(Constants.SOCIAL_SECURITY_CLOSE_SCHEMA);
        bizObjectModel.setSequenceStatus("DRAFT");

        Map<String, Object> data = new HashMap <>();
        // 姓名
        data.put("employee_name", socialSecurityClose.getEmployeeName());
        // 性别
        data.put("gender", socialSecurityClose.getGender());
        // 证件类型
        data.put("identityNo_type", socialSecurityClose.getIdentityNoType());
        // 身份证号码
        data.put("identityNo", socialSecurityClose.getIdentityNo());
        // 出生日期
        data.put("birthday", socialSecurityClose.getBirthday());
        // 派出单位
        data.put("dispatch_unit", socialSecurityClose.getDispatchUnit());
        // 客户名称
        data.put("client_name", socialSecurityClose.getClientName());
        // 客服
        data.put("customer_service", socialSecurityClose.getCustomerService());
        // 申请人
        data.put("applicant", socialSecurityClose.getApplicant());
        // 申请日期
        data.put("application_time", socialSecurityClose.getApplicationTime());
        // 起始月
        data.put("start_month", socialSecurityClose.getStartMonth());
        // 员工订单
        data.put("employee_order_form_id", socialSecurityClose.getEmployeeOrderFormId());
        // 离职备注
        data.put("resignation_remarks", socialSecurityClose.getResignationRemarks());
        // 社保基数
        data.put("social_security_base", socialSecurityClose.getSocialSecurityBase());
        // 收费截止月
        data.put("charge_end_month", socialSecurityClose.getChargeEndMonth());
        // 福利办理方
        data.put("welfare_handler", socialSecurityClose.getWelfareHandler());
        // 运行负责人
        data.put("operate_leader", socialSecurityClose.getOperateLeader());

        data.put("status", socialSecurityClose.getStatus());

        bizObjectModel.put(data);

        // 创建业务对象模型
        String socialSecurityId = bizObjectFacade.saveBizObjectModel(user.getId(), bizObjectModel, "id");
        log.info("创建停缴社保模型成功:" + socialSecurityId);
        // 启动流程实例，不结束发起节点
        // 启动流程，返回流程实例id
        String socialSecurityWfId = workflowInstanceFacade.startWorkflowInstance(user.getDepartmentId(),
                user.getId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA_WF, socialSecurityId, false);
        log.info("启动停缴社保流程:" + socialSecurityWfId);

    }

    /**
     * 方法说明：提交之后更新订单数据
     * @Param id 办理通过提交的表单id
     * @Param oldId 原订单id
     * @Param newId 新订单id
     * @Param type 类型
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/2/17 17:05
     */
    private void updateEmployeeOrderFormInfo(String id, String oldId, String newId, String type, Double serviceFee)
            throws Exception {
        Double total = 0.00;
        Map<String, String> info = new HashMap <>();
        // 原订单改为历史表单
        employeesMaintainDao.updateEmployeeOrderFormIsHistory(oldId);
        // 生成社保公积金数据
        employeesMaintainDao.createSocialSecurityFundDetail(id, oldId, newId, type);
        // 查询社保公积金数据
        List <Map <String, Object>> list = employeesMaintainDao.getSocialSecurityFundDetailByParentId(newId);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String productName = list.get(i).get("product_name") == null ? "" :
                        list.get(i).get("product_name").toString();
                Double sum = list.get(i).get("sum") == null ? 0.0 :
                        Double.parseDouble(list.get(i).get("sum").toString());
                Double companyMoney = list.get(i).get("company_money") == null ? 0.0 :
                        Double.parseDouble(list.get(i).get("company_money").toString());
                Double employeeMoney = list.get(i).get("employee_money") == null ? 0.0 :
                        Double.parseDouble(list.get(i).get("employee_money").toString());

                total = total + sum;
                String str = sum + "(" + companyMoney + "+" + employeeMoney + ")";

                if (productName.indexOf("养老") >= 0) {
                    info.put("endowment", str);
                } else if (productName.indexOf("医疗") >= 0 && productName.indexOf("大病") < 0) {
                    info.put("medical", str);
                } else if (productName.indexOf("失业") >= 0) {
                    info.put("unemployment", str);
                } else if (productName.indexOf("工伤") >= 0 && productName.indexOf("补充") < 0) {
                    info.put("work_related_injury", str);
                } else if (productName.indexOf("生育") >= 0) {
                    info.put("childbirth", str);
                } else if (productName.indexOf("大病") >= 0) {
                    info.put("critical_illness", str);
                } else if (productName.indexOf("公积金") >= 0 && productName.indexOf("补充") < 0) {
                    info.put("housing_accumulation_funds", str);
                }

            }
        }
        info.put("sum", String.valueOf(total));
        Double all = total + serviceFee;
        info.put("total", String.valueOf(all));

        employeesMaintainDao.updateEmployeeOrderFormInfo(newId, info);

        // 修改申报表单订单id
        employeesMaintainDao.updateDeclareEmployeeOrderFormId(oldId, newId);
    }

    /**
     * 方法说明：增员提交:创建公积金申报
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param addEmployeeInfo
     * @Param customerService
     * @author liulei
     * @Date 2020/2/11 16:14
     */
    @Override
    public void createProvidentFundDeclare(BizObjectFacade bizObjectFacade,
                                           WorkflowInstanceFacade workflowInstanceFacade,
                                           ProvidentFundDeclare providentFundDeclare, UserModel customerService)
            throws Exception {
        // 生成公积金申报BizObjectModel
        BizObjectModel bizObjectModel = new BizObjectModel();
        bizObjectModel.setSchemaCode(Constants.PROVIDENT_FUND_DECLARE_SCHEMA);
        bizObjectModel.setSequenceStatus("DRAFT");

        Map<String, Object> data = new HashMap <>();
        // 姓名
        data.put("employee_name", providentFundDeclare.getEmployeeName());
        // 性别
        data.put("gender", providentFundDeclare.getGender());
        // 证件类型
        data.put("identityNo_type", providentFundDeclare.getIdentityNoType());
        // 身份证号码
        data.put("identityNo", providentFundDeclare.getIdentityNo());
        // 出生日期
        data.put("birthday", providentFundDeclare.getBirthday());
        // 派出单位
        data.put("dispatch_unit", providentFundDeclare.getDispatchUnit());
        // 客户名称
        data.put("client_name", providentFundDeclare.getClientName());
        // 客服
        data.put("customer_service", providentFundDeclare.getCustomerService());
        // 客服部门
        data.put("customer_services", providentFundDeclare.getCustomerServices());
        // 申请人
        data.put("applicant", providentFundDeclare.getApplicant());
        // 申请日期
        data.put("application_date", providentFundDeclare.getApplicationDate());
        // 员工订单
        data.put("employee_order_form_id", providentFundDeclare.getEmployeeOrderFormId());
        // 起始月
        data.put("start_month", providentFundDeclare.getStartMonth());
        // 公积金基数
        data.put("provident_fund_base", providentFundDeclare.getProvidentFundBase());
        // 企业缴存额
        data.put("corporate_payment", providentFundDeclare.getCorporatePayment());
        // 个人缴存额
        data.put("personal_deposit", providentFundDeclare.getPersonalDeposit());
        // 缴存总额
        data.put("total_deposit", providentFundDeclare.getTotalDeposit());
        // 福利办理方
        data.put("welfare_handler", providentFundDeclare.getWelfareHandler());
        // 运行负责人
        data.put("operate_leader", providentFundDeclare.getOperateLeader());

        data.put("status", providentFundDeclare.getStatus());

        bizObjectModel.put(data);
        // 创建业务对象模型
        String providentFundId = bizObjectFacade.saveBizObjectModel(customerService.getId(), bizObjectModel, "id");
        log.info(" 创建公积金申报模型:" + providentFundId);
        // 启动流程实例，不结束发起节点，返回流程实例id
        String providentFundIdWfId = workflowInstanceFacade.startWorkflowInstance(customerService.getDepartmentId(),
                customerService.getId(), Constants.PROVIDENT_FUND_DECLARE_SCHEMA_WF, providentFundId, false);
        log.info("启动公积金申报流程:" + providentFundIdWfId);

        // 创建公积金子表数据
        employeesMaintainDao.createSocialSecurityFundDetail(providentFundId, providentFundDeclare.getProvidentFundDetail(),
                Constants.PROVIDENT_FUND_DETAIL);
    }

    /**
     * 方法说明：增员提交:创建社保申报
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param addEmployeeInfo
     * @Param customerService
     * @author liulei
     * @Date 2020/2/11 16:14
     */
    @Override
    public void createSocialSecurityDeclare(BizObjectFacade bizObjectFacade,
                                            WorkflowInstanceFacade workflowInstanceFacade,
                                            SocialSecurityDeclare socialSecurityDeclare, UserModel customerService)
            throws Exception {
        // 生成停缴社保BizObjectModel
        BizObjectModel bizObjectModel = new BizObjectModel();
        bizObjectModel.setSchemaCode(Constants.SOCIAL_SECURITY_DECLARE_SCHEMA);
        bizObjectModel.setSequenceStatus("DRAFT");

        Map<String, Object> data = new HashMap <>();
        // 姓名
        data.put("employee_name", socialSecurityDeclare.getEmployeeName());
        // 性别
        data.put("gender", socialSecurityDeclare.getGender());
        // 证件类型
        data.put("identityNo_type", socialSecurityDeclare.getIdentityNoType());
        // 身份证号码
        data.put("identityNo", socialSecurityDeclare.getIdentityNo());
        // 出生日期
        data.put("birthday", socialSecurityDeclare.getBirthday());
        // 派出单位
        data.put("dispatch_unit", socialSecurityDeclare.getDispatchUnit());
        // 客户名称
        data.put("client_name", socialSecurityDeclare.getClientName());
        // 客服
        data.put("customer_service", socialSecurityDeclare.getCustomerService());
        // 客服部门
        data.put("customer_services", socialSecurityDeclare.getCustomerServices());
        // 申请人
        data.put("applicant", socialSecurityDeclare.getApplicant());
        // 申请日期
        data.put("application_date", socialSecurityDeclare.getApplicationDate());
        // 员工订单
        data.put("employee_order_form_id", socialSecurityDeclare.getEmployeeOrderFormId());
        // 合同签订日期
        data.put("contract_signing_date", socialSecurityDeclare.getContractSigningDate());
        // 合同截止日期
        data.put("contract_deadline", socialSecurityDeclare.getContractDeadline());
        // 起始月
        data.put("start_month", socialSecurityDeclare.getStartMonth());
        // 转正工资
        data.put("positive_salary", socialSecurityDeclare.getPositiveSalary());
        // 缴费基数
        data.put("base_pay", socialSecurityDeclare.getBasePay());
        // 手机号码
        data.put("mobile", socialSecurityDeclare.getMobile());
        // 福利办理方
        data.put("welfare_handler", socialSecurityDeclare.getWelfareHandler());
        // 运行负责人
        data.put("operate_leader", socialSecurityDeclare.getOperateLeader());

        data.put("status", socialSecurityDeclare.getStatus());

        bizObjectModel.put(data);

        // 创建业务对象模型
        String socialSecurityId = bizObjectFacade.saveBizObjectModel(customerService.getId(), bizObjectModel, "id");
        log.info(" 创建社保申报模型:" + socialSecurityId);
        // 启动流程实例，不结束发起节点，返回流程实例id
        String socialSecurityWfId = workflowInstanceFacade.startWorkflowInstance(customerService.getDepartmentId(),
                customerService.getId(), Constants.SOCIAL_SECURITY_DECLARE_SCHEMA_WF, socialSecurityId, false);
        log.info("启动社保申报流程:" + socialSecurityWfId);

        // 创建公积金子表数据
        employeesMaintainDao.createSocialSecurityFundDetail(socialSecurityId,
                socialSecurityDeclare.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
    }

    /**
     * 方法说明：增员提交:创建员工订单
     * @param bizObjectFacade
     * @param employeeOrderForm
     * @param customerService
     * @author liulei
     * @Date 2020/2/21 11:37
     */
    @Override
    public String createEmployeeOrderForm(BizObjectFacade bizObjectFacade, EmployeeOrderForm employeeOrderForm,
                                          UserModel customerService) throws Exception {
        // 生成员工订单BizObjectModel
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode(Constants.EMPLOYEE_ORDER_FORM_SCHEMA);
        model.setSequenceStatus("COMPLETED");

        Map <String, Object> data = new HashMap <>();
        // 员工档案单据号
        data.put("employee_files_id", employeeOrderForm.getEmployeeFilesId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 详细
        data.put("detail", employeeOrderForm.getStartTime() == null ? null :
                sdf.format(employeeOrderForm.getStartTime()));
        // 是否历史订单
        data.put("is_history", "否");
        // 总金额
        data.put("total", employeeOrderForm.getTotal());
        // 社保福利地
        data.put("social_security_city", employeeOrderForm.getSocialSecurityCity());
        // 公积金福利地
        data.put("provident_fund_city", employeeOrderForm.getProvidentFundCity());
        // 养老保险
        data.put("endowment", employeeOrderForm.getEndowment());
        // 医疗保险
        data.put("medical", employeeOrderForm.getMedical());
        // 失业保险
        data.put("unemployment", employeeOrderForm.getUnemployment());
        // 工伤保险
        data.put("work_related_injury", employeeOrderForm.getWorkRelatedInjury());
        // 生育保险
        data.put("childbirth", employeeOrderForm.getChildbirth());
        // 大病保险
        data.put("critical_illness", employeeOrderForm.getCriticalIllness());
        // 住房公积金
        data.put("housing_accumulation_funds", employeeOrderForm.getHousingAccumulationFunds());
        // 服务费
        data.put("service_fee", employeeOrderForm.getServiceFee());
        // 起始时间
        data.put("start_time", employeeOrderForm.getStartTime());
        // 结束时间
        //data.put("end_time", "");
        // 输入时间
        data.put("input_time", new Date());
        // 社保状态
        data.put("social_security_status", "待办");
        // 公积金状态
        data.put("provident_fund_status", "待办");

        model.put(data);
        // 创建业务对象模型
        String modelId = bizObjectFacade.saveBizObjectModel(customerService.getId(), model, "id");
        log.info("创建员工订单业务对象模型:" + modelId);
        // 创建公积金子表数据
        employeesMaintainDao.createSocialSecurityFundDetail(modelId, employeeOrderForm.getSocialSecurityDetail(),
                Constants.SOCIAL_SECURITY_FUND_DETAIL);
        employeesMaintainDao.createSocialSecurityFundDetail(modelId, employeeOrderForm.getProvidentFundDetail(),
                Constants.SOCIAL_SECURITY_FUND_DETAIL);

        // 更新订单的起始时间为子表中开始时间，结束时间
        addEmployeeMapper.updateEmployeeOrderFromTime(modelId);
        return modelId;
    }

    /**
     * 方法说明：增员提交:创建入职通知
     * @Param bizObjectFacade
     * @Param workflowInstanceFacade
     * @Param addEmployeeInfo
     * @Param customerService
     * @author liulei
     * @Date 2020/2/11 16:05
     */
    @Override
    public void createEntryNotice(BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade,
                                  EntryNotice entryNotice, UserModel customerService) throws Exception {
        // 生成入职通知BizObjectModel
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode(Constants.ENTRY_NOTICE_SCHEMA);
        model.setSequenceStatus("DRAFT");

        Map <String, Object> data = new HashMap <>();
        // 姓名
        data.put("employee_name", entryNotice.getEmployeeName());
        // 身份证号码
        data.put("identityNo", entryNotice.getIdentityNo());
        // 客户名称
        data.put("client_name", entryNotice.getClientName());
        // 运行签收人
        data.put("operate_signatory", entryNotice.getOperateSignatory());
        // 社保
        data.put("social_security", entryNotice.getSocialSecurity());
        // 公积金
        data.put("provident_fund", entryNotice.getProvidentFund());

        model.put(data);
        // 创建业务对象模型
        String modelId = bizObjectFacade.saveBizObjectModel(customerService.getId(), model, "id");
        log.info("创建入职通知业务对象模型:" + modelId);
        // 启动流程实例，不结束发起节点，返回流程实例id
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(customerService.getDepartmentId(),
                customerService.getId(), Constants.ENTRY_NOTICE_SCHEMA_WF, modelId, false);
        log.info("启动入职通知流程:" + modelWfId);
    }

    /**
     * 方法说明：增员提交:创建员工档案
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param employeeFiles
     * @param customerService
     * @author liulei
     * @Date 2020/2/11 16:02
     */
    @Override
    public String createEmployeeInfoModel(BizObjectFacade bizObjectFacade,
                                          WorkflowInstanceFacade workflowInstanceFacade,
                                          EmployeeFiles employeeFiles, UserModel customerService) throws Exception {
        // 生成员工档案BizObjectModel
        BizObjectModel model = new BizObjectModel();
        model.setSchemaCode(Constants.EMPLOYEE_FILES_SCHEMA);
        model.setSequenceStatus("COMPLETED");

        Map<String, Object> data = new HashMap <>();
        //委托单位
        data.put("entrusted_unit", employeeFiles.getEntrustedUnit());
        //客户名称
        data.put("client_name", employeeFiles.getClientName());
        //业务员
        data.put("salesman", employeeFiles.getSalesman());
        //员工姓名
        data.put("employee_name", employeeFiles.getEmployeeName());
        //证件类型
        data.put("id_type", employeeFiles.getIdType());
        //证件号码
        data.put("id_no", employeeFiles.getIdNo());
        //性别
        data.put("gender", employeeFiles.getGender());
        //出生年月
        data.put("birth_date", employeeFiles.getBirthDate());
        //员工性质
        data.put("employee_nature", employeeFiles.getEmployeeNature());
        //户籍性质
        data.put("household_register_nature", employeeFiles.getHouseholdRegisterNature());
        //联系电话
        data.put("mobile", employeeFiles.getMobile());
        //邮箱
        data.put("email", employeeFiles.getEmail());
        //合同开始日期
        data.put("labour_contract_start_time", employeeFiles.getLabourContractStartTime());
        //合同结束日期
        data.put("labour_contract_end_time", employeeFiles.getLabourContractEndTime());
        //合同工资
        data.put("salary", employeeFiles.getSalary());
        //社保福利地
        data.put("social_security_city", employeeFiles.getSocialSecurityCity());
        //公积金福利地
        data.put("provident_fund_city", employeeFiles.getProvidentFundCity());
        //报入职时间
        data.put("report_entry_time", employeeFiles.getReportEntryTime());
        //报入职人
        data.put("report_recruits", employeeFiles.getReportRecruits());
        //入职日期
        data.put("entry_time", employeeFiles.getEntryTime());
        //社保收费开始
        data.put("social_security_charge_start", employeeFiles.getSocialSecurityChargeStart());
        //公积金收费开始
        data.put("provident_fund_charge_start", employeeFiles.getProvidentFundChargeStart());
        //社保福利办理方
        data.put("social_security_area", employeeFiles.getSocialSecurityArea());
        //公积金福利办理方
        data.put("provident_fund_area", employeeFiles.getProvidentFundArea());
        //入职备注
        data.put("entry_description", employeeFiles.getEntryDescription());
        //是否入职通知
        data.put("entry_notice", employeeFiles.getEntryNotice());

        data.put("stop_generate_bill", "否");
        data.put("is_old_employee", "否");

        model.put(data);
        // 创建业务对象模型
        String id = bizObjectFacade.saveBizObjectModel(customerService.getId(), model, "id");
        log.info("创建员工档案模型:" + id);

        //  根据id查询单据号
        //String sequenceNo = employeesMaintainDao.getEmployeeFilesSequenceNoById(id);
        return id;
    }

    /**
     * 方法说明：导入时，获取模型Data数据
     * @Param list
     * @Param schemaCode
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @throws
     * @author liulei
     * @Date 2020/2/9 15:56
     */
    private Map<String, Object> getBizObjectModelData(List<String> list, String schemaCode) throws Exception {
        Map<String, Object> data = new HashMap <>();
        if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) { // 增员数据
            // 客户名称
            data.put("client_name", list.get(0));
            // ERP成本中心
            data.put("ERP", list.get(1));
            // 姓名
            data.put("employee_name", list.get(2));
            // 证件类型
            data.put("identityNo_type", list.get(3));
            // 证件号码
            data.put("identityNo", list.get(4));
            // 联系电话
            data.put("mobile", list.get(5));
            // 邮箱
            data.put("email", list.get(6));
            // 户籍性质
            data.put("family_register_nature", list.get(7));
            // 员工性质
            data.put("employee_nature", list.get(8));
            // 入职日期
            if (StringUtils.isNotBlank(list.get(9))) {
                data.put("entry_time", DateUtils.parseDate(list.get(9), Constants.PARSE_PATTERNS));
            }
            // 合同开始日期
            if (StringUtils.isNotBlank(list.get(10))) {
                data.put("contract_start_time", DateUtils.parseDate(list.get(10), Constants.PARSE_PATTERNS));
            }
            // 合同结束日期
            if (StringUtils.isNotBlank(list.get(11))) {
                data.put("contract_end_time", DateUtils.parseDate(list.get(11), Constants.PARSE_PATTERNS));
            }
            // 合同工资
            data.put("contract_salary", list.get(12));
            // 社保福利地
            data.put("social_security_city", list.get(13));
            // 社保起做时间
            if (StringUtils.isNotBlank(list.get(14))) {
                data.put("social_security_start_time", DateUtils.parseDate(list.get(14), Constants.PARSE_PATTERNS));
            }
            // 社保基数
            data.put("social_security_base", list.get(15));
            // 公积金福利地
            data.put("provident_fund_city", list.get(16));
            // 公积金起做时间
            if (StringUtils.isNotBlank(list.get(17))) {
                data.put("provident_fund_start_time", DateUtils.parseDate(list.get(17), Constants.PARSE_PATTERNS));
            }
            // 公积金基数
            data.put("provident_fund_base", list.get(18));
            // 单位公积金比例
            data.put("company_provident_fund_bl", list.get(19));
            // 个人公积金比例
            data.put("employee_provident_fund_bl", list.get(20));
            // 备注
            data.put("remark", list.get(21));
        } else if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) { // 减员数据
            // 客户名称
            data.put("client_name", list.get(0));
            // 姓名
            data.put("employee_name", list.get(1));
            // 证件类型
            data.put("identityNo_type", list.get(2));
            // 身份证号
            data.put("identityNo", list.get(3));
            // 地区
            data.put("city", list.get(4));
            // 离职原因
            data.put("leave_reason", list.get(5));
            // 离职日期
            if (StringUtils.isNotBlank(list.get(6))) {
                data.put("leave_time", DateUtils.parseDate(list.get(6), Constants.PARSE_PATTERNS));
            }
            // 社保终止时间
            if (StringUtils.isNotBlank(list.get(7))) {
                data.put("social_security_end_time", DateUtils.parseDate(list.get(7), Constants.PARSE_PATTERNS));
            }
            // 公积金终止时间
            if (StringUtils.isNotBlank(list.get(8))) {
                data.put("provident_fund_end_time", DateUtils.parseDate(list.get(8), Constants.PARSE_PATTERNS));
            }
            // 备注
            data.put("remark", list.get(9));
        }
        return data;
    }
}
