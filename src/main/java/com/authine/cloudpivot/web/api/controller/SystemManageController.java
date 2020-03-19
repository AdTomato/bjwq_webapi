package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.OrgUser;
import com.authine.cloudpivot.web.api.entity.SmsHistory;
import com.authine.cloudpivot.web.api.service.SystemManageService;
import com.authine.cloudpivot.web.api.utils.SendSmsUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author: wangyong
 * @Date: 2020-02-07 15:07
 * @Description: 系统管理controller
 */
@RestController
@Slf4j
@RequestMapping("/controller/systemManage")
public class SystemManageController extends BaseController {

    private static final long MAX_SECOND_DIFFERENCE = 180;  // 短信验证码的有效时间

    @Autowired
    SystemManageService systemManageService;

    @GetMapping("/getTimeNode")
    public ResponseResult<Map<String, Integer>> getTimeNode(String cityName) {
        Integer timeNode = systemManageService.getTimeNodeByCity(cityName);
        Map<String, Integer> result = new HashMap<>();
        result.put("timeNode", timeNode);
        return this.getOkResponseResult(result, "成功");
    }

    /**
     * @param mobile: 手机号码
     * @Author: wangyong
     * @Date: 2020/3/15 23:54
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 发送短信验证码
     */
    @PostMapping("/sendSmsCode")
    public ResponseResult<Object> sendSmsCode(@RequestParam String mobile){
        log.info("发送验证码，手机号码为：" + mobile);
        OrgUser user = systemManageService.getOrgUserByMobile(mobile);
        if (null == user) {
            this.getErrResponseResult(null, 404L, "该手机号码不存在系统中");
        }
        String userId = Constants.ADMIN_ID;
        String code = getCode();
        String content = "您的验证码为：" + code + "，有效时间为3分钟";
        int result = 0;
        try {
            result = SendSmsUtils.sendSms(content, mobile);
            if (result == 0) {
                // 发送成功，生成数据
                BizObjectModel model = new BizObjectModel();
                model.setSchemaCode(Constants.SMS_HISTORY_SCHEMA);
                model.setSequenceStatus(Constants.COMPLETED_STATUS);
                Map data = new HashMap();
                data.put("phone", mobile);
                data.put("time", new Date());
                data.put("code", code);
                model.put(data);
                String id = this.getBizObjectFacade().saveBizObjectModel(userId, model, "id");
                return this.getErrResponseResult(id, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
            } else {
                return this.getErrResponseResult(null, 404L, "短信发送失败，失败code：" + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return this.getErrResponseResult(null, 405L, "短信验证码发送失败");
        }
    }

    /**
     * @param id:       id
     * @param code:     验证码
     * @param mobile:   手机号码
     * @param password: 新密码
     * @Author: wangyong
     * @Date: 2020/3/15 23:54
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 修改密码
     */
    @PutMapping("/modifyPassword")
    public ResponseResult<Object> modifyPassword(@RequestParam String id, @RequestParam String code, @RequestParam String mobile, @RequestParam String password) {
        log.info("修改密码");
        log.info("id：" + id);
        log.info("mobile：" + mobile);
        log.info("password：" + password);
        SmsHistory smsHistory = systemManageService.getSmsHistoryById(id);
        if (null == smsHistory) {
            return getErrResponseResult(null, 404L, "没有查询到该id的验证码历史数据");
        }

        if (!smsHistory.getPhone().equals(mobile)) {
            return getErrResponseResult(null, 406L, "获取验证码的手机号码和修改密码的手机号码不一致");
        }

        if (!smsHistory.getCode().equals(code)) {
            return getErrResponseResult(null, 407L, "短信验证码错误");
        }

        long second = getTimeDifference(new Date(), smsHistory.getTime());
        if (second <= MAX_SECOND_DIFFERENCE) {
            // 在有效期内
            // 修改密码
            OrgUser user = systemManageService.getOrgUserByMobile(mobile);
            UserModel userModel = this.getOrganizationFacade().getUser(user.getId());
            String pwd = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
            userModel.setPassword(pwd);
            this.getOrganizationFacade().updateUser(userModel);
            return getErrResponseResult(null, ErrCode.OK.getErrCode(), "密码修改成功");
        } else {
            // 不在有效期内
            return getErrResponseResult(null, 405L, "短信验证码失效");
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/15 23:21
     * @return: java.lang.String
     * @Description: 生成一个五位数的随机数
     */
    private String getCode() {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(r.nextInt(10) + "");
        }
        return sb.toString();
    }

    /**
     * @param time1: 时间1
     * @param time2: 时间2
     * @Author: wangyong
     * @Date: 2020/3/15 23:45
     * @return: long
     * @Description: 获取两个时间所相差的秒数差
     */
    private long getTimeDifference(Date time1, Date time2) {
        long second = (time2.getTime() - time1.getTime()) / 1000;
        return second < 0 ? -second : second;
    }

}
