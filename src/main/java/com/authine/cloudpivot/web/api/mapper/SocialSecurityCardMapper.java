package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ImportSocialSecurityCardTemp;
import com.authine.cloudpivot.web.api.entity.SocialSecurityCard;

import java.util.List;

/**
 * @author liulei
 * @Description 福利平台，社保卡
 * @ClassName com.authine.cloudpivot.web.api.mapper.SocialSecurityCardMapper
 * @Date 2020/3/17 10:31
 **/
public interface SocialSecurityCardMapper {
    /**
     * 方法说明：批量生成社保卡办理数据
     * @param socialSecurityCards 社保卡办理数据
     * @return void
     * @author liulei
     * @Date 2020/3/17 10:40
     */
    void batchInsertSocialSecurityCard(List<SocialSecurityCard> socialSecurityCards);

    /**
     * 方法说明：导入社保卡申请数据时更新字段内容
     *  更新字段：业务员，地区（改为关联表单的id）,经办人，VIP客户，联系电话
     * @param ids 导入的社保卡申请数据id
     * @return void
     * @author liulei
     * @Date 2020/3/17 10:40
     */
    void updateSocialSecurityCardWhenInsert(List<String> ids);

    /**
     * 方法说明：导入办理状态和发卡进度时生成临时表数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/17 13:51
     */
    void insertTempData(List<ImportSocialSecurityCardTemp> list);

    /**
     * 方法说明：根据临时表数据，更新子表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/3/17 14:24
     */
    void updateProcessByTempTable(String sourceId);

    /**
     * 方法说明：根据临时表数据，更新主表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/3/17 15:04
     */
    void updateProcessFeedbackByTempTable(String sourceId);

    /**
     * 方法说明：根据临时表数据，更新子表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/3/17 15:05
     */
    void updateIssueByTempTable(String sourceId);

    /**
     * 方法说明：根据临时表数据，更新主表数据
     * @param sourceId
     * @return void
     * @author liulei
     * @Date 2020/3/17 15:05
     */
    void updateIssueFeedbackByTempTable(String sourceId);

    /**
     * 方法说明：获取办理成功的待办任务id
     * @param sourceId
     * @param userId
     * @return java.util.List<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 15:05
     */
    List<String> getWorkItemIdsWhenProcessSuccess(String sourceId, String userId);

    /**
     * 方法说明：获取办理失败的待办任务id
     * @param sourceId
     * @param userId
     * @return java.util.List<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 15:07
     */
    List<String> getWorkItemIdsWhenProcessError(String sourceId, String userId);

    /**
     * 方法说明：获取发卡成功的待办任务id
     * @param sourceId
     * @param userId
     * @return java.util.List<java.lang.String>
     * @author liulei
     * @Date 2020/3/17 15:07
     */
    List<String> getWorkItemIdsWhenIssueSuccess(String sourceId, String userId);

    void deleteTempData(String sourceId);
}
