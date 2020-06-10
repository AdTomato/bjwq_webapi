package com.authine.cloudpivot.web.api.service;

import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/4/16 10:26
 * @Description:
 */
public interface AnnouncementService {

    /**
     * 获取公告内容
     *
     * @return
     */
    String getAnnouncementContent();


    /**
     * 获取没有关闭的公告id公告标题列表
     *
     * @return 公告id、公告标题列表
     * @author wangyong
     */
    List<Map<String, String>> getAnnouncementContentList();

    /**
     * 根据公告id获取公告内容
     *
     * @param id 公告id
     * @return 公告内容
     * @author wangyong
     */
    String getAnnouncementContentById(String id);

}
