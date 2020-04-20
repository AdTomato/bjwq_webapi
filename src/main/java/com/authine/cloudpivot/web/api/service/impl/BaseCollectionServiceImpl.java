package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.StartCollect;
import com.authine.cloudpivot.web.api.mapper.BaseCollectionMapper;
import com.authine.cloudpivot.web.api.service.BaseCollectionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseCollectionServiceImpl
 * @Author:lfh
 * @Date:2020/3/17 10:09
 * @Description: 基数采集服务层
 **/
@Service
public class BaseCollectionServiceImpl implements BaseCollectionService {

    @Resource
    private BaseCollectionMapper baseCollectionMapper;

    /**
     * 通过发起基数采集的id查询基数采集客户的id
     * @param bizObjectId   发起基数采集的id
     * @param clientName 客户名称
     * @return 匹配的基数采集客户id集合
     */
    @Override
    public String  findClientIds(String bizObjectId,String clientName) {
        return baseCollectionMapper.findClientIds(bizObjectId,clientName);
    }

    @Override
    public List<Map<String,String>> findClientName(List<String> client) {
        return baseCollectionMapper.findClientName(client);
    }

    /**
     * 通过bizObjectId 查询附件表中是否已经生成附件信息
     * @param bizObjectId
     * @return
     */
    @Override
    public String findAttachment(String bizObjectId) {
        return baseCollectionMapper.findAttachment(bizObjectId);
    }

    /**
     * 插入附件表
     * @param map
     */
    @Override
    public void insertAttachment(Map<String, Object> map) {
        baseCollectionMapper.insertAttachment(map);
    }

    /**
     * 通过附件表的id查询附件表的表名
     * @param attachmentId
     * @return 附件表名
     */
    @Override
    public String findAttachmentName(String attachmentId) {
        return baseCollectionMapper.findAttachmentName(attachmentId);
    }

    @Override
    public void updateFileSize(String attachmentId, long length) {
        baseCollectionMapper.updateFileSize(attachmentId,length);
    }

    @Override
    public StartCollect getStartCollectById(String bizObjectId) {
        return baseCollectionMapper.getStartCollectById(bizObjectId);
    }

    @Override
    public String findCompanyName(String companyName) {
       return baseCollectionMapper.findCompanyName(companyName);

    }

    @Override
    public String findSalesman(String clientName) {
        return baseCollectionMapper.findSalesman(clientName);
    }

    /**
     * 查询二级客户
     * @param clientName
     * @return
     */
    @Override
    public String findSecondCompanyName(String clientName) {
        return baseCollectionMapper.findSecondCompanyName(clientName);
    }

    @Override
    public String findSalesmanFromSecondClient(String clientName) {
        return baseCollectionMapper.findSalesmanFromSecondClient(clientName);
    }


}
