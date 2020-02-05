package com.authine.cloudpivot.web.api.utils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.utils.SendMailUtils
 * @Date 2019/12/26 14:31
 **/
public class SendMailUtils {

    /**
     * 方法说明：发送邮件
     *
     * @return void
     * @throws
     * @Param subject 邮件主题
     * @Param body 邮件内容
     * @Param recipients 收件人
     * @author liulei
     * @Date 2019/12/26 14:38
     */
    public static void sendMail(String subject, String body, String recipients) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "mail.fesco.com.cn");// 主机名
        properties.put("mail.smtp.port", 25);// 端口号
        properties.put("mail.smtp.auth", "true");// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        //properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        message.setFrom(new InternetAddress("C-LOOP@fesco.com.cn"));
        // 设置收件人邮箱地址
        List<InternetAddress> internetAddressesList = new ArrayList<>();
        String[] recipientArr = recipients.split(",");
        for (String recipient : recipientArr) {//收件人
            internetAddressesList.add(new InternetAddress(recipient));
        }
        Address[] address = new Address[internetAddressesList.size()];
        internetAddressesList.toArray(address);
        message.setRecipients(Message.RecipientType.TO, address);
        /*//一个收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("chu.xiao.jun@fesco.com.cn"));
        //多个收件人
        message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com")});*/
        // 设置邮件标题
        message.setSubject(subject);
        // 设置邮件内容
        message.setText(body);
        // 得到邮差对象
        Transport transport = session.getTransport();
        // 连接自己的邮箱账户
        transport.connect("C-LOOP@fesco.com.cn", "fesco*!abd2019");// 密码为邮箱开通的stmp服务后得到的客户端授权码
        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
