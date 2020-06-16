package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ProvidentFundClose;
import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import com.authine.cloudpivot.web.api.entity.SocialSecurityClose;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.EmployeesMaintainMapper
 * @Date 2020/5/22 11:18
 **/
public interface EmployeesMaintainMapper {

    SocialSecurityDeclare getSocialSecurityDeclareById(String id);

    ProvidentFundDeclare getProvidentFundDeclareById(String id);

    SocialSecurityClose getSocialSecurityCloseById(String id);

    ProvidentFundClose getProvidentFundCloseById(String id);
    /**
     * 方法说明：同时更新增减员表单社保，公积金状态
     * @param ids
     * @param tableName
     * @param status
     * @return void
     * @author liulei
     * @Date 2020/5/22 11:21
     */
    void updateAddOrDelEmployeeStatus(@Param("ids") List <String> ids, @Param("tableName") String tableName,
                                      @Param("status") String status);

    void updateTableStatus(@Param("ids") List <String> ids, @Param("tableName") String tableName,
                           @Param("field") String field, @Param("status") String status);

    void updateStatus(String id, String tableName, String field, String status, String billYear, String returnReasonAlready);

    void insertEmployeeOrderFormDetailsFromSb(String parentId, String tableName);

    void insertEmployeeOrderFormDetailsFromGjj(String parentId, String tableName);
}
