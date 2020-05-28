package com.authine.cloudpivot.web.api.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 五险一金享受
 * @ClassName com.authine.cloudpivot.web.api.mapper.InsuranceAndHousingFundMapper
 * @Date 2020/5/7 10:17
 **/
public interface InsuranceAndHousingFundMapper {
    /**
     * 方法说明：五险一金，导入办理状态子表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/5/7 10:20
     */
    void updateEnjoyProgressByTempTable(String sourceId);

    /**
     * 方法说明：五险一金，导入办理状态更新主表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/5/7 10:21
     */
    void updateProcessFeedbackByTempTable(String sourceId);

    /**
     * 方法说明：五险一金，导入业务反馈，更新主表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/5/7 10:21
     */
    void updateEnjoyFeedbackByTempTable(String sourceId);

    /**
     * 方法说明：导入临时表数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/5/7 10:28
     */
    void insertTempData(List<Map<String, Object>> list);
}
