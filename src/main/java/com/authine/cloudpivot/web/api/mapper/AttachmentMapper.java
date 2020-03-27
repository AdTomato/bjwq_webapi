package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.Attachment;

/**
 * @Author:wangyong
 * @Date:2020/3/24 14:18
 * @Description: 附件mapper
 */
public interface AttachmentMapper {

    Attachment getFileName(String bizObjectId, String schemaCode, String bizPropertyCode);

}
