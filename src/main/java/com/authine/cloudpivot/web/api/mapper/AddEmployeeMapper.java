package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;
import io.lettuce.core.dynamic.annotation.Param;
import org.mapstruct.Mapper;

import java.util.List;

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

    void updateOrderFormStatusToPrePoint(@Param("ids")String[] ids, @Param("field")String field);
}