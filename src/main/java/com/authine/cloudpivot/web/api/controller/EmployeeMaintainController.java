package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.service.*;
import com.authine.cloudpivot.web.api.service.impl.UpdateEmployeesServiceImpl;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
    private ClientService clientService;

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
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            employeeMaintainService.addEmployee(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), fileName, userId, user.getDepartmentId());
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
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 导入
            employeeMaintainService.deleteEmployee(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), fileName, userId, user.getDepartmentId());
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
            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(delEmployee.getCreater())) {
                applicant = "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]";
            }

            // 性别&出生日期
            Map <String, String> genderAndBirthday = getGenderAndBirthdayByIdentityNo(delEmployee.getIdentityNo(),
                    delEmployee.getIdentityNoType());
            // 出生日期
            Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                    DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;

            //获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoOrClientName(delEmployee.getIdentityNo(),
                            delEmployee.getClientName());
            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(employeeFiles.getSocialSecurityCity())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader != null) {
                    socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();
                }
            }
            if (StringUtils.isNotBlank(employeeFiles.getProvidentFundCity())) {
                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (employeeFiles.getProvidentFundCity().equals(employeeFiles.getSocialSecurityCity())
                        && welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                    if (operateLeader != null) {
                        providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                    }
                }
            }

            // 根据员工档案的id获取社保公积金信息
            Map <String, String> socialSecurityFund =
                    employeeMaintainService.getSocialSecurityFundDetail(employeeFiles.getId());
            // 开始时间
            Date startMonth = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("startMonth"))) {
                startMonth = DateUtils.parseDate(socialSecurityFund.get("startMonth"), Constants.PARSE_PATTERNS);
            }
            // 社保基数
            Double socialSecurityBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("socialSecurityBase"))){
                socialSecurityBase = Double.parseDouble(socialSecurityFund.get("socialSecurityBase"));
            }
            // 公积金基数
            Double providentFundBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("providentFundBase"))){
                providentFundBase = Double.parseDouble(socialSecurityFund.get("providentFundBase"));
            }
            // 企业缴存额
            Double enterpriseDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("enterpriseDeposit"))){
                enterpriseDeposit = Double.parseDouble(socialSecurityFund.get("enterpriseDeposit"));
            }
            // 个人缴存额
            Double personalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("personalDeposit"))){
                personalDeposit = Double.parseDouble(socialSecurityFund.get("personalDeposit"));
            }
            // 缴存总额
            Double totalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("totalDeposit"))){
                totalDeposit = Double.parseDouble(socialSecurityFund.get("totalDeposit"));
            }

            // 社保停缴实体
            SocialSecurityClose socialSecurityClose = new SocialSecurityClose(socialSecurityFund.get(
                    "employeeOrderFormId"), delEmployee.getEmployeeName(), genderAndBirthday.get("gender"), birthDate,
                    delEmployee.getIdentityNoType(), delEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                    delEmployee.getClientName(), employeeFiles.getSalesman(), employeeFiles.getSocialSecurityArea(),
                    applicant, delEmployee.getCreatedTime(), startMonth, delEmployee.getSocialSecurityEndTime(),
                    socialSecurityBase, delEmployee.getLeaveReason(), socialSecurityOperateLeader);

            // 公积金停缴实体
            ProvidentFundClose providentFundClose = new ProvidentFundClose(socialSecurityFund.get(
                    "employeeOrderFormId"), delEmployee.getEmployeeName(), genderAndBirthday.get("gender"), birthDate,
                    delEmployee.getIdentityNoType(), delEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                    delEmployee.getClientName(), employeeFiles.getSalesman(), employeeFiles.getProvidentFundArea(),
                    applicant, delEmployee.getCreatedTime(), startMonth, delEmployee.getProvidentFundEndTime(),
                    providentFundBase, enterpriseDeposit, personalDeposit, totalDeposit, providentFundOperateLeader);

            employeeFiles.setReportQuitDate(delEmployee.getCreatedTime());
            employeeFiles.setReportSeveranceOfficer(applicant);
            employeeFiles.setQuitDate(delEmployee.getLeaveTime());
            employeeFiles.setSocialSecurityChargeEnd(delEmployee.getSocialSecurityEndTime());
            employeeFiles.setProvidentFundChargeEnd(delEmployee.getProvidentFundEndTime());
            employeeFiles.setQuitReason(delEmployee.getLeaveReason());
            employeeFiles.setQuitRemark(delEmployee.getRemark());

            // 停缴社保，公积金流程发起人(业务员即客服)
            String userJsonStr = employeeFiles.getSalesman();
            if (StringUtils.isBlank(userJsonStr)) {
                return this.getErrResponseResult("error", 404l, "没有获取到客服。");
            }
            String[] strArr = userJsonStr.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel user = this.getOrganizationFacade().getUser(userId);
            employeeMaintainService.deleteEmployeeSubmit(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                    socialSecurityClose, providentFundClose, employeeFiles, user);

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

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getClientName(), employeeFiles.getIdNo(),
                    EMPLOYEE_STATUS_QUIT);

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
            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(shDeleteEmployee.getCreater())) {
                applicant = "[{\"id\":\"" + shDeleteEmployee.getCreater() + "\",\"type\":3}]";
            }

            // 性别&出生日期
            Map <String, String> genderAndBirthday = getGenderAndBirthdayByIdentityNo(shDeleteEmployee.getIdentityNo(),
                    shDeleteEmployee.getIdentityNoType());
            // 出生日期
            Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                    DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;

            // 获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoOrClientName(shDeleteEmployee.getIdentityNo(),
                            shDeleteEmployee.getClientName());
            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(employeeFiles.getSocialSecurityCity())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader != null) {
                    socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();
                }
            }
            if (StringUtils.isNotBlank(employeeFiles.getProvidentFundCity())) {
                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (employeeFiles.getProvidentFundCity().equals(employeeFiles.getSocialSecurityCity())
                        && welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                    if (operateLeader != null) {
                        providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                    }
                }
            }
            // 根据员工档案的单据号获取社保公积金信息
            Map <String, String> socialSecurityFund =
                    employeeMaintainService.getSocialSecurityFundDetail(employeeFiles.getSequenceNo());
            // 开始时间
            Date startMonth = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("startMonth"))) {
                startMonth = DateUtils.parseDate(socialSecurityFund.get("startMonth"), Constants.PARSE_PATTERNS);
            }
            // 社保基数
            Double socialSecurityBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("socialSecurityBase"))){
                socialSecurityBase = Double.parseDouble(socialSecurityFund.get("socialSecurityBase"));
            }
            // 公积金基数
            Double providentFundBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("providentFundBase"))){
                providentFundBase = Double.parseDouble(socialSecurityFund.get("providentFundBase"));
            }
            // 企业缴存额
            Double enterpriseDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("enterpriseDeposit"))){
                enterpriseDeposit = Double.parseDouble(socialSecurityFund.get("enterpriseDeposit"));
            }
            // 个人缴存额
            Double personalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("personalDeposit"))){
                personalDeposit = Double.parseDouble(socialSecurityFund.get("personalDeposit"));
            }
            // 缴存总额
            Double totalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("totalDeposit"))){
                totalDeposit = Double.parseDouble(socialSecurityFund.get("totalDeposit"));
            }

            // 社保停缴实体
            SocialSecurityClose socialSecurityClose = new SocialSecurityClose(socialSecurityFund.get(
                    "employeeOrderFormId"), shDeleteEmployee.getEmployeeName(), genderAndBirthday.get("gender"),
                    birthDate, shDeleteEmployee.getIdentityNoType(), shDeleteEmployee.getIdentityNo(),
                    employeeFiles.getEntrustedUnit(), shDeleteEmployee.getClientName(), employeeFiles.getSalesman(),
                    employeeFiles.getSocialSecurityArea(), applicant, shDeleteEmployee.getCreatedTime(), startMonth,
                    shDeleteEmployee.getChargeEndTime(), socialSecurityBase, shDeleteEmployee.getLeaveReason(),
                    socialSecurityOperateLeader);

            // 公积金停缴实体
            ProvidentFundClose providentFundClose = new ProvidentFundClose(socialSecurityFund.get(
                    "employeeOrderFormId"), shDeleteEmployee.getEmployeeName(), genderAndBirthday.get("gender"),
                    birthDate, shDeleteEmployee.getIdentityNoType(), shDeleteEmployee.getIdentityNo(),
                    employeeFiles.getEntrustedUnit(), shDeleteEmployee.getClientName(), employeeFiles.getSalesman(),
                    employeeFiles.getProvidentFundArea(), applicant, shDeleteEmployee.getCreatedTime(), startMonth,
                    shDeleteEmployee.getChargeEndTime(), providentFundBase, enterpriseDeposit, personalDeposit,
                    totalDeposit, providentFundOperateLeader);

            employeeFiles.setReportQuitDate(shDeleteEmployee.getCreatedTime());
            employeeFiles.setReportSeveranceOfficer(applicant);
            employeeFiles.setQuitDate(shDeleteEmployee.getDepartureTime());
            employeeFiles.setSocialSecurityChargeEnd(shDeleteEmployee.getChargeEndTime());
            employeeFiles.setProvidentFundChargeEnd(shDeleteEmployee.getChargeEndTime());
            employeeFiles.setQuitReason(shDeleteEmployee.getLeaveReason());
            employeeFiles.setQuitRemark(shDeleteEmployee.getLeaveRemark());

            // 停缴社保，公积金流程发起人(业务员即客服)
            String userJsonStr = employeeFiles.getSalesman();
            if (StringUtils.isBlank(userJsonStr)) {
                return this.getErrResponseResult("error", 404l, "没有获取到客服。");
            }
            String[] strArr = userJsonStr.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel user = this.getOrganizationFacade().getUser(userId);
            employeeMaintainService.deleteEmployeeSubmit(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                    socialSecurityClose, providentFundClose, employeeFiles, user);

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

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getClientName(), employeeFiles.getIdNo(),
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
            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(nationwideDispatch.getCreater())) {
                applicant = "[{\"id\":\"" + nationwideDispatch.getCreater() + "\",\"type\":3}]";
            }

            // 性别&出生日期
            Map <String, String> genderAndBirthday =
                    getGenderAndBirthdayByIdentityNo(nationwideDispatch.getIdentityNo(),
                            nationwideDispatch.getIdentityNoType());
            // 出生日期
            Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                    DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;

            // 获取员工档案
            EmployeeFiles employeeFiles =
                    employeeFilesService.getEmployeeFilesByIdNoOrClientName(nationwideDispatch.getIdentityNo(),
                            nationwideDispatch.getBusinessCustomerName());
            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(employeeFiles.getSocialSecurityCity())) {
                operateLeader =
                        employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                                employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader != null) {
                    socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();
                }
            }
            if (StringUtils.isNotBlank(employeeFiles.getProvidentFundCity())) {
                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (employeeFiles.getProvidentFundCity().equals(employeeFiles.getSocialSecurityCity())
                        && welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                                    employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                    if (operateLeader != null) {
                        providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                    }
                }
            }
            // 根据员工档案的单据号获取社保公积金信息
            Map <String, String> socialSecurityFund =
                    employeeMaintainService.getSocialSecurityFundDetail(employeeFiles.getSequenceNo());
            // 开始时间
            Date startMonth = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("startMonth"))) {
                startMonth = DateUtils.parseDate(socialSecurityFund.get("startMonth"), Constants.PARSE_PATTERNS);
            }
            // 社保基数
            Double socialSecurityBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("socialSecurityBase"))){
                socialSecurityBase = Double.parseDouble(socialSecurityFund.get("socialSecurityBase"));
            }
            // 公积金基数
            Double providentFundBase = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("providentFundBase"))){
                providentFundBase = Double.parseDouble(socialSecurityFund.get("providentFundBase"));
            }
            // 企业缴存额
            Double enterpriseDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("enterpriseDeposit"))){
                enterpriseDeposit = Double.parseDouble(socialSecurityFund.get("enterpriseDeposit"));
            }
            // 个人缴存额
            Double personalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("personalDeposit"))){
                personalDeposit = Double.parseDouble(socialSecurityFund.get("personalDeposit"));
            }
            // 缴存总额
            Double totalDeposit = null;
            if (StringUtils.isNotBlank(socialSecurityFund.get("totalDeposit"))){
                totalDeposit = Double.parseDouble(socialSecurityFund.get("totalDeposit"));
            }

            // 社保停缴实体
            SocialSecurityClose socialSecurityClose = new SocialSecurityClose(socialSecurityFund.get(
                    "employeeOrderFormId"), nationwideDispatch.getEmployeeName(), genderAndBirthday.get("gender"),
                    birthDate, nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(),
                    employeeFiles.getEntrustedUnit(), nationwideDispatch.getBusinessCustomerName(),
                    employeeFiles.getSalesman(), employeeFiles.getSocialSecurityArea(), applicant,
                    nationwideDispatch.getCreatedTime(), startMonth, nationwideDispatch.getSServiceFeeEndDate(),
                    socialSecurityBase,
                    nationwideDispatch.getSocialSecurityStopReason() + nationwideDispatch.getDepartureRemark(),
                    socialSecurityOperateLeader);

            // 公积金停缴实体
            ProvidentFundClose providentFundClose = new ProvidentFundClose(socialSecurityFund.get(
                    "employeeOrderFormId"), nationwideDispatch.getEmployeeName(), genderAndBirthday.get("gender"),
                    birthDate, nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(),
                    employeeFiles.getEntrustedUnit(), nationwideDispatch.getBusinessCustomerName(),
                    employeeFiles.getSalesman(), employeeFiles.getProvidentFundArea(), applicant,
                    nationwideDispatch.getCreatedTime(), startMonth, nationwideDispatch.getGServiceFeeEndDate(),
                    providentFundBase, enterpriseDeposit, personalDeposit, totalDeposit, providentFundOperateLeader);

            employeeFiles.setReportQuitDate(nationwideDispatch.getCreatedTime());
            employeeFiles.setReportSeveranceOfficer(applicant);
            employeeFiles.setQuitDate(nationwideDispatch.getDepartureDate());
            employeeFiles.setSocialSecurityChargeEnd(nationwideDispatch.getSServiceFeeEndDate());
            employeeFiles.setProvidentFundChargeEnd(nationwideDispatch.getGServiceFeeEndDate());
            employeeFiles.setQuitReason(nationwideDispatch.getSocialSecurityStopReason());
            employeeFiles.setQuitRemark(nationwideDispatch.getDepartureRemark());

            // 停缴社保，公积金流程发起人(业务员即客服)
            String userJsonStr = employeeFiles.getSalesman();
            if (StringUtils.isBlank(userJsonStr)) {
                return this.getErrResponseResult("error", 404l, "没有获取到客服。");
            }
            String[] strArr = userJsonStr.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel user = this.getOrganizationFacade().getUser(userId);
            employeeMaintainService.deleteEmployeeSubmit(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                    socialSecurityClose, providentFundClose, employeeFiles, user);

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

            // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
            businessInsuranceService.updateEmployeeStatus(employeeFiles.getClientName(), employeeFiles.getIdNo(),
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
            if (addEmployee.getSocialSecurityCity().indexOf("六安") >= 0) {
                Double base = CommonUtils.processingData(addEmployee.getSocialSecurityBase(), "四舍五入", "0");
                addEmployee.setSocialSecurityBase(base);
            }
            if (addEmployee.getProvidentFundCity().indexOf("六安") >= 0) {
                Double base = CommonUtils.processingData(addEmployee.getProvidentFundBase(), "四舍五入", "0");
                addEmployee.setProvidentFundBase(base);
            }

            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(addEmployee.getCreater())) {
                applicant = "[{\"id\":\"" + addEmployee.getCreater() + "\",\"type\":3}]";
            }

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(addEmployee);

            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && addEmployee.getSocialSecurityStartTime() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();
            }
            if (StringUtils.isNotBlank(employeeFiles.getProvidentFundCity())) {
                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" : employeeFiles.getSocialSecurityArea();
                if (employeeFiles.getProvidentFundCity().equals(employeeFiles.getSocialSecurityCity())
                        && welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && addEmployee.getProvidentFundStartTime() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 根据公司名称，委托单位，地区，员工性质获取业务员和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(),
                            employeeFiles.getEmployeeNature());
            String salesman = salesmanAndServiceFee.get("salesman") == null ? "" : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";

            employeeFiles.setSalesman(salesman);
            employeeFiles.setReportRecruits(applicant);

            /** 入职通知书*/
            EntryNotice entryNotice = new EntryNotice(addEmployee.getEmployeeName(), addEmployee.getIdentityNo(),
                    addEmployee.getSocialSecurityCity(), addEmployee.getClientName());

            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal = employeeMaintainService.getTimeNode(addEmployee.getClientName(),
                    addEmployee.getSocialSecurityCity());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), addEmployee.getSocialSecurityStartTime());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), addEmployee.getProvidentFundStartTime());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }
            employeeFiles.setEntryNotice("否");
            if (sMonthDifference > 0) {
                Map <String, String> entryNoticeInfo = employeeMaintainService.getEntryNoticeInfo(sMonthDifference,
                        addEmployee.getSocialSecurityCity());
                if ("true".equals(entryNoticeInfo.get("haveEntryNotice"))) {
                    employeeFiles.setEntryNotice("是");
                    // 运行签收人
                    entryNotice.setOperateSignatory(entryNoticeInfo.get("operate_signatory"));
                    // 用工备案
                    entryNotice.setRecordOfEmployment(entryNoticeInfo.get("record_of_employment"));
                    // 社保
                    entryNotice.setSocialSecurity(entryNoticeInfo.get("social_security"));
                    // 公积金
                    entryNotice.setProvidentFund(entryNoticeInfo.get("provident_fund"));
                }
            }

            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    addEmployee.getSocialSecurityCity(), addEmployee.getSocialSecurityStartTime(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundCity(),
                    addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl(), personal);

            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare = new SocialSecurityDeclare(addEmployee.getEmployeeName()
                    , employeeFiles.getGender(), employeeFiles.getBirthDate(), addEmployee.getIdentityNoType(),
                    addEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), addEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getSocialSecurityArea(), applicant,
                    addEmployee.getCreatedTime(), addEmployee.getContractStartTime(), addEmployee.getContractEndTime(),
                    addEmployee.getSocialSecurityStartTime(), addEmployee.getContractSalary(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getMobile(), weatherPartWorkInjury, socialSecurityOperateLeader
                    , employeeOrderForm.getSocialSecurityDetail());

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(addEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), addEmployee.getIdentityNoType(),
                    addEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), addEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getProvidentFundArea(), applicant,
                    addEmployee.getCreatedTime(), addEmployee.getProvidentFundStartTime(),
                    addEmployee.getProvidentFundBase(), providentFundOperateLeader,
                    employeeOrderForm.getProvidentFundDetail());

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }

            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            //创建员工档案
            String employeeFilesId = employeeMaintainService.createEmployeeInfoModel(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), employeeFiles, customerService);
            if ("是".equals(employeeFiles.getEntryNotice())) {
                //创建入职通知
                employeeMaintainService.createEntryNotice(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        entryNotice, customerService);
            }
            //创建员工订单
            employeeOrderForm.setEmployeeFilesId(employeeFilesId);
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, customerService);
            //创建社保申报
            if (socialSecurityDeclare.getStartMonth() != null) {
                socialSecurityDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createSocialSecurityDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        socialSecurityDeclare, customerService);
            }
            //创建公积金申报
            if (providentFundDeclare.getStartMonth() != null) {
                providentFundDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createProvidentFundDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        providentFundDeclare, customerService);
            }

            // 判断批量预派（省外）
            List<BatchPreDispatch> batchPreDispatches = new ArrayList <>();
            if (Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) < 0) {
                String providentFundRatio =
                        "" + addEmployee.getCompanyProvidentFundBl() + "+" + addEmployee.getEmployeeProvidentFundBl();
                BatchPreDispatch batchPreDispatch = new BatchPreDispatch(employeeFiles.getEmployeeName(),
                        employeeFiles.getIdNo(), employeeFiles.getMobile(), addEmployee.getSocialSecurityBase(),
                        providentFundRatio, null, addEmployee.getProvidentFundBase(), employeeFiles.getEntryTime(),
                        employeeFiles.getReportEntryTime(), employeeFiles.getSocialSecurityCity(),
                        employeeFiles.getEntryDescription());
                batchPreDispatches.add(batchPreDispatch);
            }
            // 生成批量预派
            batchPreDispatchService.addBatchPreDispatchs(this.getUserId(), this.getOrganizationFacade(),
                    batchPreDispatches);

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
            if (shAddEmployee.getCityName().indexOf("六安") >= 0) {
                Double sbase = CommonUtils.processingData(shAddEmployee.getSocialSecurityBase(), "四舍五入", "0");
                shAddEmployee.setSocialSecurityBase(sbase);
                Double gbase = CommonUtils.processingData(shAddEmployee.getProvidentFundBase(), "四舍五入", "0");
                shAddEmployee.setProvidentFundBase(gbase);
            }

            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(shAddEmployee.getCreater())) {
                applicant = "[{\"id\":\"" + shAddEmployee.getCreater() + "\",\"type\":3}]";
            }

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(shAddEmployee);

            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(shAddEmployee.getCityName())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && shAddEmployee.getBenefitStartTime() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();

                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && shAddEmployee.getProvidentFundStartTime() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 查询客服和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(), "代理");
            String salesman = salesmanAndServiceFee.get("salesman") == null ? applicant : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";
            employeeFiles.setSalesman(salesman);
            employeeFiles.setReportRecruits(applicant);
            if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
                employeeFiles.setProvidentFundChargeStart(employeeFiles.getSocialSecurityChargeStart());
            }

            /** 入职通知书*/
            EntryNotice entryNotice = new EntryNotice(shAddEmployee.getEmployeeName(), shAddEmployee.getIdentityNo(),
                    shAddEmployee.getCityName(), shAddEmployee.getClientName());

            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal = employeeMaintainService.getTimeNode(shAddEmployee.getClientName(),
                    shAddEmployee.getCityName());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), shAddEmployee.getBenefitStartTime());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), shAddEmployee.getProvidentFundStartTime());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }
            employeeFiles.setEntryNotice("否");
            if (sMonthDifference > 0) {
                Map <String, String> entryNoticeInfo = employeeMaintainService.getEntryNoticeInfo(sMonthDifference,
                        shAddEmployee.getCityName());
                if ("true".equals(entryNoticeInfo.get("haveEntryNotice"))) {
                    employeeFiles.setEntryNotice("是");
                    // 运行签收人
                    entryNotice.setOperateSignatory(entryNoticeInfo.get("operate_signatory"));
                    // 用工备案
                    entryNotice.setRecordOfEmployment(entryNoticeInfo.get("record_of_employment"));
                    // 社保
                    entryNotice.setSocialSecurity(entryNoticeInfo.get("social_security"));
                    // 公积金
                    entryNotice.setProvidentFund(entryNoticeInfo.get("provident_fund"));
                }
            }

            /** 员工订单实体*/
            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    shAddEmployee.getClientName(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getClientName(),
                    shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getPSupplementProvidentFund(), shAddEmployee.getUSupplementProvidentFund(), personal);
            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare = new SocialSecurityDeclare(shAddEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), shAddEmployee.getIdentityNoType(),
                    shAddEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), shAddEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getSocialSecurityArea(), applicant,
                    shAddEmployee.getCreatedTime(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getSocialSecurityBase(),
                    shAddEmployee.getMobile(), weatherPartWorkInjury, socialSecurityOperateLeader,
                    employeeOrderForm.getSocialSecurityDetail());

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(shAddEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), shAddEmployee.getIdentityNoType(),
                    shAddEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), shAddEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getProvidentFundArea(), applicant,
                    shAddEmployee.getCreatedTime(), shAddEmployee.getProvidentFundStartTime(),
                    shAddEmployee.getProvidentFundBase(), providentFundOperateLeader, employeeOrderForm.getProvidentFundDetail());

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }
            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            //创建员工档案
            String employeeFilesId = employeeMaintainService.createEmployeeInfoModel(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), employeeFiles, customerService);
            if ("是".equals(employeeFiles.getEntryNotice())) {
                //创建入职通知
                employeeMaintainService.createEntryNotice(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        entryNotice, customerService);
            }
            //创建员工订单
            employeeOrderForm.setEmployeeFilesId(employeeFilesId);
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, customerService);
            //创建社保申报
            if (socialSecurityDeclare.getStartMonth() != null) {
                socialSecurityDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createSocialSecurityDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        socialSecurityDeclare, customerService);
            }
            //创建公积金申报
            if (providentFundDeclare.getStartMonth() != null) {
                providentFundDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createProvidentFundDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        providentFundDeclare, customerService);
            }

            // 判断批量预派（省外）
            List<BatchPreDispatch> batchPreDispatches = new ArrayList <>();
            if (Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) < 0) {
                String providentFundRatio =
                        "" + shAddEmployee.getPSupplementProvidentFund() + "+" + shAddEmployee.getUSupplementProvidentFund();
                BatchPreDispatch batchPreDispatch = new BatchPreDispatch(employeeFiles.getEmployeeName(),
                        employeeFiles.getIdNo(), employeeFiles.getMobile(), shAddEmployee.getSocialSecurityBase(),
                        providentFundRatio, providentFundRatio, shAddEmployee.getProvidentFundBase(), employeeFiles.getEntryTime(),
                        employeeFiles.getReportEntryTime(), employeeFiles.getSocialSecurityCity(),
                        employeeFiles.getEntryDescription());
                batchPreDispatches.add(batchPreDispatch);
            }
            // 生成批量预派
            batchPreDispatchService.addBatchPreDispatchs(this.getUserId(), this.getOrganizationFacade(),
                    batchPreDispatches);

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
            if (nationwideDispatch.getInvolved().indexOf("六安") >= 0) {
                Double sbase = CommonUtils.processingData(nationwideDispatch.getSocialInsuranceAmount(), "四舍五入", "0");
                nationwideDispatch.setSocialInsuranceAmount(sbase);
                Double gbase = CommonUtils.processingData(nationwideDispatch.getProvidentFundAmount(), "四舍五入", "0");
                nationwideDispatch.setProvidentFundAmount(gbase);
            }
            // 申请人
            String applicant = null;
            if (StringUtils.isNotBlank(nationwideDispatch.getCreater())) {
                applicant = "[{\"id\":\"" + nationwideDispatch.getCreater() + "\",\"type\":3}]";
            }

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(nationwideDispatch);
            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = "";
            String providentFundOperateLeader = "";
            if (StringUtils.isNotBlank(nationwideDispatch.getInvolved())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && nationwideDispatch.getSServiceFeeStartDate() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();

                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && nationwideDispatch.getGServiceFeeStartDate() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 查询客服和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(), "代理");
            String salesman = salesmanAndServiceFee.get("salesman") == null ? applicant : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";
            employeeFiles.setSalesman(salesman);
            employeeFiles.setReportRecruits(applicant);

            /** 入职通知书*/
            EntryNotice entryNotice = new EntryNotice(nationwideDispatch.getEmployeeName(), nationwideDispatch.getIdentityNo(),
                    nationwideDispatch.getInvolved(), nationwideDispatch.getBusinessCustomerName());

            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal =
                    employeeMaintainService.getTimeNode(nationwideDispatch.getBusinessCustomerName(),
                            nationwideDispatch.getInvolved());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), nationwideDispatch.getSServiceFeeStartDate());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), nationwideDispatch.getGServiceFeeStartDate());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }

            employeeFiles.setEntryNotice("否");
            if (sMonthDifference > 0) {
                Map <String, String> entryNoticeInfo = employeeMaintainService.getEntryNoticeInfo(sMonthDifference,
                        nationwideDispatch.getInvolved());
                if ("true".equals(entryNoticeInfo.get("haveEntryNotice"))) {
                    employeeFiles.setEntryNotice("是");
                    // 运行签收人
                    entryNotice.setOperateSignatory(entryNoticeInfo.get("operate_signatory"));
                    // 用工备案
                    entryNotice.setRecordOfEmployment(entryNoticeInfo.get("record_of_employment"));
                    // 社保
                    entryNotice.setSocialSecurity(entryNoticeInfo.get("social_security"));
                    // 公积金
                    entryNotice.setProvidentFund(entryNoticeInfo.get("provident_fund"));
                }
            }

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
                    companyRatio, employeeRatio, personal);
            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare =
                    new SocialSecurityDeclare(nationwideDispatch.getEmployeeName(), employeeFiles.getGender(),
                            employeeFiles.getBirthDate(), nationwideDispatch.getIdentityNoType(),
                            nationwideDispatch.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                            nationwideDispatch.getBusinessCustomerName(), salesman, customerServices,
                            employeeFiles.getSocialSecurityArea(), applicant, nationwideDispatch.getCreatedTime(),
                            nationwideDispatch.getSServiceFeeStartDate(),
                            nationwideDispatch.getSocialInsuranceAmount(),
                            nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getContactNumber(),
                            weatherPartWorkInjury, socialSecurityOperateLeader, employeeOrderForm.getSocialSecurityDetail());

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(nationwideDispatch.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), nationwideDispatch.getIdentityNoType(),
                    nationwideDispatch.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                    nationwideDispatch.getBusinessCustomerName(), salesman, customerServices,
                    employeeFiles.getProvidentFundArea(), applicant, nationwideDispatch.getCreatedTime(),
                    nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                    providentFundOperateLeader, employeeOrderForm.getProvidentFundDetail());

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }
            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            //创建员工档案
            String employeeFilesId = employeeMaintainService.createEmployeeInfoModel(this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade(), employeeFiles, customerService);
            if ("是".equals(employeeFiles.getEntryNotice())) {
                //创建入职通知
                employeeMaintainService.createEntryNotice(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        entryNotice, customerService);
            }
            //创建员工订单
            employeeOrderForm.setEmployeeFilesId(employeeFilesId);
            String employeeOrderFormId = employeeMaintainService.createEmployeeOrderForm(this.getBizObjectFacade(),
                    employeeOrderForm, customerService);
            //创建社保申报
            if (socialSecurityDeclare.getStartMonth() != null) {
                socialSecurityDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createSocialSecurityDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        socialSecurityDeclare, customerService);
            }
            //创建公积金申报
            if (providentFundDeclare.getStartMonth() != null) {
                providentFundDeclare.setEmployeeOrderFormId(employeeOrderFormId);
                employeeMaintainService.createProvidentFundDeclare(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                        providentFundDeclare, customerService);
            }

            // 判断批量预派（省外）
            List<BatchPreDispatch> batchPreDispatches = new ArrayList <>();
            if (Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(employeeFiles.getSocialSecurityCity()) < 0) {
                BatchPreDispatch batchPreDispatch = new BatchPreDispatch(employeeFiles.getEmployeeName(),
                        employeeFiles.getIdNo(), employeeFiles.getMobile(),
                        nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getProvidentFundRatio(),
                        nationwideDispatch.getSuppleProvidentFundRatio(), nationwideDispatch.getProvidentFundAmount(),
                        employeeFiles.getEntryTime(), employeeFiles.getReportEntryTime(),
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getEntryDescription());
                batchPreDispatches.add(batchPreDispatch);
            }
            // 生成批量预派
            batchPreDispatchService.addBatchPreDispatchs(this.getUserId(), this.getOrganizationFacade(),
                    batchPreDispatches);
            return this.getOkResponseResult("success", "增员提交成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：获取补缴月份
     * @param timeNode
     * @param curTime 当前时间
     * @param startTime 开始缴纳时间
     * @return int
     * @throws
     * @author liulei
     * @Date 2020/2/14 14:30
     */
    private int getMonthDifference(String timeNode, Date curTime, Date startTime) throws Exception{
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

        Double node1 = StringUtils.isBlank(timeNode) ? 0.0 : Double.parseDouble(timeNode);
        int node = node1.intValue();
        if (curDay > node) {
            curMonth++;
        }

        int monthDifference = (curYear - startYear) * 12 + (curMonth - startMonth);
        monthDifference++;
        return monthDifference;
    }

    /**
     * 方法说明：增员_客户创建员工档案
     * @param addEmployee
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/2/26 13:36
     */
    private EmployeeFiles getEmployeeFilesByAddEmployee(AddEmployee addEmployee) throws Exception{
        //委托单位
        String entrustedUnit = employeeMaintainService.getDispatchUnitByClientName(addEmployee.getClientName());
        if (StringUtils.isBlank(entrustedUnit)) {
            throw new Exception("没有获取到委托单位。");
        }
        // 性别&出生日期
        Map <String, String> genderAndBirthday = getGenderAndBirthdayByIdentityNo(addEmployee.getIdentityNo(),
                addEmployee.getIdentityNoType());
        // 出生日期
        Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;
        // 社保福利办理方
        AppointmentSheet appointmentSheet = null;
        String socialSecurityWelfareHandler = null;
        String providentFundWelfareHandler = null;
        if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity())) {
            appointmentSheet =
                    employeeMaintainService.getWelfareHandlerByClientNameAndCity(addEmployee.getClientName(),
                            addEmployee.getSocialSecurityCity());
            if (appointmentSheet == null) {
                throw new RuntimeException("没有查询到福利办理方！");
            }
            socialSecurityWelfareHandler = appointmentSheet.getSWelfareHandler();
        }
        if (StringUtils.isNotBlank(addEmployee.getProvidentFundCity())) {
            if (addEmployee.getProvidentFundCity().equals(addEmployee.getSocialSecurityCity())) {
                providentFundWelfareHandler = appointmentSheet.getGWelfareHandler();
            } else {
                appointmentSheet =
                        employeeMaintainService.getWelfareHandlerByClientNameAndCity(addEmployee.getClientName(),
                                addEmployee.getProvidentFundCity());
                if (appointmentSheet == null) {
                    throw new RuntimeException("没有查询到福利办理方！");
                }
                providentFundWelfareHandler = appointmentSheet.getGWelfareHandler();
            }
        }

        EmployeeFiles employeeFiles = new EmployeeFiles(entrustedUnit, addEmployee.getClientName(),
                addEmployee.getEmployeeName(), addEmployee.getIdentityNoType(), addEmployee.getIdentityNo(),
                genderAndBirthday.get("gender"), birthDate, addEmployee.getEmployeeNature(),
                addEmployee.getFamilyRegisterNature(), addEmployee.getMobile(), addEmployee.getContractStartTime(),
                addEmployee.getContractEndTime(), addEmployee.getContractSalary(), addEmployee.getSocialSecurityCity(),
                addEmployee.getProvidentFundCity(), addEmployee.getCreatedTime(), addEmployee.getEntryTime(),
                addEmployee.getSocialSecurityStartTime(), addEmployee.getProvidentFundStartTime(),
                socialSecurityWelfareHandler, providentFundWelfareHandler, addEmployee.getRemark(),
                addEmployee.getEmail());
        return  employeeFiles;
    }

    /**
     * 方法说明：增员_上海创建员工档案
     * @param shAddEmployee
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/2/26 13:36
     */
    private EmployeeFiles getEmployeeFilesByAddEmployee(ShAddEmployee shAddEmployee) throws Exception{
        //委托单位
        String entrustedUnit = "北京外企德科人力资源服务上海有限公司";
        // 性别&出生日期
        Map <String, String> genderAndBirthday = getGenderAndBirthdayByIdentityNo(shAddEmployee.getIdentityNo(),
                shAddEmployee.getIdentityNoType());
        // 出生日期
        Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;

        // 福利办理方
        AppointmentSheet appointmentSheet = null;
        if (StringUtils.isNotBlank(shAddEmployee.getCityName())) {
            appointmentSheet =
                    employeeMaintainService.getWelfareHandlerByClientNameAndCity(shAddEmployee.getClientName(),
                            shAddEmployee.getCityName());
            if (appointmentSheet == null) {
                throw new RuntimeException("没有查询到福利办理方！");
            }
        }

        EmployeeFiles employeeFiles = new EmployeeFiles(entrustedUnit, shAddEmployee.getClientName(),
                shAddEmployee.getEmployeeName(), shAddEmployee.getIdentityNoType(), shAddEmployee.getIdentityNo(),
                genderAndBirthday.get("gender"), birthDate, "代理", shAddEmployee.getMobile(),
                shAddEmployee.getCityName(), shAddEmployee.getCityName(), shAddEmployee.getCreatedTime(),
                shAddEmployee.getEntryTime(), shAddEmployee.getBenefitStartTime(),
                shAddEmployee.getProvidentFundStartTime(), appointmentSheet.getSWelfareHandler(), appointmentSheet.getGWelfareHandler(),
                shAddEmployee.getInductionRemark(), shAddEmployee.getMail());

        return  employeeFiles;
    }

    /**
     * 方法说明：增员_全国创建员工档案
     * @param nationwideDispatch
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/2/26 13:36
     */
    private EmployeeFiles getEmployeeFilesByAddEmployee(NationwideDispatch nationwideDispatch) throws Exception{
        // 性别&出生日期
        Map <String, String> genderAndBirthday = getGenderAndBirthdayByIdentityNo(nationwideDispatch.getIdentityNo(),
                nationwideDispatch.getIdentityNoType());
        // 出生日期
        Date birthDate = StringUtils.isNotBlank(genderAndBirthday.get("birthday")) ?
                DateUtils.parseDate(genderAndBirthday.get("birthday"), Constants.PARSE_PATTERNS) : null;
        // 福利办理方
        AppointmentSheet appointmentSheet = null;
        if (StringUtils.isNotBlank(nationwideDispatch.getInvolved())) {
            appointmentSheet =
                    employeeMaintainService.getWelfareHandlerByClientNameAndCity(nationwideDispatch.getBusinessCustomerName(),
                            nationwideDispatch.getInvolved());
            if (appointmentSheet == null) {
                throw new RuntimeException("没有查询到福利办理方！");
            }
        }

        EmployeeFiles employeeFiles = new EmployeeFiles(nationwideDispatch.getContractingSupplier(),
                nationwideDispatch.getBusinessCustomerName(), nationwideDispatch.getEmployeeName(),
                nationwideDispatch.getIdentityNoType(), nationwideDispatch.getIdentityNo(),
                genderAndBirthday.get("gender"), birthDate, "代理", nationwideDispatch.getContactNumber(),
                nationwideDispatch.getInvolved(), nationwideDispatch.getInvolved(), nationwideDispatch.getCreatedTime(),
                nationwideDispatch.getEntryDate(), nationwideDispatch.getSServiceFeeStartDate(),
                nationwideDispatch.getGServiceFeeStartDate(), appointmentSheet.getSWelfareHandler(), appointmentSheet.getGWelfareHandler(),
                nationwideDispatch.getRemark(), nationwideDispatch.getEmployeeEmail());

        return  employeeFiles;

    }

    /**
     * 方法说明：根据身份证号获取生日和性别
     * @param identityNo 身份证号
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author liulei
     * @Date 2020/2/26 13:56
     */
    private Map<String, String> getGenderAndBirthdayByIdentityNo(String identityNo, String identityNoType) throws Exception{
        Map<String, String> map = new HashMap <>();
        if (!"身份证".equals(identityNoType)) {
            return map;
        }
        // 性别
        String gender = null;
        // 出生日期
        String birthday = null;
        if (identityNo.length() == 18) {
            birthday = identityNo.substring(6, 10) + "-" + identityNo.substring(10, 12) + "-" + identityNo.substring(12,
                    14);
            String sexCode = identityNo.substring(identityNo.length() - 4, identityNo.length() - 1);
            gender = Integer.parseInt(sexCode) % 2 == 0 ? "女" : "男";
        } else if (identityNo.length() == 15) {
            birthday = "19" + identityNo.substring(6, 8) + "-" + identityNo.substring(8, 10) + "-" +
                    identityNo.substring(10, 12);
            String sexCode = identityNo.substring(identityNo.length() - 3, identityNo.length());
            gender = Integer.parseInt(sexCode) % 2 == 0 ? "女" : "男";
        } else {
            throw new Exception("身份证号码格式不正确。");
        }
        map.put("birthday", birthday);
        map.put("gender", gender);
        return map;
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
            DeleteEmployee deleteEmployee = updateEmployeeService.getDeleteEmployeeUpdateById(id);
            if (deleteEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            employeeMaintainService.deleteEmployeeUpdateSubmit(deleteEmployee);

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
            ShDeleteEmployee shDeleteEmployee = updateEmployeeService.getShDeleteEmployeeUpdateById(id);
            if (shDeleteEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            employeeMaintainService.shDeleteEmployeeUpdateSubmit(shDeleteEmployee);
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
            NationwideDispatch nationwideDispatch = updateEmployeeService.getQgDeleteEmployeeUpdateById(id);
            if (nationwideDispatch == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到修改表单数据！");
            }
            employeeMaintainService.qgDeleteEmployeeUpdateSubmit(nationwideDispatch);
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
            if (addEmployee.getSocialSecurityCity().indexOf("六安") >= 0) {
                Double base = CommonUtils.processingData(addEmployee.getSocialSecurityBase(), "四舍五入", "0");
                addEmployee.setSocialSecurityBase(base);
            }
            if (addEmployee.getProvidentFundCity().indexOf("六安") >= 0) {
                Double base = CommonUtils.processingData(addEmployee.getProvidentFundBase(), "四舍五入", "0");
                addEmployee.setProvidentFundBase(base);
            }
            // 获取原增员表单
            AddEmployee oldAddEmployee = addEmployeeService.getAddEmployeeById(addEmployee.getAddEmployeeId());
            if (oldAddEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            // 获取原员工档案
            EmployeeFiles oldEmployeeFiles = employeeFilesService.getEmployeeFilesByIdNoOrClientName(oldAddEmployee.getIdentityNo(),
                    oldAddEmployee.getClientName());
            if (oldEmployeeFiles == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应员工档案数据。");
            }
            // 原社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    addEmployeeService.getSocialSecurityDeclareByClientNameAndIdentityNo(oldAddEmployee.getClientName(),
                            oldAddEmployee.getIdentityNo());
            if (oldSocialSecurityDeclare == null) {
                oldSocialSecurityDeclare = new SocialSecurityDeclare();
            }
            // 原公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    addEmployeeService.getProvidentFundDeclareByClientNameAndIdentityNo(oldAddEmployee.getClientName(),
                            oldAddEmployee.getIdentityNo());
            if (oldProvidentFundDeclare == null) {
                oldProvidentFundDeclare = new ProvidentFundDeclare();
            }
            // 原员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormById(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            if (oldEmployeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应员工订单数据。");
            }

            // 创建时间还是原来的创建时间
            addEmployee.setId(oldAddEmployee.getId());
            addEmployee.setCreatedTime(oldAddEmployee.getCreatedTime());
            // 申报人
            String applicant = oldEmployeeFiles.getReportRecruits();

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(addEmployee);
            employeeFiles.setName(employeeFiles.getEmployeeName());
            employeeFiles.setReportRecruits(applicant);
            employeeFiles.setEntryNotice(employeeFiles.getEntryNotice());

            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(addEmployee.getSocialSecurityCity())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && addEmployee.getSocialSecurityStartTime() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();
            }
            if (StringUtils.isNotBlank(employeeFiles.getProvidentFundCity())) {
                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" : employeeFiles.getSocialSecurityArea();
                if (employeeFiles.getProvidentFundCity().equals(employeeFiles.getSocialSecurityCity())
                        && welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && addEmployee.getProvidentFundStartTime() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 根据公司名称，委托单位，地区，员工性质获取业务员和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(),
                            employeeFiles.getEmployeeNature());
            String salesman = salesmanAndServiceFee.get("salesman") == null ? "" : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";

            employeeFiles.setSalesman(salesman);


            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal = employeeMaintainService.getTimeNode(addEmployee.getClientName(),
                    addEmployee.getSocialSecurityCity());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), addEmployee.getSocialSecurityStartTime());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), addEmployee.getProvidentFundStartTime());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }

            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    addEmployee.getSocialSecurityCity(), addEmployee.getSocialSecurityStartTime(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getProvidentFundCity(),
                    addEmployee.getProvidentFundStartTime(), addEmployee.getProvidentFundBase(),
                    addEmployee.getCompanyProvidentFundBl(), addEmployee.getEmployeeProvidentFundBl(), personal);
            employeeOrderForm.setId(oldEmployeeOrderForm.getId());

            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare = new SocialSecurityDeclare(addEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), addEmployee.getIdentityNoType(),
                    addEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), addEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getSocialSecurityArea(), applicant,
                    addEmployee.getCreatedTime(), addEmployee.getContractStartTime(), addEmployee.getContractEndTime(),
                    addEmployee.getSocialSecurityStartTime(), addEmployee.getContractSalary(),
                    addEmployee.getSocialSecurityBase(), addEmployee.getMobile(), weatherPartWorkInjury,
                    socialSecurityOperateLeader, employeeOrderForm.getSocialSecurityDetail());
            socialSecurityDeclare.setId(oldSocialSecurityDeclare.getId());
            socialSecurityDeclare.setName(employeeFiles.getEmployeeName());
            socialSecurityDeclare.setEmployeeOrderFormId(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            socialSecurityDeclare.setIsChange("是");

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(addEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), addEmployee.getIdentityNoType(),
                    addEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), addEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getProvidentFundArea(), applicant,
                    addEmployee.getCreatedTime(), addEmployee.getProvidentFundStartTime(),
                    addEmployee.getProvidentFundBase(), providentFundOperateLeader,
                    employeeOrderForm.getProvidentFundDetail());
            providentFundDeclare.setId(oldProvidentFundDeclare.getId());
            providentFundDeclare.setName(employeeFiles.getEmployeeName());
            providentFundDeclare.setEmployeeOrderFormId(oldProvidentFundDeclare.getEmployeeOrderFormId());
            providentFundDeclare.setIsChange("是");

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }
            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            employeeMaintainService.addEmployeeUpdateSubmit(addEmployee, employeeFiles, employeeOrderForm,
                    socialSecurityDeclare, providentFundDeclare);

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
            if (shAddEmployee.getCityName().indexOf("六安") >= 0) {
                Double sbase = CommonUtils.processingData(shAddEmployee.getSocialSecurityBase(), "四舍五入", "0");
                shAddEmployee.setSocialSecurityBase(sbase);
                Double gbase = CommonUtils.processingData(shAddEmployee.getProvidentFundBase(), "四舍五入", "0");
                shAddEmployee.setProvidentFundBase(gbase);
            }
            // 获取原增员表单
            ShAddEmployee oldShAddEmployee =
                    addEmployeeService.getShAddEmployeeById(shAddEmployee.getShAddEmployeeId());
            if (oldShAddEmployee == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            //获取员工档案
            EmployeeFiles oldEmployeeFiles = employeeFilesService.getEmployeeFilesByIdNoOrClientName(oldShAddEmployee.getIdentityNo(),
                    oldShAddEmployee.getClientName());
            if (oldEmployeeFiles == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工档案数据！");
            }
            // 社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    addEmployeeService.getSocialSecurityDeclareByClientNameAndIdentityNo(oldShAddEmployee.getClientName(),
                            oldShAddEmployee.getIdentityNo());
            if (oldSocialSecurityDeclare == null) {
                oldSocialSecurityDeclare = new SocialSecurityDeclare();
            }
            // 公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    addEmployeeService.getProvidentFundDeclareByClientNameAndIdentityNo(oldShAddEmployee.getClientName(),
                            oldShAddEmployee.getIdentityNo());
            if (oldProvidentFundDeclare == null) {
                oldProvidentFundDeclare = new ProvidentFundDeclare();
            }
            // 员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormById(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            if (oldEmployeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工订单数据！");
            }

            // 创建时间还是原来的创建时间
            shAddEmployee.setId(oldShAddEmployee.getId());
            shAddEmployee.setCreatedTime(oldShAddEmployee.getCreatedTime());
            // 申报人
            String applicant = oldEmployeeFiles.getReportRecruits();

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(shAddEmployee);
            employeeFiles.setName(employeeFiles.getEmployeeName());
            employeeFiles.setReportRecruits(applicant);
            employeeFiles.setEntryNotice(employeeFiles.getEntryNotice());

            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = null;
            String providentFundOperateLeader = null;
            if (StringUtils.isNotBlank(shAddEmployee.getCityName())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && shAddEmployee.getBenefitStartTime() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();

                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && shAddEmployee.getProvidentFundStartTime() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 查询客服和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(), "代理");
            String salesman = salesmanAndServiceFee.get("salesman") == null ? applicant : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";
            employeeFiles.setSalesman(salesman);

            if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
                employeeFiles.setProvidentFundChargeStart(employeeFiles.getSocialSecurityChargeStart());
            }

            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal = employeeMaintainService.getTimeNode(shAddEmployee.getClientName(),
                    shAddEmployee.getCityName());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), shAddEmployee.getBenefitStartTime());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), shAddEmployee.getProvidentFundStartTime());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }

            /** 员工订单实体*/
            EmployeeOrderForm employeeOrderForm = employeeMaintainService.getEmployeeOrderForm(
                    shAddEmployee.getClientName(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getClientName(),
                    shAddEmployee.getProvidentFundStartTime(), shAddEmployee.getProvidentFundBase(),
                    shAddEmployee.getPSupplementProvidentFund(), shAddEmployee.getUSupplementProvidentFund(), personal);
            employeeOrderForm.setId(oldEmployeeOrderForm.getId());
            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare = new SocialSecurityDeclare(shAddEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), shAddEmployee.getIdentityNoType(),
                    shAddEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), shAddEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getSocialSecurityArea(), applicant,
                    shAddEmployee.getCreatedTime(), shAddEmployee.getBenefitStartTime(),
                    shAddEmployee.getSocialSecurityBase(), shAddEmployee.getSocialSecurityBase(),
                    shAddEmployee.getMobile(), weatherPartWorkInjury, socialSecurityOperateLeader,
                    employeeOrderForm.getSocialSecurityDetail());
            socialSecurityDeclare.setId(oldSocialSecurityDeclare.getId());
            socialSecurityDeclare.setName(employeeFiles.getEmployeeName());
            socialSecurityDeclare.setEmployeeOrderFormId(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            socialSecurityDeclare.setIsChange("是");

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(shAddEmployee.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), shAddEmployee.getIdentityNoType(),
                    shAddEmployee.getIdentityNo(), employeeFiles.getEntrustedUnit(), shAddEmployee.getClientName(),
                    salesman, customerServices, employeeFiles.getProvidentFundArea(), applicant,
                    shAddEmployee.getCreatedTime(), shAddEmployee.getProvidentFundStartTime(),
                    shAddEmployee.getProvidentFundBase(), providentFundOperateLeader, employeeOrderForm.getProvidentFundDetail());
            providentFundDeclare.setId(oldProvidentFundDeclare.getId());
            providentFundDeclare.setName(employeeFiles.getEmployeeName());
            providentFundDeclare.setEmployeeOrderFormId(oldProvidentFundDeclare.getEmployeeOrderFormId());
            providentFundDeclare.setIsChange("是");

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }
            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            employeeMaintainService.shAddEmployeeUpdateSubmit(shAddEmployee, employeeFiles, employeeOrderForm,
                    socialSecurityDeclare, providentFundDeclare);

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
            if (nationwideDispatch.getInvolved().indexOf("六安") >= 0) {
                Double sbase = CommonUtils.processingData(nationwideDispatch.getSocialInsuranceAmount(), "四舍五入", "0");
                nationwideDispatch.setSocialInsuranceAmount(sbase);
                Double gbase = CommonUtils.processingData(nationwideDispatch.getProvidentFundAmount(), "四舍五入", "0");
                nationwideDispatch.setProvidentFundAmount(gbase);
            }
            // 获取原增员表单
            NationwideDispatch oldNationwideDispatch =
                    addEmployeeService.getNationwideDispatchById(nationwideDispatch.getNationwideDispatchId());
            if (oldNationwideDispatch == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到对应增员表单数据。");
            }
            //获取员工档案
            EmployeeFiles oldEmployeeFiles = employeeFilesService.getEmployeeFilesByIdNoOrClientName(nationwideDispatch.getIdentityNo(),
                    nationwideDispatch.getBusinessCustomerName());
            if (oldEmployeeFiles == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工档案数据！");
            }
            // 社保申报实体
            SocialSecurityDeclare oldSocialSecurityDeclare =
                    addEmployeeService.getSocialSecurityDeclareByClientNameAndIdentityNo(
                            nationwideDispatch.getBusinessCustomerName(), nationwideDispatch.getIdentityNo());
            if (oldSocialSecurityDeclare == null) {
                oldSocialSecurityDeclare = new SocialSecurityDeclare();
            }
            // 公积金申报实体
            ProvidentFundDeclare oldProvidentFundDeclare =
                    addEmployeeService.getProvidentFundDeclareByClientNameAndIdentityNo(
                            nationwideDispatch.getBusinessCustomerName(), nationwideDispatch.getIdentityNo());
            if (oldProvidentFundDeclare == null) {
                oldProvidentFundDeclare = new ProvidentFundDeclare();
            }
            // 员工订单实体
            EmployeeOrderForm oldEmployeeOrderForm =
                    addEmployeeService.getEmployeeOrderFormById(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            if (oldEmployeeOrderForm == null) {
                return this.getErrResponseResult("error", 404l, "没有获取到员工订单数据！");
            }
            // 申请人
            nationwideDispatch.setId(oldNationwideDispatch.getId());
            nationwideDispatch.setCreatedTime(oldNationwideDispatch.getCreatedTime());
            // 申报人
            String applicant = oldEmployeeFiles.getReportRecruits();

            /** 员工档案*/
            EmployeeFiles employeeFiles = getEmployeeFilesByAddEmployee(nationwideDispatch);
            employeeFiles.setName(employeeFiles.getEmployeeName());
            employeeFiles.setReportRecruits(applicant);
            employeeFiles.setEntryNotice(employeeFiles.getEntryNotice());

            // 运行负责人
            OperateLeader operateLeader = null;
            String socialSecurityOperateLeader = "";
            String providentFundOperateLeader = "";
            if (StringUtils.isNotBlank(nationwideDispatch.getInvolved())) {
                operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                        employeeFiles.getSocialSecurityCity(), employeeFiles.getSocialSecurityArea());
                if (operateLeader == null && nationwideDispatch.getSServiceFeeStartDate() != null) {
                    return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                }
                socialSecurityOperateLeader = operateLeader.getSocialSecurityLeader();

                String welfareHandler = employeeFiles.getSocialSecurityArea() == null ? "" :
                        employeeFiles.getSocialSecurityArea();
                if (welfareHandler.equals(employeeFiles.getProvidentFundArea())) {
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                } else {
                    operateLeader = employeeMaintainService.getOperateLeaderByCityAndWelfareHandler(
                            employeeFiles.getProvidentFundCity(), employeeFiles.getProvidentFundArea());
                    if (operateLeader == null && nationwideDispatch.getGServiceFeeStartDate() != null) {
                        return this.getErrResponseResult("error", 404l, "没有获取到运行负责人！");
                    }
                    providentFundOperateLeader = operateLeader.getProvidentFundLeader();
                }
            }

            // 查询客服和服务费
            Map <String, Object> salesmanAndServiceFee =
                    clientService.getClientSalesmanAndFee(employeeFiles.getClientName(),
                            employeeFiles.getEntrustedUnit(), employeeFiles.getSocialSecurityCity(), "代理");
            String salesman = salesmanAndServiceFee.get("salesman") == null ? applicant : salesmanAndServiceFee.get("salesman").toString();
            String serviceFee = salesmanAndServiceFee.get("fee") == null ? "" : salesmanAndServiceFee.get("fee").toString();
            // 客服
            String[] strArr = salesman.split(",");
            String userId = strArr[0].substring(8, strArr[0].length()-1);
            UserModel customerService = this.getOrganizationFacade().getUser(userId);
            String  customerServices = "[{\"id\":\"" + customerService.getDepartmentId() + "\",\"type\":1}]";
            employeeFiles.setSalesman(salesman);

            // 获取当前时间节点,如果是个性化客户，获取个性化设置
            Map <String, String> personal = employeeMaintainService.getTimeNode(nationwideDispatch.getBusinessCustomerName(),
                    nationwideDispatch.getInvolved());
            String timeNode = personal.get("time_node");
            // 补缴月份
            int sMonthDifference = getMonthDifference(timeNode, new Date(), nationwideDispatch.getSServiceFeeStartDate());
            int gMonthDifference = getMonthDifference(timeNode, new Date(), nationwideDispatch.getGServiceFeeStartDate());
            if (sMonthDifference > 0) {
                personal.put("pay_month_s", String.valueOf(sMonthDifference));
            }
            if (gMonthDifference > 0) {
                personal.put("pay_month_g", String.valueOf(gMonthDifference));
            }

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
                    companyRatio, employeeRatio, personal);
            employeeOrderForm.setId(oldEmployeeOrderForm.getId());
            employeeOrderForm.setServiceFee(StringUtils.isBlank(serviceFee) ? 0.0 : Double.parseDouble(serviceFee));
            employeeOrderForm.setTotal(employeeOrderForm.getSum() + employeeOrderForm.getServiceFee());

            String weatherPartWorkInjury = "否";
            if (StringUtils.isNotBlank(employeeOrderForm.getWorkRelatedInjury())) {
                weatherPartWorkInjury = "是";
            }
            /** 社保申报实体*/
            SocialSecurityDeclare socialSecurityDeclare =
                    new SocialSecurityDeclare(nationwideDispatch.getEmployeeName(), employeeFiles.getGender(),
                            employeeFiles.getBirthDate(), nationwideDispatch.getIdentityNoType(),
                            nationwideDispatch.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                            nationwideDispatch.getBusinessCustomerName(), salesman, customerServices,
                            employeeFiles.getSocialSecurityArea(), applicant, nationwideDispatch.getCreatedTime(),
                            nationwideDispatch.getSServiceFeeStartDate(),
                            nationwideDispatch.getSocialInsuranceAmount(),
                            nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatch.getContactNumber(),
                            weatherPartWorkInjury, socialSecurityOperateLeader, employeeOrderForm.getSocialSecurityDetail());
            socialSecurityDeclare.setId(oldSocialSecurityDeclare.getId());
            socialSecurityDeclare.setName(employeeFiles.getEmployeeName());
            socialSecurityDeclare.setEmployeeOrderFormId(oldSocialSecurityDeclare.getEmployeeOrderFormId());
            socialSecurityDeclare.setIsChange("是");

            /** 公积金申报实体*/
            ProvidentFundDeclare providentFundDeclare = new ProvidentFundDeclare(nationwideDispatch.getEmployeeName(),
                    employeeFiles.getGender(), employeeFiles.getBirthDate(), nationwideDispatch.getIdentityNoType(),
                    nationwideDispatch.getIdentityNo(), employeeFiles.getEntrustedUnit(),
                    nationwideDispatch.getBusinessCustomerName(), salesman, customerServices,
                    employeeFiles.getProvidentFundArea(), applicant, nationwideDispatch.getCreatedTime(),
                    nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatch.getProvidentFundAmount(),
                    providentFundOperateLeader, employeeOrderForm.getProvidentFundDetail());
            providentFundDeclare.setId(oldProvidentFundDeclare.getId());
            providentFundDeclare.setName(employeeFiles.getEmployeeName());
            providentFundDeclare.setEmployeeOrderFormId(oldProvidentFundDeclare.getEmployeeOrderFormId());
            providentFundDeclare.setIsChange("是");

            /** 缴存总额*/
            Double totalDeposit = null;
            /** 企业缴存额*/
            Double corporatePayment = null;
            /** 个人缴存额*/
            Double personalDeposit = null;
            String housingFunds = employeeOrderForm.getHousingAccumulationFunds();
            if (StringUtils.isNotBlank(housingFunds)) {
                // housingFunds 保存类型: 缴存总额(corporatePayment+个人缴存额)
                housingFunds = housingFunds.substring(0, housingFunds.length() - 1);
                housingFunds = housingFunds.replaceAll("\\(", "#").replaceAll("\\+", "#");
                String fundsArr[] = housingFunds.split("#");
                if (StringUtils.isNotBlank(fundsArr[0])) {
                    totalDeposit = Double.parseDouble(fundsArr[0]);
                }
                if (StringUtils.isNotBlank(fundsArr[1])) {
                    corporatePayment = Double.parseDouble(fundsArr[1]);
                }
                if (StringUtils.isNotBlank(fundsArr[2])) {
                    personalDeposit = Double.parseDouble(fundsArr[2]);
                }
            }

            providentFundDeclare.setCorporatePayment(corporatePayment);
            providentFundDeclare.setPersonalDeposit(personalDeposit);
            providentFundDeclare.setTotalDeposit(totalDeposit);

            employeeMaintainService.qgAddEmployeeUpdateSubmit(nationwideDispatch, employeeFiles, employeeOrderForm,
                    socialSecurityDeclare, providentFundDeclare);
            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：修改员工钉钉状态为预点
     * @param ids 表单id
     * @param field 修改字段名称
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 14:08
     */
    @GetMapping("/updateOrderFormStatusToPrePoint")
    @ResponseBody
    public ResponseResult<String> updateOrderFormStatusToPrePoint(String ids, String field) {
        if (StringUtils.isBlank(ids)) {
            return this.getErrResponseResult("error", 404l, "没有获取到ids！");
        }
        if (StringUtils.isBlank(field)) {
            return this.getErrResponseResult("error", 404l, "没有获取到修改字段值！");
        }
        try {
            employeeMaintainService.updateOrderFormStatusToPrePoint(ids, field);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }
}
