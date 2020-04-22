package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.service.*;
import com.authine.cloudpivot.web.api.service.impl.UpdateEmployeesServiceImpl;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liulei
 * @Description 员工信息维护（增减员）
 * @ClassName com.authine.cloudpivot.web.api.controller.EmployeeMaintainController
 * @Date 2020/2/4 17:15
 **/
@RestController
@RequestMapping("/controller/employeeMaintain")
@Slf4j
public class EmployeeMaintainController extends BaseController {

    private static final String EMPLOYEE_STATUS_QUIT = "已离职";

    @Resource
    private EmployeeMaintainService employeeMaintainService;

    @Resource
    private AddEmployeeService addEmployeeService;

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private EmployeeFilesService employeeFilesService;

    @Resource
    private BatchPreDispatchService batchPreDispatchService;

    @Resource
    private BatchEvacuationService batchEvacuationService;

    @Resource
    private BusinessInsuranceService businessInsuranceService;

    @Resource
    private UpdateEmployeesServiceImpl updateEmployeeService;

    @Resource
    private CollectionRuleService collectionRuleService;

    @Resource
    private LaborContractInfoService laborContractInfoService;

    /**
     * 方法说明：增员导入
     * @Param fileName
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/7 8:35
     */
    @GetMapping("/addEmployeeImport")
    @ResponseBody
    public ResponseResult <String> addEmployeeImport(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            String userId = getUserId();
            UserModel user = this.getOrganizationFacade().getUser(userId);
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            employeeMaintainService.addEmployee(this.getWorkflowInstanceFacade(), fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员导入
     * @param fileName
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/7 8:36
     */
    @GetMapping("/deleteEmployeeImport")
    @ResponseBody
    public ResponseResult <String> deleteEmployeeImport(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            String userId = getUserId();
            UserModel user = this.getOrganizationFacade().getUser(userId);
            DepartmentModel dept = this.getOrganizationFacade().getDepartment(user.getDepartmentId());
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            employeeMaintainService.deleteEmployee(this.getWorkflowInstanceFacade(), fileName, user, dept);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_客户
     *     提交时激发停缴社保，公积金流程, 修改员工档案数据
     * @param id 减员_客户实体id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/9 10:27
     */
    @GetMapping("/deleteEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> deleteEmployeeSubmit(String id) {

        try {
            DeleteEmployee delEmployee = employeeFilesService.getDeleteEmployeeData(id);
            if (delEmployee == null) {
                log.error("没有获取到减员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到减员数据！");
            }
            delEmployee = CommonUtils.processingIdentityNo(delEmployee);
            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(delEmployee.getIdentityNo(),
                            delEmployee.getFirstLevelClientName(), delEmployee.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, delEmployee.getCreatedTime(),
                    "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]", delEmployee.getLeaveTime(),
                    delEmployee.getSocialSecurityEndTime(), delEmployee.getProvidentFundEndTime(),
                    delEmployee.getLeaveReason(), delEmployee.getRemark());

            boolean sIsOut = false, gIsOut = false;
            if (StringUtils.isNotBlank(delEmployee.getSocialSecurityCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getSocialSecurityCity()) < 0) {
                sIsOut = true;
            }
            if (StringUtils.isNotBlank(delEmployee.getProvidentFundCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getProvidentFundCity()) < 0) {
                gIsOut = true;
            }
            if (sIsOut || gIsOut) {
                // 判断批量撤离（省外）
                List<BatchEvacuation> batchEvacuations = new ArrayList <>();
                if (Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) < 0) {
                    BatchEvacuation batchEvacuation = new BatchEvacuation(employeeFiles.getEmployeeName(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getReportQuitDate(),
                            employeeFiles.getSocialSecurityChargeEnd(), employeeFiles.getProvidentFundChargeEnd(),
                            employeeFiles.getQuitReason(), employeeFiles.getQuitRemark());
                    batchEvacuations.add(batchEvacuation);
                }
                // 生成批量撤离
                batchEvacuationService.addBatchEvacuationDatas(this.getUserId(), this.getOrganizationFacade(),
                        batchEvacuations);
            }

            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            if (sIsOut && gIsOut) {
                return this.getOkResponseResult("success", "操作成功!");
            }

            // 员工订单实体
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            // 运行负责人
            OperateLeader operateLeader = getOperateLeader(delEmployee.getSocialSecurityCity(),
                    delEmployee.getSWelfareHandler(), delEmployee.getProvidentFundCity(), delEmployee.getGWelfareHandler(),
                    delEmployee.getSecondLevelClientName());

            if (!sIsOut && employeeFiles.getSocialSecurityChargeEnd() != null) {
                // 有社保停缴申请
                SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING", delEmployee.getCreater(),
                        delEmployee.getCreatedDeptId(), delEmployee.getCreatedTime(), delEmployee.getOwner(),
                        delEmployee.getOwnerDeptId(), delEmployee.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), delEmployee.getSWelfareHandler(),
                        employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                        employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                        operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), delEmployee.getSubordinateDepartment(),
                        delEmployee.getSocialSecurityCity());
                // 社保停缴
                createSocialSecurityClose(sClose, delEmployee.getCreater(), delEmployee.getCreatedDeptId());
            }

            if (!gIsOut && employeeFiles.getProvidentFundChargeEnd() != null) {
                SocialSecurityFundDetail detail =
                        employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                if (detail != null) {
                    enterpriseDeposit = detail.getCompanyMoney();
                    personalDeposit = detail.getEmployeeMoney();
                    totalDeposit = detail.getSum();
                }
                // 有公积金停缴
                ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",delEmployee.getCreater(),
                        delEmployee.getCreatedDeptId(), delEmployee.getCreatedTime(), delEmployee.getOwner(),
                        delEmployee.getOwnerDeptId(), delEmployee.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), delEmployee.getGWelfareHandler(),
                        employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                        employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                        operateLeader.getProvidentFundLeader(), "待办", delEmployee.getSubordinateDepartment(),
                        delEmployee.getProvidentFundCity());
                // 公积金停缴
                createProvidentFundClose(gClose, delEmployee.getCreater(), delEmployee.getCreatedDeptId());
            }

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getSecondLevelClientName(),
                    employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

            return this.getOkResponseResult("success", "激发停缴社保，公积金流程成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_上海
     *     提交时激发停缴社保，公积金流程, 修改员工档案数据
     * @param id 减员_客户实体id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/9 10:27
     */
    @GetMapping("/shDeleteEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> shDeleteEmployeeSubmit(String id) {

        try {
            ShDeleteEmployee shDeleteEmployee = employeeFilesService.getShDeleteEmployeeData(id);
            if (shDeleteEmployee == null) {
                log.error("没有获取到减员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到减员数据！");
            }
            shDeleteEmployee = CommonUtils.processingIdentityNo(shDeleteEmployee);

            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(shDeleteEmployee.getIdentityNo(),
                            shDeleteEmployee.getFirstLevelClientName(), shDeleteEmployee.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, shDeleteEmployee.getCreatedTime(),
                    "[{\"id\":\"" + shDeleteEmployee.getCreater() + "\",\"type\":3}]", shDeleteEmployee.getDepartureTime(),
                    shDeleteEmployee.getChargeEndTime(), shDeleteEmployee.getChargeEndTime(),
                    shDeleteEmployee.getLeaveReason(), shDeleteEmployee.getLeaveRemark());

            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            // 员工订单实体
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(employeeFiles.getSocialSecurityCity(),
                    employeeFiles.getSWelfareHandler(), employeeFiles.getSecondLevelClientName());
            if (employeeFiles.getSocialSecurityChargeEnd() != null) {
                // 有社保停缴申请
                SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING",shDeleteEmployee.getCreater(),
                        shDeleteEmployee.getCreatedDeptId(), shDeleteEmployee.getCreatedTime(), shDeleteEmployee.getOwner(),
                        shDeleteEmployee.getOwnerDeptId(), shDeleteEmployee.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                        employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                        employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                        operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), shDeleteEmployee.getSubordinateDepartment(),
                        employeeFiles.getSocialSecurityCity());
                // 社保停缴
                createSocialSecurityClose(sClose, shDeleteEmployee.getCreater(), shDeleteEmployee.getCreatedDeptId());
            }

            if (employeeFiles.getProvidentFundChargeEnd() != null) {
                SocialSecurityFundDetail detail =
                        employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                if (detail != null) {
                    enterpriseDeposit = detail.getCompanyMoney();
                    personalDeposit = detail.getEmployeeMoney();
                    totalDeposit = detail.getSum();
                }
                // 有公积金停缴
                ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",shDeleteEmployee.getCreater(),
                        shDeleteEmployee.getCreatedDeptId(), shDeleteEmployee.getCreatedTime(), shDeleteEmployee.getOwner(),
                        shDeleteEmployee.getOwnerDeptId(), shDeleteEmployee.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), employeeFiles.getGWelfareHandler(),
                        employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                        employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                        operateLeader.getProvidentFundLeader(), "待办", shDeleteEmployee.getSubordinateDepartment(),
                        employeeFiles.getProvidentFundCity());
                // 公积金停缴
                createProvidentFundClose(gClose, shDeleteEmployee.getCreater(), shDeleteEmployee.getCreatedDeptId());
            }

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(),
                    EMPLOYEE_STATUS_QUIT);

            return this.getOkResponseResult("success", "激发停缴社保，公积金流程成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_全国
     *     提交时激发停缴社保，公积金流程, 修改员工档案数据
     * @param id 减员_客户id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/9 10:27
     */
    @GetMapping("/qgDeleteEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> qgDeleteEmployeeSubmit(String id) {

        try {
            NationwideDispatch nationwideDispatch = employeeFilesService.getNationwideDeleteEmployeeData(id);
            if (nationwideDispatch == null) {
                log.error("没有获取到减员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到增员数据！");
            }

            nationwideDispatch = CommonUtils.processingIdentityNo(nationwideDispatch);

            // 获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(nationwideDispatch.getIdentityNo(),
                            nationwideDispatch.getFirstLevelClientName(),
                            nationwideDispatch.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, nationwideDispatch.getCreatedTime(),
                    "[{\"id\":\"" + nationwideDispatch.getCreater() + "\",\"type\":3}]", nationwideDispatch.getDepartureDate(),
                    nationwideDispatch.getSServiceFeeEndDate(), nationwideDispatch.getGServiceFeeEndDate(),
                    nationwideDispatch.getSocialSecurityStopReason(), nationwideDispatch.getDepartureRemark());

            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            // 员工订单实体
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(employeeFiles.getSocialSecurityCity(),
                    employeeFiles.getSWelfareHandler(), employeeFiles.getSecondLevelClientName());

            if (employeeFiles.getSocialSecurityChargeEnd() != null) {
                // 有社保停缴申请
                SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING",nationwideDispatch.getCreater(),
                        nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime() ,nationwideDispatch.getOwner(),
                        nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                        employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                        employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason() + employeeFiles.getQuitRemark(),
                        operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), nationwideDispatch.getSubordinateDepartment(),
                        employeeFiles.getSocialSecurityCity());
                // 社保停缴
                createSocialSecurityClose(sClose, nationwideDispatch.getCreater(), nationwideDispatch.getCreatedDeptId());
            }

            if (employeeFiles.getProvidentFundChargeEnd() != null) {
                SocialSecurityFundDetail detail =
                        employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                if (detail != null) {
                    enterpriseDeposit = detail.getCompanyMoney();
                    personalDeposit = detail.getEmployeeMoney();
                    totalDeposit = detail.getSum();
                }
                // 有公积金停缴
                ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",nationwideDispatch.getCreater(),
                        nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime(), nationwideDispatch.getOwner(),
                        nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                        employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                        employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                        employeeFiles.getSecondLevelClientName(), employeeFiles.getGWelfareHandler(),
                        employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                        employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                        operateLeader.getProvidentFundLeader(), "待办", nationwideDispatch.getSubordinateDepartment(),
                        employeeFiles.getProvidentFundCity());
                // 公积金停缴
                createProvidentFundClose(gClose, nationwideDispatch.getCreater(), nationwideDispatch.getCreatedDeptId());
            }

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(),
                    EMPLOYEE_STATUS_QUIT);

            return this.getOkResponseResult("success", "激发停缴社保，公积金流程成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_客户提交
     *     1.创建员工档案；2.创建入职通知；3.创建员工订单；4.创建社保申报，公积金申报
     * @param id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/11 11:11
     */
    @GetMapping("/addEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> addEmployeeSubmit(String id) {

        try {
            AddEmployee addEmployee = employeeFilesService.getAddEmployeeData(id);
            if (addEmployee == null) {
                log.error("没有获取到增员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到增员数据！");
            }
            // 判断是否是六安，是基数四舍五入取整
            addEmployee = CommonUtils.needBaseRounding(addEmployee);
            addEmployee = CommonUtils.processingIdentityNo(addEmployee);

            EmployeeFiles employeeFiles = new EmployeeFiles("COMPLETED", addEmployee.getCreater(),
                    addEmployee.getCreatedDeptId(), addEmployee.getCreatedTime(), addEmployee.getOwner(),
                    addEmployee.getOwnerDeptId(), addEmployee.getOwnerDeptQueryCode(), addEmployee.getEmployeeName(),
                    addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getGender(),
                    addEmployee.getBirthday(), addEmployee.getEmployeeNature(), addEmployee.getFamilyRegisterNature(),
                    addEmployee.getMobile(), addEmployee.getSocialSecurityCity(), addEmployee.getProvidentFundCity(),
                    addEmployee.getCreatedTime(), "[{\"id\":\"" + addEmployee.getCreater() + "\",\"type\":3}]",
                    addEmployee.getEntryTime(), addEmployee.getSocialSecurityStartTime(),
                    addEmployee.getProvidentFundStartTime(), addEmployee.getRemark(), addEmployee.getEmail(), 0, 0,
                    addEmployee.getSubordinateDepartment(), addEmployee.getFirstLevelClientName(),
                    addEmployee.getSecondLevelClientName(), addEmployee.getHouseholdRegisterRemarks(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundBase(),
                    addEmployee.getSWelfareHandler(), addEmployee.getGWelfareHandler(),
                    addEmployee.getIsRetiredSoldier(), addEmployee.getIsPoorArchivists(), addEmployee.getIsDisabled());

            boolean sIsOut = false, gIsOut = false;
            if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getSocialSecurityCity()) < 0) {
                sIsOut = true;
            }
            if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getProvidentFundCity()) < 0) {
                gIsOut = true;
            }
            if (sIsOut || gIsOut) {
                // 批量生成预派
                addBatchPreDispatch(addEmployee, sIsOut, gIsOut);
            }
            // 查询征缴规则
            CollectionRule collectionRule =
                    collectionRuleService.getCollectionRuleByCity(addEmployee.getSocialSecurityCity(),
                        addEmployee.getProvidentFundCity());
            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName());
            int sTimeNode=0,gTimeNode=0;
            if (ccps != null) {
                sTimeNode = ccps.getTimeNode();
                gTimeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity())) {
                    sTimeNode = employeeMaintainService.getTimeNode(addEmployee.getSocialSecurityCity());
                }
                if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity())) {
                    if (addEmployee.getProvidentFundCity().equals(addEmployee.getSocialSecurityCity())) {
                        gTimeNode = sTimeNode;
                    } else {
                        gTimeNode = employeeMaintainService.getTimeNode(addEmployee.getProvidentFundCity());
                    }
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(sTimeNode, new Date(), addEmployee.getSocialSecurityStartTime());
            int gMonth = getMonthDifference(gTimeNode, new Date(), addEmployee.getProvidentFundStartTime());
            // 是否入职通知
            BizObjectModel entryNotice = employeeMaintainService.getEntryNotice(addEmployee, sMonth, gMonth,
                    collectionRule);
            if(entryNotice == null) {
                employeeFiles.setEntryNotice("否");
            } else {
                employeeFiles.setEntryNotice("是");
            }
            // 运行负责人
            OperateLeader operateLeader = getOperateLeader(addEmployee.getSocialSecurityCity(),
                    addEmployee.getSWelfareHandler(), addEmployee.getProvidentFundCity(), addEmployee.getGWelfareHandler(),
                    addEmployee.getSecondLevelClientName());

            if (entryNotice != null) {
                entryNotice.getData().put("operate_signatory", operateLeader.getSocialSecurityLeader() == null ?
                        operateLeader.getProvidentFundLeader() : operateLeader.getSocialSecurityLeader());
            }

            // 生成员工档案数据
            String employeeFilesId = createEmployeeFiles(employeeFiles, addEmployee.getCreater());
            // 生成入职通知
            if (entryNotice != null) {
                createEntryNotice(entryNotice, addEmployee.getCreater(), addEmployee.getCreatedDeptId());
            }

            // 如果员工性质是“派遣”，“外包”需要创建劳动合同
            if ("派遣".equals(addEmployee.getEmployeeNature()) || "外包".equals(addEmployee.getEmployeeNature())) {
                LaborContractInfo laborContractInfo = new LaborContractInfo(addEmployee);
                laborContractInfo.setEmployeeFilesId(employeeFilesId);
                laborContractInfoService.saveLaborContractInfo(laborContractInfo);
            }

            if (sIsOut && gIsOut) {
                return this.getOkResponseResult("success", "增员提交成功!");
            }

            // 查询服务费
            Double serviceFee = salesContractService.getFee(addEmployee.getSecondLevelClientName(),
                    addEmployee.getEmployeeNature(), StringUtils.isBlank(addEmployee.getSocialSecurityCity()) ?
                            addEmployee.getProvidentFundCity() : addEmployee.getSocialSecurityCity());

            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    addEmployee.getSocialSecurityCity(), addEmployee.getSocialSecurityStartTime(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundCity(),
                    addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl(),
                    sMonth, gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    employeeFilesId, addEmployee.getSWelfareHandler(), addEmployee.getGWelfareHandler(),
                    addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getEmployeeNature(),
                    addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, addEmployee.getCreater(), addEmployee.getOwner(), addEmployee.getOwnerDeptId()
                    , addEmployee.getOwnerDeptQueryCode());

            //创建社保申报
            if (addEmployee.getSocialSecurityStartTime() != null && !sIsOut) {
                SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING", addEmployee.getCreater(),
                        addEmployee.getCreatedDeptId(), addEmployee.getCreatedTime(), addEmployee.getOwner(),
                        addEmployee.getOwnerDeptId(), addEmployee.getOwnerDeptQueryCode(),
                        addEmployee.getSocialSecurityStartTime(), addEmployee.getEmployeeName(), addEmployee.getGender(),
                        addEmployee.getIdentityNo(), addEmployee.getIdentityNoType(), addEmployee.getContractStartTime(),
                        addEmployee.getContractEndTime(), addEmployee.getContractSalary(),
                        addEmployee.getSocialSecurityBase(), addEmployee.getMobile(), addEmployee.getSWelfareHandler(),
                        addEmployee.getBirthday(), operateLeader.getSocialSecurityLeader(), employeeOrderFormId, "待办",
                        addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName(),
                        addEmployee.getSubordinateDepartment(), addEmployee.getSocialSecurityCity(),
                        addEmployee.getRemark());
                String sId = createSocialSecurityDeclare(sDeclare, addEmployee.getCreater(),
                        addEmployee.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(sId,
                        employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
            }

            //创建公积金申报
            if (addEmployee.getProvidentFundStartTime() != null  && !gIsOut) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }

                ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", addEmployee.getCreater(),
                        addEmployee.getCreatedDeptId(), addEmployee.getCreatedTime(), addEmployee.getOwner(),
                        addEmployee.getOwnerDeptId(), addEmployee.getOwnerDeptQueryCode(), employeeOrderFormId,
                        addEmployee.getEmployeeName(), addEmployee.getGender(), addEmployee.getBirthday(),
                        addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getGWelfareHandler(),
                        addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(), corporatePayment,
                        personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                        addEmployee.getProvidentFundCity(), addEmployee.getFirstLevelClientName(),
                        addEmployee.getSecondLevelClientName(), addEmployee.getSubordinateDepartment());

                String gId = createProvidentFundDeclare(gDeclare, addEmployee.getCreater(), addEmployee.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(gId,
                        employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
            }

            return this.getOkResponseResult("success", "增员提交成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_上海提交
     *     1.创建员工档案；2.创建入职通知；3.创建员工订单；4.创建社保申报，公积金申报
     * @param id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/11 11:11
     */
    @GetMapping("/shAddEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> shAddEmployeeSubmit(String id) {

        try {
            ShAddEmployee shAddEmployee = employeeFilesService.getShAddEmployeeData(id);
            if (shAddEmployee == null) {
                log.error("没有获取到增员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到增员数据！");
            }
            // 判断是否是六安，是基数四舍五入取整
            shAddEmployee = CommonUtils.needBaseRounding(shAddEmployee);
            // 身份证号验证
            shAddEmployee = CommonUtils.processingIdentityNo(shAddEmployee);

            if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
                shAddEmployee.setProvidentFundStartTime(shAddEmployee.getBenefitStartTime());
            }
            EmployeeFiles employeeFiles = new EmployeeFiles("COMPLETED",shAddEmployee.getCreater(),
                    shAddEmployee.getCreatedDeptId(), shAddEmployee.getCreatedTime(), shAddEmployee.getOwner(),
                    shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode(), shAddEmployee.getEmployeeName(),
                    shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), shAddEmployee.getGender(),
                    shAddEmployee.getBirthday(), "代理", null,
                    shAddEmployee.getMobile(), shAddEmployee.getCityName(), shAddEmployee.getCityName(),
                    shAddEmployee.getCreatedTime(), "[{\"id\":\"" + shAddEmployee.getCreater() + "\",\"type\":3}]",
                    shAddEmployee.getEntryTime(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getInductionRemark(), shAddEmployee.getMail(), 0, 0,
                    shAddEmployee.getSubordinateDepartment(), shAddEmployee.getFirstLevelClientName(),
                    shAddEmployee.getSecondLevelClientName(), shAddEmployee.getHouseholdRegisterRemarks(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getWelfareHandler(), shAddEmployee.getWelfareHandler(), shAddEmployee.getIsRetiredSoldier(),
                    shAddEmployee.getIsPoorArchivists(), shAddEmployee.getIsDisabled());

            // 查询征缴规则
            CollectionRule collectionRule = collectionRuleService.getCollectionRuleByCity(shAddEmployee.getCityName());
            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName());
            int timeNode = 0;
            if (ccps != null) {
                timeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(shAddEmployee.getCityName())) {
                    timeNode = employeeMaintainService.getTimeNode(shAddEmployee.getCityName());
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(timeNode, new Date(), shAddEmployee.getBenefitStartTime());
            int gMonth = getMonthDifference(timeNode, new Date(), shAddEmployee.getProvidentFundStartTime());
            // 是否入职通知
            BizObjectModel entryNotice = employeeMaintainService.getEntryNotice(shAddEmployee, sMonth, gMonth,
                    collectionRule);
            if(entryNotice == null) {
                employeeFiles.setEntryNotice("否");
            } else {
                employeeFiles.setEntryNotice("是");
            }
            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(shAddEmployee.getCityName(),
                    shAddEmployee.getWelfareHandler(), shAddEmployee.getSecondLevelClientName());

            if (entryNotice != null) {
                entryNotice.getData().put("operate_signatory", operateLeader.getSocialSecurityLeader() == null ?
                        operateLeader.getProvidentFundLeader() : operateLeader.getSocialSecurityLeader());
            }

            // 生成员工档案数据
            String employeeFilesId = createEmployeeFiles(employeeFiles, shAddEmployee.getCreater());
            // 生成入职通知
            if (entryNotice != null) {
                createEntryNotice(entryNotice, shAddEmployee.getCreater(), shAddEmployee.getCreatedDeptId());
            }

            // 查询服务费
            Double serviceFee = salesContractService.getFee(shAddEmployee.getSecondLevelClientName(), "代理",
                    shAddEmployee.getCityName());

            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    shAddEmployee.getCityName(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getCityName(),
                    shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getPSupplementProvidentFund(), shAddEmployee.getUSupplementProvidentFund(), sMonth,
                    gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    employeeFilesId, shAddEmployee.getWelfareHandler(), shAddEmployee.getWelfareHandler(),
                    shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), "代理",
                    shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, shAddEmployee.getCreater(), shAddEmployee.getOwner(),
                    shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode());

            //创建社保申报
            if (shAddEmployee.getBenefitStartTime() != null) {
                SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING", shAddEmployee.getCreater(),
                        shAddEmployee.getCreatedDeptId(), shAddEmployee.getCreatedTime(), shAddEmployee.getOwner(),
                        shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode(),
                        shAddEmployee.getBenefitStartTime(), shAddEmployee.getEmployeeName(), shAddEmployee.getGender(),
                        shAddEmployee.getIdentityNo(), shAddEmployee.getIdentityNoType(), null,
                        null, shAddEmployee.getSocialSecurityBase(),
                        shAddEmployee.getSocialSecurityBase(), shAddEmployee.getMobile(), shAddEmployee.getWelfareHandler(),
                        shAddEmployee.getBirthday(), operateLeader.getSocialSecurityLeader(), employeeOrderFormId, "待办",
                        shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName(),
                        shAddEmployee.getSubordinateDepartment(), shAddEmployee.getCityName(),
                        shAddEmployee.getInductionRemark());
                String sId = createSocialSecurityDeclare(sDeclare, shAddEmployee.getCreater(), shAddEmployee.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(sId,
                        employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
            }

            //创建公积金申报
            if (shAddEmployee.getProvidentFundStartTime() != null) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }

                ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", shAddEmployee.getCreater(),
                        shAddEmployee.getCreatedDeptId(), shAddEmployee.getCreatedTime(), shAddEmployee.getOwner(),
                        shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode(), employeeOrderFormId,
                        shAddEmployee.getEmployeeName(), shAddEmployee.getGender(), shAddEmployee.getBirthday(),
                        shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), shAddEmployee.getWelfareHandler(),
                        shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(), corporatePayment,
                        personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                        shAddEmployee.getCityName(), shAddEmployee.getFirstLevelClientName(),
                        shAddEmployee.getSecondLevelClientName(), shAddEmployee.getSubordinateDepartment());
                String gId = createProvidentFundDeclare(gDeclare, shAddEmployee.getCreater(), shAddEmployee.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(gId,
                        employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
            }

            return this.getOkResponseResult("success", "增员提交成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_全国提交
     *     1.创建员工档案；2.创建入职通知；3.创建员工订单；4.创建社保申报，公积金申报
     * @param id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/11 11:11
     */
    @GetMapping("/qgAddEmployeeSubmit")
    @ResponseBody
    public ResponseResult <String> qgAddEmployeeSubmit(String id) {

        try {
            NationwideDispatch nationwideDispatch = employeeFilesService.getNationwideAddEmployeeData(id);
            if (nationwideDispatch == null) {
                log.error("没有获取到增员数据！");
                return this.getErrResponseResult("error", 404l, "没有获取到增员数据！");
            }

            // 判断是否是六安，是基数四舍五入取整
            nationwideDispatch = CommonUtils.needBaseRounding(nationwideDispatch);
            nationwideDispatch = CommonUtils.processingIdentityNo(nationwideDispatch);

            EmployeeFiles employeeFiles = new EmployeeFiles("COMPLETED",nationwideDispatch.getCreater(),
                    nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime(), nationwideDispatch.getOwner(),
                    nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(), nationwideDispatch.getEmployeeName(),
                    nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(), nationwideDispatch.getGender(),
                    nationwideDispatch.getBirthday(), "代理", null,
                    nationwideDispatch.getContactNumber(), nationwideDispatch.getInvolved(), nationwideDispatch.getInvolved(),
                    nationwideDispatch.getCreatedTime(), "[{\"id\":\"" + nationwideDispatch.getCreater() + "\",\"type\":3}]",
                    nationwideDispatch.getEntryDate(), nationwideDispatch.getSServiceFeeStartDate(),
                    nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getRemark(), nationwideDispatch.getEmployeeEmail(), 0, 0,
                    nationwideDispatch.getSubordinateDepartment(), nationwideDispatch.getFirstLevelClientName(),
                    nationwideDispatch.getSecondLevelClientName(), nationwideDispatch.getHouseholdRegisterRemarks(),
                    nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getProvidentFundAmount(),
                    nationwideDispatch.getWelfareHandler(), nationwideDispatch.getWelfareHandler(), nationwideDispatch.getIsRetiredSoldier(),
                    nationwideDispatch.getIsPoorArchivists(), nationwideDispatch.getIsDisabled());

            // 查询征缴规则
            CollectionRule collectionRule = collectionRuleService.getCollectionRuleByCity(nationwideDispatch.getInvolved());
            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(nationwideDispatch.getFirstLevelClientName(),
                    nationwideDispatch.getSecondLevelClientName());
            int timeNode = 0;
            if (ccps != null) {
                timeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(nationwideDispatch.getInvolved())) {
                    timeNode = employeeMaintainService.getTimeNode(nationwideDispatch.getInvolved());
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(timeNode, new Date(), nationwideDispatch.getSServiceFeeStartDate());
            int gMonth = getMonthDifference(timeNode, new Date(), nationwideDispatch.getGServiceFeeStartDate());
            // 是否入职通知
            BizObjectModel entryNotice = employeeMaintainService.getEntryNotice(nationwideDispatch, sMonth, gMonth,
                    collectionRule);
            if(entryNotice == null) {
                employeeFiles.setEntryNotice("否");
            } else {
                employeeFiles.setEntryNotice("是");
            }
            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(nationwideDispatch.getInvolved(),
                    nationwideDispatch.getWelfareHandler(), nationwideDispatch.getSecondLevelClientName());

            if (entryNotice != null) {
                entryNotice.getData().put("operate_signatory", operateLeader.getSocialSecurityLeader() == null ?
                        operateLeader.getProvidentFundLeader() : operateLeader.getSocialSecurityLeader());
            }

            // 生成员工档案数据
            String employeeFilesId = createEmployeeFiles(employeeFiles, nationwideDispatch.getCreater());
            // 生成入职通知
            if (entryNotice != null) {
                createEntryNotice(entryNotice, nationwideDispatch.getCreater(), nationwideDispatch.getCreatedDeptId());
            }

            // 查询服务费
            Double serviceFee = salesContractService.getFee(nationwideDispatch.getSecondLevelClientName(), "代理",
                    nationwideDispatch.getInvolved());

            /** 员工订单实体*/
            String providentFundRatio = nationwideDispatch.getProvidentFundRatio();
            Double companyRatio = 0.0;
            Double employeeRatio = 0.0;
            if (StringUtils.isNotBlank(providentFundRatio)) {
                String[] ratioArr = providentFundRatio.split("\\+");
                companyRatio = StringUtils.isNotBlank(ratioArr[0]) ? Double.parseDouble(ratioArr[0]) : 0.0;
                employeeRatio = StringUtils.isNotBlank(ratioArr[1]) ? Double.parseDouble(ratioArr[1]) : 0.0;
            }
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    nationwideDispatch.getInvolved(), nationwideDispatch.getSServiceFeeStartDate(),
                    nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getInvolved(),
                    nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                    companyRatio, employeeRatio, sMonth, gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    employeeFilesId, nationwideDispatch.getWelfareHandler(), nationwideDispatch.getWelfareHandler(),
                    nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(), "代理",
                    nationwideDispatch.getFirstLevelClientName(), nationwideDispatch.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, nationwideDispatch.getCreater(), nationwideDispatch.getOwner(),
                    nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode());
            //创建社保申报
            if (nationwideDispatch.getSServiceFeeStartDate() != null) {
                SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING",nationwideDispatch.getCreater(),
                        nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime(),  nationwideDispatch.getOwner(),
                        nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(),
                        nationwideDispatch.getSServiceFeeStartDate(), nationwideDispatch.getEmployeeName(),
                        nationwideDispatch.getGender(), nationwideDispatch.getIdentityNo(),
                        nationwideDispatch.getIdentityNoType(), null, null,
                        nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getSocialInsuranceAmount(),
                        nationwideDispatch.getContactNumber(), nationwideDispatch.getWelfareHandler(),
                        nationwideDispatch.getBirthday(), operateLeader.getSocialSecurityLeader(),
                        employeeOrderFormId, "待办", nationwideDispatch.getFirstLevelClientName(),
                        nationwideDispatch.getSecondLevelClientName(), nationwideDispatch.getSubordinateDepartment(),
                        nationwideDispatch.getInvolved(), nationwideDispatch.getRemark());
                String sId = createSocialSecurityDeclare(sDeclare, nationwideDispatch.getCreater(), nationwideDispatch.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(sId,
                        employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
            }

            //创建公积金申报
            if (nationwideDispatch.getGServiceFeeStartDate() != null) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }

                ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", nationwideDispatch.getCreater(),
                        nationwideDispatch.getCreatedDeptId(), nationwideDispatch.getCreatedTime(), nationwideDispatch.getOwner(),
                        nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode(), employeeOrderFormId,
                        nationwideDispatch.getEmployeeName(), nationwideDispatch.getGender(), nationwideDispatch.getBirthday(),
                        nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(), nationwideDispatch.getWelfareHandler(),
                        nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(), corporatePayment,
                        personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                        nationwideDispatch.getInvolved(), nationwideDispatch.getFirstLevelClientName(),
                        nationwideDispatch.getSecondLevelClientName(), nationwideDispatch.getSubordinateDepartment());
                String gId = createProvidentFundDeclare(gDeclare, nationwideDispatch.getCreater(), nationwideDispatch.getCreatedDeptId());
                // 创建子表数据
                employeeMaintainService.createSocialSecurityFundDetail(gId,
                        employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
            }

            return this.getOkResponseResult("success", "增员提交成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：修改员工订单状态
     * @param ids 员工订单id,多个id用“,”隔开
     * @param field 修改的字段
     * @param status 修改后的状态
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/17 9:34
     */
    @GetMapping("/updateEmployeeOrderFormStatus")
    @ResponseBody
    public ResponseResult<String> updateEmployeeOrderFormStatus(String ids, String field, String status) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到订单id,修改员工订单状态失败。");
        }
        if (StringUtils.isBlank(field)) {
            return this.getErrResponseResult("error", 404l, "没有获取到修改字段名称,修改员工订单状态失败。");
        }
        if (StringUtils.isBlank(status)) {
            return this.getErrResponseResult("error", 404l, "没有获取到修改后的状态,修改员工订单状态失败。");
        }
        try {
            employeeMaintainService.updateEmployeeOrderFormStatus(ids, field, status);
            return this.getOkResponseResult("success", "修改员工订单状态成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：社保，公积金运行提交时重新生成员工订单数据
     * @param id 社保申报，公积金申报表单id
     * @param employeeOrderFormId 员工订单表单id
     * @param type 类型(社保办理成功：social_security；公积金办理成功：provident_fund；)
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/2/17 15:59
     */
    @GetMapping("/createNewEmployeeOrderForm")
    @ResponseBody
    public ResponseResult<String> createNewEmployeeOrderForm(String id, String employeeOrderFormId, String type) {
        try {
            employeeMaintainService.createNewEmployeeOrderForm(this.getBizObjectFacade(), id, employeeOrderFormId,
                    type);
            return this.getOkResponseResult("success", "重新生成员工订单数据成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：批量提交接口
     * @param ids 表单id,多个id使用“,”隔开
     * @param code 表单编码
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    @GetMapping("/batchSubmit")
    @ResponseBody
    public ResponseResult<String> batchSubmit(String ids, String code) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到id,操作失败。");
        }
        if (StringUtils.isBlank(code)) {
            return this.getErrResponseResult("error", 404l, "没有获取到表单编码,操作失败。");
        }
        try {
            employeeMaintainService.batchSubmit(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), this.getUserId(), ids, code);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：批量驳回接口
     * @param ids 表单id，多个id使用“,”隔开
     * @param code 表单编码
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    @GetMapping("/batchReject")
    @ResponseBody
    public ResponseResult<String> batchReject(String ids, String code) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到id,操作失败。");
        }
        if (StringUtils.isBlank(code)) {
            return this.getErrResponseResult("error", 404l, "没有获取到表单编码,操作失败。");
        }
        try {
            employeeMaintainService.batchReject(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), this.getUserId(), ids, code);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_客户修改表单审核提交
     * @param id 减员_客户修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 8:59
     */
    @GetMapping("/deleteEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> deleteEmployeeUpdateSubmit(String id) {
        try {
            DeleteEmployee delUpdate = updateEmployeeService.getDeleteEmployeeUpdateById(id);
            if (delUpdate == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            DeleteEmployee del = employeeFilesService.getDeleteEmployeeData(delUpdate.getDeleteEmployeeId());
            if (del == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应的减员表单数据！");
            }
            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(del.getIdentityNo(),
                            del.getFirstLevelClientName(), del.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", delUpdate.getLeaveTime(),
                    delUpdate.getSocialSecurityEndTime(), delUpdate.getProvidentFundEndTime(),
                    delUpdate.getLeaveReason(), delUpdate.getRemark());
            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            boolean sIsOut = false, gIsOut = false;
            if (StringUtils.isNotBlank(delUpdate.getSocialSecurityCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delUpdate.getSocialSecurityCity()) < 0) {
                sIsOut = true;
            }
            if (StringUtils.isNotBlank(delUpdate.getProvidentFundCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delUpdate.getProvidentFundCity()) < 0) {
                gIsOut = true;
            }
            // 获取订单数据
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            // 获取社保停缴数据
            SocialSecurityClose securityClose = new SocialSecurityClose();
            // 获取公积金停缴数据
            ProvidentFundClose fundClose = new ProvidentFundClose();
            if (employeeOrderForm != null) {
                securityClose = getOldSocialSecurityCloseByOrderFormId(employeeOrderForm.getId());
                fundClose = getOldProvidentFundCloseByOrderFormId(employeeOrderForm.getId());
            }
            // 运行负责人
            OperateLeader operateLeader = getOperateLeader(delUpdate.getSocialSecurityCity(),
                    delUpdate.getSWelfareHandler(), delUpdate.getProvidentFundCity(), delUpdate.getGWelfareHandler(),
                    delUpdate.getSecondLevelClientName());

            if (sIsOut && gIsOut) {
                if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                    // 删除
                    this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA,
                            securityClose.getId());
                }
                if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                    // 删除
                    this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA,
                            fundClose.getId());
                }
                employeeMaintainService.updateDeleteEmployee(delUpdate.getId(), delUpdate.getDeleteEmployeeId());
                return this.getOkResponseResult("success", "操作成功!");
            }

            if (!sIsOut && employeeFiles.getSocialSecurityChargeEnd() != null) {
                if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                    // 更新
                    securityClose = getChangeValue(securityClose, employeeFiles.getEmployeeName(),
                            employeeFiles.getGender(), employeeFiles.getBirthDate(), employeeFiles.getIdType(),
                            employeeFiles.getIdNo(), delUpdate.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                            operateLeader.getSocialSecurityLeader(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), delUpdate.getSocialSecurityCity());
                    employeeMaintainService.updateSocialSecurityClose(securityClose);
                } else {
                    // 有社保停缴申请
                    SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), delUpdate.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                            operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), del.getSubordinateDepartment(),
                            delUpdate.getSocialSecurityCity());
                    // 社保停缴
                    createSocialSecurityClose(sClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA,
                        securityClose.getId());
            }

            // 获取公积金停缴数据
            if (!gIsOut && employeeFiles.getProvidentFundChargeEnd() != null) {
                if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                    // 更新
                    fundClose = getChangeValue(fundClose,  employeeFiles.getEmployeeName(), employeeFiles.getGender(),
                            employeeFiles.getBirthDate(), employeeFiles.getIdType(), employeeFiles.getIdNo(),
                            employeeFiles.getFirstLevelClientName(), employeeFiles.getSecondLevelClientName(),
                            delUpdate.getGWelfareHandler(), employeeFiles.getProvidentFundChargeStart(),
                            employeeFiles.getProvidentFundChargeEnd(), employeeFiles.getProvidentFundBase(),
                            operateLeader.getProvidentFundLeader(), delUpdate.getProvidentFundCity());

                    employeeMaintainService.updateProvidentFundClose(fundClose);
                } else {
                    // 有公积金停缴
                    SocialSecurityFundDetail detail =
                            employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                    Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                    if (detail != null) {
                        enterpriseDeposit = detail.getCompanyMoney();
                        personalDeposit = detail.getEmployeeMoney();
                        totalDeposit = detail.getSum();
                    }
                    ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), delUpdate.getGWelfareHandler(),
                            employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                            employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                            operateLeader.getProvidentFundLeader(), "待办", del.getSubordinateDepartment(),
                            delUpdate.getProvidentFundCity());
                    // 公积金停缴
                    createProvidentFundClose(gClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA,
                        fundClose.getId());
            }

            employeeMaintainService.updateDeleteEmployee(delUpdate.getId(), delUpdate.getDeleteEmployeeId());

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_上海修改表单审核提交
     * @param id 减员_上海预修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 8:59
     */
    @GetMapping("/shDeleteEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> shDeleteEmployeeUpdateSubmit(String id) {
        try {
            ShDeleteEmployee delUpdate = updateEmployeeService.getShDeleteEmployeeUpdateById(id);
            if (delUpdate == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            ShDeleteEmployee del = employeeFilesService.getShDeleteEmployeeData(delUpdate.getShDeleteEmployeeId());
            if (del == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应的减员表单数据！");
            }
            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(del.getIdentityNo(),
                            del.getFirstLevelClientName(), del.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", delUpdate.getDepartureTime(),
                    delUpdate.getChargeEndTime(), delUpdate.getChargeEndTime(), delUpdate.getLeaveReason(),
                    delUpdate.getLeaveRemark());
            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            // 员工订单实体
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            if (employeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应的员工订单数据！");
            }

            // 获取社保停缴数据
            SocialSecurityClose securityClose = getOldSocialSecurityCloseByOrderFormId(employeeOrderForm.getId());
            // 获取公积金停缴数据
            ProvidentFundClose fundClose = getOldProvidentFundCloseByOrderFormId(employeeOrderForm.getId());
            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(employeeFiles.getSocialSecurityCity(),
                    employeeFiles.getSWelfareHandler(), employeeFiles.getSecondLevelClientName());

            if (employeeFiles.getSocialSecurityChargeEnd() != null) {
                if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                    // 更新
                    securityClose = getChangeValue(securityClose, employeeFiles.getEmployeeName(),
                            employeeFiles.getGender(), employeeFiles.getBirthDate(), employeeFiles.getIdType(),
                            employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                            operateLeader.getSocialSecurityLeader(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), employeeFiles.getSocialSecurityCity());
                    employeeMaintainService.updateSocialSecurityClose(securityClose);
                } else {
                    // 有社保停缴申请
                    SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                            operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), del.getSubordinateDepartment(),
                            employeeFiles.getSocialSecurityCity());
                    // 社保停缴
                    createSocialSecurityClose(sClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA,
                        securityClose.getId());
            }

            // 获取公积金停缴数据
            if (employeeFiles.getProvidentFundChargeEnd() != null) {
                if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                    // 更新
                    fundClose = getChangeValue(fundClose,  employeeFiles.getEmployeeName(), employeeFiles.getGender(),
                            employeeFiles.getBirthDate(), employeeFiles.getIdType(), employeeFiles.getIdNo(),
                            employeeFiles.getFirstLevelClientName(), employeeFiles.getSecondLevelClientName(),
                            employeeFiles.getGWelfareHandler(), employeeFiles.getProvidentFundChargeStart(),
                            employeeFiles.getProvidentFundChargeEnd(), employeeFiles.getProvidentFundBase(),
                            operateLeader.getProvidentFundLeader(), employeeFiles.getProvidentFundCity());

                    employeeMaintainService.updateProvidentFundClose(fundClose);
                } else {
                    // 有公积金停缴
                    SocialSecurityFundDetail detail =
                            employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                    Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                    if (detail != null) {
                        enterpriseDeposit = detail.getCompanyMoney();
                        personalDeposit = detail.getEmployeeMoney();
                        totalDeposit = detail.getSum();
                    }
                    ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), employeeFiles.getGWelfareHandler(),
                            employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                            employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                            operateLeader.getProvidentFundLeader(), "待办", del.getSubordinateDepartment(),
                            employeeFiles.getProvidentFundCity());
                    // 公积金停缴
                    createProvidentFundClose(gClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA,
                        fundClose.getId());
            }

            employeeMaintainService.updateShDeleteEmployee(delUpdate.getId(), delUpdate.getShDeleteEmployeeId());
            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员_全国修改表单审核提交
     * @param id 减员_全国预修改数据表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 13:57
     */
    @GetMapping("/qgDeleteEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> qgDeleteEmployeeUpdateSubmit(String id) {
        try {
            NationwideDispatch delUpdate = updateEmployeeService.getQgDeleteEmployeeUpdateById(id);
            if (delUpdate == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            NationwideDispatch del = employeeFilesService.getNationwideDeleteEmployeeData(delUpdate.getNationwideDispatchDelId());
            if (del == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应的减员表单数据！");
            }
            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(del.getIdentityNo(),
                            del.getFirstLevelClientName(), del.getSecondLevelClientName());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", delUpdate.getDepartureDate(),
                    delUpdate.getSServiceFeeEndDate(), delUpdate.getGServiceFeeEndDate(), delUpdate.getSocialSecurityStopReason(),
                    delUpdate.getDepartureRemark());
            // 更新员工档案数据
            employeeMaintainService.updateEmployeeFiles(employeeFiles);

            // 员工订单实体
            EmployeeOrderForm employeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            if (employeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应的员工订单数据！");
            }
            // 获取社保停缴数据
            SocialSecurityClose securityClose = getOldSocialSecurityCloseByOrderFormId(employeeOrderForm.getId());
            // 获取公积金停缴数据
            ProvidentFundClose fundClose = getOldProvidentFundCloseByOrderFormId(employeeOrderForm.getId());

            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(employeeFiles.getSocialSecurityCity(),
                    employeeFiles.getSWelfareHandler(), employeeFiles.getSecondLevelClientName());

            if (employeeFiles.getSocialSecurityChargeEnd() != null) {
                if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                    // 更新
                    securityClose = getChangeValue(securityClose, employeeFiles.getEmployeeName(),
                            employeeFiles.getGender(), employeeFiles.getBirthDate(), employeeFiles.getIdType(),
                            employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason(),
                            operateLeader.getSocialSecurityLeader(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), employeeFiles.getSocialSecurityCity());
                    employeeMaintainService.updateSocialSecurityClose(securityClose);
                } else {
                    // 有社保停缴申请
                    // 有社保停缴申请
                    SocialSecurityClose sClose = new SocialSecurityClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getSWelfareHandler(),
                            employeeFiles.getSocialSecurityChargeStart(), employeeFiles.getSocialSecurityChargeEnd(),
                            employeeFiles.getSocialSecurityBase(), employeeFiles.getQuitReason() + employeeFiles.getQuitRemark(),
                            operateLeader.getSocialSecurityLeader(), "待办", employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), del.getSubordinateDepartment(),
                            employeeFiles.getSocialSecurityCity());
                    // 社保停缴
                    createSocialSecurityClose(sClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (securityClose != null && StringUtils.isNotBlank(securityClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA,
                        securityClose.getId());
            }

            // 获取公积金停缴数据
            if (employeeFiles.getProvidentFundChargeEnd() != null) {
                if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                    // 更新
                    fundClose = getChangeValue(fundClose,  employeeFiles.getEmployeeName(), employeeFiles.getGender(),
                            employeeFiles.getBirthDate(), employeeFiles.getIdType(), employeeFiles.getIdNo(),
                            employeeFiles.getFirstLevelClientName(), employeeFiles.getSecondLevelClientName(),
                            employeeFiles.getGWelfareHandler(), employeeFiles.getProvidentFundChargeStart(),
                            employeeFiles.getProvidentFundChargeEnd(), employeeFiles.getProvidentFundBase(),
                            operateLeader.getProvidentFundLeader(), employeeFiles.getProvidentFundCity());

                    employeeMaintainService.updateProvidentFundClose(fundClose);
                } else {
                    // 有公积金停缴
                    SocialSecurityFundDetail detail =
                            employeeMaintainService.getSocialSecurityFundDetail(employeeOrderForm.getId(), "公积金");
                    Double enterpriseDeposit = null, personalDeposit = null, totalDeposit = null;
                    if (detail != null) {
                        enterpriseDeposit = detail.getCompanyMoney();
                        personalDeposit = detail.getEmployeeMoney();
                        totalDeposit = detail.getSum();
                    }
                    // 有公积金停缴
                    ProvidentFundClose gClose = new ProvidentFundClose("PROCESSING",del.getCreater(),
                            del.getCreatedDeptId(), del.getCreatedTime(), del.getOwner(),
                            del.getOwnerDeptId(), del.getOwnerDeptQueryCode(), employeeOrderForm.getId(),
                            employeeFiles.getEmployeeName(), employeeFiles.getGender(), employeeFiles.getBirthDate(),
                            employeeFiles.getIdType(), employeeFiles.getIdNo(), employeeFiles.getFirstLevelClientName(),
                            employeeFiles.getSecondLevelClientName(), employeeFiles.getGWelfareHandler(),
                            employeeFiles.getProvidentFundChargeStart(), employeeFiles.getProvidentFundChargeEnd(),
                            employeeFiles.getProvidentFundBase(), enterpriseDeposit, personalDeposit, totalDeposit,
                            operateLeader.getProvidentFundLeader(), "待办", del.getSubordinateDepartment(),
                            employeeFiles.getProvidentFundCity());
                    // 公积金停缴
                    createProvidentFundClose(gClose, del.getCreater(), del.getCreatedDeptId());
                }
            } else if (fundClose != null && StringUtils.isNotBlank(fundClose.getId())) {
                // 删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA,
                        fundClose.getId());
            }
            employeeMaintainService.updateQgAddEmployee(delUpdate.getId(), delUpdate.getNationwideDispatchDelId(),
                    "del");

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：员工档案数据修改审核提交
     * @param id 员工档案修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 16:47
     */
    @GetMapping("/employeeFilesUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> employeeFilesUpdateSubmit(String id) {
        try {
            EmployeeFiles employeeFiles = updateEmployeeService.getEmployeeFilesUpdateById(id);
            if (employeeFiles == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            employeeMaintainService.employeeFilesUpdateSubmit(employeeFiles);
            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_客户修改表单审核提交
     * @param id 增员_客户预修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 8:59
     */
    @GetMapping("/addEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> addEmployeeUpdateSubmit(String id) {
        try {
            AddEmployee addEmployee = updateEmployeeService.getAddEmployeeUpdateById(id);
            if (addEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            // 判断是否是六安，是基数四舍五入取整
            // 判断是否是六安，是基数四舍五入取整
            addEmployee = CommonUtils.needBaseRounding(addEmployee);
            addEmployee = CommonUtils.processingIdentityNo(addEmployee);
            // 获取原增员表单
            AddEmployee oldAddEmployee = addEmployeeService.getAddEmployeeById(addEmployee.getAddEmployeeId());
            if (oldAddEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            // 获取原员工档案
            EmployeeFiles oldEmployeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(oldAddEmployee.getIdentityNo(),
                            oldAddEmployee.getFirstLevelClientName(), oldAddEmployee.getSecondLevelClientName());

            // 原员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(oldEmployeeFiles.getId());
            if (oldEmployeeOrderForm == null) {
                oldEmployeeOrderForm = new EmployeeOrderForm();
            }
            // 原社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    getOldSocialSecurityDeclareByOrderFormId(oldEmployeeOrderForm.getId());
            // 原公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    getOldProvidentFundDeclareByOrderFormId(oldEmployeeOrderForm.getId());

            // 基本信息还是原来的数据
            addEmployee.setCreatedTime(oldAddEmployee.getCreatedTime());
            addEmployee.setCreater(oldAddEmployee.getCreater());
            addEmployee.setCreatedDeptId(oldAddEmployee.getCreatedDeptId());
            addEmployee.setOwner(oldAddEmployee.getOwner());
            addEmployee.setOwnerDeptId(oldAddEmployee.getOwnerDeptId());
            addEmployee.setOwnerDeptQueryCode(oldAddEmployee.getOwnerDeptQueryCode());

            /** 员工档案*/
            oldEmployeeFiles = getChangeValue(oldEmployeeFiles, addEmployee.getEmployeeName(),
                    addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getGender(),
                    addEmployee.getBirthday(), addEmployee.getEmployeeNature(), addEmployee.getFamilyRegisterNature(),
                    addEmployee.getMobile(), addEmployee.getSocialSecurityCity(), addEmployee.getProvidentFundCity(),
                    addEmployee.getSocialSecurityStartTime(), addEmployee.getProvidentFundStartTime(),
                    addEmployee.getEmail(), addEmployee.getHouseholdRegisterRemarks(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundBase(),
                    addEmployee.getSWelfareHandler(), addEmployee.getGWelfareHandler(),
                    addEmployee.getIsRetiredSoldier(), addEmployee.getIsPoorArchivists(), addEmployee.getIsDisabled());

            employeeMaintainService.updateEmployeeFiles(oldEmployeeFiles);

            boolean sIsOut = false, gIsOut = false;
            if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getSocialSecurityCity()) < 0) {
                sIsOut = true;
            }
            if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity()) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getProvidentFundCity()) < 0) {
                gIsOut = true;
            }

            if (gIsOut && sIsOut) {
                // 省外数据
                if(StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())){
                    this.getBizObjectFacade().removeBizObject(this.getUserId(),
                            Constants.PROVIDENT_FUND_DECLARE_SCHEMA, oldProvidentFundDeclare.getId());
                }
                if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                    this.getBizObjectFacade().removeBizObject(this.getUserId(),
                            Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, oldSocialSecurityDeclare.getId());
                }
                if (StringUtils.isNotBlank(oldEmployeeOrderForm.getId())) {
                    this.getBizObjectFacade().removeBizObject(this.getUserId(),
                            Constants.EMPLOYEE_ORDER_FORM_SCHEMA, oldEmployeeOrderForm.getId());
                }
                employeeMaintainService.updateAddEmployee(addEmployee.getId(), addEmployee.getAddEmployeeId());
                return this.getOkResponseResult("success", "操作成功");
            }
            // 有省内数据
            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName());
            int sTimeNode=0,gTimeNode=0;
            if (ccps != null) {
                sTimeNode = ccps.getTimeNode();
                gTimeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity())) {
                    sTimeNode = employeeMaintainService.getTimeNode(addEmployee.getSocialSecurityCity());
                }
                if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity())) {
                    if (addEmployee.getProvidentFundCity().equals(addEmployee.getSocialSecurityCity())) {
                        gTimeNode = sTimeNode;
                    } else {
                        gTimeNode = employeeMaintainService.getTimeNode(addEmployee.getProvidentFundCity());
                    }
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(sTimeNode, new Date(), addEmployee.getSocialSecurityStartTime());
            int gMonth = getMonthDifference(gTimeNode, new Date(), addEmployee.getProvidentFundStartTime());

            // 运行负责人
            OperateLeader operateLeader = getOperateLeader(addEmployee.getSocialSecurityCity(),
                    addEmployee.getSWelfareHandler(), addEmployee.getProvidentFundCity(), addEmployee.getGWelfareHandler(),
                    addEmployee.getSecondLevelClientName());

            // 查询服务费
            Double serviceFee = salesContractService.getFee(addEmployee.getSecondLevelClientName(),
                    addEmployee.getEmployeeNature(), StringUtils.isNotBlank(addEmployee.getSocialSecurityCity()) ?
                            addEmployee.getSocialSecurityCity() : addEmployee.getProvidentFundCity());

            // 更新订单
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    addEmployee.getSocialSecurityCity(), addEmployee.getSocialSecurityStartTime(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundCity(),
                    addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl(),
                    sMonth, gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    oldEmployeeFiles.getId(), addEmployee.getSWelfareHandler(), addEmployee.getGWelfareHandler(),
                    addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getEmployeeNature(),
                    addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, addEmployee.getCreater(), addEmployee.getOwner(), addEmployee.getOwnerDeptId()
                    , addEmployee.getOwnerDeptQueryCode());
            if (StringUtils.isNotBlank(oldEmployeeOrderForm.getId())) {
                // 原订单改为历史表单
                employeeMaintainService.updateEmployeeOrderFormIsHistory(oldEmployeeOrderForm.getId());
                // 修改申报表单订单id
                employeeMaintainService.updateDeclareEmployeeOrderFormId(oldEmployeeOrderForm.getId(), employeeOrderFormId);
            }
            // 有社保申报
            if (!sIsOut && addEmployee.getSocialSecurityStartTime() != null) {
                if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                    // 更新
                    oldSocialSecurityDeclare = getChangeValue(oldSocialSecurityDeclare,
                            addEmployee.getSocialSecurityStartTime(), addEmployee.getEmployeeName(), addEmployee.getGender(),
                            addEmployee.getIdentityNo(), addEmployee.getIdentityNoType(),
                            addEmployee.getContractStartTime(), addEmployee.getContractEndTime(), addEmployee.getContractSalary(),
                            addEmployee.getSocialSecurityBase(), addEmployee.getMobile(), addEmployee.getSWelfareHandler(),
                            addEmployee.getBirthday(), operateLeader.getSocialSecurityLeader(), employeeOrderFormId,
                            oldSocialSecurityDeclare.getStatus(), addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName(),
                            addEmployee.getSocialSecurityCity(), addEmployee.getRemark());
                    oldSocialSecurityDeclare.setSocialSecurityDetail(employeeOrderForm.getSocialSecurityDetail());

                    employeeMaintainService.updateSocialSecurityDeclare(oldSocialSecurityDeclare);
                } else {
                    // 新增
                    SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING", oldAddEmployee.getCreater(),
                            oldAddEmployee.getCreatedDeptId(), oldAddEmployee.getCreatedTime(),
                            oldAddEmployee.getOwner(), oldAddEmployee.getOwnerDeptId(),
                            oldAddEmployee.getOwnerDeptQueryCode(), addEmployee.getSocialSecurityStartTime(),
                            addEmployee.getEmployeeName(), addEmployee.getGender(), addEmployee.getIdentityNo(),
                            addEmployee.getIdentityNoType(), addEmployee.getContractStartTime(),
                            addEmployee.getContractEndTime(), addEmployee.getContractSalary(),
                            addEmployee.getSocialSecurityBase(), addEmployee.getMobile(), addEmployee.getSWelfareHandler(),
                            addEmployee.getBirthday(), operateLeader.getSocialSecurityLeader(), employeeOrderFormId,
                            "待办",
                            addEmployee.getFirstLevelClientName(), addEmployee.getSecondLevelClientName(),
                            oldAddEmployee.getSubordinateDepartment(), addEmployee.getSocialSecurityCity(),
                            addEmployee.getRemark());
                    String sId = createSocialSecurityDeclare(sDeclare, oldAddEmployee.getCreater(), oldAddEmployee.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(sId,
                            employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_DECLARE_SCHEMA,
                        oldSocialSecurityDeclare.getId());
            }
            // 有公积金申报
            if (!gIsOut && addEmployee.getProvidentFundStartTime() != null) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }
                if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                    // 更新
                    oldProvidentFundDeclare = getChangeValue(oldProvidentFundDeclare, employeeOrderFormId,
                            addEmployee.getEmployeeName(), addEmployee.getGender(), addEmployee.getBirthday(),
                            addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getGWelfareHandler(),
                            addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(), corporatePayment,
                            personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), oldSocialSecurityDeclare.getStatus(),
                            addEmployee.getProvidentFundCity(), addEmployee.getFirstLevelClientName(),
                            addEmployee.getSecondLevelClientName());
                    oldProvidentFundDeclare.setProvidentFundDetail(employeeOrderForm.getProvidentFundDetail());

                    employeeMaintainService.updateProvidentFundDeclare(oldProvidentFundDeclare);
                } else {
                    // 新增
                    ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", oldAddEmployee.getCreater(),
                            oldAddEmployee.getCreatedDeptId(), oldAddEmployee.getCreatedTime(), oldAddEmployee.getOwner(),
                            oldAddEmployee.getOwnerDeptId(), oldAddEmployee.getOwnerDeptQueryCode(), employeeOrderFormId,
                            addEmployee.getEmployeeName(), addEmployee.getGender(), addEmployee.getBirthday(),
                            addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(), addEmployee.getGWelfareHandler(),
                            addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(), corporatePayment,
                            personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                            addEmployee.getProvidentFundCity(), addEmployee.getFirstLevelClientName(),
                            addEmployee.getSecondLevelClientName(), oldAddEmployee.getSubordinateDepartment());

                    String gId = createProvidentFundDeclare(gDeclare, oldAddEmployee.getCreater(), oldAddEmployee.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(gId,
                            employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                        oldEmployeeOrderForm.getId());
            }

            employeeMaintainService.updateAddEmployee(addEmployee.getId(), addEmployee.getAddEmployeeId());
            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_上海修改表单审核提交
     * @param id 增员_上海预修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 8:59
     */
    @GetMapping("/shAddEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> shAddEmployeeUpdateSubmit(String id) {
        try {
            ShAddEmployee shAddEmployee = updateEmployeeService.getShAddEmployeeUpdateById(id);
            if (shAddEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            // 判断是否是六安，是基数四舍五入取整
            shAddEmployee = CommonUtils.needBaseRounding(shAddEmployee);
            shAddEmployee = CommonUtils.processingIdentityNo(shAddEmployee);

            if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
                shAddEmployee.setProvidentFundStartTime(shAddEmployee.getBenefitStartTime());
            }
            // 获取原增员表单
            ShAddEmployee oldShAddEmployee =
                    addEmployeeService.getShAddEmployeeById(shAddEmployee.getShAddEmployeeId());
            if (oldShAddEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            //获取员工档案
            EmployeeFiles oldEmployeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(oldShAddEmployee.getIdentityNo(),
                            oldShAddEmployee.getFirstLevelClientName(), oldShAddEmployee.getSecondLevelClientName());

            // 员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(oldEmployeeFiles.getId());
            if (oldEmployeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工订单数据！");
            }
            // 原社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    getOldSocialSecurityDeclareByOrderFormId(oldEmployeeOrderForm.getId());
            // 原公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    getOldProvidentFundDeclareByOrderFormId(oldEmployeeOrderForm.getId());

            // 基本信息还是原来的数据
            shAddEmployee.setCreatedTime(oldShAddEmployee.getCreatedTime());
            shAddEmployee.setCreater(oldShAddEmployee.getCreater());
            shAddEmployee.setCreatedDeptId(oldShAddEmployee.getCreatedDeptId());
            shAddEmployee.setOwner(oldShAddEmployee.getOwner());
            shAddEmployee.setOwnerDeptId(oldShAddEmployee.getOwnerDeptId());
            shAddEmployee.setOwnerDeptQueryCode(oldShAddEmployee.getOwnerDeptQueryCode());

            /** 员工档案 更新*/
            oldEmployeeFiles = getChangeValue(oldEmployeeFiles, shAddEmployee.getEmployeeName(),
                    shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), shAddEmployee.getGender(),
                    shAddEmployee.getBirthday(), "代理", oldEmployeeFiles.getHouseholdRegisterNature(),
                    shAddEmployee.getMobile(), shAddEmployee.getCityName(), shAddEmployee.getCityName(),
                    shAddEmployee.getBenefitStartTime(), shAddEmployee.getProvidentFundStartTime(),
                    shAddEmployee.getMail(), shAddEmployee.getHouseholdRegisterRemarks(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getWelfareHandler(), shAddEmployee.getWelfareHandler(),
                    shAddEmployee.getIsRetiredSoldier(), shAddEmployee.getIsPoorArchivists(),
                    shAddEmployee.getIsDisabled());
            employeeMaintainService.updateEmployeeFiles(oldEmployeeFiles);

            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName());
            int timeNode = 0;
            if (ccps != null) {
                timeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(shAddEmployee.getCityName())) {
                    timeNode = employeeMaintainService.getTimeNode(shAddEmployee.getCityName());
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(timeNode, new Date(), shAddEmployee.getBenefitStartTime());
            int gMonth = getMonthDifference(timeNode, new Date(), shAddEmployee.getProvidentFundStartTime());

            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(shAddEmployee.getCityName(),
                    shAddEmployee.getWelfareHandler(), shAddEmployee.getSecondLevelClientName());

            // 查询服务费
            Double serviceFee = salesContractService.getFee(shAddEmployee.getSecondLevelClientName(), "代理",
                    shAddEmployee.getCityName());

            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    shAddEmployee.getCityName(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getCityName(),
                    shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getPSupplementProvidentFund(), shAddEmployee.getUSupplementProvidentFund(), sMonth,
                    gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    oldEmployeeFiles.getId(), shAddEmployee.getWelfareHandler(), shAddEmployee.getWelfareHandler(),
                    shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), "代理",
                    shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, shAddEmployee.getCreater(), shAddEmployee.getOwner(),
                    shAddEmployee.getOwnerDeptId(), shAddEmployee.getOwnerDeptQueryCode());
            // 原订单改为历史表单
            employeeMaintainService.updateEmployeeOrderFormIsHistory(oldEmployeeOrderForm.getId());
            // 修改申报表单订单id
            employeeMaintainService.updateDeclareEmployeeOrderFormId(oldEmployeeOrderForm.getId(), employeeOrderFormId);

            if (shAddEmployee.getBenefitStartTime() != null) {
                if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                    // 更新
                    oldSocialSecurityDeclare = getChangeValue(oldSocialSecurityDeclare,
                            shAddEmployee.getBenefitStartTime(), shAddEmployee.getEmployeeName(),
                            shAddEmployee.getGender(),
                            shAddEmployee.getIdentityNo(), shAddEmployee.getIdentityNoType(),
                            oldSocialSecurityDeclare.getContractSigningDate(),
                            oldSocialSecurityDeclare.getContractDeadline(), shAddEmployee.getSocialSecurityBase(),
                            shAddEmployee.getSocialSecurityBase(), shAddEmployee.getMobile(),
                            shAddEmployee.getWelfareHandler(), shAddEmployee.getBirthday(),
                            operateLeader.getSocialSecurityLeader(), employeeOrderFormId,
                            oldSocialSecurityDeclare.getStatus(), shAddEmployee.getFirstLevelClientName(),
                            shAddEmployee.getSecondLevelClientName(), shAddEmployee.getCityName(),
                            shAddEmployee.getInductionRemark());
                    oldSocialSecurityDeclare.setSocialSecurityDetail(employeeOrderForm.getSocialSecurityDetail());

                    employeeMaintainService.updateSocialSecurityDeclare(oldSocialSecurityDeclare);
                } else {
                    // 新增
                    SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING", oldShAddEmployee.getCreater(),
                            oldShAddEmployee.getCreatedDeptId(), oldShAddEmployee.getCreatedTime(),
                            oldShAddEmployee.getOwner(), oldShAddEmployee.getOwnerDeptId(),
                            oldShAddEmployee.getOwnerDeptQueryCode(), shAddEmployee.getBenefitStartTime(),
                            shAddEmployee.getEmployeeName(), shAddEmployee.getGender(), shAddEmployee.getIdentityNo(),
                            shAddEmployee.getIdentityNoType(), null, null, shAddEmployee.getSocialSecurityBase(),
                            shAddEmployee.getSocialSecurityBase(), shAddEmployee.getMobile(),
                            shAddEmployee.getWelfareHandler(), shAddEmployee.getBirthday(),
                            operateLeader.getSocialSecurityLeader(), employeeOrderFormId, "待办",
                            shAddEmployee.getFirstLevelClientName(), shAddEmployee.getSecondLevelClientName(),
                            oldShAddEmployee.getSubordinateDepartment(), shAddEmployee.getCityName(),
                            shAddEmployee.getInductionRemark());
                    String sId = createSocialSecurityDeclare(sDeclare, oldShAddEmployee.getCreater(), oldShAddEmployee.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(sId,
                            employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_DECLARE_SCHEMA,
                        oldSocialSecurityDeclare.getId());
            }

            if (shAddEmployee.getProvidentFundStartTime() != null) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }
                if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                    // 更新
                    oldProvidentFundDeclare = getChangeValue(oldProvidentFundDeclare, employeeOrderFormId,
                            shAddEmployee.getEmployeeName(), shAddEmployee.getGender(), shAddEmployee.getBirthday(),
                            shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), shAddEmployee.getWelfareHandler(),
                            shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(), corporatePayment,
                            personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), oldSocialSecurityDeclare.getStatus(),
                            shAddEmployee.getCityName(), shAddEmployee.getFirstLevelClientName(),
                            shAddEmployee.getSecondLevelClientName());
                    oldProvidentFundDeclare.setProvidentFundDetail(employeeOrderForm.getProvidentFundDetail());

                    employeeMaintainService.updateProvidentFundDeclare(oldProvidentFundDeclare);
                } else {
                    ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", oldShAddEmployee.getCreater(),
                            oldShAddEmployee.getCreatedDeptId(), oldShAddEmployee.getCreatedTime(), oldShAddEmployee.getOwner(),
                            oldShAddEmployee.getOwnerDeptId(), oldShAddEmployee.getOwnerDeptQueryCode(), employeeOrderFormId,
                            shAddEmployee.getEmployeeName(), shAddEmployee.getGender(), shAddEmployee.getBirthday(),
                            shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(), shAddEmployee.getWelfareHandler(),
                            shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(), corporatePayment,
                            personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                            shAddEmployee.getCityName(), shAddEmployee.getFirstLevelClientName(),
                            shAddEmployee.getSecondLevelClientName(), oldShAddEmployee.getSubordinateDepartment());
                    String gId = createProvidentFundDeclare(gDeclare, oldShAddEmployee.getCreater(), oldShAddEmployee.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(gId,
                            employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                        oldEmployeeOrderForm.getId());
            }
            employeeMaintainService.updateShAddEmployee(shAddEmployee.getId(), shAddEmployee.getShAddEmployeeId());

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员_全国修改表单审核提交
     * @param id 增员_全国预修改表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/2/27 13:57
     */
    @GetMapping("/qgAddEmployeeUpdateSubmit")
    @ResponseBody
    public ResponseResult <String> qgAddEmployeeUpdateSubmit(String id) {
        try {
            NationwideDispatch nationwideDispatch = updateEmployeeService.getQgAddEmployeeUpdateById(id);
            if (nationwideDispatch == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            // 判断是否是六安，是基数四舍五入取整
            nationwideDispatch = CommonUtils.needBaseRounding(nationwideDispatch);

            nationwideDispatch = CommonUtils.processingIdentityNo(nationwideDispatch);

            // 获取原增员表单
            NationwideDispatch oldNationwideDispatch =
                    addEmployeeService.getNationwideDispatchById(nationwideDispatch.getNationwideDispatchId());
            if (oldNationwideDispatch == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            //获取员工档案
            EmployeeFiles oldEmployeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoAndClientName(oldNationwideDispatch.getIdentityNo(),
                            oldNationwideDispatch.getFirstLevelClientName(),
                            oldNationwideDispatch.getSecondLevelClientName());

            // 员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(oldEmployeeFiles.getId());
            if (oldEmployeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工订单数据！");
            }
            // 原社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    getOldSocialSecurityDeclareByOrderFormId(oldEmployeeOrderForm.getId());
            // 原公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    getOldProvidentFundDeclareByOrderFormId(oldEmployeeOrderForm.getId());

            // 基本信息还是原来的数据
            nationwideDispatch.setCreatedTime(oldNationwideDispatch.getCreatedTime());
            nationwideDispatch.setCreater(oldNationwideDispatch.getCreater());
            nationwideDispatch.setCreatedDeptId(oldNationwideDispatch.getCreatedDeptId());
            nationwideDispatch.setOwner(oldNationwideDispatch.getOwner());
            nationwideDispatch.setOwnerDeptId(oldNationwideDispatch.getOwnerDeptId());
            nationwideDispatch.setOwnerDeptQueryCode(oldNationwideDispatch.getOwnerDeptQueryCode());

            /** 员工档案 更新*/
            oldEmployeeFiles = getChangeValue(oldEmployeeFiles, nationwideDispatch.getEmployeeName(),
                    nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(),
                    nationwideDispatch.getGender(),
                    nationwideDispatch.getBirthday(), "代理", oldEmployeeFiles.getHouseholdRegisterNature(),
                    nationwideDispatch.getContactNumber(), nationwideDispatch.getInvolved(),
                    nationwideDispatch.getInvolved(),
                    nationwideDispatch.getSServiceFeeStartDate(), nationwideDispatch.getGServiceFeeStartDate(),
                    nationwideDispatch.getEmployeeEmail(), nationwideDispatch.getHouseholdRegisterRemarks(),
                    nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getProvidentFundAmount(),
                    nationwideDispatch.getWelfareHandler(), nationwideDispatch.getWelfareHandler(),
                    nationwideDispatch.getIsRetiredSoldier(), nationwideDispatch.getIsPoorArchivists(),
                    nationwideDispatch.getIsDisabled());
            employeeMaintainService.updateEmployeeFiles(oldEmployeeFiles);

            // 查询征缴规则
            CollectionRule collectionRule =
                    collectionRuleService.getCollectionRuleByCity(nationwideDispatch.getInvolved());
            // 查询客户个性化设置
            Ccps ccps = employeeMaintainService.getCcps(nationwideDispatch.getFirstLevelClientName(),
                    nationwideDispatch.getSecondLevelClientName());
            int timeNode = 0;
            if (ccps != null) {
                timeNode = ccps.getTimeNode();
            } else {
                if (StringUtils.isNotBlank(nationwideDispatch.getInvolved())) {
                    timeNode = employeeMaintainService.getTimeNode(nationwideDispatch.getInvolved());
                }
            }
            // 补缴月份
            int sMonth = getMonthDifference(timeNode, new Date(), nationwideDispatch.getSServiceFeeStartDate());
            int gMonth = getMonthDifference(timeNode, new Date(), nationwideDispatch.getGServiceFeeStartDate());

            // 运行负责人
            OperateLeader operateLeader = employeeMaintainService.getOperateLeader(nationwideDispatch.getInvolved(),
                    nationwideDispatch.getWelfareHandler(), nationwideDispatch.getSecondLevelClientName());

            // 查询服务费
            Double serviceFee = salesContractService.getFee(nationwideDispatch.getSecondLevelClientName(), "代理",
                    nationwideDispatch.getInvolved());

            /** 员工订单实体*/
            String providentFundRatio = nationwideDispatch.getProvidentFundRatio();
            Double companyRatio = 0.0;
            Double employeeRatio = 0.0;
            if (StringUtils.isNotBlank(providentFundRatio)) {
                String[] ratioArr = providentFundRatio.split("\\+");
                companyRatio = StringUtils.isNotBlank(ratioArr[0]) ? Double.parseDouble(ratioArr[0]) : 0.0;
                employeeRatio = StringUtils.isNotBlank(ratioArr[1]) ? Double.parseDouble(ratioArr[1]) : 0.0;
            }
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    nationwideDispatch.getInvolved(), nationwideDispatch.getSServiceFeeStartDate(),
                    nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getInvolved(),
                    nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                    companyRatio, employeeRatio, sMonth, gMonth, ccps);
            // TODO: 2020/4/16  businessType待确定
            employeeOrderForm = setEmployeeOrderFormValue(employeeOrderForm, serviceFee, employeeOrderForm.getSum(),
                    oldEmployeeFiles.getId(), nationwideDispatch.getWelfareHandler(),
                    nationwideDispatch.getWelfareHandler(),
                    nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(), "代理",
                    nationwideDispatch.getFirstLevelClientName(), nationwideDispatch.getSecondLevelClientName());

            // 创建员工订单数据
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, nationwideDispatch.getCreater(), nationwideDispatch.getOwner(),
                    nationwideDispatch.getOwnerDeptId(), nationwideDispatch.getOwnerDeptQueryCode());
            // 原订单改为历史表单
            employeeMaintainService.updateEmployeeOrderFormIsHistory(oldEmployeeOrderForm.getId());
            // 修改申报表单订单id
            employeeMaintainService.updateDeclareEmployeeOrderFormId(oldEmployeeOrderForm.getId(), employeeOrderFormId);

            if (nationwideDispatch.getSServiceFeeStartDate() != null) {
                if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                    // 更新
                    oldSocialSecurityDeclare = getChangeValue(oldSocialSecurityDeclare,
                            nationwideDispatch.getSServiceFeeStartDate(), nationwideDispatch.getEmployeeName(),
                            nationwideDispatch.getGender(), nationwideDispatch.getIdentityNo(),
                            nationwideDispatch.getIdentityNoType(), oldSocialSecurityDeclare.getContractSigningDate(),
                            oldSocialSecurityDeclare.getContractDeadline(), nationwideDispatch.getSocialInsuranceAmount(),
                            nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getContactNumber(),
                            nationwideDispatch.getWelfareHandler(), nationwideDispatch.getBirthday(),
                            operateLeader.getSocialSecurityLeader(), employeeOrderFormId,
                            oldSocialSecurityDeclare.getStatus(), nationwideDispatch.getFirstLevelClientName(),
                            nationwideDispatch.getSecondLevelClientName(), nationwideDispatch.getInvolved(),
                            nationwideDispatch.getRemark());
                    oldSocialSecurityDeclare.setSocialSecurityDetail(employeeOrderForm.getSocialSecurityDetail());

                    employeeMaintainService.updateSocialSecurityDeclare(oldSocialSecurityDeclare);
                } else {
                    // 新增
                    SocialSecurityDeclare sDeclare = new SocialSecurityDeclare("PROCESSING",
                            oldNationwideDispatch.getCreater(), oldNationwideDispatch.getCreatedDeptId(),
                            oldNationwideDispatch.getCreatedTime(), oldNationwideDispatch.getOwner(),
                            oldNationwideDispatch.getOwnerDeptId(), oldNationwideDispatch.getOwnerDeptQueryCode(),
                            nationwideDispatch.getSServiceFeeStartDate(), nationwideDispatch.getEmployeeName(),
                            nationwideDispatch.getGender(), nationwideDispatch.getIdentityNo(),
                            nationwideDispatch.getIdentityNoType(), null, null,
                            nationwideDispatch.getSocialInsuranceAmount(),
                            nationwideDispatch.getSocialInsuranceAmount(),
                            nationwideDispatch.getContactNumber(), nationwideDispatch.getWelfareHandler(),
                            nationwideDispatch.getBirthday(), operateLeader.getSocialSecurityLeader(),
                            employeeOrderFormId, "待办", nationwideDispatch.getFirstLevelClientName(),
                            nationwideDispatch.getSecondLevelClientName(),
                            oldNationwideDispatch.getSubordinateDepartment(),
                            nationwideDispatch.getInvolved(), nationwideDispatch.getRemark());
                    String sId = createSocialSecurityDeclare(sDeclare, oldNationwideDispatch.getCreater(),
                            oldNationwideDispatch.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(sId,
                            employeeOrderForm.getSocialSecurityDetail(), Constants.SOCIAL_SECURITY_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldSocialSecurityDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_DECLARE_SCHEMA,
                        oldSocialSecurityDeclare.getId());
            }

            if (nationwideDispatch.getGServiceFeeStartDate() != null) {
                /** 企业缴存额, 个人缴存额, 缴存总额*/
                Double corporatePayment = null, personalDeposit = null, totalDeposit = null;
                List <Map <String, String>> details = employeeOrderForm.getProvidentFundDetail();
                if (details != null && details.size() > 0) {
                    corporatePayment = StringUtils.isNotBlank(details.get(0).get("company_money")) ?
                            Double.parseDouble(details.get(0).get("company_money")) : null;
                    personalDeposit = StringUtils.isNotBlank(details.get(0).get("employee_money")) ?
                            Double.parseDouble(details.get(0).get("employee_money")) : null;
                    totalDeposit = StringUtils.isNotBlank(details.get(0).get("sum")) ?
                            Double.parseDouble(details.get(0).get("sum")) : null;
                }
                if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                    // 更新
                    oldProvidentFundDeclare = getChangeValue(oldProvidentFundDeclare, employeeOrderFormId,
                            nationwideDispatch.getEmployeeName(), nationwideDispatch.getGender(),
                            nationwideDispatch.getBirthday(), nationwideDispatch.getIdentityNoType(),
                            nationwideDispatch.getIdentityNo(), nationwideDispatch.getWelfareHandler(),
                            nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                            corporatePayment, personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(),
                            oldSocialSecurityDeclare.getStatus(), nationwideDispatch.getInvolved(),
                            nationwideDispatch.getFirstLevelClientName(), nationwideDispatch.getSecondLevelClientName());
                    oldProvidentFundDeclare.setProvidentFundDetail(employeeOrderForm.getProvidentFundDetail());

                    employeeMaintainService.updateProvidentFundDeclare(oldProvidentFundDeclare);
                } else {
                    ProvidentFundDeclare gDeclare = new ProvidentFundDeclare("PROCESSING", oldNationwideDispatch.getCreater(),
                            oldNationwideDispatch.getCreatedDeptId(), oldNationwideDispatch.getCreatedTime(),
                            oldNationwideDispatch.getOwner(), oldNationwideDispatch.getOwnerDeptId(), oldNationwideDispatch.getOwnerDeptQueryCode(),
                            employeeOrderFormId, nationwideDispatch.getEmployeeName(), nationwideDispatch.getGender(),
                            nationwideDispatch.getBirthday(), nationwideDispatch.getIdentityNoType(),
                            nationwideDispatch.getIdentityNo(), nationwideDispatch.getWelfareHandler(),
                            nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                            corporatePayment, personalDeposit, totalDeposit, operateLeader.getProvidentFundLeader(), "待办",
                            nationwideDispatch.getInvolved(), nationwideDispatch.getFirstLevelClientName(),
                            nationwideDispatch.getSecondLevelClientName(),
                            nationwideDispatch.getSubordinateDepartment());
                    String gId = createProvidentFundDeclare(gDeclare, oldNationwideDispatch.getCreater(),
                            oldNationwideDispatch.getCreatedDeptId());
                    // 创建子表数据
                    employeeMaintainService.createSocialSecurityFundDetail(gId,
                            employeeOrderForm.getProvidentFundDetail(), Constants.PROVIDENT_FUND_DETAIL);
                }
            } else if (StringUtils.isNotBlank(oldProvidentFundDeclare.getId())) {
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                        oldEmployeeOrderForm.getId());
            }

            employeeMaintainService.updateQgAddEmployee(nationwideDispatch.getId(),
                    nationwideDispatch.getNationwideDispatchId(), "add");

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：修改员工钉钉状态为预点
     * @param ids 表单id,多个id使用“,”隔开
     * @param field 修改字段名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 14:08
     */
    @GetMapping("/updateStatusToPrePoint")
    @ResponseBody
    public ResponseResult<String> updateStatusToPrePoint(String ids, String field) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到ids！");
        }
        if (StringUtils.isBlank(field)) {
            return this.getErrResponseResult("error", 404l, "没有获取到修改字段值！");
        }
        try {
            employeeMaintainService.updateStatusToPrePoint(ids, field);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增减员批量提交
     * @param ids 表单id,多个id使用“,”隔开
     * @param type 类型（增员_客户：add；增员_上海：shAdd；增员_全国：qgAdd；减员_客户：del；减员_上海：shDel；减员_全国：qgDel；）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/26 9:50
     */
    @GetMapping("/batchAddOrDelSubmit")
    @ResponseBody
    public ResponseResult<String> batchAddOrDelSubmit(String ids, String type) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到ids！");
        }
        if (StringUtils.isBlank(type)) {
            return this.getErrResponseResult("error", 404l, "没有获取到提交类型！");
        }
        String tableName = "";
        if ("add".equals(type)) {
            tableName = "add_employee";
        } else if ("shAdd".equals(type)) {
            tableName = "sh_add_employee";
        } else if ("qgAdd".equals(type)) {
            tableName = "nationwide_dispatch";
        } else if ("del".equals(type)) {
            tableName = "delete_employee";
        } else if ("shDel".equals(type)) {
            tableName = "sh_delete_employee";
        } else if ("qgDel".equals(type)) {
            tableName = "nationwide_dispatch_delete";
        } else {
            return this.getErrResponseResult("error", 404l, "提交类型出错！");
        }
        try {
            // 获取当前表单的代办任务id
            List<Map<String, Object>> list = employeeMaintainService.getAddOrDelWorkItemId(ids, tableName);
            String userId = this.getUserId();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i ++) {
                    String id = list.get(i).get("id").toString();
                    String workItemId = list.get(i).get("workItemId").toString();
                    // 提交流程
                    this.getWorkflowInstanceFacade().submitWorkItem(this.getUserId(), workItemId, true);
                    if ("add".equals(type)) {
                        this.addEmployeeSubmit(id);
                    } else if ("shAdd".equals(type)) {
                        this.shAddEmployeeSubmit(id);
                    } else if ("qgAdd".equals(type)) {
                        this.qgAddEmployeeSubmit(id);
                    } else if ("del".equals(type)) {
                        this.deleteEmployeeSubmit(id);
                    } else if ("shDel".equals(type)) {
                        this.shDeleteEmployeeSubmit(id);
                    } else if ("qgDel".equals(type)) {
                        this.qgDeleteEmployeeSubmit(id);
                    }
                }
            }
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：申报驳回导入
     * @param fileName 文件名称
     * @param code 申报表单code(社保申报：social_security_declare；公积金申报：provident_fund_declare；)
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/27 10:01
     */
    @GetMapping("/rejectImport")
    @ResponseBody
    public ResponseResult<String> rejectImport(String fileName, String code) {
        if (StringUtils.isBlank(fileName)) {
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        try {
            // 当前用户
            String userId = this.getUserId();
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            employeeMaintainService.rejectImport(fileName, code, userId, this.getWorkflowInstanceFacade());
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：生成批量预派
     * @param addEmployee 增员客户
     * @param sIsOut 社保是省外
     * @param gIsOut 公积金是省外
     * @return void
     * @author liulei
     * @Date 2020/4/2 14:15
     */
    private void addBatchPreDispatch(AddEmployee addEmployee, boolean sIsOut, boolean gIsOut) throws Exception {
        List<BatchPreDispatch> batchPreDispatches = new ArrayList <>();
        BatchPreDispatch batchPreDispatch = new BatchPreDispatch();
        String providentFundRatio =
                "" + addEmployee.getCompanyProvidentFundBl() + "+" + addEmployee.getEmployeeProvidentFundBl();
        if (sIsOut && gIsOut) {
            // 社保，公积金都是省外
            batchPreDispatch = new BatchPreDispatch(addEmployee.getEmployeeName(), addEmployee.getIdentityNo(),
                    addEmployee.getMobile(), addEmployee.getSocialSecurityBase(), providentFundRatio, null,
                    addEmployee.getProvidentFundBase(), addEmployee.getEntryTime(), addEmployee.getCreatedTime(),
                    addEmployee.getSocialSecurityCity(), addEmployee.getProvidentFundCity(), addEmployee.getRemark());
            batchPreDispatches.add(batchPreDispatch);
        } else if (sIsOut && !gIsOut) {
            // 社保省外，公积金省内
            batchPreDispatch = new BatchPreDispatch(addEmployee.getEmployeeName(), addEmployee.getIdentityNo(),
                    addEmployee.getMobile(), addEmployee.getSocialSecurityBase(), null, null, null,
                    addEmployee.getEntryTime(), addEmployee.getCreatedTime(), addEmployee.getSocialSecurityCity(),
                    null, addEmployee.getRemark());
            batchPreDispatches.add(batchPreDispatch);
        } else if (!sIsOut && gIsOut) {
            // 社保省内，公积金省外
            batchPreDispatch = new BatchPreDispatch(addEmployee.getEmployeeName(), addEmployee.getIdentityNo(),
                    addEmployee.getMobile(), null, providentFundRatio, null, addEmployee.getProvidentFundBase(),
                    addEmployee.getEntryTime(), addEmployee.getCreatedTime(), null, addEmployee.getProvidentFundCity(),
                    addEmployee.getRemark());
            batchPreDispatches.add(batchPreDispatch);

        }
        // 生成批量预派
        batchPreDispatchService.addBatchPreDispatchs(getUserId(), this.getOrganizationFacade(),
                batchPreDispatches);
    }

    /**
     * 方法说明：创建公积金申报流程
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private String createProvidentFundDeclare(ProvidentFundDeclare gDeclare, String creater, String createdDeptId) throws Exception {
        BizObjectModel model =  GetBizObjectModelUntils.getProvidentFundDeclare(gDeclare);
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, model, "id");
        String modelWfId = this.getWorkflowInstanceFacade().startWorkflowInstance(createdDeptId,
                creater, Constants.PROVIDENT_FUND_DECLARE_SCHEMA_WF, id, true);
        log.info("创建公积金申报业务对象成功：" + id + "; 启动公积金申报流程成功:" + modelWfId);
        return id;
    }

    /**
     * 方法说明：创建社保申报流程
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private String createSocialSecurityDeclare(SocialSecurityDeclare sDeclare, String creater, String createdDeptId) throws Exception{
        BizObjectModel model =  GetBizObjectModelUntils.getSocialSecurityDeclare(sDeclare);
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, model, "id");
        String modelWfId = this.getWorkflowInstanceFacade().startWorkflowInstance(createdDeptId,
                creater, Constants.SOCIAL_SECURITY_DECLARE_SCHEMA_WF, id, true);
        log.info("创建社保申报业务对象成功：" + id + "; 启动社保申报流程成功:" + modelWfId);
        return id;
    }

    /**
     * 方法说明：创建公积金停缴流程
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private String createProvidentFundClose(ProvidentFundClose gClose, String creater, String createdDeptId) throws Exception {
        BizObjectModel model =  GetBizObjectModelUntils.getProvidentFundClose(gClose);
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, model, "id");
        String modelWfId = this.getWorkflowInstanceFacade().startWorkflowInstance(createdDeptId,
                creater, Constants.PROVIDENT_FUND_CLOSE_SCHEMA_WF, id, true);
        log.info("创建公积金停缴业务对象成功：" + id + "; 启动公积金停缴流程成功:" + modelWfId);
        return id;
    }

    /**
     * 方法说明：创建社保停缴流程
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private String createSocialSecurityClose(SocialSecurityClose sClose, String creater, String createdDeptId) throws Exception{
        BizObjectModel model =  GetBizObjectModelUntils.getSocialSecurityClose(sClose);
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, model, "id");
        String modelWfId = this.getWorkflowInstanceFacade().startWorkflowInstance(createdDeptId,
                creater, Constants.SOCIAL_SECURITY_CLOSE_SCHEMA_WF, id, true);
        log.info("创建社保停缴业务对象成功：" + id + "; 启动社保停缴流程成功:" + modelWfId);
        return id;
    }

    /**
     * 方法说明：创建入职通知流程
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private void createEntryNotice(BizObjectModel entryNotice, String creater, String createdDeptId) throws Exception{
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, entryNotice, "id");
        String modelWfId = this.getWorkflowInstanceFacade().startWorkflowInstance(createdDeptId,
                creater, Constants.ENTRY_NOTICE_SCHEMA_WF, id, true);
        log.info("创建入职通知业务对象成功：" + id + "; 启动入职通知流程成功:" + modelWfId);
    }

    /**
     * 方法说明：创建员工档案业务对象
     * @return void
     * @author liulei
     * @Date 2020/4/16 17:29
     */
    private String createEmployeeFiles(EmployeeFiles employeeFiles, String creater) throws Exception {
        BizObjectModel model = GetBizObjectModelUntils.getEmployeeFilesModel(employeeFiles);
        String id = this.getBizObjectFacade().saveBizObjectModel(creater, model, "id");
        log.info("创建员工档案业务对象成功：" + id);
        return id;
    }


    /**
     * 方法说明：获取社保申报
     * @param id 员工订单id
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
     * @author liulei
     * @Date 2020/4/17 17:45
     */
    private SocialSecurityDeclare getOldSocialSecurityDeclareByOrderFormId(String id) throws Exception {
        SocialSecurityDeclare oldSocialSecurityDeclare = new SocialSecurityDeclare();
        if (StringUtils.isNotBlank(id)) {
            oldSocialSecurityDeclare = addEmployeeService.getSocialSecurityDeclareByOrderFormId(id);
            if (oldSocialSecurityDeclare == null) {
                oldSocialSecurityDeclare = new SocialSecurityDeclare();
            }
        }
        return oldSocialSecurityDeclare;
    }

    /**
     * 方法说明：获取公积金申报
     * @param id 员工订单id
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare
     * @author liulei
     * @Date 2020/4/17 17:45
     */
    private ProvidentFundDeclare getOldProvidentFundDeclareByOrderFormId(String id) throws Exception {
        ProvidentFundDeclare oldProvidentFundDeclare = new ProvidentFundDeclare();
        if (StringUtils.isNotBlank(id)) {
            oldProvidentFundDeclare = addEmployeeService.getProvidentFundDeclareByOrderFormId(id);
            if (oldProvidentFundDeclare == null) {
                oldProvidentFundDeclare = new ProvidentFundDeclare();
            }
        }
        return oldProvidentFundDeclare;
    }

    /**
     * 方法说明：获取社保停缴
     * @param id 员工订单id
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityClose
     * @author liulei
     * @Date 2020/4/17 17:45
     */
    private SocialSecurityClose getOldSocialSecurityCloseByOrderFormId(String id) throws Exception {
        SocialSecurityClose sClose = new SocialSecurityClose();
        if (StringUtils.isNotBlank(id)) {
            sClose = addEmployeeService.getSocialSecurityCloseByOrderFormId(id);
            if (sClose == null) {
                sClose = new SocialSecurityClose();
            }
        }
        return sClose;
    }

    /**
     * 方法说明：获取公积金停缴
     * @param id 员工订单id
     * @return com.authine.cloudpivot.web.api.entity.ProvidentFundClose
     * @author liulei
     * @Date 2020/4/17 17:45
     */
    private ProvidentFundClose getOldProvidentFundCloseByOrderFormId(String id) throws Exception {
        ProvidentFundClose gClose = new ProvidentFundClose();
        if (StringUtils.isNotBlank(id)) {
            gClose = addEmployeeService.getProvidentFundCloseByOrderFormId(id);
            if (gClose == null) {
                gClose = new ProvidentFundClose();
            }
        }
        return gClose;
    }

    /**
     * 方法说明：员工订单赋值
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/4/17 16:58
     */
    private EmployeeOrderForm setEmployeeOrderFormValue(EmployeeOrderForm employeeOrderForm, Double serviceFee,
                                                        Double sum, String employeeFilesId, String sWelfareHandler,
                                                        String gWelfareHandler, String identityNoType,
                                                        String identityNo, String businessType,
                                                        String firstLevelClientName, String secondLevelClientName) {
        employeeOrderForm.setServiceFee(serviceFee);
        employeeOrderForm.setTotal(sum + serviceFee);
        employeeOrderForm.setEmployeeFilesId(employeeFilesId);
        employeeOrderForm.setSWelfareHandler(sWelfareHandler);
        employeeOrderForm.setGWelfareHandler(gWelfareHandler);
        employeeOrderForm.setIdType(identityNoType);
        employeeOrderForm.setIdentityNo(identityNo);
        employeeOrderForm.setBusinessType(businessType);
        employeeOrderForm.setFirstLevelClientName(firstLevelClientName);
        employeeOrderForm.setSecondLevelClientName(secondLevelClientName);

        return employeeOrderForm;
    }

    /**
     * 方法说明：增员变更时，需要变更的公积金申报字段
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/4/17 14:15
     */
    private ProvidentFundDeclare getChangeValue(ProvidentFundDeclare oldProvidentFundDeclare,
                                                String employeeOrderFormId, String employeeName, String gender,
                                                Date birthday, String identityNoType, String identityNo,
                                                String welfareHandler, Date startMonth, Double providentFundBase,
                                                Double corporatePayment, Double personalDeposit, Double totalDeposit,
                                                String operateLeader, String status, String city,
                                                String firstLevelClientName, String secondLevelClientName) {
        oldProvidentFundDeclare.setEmployeeOrderFormId(employeeOrderFormId);
        oldProvidentFundDeclare.setEmployeeName(employeeName);
        oldProvidentFundDeclare.setGender(gender);
        oldProvidentFundDeclare.setBirthday(birthday);
        oldProvidentFundDeclare.setIdentityNo(identityNo);
        oldProvidentFundDeclare.setIdentityNoType(identityNoType);
        oldProvidentFundDeclare.setWelfareHandler(welfareHandler);
        oldProvidentFundDeclare.setStartMonth(startMonth);
        oldProvidentFundDeclare.setProvidentFundBase(providentFundBase);
        oldProvidentFundDeclare.setCorporatePayment(corporatePayment);
        oldProvidentFundDeclare.setPersonalDeposit(personalDeposit);
        oldProvidentFundDeclare.setTotalDeposit(totalDeposit);
        oldProvidentFundDeclare.setOperateLeader(operateLeader);
        oldProvidentFundDeclare.setStatus(status);
        oldProvidentFundDeclare.setCity(city);
        oldProvidentFundDeclare.setFirstLevelClientName(firstLevelClientName);
        oldProvidentFundDeclare.setSecondLevelClientName(secondLevelClientName);

        return oldProvidentFundDeclare;
    }

    /**
     * 方法说明：增员变更时，需要变更的社保申报字段
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/4/17 14:15
     */
    private SocialSecurityDeclare getChangeValue(SocialSecurityDeclare oldSocialSecurityDeclare, Date startMonth,
                                                 String employeeName, String gender, String identityNo,
                                                 String identityNoType, Date contractSigningDate, Date contractDeadline,
                                                 Double positiveSalary, Double basePay, String mobile,
                                                 String welfareHandler, Date birthday, String operateLeader,
                                                 String employeeOrderFormId, String status, String firstLevelClientName,
                                                 String secondLevelClientName, String city, String remark) {
        oldSocialSecurityDeclare.setStartMonth(startMonth);
        oldSocialSecurityDeclare.setEmployeeName(employeeName);
        oldSocialSecurityDeclare.setGender(gender);
        oldSocialSecurityDeclare.setIdentityNo(identityNo);
        oldSocialSecurityDeclare.setIdentityNoType(identityNoType);
        oldSocialSecurityDeclare.setContractSigningDate(contractSigningDate);
        oldSocialSecurityDeclare.setContractDeadline(contractDeadline);
        oldSocialSecurityDeclare.setPositiveSalary(positiveSalary);
        oldSocialSecurityDeclare.setBasePay(basePay);
        oldSocialSecurityDeclare.setMobile(mobile);
        oldSocialSecurityDeclare.setWelfareHandler(welfareHandler);
        oldSocialSecurityDeclare.setBirthday(birthday);
        oldSocialSecurityDeclare.setOperateLeader(operateLeader);
        oldSocialSecurityDeclare.setEmployeeOrderFormId(employeeOrderFormId);
        oldSocialSecurityDeclare.setStatus(status);
        oldSocialSecurityDeclare.setFirstLevelClientName(firstLevelClientName);
        oldSocialSecurityDeclare.setSecondLevelClientName(secondLevelClientName);
        oldSocialSecurityDeclare.setCity(city);
        oldSocialSecurityDeclare.setRemark(remark);

        return oldSocialSecurityDeclare;
    }

    /**
     * 方法说明：增员变更时，需要变更的员工档案字段
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/4/17 14:15
     */
    private EmployeeFiles getChangeValue(EmployeeFiles oldEmployeeFiles, String employeeName, String identityNoType,
                                         String identityNo, String gender, Date birthday, String employeeNature,
                                         String familyRegisterNature, String mobile, String socialSecurityCity,
                                         String providentFundCity, Date socialSecurityStartTime,
                                         Date providentFundStartTime, String email, String householdRegisterRemarks,
                                         Double socialSecurityBase, Double providentFundBase, String sWelfareHandler,
                                         String gWelfareHandler, String isRetiredSoldier, String isPoorArchivists,
                                         String isDisabled) {
        oldEmployeeFiles.setEmployeeName(employeeName);
        oldEmployeeFiles.setIdType(identityNoType);
        oldEmployeeFiles.setIdNo(identityNo);
        oldEmployeeFiles.setGender(gender);
        oldEmployeeFiles.setBirthDate(birthday);
        oldEmployeeFiles.setEmployeeNature(employeeNature);
        oldEmployeeFiles.setHouseholdRegisterNature(familyRegisterNature);
        oldEmployeeFiles.setMobile(mobile);
        oldEmployeeFiles.setSocialSecurityCity(socialSecurityCity);
        oldEmployeeFiles.setProvidentFundCity(providentFundCity);
        oldEmployeeFiles.setSocialSecurityChargeStart(socialSecurityStartTime);
        oldEmployeeFiles.setProvidentFundChargeStart(providentFundStartTime);
        oldEmployeeFiles.setEmail(email);
        oldEmployeeFiles.setHouseholdRegisterRemarks(householdRegisterRemarks);
        oldEmployeeFiles.setSocialSecurityBase(socialSecurityBase);
        oldEmployeeFiles.setProvidentFundBase(providentFundBase);
        oldEmployeeFiles.setSWelfareHandler(sWelfareHandler);
        oldEmployeeFiles.setGWelfareHandler(gWelfareHandler);
        oldEmployeeFiles.setIsRetiredSoldier(isRetiredSoldier);
        oldEmployeeFiles.setIsPoorArchivists(isPoorArchivists);
        oldEmployeeFiles.setIsDisabled(isDisabled);

        return oldEmployeeFiles;
    }

    /**
     * 方法说明：获取补缴月份
     * @param node
     * @param curTime 当前时间
     * @param startTime 开始缴纳时间
     * @return int
     * @throws
     * @author liulei
     * @Date 2020/2/14 14:30
     */
    private int getMonthDifference(int node, Date curTime, Date startTime) throws Exception{
        if (startTime == null) {
            return  0;
        }
        Calendar cur = Calendar.getInstance();
        cur.setTime(curTime);
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);

        int curYear = cur.get(Calendar.YEAR);
        int curMonth = cur.get(Calendar.MONTH) + 1;
        int curDay = cur.get(Calendar.DAY_OF_MONTH);

        int startYear = start.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH) + 1;

        if (curDay > node) {
            curMonth++;
        }

        int monthDifference = (curYear - startYear) * 12 + (curMonth - startMonth);
        monthDifference++;
        return monthDifference;
    }


    /**
     * 方法说明：减员时员工档案赋值
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/4/20 9:21
     */
    private EmployeeFiles setEmployeeFilesQuitInfo(EmployeeFiles employeeFiles, Date reportQuitDate,
                                                   String reportSeveranceOfficer, Date quitDate, Date sEndTime,
                                                   Date gEndTime, String quitReason, String quitRemark) {
        employeeFiles.setReportQuitDate(reportQuitDate);
        employeeFiles.setReportSeveranceOfficer(reportSeveranceOfficer);
        employeeFiles.setQuitDate(quitDate);
        employeeFiles.setSocialSecurityChargeEnd(sEndTime);
        employeeFiles.setProvidentFundChargeEnd(gEndTime);
        employeeFiles.setQuitReason(quitReason);
        employeeFiles.setQuitRemark(quitRemark);
        return employeeFiles;
    }

    /**
     * 方法说明：获取社保，公积金运行负责人
     * @param sCity 社保福利地
     * @param sWelfareHandler 社保福利办理方
     * @param gCity 公积金福利地
     * @param gWelfareHandler 公积金福利办理方
     * @param secondLevelClientName 二级客户名称
     * @return com.authine.cloudpivot.web.api.entity.OperateLeader
     * @author liulei
     * @Date 2020/4/20 9:47
     */
    private OperateLeader getOperateLeader(String sCity, String sWelfareHandler, String gCity, String gWelfareHandler,
                                           String secondLevelClientName) throws Exception{
        OperateLeader operateLeader = new OperateLeader();
        if (StringUtils.isNotBlank(sCity) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(sCity) >= 0) {
            operateLeader = employeeMaintainService.getOperateLeader(sCity, sWelfareHandler, secondLevelClientName);
        }
        if (StringUtils.isNotBlank(gCity) && Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(gCity) >= 0) {
            if (!gCity.equals(sCity) || !sWelfareHandler.equals(gWelfareHandler)) {
                OperateLeader operateLeader1 = employeeMaintainService.getOperateLeader(gCity, gWelfareHandler,
                        secondLevelClientName);
                operateLeader.setProvidentFundLeader(operateLeader1.getProvidentFundLeader());
            }
        }
        return operateLeader;
    }

    /**
     * 方法说明：减员变更社保停缴
     * @return com.authine.cloudpivot.web.api.entity.SocialSecurityClose
     * @author liulei
     * @Date 2020/4/20 13:42
     */
    private SocialSecurityClose getChangeValue(SocialSecurityClose securityClose, String employeeName, String gender,
                                               Date birthday, String identityNoType, String identityNo,
                                               String welfareHandler, Date startMonth, Date chargeEndMonth,
                                               Double socialSecurityBase, String resignationRemarks,
                                               String operateLeader, String firstLevelClientName,
                                               String secondLevelClientName, String city) {
        securityClose.setEmployeeName(employeeName);
        securityClose.setGender(gender);
        securityClose.setBirthday(birthday);
        securityClose.setIdentityNoType(identityNoType);
        securityClose.setIdentityNo(identityNo);
        securityClose.setWelfareHandler(welfareHandler);
        securityClose.setStartMonth(startMonth);
        securityClose.setChargeEndMonth(chargeEndMonth);
        securityClose.setSocialSecurityBase(socialSecurityBase);
        securityClose.setResignationRemarks(resignationRemarks);
        securityClose.setOperateLeader(operateLeader);
        securityClose.setFirstLevelClientName(firstLevelClientName);
        securityClose.setSecondLevelClientName(secondLevelClientName);
        securityClose.setCity(city);
        return securityClose;
    }

    /**
     * 方法说明：减员变更公积金停缴
     * @return com.authine.cloudpivot.web.api.entity.ProvidentFundClose
     * @author liulei
     * @Date 2020/4/20 13:55
     */
    private ProvidentFundClose getChangeValue(ProvidentFundClose fundClose, String employeeName, String gender,
                                              Date birthday, String identityNoType, String identityNo,
                                              String firstLevelClientName, String secondLevelClientName,
                                              String welfareHandler, Date startMonth, Date chargeEndMonth,
                                              Double providentFundBase, String operateLeader, String city) {
        fundClose.setEmployeeName(employeeName);
        fundClose.setGender(gender);
        fundClose.setBirthday(birthday);
        fundClose.setIdentityNoType(identityNoType);
        fundClose.setIdentityNo(identityNo);
        fundClose.setFirstLevelClientName(firstLevelClientName);
        fundClose.setSecondLevelClientName(secondLevelClientName);
        fundClose.setWelfareHandler(welfareHandler);
        fundClose.setStartMonth(startMonth);
        fundClose.setChargeEndMonth(chargeEndMonth);
        fundClose.setProvidentFundBase(providentFundBase);
        fundClose.setOperateLeader(operateLeader);
        fundClose.setCity(city);
        return fundClose;
    }
}
