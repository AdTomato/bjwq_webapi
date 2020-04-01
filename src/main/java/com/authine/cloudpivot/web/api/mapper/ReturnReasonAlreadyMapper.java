package com.authine.cloudpivot.web.api.mapper;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 已有退回原因
 * @ClassName com.authine.cloudpivot.web.api.mapper.ReturnReasonAlreadyMapper
 * @Date 2020/3/27 13:47
 **/
public interface ReturnReasonAlreadyMapper {
    /**
     * 方法说明：新增已有退回原因临时表
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/27 13:48
     */
    void insertReturnReasonAlready(List<Map<String, Object>> list);

    void updateReturnReasonAlready(String tableName, String sourceId);

    List <Map <String, Object>> getWorkItemInfo(String tableName, String sourceId);

    void updateEmployeeOrderFormStatus(@Param("ids") List<String> ids, @Param("field") String field,
                                       @Param("status") String status);

    void updateDeclareStatus(@Param("sequenceNos") List<String> sequenceNos, @Param("tableName") String tableName,
                             @Param("status") String status);

    void deleteTempDataBySourceId(String sourceId);
}
