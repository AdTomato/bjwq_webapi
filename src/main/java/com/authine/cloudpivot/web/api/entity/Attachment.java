package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:wangyong
 * @Date:2020/3/24 14:24
 * @Description: 附件实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    private String id;
    private String bizObjectId;
    private String bizPropertyCode;
    private Date createdTime;
    private String creater;
    private String fileExtension;
    private Integer fileSize;
    private String mimeType;
    private String name;
    private String parentBizObjectId;
    private String parentSchemaCode;
    private String refId;
    private String schemaCode;

}
