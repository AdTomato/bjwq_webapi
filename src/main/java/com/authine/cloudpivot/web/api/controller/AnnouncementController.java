package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.AnnouncementService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: wangyong
 * @date:2020/4/16 10:28
 * @description: 公告控制层
 */
@RestController
@Slf4j
@RequestMapping("/controller/announcementController")
@Api(value = "公告信息", tags = "二次开发：公告信息")
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
    @ApiOperation(value = "获取最近一次发布的公告信息")
    public ResponseResult<Object> getAnnouncementContent() {
        String announcementContent = announcementService.getAnnouncementContent();
        if (StringUtil.isEmpty(announcementContent)) {
            return this.getErrResponseResult(null, 405L, "没有公告数据");
        }
        return this.getErrResponseResult(announcementContent, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @GetMapping("/getAnnouncementContentList")
    @ApiOperation(value = "获取公告id和标题列表")
    public ResponseResult<Object> getAnnouncementContentList() {
        List<Map<String, String>> announcementContentList = announcementService.getAnnouncementContentList();
        if (announcementContentList == null || announcementContentList.isEmpty()) {
            return this.getErrResponseResult(null, 405L, "没有公告数据");
        }
        return this.getErrResponseResult(announcementContentList, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @GetMapping("/getAnnouncementContentById")
    @ApiOperation(value = "根据公告id获取公告内容")
    public ResponseResult<Object> getAnnouncementContentById(@ApiParam(value = "公告id", required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return this.getErrResponseResult(null, 407L, "id不能为空");
        }
        String announcementContent = announcementService.getAnnouncementContentById(id);
        if (StringUtil.isEmpty(announcementContent)) {
            return this.getErrResponseResult(null, 405L, "没有公告数据");
        }
        return this.getErrResponseResult(announcementContent, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }
}
