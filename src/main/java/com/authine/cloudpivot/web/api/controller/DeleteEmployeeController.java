package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.DeleteEmployee;
import com.authine.cloudpivot.web.api.entity.NationwideDispatch;
import com.authine.cloudpivot.web.api.entity.ShDeleteEmployee;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn;
import com.authine.cloudpivot.web.api.params.DelEmployeeCheckParams;
import com.authine.cloudpivot.web.api.params.OtherDelEmployeeCheckParams;
import com.authine.cloudpivot.web.api.service.DeleteEmployeeService;
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
 * @Description 减员控制层
 * @ClassName com.authine.cloudpivot.web.api.controller.DelEmployeeController
 * @Date 2020/6/15 9:21
 **/
@RestController
@RequestMapping("/controller/deleteEmployee")
@Slf4j
public class DeleteEmployeeController extends BaseController {

    @Resource
    private DeleteEmployeeService delEmployeeService;

    /**
     * 方法说明：减员表单提交
     * @param ids 表单id，多个id使用“,”隔开
     * @param schemaCode 表单编码（减员_客户：delete_employee；减员_上海：sh_delete_employee；减员_全国：nationwide_dispatch_delete）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/15 9:33
     */
    @GetMapping("/delSubmit")
    @ResponseBody
    public ResponseResult <Void> delSubmit(String ids, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        try {
            String idArr[] = ids.split(",");
            if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                List <DeleteEmployee> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    DeleteEmployee delEmployee = delEmployeeService.getDeleteEmployeeById(idArr[i]);
                    if (delEmployee == null) {
                        log.error("根据id[" + idArr[i] + "]没有查询到减员数据");
                        return this.getErrResponseResult(ErrCode.BIZ_OBJECT_NOT_EXIST.getErrCode(),
                                "根据id[" + idArr[i] + "]没有查询到减员数据");
                    }
                    AddEmployeeCheckReturn checkReturn = SubmitCheckUtils.delEmployeeCheck(new DelEmployeeCheckParams(delEmployee));
                    if (!checkReturn.getIsCanSubmit()) {
                        delEmployeeService.updateDelEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.DELETE_EMPLOYEE_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                delEmployee.getEmployeeName() + "不可提交，操作失败");
                    } else {
                        delEmployee.setFirstLevelClientName(checkReturn.getFirstLevelClientName());
                        delEmployee.setSecondLevelClientName(checkReturn.getSecondLevelClientName());
                        delEmployee.setOperator(checkReturn.getEditStr());
                        delEmployee.setInquirer(checkReturn.getLookStr());
                        delEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(delEmployee.getIdentityNoType())) {
                            delEmployee.setGender(checkReturn.getGender());
                            delEmployee.setBirthday(checkReturn.getBirthday());
                        }

                        if (delEmployee.getSocialSecurityEndTime() != null) {
                            delEmployee.setSbStatus("待办");
                        }
                        if (delEmployee.getProvidentFundEndTime() != null) {
                            delEmployee.setGjjStatus("待办");
                        }

                        list.add(delEmployee);
                    }
                }
                delEmployeeService.delSubmit(list, this.getUserId(), this.getOrganizationFacade());
            } else if (Constants.SH_DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                List <ShDeleteEmployee> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    ShDeleteEmployee delEmployee = delEmployeeService.getShDeleteEmployeeById(idArr[i]);
                    if (delEmployee == null) {
                        log.error("根据id[" + idArr[i] + "]没有查询到减员数据");
                        return this.getErrResponseResult(ErrCode.BIZ_OBJECT_NOT_EXIST.getErrCode(),
                                "根据id[" + idArr[i] + "]没有查询到减员数据");
                    }
                    AddEmployeeCheckReturn checkReturn =
                            SubmitCheckUtils.otherDelEmployeeCheck(new OtherDelEmployeeCheckParams(delEmployee));
                    if (!checkReturn.getIsCanSubmit()) {
                        delEmployeeService.updateDelEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.SH_DELETE_EMPLOYEE_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                delEmployee.getEmployeeName() + "不可提交，操作失败");
                    } else {
                        delEmployee.setOperator(checkReturn.getEditStr());
                        delEmployee.setInquirer(checkReturn.getLookStr());
                        delEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(delEmployee.getIdentityNoType())) {
                            delEmployee.setGender(delEmployee.getGender());
                            delEmployee.setBirthday(delEmployee.getBirthday());
                        }
                        list.add(delEmployee);
                    }
                }
                delEmployeeService.shDelSubmit(list);
            } else if (Constants.NATIONWIDE_DISPATCH_DELETE_SCHEMA.equals(schemaCode)) {
                List <NationwideDispatch> list = new ArrayList <>();
                for (int i = 0; i < idArr.length; i++) {
                    NationwideDispatch delEmployee = delEmployeeService.getQgDeleteEmployeeById(idArr[i]);
                    AddEmployeeCheckReturn checkReturn =
                            SubmitCheckUtils.otherDelEmployeeCheck(new OtherDelEmployeeCheckParams(delEmployee));
                    if (!checkReturn.getIsCanSubmit()) {
                        delEmployeeService.updateDelEmployeeReturnReason(idArr[i], checkReturn.getMessage(),
                                Constants.NATIONWIDE_DISPATCH_DELETE_TABLE_NAME);
                        return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(),
                                delEmployee.getEmployeeName() + "不可提交，操作失败");
                    } else {
                        delEmployee.setOperator(checkReturn.getEditStr());
                        delEmployee.setInquirer(checkReturn.getLookStr());
                        delEmployee.setSubordinateDepartment(checkReturn.getDeptStr());
                        if (Constants.ID_CARD_CHINESE.equals(delEmployee.getIdentityNoType())) {
                            delEmployee.setGender(checkReturn.getGender());
                            delEmployee.setBirthday(checkReturn.getBirthday());
                        }
                        list.add(delEmployee);
                    }
                }
                delEmployeeService.qgDelSubmit(list);
            }
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：减员变更提交
     * @param id 表单id
     * @param schemaCode 表单编码（减员_客户：delete_employee；减员_上海：sh_delete_employee；减员_全国：nationwide_dispatch_delete）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/15 9:33
     */
    @GetMapping("/delUpdateSubmit")
    @ResponseBody
    public ResponseResult <Void> delUpdateSubmit(String id, String schemaCode) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        try {
            if (Constants.DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                delEmployeeService.delUpdateSubmit(id, this.getUserId(), this.getOrganizationFacade());
            } else if (Constants.SH_DELETE_EMPLOYEE_SCHEMA.equals(schemaCode)) {
                delEmployeeService.shDelUpdateSubmit(id);
            } else if (Constants.NATIONWIDE_DISPATCH_DELETE_SCHEMA.equals(schemaCode)) {
                delEmployeeService.qgDelUpdateSubmit(id);
            }
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：减员取派
     * @param id 减员_客户表单id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/15 16:24
     */
    @GetMapping("/cancelDispatch")
    @ResponseBody
    public ResponseResult <Void> cancelDispatch(String id) {
        try {
            delEmployeeService.cancelDispatch(id);
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：停缴提交
     * @param ids 表单id，多个id使用“,”隔开
     * @param schemaCode 表单编码（社保停缴：social_security_close；公积金停缴：provident_fund_close；）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/16 9:17
     */
    @GetMapping("/closeSubmit")
    @ResponseBody
    public ResponseResult <Void> closeSubmit(String ids, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        try {
            List <String> idList = Arrays.asList(ids.split(","));
            delEmployeeService.closeSubmit(idList, schemaCode);
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }

    /**
     * 方法说明：停缴驳回
     * @param ids 表单id，多个id使用“,”隔开
     * @param returnReason 退回原因
     * @param schemaCode 表单编码（社保停缴：social_security_close；公积金停缴：provident_fund_close；）
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Void>
     * @author liulei
     * @Date 2020/6/16 10:21
     */
    @GetMapping("/closeReject")
    @ResponseBody
    public ResponseResult <Void> closeReject(String ids, String returnReason, String schemaCode) {
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(returnReason) || StringUtils.isBlank(schemaCode)) {
            return this.getErrResponseResult(ErrCode.SYS_PARAMETER_EMPTY.getErrCode(),
                    ErrCode.SYS_PARAMETER_EMPTY.getErrMsg());
        }
        returnReason = returnReason.trim();
        try {
            List <String> idList = Arrays.asList(ids.split(","));
            delEmployeeService.closeReject(idList, returnReason, schemaCode);
            return this.getOkResponseResult(ErrCode.OK.getErrMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.getErrResponseResult(ErrCode.UNKNOW_ERROR.getErrCode(), e.getMessage());
        }
    }
}
