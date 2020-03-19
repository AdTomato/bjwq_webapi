package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

import java.util.UUID;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.ImportSocialSecurityCardTemp
 * @Date 2020/3/17 13:36
 **/
@Data
public class ImportSocialSecurityCardTemp {
    String id;
    String sourceId;
    String sequenceNo;
    String processSelect;
    String processDetail;
    String sinCard;
    String issueSelect;
    String issueDetail;

    /**
     * 方法说明：导入办理状态构造
     * @param sourceId
     * @param sequenceNo
     * @param processSelect
     * @param processDetail
     * @param sinCard
     * @author liulei
     * @Date 2020/3/17 13:45
     */
    public ImportSocialSecurityCardTemp( String sourceId, String sequenceNo, String processSelect,
                                        String processDetail, String sinCard) {
        setId(UUID.randomUUID().toString().replaceAll("-", ""));
        this.sourceId = sourceId;
        this.sequenceNo = sequenceNo;
        this.processSelect = processSelect;
        this.processDetail = processDetail;
        this.sinCard = sinCard;
    }

    /**
     * 方法说明：导入发卡进度构造
     * @param sourceId
     * @param sequenceNo
     * @param issueSelect
     * @param issueDetail
     * @author liulei
     * @Date 2020/3/17 13:47
     */
    public ImportSocialSecurityCardTemp(String sourceId, String sequenceNo, String issueSelect,
                                        String issueDetail) {
        setId(UUID.randomUUID().toString().replaceAll("-", ""));
        this.sourceId = sourceId;
        this.sequenceNo = sequenceNo;
        this.issueSelect = issueSelect;
        this.issueDetail = issueDetail;
    }
}
