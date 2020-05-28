package com.authine.cloudpivot.web.api.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/4/2 21:46
 * @Description: 导入进度
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportProgress {

    /**
     * 总行数
     */
    public Integer allNum;

    /**
     * 成功行数
     */
    public Integer successNum;

    /**
     * 失败行数
     */
    public Integer fileNum;

    /**
     * 失败原因
     */
    public String fileReason;

    /**
     * 是否结束
     */
    public boolean isOver;


}
