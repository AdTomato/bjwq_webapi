package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.UpdateAddEmployeeDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.service.*;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.GetBizObjectModelUntils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
    private DeleteEmployeeService deleteEmployeeService;

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private BatchPreDispatchService batchPreDispatchService;

    @Resource
    private BatchEvacuationService batchEvacuationService;

    @Resource
    private BusinessInsuranceService businessInsuranceService;

    @Resource
    private UpdateEmployeeService updateEmployeeService;

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
            DeleteEmployee delEmployee = deleteEmployeeService.getDeleteEmployeeById(id);
            return deleteEmployeeSubmit(delEmployee);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员提交
     * @param delEmployee 减员实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:04
     */
    private ResponseResult <String> deleteEmployeeSubmit(DeleteEmployee delEmployee) throws Exception{
        /*delEmployee = CommonUtils.processingIdentityNo(delEmployee);*/
        //获取员工档案
        EmployeeFiles employeeFiles = getEmployeeFiles(delEmployee.getFirstLevelClientName(),
                delEmployee.getSecondLevelClientName(), delEmployee.getIdentityNo());

        employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, delEmployee.getCreatedTime(),
                "[{\"id\":\"" + delEmployee.getCreater() + "\",\"type\":3}]", delEmployee.getLeaveTime(),
                delEmployee.getSocialSecurityEndTime(), delEmployee.getProvidentFundEndTime(),
                delEmployee.getLeaveReason(), delEmployee.getRemark(), delEmployee.getId());

        boolean sIsOut = StringUtils.isNotBlank(delEmployee.getSocialSecurityCity()) &&
                Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getSocialSecurityCity()) < 0 ? true : false;
        boolean gIsOut = StringUtils.isNotBlank(delEmployee.getProvidentFundCity()) &&
                Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delEmployee.getProvidentFundCity()) < 0 ? true : false;

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
        addEmployeeService.updateEmployeeFiles(employeeFiles);

        // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
        businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

        if (sIsOut && gIsOut) {
            return this.getOkResponseResult("success", "操作成功!");
        }

        deleteEmployeeService.createDeleteEmployeeData(delEmployee, employeeFiles, this.getBizObjectFacade(),
                this.getWorkflowInstanceFacade());

        return this.getOkResponseResult("success", "操作成功!");
    }

    /**
     * 方法说明：获取员工档案
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @param identityNo
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/5/15 14:10
     */
    private EmployeeFiles getEmployeeFiles(String firstLevelClientName, String secondLevelClientName,
                                           String identityNo) throws Exception {
        EmployeeFiles employeeFiles = addEmployeeService.getEmployeeFilesByClientNameAndIdentityNo(firstLevelClientName,
                secondLevelClientName, identityNo);
        if (employeeFiles == null) {
            throw new RuntimeException("没有获取到员工档案数据！");
        }
        return employeeFiles;
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
            ShDeleteEmployee shDelEmployee = deleteEmployeeService.getShDeleteEmployeeById(id);
            return shDeleteEmployeeSubmit(shDelEmployee);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员上海
     * @param shDelEmployee 减员上海实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:08
     */
    private ResponseResult <String> shDeleteEmployeeSubmit(ShDeleteEmployee shDelEmployee) throws Exception {
        shDelEmployee = CommonUtils.processingIdentityNo(shDelEmployee);
        //获取员工档案
        EmployeeFiles employeeFiles = getEmployeeFiles(shDelEmployee.getFirstLevelClientName(),
                shDelEmployee.getSecondLevelClientName(), shDelEmployee.getIdentityNo());

        employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, shDelEmployee.getCreatedTime(),
                "[{\"id\":\"" + shDelEmployee.getCreater() + "\",\"type\":3}]", shDelEmployee.getDepartureTime(),
                shDelEmployee.getChargeEndTime(), shDelEmployee.getChargeEndTime(), shDelEmployee.getLeaveReason(),
                shDelEmployee.getLeaveRemark(), shDelEmployee.getId());

        // 更新员工档案数据
        addEmployeeService.updateEmployeeFiles(employeeFiles);

        // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
        businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

        return this.getOkResponseResult("success", "操作成功!");
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
            NationwideDispatch qgDelEmployee = deleteEmployeeService.getQgDeleteEmployeeById(id);
            return qgDeleteEmployeeSubmit(qgDelEmployee);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：减员全国
     * @param qgDelEmployee 减员全国实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:08
     */
    private ResponseResult <String> qgDeleteEmployeeSubmit(NationwideDispatch qgDelEmployee) throws Exception {
        /*qgDelEmployee = CommonUtils.processingIdentityNo(qgDelEmployee);*/
        EmployeeFiles employeeFiles = getEmployeeFiles(qgDelEmployee.getFirstLevelClientName(),
                qgDelEmployee.getSecondLevelClientName(), qgDelEmployee.getIdentityNo());

        employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, qgDelEmployee.getCreatedTime(),
                "[{\"id\":\"" + qgDelEmployee.getCreater() + "\",\"type\":3}]", qgDelEmployee.getDepartureDate(),
                qgDelEmployee.getSServiceFeeEndDate(), qgDelEmployee.getGServiceFeeEndDate(),
                qgDelEmployee.getSocialSecurityStopReason(), qgDelEmployee.getDepartureRemark(), qgDelEmployee.getId());

        // 更新员工档案数据
        addEmployeeService.updateEmployeeFiles(employeeFiles);

        // 修改员工补充福利（商保，体检，代发福利）的员工状态为已离职
        businessInsuranceService.updateEmployeeStatus(employeeFiles.getFirstLevelClientName(),
                employeeFiles.getSecondLevelClientName(), employeeFiles.getIdNo(), EMPLOYEE_STATUS_QUIT);

        return this.getOkResponseResult("success", "激发停缴社保，公积金流程成功!");
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
            AddEmployee addEmployee = addEmployeeService.getAddEmployeeById(id);
            return addEmployeeSubmit(addEmployee);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员客户
     * @param addEmployee 增员客户实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:12
     */
    private ResponseResult <String> addEmployeeSubmit(AddEmployee addEmployee) throws Exception{
        // 处理身份证号码
        /*addEmployee = CommonUtils.processingIdentityNo(addEmployee);*/
        // 判断是否已经存在员工档案
        EmployeeFiles employeeFiles =
                addEmployeeService.getEmployeeFilesByClientNameAndIdentityNo(addEmployee.getFirstLevelClientName(),
                        addEmployee.getSecondLevelClientName(), addEmployee.getIdentityNo());
        if (employeeFiles == null) {
            employeeFiles = new EmployeeFiles(addEmployee);
            // 生成员工档案数据
            String employeeFilesId = createEmployeeFiles(employeeFiles, addEmployee.getCreater());

            boolean sIsOut = addEmployee.getSocialSecurityBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getSocialSecurityCity()) < 0 ? true : false;
            boolean gIsOut = addEmployee.getProvidentFundBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(addEmployee.getProvidentFundCity()) < 0 ? true : false;

            if (sIsOut || gIsOut) {
                // 批量生成预派
                addBatchPreDispatch(addEmployee, sIsOut, gIsOut);
            }
            if (sIsOut && gIsOut) {
                return this.getOkResponseResult("success", "增员提交成功!");
            }
            // 如果员工性质是“派遣”，“外包”需要创建劳动合同
            if ("派遣".equals(addEmployee.getEmployeeNature()) || "外包".equals(addEmployee.getEmployeeNature())) {
                LaborContractInfo laborContractInfo = new LaborContractInfo(addEmployee);
                laborContractInfo.setEmployeeFilesId(employeeFilesId);
                laborContractInfoService.saveLaborContractInfo(laborContractInfo);
            }

            ServiceChargeUnitPrice price = addEmployeeService.createAddEmployeeData(addEmployee, employeeFilesId,
                    this.getBizObjectFacade(), this.getWorkflowInstanceFacade());
            // 生成订单的服务费等数据
            if (price != null) {
                updateEmployeeService.addEmployeeOrderFormDetails(price, price.getOrderFormId());
            }
        } else {
            // 存在历史数据，此时增加没有的数据
            ServiceChargeUnitPrice price = addEmployeeService.addAddEmployeeData(addEmployee, employeeFiles, this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade());
            if (price != null) {
                updateEmployeeService.addEmployeeOrderFormDetails(price, price.getOrderFormId());
            }
        }

        return this.getOkResponseResult("success", "增员提交成功!");
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
            ShAddEmployee shAddEmployee = addEmployeeService.getShAddEmployeeById(id);
            return shAddEmployeeSubmit(shAddEmployee);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员上海
     * @param shAddEmployee 增员上海实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:12
     */
    private ResponseResult<String> shAddEmployeeSubmit(ShAddEmployee shAddEmployee) throws Exception{
        // 身份证号验证
        /*shAddEmployee = CommonUtils.processingIdentityNo(shAddEmployee);*/
        if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
            shAddEmployee.setProvidentFundStartTime(shAddEmployee.getBenefitStartTime());
        }
        // 判断是否已经存在员工档案
        EmployeeFiles employeeFiles =
                addEmployeeService.getEmployeeFilesByClientNameAndIdentityNo(shAddEmployee.getFirstLevelClientName(),
                        shAddEmployee.getSecondLevelClientName(), shAddEmployee.getIdentityNo());
        if (employeeFiles != null) {
            boolean needAdd = false;
            if (shAddEmployee.getSocialSecurityBase() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getSbAddEmployeeId())) {
                    // 此时是社保申报数据
                    employeeFiles.setSbAddEmployeeId(shAddEmployee.getId());
                    employeeFiles.setSocialSecurityCity(shAddEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(shAddEmployee.getBenefitStartTime());
                    employeeFiles.setSocialSecurityBase(shAddEmployee.getSocialSecurityBase());
                    employeeFiles.setSWelfareHandler(shAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的社保申报数据不是该增员数据，此时是新增
                    needAdd = true;
                    throw new RuntimeException("员工档案已经存在了该员工的社保申报信息！");
                }
            }
            if (shAddEmployee.getProvidentFundBase() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId())) {
                    employeeFiles.setGjjAddEmployeeId(shAddEmployee.getId());
                    employeeFiles.setProvidentFundCity(shAddEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(shAddEmployee.getProvidentFundStartTime());
                    employeeFiles.setProvidentFundBase(shAddEmployee.getProvidentFundBase());
                    employeeFiles.setGWelfareHandler(shAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的公积金申报数据不是该增员数据，此时是新增
                    needAdd = true;
                    throw new RuntimeException("员工档案已经存在了该员工的公积金申报信息！");
                }
            }
            addEmployeeService.updateEmployeeFiles(employeeFiles);
        } else {
            employeeFiles = new EmployeeFiles(shAddEmployee);
            createEmployeeFiles(employeeFiles, shAddEmployee.getCreater());
        }

        return this.getOkResponseResult("success", "增员提交成功!");
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
            NationwideDispatch nationwideDispatch = addEmployeeService.getQgAddEmployeeById(id);
            return qgAddEmployeeSubmit(nationwideDispatch);
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：增员全国
     * @param nationwideDispatch 增员全国实体
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/25 15:12
     */
    private ResponseResult<String> qgAddEmployeeSubmit(NationwideDispatch nationwideDispatch) throws Exception{
        // 身份证号验证
        /*nationwideDispatch = CommonUtils.processingIdentityNo(nationwideDispatch);*/
        // 判断是否已经存在员工档案
        EmployeeFiles employeeFiles =
                addEmployeeService.getEmployeeFilesByClientNameAndIdentityNo(nationwideDispatch.getFirstLevelClientName(),
                        nationwideDispatch.getSecondLevelClientName(), nationwideDispatch.getIdentityNo());
        if (employeeFiles != null) {
            boolean needAdd = false;
            if (nationwideDispatch.getSocialInsuranceAmount() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getSbAddEmployeeId())) {
                    // 此时是社保申报数据
                    employeeFiles.setSbAddEmployeeId(nationwideDispatch.getId());
                    employeeFiles.setSocialSecurityCity(nationwideDispatch.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(nationwideDispatch.getSServiceFeeStartDate());
                    employeeFiles.setSocialSecurityBase(nationwideDispatch.getSocialInsuranceAmount());
                    employeeFiles.setSWelfareHandler(nationwideDispatch.getWelfareHandler());
                } else {
                    // 改员工档案对应的社保申报数据不是该增员数据，此时是新增
                    needAdd = true;
                    throw new RuntimeException("员工档案已经存在了该员工的社保申报信息！");
                }
            }
            if (nationwideDispatch.getProvidentFundAmount() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId())) {
                    employeeFiles.setGjjAddEmployeeId(nationwideDispatch.getId());
                    employeeFiles.setProvidentFundCity(nationwideDispatch.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(nationwideDispatch.getGServiceFeeStartDate());
                    employeeFiles.setProvidentFundBase(nationwideDispatch.getProvidentFundAmount());
                    employeeFiles.setGWelfareHandler(nationwideDispatch.getWelfareHandler());
                } else {
                    // 改员工档案对应的公积金申报数据不是该增员数据，此时是新增
                    needAdd = true;
                    throw new RuntimeException("员工档案已经存在了该员工的公积金申报信息！");
                }
            }
            addEmployeeService.updateEmployeeFiles(employeeFiles);
        } else {
            employeeFiles = new EmployeeFiles(nationwideDispatch);
            createEmployeeFiles(employeeFiles, nationwideDispatch.getCreater());
        }

        return this.getOkResponseResult("success", "增员提交成功!");
    }

    /**
     * 方法说明：批量提交申报，停缴接口
     * @param ids 表单id,多个id使用“,”隔开
     * @param code 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；
     *             社保停缴：social_security_close；公积金停缴：provident_fund_close。）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    @GetMapping("/batchSubmit")
    @ResponseBody
    public ResponseResult<String> batchSubmit(String ids, String code) {
        try {
            List<String> idList = getListByIds(ids);
            employeeMaintainService.batchSubmit(this.getWorkflowInstanceFacade(), this.getUserId(), idList, code);
            return this.getOkResponseResult("success", "操作成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    private List<String> getListByIds(String ids) throws Exception{
        if (StringUtils.isBlank(ids)) {
            throw new RuntimeException("没有获取到表单id");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        if (idList == null && idList.size() == 0) {
            throw new RuntimeException("没有获取到表单id");
        }
        return idList;
    }

    /**
     * 方法说明：批量驳回申报，停缴接口
     * @param ids 表单id，多个id使用“,”隔开
     * @param code 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；
     *             社保停缴：social_security_close；公积金停缴：provident_fund_close。）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/3/12 13:22
     */
    @GetMapping("/batchReject")
    @ResponseBody
    public ResponseResult<String> batchReject(String ids, String code) {
        try {
            List<String> idList = getListByIds(ids);
            employeeMaintainService.batchReject(this.getWorkflowInstanceFacade(), this.getUserId(), idList, code);
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

            DeleteEmployee del = deleteEmployeeService.getDeleteEmployeeById(delUpdate.getDeleteEmployeeId());

            //获取员工档案
            EmployeeFiles employeeFiles =
                    deleteEmployeeService.getEmployeeFilesByDelEmployeeId(delUpdate.getDeleteEmployeeId());

            SocialSecurityClose sClose = deleteEmployeeService.getSocialSecurityCloseByDelEmployeeId(del.getId());

            ProvidentFundClose gClose = deleteEmployeeService.getProvidentFundCloseByDelEmployeeId(del.getId());
            // 原员工订单实体
            EmployeeOrderForm orderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            if (orderForm == null) {
                throw new RuntimeException("没有查询到对应的员工订单数据！");
            }
            if (delUpdate.getSocialSecurityEndTime() != null) {
                if(StringUtils.isBlank(employeeFiles.getSbDelEmployeeId()) || del.getId().equals(employeeFiles.getSbDelEmployeeId())) {
                    // 此时是社保停缴数据,员工档案原来没有社保数据，或者有当前表单饿数据
                    employeeFiles.setQuitDate(delUpdate.getLeaveTime());
                    employeeFiles.setSocialSecurityChargeEnd(delUpdate.getSocialSecurityEndTime());
                    employeeFiles.setQuitReason(delUpdate.getLeaveReason());
                    employeeFiles.setQuitRemark(delUpdate.getRemark());
                    employeeFiles.setSbDelEmployeeId(del.getId());

                    orderForm.setSocialSecurityChargeEnd(delUpdate.getSocialSecurityEndTime());
                } else {
                    // 改员工档案对应的社保停缴数据不是该减员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的社保停缴信息！");
                }
            }
            if (delUpdate.getProvidentFundEndTime() != null) {
                if(StringUtils.isBlank(employeeFiles.getGjjDelEmployeeId()) || del.getId().equals(employeeFiles.getGjjDelEmployeeId())) {
                    employeeFiles.setQuitDate(delUpdate.getLeaveTime());
                    employeeFiles.setProvidentFundChargeEnd(delUpdate.getProvidentFundEndTime());
                    employeeFiles.setQuitReason(delUpdate.getLeaveReason());
                    employeeFiles.setQuitRemark(delUpdate.getRemark());
                    employeeFiles.setGjjDelEmployeeId(del.getId());

                    orderForm.setProvidentFundChargeEnd(delUpdate.getProvidentFundEndTime());
                } else {
                    // 改员工档案对应的公积金停缴数据不是该减员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的公积金停缴信息！");
                }
            }
            // 更新员工档案数据
            addEmployeeService.updateEmployeeFiles(employeeFiles);

            updateEmployeeService.upateEmployeeOrderForm(orderForm);

            if (delUpdate.getSocialSecurityEndTime() != null  && employeeFiles.getSocialSecurityBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delUpdate.getSocialSecurityCity()) >= 0) {
                // 需要生成社保停缴
                if (sClose == null) {
                    deleteEmployeeService.createSocialSecurityClose(delUpdate, employeeFiles, orderForm.getId(),
                            this.getBizObjectFacade(), this.getWorkflowInstanceFacade());
                } else {
                    // 修改
                    sClose.setEmployeeName(delUpdate.getEmployeeName());
                    sClose.setGender(delUpdate.getGender());
                    sClose.setBirthday(delUpdate.getBirthday());
                    sClose.setIdentityNoType(delUpdate.getIdentityNoType());
                    sClose.setIdentityNo(delUpdate.getIdentityNo());
                    sClose.setWelfareHandler(delUpdate.getSWelfareHandler());
                    sClose.setChargeEndMonth(delUpdate.getSocialSecurityEndTime());
                    sClose.setResignationRemarks(delUpdate.getRemark());
                    sClose.setFirstLevelClientName(delUpdate.getFirstLevelClientName());
                    sClose.setSecondLevelClientName(delUpdate.getSecondLevelClientName());
                    sClose.setSubordinateDepartment(delUpdate.getSubordinateDepartment());
                    sClose.setCity(delUpdate.getSocialSecurityCity());
                    sClose.setOperator(delUpdate.getOperator());
                    sClose.setInquirer(delUpdate.getInquirer());
                    sClose.setInquirer(delUpdate.getInquirer());
                    sClose.setDelEmployeeId(delUpdate.getDeleteEmployeeId());
                    String handler = addEmployeeService.getHsLevyHandler(sClose.getCity(), sClose.getWelfareHandler(),
                            "社保", sClose.getSecondLevelClientName());
                    sClose.setOperateLeader(handler);

                    updateEmployeeService.updateSocialSecurityClose(sClose);
                }
            } else if (sClose != null) {
                // 不需要，删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.SOCIAL_SECURITY_CLOSE_SCHEMA,
                        sClose.getId());
            }

            if (delUpdate.getProvidentFundEndTime() != null  && employeeFiles.getProvidentFundBase() - 0d > 0d &&
                    Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(delUpdate.getProvidentFundCity()) >= 0) {
                // 需要生成社保停缴
                if (gClose == null) {
                    deleteEmployeeService.createProvidentFundClose(delUpdate, employeeFiles, orderForm.getId(),
                            this.getBizObjectFacade(), this.getWorkflowInstanceFacade());
                } else {
                    // 修改
                    gClose.setEmployeeName(delUpdate.getEmployeeName());
                    gClose.setGender(delUpdate.getGender());
                    gClose.setBirthday(delUpdate.getBirthday());
                    gClose.setIdentityNoType(delUpdate.getIdentityNoType());
                    gClose.setIdentityNo(delUpdate.getIdentityNo());
                    gClose.setWelfareHandler(delUpdate.getGWelfareHandler());
                    gClose.setChargeEndMonth(delUpdate.getProvidentFundEndTime());
                    gClose.setFirstLevelClientName(delUpdate.getFirstLevelClientName());
                    gClose.setSecondLevelClientName(delUpdate.getSecondLevelClientName());
                    gClose.setSubordinateDepartment(delUpdate.getSubordinateDepartment());
                    gClose.setCity(delUpdate.getProvidentFundCity());
                    gClose.setOperator(delUpdate.getOperator());
                    gClose.setInquirer(delUpdate.getInquirer());
                    gClose.setInquirer(delUpdate.getInquirer());
                    gClose.setDelEmployeeId(delUpdate.getDeleteEmployeeId());
                    String handler = addEmployeeService.getHsLevyHandler(gClose.getCity(), gClose.getWelfareHandler(),
                            "公积金", gClose.getSecondLevelClientName());
                    gClose.setOperateLeader(handler);

                    updateEmployeeService.updateProvidentFundClose(gClose);
                }
            } else if (gClose != null) {
                // 不需要，删除
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.PROVIDENT_FUND_CLOSE_SCHEMA,
                        gClose.getId());
            }

            updateEmployeeService.updateDeleteEmployee(delUpdate);

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
            ShDeleteEmployee del = deleteEmployeeService.getShDeleteEmployeeById(delUpdate.getShDeleteEmployeeId());

            //获取员工档案
            EmployeeFiles employeeFiles = deleteEmployeeService.getEmployeeFilesByDelEmployeeId(del.getId());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", delUpdate.getDepartureTime(),
                    delUpdate.getChargeEndTime(), delUpdate.getChargeEndTime(), delUpdate.getLeaveReason(),
                    delUpdate.getLeaveRemark(), del.getId());
            // 更新员工档案数据
            addEmployeeService.updateEmployeeFiles(employeeFiles);

            updateEmployeeService.updateShDeleteEmployee(delUpdate);

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
            NationwideDispatch del = deleteEmployeeService.getQgDeleteEmployeeById(delUpdate.getNationwideDispatchDelId());
            //获取员工档案
            EmployeeFiles employeeFiles = deleteEmployeeService.getEmployeeFilesByDelEmployeeId(del.getId());

            employeeFiles = setEmployeeFilesQuitInfo(employeeFiles, del.getCreatedTime(),
                    "[{\"id\":\"" + del.getCreater() + "\",\"type\":3}]", delUpdate.getDepartureDate(),
                    delUpdate.getSServiceFeeEndDate(), delUpdate.getGServiceFeeEndDate(), delUpdate.getSocialSecurityStopReason(),
                    delUpdate.getDepartureRemark(), del.getId());
            // 更新员工档案数据
            addEmployeeService.updateEmployeeFiles(employeeFiles);

            updateEmployeeService.updateQgDeleteEmployee(delUpdate);

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
            EmployeeFiles update = updateEmployeeService.getEmployeeFilesUpdateById(id);
            EmployeeFiles files = updateEmployeeService.getEmployeeFilesById(update.getEmployeeFilesId());
            // 隐藏字段赋值
            update.setStopGenerateBill(files.getStopGenerateBill());
            update.setIsOldEmployee(files.getStopGenerateBill());
            update.setSPaymentApplication(files.getSPaymentApplication());
            update.setGPaymentApplication(files.getGPaymentApplication());
            update.setSbAddEmployeeId(files.getSbAddEmployeeId());
            update.setGjjAddEmployeeId(files.getGjjAddEmployeeId());
            update.setSbDelEmployeeId(files.getSbDelEmployeeId());
            update.setGjjDelEmployeeId(files.getGjjDelEmployeeId());

            addEmployeeService.updateEmployeeFiles(update);
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

            AddEmployee updateAddEmployee = updateEmployeeService.getAddEmployeeUpdateById(id);
            updateAddEmployee = CommonUtils.processingIdentityNo(updateAddEmployee);

            // 获取原增员表单
            AddEmployee addEmployee = addEmployeeService.getAddEmployeeById(updateAddEmployee.getAddEmployeeId());

            // 获取原员工档案
            EmployeeFiles employeeFiles = addEmployeeService.getEmployeeFilesByAddEmployeeId(addEmployee.getId());
            // 社保申报数据
            SocialSecurityDeclare sDeclare = addEmployeeService.getSocialSecurityDeclareByAddEmployeeId(addEmployee.getId());
            // 公积金申报数据
            ProvidentFundDeclare pDeclare = addEmployeeService.getProvidentFundDeclareByAddEmployeeId(addEmployee.getId());
            // 原员工订单实体
            EmployeeOrderForm orderForm =
                    addEmployeeService.getEmployeeOrderFormByEmployeeFilesId(employeeFiles.getId());
            if (orderForm != null) {
                orderForm.setFirstLevelClientName(updateAddEmployee.getFirstLevelClientName());
                orderForm.setSecondLevelClientName(updateAddEmployee.getSecondLevelClientName());
                orderForm.setBusinessType(updateAddEmployee.getEmployeeNature());
                orderForm.setIdentityNo(updateAddEmployee.getIdentityNo());
                orderForm.setIdType(updateAddEmployee.getIdentityNoType());
                orderForm.setDetail(updateAddEmployee.getRemark());
            }
            // 根据当前表单生成增员数据
            UpdateAddEmployeeDto dto = addEmployeeService.getAddEmployeeData(updateAddEmployee, employeeFiles.getId());
            EmployeeOrderForm newOrderForm = dto.getOrderForm();
            SocialSecurityDeclare newSDeclare = dto.getSDeclare();
            ProvidentFundDeclare newPDeclare = dto.getPDeclare();
            if (newSDeclare == null && newPDeclare == null) {
                newOrderForm = null;
            }
            if (addEmployee.getId().equals(employeeFiles.getSbAddEmployeeId()) &&
                    addEmployee.getId().equals(employeeFiles.getGjjAddEmployeeId())) {
                String employeeOrderFormId = updateEmployeeOrderForm(newOrderForm, orderForm, true, true, newSDeclare, newPDeclare);
                updateSocialSecurityDeclare(newSDeclare, sDeclare, employeeOrderFormId);
                updateProvidentFundDeclare(newPDeclare, pDeclare, employeeOrderFormId);
            }else if (addEmployee.getId().equals(employeeFiles.getSbAddEmployeeId())) {
                String employeeOrderFormId = updateEmployeeOrderForm(newOrderForm, orderForm, true, false, newSDeclare, newPDeclare);
                // 该增员表单对应社保数据
                updateSocialSecurityDeclare(newSDeclare, sDeclare, employeeOrderFormId);

            }else if (addEmployee.getId().equals(employeeFiles.getGjjAddEmployeeId())) {
                String employeeOrderFormId = updateEmployeeOrderForm(newOrderForm, orderForm, false, true, newSDeclare, newPDeclare);
                // 该增员表单对应公积金数据
                updateProvidentFundDeclare(newPDeclare, pDeclare, employeeOrderFormId);
            }
            // 更新增员表单
            updateEmployeeService.updateAddEmployee(updateAddEmployee);

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    private String updateEmployeeOrderForm(EmployeeOrderForm newOrderForm, EmployeeOrderForm orderForm, boolean sb,
                                           boolean gjj, SocialSecurityDeclare newSDeclare,
                                           ProvidentFundDeclare newPDeclare) throws Exception {
        String employeeOrderFormId = "";
        // 该增员表单对应社保，公积金数据
        if (orderForm == null && newOrderForm != null) {
            // 原来没有员工订单,现在有了,新增
            ServiceChargeUnitPrice price = getServiceChargeUnitPrice(newOrderForm);
            if (price != null) {
                orderForm.setPrecollected(price.getPrecollected());
                orderForm.setPayCycle(price.getPayCycle());
            }
            employeeOrderFormId = addEmployeeService.createEmployeeOrderForm(newOrderForm, this.getBizObjectFacade());
            if (price != null) {
                updateEmployeeService.addEmployeeOrderFormDetails(price, orderForm.getId());
            }
            return employeeOrderFormId;
        } else if (orderForm != null && newOrderForm != null) {
            // 更新员工订单社保，公积金数据
            employeeOrderFormId = orderForm.getId();
            if (sb && gjj) {
                // 原增员数据申报了社保和公积金，更新社保，公积金数据
                orderForm = updateEmployeeOrderFormSbData(orderForm, newOrderForm);
                orderForm = updateEmployeeOrderFormGjjData(orderForm, newOrderForm);
                // 删除员工订单的所有子表数据
                updateEmployeeService.delEmployeeOrderFormDetails(orderForm.getId(), "3");
                updateEmployeeService.addEmployeeOrderFormDetails(orderForm.getId(), newOrderForm.getPayBackList(),
                        newOrderForm.getRemittanceList());
            } else if (sb) {
                // 更新订单的社保数据
                orderForm = updateEmployeeOrderFormSbData(orderForm, newOrderForm);
                // 删除员工订单的社保,服务费,增值税费,风险管理费,福利产品子表数据
                updateEmployeeService.delEmployeeOrderFormDetails(orderForm.getId(), "1");
                if (newSDeclare != null) {
                    updateEmployeeService.addEmployeeOrderFormDetails(orderForm.getId(), newSDeclare.getPayBackList(),
                            newSDeclare.getRemittanceList());
                }
            } else if (gjj) {
                // 更新订单的公积金数据
                orderForm = updateEmployeeOrderFormGjjData(orderForm, newOrderForm);
                // 删除员工订单的公积金,服务费,增值税费,风险管理费,福利产品子表数据
                updateEmployeeService.delEmployeeOrderFormDetails(orderForm.getId(), "2");
                if (newPDeclare != null) {
                    updateEmployeeService.addEmployeeOrderFormDetails(orderForm.getId(), newPDeclare.getPayBackList(),
                            newPDeclare.getRemittanceList());
                }
            }
        } else if (orderForm != null && newOrderForm == null){
            if (sb && gjj) {
                // 原增员数据申报了社保和公积金
                this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                        employeeOrderFormId);
                return employeeOrderFormId;
            } else if (sb) {
                // 删除订单的社保数据
                if (orderForm.getProvidentFundBase() - 0d > 0d) {
                    // 订单有公积金数据, 删除社保数据
                    orderForm = updateEmployeeOrderFormSbData(orderForm, new EmployeeOrderForm());

                    updateEmployeeService.delEmployeeOrderFormDetails(orderForm.getId(), "1");
                } else {
                    this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                            employeeOrderFormId);
                    return employeeOrderFormId;
                }
            } else if (gjj) {
                // 删除订单的公积金数据
                if (orderForm.getSocialSecurityBase() - 0d > 0d) {
                    // 订单有社保数据 ,删除公积金数据
                    orderForm = updateEmployeeOrderFormGjjData(orderForm, new EmployeeOrderForm());
                    updateEmployeeService.delEmployeeOrderFormDetails(orderForm.getId(), "2");
                } else {
                    this.getBizObjectFacade().removeBizObject(this.getUserId(), Constants.EMPLOYEE_ORDER_FORM_SCHEMA,
                            employeeOrderFormId);
                    return employeeOrderFormId;
                }
            }
        }
        ServiceChargeUnitPrice price = getServiceChargeUnitPrice(orderForm);
        if (price != null) {
            orderForm.setPrecollected(price.getPrecollected());
            orderForm.setPayCycle(price.getPayCycle());
            updateEmployeeService.addEmployeeOrderFormDetails(price, orderForm.getId());
        }
        updateEmployeeService.upateEmployeeOrderForm(orderForm);
        return employeeOrderFormId;
    }

    private ServiceChargeUnitPrice getServiceChargeUnitPrice(EmployeeOrderForm newOrderForm) {
        String city = StringUtils.isNotBlank(newOrderForm.getSocialSecurityCity()) ? newOrderForm.getSocialSecurityCity() :
                newOrderForm.getProvidentFundCity();
        ServiceChargeUnitPrice price = salesContractService.getServiceChargeUnitPrice(newOrderForm.getFirstLevelClientName(),
                newOrderForm.getBusinessType(), Constants.ALL_CITIES_IN_ANHUI_PROVINCE.indexOf(city) < 0 ? "省外" : "省内", city);
        return  price;
    }

    private EmployeeOrderForm updateEmployeeOrderFormGjjData(EmployeeOrderForm orderForm,
                                                             EmployeeOrderForm newOrderForm) {
        orderForm.setProvidentFundStatus(newOrderForm.getProvidentFundStatus());
        orderForm.setProvidentFundCity(newOrderForm.getProvidentFundCity());
        orderForm.setGWelfareHandler(newOrderForm.getGWelfareHandler());
        orderForm.setProvidentFundBase(newOrderForm.getProvidentFundBase());
        orderForm.setProvidentFundChargeStart(newOrderForm.getProvidentFundChargeStart());
        return orderForm;
    }

    private EmployeeOrderForm updateEmployeeOrderFormSbData(EmployeeOrderForm orderForm,
                                                            EmployeeOrderForm newOrderForm) {
        orderForm.setSocialSecurityStatus(newOrderForm.getSocialSecurityStatus());
        orderForm.setSocialSecurityCity(newOrderForm.getSocialSecurityCity());
        orderForm.setSWelfareHandler(newOrderForm.getSWelfareHandler());
        orderForm.setSocialSecurityBase(newOrderForm.getSocialSecurityBase());
        orderForm.setSocialSecurityChargeStart(newOrderForm.getSocialSecurityChargeStart());
        return orderForm;
    }

    private void updateProvidentFundDeclare(ProvidentFundDeclare newPDeclare, ProvidentFundDeclare pDeclare,
                                            String employeeOrderFormId) throws Exception {
        // 原增员表单同时申报了，社保，公积金数据
        if (newPDeclare != null && pDeclare == null) {
            // 修改后增员有社保申报，原没有社保申报,此时新增社保申报数据
            newPDeclare.setEmployeeOrderFormId(employeeOrderFormId);
            addEmployeeService.createProvidentFundDeclare(newPDeclare, this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade());
        } else if (newPDeclare != null && pDeclare != null) {
            // 修改后增员有社保申报，原也有社保申报,此时更新社保申报数据
            newPDeclare.setEmployeeOrderFormId(employeeOrderFormId);
            updateEmployeeService.updateProvidentFundDeclare(newPDeclare, pDeclare);
        } else if (newPDeclare == null && pDeclare != null){
            // 修改没有，原有，此时删除
            this.getBizObjectFacade().removeBizObject(this.getUserId(),
                    Constants.PROVIDENT_FUND_DECLARE_SCHEMA, pDeclare.getId());
        }
    }

    private void updateSocialSecurityDeclare(SocialSecurityDeclare newSDeclare, SocialSecurityDeclare sDeclare,
                                             String employeeOrderFormId) throws Exception{
        // 原增员表单同时申报了，社保，公积金数据
        if (newSDeclare != null && sDeclare == null) {
            // 修改后增员有社保申报，原没有社保申报,此时新增社保申报数据
            newSDeclare.setEmployeeOrderFormId(employeeOrderFormId);
            addEmployeeService.createSocialSecurityDeclare(newSDeclare, this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade());
        } else if (newSDeclare != null && sDeclare != null) {
            // 修改后增员有社保申报，原也有社保申报,此时更新社保申报数据
            newSDeclare.setEmployeeOrderFormId(employeeOrderFormId);
            updateEmployeeService.updateSocialSecurityDeclare(newSDeclare, sDeclare);
        } else if (newSDeclare == null && sDeclare != null) {
            // 修改没有，原有，此时删除
            this.getBizObjectFacade().removeBizObject(this.getUserId(),
                    Constants.SOCIAL_SECURITY_DECLARE_SCHEMA, sDeclare.getId());
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
            ShAddEmployee updateAddEmployee = updateEmployeeService.getShAddEmployeeUpdateById(id);
            updateAddEmployee = CommonUtils.processingIdentityNo(updateAddEmployee);

            if ("一致".equals(updateAddEmployee.getWhetherConsistent())) {
                updateAddEmployee.setProvidentFundStartTime(updateAddEmployee.getBenefitStartTime());
            }
            // 获取原增员表单
            ShAddEmployee addEmployee =
                    addEmployeeService.getShAddEmployeeById(updateAddEmployee.getShAddEmployeeId());

            // 获取原员工档案
            EmployeeFiles employeeFiles = addEmployeeService.getEmployeeFilesByAddEmployeeId(addEmployee.getId());

            if (updateAddEmployee.getSocialSecurityBase() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getSbAddEmployeeId()) || addEmployee.getId().equals(employeeFiles.getSbAddEmployeeId())) {
                    // 此时是社保申报数据,员工档案原来没有社保数据，或者有当前表单饿数据
                    employeeFiles.setSbAddEmployeeId(updateAddEmployee.getId());
                    employeeFiles.setSocialSecurityCity(updateAddEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(updateAddEmployee.getBenefitStartTime());
                    employeeFiles.setSocialSecurityBase(updateAddEmployee.getSocialSecurityBase());
                    employeeFiles.setSWelfareHandler(updateAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的社保申报数据不是该增员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的社保申报信息！");
                }
            }
            if (updateAddEmployee.getProvidentFundBase() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId()) || addEmployee.getId().equals(employeeFiles.getGjjAddEmployeeId())) {
                    employeeFiles.setGjjAddEmployeeId(updateAddEmployee.getId());
                    employeeFiles.setProvidentFundCity(updateAddEmployee.getCityName());
                    employeeFiles.setSocialSecurityChargeStart(updateAddEmployee.getProvidentFundStartTime());
                    employeeFiles.setProvidentFundBase(updateAddEmployee.getProvidentFundBase());
                    employeeFiles.setGWelfareHandler(updateAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的公积金申报数据不是该增员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的公积金申报信息！");
                }
            }
            addEmployeeService.updateEmployeeFiles(employeeFiles);

            updateEmployeeService.updateShAddEmployee(updateAddEmployee);
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
            NationwideDispatch updateAddEmployee = updateEmployeeService.getQgAddEmployeeUpdateById(id);

            updateAddEmployee = CommonUtils.processingIdentityNo(updateAddEmployee);

            // 获取原增员表单
            NationwideDispatch addEmployee =
                    addEmployeeService.getQgAddEmployeeById(updateAddEmployee.getNationwideDispatchId());

            //获取员工档案
            EmployeeFiles employeeFiles = addEmployeeService.getEmployeeFilesByAddEmployeeId(addEmployee.getId());

            if (updateAddEmployee.getSocialInsuranceAmount() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getSbAddEmployeeId()) || addEmployee.getId().equals(employeeFiles.getSbAddEmployeeId())) {
                    // 此时是社保申报数据,员工档案原来没有社保数据，或者有当前表单饿数据
                    employeeFiles.setSbAddEmployeeId(updateAddEmployee.getId());
                    employeeFiles.setSocialSecurityCity(updateAddEmployee.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(updateAddEmployee.getSServiceFeeStartDate());
                    employeeFiles.setSocialSecurityBase(updateAddEmployee.getSocialInsuranceAmount());
                    employeeFiles.setSWelfareHandler(updateAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的社保申报数据不是该增员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的社保申报信息！");
                }
            }
            if (updateAddEmployee.getProvidentFundAmount() - 0d > 0d) {
                if(StringUtils.isBlank(employeeFiles.getGjjAddEmployeeId()) || addEmployee.getId().equals(employeeFiles.getGjjAddEmployeeId())) {
                    employeeFiles.setGjjAddEmployeeId(updateAddEmployee.getId());
                    employeeFiles.setProvidentFundCity(updateAddEmployee.getInvolved());
                    employeeFiles.setSocialSecurityChargeStart(updateAddEmployee.getGServiceFeeStartDate());
                    employeeFiles.setProvidentFundBase(updateAddEmployee.getProvidentFundAmount());
                    employeeFiles.setGWelfareHandler(updateAddEmployee.getWelfareHandler());
                } else {
                    // 改员工档案对应的公积金申报数据不是该增员数据，此时是新增
                    throw new RuntimeException("员工档案已经存在了该员工的公积金申报信息！");
                }
            }
            addEmployeeService.updateEmployeeFiles(employeeFiles);

            updateEmployeeService.updateQgAddEmployee(updateAddEmployee);

            return this.getOkResponseResult("success", "操作成功");
        } catch (Exception e) {
            log.info(e.getMessage());
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
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i ++) {
                    String id = list.get(i).get("id").toString();
                    String workItemId = list.get(i).get("workItemId").toString();
                    // 提交流程
                    this.getWorkflowInstanceFacade().submitWorkItem(this.getUserId(), workItemId, true);
                    if ("add".equals(type)) {
                        /*AddEmployee addEmployee = addEmployeeService.getAddEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfo(addEmployee.getCreater(),
                                addEmployee.getIdentityNo(), addEmployee.getIdentityNoType(),
                                addEmployee.getEmployeeNature(), addEmployee.getSocialSecurityBase() - 0d > 0d ?
                                        addEmployee.getSocialSecurityCity() : null, addEmployee.getSWelfareHandler(),
                                addEmployee.getProvidentFundBase() - 0d > 0d ? addEmployee.getProvidentFundCity() :
                                        null, addEmployee.getGWelfareHandler());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            addEmployee.setReturnReason(queryInfo.getReturnReason());
                            addEmployeeService.updateAddEmployee(addEmployee);
                            continue;
                        } else {
                            addEmployee.setGender(queryInfo.getGender());
                            addEmployee.setBirthday(queryInfo.getBirthday());
                            addEmployee.setFirstLevelClientName(queryInfo.getFirstLevelClientName());
                            addEmployee.setSecondLevelClientName(queryInfo.getSecondLevelClientName());
                            addEmployee.setOperator(queryInfo.getOperator());
                            addEmployee.setInquirer(queryInfo.getInquirer());
                            addEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            addEmployeeService.updateAddEmployee(addEmployee);
                            addEmployeeSubmit(addEmployee);
                        }*/
                    } else if ("shAdd".equals(type)) {
                        /*ShAddEmployee shAddEmployee = addEmployeeService.getShAddEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfoShQgAdd(shAddEmployee.getIdentityNo(),
                                shAddEmployee.getIdentityNoType(), shAddEmployee.getFirstLevelClientName(),
                                shAddEmployee.getSecondLevelClientName(), shAddEmployee.getCityName(),
                                shAddEmployee.getWelfareHandler());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            shAddEmployee.setReturnReason(queryInfo.getReturnReason());
                            addEmployeeService.updateShAddEmployee(shAddEmployee);
                            continue;
                        } else {
                            shAddEmployee.setGender(queryInfo.getGender());
                            shAddEmployee.setBirthday(queryInfo.getBirthday());
                            shAddEmployee.setBirthday(queryInfo.getBirthday());
                            shAddEmployee.setOperator(queryInfo.getOperator());
                            shAddEmployee.setInquirer(queryInfo.getInquirer());
                            shAddEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            addEmployeeService.updateShAddEmployee(shAddEmployee);
                            shAddEmployeeSubmit(shAddEmployee);
                        }*/
                    } else if ("qgAdd".equals(type)) {
                        /*NationwideDispatch qgAddEmployee = addEmployeeService.getQgAddEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfoShQgAdd(qgAddEmployee.getIdentityNo(),
                                qgAddEmployee.getIdentityNoType(), qgAddEmployee.getFirstLevelClientName(),
                                qgAddEmployee.getSecondLevelClientName(), qgAddEmployee.getInvolved(),
                                qgAddEmployee.getWelfareHandler());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            qgAddEmployee.setReturnReason(queryInfo.getReturnReason());
                            addEmployeeService.updateQgAddEmployee(qgAddEmployee);
                            continue;
                        } else {
                            qgAddEmployee.setGender(queryInfo.getGender());
                            qgAddEmployee.setBirthday(queryInfo.getBirthday());
                            qgAddEmployee.setBirthday(queryInfo.getBirthday());
                            qgAddEmployee.setOperator(queryInfo.getOperator());
                            qgAddEmployee.setInquirer(queryInfo.getInquirer());
                            qgAddEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            addEmployeeService.updateQgAddEmployee(qgAddEmployee);
                            qgAddEmployeeSubmit(qgAddEmployee);
                        }*/
                    } else if ("del".equals(type)) {
                        /*DeleteEmployee deleteEmployee = deleteEmployeeService.getDeleteEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfoDel(deleteEmployee.getCreater(),
                                deleteEmployee.getIdentityNo(), deleteEmployee.getSocialSecurityEndTime() == null ?
                                        null : deleteEmployee.getSocialSecurityCity(),
                                deleteEmployee.getSWelfareHandler(),
                                deleteEmployee.getProvidentFundEndTime() == null ? null :
                                        deleteEmployee.getProvidentFundCity(), deleteEmployee.getGWelfareHandler());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            deleteEmployee.setReturnReason(queryInfo.getReturnReason());
                            deleteEmployeeService.updateDeleteEmployee(deleteEmployee);
                            continue;
                        } else {
                            deleteEmployee.setGender(queryInfo.getGender());
                            deleteEmployee.setBirthday(queryInfo.getBirthday());
                            deleteEmployee.setFirstLevelClientName(queryInfo.getFirstLevelClientName());
                            deleteEmployee.setSecondLevelClientName(queryInfo.getSecondLevelClientName());
                            deleteEmployee.setOperator(queryInfo.getOperator());
                            deleteEmployee.setInquirer(queryInfo.getInquirer());
                            deleteEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            deleteEmployeeService.updateDeleteEmployee(deleteEmployee);
                            deleteEmployeeSubmit(deleteEmployee);
                        }*/
                    } else if ("shDel".equals(type)) {
                        /*ShDeleteEmployee shDeleteEmployee = deleteEmployeeService.getShDeleteEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfoShQgDel(shDeleteEmployee.getFirstLevelClientName(),
                                shDeleteEmployee.getSecondLevelClientName(), shDeleteEmployee.getIdentityNo());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            shDeleteEmployee.setReturnReason(queryInfo.getReturnReason());
                            deleteEmployeeService.updateShDeleteEmployee(shDeleteEmployee);
                            continue;
                        } else {
                            shDeleteEmployee.setGender(queryInfo.getGender());
                            shDeleteEmployee.setBirthday(queryInfo.getBirthday());
                            shDeleteEmployee.setFirstLevelClientName(queryInfo.getFirstLevelClientName());
                            shDeleteEmployee.setSecondLevelClientName(queryInfo.getSecondLevelClientName());
                            shDeleteEmployee.setOperator(queryInfo.getOperator());
                            shDeleteEmployee.setInquirer(queryInfo.getInquirer());
                            shDeleteEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            deleteEmployeeService.updateShDeleteEmployee(shDeleteEmployee);
                            shDeleteEmployeeSubmit(shDeleteEmployee);
                        }*/
                    } else if ("qgDel".equals(type)) {
                        /*NationwideDispatch qgDeleteEmployee = deleteEmployeeService.getQgDeleteEmployeeById(id);
                        ResponseResult <QueryInfo> result = queryInfoShQgDel(qgDeleteEmployee.getFirstLevelClientName(),
                                qgDeleteEmployee.getSecondLevelClientName(), qgDeleteEmployee.getIdentityNo());
                        QueryInfo queryInfo = result.getData();
                        if ("error".equals(result.getErrmsg())) {
                            qgDeleteEmployee.setReturnReason(queryInfo.getReturnReason());
                            deleteEmployeeService.updateQgDeleteEmployee(qgDeleteEmployee);
                            continue;
                        } else {
                            qgDeleteEmployee.setGender(queryInfo.getGender());
                            qgDeleteEmployee.setBirthday(queryInfo.getBirthday());
                            qgDeleteEmployee.setFirstLevelClientName(queryInfo.getFirstLevelClientName());
                            qgDeleteEmployee.setSecondLevelClientName(queryInfo.getSecondLevelClientName());
                            qgDeleteEmployee.setOperator(queryInfo.getOperator());
                            qgDeleteEmployee.setInquirer(queryInfo.getInquirer());
                            qgDeleteEmployee.setSubordinateDepartment(queryInfo.getSubordinateDepartment());
                            deleteEmployeeService.updateQgDeleteEmployee(qgDeleteEmployee);
                            qgDeleteEmployeeSubmit(qgDeleteEmployee);
                        }*/
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
     * 方法说明：减员时员工档案赋值
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/4/20 9:21
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

    /**
     * 方法说明：增减员客户取派；申报/停缴流程节点变化，更新订单，增员对应状态
     * @param ids 表单id
     * @param schemaCode 表单编码（增员客户：add_employee；减员客户：delete_employee；社保申报：social_security_declare；
     *                             公积金申报：provident_fund_declare；社保停缴：social_security_close；
     *                            公积金停缴：provident_fund_close；）
     * @param status 状态 （增员客户、减员客户：取派；
     *                      社保申报、公积金申报：在办、预点、在缴、驳回；
     *                      社保停缴、公积金停缴：在办、停缴、驳回）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/5/22 9:49
     */
    @GetMapping("/updateStatus")
    @ResponseBody
    public ResponseResult<String> updateStatus(String ids, String schemaCode, String status) {
        try {
            employeeMaintainService.updateStatus(ids, schemaCode, status, this.getUserId(), this.getBizObjectFacade(),
                    this.getWorkflowInstanceFacade());
            return this.getOkResponseResult("success", "操作成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

}
