package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.AnnouncementMapper;
import com.authine.cloudpivot.web.api.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取没有关闭的公告id公告标题列表
     *
     * @return 公告id、公告标题列表
     * @author wangyong
     */
    @Override
    public List<Map<String, String>> getAnnouncementContentList() {
        return announcementMapper.getAnnouncementContentList();
    }

    /**
     * 根据公告id获取公告内容
     *
     * @param id 公告id
     * @return 公告内容
     * @author wangyong
     */
    @Override
    public String getAnnouncementContentById(String id) {
        return announcementMapper.getAnnouncementContentById(id);
    }
}
