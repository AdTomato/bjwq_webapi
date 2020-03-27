package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.Attachment;
import com.authine.cloudpivot.web.api.mapper.AttachmentMapper;
import com.authine.cloudpivot.web.api.service.AttachmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:wangyong
 * @Date:2020/3/24 14:23
 * @Description: 附件serivice
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Resource
    AttachmentMapper attachmentMapper;

    @Override
    public Attachment getFileName(String bizObjectId, String schemaCode, String bizPropertyCode) {
        return attachmentMapper.getFileName(bizObjectId, schemaCode, bizPropertyCode);
    }
}
