package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.BaseEntity;
import lombok.Data;

/**
 * @ClassName CollectParams
 * @Author:lfh
 * @Date:2020/4/21 11:40
 * @Description:
 **/
@Data
public class CollectParams{
    /**
     * 当前表单id
     */
    private String id;
    /**
     * 上传的excel文件名
     */
    private String fileName;
    /**
     * 基数采集的id
     */
    private String submit_collect;
    /**
     * 发起基数采集的id
     */
    private String start_collect_id;

    /**
     * 表单类型
     */
    private String formType;
}
