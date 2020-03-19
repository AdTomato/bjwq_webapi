package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dao.SendMessageDao;
import com.authine.cloudpivot.web.api.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.SendMessageServiceImpl
 * @Date 2020/1/7 11:03
 **/
@Service
public class SendMessageServiceImpl implements SendMessageService {

    @Autowired
    private SendMessageDao sendMessageDao;

    @Override
    public void sendMessageHaveTimeout() throws Exception{
        // 获取需要提醒的数据
        List <Map <String, String>> sendMessage = sendMessageDao.getHaveTimeOutInfo();
        // 发送钉钉消息
        if (sendMessage != null && sendMessage.size() > 0) {
            for (int i = 0; i < sendMessage.size(); i++) {
                String message = sendMessage.get(i).get("message");
                String sendId = sendMessage.get(i).get("sendId");
                // TODO 发送信息

            }
        }
    }
}
