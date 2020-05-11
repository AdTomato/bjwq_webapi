package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.AnnouncementService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangyong
 * @date:2020/4/16 10:28
 * @description: 公告控制层
 */
@RestController
@Slf4j
@RequestMapping("/controller/announcementController")
public class AnnouncementController extends BaseController {

    @Autowired
    AnnouncementService announcementService;

    /**
     * @Author: wangyong
     * @Date: 2020/4/16 10:32
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 获取最新的公告内容
     */
    @GetMapping("/getAnnouncementContent")
    public ResponseResult<Object> getAnnouncementContent() {
//        String userId = getUserId();
//        if (StringUtil.isEmpty(userId)) {
//            return this.getErrResponseResult(null, 404L, "非法访问接口");
//        }
        String announcementContent = announcementService.getAnnouncementContent();
        if (StringUtil.isEmpty(announcementContent)) {
            return this.getErrResponseResult(null, 405L, "没有公告数据");
        }
        return this.getErrResponseResult(announcementContent, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
