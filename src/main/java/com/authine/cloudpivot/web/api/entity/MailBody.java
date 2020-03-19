package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 五险一金享受邮件发送内容
 * @ClassName com.authine.cloudpivot.web.api.entity.MailBody
 * @Date 2020/1/5 20:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailBody{
    /** 业务发起途径 */
    String businessInitiationChannel;
    /** 城市 */
    String city;
    /** 城市 */
    String isEmerg;
    /** 城市 */
    String isVip;
    /** 委托方 */
    String entrust;
    /** 客户名称 */
    String clientName;
    /** 身份证号 */
    String employeeName;
    /** 城市 */
    String identityNo;
    /** 业务类型 */
    String businessType;
    /** 业务项目 */
    String businessItem;
    /** 详细描述 */
    String detail;
    /** 收件人 */
    String recipients;
    /** 经办人 */
    String operator;
}
