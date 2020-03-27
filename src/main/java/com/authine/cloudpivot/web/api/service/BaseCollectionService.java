package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.StartCollect;

import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseCollectionService
 * @Author:lfh
 * @Date:2020/3/17 10:09
 * @Description: 基数采集服务层
 **/
public interface BaseCollectionService {
    /**
     * 通过发起基数采集的id查询基数采集客户的id
     * @param bizObjectId   发起基数采集的id
     * @param clientName 客户名称
     * @return 匹配的基数采集客户id集合
     */
    String findClientIds(String bizObjectId, String clientName);

    /**
     * 通过公司名称（id）查询公司名称
     * @param client
     * @return
     */
    List<Map<String,String>> findClientName(List<String> client);

    /**
     * 通过bizObjectId 查询附件表中是否已经生成附件信息
     * @param bizObjectId
     * @return
     */
    String findAttachment(String bizObjectId);

    /**
     * 插入附件表
     * @param map
     */
    void insertAttachment(Map<String, Object> map);

    /**
     * 通过附件表的id查询附件表名
     * @param attachmentId
     * @return 附件表名
     */
    String findAttachmentName(String attachmentId);

    /**
     * 更新附件表文件大小
     * @param length
     */
    void updateFileSize(String attachment, long length);

    StartCollect getStartCollectById(String bizObjectId);
}
