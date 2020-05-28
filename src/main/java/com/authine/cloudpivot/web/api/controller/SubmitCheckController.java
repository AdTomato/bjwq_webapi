package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.ClientManagement;
import com.authine.cloudpivot.web.api.entity.ProcessIdentityNo;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckParams;
import com.authine.cloudpivot.web.api.params.AddEmployeeCheckReturn;
import com.authine.cloudpivot.web.api.service.CityTimeNodeService;
import com.authine.cloudpivot.web.api.service.ClientManagementService;
import com.authine.cloudpivot.web.api.service.ClientService;
import com.authine.cloudpivot.web.api.service.CollectionRuleService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 提交检验
 * @author wangyong
 * @time 2020/5/26 11:25
 */
@RestController
@Slf4j
@Api(value = "提交校验", tags = "二次开发：提交校验")
@RequestMapping("/controller/submitCheckout")
public class SubmitCheckController extends BaseController {

    @Autowired
    ClientManagementService clientManagementService;

    @Autowired
    ClientService clientService;

    @Autowired
    CityTimeNodeService cityTimeNodeService;

    @Autowired
    CollectionRuleService collectionRuleService;



    @ApiOperation(value = "增员客户提交校验")
    @GetMapping("/addEmployeeCheck")
    public ResponseResult<Object> addEmployeeCheck(@RequestBody AddEmployeeCheckParams params) {
        String creater = params.getCreater();
        ClientManagement clientManagement = clientManagementService.getClientNameByUserId(creater);
        AddEmployeeCheckReturn result = new AddEmployeeCheckReturn();
        result.setCanSubmit(true);

        if ("身份证".equals(params.getIdentityNoType())) {
            ProcessIdentityNo processIdentityNo = new ProcessIdentityNo(params.getIdentityNo());
            result.setBirthday(processIdentityNo.getBirthday());
            result.setGender(processIdentityNo.getGender());
        }

        if (clientManagement != null && !StringUtils.isEmpty(clientManagement.getFirstLevelClientName()) && !StringUtils.isEmpty(clientManagement.getSecondLevelClientName())) {
            // 一级客户名称
            String firstLevelClientName = clientManagement.getFirstLevelClientName();

            // 二级客户名称
            String secondLevelClientName = clientManagement.getSecondLevelClientName();

            result.setFirstLevelClientName(firstLevelClientName);
            result.setSecondLevelClientName(secondLevelClientName);
            // 业务类型
            String businessType = params.getBusinessType();

            // 福利地
            String welfare = params.getWelfare();

            // 福利办理方
            String welfareHandler = params.getWelfareHandler();

            // 查看人、操作人、所属部门
            Map<String, String> lookAndEditPerson = clientService.getLookAndEditPerson(firstLevelClientName, secondLevelClientName, businessType, welfare, welfareHandler);
            result.setLook(lookAndEditPerson.get("look"));
            result.setEdit(lookAndEditPerson.get("edit"));
            result.setDept(lookAndEditPerson.get("dept"));

            Calendar calendar = Calendar.getInstance();
            Date notTime = new Date();
            calendar.setTime(notTime);
            String businessYear = calendar.get(Calendar.MONTH) < 10 ? calendar.get(Calendar.YEAR) + "0" + calendar.get(calendar.get(Calendar.MONTH)) : calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
            CityTimeNode cityTimeNode = cityTimeNodeService.getCityTimeNodeByBusinessYearAndCity(businessYear, welfare);
            if (cityTimeNode != null) {
                if (cityTimeNode.getStartTime() != null && cityTimeNode.getEndTime() != null) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTime(cityTimeNode.getStartTime());
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(cityTimeNode.getEndTime());
                    endTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59, 59);
                    if (!(notTime.getTime() > startTime.getTimeInMillis() && notTime.getTime() < endTime.getTimeInMillis())) {
                        result.setCanSubmit(false);
                        result.setMessage(StringUtils.isEmpty(result.getMessage()) ? welfare + "的申报时间范围为：" + startTime + "-" + endTime + "\n" : result.getMessage() + welfare + "的申报时间范围为：" + startTime + "-" + endTime + "\n" );
                    }
                }
            }
            Double companyRadio = params.getCompanyProvidentFundBl();
            boolean isHave = collectionRuleService.isHaveCompanyRatioInMaxStartMonth(welfare, companyRadio);
            if (!isHave) {
                result.setCanSubmit(false);
                result.setMessage(StringUtils.isEmpty(result.getMessage()) ? "公积金单位比例：" + companyRadio + "在" + welfare + "无法申报\n" : result.getMessage() + "公积金单位比例：" + companyRadio + "在" + welfare + "无法申报\n");
            }
        } else {
            result.setCanSubmit(false);
            result.setMessage("一级客户名称/二级客户名称未查询到");
        }
        return this.getErrResponseResult(result, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
