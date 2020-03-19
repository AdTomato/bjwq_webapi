package com.authine.cloudpivot.web.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/3/12 21:47
 * @Description:
 */
public class SendSmsUtils {



    public static int sendSms(String content, String destMobiles) throws IOException {
        // 发送短信地址，以下为示例地址，具体地址询问网关获取
        String url_send_sms = "http://112.122.11.247:8860/sendSms";
        // 用户账号，必填
        String cust_code = "000042";
        // 用户密码，必填
        String cust_pwd = "123456";
        // 业务标识，选填，由客户自行填写不超过20位的数字
        String uid = "";
        // 长号码，选填
        String sp_code = "";
        // 是否需要状态报告
        String need_report = "yes";
        // 数字签名，签名内容根据 “短信内容+客户密码”进行MD5编码后获得
        String sign = content + cust_pwd;
        sign = MD5.getMD5(sign.getBytes("UTF-8"));
        String json_send_sms = "{\"cust_code\":\"" + cust_code + "\",\"sp_code\":\"" + sp_code + "\",\"content\":\"" + content +
                "\",\"destMobiles\":\"" + destMobiles + "\",\"uid\":\"" + uid + "\",\"need_report\":\"" + need_report + "\",\"sign\":\"" + sign + "\"}";
        URL url = new URL(url_send_sms);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        OutputStream out = httpURLConnection.getOutputStream();
        out.write(json_send_sms.getBytes("UTF-8"));
        System.out.println("send msg:\t"+json_send_sms);
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            System.out.println("recv msg:\t"+resultBuffer.toString());
            JSONObject parse = (JSONObject) JSON.parse(resultBuffer.toString());
            if (null != parse) {
                return parse.getInteger("respCode");
            }
            return -1;
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
    }


}
