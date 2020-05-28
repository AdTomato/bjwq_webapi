package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dto.UpdateAddEmployeeDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper;
import com.authine.cloudpivot.web.api.mapper.EmployeeFilesMapper;
import com.authine.cloudpivot.web.api.mapper.NccpsMapper;
import com.authine.cloudpivot.web.api.service.AddEmployeeService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.AddEmployeeServiceImpl
 * @Date 2020/2/28 14:33
 **/
@Service
public class AddEmployeeServiceImpl implements AddEmployeeService {

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Resource
    private CollectionRuleMapper collectionRuleMapper;

    @Resource
    private NccpsMapper nccpsMapper;

    @Resource
    private SalesContractService salesContractService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    @Override
    public AddEmployee getAddEmployeeById(String id) throws Exception {
        AddEmployee addEmployee = addEmployeeMapper.getAddEmployeeById(id);
        if (addEmployee == null) {
            throw new RuntimeException("没有获取到增员数据！");
        }
        // 判断是否是六安，是基数四舍五入取整
        addEmployee = CommonUtils.needBaseRounding(addEmployee);
        return addEmployee;
    }

    @Override
    public ShAddEmployee getShAddEmployeeById(String id) throws Exception {
        ShAddEmployee shAddEmployee = addEmployeeMapper.getShAddEmployeeById(id);
        if (shAddEmployee == null) {
            throw new RuntimeException("没有获取到增员数据！");
        }
        return shAddEmployee;
    }

    @Override
    public NationwideDispatch getQgAddEmployeeById(String id) throws Exception {
        NationwideDispatch nationwideDispatch = addEmployeeMapper.getQgAddEmployeeById(id);
        if (nationwideDispatch == null) {
            throw new RuntimeException("没有获取到增员数据！");
        }
        return nationwideDispatch;
    }

    @Override
    public EmployeeOrderForm getEmployeeOrderFormByEmployeeFilesId(String id) throws Exception {
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(id);
        return orderForm;
    }

    @Override
    public ServiceChargeUnitPrice createAddEmployeeData(AddEmployee addEmployee, String employeeFilesId,
                                      BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        UpdateAddEmployeeDto dto = getAddEmployeeData(addEmployee, employeeFilesId);
        // 创建员工订单
        EmployeeOrderForm orderForm = dto.getOrderForm();
        String city = StringUtils.isNotBlank(orderForm.getSocialSecurityCity()) ? orderForm.getSocialSecurityCity() :
                orderForm.getProvidentFundCity();
        ServiceChargeUnitPrice price = salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                orderForm.getBusinessType(), Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(city) < 0 ? "省外" : "省内", city);
        if (price != null) {
            orderForm.setPrecollected(price.getPrecollected());
            orderForm.setPayCycle(price.getPayCycle());
        }
        String orderFormId = createEmployeeOrderForm(orderForm, bizObjectFacade);
        if (price != null) {
            price.setOrderFormId(orderFormId);
        }
        SocialSecurityDeclare sDeclare = dto.getSDeclare();
        if (sDeclare != null) {
            sDeclare.setEmployeeOrderFormId(orderFormId);
            // 创建社保申报数据
            createSocialSecurityDeclare(sDeclare, bizObjectFacade, workflowInstanceFacade);
        }
        ProvidentFundDeclare pDeclare = dto.getPDeclare();
        if (pDeclare != null) {
            pDeclare.setEmployeeOrderFormId(orderFormId);
            // 创建公积金申报数据
            createProvidentFundDeclare(pDeclare, bizObjectFacade, workflowInstanceFacade);
        }

        // 是否入职通知
        boolean haveEntryNotice = false;
        if ("是".equals(addEmployee.getIsDisabled()) || "是".equals(addEmployee.getIsPoorArchivists()) ||
                "是".equals(addEmployee.getIsRetiredSoldier())) {
            haveEntryNotice = true;
        }
        if (orderForm.getPayBackList() != null && orderForm.getPayBackList().size() > 0) {
            // 有补缴数据
            haveEntryNotice = true;
        }
        if (haveEntryNotice) {
            EntryNotice entryNotice = dto.getEntryNotice();
            entryNotice.setOperateSignatory(sDeclare == null ? pDeclare.getOperateLeader() : sDeclare.getOperateLeader());
            // 创建入职通知数据
            createEntryNotice(entryNotice, bizObjectFacade, workflowInstanceFacade);
        }

        return price;
    }


    @Override
    public ServiceChargeUnitPrice addAddEmployeeData(AddEmployee addEmployee, EmployeeFiles employeeFiles,
                                   BizObjectFacade bizObjectFacade, WorkflowInstanceFacade workflowInstanceFacade) throws Exception{
        ServiceChargeUnitPrice price = null;
                // 查询员工订单数据
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
        if (orderForm != null) {
            // 查询征缴规则
            CollectionRuleMaintain sCollectionRule = null, gCollectionRule = null;
            // 汇缴订单明细排序,补缴订单明细排序
            Double remittanceSortKey = orderForm.getRemittanceSortKey(), payBackSortKey = orderForm.getPayBackSortKey();
            if (addEmployee.getSocialSecurityBase() - 0d > 0d && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getSocialSecurityCity()) >= 0) {
                // 更新订单数据
                orderForm.setSocialSecurityStatus("待办");
                orderForm.setSocialSecurityCity(addEmployee.getSocialSecurityCity());
                orderForm.setSWelfareHandler(addEmployee.getSWelfareHandler());
                orderForm.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
                orderForm.setSocialSecurityChargeStart(addEmployee.getSocialSecurityStartTime());

                sCollectionRule = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getSocialSecurityCity(),
                        addEmployee.getSWelfareHandler());
                if (sCollectionRule == null) {
                    throw new RuntimeException("没有查询到社保对应的征缴规则数据");
                }
                SocialSecurityDeclare declare = getSocialSecurityDeclareData(addEmployee, employeeFiles.getId(),
                        Integer.parseInt(sdf.format(new Date())), new EntryNotice(), sCollectionRule,
                        remittanceSortKey, payBackSortKey);
                String handler = addEmployeeMapper.getHsLevyHandler(declare.getCity(), declare.getWelfareHandler(),
                        "社保", declare.getSecondLevelClientName());
                declare.setEmployeeOrderFormId(orderForm.getId());
                declare.setOperateLeader(handler);
                // 创建社保申报数据
                createSocialSecurityDeclare(declare, bizObjectFacade, workflowInstanceFacade);
                if (declare.getPayBackList() != null && declare.getPayBackList().size() > 0) {
                    addEmployeeMapper.createEmployeeOrderFormDetails(declare.getPayBackList(), orderForm.getId(),
                            "i4fvb_order_details_pay_back");
                }
                if (declare.getRemittanceList() != null && declare.getRemittanceList().size() > 0) {
                    addEmployeeMapper.createEmployeeOrderFormDetails(declare.getRemittanceList(), orderForm.getId(),
                            "i4fvb_order_details_remittance");
                }
            }
            if (addEmployee.getProvidentFundBase() - 0d > 0d && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getProvidentFundCity()) >= 0) {
                // 更新订单数据
                orderForm.setProvidentFundStatus("待办");
                orderForm.setProvidentFundCity(addEmployee.getProvidentFundCity());
                orderForm.setGWelfareHandler(addEmployee.getGWelfareHandler());
                orderForm.setProvidentFundBase(addEmployee.getProvidentFundBase());
                orderForm.setProvidentFundChargeStart(addEmployee.getProvidentFundStartTime());

                gCollectionRule = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getProvidentFundCity(),
                        addEmployee.getGWelfareHandler());
                if (gCollectionRule == null) {
                    throw new RuntimeException("没有查询到公积金对应的征缴规则数据");
                }
                ProvidentFundDeclare declare = getProvidentFundDeclareData(addEmployee, employeeFiles.getId(),
                        Integer.parseInt(sdf.format(new Date())), new EntryNotice(), gCollectionRule,
                        remittanceSortKey, payBackSortKey);
                String handler = addEmployeeMapper.getHsLevyHandler(declare.getCity(), declare.getWelfareHandler(),
                        "公积金", declare.getSecondLevelClientName());
                declare.setEmployeeOrderFormId(orderForm.getId());
                declare.setOperateLeader(handler);
                // 创建公积金申报数据
                createProvidentFundDeclare(declare, bizObjectFacade, workflowInstanceFacade);
                if (declare.getPayBackList() != null && declare.getPayBackList().size() > 0) {
                    addEmployeeMapper.createEmployeeOrderFormDetails(declare.getPayBackList(), orderForm.getId(),
                            "i4fvb_order_details_pay_back");
                }
                if (declare.getRemittanceList() != null && declare.getRemittanceList().size() > 0) {
                    addEmployeeMapper.createEmployeeOrderFormDetails(declare.getRemittanceList(), orderForm.getId(),
                            "i4fvb_order_details_remittance");
                }
            }
            // 更新订单的服务费数据
            addEmployeeMapper.delOrderFormServiceChargeUnitPrice(orderForm.getId(), "i4fvb_order_details_pay_back");
            addEmployeeMapper.delOrderFormServiceChargeUnitPrice(orderForm.getId(), "i4fvb_order_details_remittance");
            String city = StringUtils.isNotBlank(orderForm.getSocialSecurityCity()) ? orderForm.getSocialSecurityCity() :
                    orderForm.getProvidentFundCity();
            price = salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                    orderForm.getBusinessType(), Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(city) < 0 ? "省外" : "省内", city);
            if (price != null) {
                price.setOrderFormId(orderForm.getId());
            }
            addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
        } else {
            // 没有查询到员工档案数据
            price = createAddEmployeeData(addEmployee, employeeFiles.getId(), bizObjectFacade, workflowInstanceFacade);
        }
        // 更新员工档案数据
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            employeeFiles.setSbAddEmployeeId(addEmployee.getId());
            employeeFiles.setSocialSecurityCity(addEmployee.getSocialSecurityCity());
            employeeFiles.setSWelfareHandler(addEmployee.getSWelfareHandler());
            employeeFiles.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
            employeeFiles.setSocialSecurityChargeStart(addEmployee.getSocialSecurityStartTime());
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            employeeFiles.setGjjAddEmployeeId(addEmployee.getId());
            employeeFiles.setProvidentFundCity(addEmployee.getProvidentFundCity());
            employeeFiles.setGWelfareHandler(addEmployee.getGWelfareHandler());
            employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundBase());
            employeeFiles.setProvidentFundChargeStart(addEmployee.getProvidentFundStartTime());
        }
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        return price;
    }

    @Override
    public void updateEmployeeFiles(EmployeeFiles employeeFiles) throws Exception {
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
    }

    @Override
    public EmployeeFiles getEmployeeFilesByAddEmployeeId(String id) throws Exception {
        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(id);
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据！");
        }
        return employeeFiles;
    }

    @Override
    public SocialSecurityDeclare getSocialSecurityDeclareByAddEmployeeId(String id) throws Exception {
        return addEmployeeMapper.getSocialSecurityDeclareByAddEmployeeId(id);
    }

    @Override
    public ProvidentFundDeclare getProvidentFundDeclareByAddEmployeeId(String id) throws Exception {
        return addEmployeeMapper.getProvidentFundDeclareByAddEmployeeId(id);
    }

    @Override
    public UpdateAddEmployeeDto getAddEmployeeData(AddEmployee addEmployee, String employeeFilesId) throws Exception {
        UpdateAddEmployeeDto dto = new UpdateAddEmployeeDto();
        int curMonth = Integer.parseInt(sdf.format(new Date()));
        // 员工订单
        EmployeeOrderForm orderForm = new EmployeeOrderForm(addEmployee, employeeFilesId);
        // 查询
        // 入职通知
        EntryNotice entryNotice = new EntryNotice(addEmployee);
        // 社保申报
        SocialSecurityDeclare sDeclare = null;
        // 公积金申报
        ProvidentFundDeclare pDeclare = null;
        // 查询征缴规则
        CollectionRuleMaintain sCollectionRule = null, gCollectionRule = null;
        // 汇缴订单明细排序,补缴订单明细排序
        Double remittanceSortKey = 0d, payBackSortKey = 0d;
        // 补缴订单明细
        List <EmployeeOrderFormDetails> payBackList = new ArrayList <>();
        // 汇缴订单明细
        List <EmployeeOrderFormDetails> remittanceList = new ArrayList <>();
        // 生成社保申报信息
        if (addEmployee.getSocialSecurityBase() - 0d > 0d && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getSocialSecurityCity()) >= 0) {
            sCollectionRule = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getSocialSecurityCity(),
                    addEmployee.getSWelfareHandler());
            if (sCollectionRule == null) {
                throw new RuntimeException("没有查询到社保对应的征缴规则数据");
            }
            sDeclare = getSocialSecurityDeclareData(addEmployee, employeeFilesId, curMonth, entryNotice, sCollectionRule, remittanceSortKey, payBackSortKey);
            if (sDeclare.getPayBackList() != null && sDeclare.getPayBackList().size() > 0) {
                payBackList.addAll(sDeclare.getPayBackList());
            }
            if (sDeclare.getRemittanceList() != null && sDeclare.getRemittanceList().size() > 0) {
                remittanceList.addAll(sDeclare.getRemittanceList());
            }
            String handler = addEmployeeMapper.getHsLevyHandler(sDeclare.getCity(), sDeclare.getWelfareHandler(),
                    "社保", sDeclare.getSecondLevelClientName());
            sDeclare.setOperateLeader(handler);
        }

        // 生成公积金申报信息
        if (addEmployee.getProvidentFundBase() - 0d > 0d && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getProvidentFundCity())  >= 0) {
            if (addEmployee.getProvidentFundCity().equals(addEmployee.getProvidentFundCity())
                    && addEmployee.getGWelfareHandler().equals(addEmployee.getSWelfareHandler())){
                gCollectionRule = sCollectionRule;
            } else {
                gCollectionRule = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getProvidentFundCity(),
                        addEmployee.getGWelfareHandler());
            }
            if (gCollectionRule == null) {
                throw new RuntimeException("没有查询到公积金对应的征缴规则数据");
            }
            remittanceSortKey = remittanceList == null || remittanceList.size()==0 ? 0d : remittanceList.size()*1.0;
            payBackSortKey  = payBackList == null || payBackList.size()==0 ? 0d : payBackList.size()*1.0;

            pDeclare = getProvidentFundDeclareData(addEmployee, employeeFilesId, curMonth, entryNotice,
                    gCollectionRule, remittanceSortKey, payBackSortKey);

            if(pDeclare.getPayBackList() != null && pDeclare.getPayBackList().size() > 0) {
                payBackList.addAll(pDeclare.getPayBackList());
            }
            if(pDeclare.getRemittanceList() != null && pDeclare.getRemittanceList().size() > 0) {
                remittanceList.addAll(pDeclare.getRemittanceList());
            }
            String handler = addEmployeeMapper.getHsLevyHandler(pDeclare.getCity(), pDeclare.getWelfareHandler(),
                    "公积金", pDeclare.getSecondLevelClientName());
            pDeclare.setOperateLeader(handler);
        }

        orderForm.setPayBackList(payBackList);
        orderForm.setRemittanceList(remittanceList);

        dto.setOrderForm(orderForm);
        dto.setSDeclare(sDeclare);
        dto.setPDeclare(pDeclare);
        dto.setEntryNotice(entryNotice);
        return dto;
    }

    /**
     * 方法说明：根据增员数据获取公积金申报数据
     * @param addEmployee
     * @param employeeFilesId
     * @param curMonth
     * @param entryNotice
     * @param sCollectionRule
     * @param remittanceSortKey
     * @param payBackSortKey
     * @return com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare
     * @author liulei
     * @Date 2020/5/14 14:09
     */
    private ProvidentFundDeclare getProvidentFundDeclareData(AddEmployee addEmployee, String employeeFilesId,
                                                             int curMonth, EntryNotice entryNotice,
                                                             CollectionRuleMaintain sCollectionRule,
                                                             Double remittanceSortKey, Double payBackSortKey) throws Exception{
        // 判断当前月是否为补缴月
        Map <String, String> curMonthCanPayBack = new HashMap <>();
        ProvidentFundDeclare declare = new ProvidentFundDeclare(addEmployee, employeeFilesId);
        // 查询补缴开始申报月
        int sbMonth = Integer.parseInt(sdf.format(addEmployee.getProvidentFundStartTime()));
        if (sbMonth <= curMonth) {
            // 查询补缴规则数据,按产品名称，开始时间倒序排列
            List <PaymentRules> paymentRules = collectionRuleMapper.getGjjPaymentRules(sCollectionRule.getId(),
                    sdf.format(addEmployee.getProvidentFundStartTime()), addEmployee.getProvidentFundName(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl());
            // 创建补缴订单数据
            List <EmployeeOrderFormDetails> gPayBack = createPayBackData(paymentRules, curMonthCanPayBack,
                    sbMonth, curMonth, addEmployee.getProvidentFundBase(), null, payBackSortKey);
            if (gPayBack != null && gPayBack.size() > 0) {
                // 有公积金补缴，赋值入职通知数据
                entryNotice.setProvidentFund(sCollectionRule.getProvidentFundDeclarationMaterials());

                declare.setPayBackList(gPayBack);
            }
        }
        // 汇缴订单明细
        List <CollectionRule> collectionRule =
                collectionRuleMapper.getGjjCollectionRules(sCollectionRule.getId(), sbMonth <= curMonth ?
                        String.valueOf(curMonth) : String.valueOf(sbMonth), addEmployee.getProvidentFundName(),
                        addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl());
        // 汇缴开始时间初始化为开始申报时间和当前时间最大的一个
        List <EmployeeOrderFormDetails> gRemittance = createRemittanceData(collectionRule, curMonthCanPayBack,
                sbMonth, curMonth , addEmployee.getSocialSecurityBase(), null, remittanceSortKey, declare);
        if (gRemittance != null && gRemittance.size() > 0) {
            declare.setRemittanceList(gRemittance);
        }
        return declare;
    }

    /**
     * 方法说明：根据增员数据获取社保申报数据
     * @param addEmployee
     * @param employeeFilesId
     * @param curMonth
     * @param entryNotice
     * @param sCollectionRule
     * @param remittanceSortKey
     * @param payBackSortKey
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
     * @author liulei
     * @Date 2020/5/14 14:01
     */
    private SocialSecurityDeclare getSocialSecurityDeclareData(AddEmployee addEmployee, String employeeFilesId,
                                                               int curMonth, EntryNotice entryNotice,
                                                               CollectionRuleMaintain sCollectionRule,
                                                               Double remittanceSortKey, Double payBackSortKey) throws Exception {
        // 判断当前月是否为补缴月
        Map <String, String> curMonthCanPayBack = new HashMap <>();
        SocialSecurityDeclare declare = new SocialSecurityDeclare(addEmployee, employeeFilesId);
        Nccps nccps = nccpsMapper.getNccpsByClientName(addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName());
        NccpsWorkInjuryRatio workInjuryRatio = new NccpsWorkInjuryRatio();
        if (nccps != null) {
            workInjuryRatio = nccpsMapper.getNccpsWorkInjuryRatioByParentIdAndCity(nccps.getId(),
                    addEmployee.getSocialSecurityCity(), addEmployee.getSWelfareHandler());
            if (workInjuryRatio == null) {
                workInjuryRatio = new NccpsWorkInjuryRatio();
            }
        }

        // 查询补缴开始申报月
        int sbMonth = Integer.parseInt(sdf.format(addEmployee.getSocialSecurityStartTime()));
        if (sbMonth <= curMonth) {
            // 查询补缴规则数据,按产品名称，开始时间倒序排列
            List <PaymentRules> paymentRules = collectionRuleMapper.getSbPaymentRules(sCollectionRule.getId(),
                    sdf.format(addEmployee.getSocialSecurityStartTime()));
            // 创建补缴订单数据
            List <EmployeeOrderFormDetails> sPayBack = createPayBackData(paymentRules, curMonthCanPayBack,
                    sbMonth, curMonth, addEmployee.getSocialSecurityBase(), workInjuryRatio, payBackSortKey);
            if (sPayBack != null && sPayBack.size() > 0) {
                // 有社保补缴，赋值入职通知数据
                entryNotice.setRecordOfEmployment(sCollectionRule.getEmploymentFilingMaterials());
                entryNotice.setSocialSecurity(sCollectionRule.getSocialSecurityDeclarationMaterials());
                declare.setPayBackList(sPayBack);
            }
        }
        // 汇缴订单明细
        List <CollectionRule> collectionRule =
                collectionRuleMapper.getSbCollectionRules(sCollectionRule.getId(), sbMonth <= curMonth ?
                        String.valueOf(curMonth) : String.valueOf(sbMonth));
        // 汇缴开始时间初始化为开始申报时间和当前时间最大的一个
        List <EmployeeOrderFormDetails> sRemittance = createRemittanceData(collectionRule, curMonthCanPayBack,
                sbMonth, curMonth , addEmployee.getSocialSecurityBase(), workInjuryRatio, remittanceSortKey, null);
        if (sRemittance != null && sRemittance.size() > 0) {
            declare.setRemittanceList(sRemittance);
        }
        return declare;
    }

    @Override
    public EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName,
                                                                   String secondLevelClientName, String identityNo) throws Exception {
        return addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(firstLevelClientName, secondLevelClientName,
                identityNo);
    }

    private void createEntryNotice(EntryNotice entryNotice, BizObjectFacade bizObjectFacade,
                                   WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        BizObjectModel model =  GetBizObjectModelUntils.getEntryNotice(entryNotice);
        String id = bizObjectFacade.saveBizObjectModel(entryNotice.getCreater(), model, "id");
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(entryNotice.getCreatedDeptId(),
                entryNotice.getCreater(), Constants.ENTRY_NOTICE_SCHEMA_WF, id, true);
        System.out.println("创建入职通知业务对象成功：" + id + "; 启动入职通知流程成功:" + modelWfId);
    }

    @Override
    public void createProvidentFundDeclare(ProvidentFundDeclare pDeclare, BizObjectFacade bizObjectFacade,
                                            WorkflowInstanceFacade workflowInstanceFacade) throws Exception{
        BizObjectModel model =  GetBizObjectModelUntils.getProvidentFundDeclare(pDeclare);
        String id = bizObjectFacade.saveBizObjectModel(pDeclare.getCreater(), model, "id");
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(pDeclare.getCreatedDeptId(),
                pDeclare.getCreater(), Constants.PROVIDENT_FUND_DECLARE_SCHEMA_WF, id, true);
        if (pDeclare.getPayBackList() != null && pDeclare.getPayBackList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(pDeclare.getPayBackList(), id,
                    "i4fvb_order_details_pay_back_gjj");
        }
        if (pDeclare.getRemittanceList() != null && pDeclare.getRemittanceList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(pDeclare.getRemittanceList(), id,
                    "i4fvb_order_details_remittance_gjj");
        }
        System.out.println("创建公积金申报业务对象成功：" + id + "; 启动公积金申报流程成功:" + modelWfId);
    }

    @Override
    public void createSocialSecurityDeclare(SocialSecurityDeclare sDeclare, BizObjectFacade bizObjectFacade,
                                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception{
        BizObjectModel model =  GetBizObjectModelUntils.getSocialSecurityDeclare(sDeclare);
        String id = bizObjectFacade.saveBizObjectModel(sDeclare.getCreater(), model, "id");
        String modelWfId = workflowInstanceFacade.startWorkflowInstance(sDeclare.getCreatedDeptId(),
                sDeclare.getCreater(), Constants.SOCIAL_SECURITY_DECLARE_SCHEMA_WF, id, true);
        if (sDeclare.getPayBackList() != null && sDeclare.getPayBackList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(sDeclare.getPayBackList(), id,
                    "i4fvb_order_details_pay_back_sb");
        }
        if (sDeclare.getRemittanceList() != null && sDeclare.getRemittanceList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(sDeclare.getRemittanceList(), id,
                    "i4fvb_order_details_remittance_sb");
        }
        System.out.println("创建社保申报业务对象成功：" + id + "; 启动社保申报流程成功:" + modelWfId);
    }

    @Override
    public String createEmployeeOrderForm(EmployeeOrderForm orderForm, BizObjectFacade bizObjectFacade) throws Exception{
        // 生成员工订单BizObjectModel
        BizObjectModel model = GetBizObjectModelUntils.getBizObjectModel(Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                orderForm.getSequenceStatus(), orderForm.getOwner(), orderForm.getOwnerDeptId(),
                orderForm.getOwnerDeptQueryCode(), orderForm.getCreater(), orderForm.getCreatedDeptId(),
                orderForm.getCreatedTime());
        Map <String, Object> data = new HashMap <>();
        // 	详细
        data.put("detail", orderForm.getDetail());
        // 	社保状态
        data.put("social_security_status", orderForm.getSocialSecurityStatus());
        // 	公积金状态
        data.put("provident_fund_status", orderForm.getProvidentFundStatus());
        // 	社保福利地
        data.put("social_security_city", orderForm.getSocialSecurityCity());
        // 	公积金福利地
        data.put("provident_fund_city", orderForm.getProvidentFundCity());
        // 	员工订单
        data.put("employee_files_id", orderForm.getEmployeeFilesId());
        // 	一级客户名称
        data.put("first_level_client_name", orderForm.getFirstLevelClientName());
        // 	二级客户名称
        data.put("second_level_client_name", orderForm.getSecondLevelClientName());
        // 	证件类型
        data.put("id_type", orderForm.getIdType());
        // 	业务类型
        data.put("business_type", orderForm.getBusinessType());
        // 	证件号码
        data.put("identityNo", orderForm.getIdentityNo());
        // 	社保福利办理方
        data.put("s_welfare_handler", orderForm.getSWelfareHandler());
        // 	公积金利办理方
        data.put("g_welfare_handler", orderForm.getGWelfareHandler());
        // 	社保基数
        data.put("social_security_base", orderForm.getSocialSecurityBase());
        // 	公积金基数
        data.put("provident_fund_base", orderForm.getProvidentFundBase());
        // 	社保起做时间
        data.put("social_security_charge_start", orderForm.getSocialSecurityChargeStart());
        // 	公积金起做时间
        data.put("provident_fund_charge_start", orderForm.getProvidentFundChargeStart());
        // 	社保截止时间
        data.put("social_security_charge_end", orderForm.getSocialSecurityChargeEnd());
        // 	公积金截止时间
        data.put("provident_fund_charge_end", orderForm.getProvidentFundChargeEnd());
        // 	是否预收
        data.put("precollected", orderForm.getPrecollected());
        // 	收费频率
        data.put("pay_cycle", orderForm.getPayCycle());
        model.put(data);
        // 创建业务对象模型
        String modelId = bizObjectFacade.saveBizObjectModel(orderForm.getCreater(), model, "id");
        System.out.println("创建员工订单业务对象模型:" + modelId);
        if (orderForm.getPayBackList() != null && orderForm.getPayBackList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(orderForm.getPayBackList(), modelId,
                    "i4fvb_order_details_pay_back");
        }
        if (orderForm.getRemittanceList() != null && orderForm.getRemittanceList().size() > 0) {
            addEmployeeMapper.createEmployeeOrderFormDetails(orderForm.getRemittanceList(), modelId,
                    "i4fvb_order_details_remittance");
        }

        return modelId;
    }

    @Override
    public String getHsLevyHandler(String city, String welfareHandler, String type, String secondLevelClientName) throws Exception {
        return addEmployeeMapper.getHsLevyHandler(city, welfareHandler, type, secondLevelClientName);
    }

    @Override
    public void updateAddEmployee(AddEmployee addEmployee) throws Exception {
        addEmployeeMapper.updateAddEmployee(addEmployee);
    }

    @Override
    public void updateShAddEmployee(ShAddEmployee shAddEmployee) throws Exception {
        addEmployeeMapper.updateShAddEmployee(shAddEmployee);
    }

    @Override
    public void updateQgAddEmployee(NationwideDispatch nationwideDispatch) throws Exception {
        addEmployeeMapper.updateQgAddEmployee(nationwideDispatch);
    }

    private List <EmployeeOrderFormDetails> createRemittanceData(List <CollectionRule> rules,
                                                                 Map <String, String> curMonthCanPayBack, int sbMonth,
                                                                 int curMonth, Double base,
                                                                 NccpsWorkInjuryRatio workInjuryRatio,
                                                                 Double sortKey, ProvidentFundDeclare declare) throws Exception {
        List <EmployeeOrderFormDetails> remittances = new ArrayList <>();
        if (rules == null || rules.size() == 0) {
            return remittances;
        }

        for (int i = 0; i < rules.size(); i++) {
            CollectionRule rule = rules.get(i);
            // 申报的开始时间如果大于当前时间，汇缴的开始时间为申报时间，
            int curSb = 0;
            if (StringUtils.isBlank(curMonthCanPayBack.get(rule.getProductName())) ||
                    "是".equals(curMonthCanPayBack.get(rule.getProductName()))) {
                // 申报当月是补缴月，判断客户设置的开始缴纳时间与当前月+1比较大小
                curSb = sbMonth > curMonth + 1 ? sbMonth : curMonth + 1;
            } else {
                curSb = sbMonth > curMonth ? sbMonth : curMonth;
            }
            // 当前开始时间
            int startMonth = Integer.parseInt(sdf.format(rule.getEffectiveStartMonth()));
            // 当前结束时间
            int endMonth = rule.getEffectiveTerminationMonth() == null ? Integer.MAX_VALUE :
                    Integer.parseInt(sdf.format(rule.getEffectiveTerminationMonth()));
            if (endMonth < curSb) {
                continue;
            }
            // 当前创建的汇缴数据的开始时间（汇缴规则的开始时间与申报时间最大的一个）
            int curDateStart = startMonth <= curSb ? curSb : startMonth;
            sortKey = sortKey + 1;
            EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rule, base, workInjuryRatio, sortKey);
            details.setStartChargeTime(sdf.parse(String.valueOf(curDateStart)));
            if (details.getProductName().indexOf("公积金") >= 0 && details.getProductName().indexOf("补充") < 0) {
                // 企业，个人，合计征缴额赋值到公积金申报里面
                declare.setCorporatePayment(details.getCompanyMoney());
                declare.setPersonalDeposit(details.getEmployeeMoney());
                declare.setTotalDeposit(details.getSum());
            }
            remittances.add(details);
        }
        return remittances;
    }

    private List <EmployeeOrderFormDetails> createPayBackData(List <PaymentRules> paymentRules,
                                                              Map <String, String> curMonthCanPayBack, int sbMonth,
                                                              int curMonth, Double base,
                                                              NccpsWorkInjuryRatio workInjuryRatio, Double sortKey) throws Exception {
        List <EmployeeOrderFormDetails> payBacks = new ArrayList <>();
        if (paymentRules == null || paymentRules.size() == 0) {
            return payBacks;
        }
        String productName = "";
        String canCrossYearPayBack = "";
        // 待补缴结束时间
        int payEnd = curMonth;
        for (int i = 0; i < paymentRules.size(); i++) {
            PaymentRules rules = paymentRules.get(i);
            if (!rules.getProductName().equals(productName)) {
                // 险种第一次出现, 此时通过判断申报当月是否为补缴月来确定当前险种的补缴最末一个月
                productName = rules.getProductName();
                canCrossYearPayBack = rules.getCanCrossYearPayBack();
                // 记录当前月是否为补缴月
                curMonthCanPayBack.put(productName, rules.getCurMonthCanPayBack());
                payEnd = "是".equals(rules.getCurMonthCanPayBack()) ? curMonth : curMonth - 1;
                if (payEnd < sbMonth) {
                    // 如果最末一个月 < 申报月，则不补缴(只补缴申报月的情况，此时申报月是汇缴月)
                    continue;
                }
            } else if (!"是".equals(canCrossYearPayBack)){
                // 不可以跨年补缴
                continue;
            }
            if ("是".equals(rules.getCanPayBack())) {
                // 当前开始时间
                int startMonth = Integer.parseInt(sdf.format(rules.getEffectiveStartMonth()));
                // 当前结束时间
                int endMonth = rules.getEffectiveTerminationMonth() == null ? Integer.MAX_VALUE :
                        Integer.parseInt(sdf.format(rules.getEffectiveTerminationMonth()));
                // 当前创建的补缴数据的开始时间（补缴规则的开始时间与申报时间最大的一个）
                int curDateStart = startMonth <= sbMonth ? sbMonth : startMonth;
                // 当前创建的补缴数据的结束时间（补缴规则的结束时间与补缴结束月最小的一个）
                int curDateEnd = endMonth <= payEnd ? endMonth : payEnd;
                // 当前险种周期内补缴月份
                int payMonth = curDateEnd - curDateStart + 1;
                if (payMonth >= rules.getPaymentBackMinMonth() && payMonth < rules.getPaymentBackMaxMonth()) {
                    sortKey = sortKey + 1;
                    EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rules, base, workInjuryRatio, sortKey);
                    details.setStartChargeTime(sdf.parse(String.valueOf(curDateStart)));
                    details.setEndChargeTime(sdf.parse(String.valueOf(curDateEnd)));
                    payBacks.add(details);
                } else if (payMonth < rules.getPaymentBackMinMonth()){
                    continue;
                } else if (payMonth >= rules.getPaymentBackMaxMonth()){
                    Date endTime = sdf.parse(String.valueOf(curDateEnd));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(endTime);
                    calendar.add(Calendar.MONTH, 1 - rules.getPaymentBackMaxMonth());
                    Date startTime = calendar.getTime();
                    sortKey = sortKey + 1;
                    EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rules, base, workInjuryRatio, sortKey);
                    details.setStartChargeTime(startTime);
                    details.setEndChargeTime(endTime);
                    payBacks.add(details);
                } else {
                    continue;
                }
            } else {
                // 当前产品周期不可以补缴
                continue;
            }
        }
        return payBacks;
    }
}
