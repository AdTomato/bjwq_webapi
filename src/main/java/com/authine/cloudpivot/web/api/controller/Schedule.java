package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.im.MessageModel;
import com.authine.cloudpivot.engine.enums.type.IMMessageChannelType;
import com.authine.cloudpivot.engine.enums.type.IMMessageType;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liulei
 * @Description 定时器任务类
 * @ClassName com.authine.cloudpivot.web.api.controller.Schedule
 * @Date 2020/1/2 11:29
 **/
@Component
@Slf4j
public class Schedule extends BaseController {

    @Autowired
    private SendMessageService sendMessageService;

    /**
     * 方法说明：每天凌晨执行一次，五险一金享受办理超时提醒
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/2 11:39
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void start() {
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        log.info("社保卡,五险一金办理超时提醒:" + time + " start ====================");

        try {
            sendMessageService.sendMessageHaveTimeout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("社保卡,五险一金办理超时提醒:" + time + " end ====================");*/
    }

}
