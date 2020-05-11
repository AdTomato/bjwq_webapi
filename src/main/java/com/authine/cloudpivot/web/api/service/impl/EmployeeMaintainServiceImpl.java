package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.*;
import com.authine.cloudpivot.web.api.service.EmployeeMaintainService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private PaymentApplicationMapper paymentApplicationMapper;

    // 业务list
    private List <BizObjectModel> models = new ArrayList <>();

    @Override
    public void addEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                            DepartmentModel dept) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            importData(workflowInstanceFacade, fileList, user.getId(), dept.getId(), dept.getQueryCode(),
                    Constants.ADD_EMPLOYEE_SCHEMA, Constants.ADD_EMPLOYEE_SCHEMA_WF);
        }
    }

    @Override
    public void deleteEmployee(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                               DepartmentModel dept) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            importData(workflowInstanceFacade, fileList, user.getId(), dept.getId(), dept.getQueryCode(),
                    Constants.DELETE_EMPLOYEE_SCHEMA, Constants.DELETE_EMPLOYEE_SCHEMA_WF);
        }
    }

    /**
     * 方法说明：插入数据
     * @param workflowInstanceFacade 流程实例接口
     * @param fileList 导入的数据
     * @param userId
     * @param deptId
     * @param queryCode
     * @param schemaCode
     * @param schemaWfCode
     * @return void
     * @author liulei
     * @Date 2020/4/20 17:06
     */
    private void importData(WorkflowInstanceFacade workflowInstanceFacade, List <String[]> fileList, String userId,
                            String deptId, String queryCode, String schemaCode, String schemaWfCode) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList <>();
        List<String> idList = new ArrayList <>();
        List<String> fields = Arrays.asList(fileList.get(0));
        for (int i = 2; i < fileList.size(); i++) {
            List <String> list = Arrays.asList(fileList.get(i));
            Map<String, Object> data = new HashMap <>();
            for(int j = 0; j < fields.size(); j++) {
                data.put(fields.get(j), list.get(j));
            }
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            data.put("id", id);
            data.put("creater", userId);
            data.put("createdDeptId", deptId);
            data.put("owner", userId);
            data.put("ownerDeptId", deptId);
            data.put("createdTime", new Date());
            data.put("modifier", userId);
            data.put("modifiedTime", new Date());
            data.put("sequenceStatus", "DRAFT");
            data.put("ownerDeptQueryCode", queryCode);

            dataList.add(data);
            idList.add(id);
        }

        for (int j = 0; j < dataList.size(); j += 500) {
            int size = dataList.size();
            int toPasIndex = 500;
            if (j + 500 > size) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                toPasIndex = size - j;
            }
            List <Map<String, Object>> datas = dataList.subList(j, j + toPasIndex);
            List <String> ids = idList.subList(j, j + toPasIndex);
            // 新增数据
            if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)){
                // 客户增员导入
                addEmployeeMapper.addEmployeeImportData(datas);
                // 更新拥有者，业务员，等数据
                addEmployeeMapper.updateAddEmployeeOwner(datas);
            } else if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                // 减员客户导入
                addEmployeeMapper.deleteEmployeeImportData(datas);
                // 更新拥有者，业务员，等数据
                addEmployeeMapper.updateDeleteEmployeeOwner(datas);
            }
            // 启动流程实例，不结束发起节点
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, deptId, userId, schemaWfCode, ids, false);
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
        data.put("first_level_client_name", employeeOrderForm.getFirstLevelClientName());
        data.put("second_level_client_name", employeeOrderForm.getSecondLevelClientName());
        data.put("business_type", employeeOrderForm.getBusinessType());
        data.put("id_type", employeeOrderForm.getIdType());
        data.put("identityNo", employeeOrderForm.getIdentityNo());
        data.put("s_welfare_handler", employeeOrderForm.getSWelfareHandler());
        data.put("g_welfare_handler", employeeOrderForm.getGWelfareHandler());
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
    public String getOperateLeaderByCity(String city) throws Exception {
        return employeesMaintainDao.getOperateLeaderByCity(city);
    }

    @Override
    public EmployeeOrderForm getEmployeeOrderForm(String socialSecurityCity, Date socialSecurityStartTime,
                                                  Double socialSecurityBase, String providentFundCity,
                                                  Date providentFundStartTime, Double providentFundBase,
                                                  Double gcompanyRatio, Double gemployeeRatio, int sMonth, int gMonth,
                                                  Ccps ccps) throws Exception {
        if (ccps == null) {
            ccps = new Ccps();
        }
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
                            if (policyCollectPay.getProductName().indexOf("工伤") >= 0 && policyCollectPay.getProductName().indexOf("补充") < 0) {
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
                                employeeOrderForm.setEndowment(sum + "");
                            } else if (productName.indexOf("大病") >= 0) {
                                employeeOrderForm.setCriticalIllness(sum + "");
                            } else if (productName.indexOf("医疗") >= 0) {
                                employeeOrderForm.setMedical(sum + "");
                            } else if (productName.indexOf("失业") >= 0) {
                                employeeOrderForm.setUnemployment(sum + "");
                            } else if (productName.indexOf("工伤") >= 0 && productName.indexOf("补充") < 0) {
                                employeeOrderForm.setWorkRelatedInjury(sum + "");
                            } else if (productName.indexOf("生育") >= 0) {
                                employeeOrderForm.setChildbirth(sum + "");
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
                                employeeOrderForm.setHousingAccumulationFunds(sum + "");
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
    public void employeeFilesUpdateSubmit(EmployeeFiles employeeFiles) throws Exception {
        employeesMaintainDao.UpdateEmployeeFilesBetweenTwoTables(employeeFiles.getId(),
                employeeFiles.getEmployeeFilesId());
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

    @Override
    public Ccps getCcps(String firstLevelClientName, String secondLevelClientName) throws Exception {
        List <Ccps> ccpsList = systemManageMapper.getCcpsByClientNames(firstLevelClientName, secondLevelClientName);
        return ccpsList != null && ccpsList.size() > 0 ? ccpsList.get(0) : null;
    }

    @Override
    public int getTimeNode(String city) throws Exception {
        return systemManageMapper.getTimeNodeByCity(city);
    }

    @Override
    public BizObjectModel getEntryNotice(AddEmployee addEmployee, int sMonth, int gMonth,
                                      CollectionRule collectionRule) throws Exception {
        String socialSecurity = null,providentFund = null, recordOfEmployment = null;
        boolean haveEntryNotice =
                "是".equals(addEmployee.getIsDisabled()) || "是".equals(addEmployee.getIsPoorArchivists()) || "是".equals(addEmployee.getIsRetiredSoldier()) ? true : false;
        EntryNotice entryNotice = getEntryNotice(sMonth, gMonth, collectionRule);
        if (entryNotice != null) {
            haveEntryNotice = true;
            socialSecurity = entryNotice.getSocialSecurity();
            providentFund = entryNotice.getProvidentFund();
            recordOfEmployment = entryNotice.getRecordOfEmployment();
        }
        if (haveEntryNotice) {
            BizObjectModel model = GetBizObjectModelUntils.getEntryNotice("PROCESSING", addEmployee.getCreater(),
                    addEmployee.getCreatedDeptId(), addEmployee.getCreatedTime(), addEmployee.getOwner(),
                    addEmployee.getOwnerDeptId(), addEmployee.getOwnerDeptQueryCode(), addEmployee.getEmployeeName(),
                    addEmployee.getIdentityNo(), socialSecurity, providentFund, null, null, null, null, null,
                    null, addEmployee.getRemark(), "[{\"id\":\"" + addEmployee.getOwnerDeptId() + "\",\"type\":1}]",
                    addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName(),
                    addEmployee.getMobile(), addEmployee.getSocialSecurityCity(), addEmployee.getProvidentFundCity(),
                    addEmployee.getIsRetiredSoldier(), addEmployee.getIsDisabled(), addEmployee.getIsPoorArchivists(),
                    recordOfEmployment, null, "通知中");
            return model;
        }
        return null;
    }

    @Override
    public BizObjectModel getEntryNotice(ShAddEmployee shAddEmployee, int sMonth, int gMonth,
                                         CollectionRule collectionRule) throws Exception {
        String socialSecurity = null,providentFund = null, recordOfEmployment = null;
        boolean haveEntryNotice =
                "是".equals(shAddEmployee.getIsDisabled()) || "是".equals(shAddEmployee.getIsPoorArchivists()) || "是".equals(shAddEmployee.getIsRetiredSoldier()) ? true : false;
        EntryNotice entryNotice = getEntryNotice(sMonth, gMonth, collectionRule);
        if (entryNotice != null) {
            haveEntryNotice = true;
            socialSecurity = entryNotice.getSocialSecurity();
            providentFund = entryNotice.getProvidentFund();
            recordOfEmployment = entryNotice.getRecordOfEmployment();
        }
        if (haveEntryNotice) {
            BizObjectModel model = GetBizObjectModelUntils.getEntryNotice("PROCESSING", shAddEmployee.getCreater(),
                    shAddEmployee.getCreatedDeptId(), shAddEmployee.getCreatedTime(), shAddEmployee.getOwner(),
                    shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode(),
                    shAddEmployee.getEmployeeName(), shAddEmployee.getIdentityNo(), socialSecurity, providentFund,
                    null, null, null, null, null, null,
                    shAddEmployee.getInductionRemark(), "[{\"id\":\"" + shAddEmployee.getOwnerDeptId() + "\",\"type\":1}]",
                    shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName(),
                    shAddEmployee.getMobile(), shAddEmployee.getCityName(), shAddEmployee.getCityName(),
                    shAddEmployee.getIsRetiredSoldier(), shAddEmployee.getIsDisabled(),
                    shAddEmployee.getIsPoorArchivists(), recordOfEmployment, null, "通知中");
            return model;
        }
        return null;
    }

    @Override
    public BizObjectModel getEntryNotice(NationwideDispatch nationwideDispatch, int sMonth, int gMonth,
                                         CollectionRule collectionRule) throws Exception {
        String socialSecurity = null,providentFund = null, recordOfEmployment = null;
        boolean haveEntryNotice =
                "是".equals(nationwideDispatch.getIsDisabled()) || "是".equals(nationwideDispatch.getIsPoorArchivists()) || "是".equals(nationwideDispatch.getIsRetiredSoldier()) ? true : false;
        EntryNotice entryNotice = getEntryNotice(sMonth, gMonth, collectionRule);
        if (entryNotice != null) {
            haveEntryNotice = true;
            socialSecurity = entryNotice.getSocialSecurity();
            providentFund = entryNotice.getProvidentFund();
            recordOfEmployment = entryNotice.getRecordOfEmployment();
        }
        if (haveEntryNotice) {
            BizObjectModel model = GetBizObjectModelUntils.getEntryNotice("PROCESSING", nationwideDispatch.getCreater(),
                    nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime(), nationwideDispatch.getOwner(),
                    nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(),
                    nationwideDispatch.getEmployeeName(), nationwideDispatch.getIdentityNo(), socialSecurity, providentFund,
                    null, null, null, null, null, null,
                    nationwideDispatch.getRemark(), "[{\"id\":\"" + nationwideDispatch.getOwnerDeptId() + "\",\"type\":1}]",
                    nationwideDispatch.getFirstLevelClientName(), nationwideDispatch.getSecondLevelClientName(),
                    nationwideDispatch.getContactNumber(), nationwideDispatch.getInvolved(), nationwideDispatch.getInvolved(),
                    nationwideDispatch.getIsRetiredSoldier(), nationwideDispatch.getIsDisabled(),
                    nationwideDispatch.getIsPoorArchivists(), recordOfEmployment, null, "通知中");
            return model;
        }
        return null;
    }

    private EntryNotice getEntryNotice(int sMonth, int gMonth, CollectionRule collectionRule) {
        if (sMonth <= 0 && gMonth <= 0) {
            return  null;
        }
        EntryNotice entryNotice = new EntryNotice();
        boolean haveEntryNotice = false;
        // 有补缴月份
        if (collectionRule != null && StringUtils.isNotBlank(collectionRule.getId())) {
            List<CollectionRuleDetails> details = collectionRule.getCollectionRuleDetails();
            if (details != null && details.size() > 0) {
                for (int i = 0; i < details.size(); i++) {
                    CollectionRuleDetails detail = details.get(i);
                    String insuranceName = detail.getInsuranceName();
                    if (StringUtils.isBlank(insuranceName)) {
                        continue;
                    }
                    int payBackMinMonth = detail.getPayBackMinMonth() == null ? 0 : detail.getPayBackMinMonth();
                    if (insuranceName.indexOf("社保") >= 0) {
                        if (sMonth > payBackMinMonth) {
                            entryNotice.setRecordOfEmployment(collectionRule.getRecordOfEmployment());
                            entryNotice.setSocialSecurity(collectionRule.getSocialSecurity());
                            haveEntryNotice = true;
                        }
                    } else {
                        if (gMonth > payBackMinMonth) {
                            entryNotice.setProvidentFund(collectionRule.getProvidentFund());
                            haveEntryNotice = true;
                        }
                    }
                }
            }
        }
        if (haveEntryNotice) {
            return entryNotice;
        } else {
            return null;
        }
    }

    @Override
    public OperateLeader getOperateLeader(String city, String welfareHandler,
                                          String secondLevelClientName) throws Exception {
        // 先根据二级客户名称查询
        List<OperateLeader> list = systemManageMapper.getOperateLeader(city, welfareHandler, secondLevelClientName);
        if (list != null && list.size() > 0) {
            return  list.get(0);
        } else {
            list = systemManageMapper.getOperateLeaderByCityAndWelfareHandler(city, welfareHandler);
            if (list != null && list.size() > 0) {
                return  list.get(0);
            } else {
                throw new RuntimeException("福利地：" + city + "，福利办理方：" + welfareHandler + " 没有查询到对应的运行负责人！");
            }
        }
    }

    @Override
    public void createSocialSecurityFundDetail(String id, List <Map <String, String>> socialSecurityDetail,
                                               String code) throws Exception {
        employeesMaintainDao.createSocialSecurityFundDetail(id, socialSecurityDetail, code);
    }

    /**
     * 方法说明：更新员工档案数据
     * @author liulei
     * @Date 2020/4/17 14:56
     */
    @Override
    public void updateEmployeeFiles(EmployeeFiles employeeFiles) throws Exception {
        // 更新员工档案数据
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
    }

    @Override
    public void updateEmployeeOrderFormIsHistory(String id) throws Exception {
        employeesMaintainDao.updateEmployeeOrderFormIsHistory(id);
    }

    @Override
    public void updateDeclareEmployeeOrderFormId(String oldId, String newId) throws Exception {
        employeesMaintainDao.updateDeclareEmployeeOrderFormId(oldId, newId);
    }

    @Override
    public void updateSocialSecurityDeclare(SocialSecurityDeclare socialSecurityDeclare) throws Exception {
        addEmployeeMapper.updateSocialSecurityDeclare(socialSecurityDeclare);
        // 删除社保申报中社保数据
        employeesMaintainDao.deleteChildTableDataByTableNameAndParentId("i4fvb_" + Constants.SOCIAL_SECURITY_DETAIL,
                socialSecurityDeclare.getId());
        if (socialSecurityDeclare.getSocialSecurityDetail() != null && socialSecurityDeclare.getSocialSecurityDetail().size() > 0) {
            // 创建公积金子表数据
            employeesMaintainDao.createSocialSecurityFundDetail(socialSecurityDeclare.getId(),
                    socialSecurityDeclare.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
        }
    }

    @Override
    public void updateProvidentFundDeclare(ProvidentFundDeclare providentFundDeclare) throws Exception {
        addEmployeeMapper.updateProvidentFundDeclare(providentFundDeclare);
        if (providentFundDeclare.getProvidentFundDetail() != null) {
            // 删除公积金申报中公积金数据
            employeesMaintainDao.deleteChildTableDataByTableNameAndParentId("i4fvb_" + Constants.PROVIDENT_FUND_DETAIL,
                    providentFundDeclare.getId());
            if (providentFundDeclare.getProvidentFundDetail().size() > 0) {
                // 创建公积金子表数据
                employeesMaintainDao.createSocialSecurityFundDetail(providentFundDeclare.getId(),
                        providentFundDeclare.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
            }
        }
    }

    @Override
    public void updateAddEmployee(String id, String addEmployeeId) throws Exception {
        // 更新增员数据
        employeesMaintainDao.updateAddEmployeeBetweenTwoTables(id, addEmployeeId);
    }

    @Override
    public void updateShAddEmployee(String id, String shAddEmployeeId) throws Exception {
        // 更新数据
        employeesMaintainDao.updateShAddEmployeeBetweenTwoTables(id, shAddEmployeeId);
    }

    @Override
    public void updateQgAddEmployee(String id, String nationwideDispatchId, String type) throws Exception {
        employeesMaintainDao.updateNationwideDispatchBetweenTwoTables(id, nationwideDispatchId, "add");
    }

    @Override
    public SocialSecurityFundDetail getSocialSecurityFundDetail(String id, String productType) throws Exception {
        List<SocialSecurityFundDetail> details = new ArrayList <>();
        if ("公积金".equals(productType)) {
            details = paymentApplicationMapper.getSocialSecurityFundDetailByParentId(id);
        } else {
            details = paymentApplicationMapper.getSocialSecurityFundDetailByParentId1(id);
        }
        if (details != null && details.size() > 0) {
            return details.get(0);
        }
        return null;
    }

    @Override
    public void updateSocialSecurityClose(SocialSecurityClose socialSecurityClose) throws Exception {
        addEmployeeMapper.updateSocialSecurityClose(socialSecurityClose);
    }

    @Override
    public void updateProvidentFundClose(ProvidentFundClose fundClose) throws Exception {
        addEmployeeMapper.updateProvidentFundClose(fundClose);
    }

    @Override
    public void updateDeleteEmployee(String id, String deleteEmployeeId) throws Exception {
        // 根据修改后的数据,更新减员_客户数据
        employeesMaintainDao.updateDeleteEmployeeBetweenTwoTables(id, deleteEmployeeId);
    }

    @Override
    public void updateShDeleteEmployee(String id, String shDeleteEmployeeId) throws Exception {
        // 根据修改后的数据,更新减员_客户数据
        employeesMaintainDao.updateShDeleteEmployeeBetweenTwoTables(id, shDeleteEmployeeId);
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
                String str = sum + "";

                if (productName.indexOf("养老") >= 0) {
                    info.put("endowment", str);
                } else if (productName.indexOf("大病") >= 0) {
                    info.put("critical_illness", str);
                } else if (productName.indexOf("医疗") >= 0) {
                    info.put("medical", str);
                } else if (productName.indexOf("失业") >= 0) {
                    info.put("unemployment", str);
                } else if (productName.indexOf("工伤") >= 0 && productName.indexOf("补充") < 0) {
                    info.put("work_related_injury", str);
                } else if (productName.indexOf("生育") >= 0) {
                    info.put("childbirth", str);
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
     * 方法说明：增员提交:创建员工订单
     * @param bizObjectFacade
     * @param employeeOrderForm
     * @param userId
     * @author liulei
     * @Date 2020/2/21 11:37
     */
    @Override
    public String createEmployeeOrderForm(BizObjectFacade bizObjectFacade, EmployeeOrderForm employeeOrderForm,
                                          String userId, String owner, String ownerDeptId, String ownerDeptQueryCode) throws Exception {
        // 生成员工订单BizObjectModel
        BizObjectModel model = GetBizObjectModelUntils.getBizObjectModel(Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                "COMPLETED", owner, ownerDeptId, ownerDeptQueryCode, null, null, null);

        Map <String, Object> data = new HashMap <>();
        // 员工档案单据号
        data.put("employee_files_id", employeeOrderForm.getEmployeeFilesId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        data.put("first_level_client_name", employeeOrderForm.getFirstLevelClientName());
        data.put("second_level_client_name", employeeOrderForm.getSecondLevelClientName());
        data.put("business_type", employeeOrderForm.getBusinessType());
        data.put("id_type", employeeOrderForm.getIdType());
        data.put("identityNo", employeeOrderForm.getIdentityNo());
        data.put("s_welfare_handler", employeeOrderForm.getSWelfareHandler());
        data.put("g_welfare_handler", employeeOrderForm.getGWelfareHandler());
        // 详细
        data.put("detail", employeeOrderForm.getStartTime() == null ? null :
                sdf.format(employeeOrderForm.getStartTime()));
        // 是否历史订单
        data.put("is_history", "否");
        // 总金额
        data.put("total", employeeOrderForm.getTotal());
        data.put("sum", employeeOrderForm.getSum());
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
        String modelId = bizObjectFacade.saveBizObjectModel(userId, model, "id");
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
