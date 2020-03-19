package com.authine.cloudpivot.web.api.controller;

import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.service.EmployeeFilesService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author:wangyong
 * @Date:2020/3/9 13:15
 * @Description: 数据修改controller
 */
@RestController
@RequestMapping("/controller/dataModify")
@Slf4j
public class DataModifyController extends BaseController {

    @Autowired
    EmployeeFilesService employeeFilesService;

    private static OrganizationFacade organizationFacade;

    @GetMapping("/getData")
    public ResponseResult<Object> getData(@RequestParam(required = true) String id, @RequestParam(required = true) String schemaCode) {
        log.info("获取修改数据");
        log.info("获取数据的id：" + id + "  获取数据的schemaCode" + schemaCode);
        Object data = data(id, schemaCode);
        return this.getErrResponseResult(data, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @PutMapping("/check")
    public ResponseResult<Object> checkChange(@RequestParam String id, @RequestParam String sourceId, @RequestParam String schemaCode) {
        log.info("检查修改模块中，数据是否存在被修改");
        log.info("修改数据id：" + id + "   修改数据schemaCode：" + schemaCode);
        log.info("源数据id：" + sourceId);

        check(id, schemaCode, sourceId);
        log.info("检查成功");
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * @param id:         修改数据id
     * @param schemaCode: 修改数据表单编码
     * @param sourceId:   源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:49
     * @return: void
     * @Description: 检查是否存在修改
     */

    private void check(String id, String schemaCode, String sourceId) {
        switch (schemaCode) {
            case "add_employee_update":
                // 增员_客户修改
                checkAddEmployeeUpdate(id, sourceId);
                break;
            case "sh_add_employee_update":
                // 增员_上海修改
                checkShAddEmployeeUpdate(id, sourceId);
                break;
            case "nationwide_dispatch_update":
                // 增员_全国修改
                checkNationwideDispatchUpdate(id, sourceId);
                break;
            case "delete_employee_update":
                // 减员_客户修改
                checkDeleteEmployeeUpdate(id, sourceId);
                break;
            case "sh_delete_employee_update":
                // 减员_上海修改
                checkShDeleteEmployeeUpdate(id, sourceId);
                break;
            case "nation_delete_update":
                // 减员_全国修改
                checkNationwideDeleteUpdate(id, sourceId);
                break;
            case "employee_files_update":
                // 客户档案修改
                checkEmployeeFiles(id, sourceId);
                break;
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:59
     * @return: void
     * @Description: 检查员工档案是否存在修改
     */
    private void checkEmployeeFiles(String id, String sourceId) {
        EmployeeFiles employeeFiles = employeeFilesService.getEmployeeFilesData(sourceId);
        EmployeeFiles employeeFilesUpdate = employeeFilesService.getEmployeeFilesUpdateData(id);
        List<ChangeValue> data = new ArrayList<>();
        checkString(employeeFiles.getEntrustedUnit(), employeeFilesUpdate.getEntrustedUnit(), "委托单位", id, data);
        checkString(employeeFiles.getClientName(), employeeFilesUpdate.getClientName(), "客户名称", id, data);
        checkUnit(employeeFiles.getSalesman(), employeeFilesUpdate.getSalesman(), "客户名称", id, 1, data);
        checkString(employeeFiles.getEmployeeName(), employeeFilesUpdate.getEmployeeName(), "员工姓名", id, data);
        checkString(employeeFiles.getIdType(), employeeFilesUpdate.getIdType(), "证件类型", id, data);
        checkString(employeeFiles.getIdNo(), employeeFilesUpdate.getIdNo(), "证件号码", id, data);
        checkString(employeeFiles.getGender(), employeeFilesUpdate.getGender(), "性别", id, data);
        checkDate(employeeFiles.getBirthDate(), employeeFilesUpdate.getBirthDate(), "出生年月", id, data);
        checkString(employeeFiles.getEmployeeNature(), employeeFilesUpdate.getEmployeeNature(), "员工性质", id, data);
        checkString(employeeFiles.getHouseholdRegisterNature(), employeeFilesUpdate.getHouseholdRegisterNature(), "户籍性质", id, data);
        checkString(employeeFiles.getMobile(), employeeFilesUpdate.getMobile(), "联系电话", id, data);
        checkString(employeeFiles.getPosition(), employeeFilesUpdate.getPosition(), "职位", id, data);
        checkString(employeeFiles.getEmployeeLabels(), employeeFilesUpdate.getEmployeeLabels(), "员工标签", id, data);
        checkString(employeeFiles.getEmail(), employeeFilesUpdate.getEmail(), "邮箱", id, data);
        checkDate(employeeFiles.getLabourContractStartTime(), employeeFilesUpdate.getLabourContractStartTime(), "合同开始日期", id, data);
        checkDate(employeeFiles.getLabourContractEndTime(), employeeFilesUpdate.getLabourContractEndTime(), "合同结束日期", id, data);
        checkDouble(employeeFiles.getSalary(), employeeFilesUpdate.getSalary(), "合同工资", id, data);
        checkDate(employeeFiles.getProbationStartTime(), employeeFilesUpdate.getProbationStartTime(), "试用期起始时间", id, data);
        checkDate(employeeFiles.getProbationEndTime(), employeeFilesUpdate.getProbationEndTime(), "试用期结束时间", id, data);
        checkDouble(employeeFiles.getProbationSalary(), employeeFilesUpdate.getProbationSalary(), "试用期工资", id, data);
        checkString(employeeFiles.getSocialSecurityCity(), employeeFilesUpdate.getSocialSecurityCity(), "社保福利地", id, data);
        checkString(employeeFiles.getProvidentFundCity(), employeeFilesUpdate.getProvidentFundCity(), "公积金福利地", id, data);
        checkDate(employeeFiles.getReportEntryTime(), employeeFilesUpdate.getReportEntryTime(), "报入职时间", id, data);
        checkUnit(employeeFiles.getReportRecruits(), employeeFilesUpdate.getReportRecruits(), "报入职时间", id, 1, data);
        checkDate(employeeFiles.getEntryTime(), employeeFilesUpdate.getEntryTime(), "入职日期", id, data);
        checkDate(employeeFiles.getSocialSecurityChargeStart(), employeeFilesUpdate.getSocialSecurityChargeStart(), "社保收费开始", id, data);
        checkDate(employeeFiles.getProvidentFundChargeStart(), employeeFilesUpdate.getProvidentFundChargeStart(), "公积金收费开始", id, data);
        checkString(employeeFiles.getSocialSecurityArea(), employeeFilesUpdate.getSocialSecurityArea(), "社保福利办理方", id, data);
        checkString(employeeFiles.getProvidentFundArea(), employeeFilesUpdate.getProvidentFundArea(), "公积金福利办理方", id, data);
        checkString(employeeFiles.getEntryDescription(), employeeFilesUpdate.getEntryDescription(), "入职备注", id, data);
        checkString(employeeFiles.getEntryNotice(), employeeFilesUpdate.getEntryNotice(), "是否入职通知", id, data);
        checkString(employeeFiles.getHealthCheck(), employeeFilesUpdate.getHealthCheck(), "是否体检", id, data);
        checkString(employeeFiles.getWhetherPay(), employeeFilesUpdate.getWhetherPay(), "是否发薪", id, data);
        checkDate(employeeFiles.getReportQuitDate(), employeeFilesUpdate.getReportQuitDate(), "报离职时间", id, data);
        checkUnit(employeeFiles.getReportSeveranceOfficer(), employeeFilesUpdate.getReportSeveranceOfficer(), "报离职人", id, 1, data);
        checkDate(employeeFiles.getQuitDate(), employeeFilesUpdate.getQuitDate(), "离职日期", id, data);
        checkDate(employeeFiles.getSocialSecurityChargeEnd(), employeeFilesUpdate.getSocialSecurityChargeEnd(), "社保收费截止", id, data);
        checkDate(employeeFiles.getProvidentFundChargeEnd(), employeeFilesUpdate.getProvidentFundChargeEnd(), "公积金收费截止", id, data);
        checkString(employeeFiles.getQuitReason(), employeeFilesUpdate.getQuitReason(), "离职原因", id, data);
        checkString(employeeFiles.getQuitRemark(), employeeFilesUpdate.getQuitRemark(), "离职备注", id, data);
        checkString(employeeFiles.getBankAccountNumber(), employeeFilesUpdate.getBankAccountNumber(), "银行卡账号", id, data);
        checkString(employeeFiles.getBankName(), employeeFilesUpdate.getBankName(), "开户行", id, data);
        checkString(employeeFiles.getBankArea(), employeeFilesUpdate.getBankArea(), "开户地", id, data);
        if (data.size() != 0) {
            employeeFilesService.employeeFilesUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:57
     * @return: void
     * @Description: 检查减员_全国是否存在修改
     */
    private void checkNationwideDeleteUpdate(String id, String sourceId) {
        NationwideDispatch nationwideDelete = employeeFilesService.getNationwideDeleteEmployeeData(sourceId);
        NationwideDispatch nationwideDeleteUpdate = employeeFilesService.getNationwideDeleteEmployeeUpdateData(id);
        List<ChangeValue> data = nationwideChange(nationwideDelete, nationwideDeleteUpdate, id);
        if (data.size() != 0) {
            employeeFilesService.nationDelUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:55
     * @return: void
     * @Description: 检查减员_上海是否存在修改
     */
    private void checkShDeleteEmployeeUpdate(String id, String sourceId) {
        ShDeleteEmployee shDeleteEmployee = employeeFilesService.getShDeleteEmployeeData(sourceId);
        ShDeleteEmployee shDeleteEmployeeUpdate = employeeFilesService.getShDeleteEmployeeUpdateData(id);
        List<ChangeValue> data = new ArrayList<>();
        checkString(shDeleteEmployee.getEmployeeName(), shDeleteEmployeeUpdate.getEmployeeName(), "姓名", id, data);
        checkString(shDeleteEmployee.getIdentityNoType(), shDeleteEmployeeUpdate.getIdentityNoType(), "证件类型", id, data);
        checkString(shDeleteEmployee.getIdentityNo(), shDeleteEmployeeUpdate.getIdentityNo(), "身份证号码", id, data);
        checkString(shDeleteEmployee.getClientNum(), shDeleteEmployeeUpdate.getClientNum(), "客户编号", id, data);
        checkString(shDeleteEmployee.getClientName(), shDeleteEmployeeUpdate.getClientName(), "客户名称", id, data);
        checkString(shDeleteEmployee.getClientShortName(), shDeleteEmployeeUpdate.getClientShortName(), "客户简称", id, data);
        checkDate(shDeleteEmployee.getOsInitiatedDepartureTime(), shDeleteEmployeeUpdate.getOsInitiatedDepartureTime(), "OS发起离职时间", id, data);
        checkDate(shDeleteEmployee.getDepartureTime(), shDeleteEmployeeUpdate.getDepartureTime(), "离职日期", id, data);
        checkDate(shDeleteEmployee.getChargeEndTime(), shDeleteEmployeeUpdate.getChargeEndTime(), "收费结束时间", id, data);
        checkString(shDeleteEmployee.getLeaveReason(), shDeleteEmployeeUpdate.getLeaveReason(), "离职原因", id, data);
        checkString(shDeleteEmployee.getLeaveRemark(), shDeleteEmployeeUpdate.getLeaveRemark(), "离职备注", id, data);
        checkString(shDeleteEmployee.getProvidentFundTransferMode(), shDeleteEmployeeUpdate.getProvidentFundTransferMode(), "公积金转移方式", id, data);
        checkString(shDeleteEmployee.getBacktrack(), shDeleteEmployeeUpdate.getBacktrack(), "退档地", id, data);
        checkString(shDeleteEmployee.getGeLeaveReason(), shDeleteEmployeeUpdate.getGeLeaveReason(), "GE离职原因", id, data);
        checkString(shDeleteEmployee.getCustomerNum(), shDeleteEmployeeUpdate.getCustomerNum(), "客户方编号", id, data);
        checkString(shDeleteEmployee.getWeatherLeaveE(), shDeleteEmployeeUpdate.getWeatherLeaveE(), "是否离职E化", id, data);
        if (data.size() != 0) {
            employeeFilesService.shDelEmployeeUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:54
     * @return: void
     * @Description: 检查减员_客户是否存在修改
     */
    private void checkDeleteEmployeeUpdate(String id, String sourceId) {
        DeleteEmployee deleteEmployee = employeeFilesService.getDeleteEmployeeData(sourceId);
        DeleteEmployee deleteEmployeeUpdate = employeeFilesService.getDeleteEmployeeUpdateData(id);
        List<ChangeValue> data = new ArrayList<>();
        checkString(deleteEmployee.getClientName(), deleteEmployeeUpdate.getClientName(), "客户名称", id, data);
        checkString(deleteEmployee.getEmployeeName(), deleteEmployeeUpdate.getEmployeeName(), "姓名", id, data);
        checkString(deleteEmployee.getIdentityNoType(), deleteEmployeeUpdate.getIdentityNoType(), "证件类型", id, data);
        checkString(deleteEmployee.getIdentityNo(), deleteEmployeeUpdate.getIdentityNo(), "证件号码", id, data);
        checkString(deleteEmployee.getCity(), deleteEmployeeUpdate.getCity(), "地区", id, data);
        checkString(deleteEmployee.getLeaveReason(), deleteEmployeeUpdate.getLeaveReason(), "离职原因", id, data);
        checkDate(deleteEmployee.getLeaveTime(), deleteEmployeeUpdate.getLeaveTime(), "离职日期", id, data);
        checkDate(deleteEmployee.getSocialSecurityEndTime(), deleteEmployeeUpdate.getSocialSecurityEndTime(), "社保终止时间", id, data);
        checkDate(deleteEmployee.getProvidentFundEndTime(), deleteEmployeeUpdate.getProvidentFundEndTime(), "公积金终止时间", id, data);
        if (data.size() != 0) {
            employeeFilesService.delEmployeeUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:53
     * @return: void
     * @Description: 检查增员_全国是否存在修改
     */
    private void checkNationwideDispatchUpdate(String id, String sourceId) {
        NationwideDispatch nationwideDispatch = employeeFilesService.getNationwideAddEmployeeData(sourceId);
        NationwideDispatch nationwideDispatchUpdate = employeeFilesService.getNationwideAddEmployeeUpdateData(id);
        List<ChangeValue> data = nationwideChange(nationwideDispatch, nationwideDispatchUpdate, id);
        if (data.size() != 0) {
            employeeFilesService.nationAddUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:52
     * @return: void
     * @Description: 检查增员_上海是否存在修改
     */
    private void checkShAddEmployeeUpdate(String id, String sourceId) {
        ShAddEmployee shAddEmployee = employeeFilesService.getShAddEmployeeData(sourceId);
        ShAddEmployee shAddEmployeeUpdate = employeeFilesService.getShAddEmployeeUpdateData(id);
        List<ChangeValue> data = new ArrayList<>();
        checkString(shAddEmployee.getEmployeeName(), shAddEmployeeUpdate.getEmployeeName(), "员工姓名", id, data);
        checkString(shAddEmployee.getUniqueNum(), shAddEmployeeUpdate.getUniqueNum(), "唯一号", id, data);
        checkString(shAddEmployee.getIdentityNo(), shAddEmployeeUpdate.getIdentityNo(), "证件号", id, data);
        checkString(shAddEmployee.getCommissionSend(), shAddEmployeeUpdate.getCommissionSend(), "委托/派遣", id, data);
        checkString(shAddEmployee.getIdentityNoType(), shAddEmployeeUpdate.getIdentityNoType(), "证件号类型", id, data);
        checkString(shAddEmployee.getOrderCode(), shAddEmployeeUpdate.getOrderCode(), "委派单代码", id, data);
        checkString(shAddEmployee.getQuotationCode(), shAddEmployeeUpdate.getQuotationCode(), "报价单代码", id, data);
        checkString(shAddEmployee.getSysClientNum(), shAddEmployeeUpdate.getSysClientNum(), "系统客户编号", id, data);
        checkString(shAddEmployee.getSysClientNum(), shAddEmployeeUpdate.getSysClientNum(), "系统客户编号", id, data);
        checkString(shAddEmployee.getClientName(), shAddEmployeeUpdate.getClientName(), "客户名称", id, data);
        checkString(shAddEmployee.getClientShortName(), shAddEmployeeUpdate.getClientShortName(), "客户简称", id, data);
        checkString(shAddEmployee.getProjectProposalsName(), shAddEmployeeUpdate.getProjectProposalsName(), "项目书名称", id, data);
        checkString(shAddEmployee.getSocialSecurityPayDirect(), shAddEmployeeUpdate.getSocialSecurityPayDirect(), "社保支付方向", id, data);
        checkString(shAddEmployee.getProvidentFundPayDirect(), shAddEmployeeUpdate.getProvidentFundPayDirect(), "公积金支付方向", id, data);
        checkDouble(shAddEmployee.getServiceFee(), shAddEmployeeUpdate.getServiceFee(), "服务费", id, data);
        checkString(shAddEmployee.getWithFile(), shAddEmployeeUpdate.getWithFile(), "含档", id, data);
        checkString(shAddEmployee.getPackageInvolvesProcedures(), shAddEmployeeUpdate.getPackageInvolvesProcedures(), "套餐涉及手续", id, data);
        checkDate(shAddEmployee.getEntryTime(), shAddEmployeeUpdate.getEntryTime(), "入职时间", id, data);
        checkDate(shAddEmployee.getBenefitStartTime(), shAddEmployeeUpdate.getBenefitStartTime(), "福利起始时间", id, data);
        checkString(shAddEmployee.getMobile(), shAddEmployeeUpdate.getMobile(), "手机", id, data);
        checkDate(shAddEmployee.getProvidentFundStartTime(), shAddEmployeeUpdate.getProvidentFundStartTime(), "公积金开始时间", id, data);
        checkString(shAddEmployee.getWhetherConsistent(), shAddEmployeeUpdate.getWhetherConsistent(), "是否一致", id, data);
        checkString(shAddEmployee.getSocialSecurityStandards(), shAddEmployeeUpdate.getSocialSecurityStandards(), "社保组", id, data);
        checkDouble(shAddEmployee.getSocialSecurityBase(), shAddEmployeeUpdate.getSocialSecurityBase(), "社会保险基数", id, data);
        checkDouble(shAddEmployee.getProvidentFundBase(), shAddEmployeeUpdate.getProvidentFundBase(), "住房公积金基数", id, data);
        checkDouble(shAddEmployee.getPSupplementProvidentFund(), shAddEmployeeUpdate.getPSupplementProvidentFund(), "补充个人住房公积金比例", id, data);
        checkDouble(shAddEmployee.getUSupplementProvidentFund(), shAddEmployeeUpdate.getUSupplementProvidentFund(), "补充单位住房公积金比例", id, data);
        checkDouble(shAddEmployee.getSupplementProvidentFundB(), shAddEmployeeUpdate.getSupplementProvidentFundB(), "补充住房公积金基数", id, data);
        checkDate(shAddEmployee.getChargeStartDate(), shAddEmployeeUpdate.getChargeStartDate(), "收费起始日", id, data);
        checkDate(shAddEmployee.getDispatchPeriodStartDate(), shAddEmployeeUpdate.getDispatchPeriodStartDate(), "派遣期限起始日期", id, data);
        checkDate(shAddEmployee.getDispatchDeadline(), shAddEmployeeUpdate.getDispatchDeadline(), "派遣期限截至日期", id, data);
        checkDate(shAddEmployee.getStartDateTrial(), shAddEmployeeUpdate.getStartDateTrial(), "开始日期(试)", id, data);
        checkDate(shAddEmployee.getEndTimeTrial(), shAddEmployeeUpdate.getEndTimeTrial(), "结束日期(试)", id, data);
        checkDate(shAddEmployee.getEndTimeTrial(), shAddEmployeeUpdate.getEndTimeTrial(), "结束日期(试)", id, data);
        checkDouble(shAddEmployee.getWageTrial(), shAddEmployeeUpdate.getWageTrial(), "工资(试)", id, data);
        checkDate(shAddEmployee.getStartDatePositive(), shAddEmployeeUpdate.getStartDatePositive(), "开始日期(正)", id, data);
        checkDate(shAddEmployee.getEndDatePositive(), shAddEmployeeUpdate.getEndDatePositive(), "结束日期(正)", id, data);
        checkDouble(shAddEmployee.getWageTrial(), shAddEmployeeUpdate.getWageTrial(), "工资(正)", id, data);
        checkString(shAddEmployee.getPhone(), shAddEmployeeUpdate.getPhone(), "电话", id, data);
        checkString(shAddEmployee.getEmployeeCustomerSideNum(), shAddEmployeeUpdate.getEmployeeCustomerSideNum(), "员工客户方编号", id, data);
        checkString(shAddEmployee.getContactAddress(), shAddEmployeeUpdate.getContactAddress(), "联系地址", id, data);
        checkString(shAddEmployee.getPostalCode(), shAddEmployeeUpdate.getPostalCode(), "邮政编码", id, data);
        checkString(shAddEmployee.getHukouLocation(), shAddEmployeeUpdate.getHukouLocation(), "户口所在地", id, data);
        checkString(shAddEmployee.getMail(), shAddEmployeeUpdate.getMail(), "电子邮件", id, data);
        checkString(shAddEmployee.getBankName(), shAddEmployeeUpdate.getBankName(), "开户银行名称", id, data);
        checkString(shAddEmployee.getBankAccount(), shAddEmployeeUpdate.getBankAccount(), "帐号", id, data);
        checkString(shAddEmployee.getAccountProvinceName(), shAddEmployeeUpdate.getAccountProvinceName(), "帐号省名", id, data);
        checkString(shAddEmployee.getAccountCityName(), shAddEmployeeUpdate.getAccountCityName(), "帐号市区名", id, data);
        checkString(shAddEmployee.getAccountName(), shAddEmployeeUpdate.getAccountName(), "帐户名", id, data);
        checkString(shAddEmployee.getBankCategory(), shAddEmployeeUpdate.getBankCategory(), "银行类别", id, data);
        checkString(shAddEmployee.getCityName(), shAddEmployeeUpdate.getCityName(), "城市名称", id, data);
        checkString(shAddEmployee.getTaxProperties(), shAddEmployeeUpdate.getTaxProperties(), "报税属性", id, data);
        checkString(shAddEmployee.getJobNum(), shAddEmployeeUpdate.getJobNum(), "工号", id, data);
        checkString(shAddEmployee.getHro(), shAddEmployeeUpdate.getHro(), "HRO", id, data);
        checkString(shAddEmployee.getBusinessUnit(), shAddEmployeeUpdate.getBusinessUnit(), "业务部门", id, data);
        checkString(shAddEmployee.getEmployeeStatus(), shAddEmployeeUpdate.getEmployeeStatus(), "员工状态", id, data);
        checkString(shAddEmployee.getSynchronousCss(), shAddEmployeeUpdate.getSynchronousCss(), "同步CSS", id, data);
        checkString(shAddEmployee.getTaxBureau(), shAddEmployeeUpdate.getTaxBureau(), "报税税局", id, data);
        checkString(shAddEmployee.getInductionRemark(), shAddEmployeeUpdate.getInductionRemark(), "入职备注", id, data);
        checkString(shAddEmployee.getEmployeeAttributes(), shAddEmployeeUpdate.getEmployeeAttributes(), "员工属性", id, data);
        checkString(shAddEmployee.getSocialSecurityStandards(), shAddEmployeeUpdate.getSocialSecurityStandards(), "社保标准", id, data);
        checkString(shAddEmployee.getWeatherOnline(), shAddEmployeeUpdate.getWeatherOnline(), "是否线上", id, data);
        checkString(shAddEmployee.getWorkSystem(), shAddEmployeeUpdate.getWorkSystem(), "工作制", id, data);
        checkString(shAddEmployee.getWhetherConsistent(), shAddEmployeeUpdate.getWhetherConsistent(), "是否入职E化", id, data);
        if (data.size() != 0) {
            employeeFilesService.shAddEmployeeUpdateDetail(data);
        }
    }

    /**
     * @param id:       修改数据id
     * @param sourceId: 源数据id
     * @Author: wangyong
     * @Date: 2020/3/13 22:51
     * @return: void
     * @Description: 检查增员_客户是否存在修改
     */
    private void checkAddEmployeeUpdate(String id, String sourceId) {
        AddEmployee addEmployee = employeeFilesService.getAddEmployeeData(sourceId);
        AddEmployee addEmployeeUpdate = employeeFilesService.getAddEmployeeUpdateData(id);
        List<ChangeValue> data = new ArrayList<>();
        checkString(addEmployee.getClientName(), addEmployeeUpdate.getClientName(), "客户名称", id, data);
        checkString(addEmployee.getErp(), addEmployeeUpdate.getErp(), "ERP", id, data);
        checkString(addEmployee.getEmployeeName(), addEmployeeUpdate.getEmployeeName(), "姓名", id, data);
        checkString(addEmployee.getIdentityNo(), addEmployeeUpdate.getIdentityNo(), "证件号码", id, data);
        checkString(addEmployee.getIdentityNoType(), addEmployeeUpdate.getIdentityNoType(), "证件类型", id, data);
        checkString(addEmployee.getEmail(), addEmployeeUpdate.getEmail(), "邮箱", id, data);
        checkString(addEmployee.getMobile(), addEmployeeUpdate.getMobile(), "联系电话", id, data);
        checkString(addEmployee.getFamilyRegisterNature(), addEmployeeUpdate.getFamilyRegisterNature(), "户籍性质", id, data);
        checkString(addEmployee.getEmployeeNature(), addEmployeeUpdate.getEmployeeNature(), "员工性质", id, data);
        checkDate(addEmployee.getEntryTime(), addEmployeeUpdate.getEntryTime(), "入职日期", id, data);
        checkDate(addEmployee.getContractStartTime(), addEmployeeUpdate.getContractStartTime(), "合同开始日期", id, data);
        checkDate(addEmployee.getContractEndTime(), addEmployeeUpdate.getContractEndTime(), "合同结束日期", id, data);
        checkDouble(addEmployee.getContractSalary(), addEmployeeUpdate.getContractSalary(), "合同工资", id, data);
        checkString(addEmployee.getSocialSecurityCity(), addEmployeeUpdate.getSocialSecurityCity(), "社保福利地", id, data);
        checkDate(addEmployee.getSocialSecurityStartTime(), addEmployeeUpdate.getSocialSecurityStartTime(), "社保起做时间", id, data);
        checkDouble(addEmployee.getSocialSecurityBase(), addEmployeeUpdate.getSocialSecurityBase(), "社保基数", id, data);
        checkString(addEmployee.getProvidentFundCity(), addEmployeeUpdate.getProvidentFundCity(), "公积金福利地", id, data);
        checkDate(addEmployee.getProvidentFundStartTime(), addEmployeeUpdate.getProvidentFundStartTime(), "公积金起做时间", id, data);
        checkDouble(addEmployee.getProvidentFundBase(), addEmployeeUpdate.getProvidentFundBase(), "公积金基数", id, data);
        checkDouble(addEmployee.getCompanyProvidentFundBl(), addEmployeeUpdate.getCompanyProvidentFundBl(), "单位公积金比例", id, data);
        checkDouble(addEmployee.getEmployeeProvidentFundBl(), addEmployeeUpdate.getEmployeeProvidentFundBl(), "个人公积金比例s", id, data);
        if (data.size() != 0) {
            employeeFilesService.addEmployeeUpdateDetail(data);
        }

    }

    private List<ChangeValue> nationwideChange(NationwideDispatch nationwideDispatch, NationwideDispatch nationwideDispatchUpdate, String id) {
        List<ChangeValue> data = new ArrayList<>();
        checkString(nationwideDispatch.getUniqueNum(), nationwideDispatchUpdate.getUniqueNum(), "唯一号", id, data);
        checkString(nationwideDispatch.getEmployeeName(), nationwideDispatchUpdate.getEmployeeName(), "姓名", id, data);
        checkString(nationwideDispatch.getContactNumber(), nationwideDispatchUpdate.getContactNumber(), "联系电话", id, data);
        checkString(nationwideDispatch.getIdentityNoType(), nationwideDispatchUpdate.getIdentityNoType(), "证件类型", id, data);
        checkString(nationwideDispatch.getIdentityNo(), nationwideDispatchUpdate.getIdentityNo(), "证件号码", id, data);
        checkString(nationwideDispatch.getNationalBusinessWfFlag(), nationwideDispatchUpdate.getNationalBusinessWfFlag(), "全国业务流程标识", id, data);
        checkString(nationwideDispatch.getInvolved(), nationwideDispatchUpdate.getInvolved(), "涉及执行地", id, data);
        checkString(nationwideDispatch.getBusinessCustomerNum(), nationwideDispatchUpdate.getBusinessCustomerNum(), "业务客户编号", id, data);
        checkString(nationwideDispatch.getBusinessCustomerName(), nationwideDispatchUpdate.getBusinessCustomerName(), "业务客户名称", id, data);
        checkString(nationwideDispatch.getBusinessWfStatus(), nationwideDispatchUpdate.getBusinessWfStatus(), "业务流转状态", id, data);
        checkString(nationwideDispatch.getOrderType(), nationwideDispatchUpdate.getOrderType(), "订单类型", id, data);
        checkString(nationwideDispatch.getProcessingStatus(), nationwideDispatchUpdate.getProcessingStatus(), "处理状态", id, data);
        checkString(nationwideDispatch.getContractingSupplier(), nationwideDispatchUpdate.getContractingSupplier(), "签约方供应商", id, data);
        checkString(nationwideDispatch.getContractingPartyDepartment(), nationwideDispatchUpdate.getContractingPartyDepartment(), "签约方业务部", id, data);
        checkString(nationwideDispatch.getContractingRepresentative(), nationwideDispatchUpdate.getContractingRepresentative(), "签约方业务代表", id, data);
        checkDate(nationwideDispatch.getIntoPendingDate(), nationwideDispatchUpdate.getIntoPendingDate(), "进入待处理时间", id, data);
        checkDate(nationwideDispatch.getTaskListUpdateDate(), nationwideDispatchUpdate.getTaskListUpdateDate(), "任务单更新时间", id, data);
        checkDate(nationwideDispatch.getEntryDate(), nationwideDispatchUpdate.getEntryDate(), "入职日期", id, data);
        checkString(nationwideDispatch.getEmployeeInternalNum(), nationwideDispatchUpdate.getEmployeeInternalNum(), "员工内部编号", id, data);
        checkString(nationwideDispatch.getEmployeeEmail(), nationwideDispatchUpdate.getEmployeeEmail(), "雇员邮箱", id, data);
        checkString(nationwideDispatch.getOrderId(), nationwideDispatchUpdate.getOrderId(), "订单id", id, data);
        checkDate(nationwideDispatch.getOrderStartDate(), nationwideDispatchUpdate.getOrderStartDate(), "订单开始日期", id, data);
        checkDate(nationwideDispatch.getAssignmentDate(), nationwideDispatchUpdate.getAssignmentDate(), "办派日期", id, data);
        checkDouble(nationwideDispatch.getEntry_procedures_num(), nationwideDispatchUpdate.getEntry_procedures_num(), "入职手续数量", id, data);
        checkString(nationwideDispatch.getBusinessTips(), nationwideDispatchUpdate.getBusinessTips(), "业务提示", id, data);
        checkDate(nationwideDispatch.getOrderEndDate(), nationwideDispatchUpdate.getOrderEndDate(), "订单结束日期", id, data);
        checkDate(nationwideDispatch.getWithdrawalDate(), nationwideDispatchUpdate.getWithdrawalDate(), "办撤日期", id, data);
        checkDouble(nationwideDispatch.getSocialInsuranceAmount(), nationwideDispatchUpdate.getSocialInsuranceAmount(), "社保申报工资", id, data);
        checkDouble(nationwideDispatch.getProvidentFundAmount(), nationwideDispatchUpdate.getProvidentFundAmount(), "公积金申报工资", id, data);
        checkDouble(nationwideDispatch.getSuppleProvidentFundAmount(), nationwideDispatchUpdate.getSuppleProvidentFundAmount(), "补充公积金申报工资", id, data);
        checkString(nationwideDispatch.getRevocationReason(), nationwideDispatchUpdate.getRevocationReason(), "委派撤销原因", id, data);
        checkString(nationwideDispatch.getSocialSecurityStopReason(), nationwideDispatchUpdate.getSocialSecurityStopReason(), "社保停做原因", id, data);
        checkDate(nationwideDispatch.getDepartureDate(), nationwideDispatchUpdate.getDepartureDate(), "离职日期", id, data);
        checkString(nationwideDispatch.getDepartureRemark(), nationwideDispatchUpdate.getDepartureRemark(), "离职备注", id, data);
        checkString(nationwideDispatch.getChangeDetails(), nationwideDispatchUpdate.getChangeDetails(), "要求变化明细内容", id, data);
        checkDate(nationwideDispatch.getChangeTakeEffectDate(), nationwideDispatchUpdate.getChangeTakeEffectDate(), "要求变化生效时间", id, data);
        checkString(nationwideDispatch.getBusinessUnit(), nationwideDispatchUpdate.getBusinessUnit(), "业务部", id, data);
        checkString(nationwideDispatch.getSalesman(), nationwideDispatchUpdate.getSalesman(), "业务员", id, data);
        checkString(nationwideDispatch.getSocialSecurityPayMethod(), nationwideDispatchUpdate.getSocialSecurityPayMethod(), "社保缴费方式", id, data);
        checkString(nationwideDispatch.getProvidentFundPayMethod(), nationwideDispatchUpdate.getProvidentFundPayMethod(), "公积金缴费方式", id, data);
        checkString(nationwideDispatch.getSupplierAgreementName(), nationwideDispatchUpdate.getSupplierAgreementName(), "供应商协议名称", id, data);
        checkString(nationwideDispatch.getProvidentFundRatio(), nationwideDispatchUpdate.getProvidentFundRatio(), "公积金比例（企业+个人）", id, data);
        checkString(nationwideDispatch.getSuppleProvidentFundRatio(), nationwideDispatchUpdate.getSuppleProvidentFundRatio(), "补充公积金比例（企业+个人）", id, data);
        checkString(nationwideDispatch.getSocialSecurityStandard(), nationwideDispatchUpdate.getSocialSecurityStandard(), "社保地方标准", id, data);
        checkDate(nationwideDispatch.getSServiceFeeStartDate(), nationwideDispatchUpdate.getSServiceFeeStartDate(), "社保服务费起做日期", id, data);
        checkDate(nationwideDispatch.getSServiceFeeEndDate(), nationwideDispatchUpdate.getSServiceFeeEndDate(), "社保服务费止做日期", id, data);
        checkDate(nationwideDispatch.getGServiceFeeStartDate(), nationwideDispatchUpdate.getGServiceFeeStartDate(), "公积金服务费起做日期", id, data);
        checkDate(nationwideDispatch.getGServiceFeeEndDate(), nationwideDispatchUpdate.getGServiceFeeEndDate(), "公积金服务费止做日期", id, data);
        return data;
    }

    /**
     * 检查用户，部门是否修改
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param type         部门，用户类型
     * @param data         数据
     */
    private void checkUnit(String beforeModify, String nextModify, String field, String parentId, Integer type, List<ChangeValue> data) {
        Unit before = null;
        Unit next = null;

        if (beforeModify == nextModify) {
            // 无变化
            return;
        }

        if ((!StringUtil.isEmpty(beforeModify) && StringUtil.isEmpty(nextModify)) || (StringUtil.isEmpty(beforeModify) && !StringUtil.isEmpty(nextModify))) {
            // 存在不同
            if (!StringUtil.isEmpty(beforeModify)) {
                before = ((List<Unit>) JSON.parse(beforeModify)).get(0);
            }
            if (!StringUtil.isEmpty(nextModify)) {
                next = ((List<Unit>) JSON.parse(nextModify)).get(0);
            }
            if (null == before) {
                // 旧值为空
                if (type == 1) {
                    // 部门
                    DepartmentModel departmentModel = organizationFacade.getDepartment(next.getId());
                    data.add(getChangeData(field, null, departmentModel.getName(), parentId));
                } else {
                    // 人员
                    UserModel userModel = organizationFacade.getUser(next.getId());
                    data.add(getChangeData(field, null, userModel.getName(), parentId));
                }
            } else {
                // 旧值为空
                if (type == 1) {
                    // 部门
                    DepartmentModel departmentModel = organizationFacade.getDepartment(before.getId());
                    data.add(getChangeData(field, null, departmentModel.getName(), parentId));
                } else {
                    // 人员
                    UserModel userModel = organizationFacade.getUser(before.getId());
                    data.add(getChangeData(field, null, userModel.getName(), parentId));
                }
            }
        } else {
            // 旧值为空
            if (type == 1) {
                // 部门
                DepartmentModel oldDepartment = organizationFacade.getDepartment(before.getId());
                DepartmentModel newDepartment = organizationFacade.getDepartment(next.getId());
                data.add(getChangeData(field, oldDepartment.getName(), newDepartment.getName(), parentId));
            } else {
                // 人员
                UserModel oldUser = organizationFacade.getUser(before.getId());
                UserModel newUser = organizationFacade.getUser(next.getId());
                data.add(getChangeData(field, oldUser.getName(), newUser.getName(), parentId));
            }
        }
    }

    /**
     * 检查字符串是否修改
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkString(String beforeModify, String nextModify, String field, String parentId, List<ChangeValue> data) {
        if(null == beforeModify && null == nextModify) {
            return;
        }
        if (null == beforeModify || null == nextModify) {
            data.add(getChangeData(field, beforeModify, nextModify, parentId));
        } else if (!beforeModify.equals(nextModify)) {
            data.add(getChangeData(field, beforeModify, nextModify, parentId));
        }
    }

    /**
     * 检查数字是否改变
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkDouble(Double beforeModify, Double nextModify, String field, String parentId, List<ChangeValue> data) {

        if (beforeModify == null && nextModify == null) {
            return;
        }

        if (beforeModify == null || nextModify == null) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        } else if (beforeModify.doubleValue() != nextModify.doubleValue()) {
            data.add(getChangeData(field, beforeModify + "", nextModify + "", parentId));
        }
    }

    /**
     * 检查日期是否改变
     *
     * @param beforeModify 修改前值
     * @param nextModify   修改后值
     * @param field        修改列名
     * @param parentId     父id
     * @param data         数据
     */
    private void checkDate(Date beforeModify, Date nextModify, String field, String parentId, List<ChangeValue> data) {
        if (beforeModify == null && nextModify == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        // 存在修改
        if (beforeModify == null || nextModify == null) {
            if (beforeModify == null) {
                calendar.setTime(nextModify);
                String time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, null, time, parentId));
            } else {
                calendar.setTime(beforeModify);
                String time = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, time, null, parentId));
            }
        } else {
            if (beforeModify.getYear() != nextModify.getYear() || beforeModify.getMonth() != nextModify.getMonth() || beforeModify.getDay() != nextModify.getDay()) {
                calendar.setTime(beforeModify);
                String time1 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                calendar.setTime(nextModify);
                String time2 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                data.add(getChangeData(field, time1, time2, parentId));
            }
        }
    }

    /**
     * @param id:     数据id
     * @param schema: 表单编码
     * @Author: wangyong
     * @Date: 2020/3/13 22:47
     * @return: java.lang.Object
     * @Description: 获取数据
     */
    private Object data(String id, String schema) {
        switch (schema) {
            case Constants.ADD_EMPLOYEE_SCHEMA:
                return employeeFilesService.getAddEmployeeData(id);
            case Constants.DELETE_EMPLOYEE_SCHEMA:
                return employeeFilesService.getDeleteEmployeeData(id);
            case Constants.SH_ADD_EMPLOYEE_SCHEMA:
                return employeeFilesService.getShAddEmployeeData(id);
            case Constants.SH_DELETE_EMPLOYEE_SCHEMA:
                return employeeFilesService.getShDeleteEmployeeData(id);
            case Constants.NATIONWIDE_DISPATCH:
                return employeeFilesService.getNationwideAddEmployeeData(id);
            case Constants.NATIONWIDE_DISPATCH_DELETE_SCHEMA:
                return employeeFilesService.getNationwideDeleteEmployeeData(id);
            case Constants.EMPLOYEE_FILES_SCHEMA:
                return employeeFilesService.getEmployeeFilesData(id);
            default:
                return null;
        }
    }

    private ChangeValue getChangeData(String field, String oldValue, String newValue, String parentId) {
        ChangeValue data = new ChangeValue();
        data.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        data.setParentId(parentId);
        data.setUpdateField(field);
        data.setUpdateBeforeValue(oldValue);
        data.setUpdateAfterValue(newValue);
        return data;
    }


}
