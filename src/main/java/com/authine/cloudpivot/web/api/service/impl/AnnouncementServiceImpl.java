package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.AnnouncementMapper;
import com.authine.cloudpivot.web.api.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:wangyong
 * @Date:2020/4/16 10:26
 * @Description:
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    AnnouncementMapper announcementMapper;

    /**
     * @Author: wangyong
     * @Date: 2020/4/16 10:32
     * @return: java.lang.String
     * @Description: 获取公告内容
     */
    @Override
    public String getAnnouncementContent() {
        return announcementMapper.getAnnouncementContent();
    }
}
