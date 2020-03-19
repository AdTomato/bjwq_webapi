package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.MailBody;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.utils.SendMailUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liulei
 * @Description 发送邮件
 * @ClassName com.authine.cloudpivot.web.api.controller.SendMailController
 * @Date 2019/12/26 10:49
 **/
@RestController
@RequestMapping("/controller/sendMailController")
@Slf4j
public class SendMailController extends BaseController {

    /**
     * 方法说明：
     * @Param mailBody 五险一金享受邮件发送内容实体
     * @return java.lang.String
     * @throws 
     * @author liulei
     * @Date 2019/12/26 10:57
     */
    @PostMapping("/sendMail")
    @CustomizedOrigin(level = 1)
    public ResponseResult <String> sendMail(@RequestBody MailBody mailBody){

        String recipients = mailBody.getRecipients();

        String subject =
                mailBody.getEmployeeName() + "办理：" + mailBody.getBusinessType() + "-" + mailBody.getBusinessItem();

        StringBuffer body = new StringBuffer();
        body.append("业务发起途径:" + mailBody.getBusinessInitiationChannel() + ";\n");
        body.append("城市:" + mailBody.getCity() + ";\n");
        body.append("紧急交办:" + mailBody.getIsEmerg() + ";\n");
        body.append("VIP客户:" + mailBody.getIsVip() + ";\n");
        body.append("委托方:" + mailBody.getEntrust() + ";\n");
        body.append("客户名称:" + mailBody.getClientName() + ";\n");
        body.append("员工姓名:" + mailBody.getEmployeeName() + ";\n");
        body.append("身份证号:" + mailBody.getIdentityNo() + ";\n");
        body.append("业务类型:" + mailBody.getBusinessType() + ";\n");
        body.append("业务项目:" + mailBody.getBusinessItem() + ";\n");
        body.append("经办人:" + mailBody.getOperator() + "\n");
        body.append("详细描述:" + mailBody.getDetail() + ";\n");

        try {
            log.info("发送邮件：" + mailBody.toString());
            SendMailUtils.sendMail(subject, body.toString(), recipients);
            log.info("发送邮件成功");
            return this.getOkResponseResult("success", "发送邮件成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", "发送邮件失败!");
        }
    }

}
