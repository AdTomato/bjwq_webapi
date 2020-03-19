package com.authine.cloudpivot.web.api.service;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.SendMessageService
 * @Date 2020/1/7 11:03
 **/
public interface SendMessageService {
    /**
     * 方法说明：社保卡，五险一金办理超时提醒
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:28
     * @param imMessageFacade
     */
    void sendMessageHaveTimeout() throws Exception;
}
