package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.AdjustBaseRatioTask;

/**
 * @author liulei
 * @Description 调基调比Service
 * @ClassName com.authine.cloudpivot.web.api.service.AdjustBaseAndRatioService
 * @Date 2020/4/13 17:06
 **/
public interface AdjustBaseAndRatioService {
    /**
     * 方法说明：根据id获取调整任务数据
     * @param id 调整任务id
     * @return com.authine.cloudpivot.web.api.entity.AdjustBaseRatioTask
     * @author liulei
     * @Date 2020/4/23 14:10
     */
    AdjustBaseRatioTask getAdjustBaseRatioTaskById(String id) throws Exception;

    /**
     * 方法说明：根据调整任务导入的文件生成调整详细数据
     * @param fileName 文件名称
     * @param task 调整任务
     * @return void
     * @author liulei
     * @Date 2020/4/23 14:14
     */
    void adjustImport(String fileName, AdjustBaseRatioTask task) throws Exception;

    /**
     * 方法说明：调整基数和比例
     * @param task 调整任务
     * @return void
     * @author liulei
     * @Date 2020/4/23 14:58
     */
    void adjustBaseAndRatio(AdjustBaseRatioTask task) throws Exception;
}
