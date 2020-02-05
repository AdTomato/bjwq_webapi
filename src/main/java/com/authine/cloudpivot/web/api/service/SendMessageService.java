package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.IMMessageFacade;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.SendMessageService
 * @Date 2020/1/7 11:03
 **/
public interface SendMessageService {
    /**
     * 方法说明：社保卡，五险一金办理超时提醒
     *
     * @param imMessageFacade
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:28
     */
    void sendMessageHaveTimeout() throws Exception;
}
