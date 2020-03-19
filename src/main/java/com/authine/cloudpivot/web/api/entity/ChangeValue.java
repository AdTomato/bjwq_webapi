package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @Author:wangyong
 * @Date:2020/3/13 23:46
 * @Description:
 */
@Data
public class ChangeValue {

    private String id;
    private String parentId;
    private Double sortKey;
    private String updateField;
    private String updateBeforeValue;
    private String updateAfterValue;

}
