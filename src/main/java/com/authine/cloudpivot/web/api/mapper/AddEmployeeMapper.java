package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: 刘磊
 * @Date: 2020-03-13 10:07
 * @Description: 增员修改使用接口
 */
@Mapper
public interface AddEmployeeMapper {

    /**
     * 方法说明：根据客户名称，证件号码获取社保申报实体
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @return void
     * @author liulei
     * @Date 2020/3/13 10:10
     */
    SocialSecurityDeclare getSocialSecurityDeclareByClientNameAndIdentityNo(String clientName, String identityNo);

    /**
     * 方法说明：根据客户名称，证件号码获取公积金申报实体
     * @param clientName 客户名称
     * @param identityNo 证件号码
     * @return void
     * @author liulei
     * @Date 2020/3/13 10:10
     */
    ProvidentFundDeclare getProvidentFundDeclareByClientNameAndIdentityNo(String clientName, String identityNo);

    /**
     * 方法说明：根据id获取员工订单实体
     * @param id 主键id
     * @return com.authine.cloudpivot.web.api.entity.EmployeeOrderForm
     * @author liulei
     * @Date 2020/3/13 10:16
     */
    EmployeeOrderForm getEmployeeOrderFormById(String id);

    void updateEmployeeFiles(EmployeeFiles employeeFiles);

    void updateSocialSecurityDeclare(SocialSecurityDeclare socialSecurityDeclare);

    void updateProvidentFundDeclare(ProvidentFundDeclare providentFundDeclare);

    void updateOrderFormStatusToPrePoint(@Param("ids") String[] ids, @Param("field") String field);

    void updateDeclareOrCloseStatus(@Param("ids") String[] ids, @Param("declareTableName") String declareTableName,
                                    @Param("status") String status);

    List<EmployeeOrderForm> getEmployeeOrderFormByEmployeeFilesId(String id);

    List<SocialSecurityDeclare> getSocialSecurityDeclareByOrderFormId(String id);

    List<ProvidentFundDeclare> getProvidentFundDeclareByOrderFormId(String id);

    void updateEmployeeOrderFromTime(String id);

    List<SocialSecurityClose> getSocialSecurityCloseByOrderFormId(String id);

    List<ProvidentFundClose> getProvidentFundCloseByOrderFormId(String id);

    void updateSocialSecurityClose(SocialSecurityClose socialSecurityClose);

    void updateProvidentFundClose(ProvidentFundClose providentFundClose);

    void addEmployeeImportData(List<Map<String, Object>> list);

    void deleteEmployeeImportData(List<Map<String, Object>> list);

    void updateAddEmployeeOwner(List<Map<String, Object>> list);

    void updateDeleteEmployeeOwner(List<Map<String, Object>> list);
}
