package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.EntryNotice;
import com.authine.cloudpivot.web.api.entity.NationwideDispatch;
import com.authine.cloudpivot.web.api.entity.ShAddEmployee;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn;
import com.authine.cloudpivot.web.api.params.OtherAddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.service.AddEmployeeService;
import com.authine.cloudpivot.web.api.utils.SubmitCheckUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liulei
 * @Description 增员控制层
 * @ClassName com.authine.cloudpivot.web.api.controller.AddEmployeeController
 * @Date 2020/6/12 9:52
 **/
@RestController
@RequestMapping("/controller/addEmployee")
@Slf4j
public class AddEmployeeController extends BaseController {

    @Resource
    private AddEmployeeService addEmployeeService;

    /**
     * 方法说明：增员表单提交
     * @param ids 表单id，多个id使用“,”隔开
     * @param schemaCode 表单编码（增员_客户：add_employee；增员_上海：sh_add_employee；增员_全国：nationwide_dispatch）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @author liulei
     * @Date 2020/6/12 10:06
     */
    @GetMapping("/addSubmit")
    @ResponseBody
    public ResponseResult <Void> addSubmit(String ids, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        String idArr[] = ids.split(",");
        try {
            if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                List <AddEmployee> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    AddEmployee addEmployee = addEmployeeService.getAddEmployeeById(idArr[i]);
                    if (addEmployee == null) {
                        log.error("根据id[" + idArr[i] + "]没有查询到增员数据");
                        return this.getErrResponseResult(ErrCode.BIZ_OBJECT_NOT_EXIST.getErrCode(),
                                "根据id[" + idArr[i] + "]没有查询到增员数据");
                    }
                    // 判断是否可以提交
                    AddEmployeeCheckReturn checkReturn =
                            SubmitCheckUtils.addEmployeeCheck(new AddEmployeeCheckParams(addEmployee));
                    if (!checkReturn.getIsCanSubmit()) {
                        log.error(addEmployee.getEmployeeName() + "不可提交，操作失败");
                        addEmployeeService.updateAddEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.ADD_EMPLOYEE_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                addEmployee.getEmployeeName() + "不可提交，操作失败");
                    } else {
                        addEmployee.setFirstLevelClientName(checkReturn.getFirstLevelClientName());
                        addEmployee.setSecondLevelClientName(checkReturn.getSecondLevelClientName());
                        addEmployee.setOperator(checkReturn.getEditStr());
                        addEmployee.setInquirer(checkReturn.getLookStr());
                        addEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(addEmployee.getIdentityNoType())) {
                            addEmployee.setGender(checkReturn.getGender());
                            addEmployee.setBirthday(checkReturn.getBirthday());
                        }

                        if (addEmployee.getSocialSecurityBase() - 0d > 0d) {
                            addEmployee.setSbStatus("待办");
                        }
                        if (addEmployee.getProvidentFundBase() - 0d > 0d) {
                            addEmployee.setGjjStatus("待办");
                        }
                        list.add(addEmployee);
                    }
                }
                List <EntryNotice> entryNotices = addEmployeeService.addSubmit(list, this.getOrganizationFacade(),
                        getUserId());
                if (entryNotices != null && entryNotices.size() > 0) {
                    for (int i = 0; i < entryNotices.size(); i++) {
                        EntryNotice entryNotice = entryNotices.get(i);
                        String wfId = this.getWorkflowInstanceFacade().startWorkflowInstance(entryNotice.getCreatedDeptId(), entryNotice.getCreater(),
                                Constants.ENTRY_NOTICE_SCHEMA_WF, entryNotice.getId(), true);
                        log.info("启动入职通知流程id:[" + wfId + "] " +entryNotice.toString());
                    }
                }
            } else if (Constants.SH_ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                List <ShAddEmployee> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    ShAddEmployee shAddEmployee = addEmployeeService.getShAddEmployeeById(idArr[i]);
                    if (shAddEmployee == null) {
                        log.error("根据id[" + idArr[i] + "]没有查询到增员数据");
                        return this.getErrResponseResult(ErrCode.BIZ_OBJECT_NOT_EXIST.getErrCode(),
                                "根据id[" + idArr[i] + "]没有查询到增员数据");
                    }
                    if ("一致".equals(shAddEmployee.getWhetherConsistent())) {
                        shAddEmployee.setProvidentFundStartTime(shAddEmployee.getBenefitStartTime());
                    }
                    AddEmployeeCheckReturn checkReturn = SubmitCheckUtils.employeeCheck(new OtherAddEmployeeCheckParams(shAddEmployee));
                    if (checkReturn.getIsCanSubmit()) {
                        shAddEmployee.setOperator(checkReturn.getEditStr());
                        shAddEmployee.setInquirer(checkReturn.getLookStr());
                        shAddEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(shAddEmployee.getIdentityNoType())) {
                            shAddEmployee.setGender(checkReturn.getGender());
                            shAddEmployee.setBirthday(checkReturn.getBirthday());
                        }
                        list.add(shAddEmployee);
                    } else {
                        log.error(shAddEmployee.getEmployeeName() + "不可提交，操作失败");
                        addEmployeeService.updateAddEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.SH_ADD_EMPLOYEE_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                shAddEmployee.getEmployeeName() + "不可提交，操作失败");
                    }
                }
                addEmployeeService.shAddSubmit(list);
            } else if (Constants.NATIONWIDE_DISPATCH_SCHEMA.equals(schemaCode)) {
                List <NationwideDispatch> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    NationwideDispatch qgAddEmployee = addEmployeeService.getQgAddEmployeeById(idArr[i]);
                    if (qgAddEmployee == null) {
                        log.error("根据id[" + idArr[i] + "]没有查询到增员数据");
                        return this.getErrResponseResult(ErrCode.BIZ_OBJECT_NOT_EXIST.getErrCode(),
                                "根据id[" + idArr[i] + "]没有查询到增员数据");
                    }
                    AddEmployeeCheckReturn checkReturn = SubmitCheckUtils.employeeCheck(new OtherAddEmployeeCheckParams(qgAddEmployee));
                    if (checkReturn.getIsCanSubmit()) {
                        qgAddEmployee.setOperator(checkReturn.getEditStr());
                        qgAddEmployee.setInquirer(checkReturn.getLookStr());
                        qgAddEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(qgAddEmployee.getIdentityNoType())) {
                            qgAddEmployee.setGender(checkReturn.getGender());
                            qgAddEmployee.setBirthday(checkReturn.getBirthday());
                        }
                        list.add(qgAddEmployee);
                    } else {
                        log.error(qgAddEmployee.getEmployeeName() + "不可提交，操作失败");
                        addEmployeeService.updateAddEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.NATIONWIDE_DISPATCH_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                qgAddEmployee.getEmployeeName() + "不可提交，操作失败");
                    }
                }
                addEmployeeService.qgAddSubmit(list);
            } else {
                return this.getErrResponseResult(ErrCode.SYS_PARAMETER_ERROR.getErrCode(),
                        ErrCode.SYS_PARAMETER_ERROR.getErrMsg());
            }
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：增员变更提交
     * @param id 表单id
     * @param schemaCode 表单编码（增员_客户：add_employee；增员_上海：sh_add_employee；增员_全国：nationwide_dispatch）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/15 11:37
     */
    @GetMapping("/addUpdateSubmit")
    @ResponseBody
    public ResponseResult <Void> addUpdateSubmit(String id, String schemaCode) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        try {
            if (Constants.ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                addEmployeeService.addUpdateSubmit(id, this.getUserId(), this.getBizObjectFacade(), this.getOrganizationFacade());
            } else if (Constants.SH_ADD_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                addEmployeeService.addShUpdateSubmit(id);
            } else if (Constants.NATIONWIDE_DISPATCH_SCHEMA.equals(schemaCode)) {
                addEmployeeService.addQgUpdateSubmit(id);
            }
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
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
    public ResponseResult <Void> employeeFilesUpdateSubmit(String id) {
        try {
            addEmployeeService.employeeFilesUpdateSubmit(id);
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：增员取派
     * @param id 增员_客户表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/15 16:24
     */
    @GetMapping("/cancelDispatch")
    @ResponseBody
    public ResponseResult <Void> cancelDispatch(String id) {
        try {
            addEmployeeService.cancelDispatch(id, this.getUserId(), this.getBizObjectFacade());
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：申报提交
     * @param ids 表单id，多个id使用“,”隔开
     * @param billYear 账单年月
     * @param schemaCode 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/16 9:17
     */
    @GetMapping("/declareSubmit")
    @ResponseBody
    public ResponseResult <Void> declareSubmit(String ids, String billYear, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(billYear) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        billYear = billYear.trim();
        try {
            List <String> idList = Arrays.asList(ids.split(","));
            addEmployeeService.declareSubmit(idList, billYear, schemaCode);
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：申报驳回
     * @param ids 表单id，多个id使用“,”隔开
     * @param returnReason 退回原因
     * @param schemaCode 表单编码（社保申报：social_security_declare；公积金申报：provident_fund_declare；）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/16 10:21
     */
    @GetMapping("/declareReject")
    @ResponseBody
    public ResponseResult <Void> declareReject(String ids, String returnReason, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(returnReason) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        returnReason = returnReason.trim();
        try {
            List <String> idList = Arrays.asList(ids.split(","));
            addEmployeeService.declareReject(idList, returnReason, schemaCode, this.getUserId(),
                    this.getBizObjectFacade());
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }
}
