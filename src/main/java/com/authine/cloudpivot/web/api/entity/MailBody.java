package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @author liulei
 * @Description 五险一金享受邮件发送内容
 * @ClassName com.authine.cloudpivot.web.api.entity.MailBody
 * @Date 2020/1/5 20:00
 **/
@Data
public class MailBody {
    /**
     * 业务发起途径
     */
    String businessInitiationChannel;
    /**
     * 城市
     */
    String city;
    /**
     * 城市
     */
    String isEmerg;
    /**
     * 城市
     */
    String isVip;
    /**
     * 委托方
     */
    String entrust;
    /**
     * 客户名称
     */
    String clientName;
    /**
     * 身份证号
     */
    String employeeName;
    /**
     * 城市
     */
    String identityNo;
    /**
     * 业务类型
     */
    String businessType;
    /**
     * 业务项目
     */
    String businessItem;
    /**
     * 详细描述
     */
    String detail;
    /**
     * 收件人
     */
    String recipients;
    /**
     * 经办人
     */
    String operator;

    @Override
    public String toString() {
        return "MailBody{" +
                "businessInitiationChannel='" + businessInitiationChannel + '\'' +
                ", city='" + city + '\'' +
                ", isEmerg='" + isEmerg + '\'' +
                ", isVip='" + isVip + '\'' +
                ", entrust='" + entrust + '\'' +
                ", clientName='" + clientName + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", identityNo='" + identityNo + '\'' +
                ", businessType='" + businessType + '\'' +
                ", businessItem='" + businessItem + '\'' +
                ", detail='" + detail + '\'' +
                ", recipients='" + recipients + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }

    public String getBusinessInitiationChannel() {
        return businessInitiationChannel;
    }

    public void setBusinessInitiationChannel(String businessInitiationChannel) {
        this.businessInitiationChannel = businessInitiationChannel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsEmerg() {
        return isEmerg;
    }

    public void setIsEmerg(String isEmerg) {
        this.isEmerg = isEmerg;
    }

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    public String getEntrust() {
        return entrust;
    }

    public void setEntrust(String entrust) {
        this.entrust = entrust;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessItem() {
        return businessItem;
    }

    public void setBusinessItem(String businessItem) {
        this.businessItem = businessItem;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
