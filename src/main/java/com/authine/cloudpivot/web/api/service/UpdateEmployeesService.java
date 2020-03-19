package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.*;

/**
 * @author liulei
 * @Description 增减员变更 Service
 * @ClassName com.authine.cloudpivot.web.api.service.UpdateEmployeesService
 * @Date 2020/3/13 16:27
 **/
public interface UpdateEmployeesService {

    /**
     * 方法说明：获取增员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    AddEmployee getAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取增员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShAddEmployee getShAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取增员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgAddEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_客户修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    DeleteEmployee getDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_上海修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    ShDeleteEmployee getShDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取减员_全国修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    NationwideDispatch getQgDeleteEmployeeUpdateById(String id) throws Exception;

    /**
     * 方法说明：获取员工档案修改表单数据
     * @param id  主键id
     * @return com.authine.cloudpivot.web.api.entity.AddEmployee
     * @author liulei
     * @Date 2020/3/13 16:48
     */
    EmployeeFiles getEmployeeFilesUpdateById(String id) throws Exception;
}
