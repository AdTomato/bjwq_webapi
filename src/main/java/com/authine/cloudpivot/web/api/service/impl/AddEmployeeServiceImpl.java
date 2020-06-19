package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.exceptions.ServiceException;
import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.CollectionRuleMapper;
import com.authine.cloudpivot.web.api.mapper.DeleteEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.NccpsMapper;
import com.authine.cloudpivot.web.api.service.AddEmployeeService;
import com.authine.cloudpivot.web.api.service.BatchPreDispatchService;
import com.authine.cloudpivot.web.api.service.LaborContractInfoService;
import com.authine.cloudpivot.web.api.service.SalesContractService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description 增员ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.AddEmployeeServiceImpl
 * @Date 2020/6/12 9:56
 **/
@Service
@Slf4j
public class AddEmployeeServiceImpl implements AddEmployeeService {

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Resource
    private CollectionRuleMapper collectionRuleMapper;

    @Resource
    private NccpsMapper nccpsMapper;

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private BatchPreDispatchService batchPreDispatchService;

    @Resource
    private LaborContractInfoService laborContractInfoService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    @Override
    public EmployeeFiles getEmployeeFilesByClientNameAndIdentityNo(String firstLevelClientName,
                                                                   String secondLevelClientName, String identityNo) throws Exception {
        return addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(firstLevelClientName, secondLevelClientName,
                identityNo);
    }

    /**
     * 根据客户名称以及证件号码查询增员客户
     *
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param identityNo       证件号码
     * @return 增员客户数据
     * @author wangyong
     */
    @Override
    public List <AddEmployee> getAddEmployeeByClientNameAndIdCard(String id, String firstClientName,
                                                                  String secondClientName, String identityNo) {
        return addEmployeeMapper.getAddEmployeeByClientNameAndIdCard(id, firstClientName, secondClientName, identityNo);
    }

    @Override
    public AddEmployee getAddEmployeeById(String id) throws Exception {
        AddEmployee addEmployee = addEmployeeMapper.getAddEmployeeById(id);
        if (addEmployee != null) {
            addEmployee = CommonUtils.needBaseRounding(addEmployee);
        }
        return addEmployee;
    }

    @Override
    public void updateAddEmployeeReturnReason(String id, String returnReason, String tableName) throws Exception {
        addEmployeeMapper.updateReturnReason(id, returnReason, tableName);
    }

    @Override
    public ShAddEmployee getShAddEmployeeById(String id) throws Exception {
        return addEmployeeMapper.getShAddEmployeeById(id);
    }

    @Override
    public NationwideDispatch getQgAddEmployeeById(String id) throws Exception {
        return addEmployeeMapper.getQgAddEmployeeById(id);
    }

    @Override
    @Transactional
    public List <EntryNotice> addSubmit(List <AddEmployee> addEmployees, OrganizationFacade organizationFacade,
                                        String userId) throws Exception {
        List<BatchPreDispatch> dispatchList = new ArrayList<>();
        List<LaborContractInfo> infoList = new ArrayList<>();
        List <EntryNotice> entryNotices = new ArrayList <>();
        for (int i = 0; i < addEmployees.size(); i++) {
            AddEmployee addEmployee = addEmployees.get(i);
            log.info("开始提交[" + addEmployee.getEmployeeName() + "]数据");
            boolean sbHave = false, gjjHave = false, sbInAnhui = false, gjjInAnhui = false;
            if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
                sbHave = true;
                if (AreaUtils.isAnhuiCity(addEmployee.getSocialSecurityCity())) {
                    sbInAnhui = true;
                }
            }
            if (addEmployee.getProvidentFundBase() - 0d > 0d) {
                gjjHave = true;
                if (AreaUtils.isAnhuiCity(addEmployee.getProvidentFundCity())) {
                    gjjInAnhui = true;
                }
            }
            // 查询是否存在员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(addEmployee.getFirstLevelClientName(),
                            addEmployee.getSecondLevelClientName(), addEmployee.getIdentityNo());
            if (employeeFiles == null) {
                // 新增
                employeeFiles = new EmployeeFiles(addEmployee);
                log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工档案数据:" + employeeFiles.toString());
                // 新增员工档案
                addEmployeeMapper.addEmployeeFiles(employeeFiles);

                if ("派遣".equals(addEmployee.getEmployeeNature()) || "外包".equals(addEmployee.getEmployeeNature())) {
                    infoList.add(new LaborContractInfo(addEmployee, employeeFiles.getId()));
                }
                // 福利地是省外
                if ((sbHave && !sbInAnhui) || (gjjHave && !gjjInAnhui)) {
                    dispatchList.add(new BatchPreDispatch(addEmployee, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                    if (!sbInAnhui && !gjjInAnhui) {
                        log.info("[" + addEmployee.getEmployeeName() + "]为省外数据");
                        //  社保，公积金没有在省内的情况
                        EntryNotice entryNotice = getEntryNotice(addEmployee);
                        boolean flag = addEntryNotice(entryNotice, addEmployee.getEmployeeName());
                        if (flag) {
                            entryNotices.add(entryNotice);
                        }

                        log.info("开始更新[" + addEmployee.getEmployeeName() + "]增员表单数据");
                        addEmployeeMapper.updateAddEmployee(addEmployee);

                        continue;
                    }
                }
                // 公积金或者社保在省内
                if (sbInAnhui || gjjInAnhui) {
                    log.info("开始处理[" + addEmployee.getEmployeeName() + "]订单数据");
                    EntryNotice entryNotice = createAddEmployeeData(addEmployee, employeeFiles.getId(), sbInAnhui, gjjInAnhui);

                    boolean flag = addEntryNotice(entryNotice, addEmployee.getEmployeeName());
                    if (flag) {
                        entryNotices.add(entryNotice);
                    }
                }
            } else {
                // 存在之前申报的
                if (sbInAnhui || gjjInAnhui) {
                    EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
                    if (orderForm != null) {
                        // 查询征缴规则
                        CollectionRuleMaintain ruleMaintain = null;
                        // 汇缴订单明细排序,补缴订单明细排序
                        Double remittanceSortKey = orderForm.getRemittanceSortKey(), payBackSortKey = orderForm.getPayBackSortKey();
                        if (sbInAnhui) {
                            // 更新订单数据
                            orderForm = employeeOrderFromSetSbInfo(orderForm, "待办", addEmployee);

                            ruleMaintain =
                                    collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getSocialSecurityCity(),
                                            addEmployee.getSWelfareHandler());
                            if (ruleMaintain == null) {
                                throw new RuntimeException("没有查询到社保对应的征缴规则数据");
                            }
                            addSocialSecurityDeclareData(addEmployee, employeeFiles.getId(), orderForm.getId(),
                                    Integer.parseInt(sdf.format(new Date())), ruleMaintain, remittanceSortKey,
                                    payBackSortKey);
                        }
                        if (gjjInAnhui) {
                            // 更新订单数据
                            orderForm = employeeOrderFromSetGjjInfo(orderForm, "待办", addEmployee);

                            ruleMaintain = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getProvidentFundCity(),
                                    addEmployee.getGWelfareHandler());
                            if (ruleMaintain == null) {
                                throw new RuntimeException("没有查询到公积金对应的征缴规则数据");
                            }
                            addProvidentFundDeclareData(addEmployee, employeeFiles.getId(), orderForm.getId(),
                                    Integer.parseInt(sdf.format(new Date())), ruleMaintain, remittanceSortKey,
                                    payBackSortKey);
                        }
                        // 更新订单的服务费数据
                        deleteServiceFeeDetails(orderForm.getId());
                        // 获取服务费
                        createServiceFee(orderForm);
                    } else {
                        log.info("开始处理[" + addEmployee.getEmployeeName() + "]订单数据");
                        createAddEmployeeData(addEmployee, employeeFiles.getId(), sbInAnhui, gjjInAnhui);
                    }
                } else {
                    // 省外数据
                    dispatchList.add(new BatchPreDispatch(addEmployee, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
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
            }
            log.info("开始更新[" + addEmployee.getEmployeeName() + "]员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);

            log.info("开始更新[" + addEmployee.getEmployeeName() + "]增员表单数据");
            addEmployeeMapper.updateAddEmployee(addEmployee);
        }

        if (dispatchList != null && dispatchList.size() > 0) {
            log.info("开始生成批量预派数据：" + dispatchList.toString());
            batchPreDispatchService.addBatchPreDispatchs(userId, organizationFacade, dispatchList);
        }

        if (infoList != null && infoList.size() > 0) {
            log.info("开始生成劳动合同数据：" + infoList.toString());
            laborContractInfoService.saveLaborContractInfos(infoList);
        }

        return entryNotices;
    }

    @Override
    @Transactional
    public void shAddSubmit(List<ShAddEmployee> addEmployees) throws Exception {
        for (int i = 0; i < addEmployees.size(); i++) {
            ShAddEmployee addEmployee = addEmployees.get(i);
            log.info("开始提交[" + addEmployee.getEmployeeName() + "]数据");
            // 判断是否已经存在员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(addEmployee.getFirstLevelClientName(),
                            addEmployee.getSecondLevelClientName(), addEmployee.getIdentityNo());
            if (employeeFiles != null) {
                if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
                    employeeFiles.setSbAddEmployeeId(addEmployee.getId());
                    employeeFiles.setSocialSecurityCity(addEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(addEmployee.getBenefitStartTime());
                    employeeFiles.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
                    employeeFiles.setSWelfareHandler(addEmployee.getWelfareHandler());
                }
                if (addEmployee.getProvidentFundBase() - 0d > 0d) {
                    employeeFiles.setGjjAddEmployeeId(addEmployee.getId());
                    employeeFiles.setProvidentFundCity(addEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(addEmployee.getProvidentFundStartTime());
                    employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundBase());
                    employeeFiles.setGWelfareHandler(addEmployee.getWelfareHandler());
                }

                log.info("开始更新[" + addEmployee.getEmployeeName() + "]员工档案数据");
                addEmployeeMapper.updateEmployeeFiles(employeeFiles);
            } else {
                employeeFiles = new EmployeeFiles(addEmployee);
                log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工档案数据:" + employeeFiles.toString());
                addEmployeeMapper.addEmployeeFiles(employeeFiles);
            }

            log.info("开始更新[" + addEmployee.getEmployeeName() + "]增员表单数据");
            addEmployeeMapper.updateShAddEmployee(addEmployee);
        }
    }

    @Override
    @Transactional
    public void qgAddSubmit(List<NationwideDispatch> addEmployees) throws Exception {
        for (int i = 0; i < addEmployees.size(); i++) {
            NationwideDispatch addEmployee = addEmployees.get(i);
            log.info("开始提交[" + addEmployee.getEmployeeName() + "]数据");
            // 判断是否已经存在员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(addEmployee.getFirstLevelClientName(),
                            addEmployee.getSecondLevelClientName(), addEmployee.getIdentityNo());
            if (employeeFiles != null) {
                if (addEmployee.getSocialInsuranceAmount() - 0d > 0d) {
                    employeeFiles.setSbAddEmployeeId(addEmployee.getId());
                    employeeFiles.setSocialSecurityCity(addEmployee.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(addEmployee.getSServiceFeeStartDate());
                    employeeFiles.setSocialSecurityBase(addEmployee.getSocialInsuranceAmount());
                    employeeFiles.setSWelfareHandler(addEmployee.getWelfareHandler());
                }
                if (addEmployee.getProvidentFundAmount() - 0d > 0d) {
                    employeeFiles.setGjjAddEmployeeId(addEmployee.getId());
                    employeeFiles.setProvidentFundCity(addEmployee.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(addEmployee.getGServiceFeeStartDate());
                    employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundAmount());
                    employeeFiles.setGWelfareHandler(addEmployee.getWelfareHandler());
                }

                log.info("开始更新[" + addEmployee.getEmployeeName() + "]员工档案数据");
                addEmployeeMapper.updateEmployeeFiles(employeeFiles);
            } else {
                employeeFiles = new EmployeeFiles(addEmployee);
                log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工档案数据:" + employeeFiles.toString());
                addEmployeeMapper.addEmployeeFiles(employeeFiles);
            }

            log.info("开始更新[" + addEmployee.getEmployeeName() + "]增员表单数据");
            addEmployeeMapper.updateQgAddEmployee(addEmployee);
        }
    }

    @Override
    @Transactional
    public void addUpdateSubmit(String id, String userId, BizObjectFacade bizObjectFacade,
                                OrganizationFacade organizationFacade) throws Exception {
        log.info("开始提交增员变更数据");
        List<BatchPreDispatch> dispatchList = new ArrayList<>();
        AddEmployee update = addEmployeeMapper.getUpdateAddEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到增员变更数据");
        } else {
            // 处理身份证号码
            update = CommonUtils.processingIdentityNo(update);
            log.info("开始更新增员表单数据");
            addEmployeeMapper.updateAddEmployeeByUpdate(update);
        }
        // 获取原增员表单
        AddEmployee add = getAddEmployeeById(update.getAddEmployeeId());
        if (add == null) {
            throw new RuntimeException("没有查询到增员数据");
        }
        // 获取原员工档案
        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(add.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = employeeFilesSetValue(employeeFiles, add);
            log.info("开始更新员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }
        // 原员工订单实体
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
        // 增员对应社保数据
        SocialSecurityDeclare sDeclare = addEmployeeMapper.getSocialSecurityDeclareByAddEmployeeId(add.getId());
        // 增员对应公积金数据
        ProvidentFundDeclare pDeclare = addEmployeeMapper.getProvidentFundDeclareByAddEmployeeId(add.getId());
        // 删除原来批量撤离数据
        addEmployeeMapper.deleteBatchPreDispatchByAddEmployeeId(add.getId());

        boolean sbHave = false, gjjHave = false, sbInAnhui = false, gjjInAnhui = false;
        if (add.getSocialSecurityBase() - 0d > 0d && add.getId().equals(employeeFiles.getSbAddEmployeeId())) {
            sbHave = true;
            if (AreaUtils.isAnhuiCity(add.getSocialSecurityCity())) {
                sbInAnhui = true;
            }
        }
        if (add.getProvidentFundBase() - 0d > 0d && add.getId().equals(employeeFiles.getGjjAddEmployeeId())) {
            gjjHave = true;
            if (AreaUtils.isAnhuiCity(add.getProvidentFundCity())) {
                gjjInAnhui = true;
            }
        }

        if (sbHave && gjjHave) {
            // 增员有社保，公积金
            if (orderForm != null) {
                bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
            }
            if (sDeclare != null) {
                bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, sDeclare.getId());
            }
            if (pDeclare != null) {
                bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA, pDeclare.getId());
            }
            if ((sbHave && !sbInAnhui) || (sbHave && !gjjInAnhui)) {
                dispatchList.add(new BatchPreDispatch(add, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                log.info("开始生成批量预派数据：" + dispatchList.toString());
                batchPreDispatchService.addBatchPreDispatchs(userId, organizationFacade, dispatchList);
            }
            if (sbInAnhui || gjjInAnhui) {
                log.info("开始处理[" + update.getEmployeeName() + "]订单数据");
                createAddEmployeeData(add, employeeFiles.getId(), sbInAnhui, gjjInAnhui);
            }
        } else if (sbHave) {
            // 增员社保
            if (sDeclare != null) {
                bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, sDeclare.getId());
            }
            if (orderForm != null) {
                deleteSbDetails(orderForm.getId());
                deleteServiceFeeDetails(orderForm.getId());

                // 汇缴订单明细排序,补缴订单明细排序
                Double remittanceSortKey = orderForm.getRemittanceSortKey(), payBackSortKey = orderForm.getPayBackSortKey();
                if (sbInAnhui) {
                    // 更新订单数据
                    orderForm = employeeOrderFromSetSbInfo(orderForm, "代办", add);

                    // 查询征缴规则
                    CollectionRuleMaintain ruleMaintain =
                            collectionRuleMapper.getCollectionRuleMaintain(add.getSocialSecurityCity(),
                                    add.getSWelfareHandler());
                    if (ruleMaintain == null) {
                        throw new RuntimeException("没有查询到社保对应的征缴规则数据");
                    }
                    addSocialSecurityDeclareData(add, employeeFiles.getId(), orderForm.getId(),
                            Integer.parseInt(sdf.format(new Date())), ruleMaintain, remittanceSortKey,
                            payBackSortKey);
                } else if (orderForm.getProvidentFundBase() - 0d > 0d) {
                    // 更新订单数据
                    orderForm = employeeOrderFromSetSbIsNull(orderForm);

                    dispatchList.add(new BatchPreDispatch(add, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                    log.info("开始生成批量预派数据：" + dispatchList.toString());
                    batchPreDispatchService.addBatchPreDispatchs(userId, organizationFacade, dispatchList);
                } else {
                    bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
                    return;
                }
                // 生成服务费数据
                createServiceFee(orderForm);
            } else {
                createAddEmployeeData(add, employeeFiles.getId(), sbInAnhui, gjjInAnhui);
            }
        } else if (gjjHave) {
            // 增员公积金
            if (pDeclare != null) {
                bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA, pDeclare.getId());
            }
            if (orderForm != null) {
                deleteGjjDetails(orderForm.getId());
                deleteServiceFeeDetails(orderForm.getId());

                // 汇缴订单明细排序,补缴订单明细排序
                Double remittanceSortKey = orderForm.getRemittanceSortKey(), payBackSortKey = orderForm.getPayBackSortKey();
                if (gjjInAnhui) {
                    // 更新订单数据
                    orderForm = employeeOrderFromSetGjjInfo(orderForm, "待办", add);

                    CollectionRuleMaintain ruleMaintain = collectionRuleMapper.getCollectionRuleMaintain(add.getProvidentFundCity(),
                            add.getGWelfareHandler());
                    if (ruleMaintain == null) {
                        throw new RuntimeException("没有查询到公积金对应的征缴规则数据");
                    }
                    addProvidentFundDeclareData(add, employeeFiles.getId(), orderForm.getId(),
                            Integer.parseInt(sdf.format(new Date())), ruleMaintain, remittanceSortKey,
                            payBackSortKey);
                } else if (orderForm.getSocialSecurityBase() - 0d > 0d) {
                    // 更新订单数据
                    orderForm = employeeOrderFormSetGjjIsNull(orderForm);

                    dispatchList.add(new BatchPreDispatch(add, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                    log.info("开始生成批量预派数据：" + dispatchList.toString());
                    batchPreDispatchService.addBatchPreDispatchs(userId, organizationFacade, dispatchList);
                } else {
                    bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
                    return;
                }
                // 生成服务费数据
                createServiceFee(orderForm);
            } else {
                createAddEmployeeData(add, employeeFiles.getId(), sbInAnhui, gjjInAnhui);
            }
        }

        log.info("提交增员变更数据结束");
    }

    @Override
    @Transactional
    public void addShUpdateSubmit(String id) throws Exception {
        ShAddEmployee update = addEmployeeMapper.getUpdateShAddEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到增员变更数据");
        } else {
            // 处理身份证号码
            update = CommonUtils.processingIdentityNo(update);
            if ("一致".equals(update.getWhetherConsistent())) {
                update.setProvidentFundStartTime(update.getBenefitStartTime());
            }
        }
        // 获取原增员表单
        ShAddEmployee add = addEmployeeMapper.getShAddEmployeeById(update.getShAddEmployeeId());
        if (add == null) {
            throw new RuntimeException("没有查询到增员数据");
        }
        // 获取原员工档案
        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(add.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = employeeFilesSetValue(employeeFiles, update);
        }

        log.info("开始更新增员表单数据");
        addEmployeeMapper.updateShAddEmployeeByUpdate(update);
        log.info("开始更新员工档案数据");
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
    }

    @Override
    @Transactional
    public void addQgUpdateSubmit(String id) throws Exception {
        NationwideDispatch update = addEmployeeMapper.getUpdateQgAddEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到增员变更数据");
        } else {
            // 处理身份证号码
            update = CommonUtils.processingIdentityNo(update);
        }
        // 获取原增员表单
        NationwideDispatch add = addEmployeeMapper.getQgAddEmployeeById(update.getNationwideDispatchId());
        if (add == null) {
            throw new RuntimeException("没有查询到增员数据");
        }
        // 获取原员工档案
        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(add.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = employeeFilesSetValue(employeeFiles, update);
        }

        log.info("开始更新员工档案数据");
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        log.info("开始更新增员表单数据");
        addEmployeeMapper.updateQgAddEmployeeByUpdate(update);
    }

    @Override
    @Transactional
    public void employeeFilesUpdateSubmit(String id) throws Exception {
        EmployeeFiles update = addEmployeeMapper.getUpdateEmployeeFilesById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到员工档案变更数据");
        }
        EmployeeFiles files = addEmployeeMapper.getEmployeeFilesById(update.getEmployeeFilesId());
        if (files == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        }

        // 隐藏字段赋值
        update.setStopGenerateBill(files.getStopGenerateBill());
        update.setIsOldEmployee(files.getStopGenerateBill());
        update.setSPaymentApplication(files.getSPaymentApplication());
        update.setGPaymentApplication(files.getGPaymentApplication());
        update.setSbAddEmployeeId(files.getSbAddEmployeeId());
        update.setGjjAddEmployeeId(files.getGjjAddEmployeeId());
        update.setSbDelEmployeeId(files.getSbDelEmployeeId());
        update.setGjjDelEmployeeId(files.getGjjDelEmployeeId());

        update.setId(files.getId());

        addEmployeeMapper.updateEmployeeFiles(update);
    }

    @Override
    @Transactional
    public void cancelDispatch(String id, String userId, BizObjectFacade bizObjectFacade) throws Exception {
        AddEmployee addEmployee = addEmployeeMapper.getAddEmployeeById(id);
        if (addEmployee == null) {
            throw new RuntimeException("没有查询到增员数据");
        }

        boolean cancelDispatch = true;
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            if (!"草稿".equals(addEmployee.getSbStatus()) && !"待办".equals(addEmployee.getSbStatus())) {
                cancelDispatch = false;
            }
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            if (!"草稿".equals(addEmployee.getGjjStatus()) && !"待办".equals(addEmployee.getGjjStatus())) {
                cancelDispatch = false;
            }
        }
        if (!cancelDispatch) {
            throw new RuntimeException("不可以取派");
        }
        // 获取原员工档案
        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesByAddEmployeeId(addEmployee.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        }
        // 原员工订单实体
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
        // 增员对应社保数据
        SocialSecurityDeclare sDeclare = addEmployeeMapper.getSocialSecurityDeclareByAddEmployeeId(addEmployee.getId());
        // 增员对应公积金数据
        ProvidentFundDeclare pDeclare = addEmployeeMapper.getProvidentFundDeclareByAddEmployeeId(addEmployee.getId());
        // 删除原来批量撤离数据
        addEmployeeMapper.deleteBatchPreDispatchByAddEmployeeId(addEmployee.getId());

        if (sDeclare != null) {
            bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, sDeclare.getId());
        }
        if (pDeclare != null) {
            bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA, pDeclare.getId());
        }
        boolean sbDel = false;
        boolean gjjDel = false;
        if (addEmployee.getId().equals(employeeFiles.getSbAddEmployeeId()) || StringUtils.isBlank(employeeFiles.getSbAddEmployeeId())) {
            sbDel = true;
        }
        if (addEmployee.getId().equals(employeeFiles.getGjjAddEmployeeId()) || StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId())) {
            gjjDel = true;
        }
        if (sbDel && gjjDel) {
            if (orderForm != null) {
                log.info("开始删除员工订单数据");
                bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
            }
            log.info("开始删除员工档案数据");
            bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_FILES_SCHEMA, employeeFiles.getId());
        } else if (sbDel) {
            if (orderForm.getProvidentFundBase() == 0d) {
                log.info("员工订单没有公积金数据，开始删除员工订单数据");
                bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
            } else {
                orderForm = employeeOrderFormSetGjjIsNull(orderForm);

                deleteSbDetails(orderForm.getId());
                deleteServiceFeeDetails(orderForm.getId());
                createServiceFee(orderForm);
            }
            log.info("开始更新员工档案数据");
            employeeFiles = employeeFileSetSbIsNull(employeeFiles);
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        } else if (gjjDel) {
            if (orderForm.getSocialSecurityBase() == 0d) {
                log.info("员工订单没有社保数据，开始删除员工订单数据");
                bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
            } else {
                orderForm = employeeOrderFromSetSbIsNull(orderForm);

                deleteGjjDetails(orderForm.getId());
                deleteServiceFeeDetails(orderForm.getId());

                createServiceFee(orderForm);
            }

            employeeFiles = employeeFileSetGjjIsNull(employeeFiles);
            log.info("开始更新员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }
        addEmployeeMapper.updateStatusToCancelDispatch(id, "i4fvb_add_employee");
    }

    @Override
    @Transactional
    public void declareSubmit(List<String> ids, String billYear, String schemaCode) throws Exception {
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(schemaCode)) {
            // 社保申报提交
            List<SocialSecurityDeclare> declares = addEmployeeMapper.listSocialSecurityDeclare(ids);
            if (declares != null && declares.size() > 0) {
                for (int i = 0; i < declares.size(); i++) {
                    SocialSecurityDeclare declare = declares.get(i);
                    if ("待办".equals(declare.getStatus())) {
                        socialSecurityDeclareSubmit(declare, billYear);
                    } else {
                        log.info(declare.getEmployeeName() + "：状态是“" + declare.getStatus() + "“,无法提交");
                    }
                }
            }
        } else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(schemaCode)) {
            // 公积金申报提交
            List<ProvidentFundDeclare> declares = addEmployeeMapper.listProvidentFundDeclare(ids);
            if (declares != null && declares.size() > 0) {
                for (int i = 0; i < declares.size(); i++) {
                    ProvidentFundDeclare declare = declares.get(i);
                    if ("待办".equals(declare.getStatus())) {
                        providentFundDeclareSubmit(declare, billYear);
                    } else {
                        log.info(declare.getEmployeeName() + "：状态是“" + declare.getStatus() + "“,无法提交");
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void declareReject(List<String> ids, String returnReason, String schemaCode, String userId,
                              BizObjectFacade bizObjectFacade) throws Exception {
        if (Constants.SOCIAL_SECURITY_DECLARE_SCHEMA.equals(schemaCode)) {
            // 社保申报驳回
            List<SocialSecurityDeclare> declares = addEmployeeMapper.listSocialSecurityDeclare(ids);
            if (declares != null && declares.size() > 0) {
                for (int i = 0; i < declares.size(); i++) {
                    SocialSecurityDeclare declare = declares.get(i);
                    if ("待办".equals(declare.getStatus())) {
                        socialSecurityDeclareReject(declare, returnReason, userId, bizObjectFacade);
                    } else {
                        log.info(declare.getEmployeeName() + "：状态是“" + declare.getStatus() + "“,无法驳回");
                    }
                }
            }
        } else if (Constants.PROVIDENT_FUND_DECLARE_SCHEMA.equals(schemaCode)) {
            // 公积金申报驳回
            List<ProvidentFundDeclare> declares = addEmployeeMapper.listProvidentFundDeclare(ids);
            if (declares != null && declares.size() > 0) {
                for (int i = 0; i < declares.size(); i++) {
                    ProvidentFundDeclare declare = declares.get(i);
                    if ("待办".equals(declare.getStatus())) {
                        providentFundDeclareReject(declare, returnReason, userId, bizObjectFacade);
                    } else {
                        log.info(declare.getEmployeeName() + "：状态是“" + declare.getStatus() + "“,无法驳回");
                    }
                }
            }
        }
    }

    /**
     * 方法说明：公积金申报驳回
     *
     * @param declare         公积金申报数据
     * @param returnReason    退回原因
     * @param userId          当前用户
     * @param bizObjectFacade 业务对象服务类
     * @author liulei
     * @Date 2020/6/16 10:56
     */
    private void providentFundDeclareReject(ProvidentFundDeclare declare, String returnReason, String userId,
                                            BizObjectFacade bizObjectFacade) throws Exception {
        log.info(declare.getEmployeeName() + "：开始驳回公积金申报");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(declare.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        }

        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesById(declare.getEmployeeFilesId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        }

        if (orderForm.getSocialSecurityBase() == 0d) {
            log.info("订单的社保基数为0，没有社保基数，直接删除员工档案");
            bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
        } else {
            // 删除公积金补缴，汇缴数据
            deleteGjjDetails(orderForm.getId());
            // 删除服务费补缴，汇缴数据
            deleteServiceFeeDetails(orderForm.getId());

            orderForm.setProvidentFundStatus("驳回");
            // 生成服务费数据
            createServiceFee(orderForm);
        }

        if (StringUtils.isBlank(employeeFiles.getSbAddEmployeeId())) {
            log.info("员工档案的社保增员表单id为空，当前员工档案只有公积金数据，删除员工订单");
            bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_FILES_SCHEMA, employeeFiles.getId());
        } else {
            log.info("更新员工档案的公积金数据");
            employeeFiles = employeeFileSetGjjIsNull(employeeFiles);
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }

        log.info("删除公积金申报数据");
        bizObjectFacade.removeBizObject(userId, Constants.PROVIDENT_FUND_DECLARE_SCHEMA, declare.getId());

        log.info("更新增员公积金状态，退回原因");
        addEmployeeMapper.updateAddEmployeeStatus(declare.getAddEmployeeId(), "gjj_status", "驳回", returnReason);

        log.info(declare.getEmployeeName() + "：公积金申报驳回成功");
    }

    /**
     * 方法说明：社保申报驳回
     *
     * @param declare         申报申报数据
     * @param returnReason    退回原因
     * @param userId          当前用户
     * @param bizObjectFacade 业务对象服务类
     * @author liulei
     * @Date 2020/6/16 10:56
     */
    private void socialSecurityDeclareReject(SocialSecurityDeclare declare, String returnReason, String userId,
                                             BizObjectFacade bizObjectFacade) throws Exception {
        log.info(declare.getEmployeeName() + "：开始驳回社保申报");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(declare.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        }

        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesById(declare.getEmployeeFilesId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        }

        if (orderForm.getProvidentFundBase() == 0d) {
            log.info("订单的公积金基数为0，没有公积金基数，直接删除员工订单");
            bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_ORDER_FORM_SCHEMA, orderForm.getId());
        } else {
            // 删除社保补缴，汇缴数据
            deleteSbDetails(orderForm.getId());
            // 删除服务费补缴，汇缴数据
            deleteServiceFeeDetails(orderForm.getId());

            orderForm.setSocialSecurityStatus("驳回");
            // 生成服务费数据
            createServiceFee(orderForm);
        }

        if (StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId())) {
            log.info("员工档案的公积金增员表单id为空，当前员工档案只有社保数据，删除员工档案");
            bizObjectFacade.removeBizObject(userId, Constants.EMPLOYEE_FILES_SCHEMA, employeeFiles.getId());
        } else {
            log.info("更新员工档案的社保数据");
            employeeFiles = employeeFileSetSbIsNull(employeeFiles);
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }

        log.info("删除社保申报数据");
        bizObjectFacade.removeBizObject(userId, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, declare.getId());

        log.info("更新增员社保状态，退回原因");
        addEmployeeMapper.updateAddEmployeeStatus(declare.getAddEmployeeId(), "sb_status", "驳回", returnReason);

        log.info(declare.getEmployeeName() + "：社保申报驳回成功");
    }

    /**
     * 方法说明：公积金申报提交
     *
     * @param declare  公积金申报
     * @param billYear 账单年月
     * @author liulei
     * @Date 2020/6/16 10:19
     */
    private void providentFundDeclareSubmit(ProvidentFundDeclare declare, String billYear) throws Exception {
        log.info(declare.getEmployeeName() + "：开始提交公积金申报");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(declare.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        }
        // 删除社保补缴，汇缴数据
        deleteGjjDetails(orderForm.getId());
        // 删除服务费补缴，汇缴数据
        deleteServiceFeeDetails(orderForm.getId());

        log.info("新增社保补缴，汇缴数据");
        addEmployeeMapper.addOrderFormDetailsFromDeclare("i4fvb_order_details_pay_back_gjj", declare.getId(),
                "i4fvb_order_details_pay_back", orderForm.getId());
        addEmployeeMapper.addOrderFormDetailsFromDeclare("i4fvb_order_details_remittance_gjj", declare.getId(),
                "i4fvb_order_details_remittance", orderForm.getId());

        orderForm.setGjjBillYear(billYear);
        orderForm.setProvidentFundStatus("在缴");
        // 生成服务费数据
        createServiceFee(orderForm);

        log.info("新增公积金申报状态");
        addEmployeeMapper.updateDeclareSubmitInfo(declare.getId(), "i4fvb_provident_fund_declare", "在缴", billYear);

        log.info("新增增员公积金状态");
        addEmployeeMapper.updateAddEmployeeStatus(declare.getAddEmployeeId(), "gjj_status", "在缴", null);

        log.info(declare.getEmployeeName() + "：公积金申报提交完成");
    }

    /**
     * 方法说明：社保申报提交
     *
     * @param declare  公积金申报
     * @param billYear 账单年月
     * @author liulei
     * @Date 2020/6/16 10:19
     */
    private void socialSecurityDeclareSubmit(SocialSecurityDeclare declare, String billYear) throws Exception {
        log.info(declare.getEmployeeName() + "：开始提交社保申报");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(declare.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        }
        // 删除社保补缴，汇缴数据
        deleteSbDetails(orderForm.getId());
        // 删除服务费补缴，汇缴数据
        deleteServiceFeeDetails(orderForm.getId());

        log.info("新增社保补缴，汇缴数据");
        addEmployeeMapper.addOrderFormDetailsFromDeclare("i4fvb_order_details_pay_back_sb", declare.getId(),
                "i4fvb_order_details_pay_back", orderForm.getId());
        addEmployeeMapper.addOrderFormDetailsFromDeclare("i4fvb_order_details_remittance_sb", declare.getId(),
                "i4fvb_order_details_remittance", orderForm.getId());

        orderForm.setSbBillYear(billYear);
        orderForm.setSocialSecurityStatus("在缴");
        // 生成服务费数据
        createServiceFee(orderForm);

        log.info("新增社保申报状态");
        addEmployeeMapper.updateDeclareSubmitInfo(declare.getId(), "i4fvb_social_security_declare", "在缴", billYear);

        log.info("新增增员社保状态");
        addEmployeeMapper.updateAddEmployeeStatus(declare.getAddEmployeeId(), "sb_status", "在缴", null);

        log.info(declare.getEmployeeName() + "：社保申报提交完成");
    }

    private EmployeeOrderForm employeeOrderFormSetGjjIsNull(EmployeeOrderForm orderForm) {
        orderForm.setProvidentFundStatus(null);
        orderForm.setProvidentFundCity(null);
        orderForm.setGWelfareHandler(null);
        orderForm.setProvidentFundBase(null);
        orderForm.setProvidentFundChargeStart(null);
        return orderForm;
    }

    private EmployeeOrderForm employeeOrderFromSetSbIsNull(EmployeeOrderForm orderForm) {
        orderForm.setSocialSecurityStatus(null);
        orderForm.setSocialSecurityCity(null);
        orderForm.setSWelfareHandler(null);
        orderForm.setSocialSecurityBase(null);
        orderForm.setSocialSecurityChargeStart(null);
        return orderForm;
    }

    private EmployeeOrderForm employeeOrderFromSetGjjInfo(EmployeeOrderForm orderForm, String status, AddEmployee addEmployee) {
        orderForm.setProvidentFundStatus("待办");
        orderForm.setProvidentFundCity(addEmployee.getProvidentFundCity());
        orderForm.setGWelfareHandler(addEmployee.getGWelfareHandler());
        orderForm.setProvidentFundBase(addEmployee.getProvidentFundBase());
        orderForm.setProvidentFundChargeStart(addEmployee.getProvidentFundStartTime());
        return orderForm;
    }

    private EmployeeOrderForm employeeOrderFromSetSbInfo(EmployeeOrderForm orderForm, String status, AddEmployee addEmployee) {
        orderForm.setSocialSecurityStatus(status);
        orderForm.setSocialSecurityCity(addEmployee.getSocialSecurityCity());
        orderForm.setSWelfareHandler(addEmployee.getSWelfareHandler());
        orderForm.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
        orderForm.setSocialSecurityChargeStart(addEmployee.getSocialSecurityStartTime());

        return orderForm;
    }

    private EmployeeFiles employeeFileSetGjjIsNull(EmployeeFiles employeeFiles) {
        employeeFiles.setGjjAddEmployeeId(null);
        employeeFiles.setProvidentFundBase(null);
        employeeFiles.setGWelfareHandler(null);
        employeeFiles.setProvidentFundCity(null);
        employeeFiles.setProvidentFundChargeStart(null);
        return employeeFiles;
    }

    private EmployeeFiles employeeFileSetSbIsNull(EmployeeFiles employeeFiles) {
        employeeFiles.setSbAddEmployeeId(null);
        employeeFiles.setSocialSecurityBase(null);
        employeeFiles.setSWelfareHandler(null);
        employeeFiles.setSocialSecurityCity(null);
        employeeFiles.setSocialSecurityChargeStart(null);
        return employeeFiles;
    }

    /**
     * 方法说明：生成订单社保数据
     *
     * @param orderFormId 订单id
     * @author liulei
     * @Date 2020/6/15 17:08
     */
    private void deleteSbDetails(String orderFormId) {
        log.info("删除订单的社保补缴，汇缴数据");
        addEmployeeMapper.delOrderFormSbDetails(orderFormId, "i4fvb_order_details_pay_back");
        addEmployeeMapper.delOrderFormSbDetails(orderFormId, "i4fvb_order_details_remittance");
    }

    /**
     * 方法说明：生成订单服务费，风险管理费等数据
     *
     * @param orderFormId 订单id
     * @author liulei
     * @Date 2020/6/15 17:08
     */
    private void deleteServiceFeeDetails(String orderFormId) {
        log.info("删除订单的服务费,风险管理费等补缴，汇缴数据");
        addEmployeeMapper.delOrderFormServiceChargeUnitPrice(orderFormId, "i4fvb_order_details_pay_back");
        addEmployeeMapper.delOrderFormServiceChargeUnitPrice(orderFormId, "i4fvb_order_details_remittance");
    }

    /**
     * 方法说明：生成订单公积金数据
     *
     * @param orderFormId 订单id
     * @author liulei
     * @Date 2020/6/15 17:08
     */
    private void deleteGjjDetails(String orderFormId) {
        log.info("删除订单的公积金补缴，汇缴数据");
        addEmployeeMapper.delOrderFormGjjDetails(orderFormId, "i4fvb_order_details_pay_back");
        addEmployeeMapper.delOrderFormGjjDetails(orderFormId, "i4fvb_order_details_remittance");
    }

    /**
     * 方法说明：生成订单服务费，风险管理费等数据
     *
     * @param orderForm 订单
     * @author liulei
     * @Date 2020/6/15 17:08
     */
    private void createServiceFee(EmployeeOrderForm orderForm) throws Exception {
        // 获取服务费
        ServiceChargeUnitPrice price = getServiceChargeUnitPrice(orderForm);
        if (price != null) {
            orderForm.setPrecollected(price.getPrecollected());
            orderForm.setPayCycle(price.getPayCycle());

            log.info("开始更新[" + orderForm.getEmployeeName() + "]订单数据");
            addEmployeeMapper.updateEmployeeOrderFrom(orderForm);

            price = createEmployeeOrderFormDetails(price, orderForm.getId(), "i4fvb_order_details_pay_back");
            createEmployeeOrderFormDetails(price, orderForm.getId(), "i4fvb_order_details_remittance");
        } else {
            addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
        }
    }

    /**
     * 方法说明：生成员工订单服务费相关数据
     *
     * @param price       服务费
     * @param orderFormId 员工订单id
     * @param tableName   表名
     * @author liulei
     * @Date 2020/6/12 15:50
     */
    private ServiceChargeUnitPrice createEmployeeOrderFormDetails(ServiceChargeUnitPrice price, String orderFormId,
                                                                  String tableName) throws Exception {
        // 固定值数据
        List<EmployeeOrderFormDetails> details = new ArrayList<>();
        // 查询补缴数据开始时间，结束时间，最大sortKey
        List<Integer> startChargeTimes = addEmployeeMapper.getStartChargeTime(tableName, orderFormId);
        if (startChargeTimes == null || startChargeTimes.size() == 0) {
            return price;
        }
        List<Integer> endChargeTimes = addEmployeeMapper.getEndChargeTime(tableName, orderFormId);
        if (endChargeTimes == null || endChargeTimes.size() == 0) {
            return price;
        }
        Double sortKey = addEmployeeMapper.getMaxSortKey(tableName, orderFormId);
        if (sortKey == null) {
            sortKey = 0d;
        }
        Date minStartChargeTime = null;
        Date maxEndChargeTime = null;
        if (startChargeTimes.get(0) != null) {
            sdf.parse(String.valueOf(startChargeTimes.get(0)));
        }
        if (endChargeTimes.get(endChargeTimes.size() - 1) != null) {
            sdf.parse(String.valueOf(endChargeTimes.get(endChargeTimes.size() - 1)));
        }


        if (price.getServiceChargeUnitPrice() - 0d > 0d) {
            // 服务费
            sortKey += 1;
            EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "服务费",
                    minStartChargeTime, maxEndChargeTime, price.getServiceChargeUnitPrice());
            if ("i4fvb_order_details_pay_back".equals(tableName)) {
                price.setServiceMaxPayBack(endChargeTimes.get(endChargeTimes.size() - 1) == null ? null :
                        endChargeTimes.get(endChargeTimes.size() - 1));
            } else if (startChargeTimes.get(0) == null ? null :
                    startChargeTimes.get(0) <= price.getServiceMaxPayBack()) {
                detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getServiceMaxPayBack()))));
            }
            details.add(detail);
        }
        if (price.getTotalWelfareProducts() - 0d > 0d) {
            // 福利产品
            sortKey += 1;
            EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "福利产品",
                    minStartChargeTime, maxEndChargeTime, price.getTotalWelfareProducts());
            if ("i4fvb_order_details_pay_back".equals(tableName)) {
                price.setWelfareMaxPayBack(endChargeTimes.get(endChargeTimes.size() - 1) == null ? null :
                        endChargeTimes.get(endChargeTimes.size() - 1));
            } else if (startChargeTimes.get(0) == null ? null :
                    startChargeTimes.get(0) <= price.getWelfareMaxPayBack()) {
                detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getWelfareMaxPayBack()))));
            }
            details.add(detail);
        }
        Double riskManagementFee = price.getRiskManagementFee();
        if (riskManagementFee - 0d > 0d && riskManagementFee.intValue() < 1) {
            // 风险管理费有值 && 风险管理费<1
            List<StartAndEndTime> timeRanges = getTimeRanges(startChargeTimes, endChargeTimes);
            if (timeRanges != null && timeRanges.size() > 0) {
                for (int i = 0; i < timeRanges.size(); i++) {
                    StartAndEndTime timeRange = timeRanges.get(i);
                    String start = String.valueOf(timeRange.getStartChargeTime());
                    String end = null;
                    if (timeRange.getEndChargeTime() != Integer.MAX_VALUE && timeRange.getEndChargeTime() != null) {
                        end = String.valueOf(timeRange.getEndChargeTime());
                    }
                    Double total = addEmployeeMapper.getSumInTimeRange(tableName, start, end, orderFormId);
                    if (total == null || total == 0d) {
                        continue;
                    }
                    sortKey += 1;
                    Date thisStart = sdf.parse(start), thisEnd = null;
                    if (end != null) {
                        thisEnd = sdf.parse(end);
                    }

                    EmployeeOrderFormDetails riskManagementFeeDetail = new EmployeeOrderFormDetails(orderFormId,
                            sortKey, "风险管理费", thisStart, thisEnd, riskManagementFee * total);
                    details.add(riskManagementFeeDetail);

                    Double vatTaxes =
                            price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee * total);
                    if (vatTaxes - 0d > 0d) {
                        sortKey += 1;
                        EmployeeOrderFormDetails vatTaxesDetails = new EmployeeOrderFormDetails(orderFormId, sortKey,
                                "增值税费", thisStart, thisEnd, vatTaxes);
                        details.add(vatTaxesDetails);
                    }
                }
            }
        } else {
            if (riskManagementFee - 0d > 0d) {
                // 风险管理费有固定值
                sortKey += 1;
                EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "风险管理费",
                        minStartChargeTime, maxEndChargeTime, riskManagementFee);
                if ("i4fvb_order_details_pay_back".equals(tableName)) {
                    price.setRiskMaxPayBack(endChargeTimes.get(endChargeTimes.size() - 1) == null ? null :
                            endChargeTimes.get(endChargeTimes.size() - 1));
                } else if (startChargeTimes.get(0) == null ? null :
                        startChargeTimes.get(0) <= price.getRiskMaxPayBack()) {
                    detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getRiskMaxPayBack()))));
                }
                details.add(detail);
            }
            Double vatTaxes = price.getVatTaxes() * (price.getServiceChargeUnitPrice() + riskManagementFee);
            if (vatTaxes - 0d > 0d) {
                sortKey += 1;
                EmployeeOrderFormDetails detail = new EmployeeOrderFormDetails(orderFormId, sortKey, "增值税费",
                        minStartChargeTime, maxEndChargeTime, vatTaxes);
                if ("i4fvb_order_details_pay_back".equals(tableName)) {
                    price.setVatMaxPayBack(endChargeTimes.get(endChargeTimes.size() - 1) == null ? null :
                            endChargeTimes.get(endChargeTimes.size() - 1));
                } else if (startChargeTimes.get(0) == null ? null :
                        startChargeTimes.get(0) <= price.getVatMaxPayBack()) {
                    detail.setStartChargeTime(sdf.parse(String.valueOf(CommonUtils.getNextMonth(price.getVatMaxPayBack()))));
                }

                details.add(detail);
            }
        }
        if (details != null && details.size() > 0) {
            addEmployeeMapper.addEmployeeOrderFormFee(details, tableName);
        }

        return price;
    }

    private EmployeeFiles employeeFilesSetValue(EmployeeFiles employeeFiles, AddEmployee addEmployee) {
        employeeFiles.setEmployeeName(addEmployee.getEmployeeName());
        employeeFiles.setIdType(addEmployee.getIdentityNoType());
        employeeFiles.setIdNo(addEmployee.getIdentityNo());
        employeeFiles.setGender(addEmployee.getGender());
        employeeFiles.setBirthDate(addEmployee.getBirthday());
        employeeFiles.setEmployeeNature(addEmployee.getEmployeeNature());
        employeeFiles.setHouseholdRegisterNature(addEmployee.getFamilyRegisterNature());
        employeeFiles.setMobile(addEmployee.getMobile());
        employeeFiles.setEntryTime(addEmployee.getEntryTime());
        employeeFiles.setEntryDescription(addEmployee.getRemark());
        employeeFiles.setEmail(addEmployee.getEmail());
        employeeFiles.setFirstLevelClientName(addEmployee.getFirstLevelClientName());
        employeeFiles.setSecondLevelClientName(addEmployee.getSecondLevelClientName());
        employeeFiles.setHouseholdRegisterRemarks(addEmployee.getHouseholdRegisterRemarks());
        employeeFiles.setIsRetiredSoldier(addEmployee.getIsRetiredSoldier());
        employeeFiles.setIsPoorArchivists(addEmployee.getIsPoorArchivists());
        employeeFiles.setIsDisabled(addEmployee.getIsDisabled());
        employeeFiles.setOperator(addEmployee.getOperator());
        employeeFiles.setInquirer(addEmployee.getInquirer());
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            employeeFiles.setSbAddEmployeeId(addEmployee.getAddEmployeeId());
            employeeFiles.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
            employeeFiles.setSWelfareHandler(addEmployee.getSWelfareHandler());
            employeeFiles.setSocialSecurityCity(addEmployee.getSocialSecurityCity());
            employeeFiles.setSocialSecurityChargeStart(addEmployee.getSocialSecurityStartTime());
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            employeeFiles.setGjjAddEmployeeId(addEmployee.getAddEmployeeId());
            employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundBase());
            employeeFiles.setGWelfareHandler(addEmployee.getGWelfareHandler());
            employeeFiles.setProvidentFundCity(addEmployee.getProvidentFundCity());
            employeeFiles.setProvidentFundChargeStart(addEmployee.getProvidentFundStartTime());
        }
        employeeFiles.setSubordinateDepartment(addEmployee.getSubordinateDepartment());
        return employeeFiles;
    }

    private EmployeeFiles employeeFilesSetValue(EmployeeFiles employeeFiles, ShAddEmployee addEmployee) {
        employeeFiles.setEmployeeName(addEmployee.getEmployeeName());
        employeeFiles.setIdType(addEmployee.getIdentityNoType());
        employeeFiles.setIdNo(addEmployee.getIdentityNo());
        employeeFiles.setGender(addEmployee.getGender());
        employeeFiles.setBirthDate(addEmployee.getBirthday());
        employeeFiles.setEmployeeNature("代理");
        employeeFiles.setMobile(addEmployee.getMobile());
        employeeFiles.setEntryTime(addEmployee.getEntryTime());
        employeeFiles.setEntryDescription(addEmployee.getInductionRemark());
        employeeFiles.setEmail(addEmployee.getMail());
        employeeFiles.setFirstLevelClientName(addEmployee.getFirstLevelClientName());
        employeeFiles.setSecondLevelClientName(addEmployee.getSecondLevelClientName());
        employeeFiles.setHouseholdRegisterRemarks(addEmployee.getHouseholdRegisterRemarks());
        employeeFiles.setIsRetiredSoldier(addEmployee.getIsRetiredSoldier());
        employeeFiles.setIsPoorArchivists(addEmployee.getIsPoorArchivists());
        employeeFiles.setIsDisabled(addEmployee.getIsDisabled());
        employeeFiles.setOperator(addEmployee.getOperator());
        employeeFiles.setInquirer(addEmployee.getInquirer());
        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
            employeeFiles.setSbAddEmployeeId(addEmployee.getId());
            employeeFiles.setSocialSecurityCity(addEmployee.getCityName());
            employeeFiles.setSocialSecurityChargeStart(addEmployee.getBenefitStartTime());
            employeeFiles.setSocialSecurityBase(addEmployee.getSocialSecurityBase());
            employeeFiles.setSWelfareHandler(addEmployee.getWelfareHandler());
        }
        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
            employeeFiles.setGjjAddEmployeeId(addEmployee.getId());
            employeeFiles.setProvidentFundCity(addEmployee.getCityName());
            employeeFiles.setProvidentFundChargeStart(addEmployee.getProvidentFundStartTime());
            employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundBase());
            employeeFiles.setGWelfareHandler(addEmployee.getWelfareHandler());
        }
        employeeFiles.setSubordinateDepartment(addEmployee.getSubordinateDepartment());
        return employeeFiles;
    }

    private EmployeeFiles employeeFilesSetValue(EmployeeFiles employeeFiles, NationwideDispatch addEmployee) {
        employeeFiles.setEmployeeName(addEmployee.getEmployeeName());
        employeeFiles.setIdType(addEmployee.getIdentityNoType());
        employeeFiles.setIdNo(addEmployee.getIdentityNo());
        employeeFiles.setGender(addEmployee.getGender());
        employeeFiles.setBirthDate(addEmployee.getBirthday());
        employeeFiles.setEmployeeNature("代理");
        employeeFiles.setMobile(addEmployee.getContactNumber());
        employeeFiles.setEntryTime(addEmployee.getEntryDate());
        employeeFiles.setEntryDescription(addEmployee.getRemark());
        employeeFiles.setEmail(addEmployee.getEmployeeEmail());
        employeeFiles.setFirstLevelClientName(addEmployee.getFirstLevelClientName());
        employeeFiles.setSecondLevelClientName(addEmployee.getSecondLevelClientName());
        employeeFiles.setHouseholdRegisterRemarks(addEmployee.getHouseholdRegisterRemarks());
        employeeFiles.setIsRetiredSoldier(addEmployee.getIsRetiredSoldier());
        employeeFiles.setIsPoorArchivists(addEmployee.getIsPoorArchivists());
        employeeFiles.setIsDisabled(addEmployee.getIsDisabled());
        employeeFiles.setOperator(addEmployee.getOperator());
        employeeFiles.setInquirer(addEmployee.getInquirer());
        if (addEmployee.getSocialInsuranceAmount() - 0d > 0d) {
            employeeFiles.setSbAddEmployeeId(addEmployee.getId());
            employeeFiles.setSocialSecurityCity(addEmployee.getInvolved());
            employeeFiles.setSocialSecurityChargeStart(addEmployee.getSServiceFeeStartDate());
            employeeFiles.setSocialSecurityBase(addEmployee.getSocialInsuranceAmount());
            employeeFiles.setSWelfareHandler(addEmployee.getWelfareHandler());
        }
        if (addEmployee.getProvidentFundAmount() - 0d > 0d) {
            employeeFiles.setGjjAddEmployeeId(addEmployee.getId());
            employeeFiles.setProvidentFundCity(addEmployee.getInvolved());
            employeeFiles.setProvidentFundChargeStart(addEmployee.getGServiceFeeStartDate());
            employeeFiles.setProvidentFundBase(addEmployee.getProvidentFundAmount());
            employeeFiles.setGWelfareHandler(addEmployee.getWelfareHandler());
        }
        employeeFiles.setSubordinateDepartment(addEmployee.getSubordinateDepartment());
        return employeeFiles;
    }

    /**
     * 方法说明：省内增员数据开始处理
     *
     * @param addEmployee     增员
     * @param employeeFilesId 员工档案id
     * @param sbInAnhui       社保在省内
     * @param gjjInAnhui      公积金在省内
     * @return void
     * @author liulei
     * @Date 2020/6/12 14:12
     */
    private EntryNotice createAddEmployeeData(AddEmployee addEmployee, String employeeFilesId, boolean sbInAnhui,
                                              boolean gjjInAnhui) throws Exception {
        int curMonth = Integer.parseInt(sdf.format(new Date()));
        // 员工订单
        EmployeeOrderForm orderForm = new EmployeeOrderForm(addEmployee, employeeFilesId);
        // 获取服务费
        ServiceChargeUnitPrice price = getServiceChargeUnitPrice(orderForm);
        if (price != null) {
            orderForm.setPrecollected(price.getPrecollected());
            orderForm.setPayCycle(price.getPayCycle());
        }

        log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工订单主表数据：" + orderForm.toString());
        addEmployeeMapper.addEmployeeOrderForm(orderForm);

        // 入职通知
        EntryNotice entryNotice = new EntryNotice(addEmployee);
        // 查询征缴规则
        CollectionRuleMaintain ruleMaintain = null;
        // 汇缴订单明细排序,补缴订单明细排序
        Double hjSortKey = 0d, bjSortKey = 0d;
        if (sbInAnhui) {
            ruleMaintain = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getSocialSecurityCity(),
                    addEmployee.getSWelfareHandler());
            if (ruleMaintain == null) {
                throw new RuntimeException("[" + addEmployee.getEmployeeName() + "]没有查询到社保对应的征缴规则数据");
            }
            SocialSecurityDeclare declare = addSocialSecurityDeclareData(addEmployee, employeeFilesId,
                    orderForm.getId(), curMonth, ruleMaintain, hjSortKey, bjSortKey);

            entryNotice.setSocialSecurity(ruleMaintain.getSocialSecurityDeclarationMaterials());
            entryNotice.setProvidentFund(ruleMaintain.getProvidentFundDeclarationMaterials());
            entryNotice.setRecordOfEmployment(ruleMaintain.getEmploymentFilingMaterials());
            entryNotice.setOperateSignatory(declare.getOperateLeader());

        }
        if (gjjInAnhui) {
            if (!sbInAnhui || !addEmployee.getProvidentFundCity().equals(addEmployee.getProvidentFundCity()) ||
                    !addEmployee.getGWelfareHandler().equals(addEmployee.getSWelfareHandler())) {
                ruleMaintain = collectionRuleMapper.getCollectionRuleMaintain(addEmployee.getProvidentFundCity(),
                        addEmployee.getGWelfareHandler());
                if (ruleMaintain == null) {
                    throw new RuntimeException("[" + addEmployee.getEmployeeName() + "]没有查询到社保对应的征缴规则数据");
                }
            }
            ProvidentFundDeclare declare = addProvidentFundDeclareData(addEmployee, employeeFilesId,
                    orderForm.getId(), curMonth, ruleMaintain, hjSortKey, bjSortKey);
            if (!sbInAnhui) {
                entryNotice.setSocialSecurity(ruleMaintain.getSocialSecurityDeclarationMaterials());
                entryNotice.setProvidentFund(ruleMaintain.getProvidentFundDeclarationMaterials());
                entryNotice.setRecordOfEmployment(ruleMaintain.getEmploymentFilingMaterials());
                entryNotice.setOperateSignatory(declare.getOperateLeader());
            }

        }
        // 生成服务费相关数据
        if (price != null) {
            price = createEmployeeOrderFormDetails(price, orderForm.getId(), "i4fvb_order_details_pay_back");
            createEmployeeOrderFormDetails(price, orderForm.getId(), "i4fvb_order_details_remittance");
        }

        return entryNotice;
    }

    /**
     * 方法说明：获取时间区间
     *
     * @param startChargeTimes 开始时间
     * @param endChargeTimes   结束时间
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.StartAndEndTime>
     * @author liulei
     * @Date 2020/6/12 15:59
     */
    private List<StartAndEndTime> getTimeRanges(List<Integer> startChargeTimes, List<Integer> endChargeTimes) throws ServiceException {
        List<StartAndEndTime> times = new ArrayList<>();
        if (startChargeTimes.size() == 1 && endChargeTimes.size() == 1) {
            StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(0), endChargeTimes.get(0) == null ?
                    Integer.MAX_VALUE : endChargeTimes.get(0), null);
            times.add(time);
        } else if (startChargeTimes.size() == 1 && endChargeTimes.size() > 1) {
            for (int i = 0; i < endChargeTimes.size(); i++) {
                if (i == 0) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(0), endChargeTimes.get(0), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(CommonUtils.getNextMonth(endChargeTimes.get(i - 1)),
                            endChargeTimes.get(i) == null ? Integer.MAX_VALUE : endChargeTimes.get(i), null);
                    times.add(time);
                }
            }
        } else if (startChargeTimes.size() > 0 && endChargeTimes.size() == 1) {
            for (int i = 0; i < startChargeTimes.size(); i++) {
                if (i < startChargeTimes.size() - 1) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i), CommonUtils.getLastMonth(startChargeTimes.get(i + 1)), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i),
                            endChargeTimes.get(0) == null ? Integer.MAX_VALUE : endChargeTimes.get(0), null);
                    times.add(time);
                }
            }
        } else {
            for (int i = 0; i < startChargeTimes.size(); i++) {
                if (i < startChargeTimes.size() - 1) {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i), CommonUtils.getLastMonth(startChargeTimes.get(i + 1)), null);
                    times.add(time);
                } else {
                    StartAndEndTime time = new StartAndEndTime(startChargeTimes.get(i),
                            endChargeTimes.get(endChargeTimes.size() - 1) == null ? Integer.MAX_VALUE :
                                    endChargeTimes.get(endChargeTimes.size() - 1), null);
                    times.add(time);
                }
            }
            for (int i = 0; i < endChargeTimes.size() - 1; i++) {
                Integer endChargeTime = endChargeTimes.get(i);
                for (int j = 0; j < times.size(); j++) {
                    StartAndEndTime time = times.get(j);
                    if (endChargeTime > time.getEndChargeTime()) {
                        continue;
                    } else if (endChargeTime == time.getStartChargeTime() && endChargeTime == time.getEndChargeTime()) {
                        break;
                    } else if (endChargeTime >= time.getStartChargeTime() && endChargeTime <= time.getEndChargeTime()) {
                        StartAndEndTime nextTime = new StartAndEndTime(CommonUtils.getNextMonth(endChargeTime), time.getEndChargeTime(), null);
                        times.get(j).setEndChargeTime(endChargeTime);
                        times.add(j + 1, nextTime);

                    }
                }
            }
        }
        return times;
    }


    /**
     * 方法说明：获取服务费相关数据
     *
     * @param orderForm 员工订单
     * @return com.authine.cloudpivot.web.api.entity.ServiceChargeUnitPrice
     * @author liulei
     * @Date 2020/6/12 15:46
     */
    private ServiceChargeUnitPrice getServiceChargeUnitPrice(EmployeeOrderForm orderForm) throws ServiceException {
        String city = orderForm.getSocialSecurityCity();
        if (StringUtils.isBlank(city)) {
            city = orderForm.getProvidentFundCity();
        }
        return salesContractService.getServiceChargeUnitPrice(orderForm.getFirstLevelClientName(),
                orderForm.getBusinessType(), !AreaUtils.isAnhuiCity(city) ? "省外" : "省内", city);
    }

    /**
     * 方法说明：生成入职通知
     *
     * @param entryNotice 入职通知
     * @param wf          流程实例接口
     * @author liulei
     * @Date 2020/6/12 15:40
     */
    private boolean addEntryNotice(EntryNotice entryNotice, String employeeName) throws ServiceException {
        // 是否入职通知
        boolean haveEntryNotice = checkHaveEntryNotice(entryNotice);
        if (haveEntryNotice) {
            log.info("开始新增[" + employeeName + "]入职通知数据" + entryNotice.toString());
            addEmployeeMapper.addEntryNotice(entryNotice);
            /*wf.startWorkflowInstance(entryNotice.getCreatedDeptId(), entryNotice.getCreater(),
                    Constants.ENTRY_NOTICE_SCHEMA_WF, entryNotice.getId(), true);*/
            return true;
        } else {
            log.info("[" + employeeName + "]不需要入职通知" + entryNotice.toString());
            return false;
        }
    }

    /**
     * 方法说明：获取公积金申报
     *
     * @param addEmployee     增员
     * @param employeeFilesId 员工档案id
     * @param orderFormId     员工订单id
     * @param curMonth        当前月
     * @param ruleMaintain    征缴规则
     * @param hjSortKey       汇缴sortKey
     * @param bjSortKey       补缴sortKey
     * @return com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare
     * @author liulei
     * @Date 2020/6/12 15:20
     */
    private ProvidentFundDeclare addProvidentFundDeclareData(AddEmployee addEmployee, String employeeFilesId,
                                                             String orderFormId, int curMonth,
                                                             CollectionRuleMaintain ruleMaintain, Double hjSortKey,
                                                             Double bjSortKey) throws Exception {
        // 判断当前月是否为补缴月
        Map<String, String> curMonthCanPayBack = new HashMap<>();
        ProvidentFundDeclare declare = new ProvidentFundDeclare(addEmployee, employeeFilesId, orderFormId);
        String handler = addEmployeeMapper.getHsLevyHandler(declare.getCity(), declare.getWelfareHandler(),
                "公积金", declare.getSecondLevelClientName());
        declare.setOperateLeader(handler);

        log.info("开始新增[" + addEmployee.getEmployeeName() + "]公积金申报主表数据：" + declare.toString());
        addEmployeeMapper.addProvidentFundDeclare(declare);

        // 查询补缴开始申报月
        int sbMonth = Integer.parseInt(sdf.format(addEmployee.getProvidentFundStartTime()));
        if (sbMonth <= curMonth) {
            // 查询补缴规则数据,按产品名称，开始时间倒序排列
            List<PaymentRules> paymentRules = collectionRuleMapper.getGjjPaymentRules(ruleMaintain.getId(),
                    sdf.format(addEmployee.getProvidentFundStartTime()), addEmployee.getProvidentFundName(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl());
            // 创建补缴订单数据
            List<EmployeeOrderFormDetails> gPayBack = createPayBackData(paymentRules, curMonthCanPayBack,
                    sbMonth, curMonth, addEmployee.getProvidentFundBase(), null, bjSortKey);
            if (gPayBack != null && gPayBack.size() > 0) {
                log.info("开始新增[" + addEmployee.getEmployeeName() + "]公积金申报补缴数据");
                addEmployeeMapper.createEmployeeOrderFormDetails(gPayBack, declare.getId(),
                        "i4fvb_order_details_pay_back_gjj");

                log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工订单补缴数据");
                addEmployeeMapper.createEmployeeOrderFormDetails(gPayBack, orderFormId,
                        "i4fvb_order_details_pay_back");
            }
        }
        // 汇缴订单明细
        List <CollectionRule> collectionRule =
                collectionRuleMapper.getGjjCollectionRules(ruleMaintain.getId(), addEmployee.getProvidentFundName(),
                        addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl());
        // 汇缴开始时间初始化为开始申报时间和当前时间最大的一个
        List <EmployeeOrderFormDetails> gRemittance = createRemittanceData(collectionRule, curMonthCanPayBack,
                sbMonth, curMonth , addEmployee.getProvidentFundBase(), null, hjSortKey, declare);
        if (gRemittance != null && gRemittance.size() > 0) {
            log.info("开始新增[" + addEmployee.getEmployeeName() + "]公积金申报汇缴数据");
            addEmployeeMapper.createEmployeeOrderFormDetails(gRemittance, declare.getId(),
                    "i4fvb_order_details_remittance_gjj");

            log.info("开始新增[" + addEmployee.getEmployeeName() + "]员工订单汇缴数据");
            addEmployeeMapper.createEmployeeOrderFormDetails(gRemittance, orderFormId,
                    "i4fvb_order_details_remittance");
        }

        return declare;
    }

    /**
     * 方法说明：获取社保申报
     *
     * @param addEmployee     增员
     * @param employeeFilesId 员工档案id
     * @param orderFormId     员工订单id
     * @param curMonth        当前月
     * @param ruleMaintain    征缴规则
     * @param hjSortKey       汇缴sortKey
     * @param bjSortKey       补缴sortKey
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
     * @author liulei
     * @Date 2020/6/12 15:20
     */
    private SocialSecurityDeclare addSocialSecurityDeclareData(AddEmployee addEmployee, String employeeFilesId,
                                                               String orderFormId, int curMonth,
                                                               CollectionRuleMaintain ruleMaintain, Double hjSortKey,
                                                               Double bjSortKey) throws Exception {
        // 判断当前月是否为补缴月
        Map<String, String> curMonthCanPayBack = new HashMap<>();
        SocialSecurityDeclare declare = new SocialSecurityDeclare(addEmployee, employeeFilesId, orderFormId);
        String handler = addEmployeeMapper.getHsLevyHandler(declare.getCity(), declare.getWelfareHandler(),
                "社保", declare.getSecondLevelClientName());
        declare.setOperateLeader(handler);

        log.info("开始新增[" + declare.getEmployeeName() + "]社保申报主表数据：" + declare.toString());
        addEmployeeMapper.addSocialSecurityDeclare(declare);

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
            List<PaymentRules> paymentRules = collectionRuleMapper.getSbPaymentRules(ruleMaintain.getId(),
                    sdf.format(addEmployee.getSocialSecurityStartTime()));
            // 创建补缴订单数据
            List<EmployeeOrderFormDetails> sPayBack = createPayBackData(paymentRules, curMonthCanPayBack,
                    sbMonth, curMonth, addEmployee.getSocialSecurityBase(), workInjuryRatio, bjSortKey);
            if (sPayBack != null && sPayBack.size() > 0) {
                log.info("开始新增[" + declare.getEmployeeName() + "]社保申报补缴数据");
                addEmployeeMapper.createEmployeeOrderFormDetails(sPayBack, declare.getId(),
                        "i4fvb_order_details_pay_back_sb");

                log.info("开始新增[" + declare.getEmployeeName() + "]员工订单补缴数据");
                addEmployeeMapper.createEmployeeOrderFormDetails(sPayBack, orderFormId,
                        "i4fvb_order_details_pay_back");
            }
        }
        // 汇缴订单明细
        List<CollectionRule> collectionRules =
                collectionRuleMapper.getSbCollectionRules(ruleMaintain.getId());
        // 汇缴开始时间初始化为开始申报时间和当前时间最大的一个
        List<EmployeeOrderFormDetails> sRemittance = createRemittanceData(collectionRules, curMonthCanPayBack,
                sbMonth, curMonth, addEmployee.getSocialSecurityBase(), workInjuryRatio, hjSortKey, null);
        if (sRemittance != null && sRemittance.size() > 0) {
            log.info("开始新增[" + declare.getEmployeeName() + "]社保申报汇缴数据");
            addEmployeeMapper.createEmployeeOrderFormDetails(sRemittance, declare.getId(),
                    "i4fvb_order_details_remittance_sb");

            log.info("开始新增[" + declare.getEmployeeName() + "]员工订单汇缴数据");
            addEmployeeMapper.createEmployeeOrderFormDetails(sRemittance, orderFormId,
                    "i4fvb_order_details_remittance");
        }

        return declare;
    }

    /**
     * 方法说明：获取汇缴数据
     *
     * @param collectionRules    征缴规则汇缴数据
     * @param curMonthCanPayBack 当前月是否为补缴月
     * @param sbMonth            补缴开始时间
     * @param curMonth           当前时间
     * @param base               基数
     * @param workInjuryRatio    个性化工伤
     * @param hjSortKey          补缴sortKey
     * @param declare            公积金申报实体
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.EmployeeOrderFormDetails>
     * @author liulei
     * @Date 2020/6/12 15:19
     */
    private List<EmployeeOrderFormDetails> createRemittanceData(List<CollectionRule> collectionRules, Map<String,
            String> curMonthCanPayBack, int sbMonth, int curMonth, Double base, NccpsWorkInjuryRatio workInjuryRatio,
                                                                Double hjSortKey, ProvidentFundDeclare declare) throws Exception {
        List<EmployeeOrderFormDetails> remittances = new ArrayList<>();
        if (collectionRules == null || collectionRules.size() == 0) {
            return remittances;
        }

        for (int i = 0; i < collectionRules.size(); i++) {
            CollectionRule rule = collectionRules.get(i);
            // 申报的开始时间如果大于当前时间，汇缴的开始时间为申报时间，
            int curSb = 0;
            if (StringUtils.isBlank(curMonthCanPayBack.get(rule.getProductName())) ||
                    "是".equals(curMonthCanPayBack.get(rule.getProductName()))) {
                // 申报当月是补缴月，判断客户设置的开始缴纳时间与当前月+1比较大小
                curSb = sbMonth > CommonUtils.getNextMonth(curMonth) ? sbMonth : CommonUtils.getNextMonth(curMonth);
            } else {
                curSb = sbMonth > curMonth ? sbMonth : curMonth;
            }
            if (rule.getEffectiveTerminationMonth() != null && Integer.parseInt(sdf.format(rule.getEffectiveTerminationMonth())) < curSb) {
                continue;
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
            hjSortKey = hjSortKey + 1;
            EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rule, base, workInjuryRatio, hjSortKey);
            details.setStartChargeTime(sdf.parse(String.valueOf(curDateStart)));
            if (endMonth != Integer.MAX_VALUE) {
                details.setEndChargeTime(sdf.parse(String.valueOf(endMonth)));
            }
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

    /**
     * 方法说明：创建补缴明细数据
     *
     * @param paymentRules       征缴规则补缴明细
     * @param curMonthCanPayBack 当前月是否为补缴月
     * @param sbMonth            补缴开始月
     * @param curMonth           当前月
     * @param base               基数
     * @param workInjuryRatio    个性化工伤
     * @param bjSortKey          补缴sortKey
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.EmployeeOrderFormDetails>
     * @author liulei
     * @Date 2020/6/12 14:31
     */
    private List<EmployeeOrderFormDetails> createPayBackData(List<PaymentRules> paymentRules,
                                                             Map<String, String> curMonthCanPayBack, int sbMonth,
                                                             int curMonth, Double base,
                                                             NccpsWorkInjuryRatio workInjuryRatio, Double bjSortKey) throws Exception {
        List<EmployeeOrderFormDetails> payBacks = new ArrayList<>();
        if (paymentRules == null || paymentRules.size() == 0) {
            return payBacks;
        }
        String productName = "";
        // 待补缴结束时间
        int payEnd = curMonth;
        boolean isContinuity = true;
        for (int i = 0; i < paymentRules.size(); i++) {
            PaymentRules rules = paymentRules.get(i);
            if (!rules.getProductName().equals(productName)) {
                // 险种第一次出现, 此时通过判断申报当月是否为补缴月来确定当前险种的补缴最末一个月
                productName = rules.getProductName();
                // 记录当前月是否为补缴月
                curMonthCanPayBack.put(productName, rules.getCurMonthCanPayBack());
                payEnd = "是".equals(rules.getCurMonthCanPayBack()) ? curMonth : CommonUtils.getLastMonth(curMonth);
                if (payEnd < sbMonth) {
                    // 如果最末一个月 < 申报月，则不补缴(只补缴申报月的情况，此时申报月是汇缴月)
                    continue;
                }
                isContinuity = true;
            }
            if (!isContinuity) {
                // 补缴不连续
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
                int payMonth = (curDateEnd / 100 - curDateStart / 100) * 12 + (curDateEnd % 100 - curDateStart % 100) + 1;
                if (payMonth >= rules.getPaymentBackMinMonth() && payMonth < rules.getPaymentBackMaxMonth()) {
                    bjSortKey = bjSortKey + 1;
                    EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rules, base, workInjuryRatio, bjSortKey);
                    details.setStartChargeTime(sdf.parse(String.valueOf(curDateStart)));
                    details.setEndChargeTime(sdf.parse(String.valueOf(curDateEnd)));

                    payBacks.add(details);
                    if (curDateStart != startMonth) {
                        isContinuity = false;
                    }
                } else if (payMonth < rules.getPaymentBackMinMonth()) {
                    isContinuity = false;
                    continue;
                } else if (payMonth >= rules.getPaymentBackMaxMonth()) {
                    Date endTime = sdf.parse(String.valueOf(curDateEnd));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(endTime);
                    calendar.add(Calendar.MONTH, 1 - rules.getPaymentBackMaxMonth());
                    Date startTime = calendar.getTime();
                    bjSortKey = bjSortKey + 1;
                    EmployeeOrderFormDetails details = new EmployeeOrderFormDetails(rules, base, workInjuryRatio, bjSortKey);
                    details.setStartChargeTime(startTime);
                    details.setEndChargeTime(endTime);
                    payBacks.add(details);
                    if (Integer.parseInt(sdf.format(startTime)) != startMonth) {
                        isContinuity = false;
                    }
                } else {
                    continue;
                }

            } else {
                // 当前产品周期不可以补缴
                isContinuity = false;
                continue;
            }
        }
        return payBacks;
    }

    /**
     * 方法说明：获取入职通知
     *
     * @param addEmployee 增员
     * @return com.authine.cloudpivot.web.api.entity.EntryNotice
     * @author liulei
     * @Date 2020/6/12 15:18
     */
    private EntryNotice getEntryNotice(AddEmployee addEmployee) throws ServiceException {
        EntryNotice entryNotice = new EntryNotice(addEmployee);
        String city = addEmployee.getSocialSecurityCity();
        String welfareHandler = addEmployee.getSWelfareHandler();
        String businessType = "社保";
        if (addEmployee.getSocialSecurityBase() == 0d) {
            city = addEmployee.getProvidentFundCity();
            welfareHandler = addEmployee.getGWelfareHandler();
            businessType = "公积金";
        }
        CollectionRuleMaintain ruleMaintain = collectionRuleMapper.getCollectionRuleMaintain(city, welfareHandler);
        entryNotice.setSocialSecurity(ruleMaintain.getSocialSecurityDeclarationMaterials());
        entryNotice.setProvidentFund(ruleMaintain.getProvidentFundDeclarationMaterials());
        entryNotice.setRecordOfEmployment(ruleMaintain.getEmploymentFilingMaterials());

        String handler = addEmployeeMapper.getHsLevyHandler(city, welfareHandler, businessType,
                addEmployee.getSecondLevelClientName());
        entryNotice.setOperateSignatory(handler);

        return entryNotice;
    }

    /**
     * 方法说明：判断是否有入职通知
     *
     * @param entryNotice 入职通知
     * @return boolean
     * @author liulei
     * @Date 2020/6/12 14:25
     */
    private boolean checkHaveEntryNotice(EntryNotice entryNotice) throws ServiceException {
        if (entryNotice == null) {
            return false;
        }
        boolean haveEntryNotice = false;
        if ("是".equals(entryNotice.getIsDisabled()) || "是".equals(entryNotice.getIsPoorArchivists()) ||
                "是".equals(entryNotice.getIsRetiredSoldier())) {
            haveEntryNotice = true;
        }
        if (StringUtils.isNotBlank(entryNotice.getSocialSecurity()) ||
                StringUtils.isNotBlank(entryNotice.getProvidentFund()) ||
                StringUtils.isNotBlank(entryNotice.getRecordOfEmployment())) {
            // 有补缴数据
            haveEntryNotice = true;
        }
        return haveEntryNotice;
    }
}
