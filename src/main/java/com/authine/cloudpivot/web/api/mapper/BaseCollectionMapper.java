package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.BaseInfoCollection;
import com.authine.cloudpivot.web.api.entity.StartCollect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseCollectionMapper
 * @Author:lfh
 * @Date:2020/3/17 10:11
 * @Description: 基数采集mapper
 **/
public interface BaseCollectionMapper {

    /**
     * 通过发起基数采集的id查询基数采集客户的id
     * @param bizObjectId 发起基数采集的id
     * @return 匹配的基数采集客户id集合
     */
    String findClientIds(String bizObjectId, String clientName);

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
     * 通过附件表的id查询附件表的表名
     * @param attachmentId
     * @return 附件表名
     */
    String findAttachmentName(String attachmentId);

    void updateFileSize(String attachmentId, long length);

    StartCollect getStartCollectById(String bizObjectId);

    String findCompanyName(String companyName);

    String findSalesman(String clientName);

    String findSecondCompanyName(String clientName);

    String findSalesmanFromSecondClient(String clientName);

    void insertCollectInfo(List<BaseInfoCollection> clientBaseNumInfo);

    // List<BaseInfoCollection> findBaseCollectInfoFromTotalInfo(List<BaseInfoCollection> baseInfoCollections,String start_collect_id);
    List<BaseInfoCollection> findBaseCollectInfoFromTotalInfo(Map<String,Object> map);

    void deleteFoundCollectInfo(List<BaseInfoCollection> collectInTotalInfo);

}
