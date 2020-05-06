package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.Attachment;

/**
 * @Author:wangyong
 * @Date:2020/3/24 14:23
 * @Description: 附件service接口
 */
public interface AttachmentService {
    /**
     *
     * @param bizObjectId 表单的id
     * @param schemaCode 表单编码
     * @param bizPropertyCode 数据项名称
     * @return
     */
    Attachment getFileName(String bizObjectId, String schemaCode, String bizPropertyCode);

}
