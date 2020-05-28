package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @Description 获取更新表数据
 * @ClassName com.authine.cloudpivot.web.api.mapper.UpdateEmployeesMapper
 * @Date 2020/3/13 16:15
 **/
public interface UpdateEmployeesMapper {

    /**
     * 方法说明：获取增员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    AddEmployee getAddEmployeeUpdateById(String id);

    /**
     * 方法说明：获取增员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShAddEmployee getShAddEmployeeUpdateById(String id);

    /**
     * 方法说明：获取增员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgAddEmployeeUpdateById(String id);

    /**
     * 方法说明：获取减员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    DeleteEmployee getDeleteEmployeeUpdateById(String id);

    /**
     * 方法说明：获取减员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShDeleteEmployee getShDeleteEmployeeUpdateById(String id);

    /**
     * 方法说明：获取减员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgDeleteEmployeeUpdateById(String id);

    /**
     * 方法说明：获取员工档案修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    EmployeeFiles getEmployeeFilesUpdateById(String id);

    EmployeeFiles getEmployeeFilesById(String id);

    void delEmployeeOrderFormDetails(String parentId, String type, String tableName);

    List <Integer> getStartChargeTime(String tableName, String parentId);

    List<Integer> getEndChargeTime(String tableName, String parentId);

    Double getMaxSortKey(String tableName, String parentId);

    void addEmployeeOrderFormDetails(List <EmployeeOrderFormDetails> list, String tableName);

    Double getSumInTimeRange(String tableName, String startChargeTime, String endChargeTime, String parentId);

    void updateSocialSecurityDeclare(SocialSecurityDeclare declare);

    void updateProvidentFundDeclare(ProvidentFundDeclare declare);

    void updateDeleteEmployee(DeleteEmployee del);

    void updateShDeleteEmployee(ShDeleteEmployee del);

    void updateQgDeleteEmployee(NationwideDispatch del);

    void updateAddEmployee(AddEmployee add);

    void updateShAddEmployee(ShAddEmployee add);

    void updateQgAddEmployee(NationwideDispatch add);

    void updateSocialSecurityClose(SocialSecurityClose close);

    void updateProvidentFundClose(ProvidentFundClose close);
}
