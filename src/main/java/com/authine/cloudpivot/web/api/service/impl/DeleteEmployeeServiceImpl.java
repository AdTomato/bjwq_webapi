package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AddEmployeeMapper;
import com.authine.cloudpivot.web.api.mapper.DeleteEmployeeMapper;
import com.authine.cloudpivot.web.api.service.BatchEvacuationService;
import com.authine.cloudpivot.web.api.service.BusinessInsuranceService;
import com.authine.cloudpivot.web.api.service.DeleteEmployeeService;
import com.authine.cloudpivot.web.api.utils.AreaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @Description 减员serviceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.DeleteEmployeeServiceImpl
 * @Date 2020/5/15 8:56
 **/
@Service
@Slf4j
public class DeleteEmployeeServiceImpl implements DeleteEmployeeService {

    @Resource
    private DeleteEmployeeMapper delEmployeeMapper;

    @Resource
    private AddEmployeeMapper addEmployeeMapper;

    @Resource
    private BatchEvacuationService batchEvacuationService;

    @Resource
    private BusinessInsuranceService businessInsuranceService;

    private static final String EMPLOYEE_STATUS_QUIT = "已离职";

    @Override
    public DeleteEmployee getDeleteEmployeeById(String id) throws Exception {
        return delEmployeeMapper.getDeleteEmployeeById(id);
    }

    @Override
    public ShDeleteEmployee getShDeleteEmployeeById(String id) throws Exception {
        return delEmployeeMapper.getShDeleteEmployeeById(id);
    }

    @Override
    public NationwideDispatch getQgDeleteEmployeeById(String id) throws Exception {
        return delEmployeeMapper.getQgDeleteEmployeeById(id);
    }

    @Override
    public void updateDelEmployeeReturnReason(String id, String returnReason, String tableName) throws Exception {
        delEmployeeMapper.updateReturnReason(id, returnReason, tableName);
    }

    @Override
    public void delSubmit(List <DeleteEmployee> delEmployees, String userId, OrganizationFacade organizationFacade) throws Exception {

        List<BatchEvacuation> batchEvacuations = new ArrayList <>();

        for (int i = 0; i < delEmployees.size(); i++) {
            DeleteEmployee delEmployee = delEmployees.get(i);
            log.info("开始提交[" + delEmployee.getEmployeeName() + "]数据");
            // 查询是否存在员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(delEmployee.getFirstLevelClientName(),
                            delEmployee.getSecondLevelClientName(), delEmployee.getIdentityNo());
            if (employeeFiles == null) {
                throw new RuntimeException("没有查询到员工档案数据");
            }
            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, delEmployee.getCreatedTime(),
                    "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]", delEmployee.getLeaveTime(),
                    delEmployee.getSocialSecurityEndTime(), delEmployee.getProvidentFundEndTime(),
                    delEmployee.getLeaveReason(), delEmployee.getRemark(), delEmployee.getId());

            boolean sbHave = false, gjjHave = false, sbInAnhui = false, gjjInAnhui = false;
            if (delEmployee.getSocialSecurityEndTime() != null) {
                sbHave = true;
                if (AreaUtils.isAnhuiCity(delEmployee.getSocialSecurityCity())) {
                    sbInAnhui = true;
                }
            }
            if (delEmployee.getProvidentFundEndTime() != null) {
                gjjHave = true;
                if (AreaUtils.isAnhuiCity(delEmployee.getProvidentFundCity())) {
                    gjjInAnhui = true;
                }
            }

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工补充福利（商保，体检，代发福利）的员工状态为已离职");
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                    employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]减员表单数据");
            delEmployeeMapper.updateDeleteEmployee(delEmployee);

            // 福利地是省外
            if ((sbHave && !sbInAnhui) || (gjjHave && !gjjInAnhui)) {
                batchEvacuations.add(new BatchEvacuation(delEmployee.getId(), employeeFiles, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                if (!sbInAnhui && !gjjInAnhui) {
                    log.info("[" + delEmployee.getEmployeeName() + "]为省外数据");
                    continue;
                }
            }
            // 有省内停缴
            // 员工订单实体
            EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            if (orderForm != null) {
                throw new RuntimeException("没有查询到员工订单信息");
            }
            if (delEmployee.getSocialSecurityEndTime() != null && employeeFiles.getSocialSecurityBase() - 0d > 0d &&
                    sbInAnhui) {
                // 有社保停缴
                addSocialSecurityCloseData(delEmployee, employeeFiles, orderForm.getId());
                orderForm.setSocialSecurityChargeEnd(delEmployee.getSocialSecurityEndTime());
            }

            if (delEmployee.getProvidentFundEndTime() != null && employeeFiles.getProvidentFundBase() - 0d > 0d &&
                    gjjInAnhui) {
                // 有公积金停缴
                addProvidentFundCloseData(delEmployee, employeeFiles, orderForm.getId());
                orderForm.setProvidentFundChargeEnd(delEmployee.getProvidentFundEndTime());
            }
            delEmployeeMapper.updateEmployeeOrderFromChargeEndTime(orderForm);
        }

        if (batchEvacuations != null && batchEvacuations.size() > 0) {
            log.info("开始生成批量预派数据：" + batchEvacuations.toString());
            batchEvacuationService.addBatchEvacuationDatas(userId, organizationFacade, batchEvacuations);
        }
    }

    @Override
    public void shDelSubmit(List <ShDeleteEmployee> delEmployees) throws Exception {
        for (int i = 0; i < delEmployees.size(); i++) {
            ShDeleteEmployee delEmployee = delEmployees.get(i);
            log.info("开始提交[" + delEmployee.getEmployeeName() + "]数据");
            //获取员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(delEmployee.getFirstLevelClientName(),
                            delEmployee.getSecondLevelClientName(), delEmployee.getIdentityNo());
            if (employeeFiles == null) {
                throw new RuntimeException("没有查询到员工档案数据");
            }

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, delEmployee.getCreatedTime(),
                    "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]", delEmployee.getDepartureTime(),
                    delEmployee.getChargeEndTime(), delEmployee.getChargeEndTime(), delEmployee.getLeaveReason(),
                    delEmployee.getLeaveRemark(), delEmployee.getId());

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]减员表单数据");
            delEmployeeMapper.updateShDeleteEmployee(delEmployee);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工补充福利（商保，体检，代发福利）的员工状态为已离职");
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                    employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

        }
    }

    @Override
    public void qgDelSubmit(List <NationwideDispatch> delEmployees) throws Exception {
        for (int i = 0; i < delEmployees.size(); i++) {
            NationwideDispatch delEmployee = delEmployees.get(i);
            log.info("开始提交[" + delEmployee.getEmployeeName() + "]数据");
            //获取员工档案
            EmployeeFiles employeeFiles =
                    addEmployeeMapper.getEmployeeFilesByClientNameAndIdentityNo(delEmployee.getFirstLevelClientName(),
                            delEmployee.getSecondLevelClientName(), delEmployee.getIdentityNo());
            if (employeeFiles == null) {
                throw new RuntimeException("没有查询到员工档案数据");
            }
            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, delEmployee.getCreatedTime(),
                    "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]", delEmployee.getDepartureDate(),
                    delEmployee.getSServiceFeeEndDate(), delEmployee.getGServiceFeeEndDate(),
                    delEmployee.getSocialSecurityStopReason(), delEmployee.getDepartureRemark(), delEmployee.getId());

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]减员表单数据");
            delEmployeeMapper.updateQgDeleteEmployee(delEmployee);

            log.info("开始更新[" + delEmployee.getEmployeeName() + "]员工补充福利（商保，体检，代发福利）的员工状态为已离职");
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                    employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

        }
    }

    @Override
    public void delUpdateSubmit(String id, String userId, OrganizationFacade organizationFacade) throws Exception {
        DeleteEmployee update = delEmployeeMapper.getUpdateDelEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到减员变更数据");
        } else {
            log.info("开始更新减员表单数据");
            delEmployeeMapper.updateDeleteEmployeeByUpdate(update);
        }
        DeleteEmployee del = delEmployeeMapper.getDeleteEmployeeById(update.getDeleteEmployeeId());
        if (del == null) {
            throw new RuntimeException("没有查询到减员数据");
        }
        //获取员工档案
        EmployeeFiles employeeFiles = delEmployeeMapper.getEmployeeFilesByDelEmployeeId(del.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", del.getLeaveTime(),
                    del.getSocialSecurityEndTime(), del.getProvidentFundEndTime(),
                    del.getLeaveReason(), del.getRemark(), del.getId());
            log.info("开始更新员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }
        // 原员工订单实体
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());

        boolean sbHave = false, gjjHave = false, sbInAnhui = false, gjjInAnhui = false;
        if (del.getSocialSecurityEndTime() != null) {
            sbHave = true;
            if (AreaUtils.isAnhuiCity(del.getSocialSecurityCity())) {
                sbInAnhui = true;
            }
        }
        if (del.getProvidentFundEndTime() != null) {
            gjjHave = true;
            if (AreaUtils.isAnhuiCity(del.getProvidentFundCity())) {
                gjjInAnhui = true;
            }
        }

        List <BatchEvacuation> batchEvacuations = new ArrayList <>();
        // 删除原来批量撤离数据
        delEmployeeMapper.deleteBatchEvacuationByDeleteEmployeeId(del.getId());

        if (sbHave && gjjHave) {
            delEmployeeMapper.deleteCloseDataByDelEmployeeId(del.getId(), "i4fvb_social_security_close");
            delEmployeeMapper.deleteCloseDataByDelEmployeeId(del.getId(), "i4fvb_provident_fund_close");
            if ((sbHave && !sbInAnhui) || (sbHave && !gjjInAnhui)) {
                batchEvacuations.add(new BatchEvacuation(del.getId(), employeeFiles, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                log.info("开始生成批量预派数据：" + batchEvacuations.toString());
                batchEvacuationService.addBatchEvacuationDatas(userId, organizationFacade, batchEvacuations);
            }
            if (sbInAnhui || gjjInAnhui) {
                if (orderForm == null) {
                    throw new RuntimeException("没有查询到员工订单数据");
                }
                if (sbInAnhui) {
                    // 有社保停缴
                    addSocialSecurityCloseData(del, employeeFiles, orderForm.getId());
                    orderForm.setSocialSecurityChargeEnd(del.getSocialSecurityEndTime());
                }

                if (gjjInAnhui) {
                    // 有公积金停缴
                    addProvidentFundCloseData(del, employeeFiles, orderForm.getId());
                    orderForm.setProvidentFundChargeEnd(del.getProvidentFundEndTime());
                }
                delEmployeeMapper.updateEmployeeOrderFromChargeEndTime(orderForm);
            }
        } else if (sbHave) {
            delEmployeeMapper.deleteCloseDataByDelEmployeeId(del.getId(), "i4fvb_social_security_close");
            if (sbInAnhui) {
                addSocialSecurityCloseData(del, employeeFiles, orderForm.getId());
                orderForm.setSocialSecurityChargeEnd(del.getSocialSecurityEndTime());
            } else {
                batchEvacuations.add(new BatchEvacuation(del.getId(), employeeFiles, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                log.info("开始生成批量预派数据：" + batchEvacuations.toString());
                batchEvacuationService.addBatchEvacuationDatas(userId, organizationFacade, batchEvacuations);
            }
        } else if (gjjHave) {
            delEmployeeMapper.deleteCloseDataByDelEmployeeId(del.getId(), "i4fvb_provident_fund_close");
            if (gjjInAnhui) {
                // 有公积金停缴
                addProvidentFundCloseData(del, employeeFiles, orderForm.getId());
                orderForm.setProvidentFundChargeEnd(del.getProvidentFundEndTime());
            } else {
                batchEvacuations.add(new BatchEvacuation(del.getId(), employeeFiles, sbHave, !sbInAnhui, gjjHave, !gjjInAnhui));
                log.info("开始生成批量预派数据：" + batchEvacuations.toString());
                batchEvacuationService.addBatchEvacuationDatas(userId, organizationFacade, batchEvacuations);
            }
        }

    }

    @Override
    public void shDelUpdateSubmit(String id) throws Exception {
        ShDeleteEmployee update = delEmployeeMapper.getUpdateShDelEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到减员变更数据");
        } else {
            log.info("开始更新减员表单数据");
            delEmployeeMapper.updateShDeleteEmployeeByUpdate(update);
        }
        ShDeleteEmployee del = delEmployeeMapper.getShDeleteEmployeeById(update.getShDeleteEmployeeId());
        //获取员工档案
        EmployeeFiles employeeFiles = delEmployeeMapper.getEmployeeFilesByDelEmployeeId(del.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", del.getDepartureTime(),
                    del.getChargeEndTime(), del.getChargeEndTime(), del.getLeaveReason(), del.getLeaveRemark(),
                    del.getId());
            log.info("开始更新员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }
    }

    @Override
    public void qgDelUpdateSubmit(String id) throws Exception {
        NationwideDispatch update = delEmployeeMapper.getUpdateQgDelEmployeeById(id);
        if (update == null) {
            throw new RuntimeException("没有查询到减员变更数据");
        } else {
            log.info("开始更新减员表单数据");
            delEmployeeMapper.updateQgDeleteEmployeeByUpdate(update);
        }
        NationwideDispatch del = delEmployeeMapper.getQgDeleteEmployeeById(update.getNationwideDispatchDelId());
        //获取员工档案
        EmployeeFiles employeeFiles = delEmployeeMapper.getEmployeeFilesByDelEmployeeId(del.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        } else {
            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", del.getDepartureDate(),
                    del.getSServiceFeeEndDate(), del.getGServiceFeeEndDate(), del.getSocialSecurityStopReason(),
                    del.getDepartureRemark(), del.getId());
            log.info("开始更新员工档案数据");
            addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        }
    }

    @Override
    public void cancelDispatch(String id) throws Exception {
        DeleteEmployee deleteEmployee = delEmployeeMapper.getDeleteEmployeeById(id);
        if (deleteEmployee == null) {
            throw new RuntimeException("没有查询到减员数据！");
        }
        boolean cancelDispatch = true;
        if (deleteEmployee.getSocialSecurityEndTime() != null) {
            if (!"草稿".equals(deleteEmployee.getSbStatus()) && !"待办".equals(deleteEmployee.getSbStatus())) {
                cancelDispatch = false;
            }
        }
        if (deleteEmployee.getProvidentFundEndTime() != null) {
            if (!"草稿".equals(deleteEmployee.getGjjStatus()) && !"待办".equals(deleteEmployee.getGjjStatus())) {
                cancelDispatch = false;
            }
        }
        if (!cancelDispatch) {
            throw new RuntimeException("不可以取派");
        }
        // 获取原员工档案
        EmployeeFiles employeeFiles = delEmployeeMapper.getEmployeeFilesByDelEmployeeId(deleteEmployee.getId());
        if (employeeFiles == null) {
            throw new RuntimeException("没有查询到员工档案数据");
        }
        // 原员工订单实体
        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
        // 删除原来批量撤离数据
        delEmployeeMapper.deleteBatchEvacuationByDeleteEmployeeId(deleteEmployee.getId());
        delEmployeeMapper.deleteCloseDataByDelEmployeeId(deleteEmployee.getId(), "i4fvb_social_security_close");
        delEmployeeMapper.deleteCloseDataByDelEmployeeId(deleteEmployee.getId(), "i4fvb_provident_fund_close");

        boolean sbDel = false, gjjDel = false;
        if (deleteEmployee.getId().equals(employeeFiles.getSbDelEmployeeId()) || StringUtils.isBlank(employeeFiles.getSbDelEmployeeId())) {
            sbDel = true;
        }
        if (deleteEmployee.getId().equals(employeeFiles.getGjjDelEmployeeId()) || StringUtils.isBlank(employeeFiles.getGjjDelEmployeeId())) {
            gjjDel = true;
        }
        if (sbDel && gjjDel) {
            // 原工档案对应减员的社保，公积金
            employeeFiles = employeeFilesSetQuitInfoToNull(employeeFiles);

            if (orderForm != null) {
                orderForm.setSocialSecurityChargeEnd(null);
                orderForm.setProvidentFundChargeEnd(null);

            }
        } else if (sbDel) {
            employeeFiles.setSbDelEmployeeId(null);
            employeeFiles.setSocialSecurityChargeEnd(null);
            if (orderForm != null) {
                orderForm.setSocialSecurityChargeEnd(null);
            }
        } else if (gjjDel) {
            employeeFiles.setGjjDelEmployeeId(null);
            employeeFiles.setProvidentFundChargeEnd(null);
            if (orderForm != null) {
                orderForm.setProvidentFundChargeEnd(null);
            }
        }
        if (orderForm != null) {
            addEmployeeMapper.updateEmployeeOrderFrom(orderForm);
        }
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);
        addEmployeeMapper.updateStatusToCancelDispatch(id, "i4fvb_delete_employee");
    }

    @Override
    public void closeSubmit(List <String> ids, String schemaCode) throws Exception {
        if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(schemaCode)) {
            // 社保停缴提交
            List <SocialSecurityClose> closes = delEmployeeMapper.listSocialSecurityClose(ids);
            if (closes != null && closes.size() > 0) {
                for (int i = 0; i < closes.size(); i++) {
                    SocialSecurityClose close = closes.get(i);
                    if ("待办".equals(close.getStatus())) {
                        socialSecurityCloseSubmit(close);
                    } else {
                        log.info(close.getEmployeeName() + "：状态是“" + close.getStatus() + "“,无法提交");
                    }
                }
            }
        } else  if (Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(schemaCode)) {
            // 公积金停缴提交
            List <ProvidentFundClose> closes = delEmployeeMapper.listProvidentFundClose(ids);
            if (closes != null && closes.size() > 0) {
                for (int i = 0; i < closes.size(); i++) {
                    ProvidentFundClose close = closes.get(i);
                    if ("待办".equals(close.getStatus())) {
                        providentFundCloseSubmit(close);
                    } else {
                        log.info(close.getEmployeeName() + "：状态是“" + close.getStatus() + "“,无法提交");
                    }
                }
            }
        }
    }

    @Override
    public void closeReject(List <String> ids, String returnReason, String schemaCode) throws Exception {
        if (Constants.SOCIAL_SECURITY_CLOSE_SCHEMA.equals(schemaCode)) {
            // 社保停缴驳回
            List <SocialSecurityClose> closes = delEmployeeMapper.listSocialSecurityClose(ids);
            if (closes != null && closes.size() > 0) {
                for (int i = 0; i < closes.size(); i++) {
                    SocialSecurityClose close = closes.get(i);
                    if ("待办".equals(close.getStatus())) {
                        socialSecurityCloseReject(close, returnReason);
                    } else {
                        log.info(close.getEmployeeName() + "：状态是“" + close.getStatus() + "“,无法驳回");
                    }
                }
            }
        } else  if (Constants.PROVIDENT_FUND_CLOSE_SCHEMA.equals(schemaCode)) {
            // 公积金停缴驳回
            List <ProvidentFundClose> closes = delEmployeeMapper.listProvidentFundClose(ids);
            if (closes != null && closes.size() > 0) {
                for (int i = 0; i < closes.size(); i++) {
                    ProvidentFundClose close = closes.get(i);
                    if ("待办".equals(close.getStatus())) {
                        providentFundCloseReject(close, returnReason);
                    } else {
                        log.info(close.getEmployeeName() + "：状态是“" + close.getStatus() + "“,无法驳回");
                    }
                }
            }
        }
    }

    /**
     * 方法说明：公积金停缴驳回操作
     * @param close 公积金停缴
     * @param returnReason 退回原因
     * @author liulei
     * @Date 2020/6/16 11:52
     */
    private void providentFundCloseReject(ProvidentFundClose close, String returnReason) throws Exception{
        log.info(close.getEmployeeName() + "：开始驳回公积金停缴");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(close.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        } else {
            log.info("更新员工订单的社保截止时间为null");
            orderForm.setProvidentFundChargeEnd(null);
            delEmployeeMapper.updateEmployeeOrderFromChargeEndTime(orderForm);
        }

        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesById(close.getEmployeeFilesId());
        if (StringUtils.isBlank(employeeFiles.getSbDelEmployeeId())) {
            log.info("员工档案没有社保减员");
            employeeFiles = employeeFilesSetQuitInfoToNull(employeeFiles);
        } else {
            log.info("员工档案有社保减员");
            employeeFiles.setGjjDelEmployeeId(null);
            employeeFiles.setProvidentFundChargeEnd(null);
        }

        log.info("更新员工档案公积金停缴信息");
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);

        log.info("更新减员客户公积金信息");
        delEmployeeMapper.updateDeleteEmployeeStatus(close.getDelEmployeeId(), "gjj_status", "驳回", returnReason);

        log.info("删除公积金停缴数据");
        delEmployeeMapper.deleteCloseDataByDelEmployeeId(close.getDelEmployeeId(), "i4fvb_provident_fund_close");

        log.info(close.getEmployeeName() + "：公积金停缴驳回成功");
    }

    /**
     * 方法说明：社保停缴驳回操作
     * @param close 社保停缴
     * @param returnReason 退回原因
     * @author liulei
     * @Date 2020/6/16 11:52
     */
    private void socialSecurityCloseReject(SocialSecurityClose close, String returnReason) throws Exception{
        log.info(close.getEmployeeName() + "：开始驳回社保停缴");

        EmployeeOrderForm orderForm = addEmployeeMapper.getEmployeeOrderFormById(close.getEmployeeOrderFormId());
        if (orderForm == null) {
            throw new RuntimeException("没有查询到员工订单数据");
        } else {
            log.info("更新员工订单的社保截止时间为null");
            orderForm.setSocialSecurityChargeEnd(null);
            delEmployeeMapper.updateEmployeeOrderFromChargeEndTime(orderForm);
        }

        EmployeeFiles employeeFiles = addEmployeeMapper.getEmployeeFilesById(close.getEmployeeFilesId());
        if (StringUtils.isBlank(employeeFiles.getGjjDelEmployeeId())) {
            log.info("员工档案没有公积金减员");
            employeeFiles = employeeFilesSetQuitInfoToNull(employeeFiles);
        } else {
            log.info("员工档案有公积金减员");
            employeeFiles.setSbDelEmployeeId(null);
            employeeFiles.setSocialSecurityChargeEnd(null);
        }
        log.info("更新员工档案社保停缴信息");
        addEmployeeMapper.updateEmployeeFiles(employeeFiles);

        log.info("更新减员客户社保信息");
        delEmployeeMapper.updateDeleteEmployeeStatus(close.getDelEmployeeId(), "sb_status", "驳回", returnReason);

        log.info("删除社保停缴数据");
        delEmployeeMapper.deleteCloseDataByDelEmployeeId(close.getDelEmployeeId(), "i4fvb_social_security_close");

        log.info(close.getEmployeeName() + "：社保停缴驳回成功");
    }

    private EmployeeFiles employeeFilesSetQuitInfoToNull(EmployeeFiles employeeFiles) {
        employeeFiles.setReportQuitDate(null);
        employeeFiles.setReportSeveranceOfficer(null);
        employeeFiles.setQuitDate(null);
        employeeFiles.setQuitReason(null);
        employeeFiles.setQuitRemark(null);
        employeeFiles.setSbDelEmployeeId(null);
        employeeFiles.setSocialSecurityChargeEnd(null);
        employeeFiles.setGjjDelEmployeeId(null);
        employeeFiles.setProvidentFundChargeEnd(null);
        return employeeFiles;
    }

    /**
     * 方法说明：公积金停缴提交
     * @param close 公积金停缴
     * @author liulei
     * @Date 2020/6/16 11:29
     */
    private void providentFundCloseSubmit(ProvidentFundClose close) throws Exception{
        log.info(close.getEmployeeName() + "：开始提交公积金停缴");

        log.info("更新员工订单的公积金状态");
        delEmployeeMapper.updateEmployeeOrderFromStatus(close.getEmployeeOrderFormId(), "provident_fund_status", "停缴");

        log.info("更新员工减员状态");
        delEmployeeMapper.updateDeleteEmployeeStatus(close.getDelEmployeeId(), "gjj_status", "停缴", null);

        log.info(close.getEmployeeName() + "：公积金停缴提交完成");
    }

    /**
     * 方法说明：社保停缴提交
     * @param close 社保停缴
     * @author liulei
     * @Date 2020/6/16 11:29
     */
    private void socialSecurityCloseSubmit(SocialSecurityClose close) throws Exception{
        log.info(close.getEmployeeName() + "：开始提交社保停缴");

        log.info("更新员工订单的社保状态");
        delEmployeeMapper.updateEmployeeOrderFromStatus(close.getEmployeeOrderFormId(), "social_security_status", "停缴");

        log.info("更新员工减员状态");
        delEmployeeMapper.updateDeleteEmployeeStatus(close.getDelEmployeeId(), "sb_status", "停缴", null);

        log.info(close.getEmployeeName() + "：社保停缴提交完成");
    }

    /**
     * 方法说明：创建公积金停缴数据
     * @param delEmployee 减员信息
     * @param employeeFiles 员工档案
     * @param orderFormId 员工订单id
     * @author liulei
     * @Date 2020/6/15 11:17
     */
    private void addProvidentFundCloseData(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId)  throws Exception{
        ProvidentFundClose close = new ProvidentFundClose(delEmployee, employeeFiles, orderFormId);

        // 查询公积金的企业，个人，合计存缴额
        EmployeeOrderFormDetails details = delEmployeeMapper.getGjjEmployeeOrderFormDetails(orderFormId);
        if (details != null) {
            close.setPersonalDeposit(details.getEmployeeMoney());
            close.setEnterpriseDeposit(details.getCompanyMoney());
            close.setTotalDeposit(details.getSum());
            close.setCompanyProvidentFundBl(details.getCompanyRatio());
            close.setEmployeeProvidentFundBl(details.getEmployeeRatio());
        }
        String handler = addEmployeeMapper.getHsLevyHandler(close.getCity(), close.getWelfareHandler(),
                "公积金", close.getSecondLevelClientName());
        close.setOperateLeader(handler);

        log.info("开始新增[" + close.getEmployeeName() + "]公积金停缴数据：" + close.toString());
        delEmployeeMapper.addProvidentFundClose(close);
    }

    /**
     * 方法说明：创建社保停缴数据
     * @param delEmployee 减员信息
     * @param employeeFiles 员工档案
     * @param orderFormId 员工订单id
     * @author liulei
     * @Date 2020/6/15 11:17
     */
    private void addSocialSecurityCloseData(DeleteEmployee delEmployee, EmployeeFiles employeeFiles, String orderFormId) throws Exception{
        SocialSecurityClose close = new SocialSecurityClose(delEmployee, employeeFiles, orderFormId);
        String handler = addEmployeeMapper.getHsLevyHandler(close.getCity(), close.getWelfareHandler(),
                "社保", close.getSecondLevelClientName());
        close.setOperateLeader(handler);

        log.info("开始新增[" + close.getEmployeeName() + "]社保停缴数据：" + close.toString());
        delEmployeeMapper.addSocialSecurityClose(close);
    }

    /**
     * 方法说明：员工档案set减员信息
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/6/15 11:17
     */
    private EmployeeFiles setEmployeeFilesQuitInfo(EmployeeFiles employeeFiles, Date reportQuitDate,
                                                   String reportSeveranceOfficer, Date quitDate, Date sEndTime,
                                                   Date gEndTime, String quitReason, String quitRemark,
                                                   String delEmployeeId) {
        employeeFiles.setReportQuitDate(reportQuitDate);
        employeeFiles.setReportSeveranceOfficer(reportSeveranceOfficer);
        employeeFiles.setQuitDate(quitDate);
        employeeFiles.setQuitReason(quitReason);
        employeeFiles.setQuitRemark(quitRemark);
        if (sEndTime != null) {
            employeeFiles.setSbDelEmployeeId(delEmployeeId);
            employeeFiles.setSocialSecurityChargeEnd(sEndTime);
        }
        if (gEndTime != null) {
            employeeFiles.setGjjDelEmployeeId(delEmployeeId);
            employeeFiles.setProvidentFundChargeEnd(gEndTime);
        }
        return employeeFiles;
    }
}
