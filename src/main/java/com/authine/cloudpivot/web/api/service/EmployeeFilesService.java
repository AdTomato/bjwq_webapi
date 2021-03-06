package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.dto.AddEmployeeDto;
import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.sun.istack.NotNull;

import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-17 09:54
 * @Description: 员工档案service接口
 */
public interface EmployeeFilesService {

    /**
     * 根据员工身份证（必填）和客户名称（非必填，可为空）获取员工档案
     *
     * @param idNo       身份证（必填）
     * @param clientName 客户名称（非必填，可为空）
     * @return 员工档案实体
     */
    public EmployeeFiles getEmployeeFilesByIdNoOrClientName(@NotNull String idNo, String clientName);

    /**
     * 获取增员_客户数据
     *
     * @param id 数据id
     * @return 增员_客户数据
     */
    AddEmployee getAddEmployeeData(String id);

    /**
     * 根据增员客户数据id获取增员客户数据
     *
     * @param id 数据id
     * @return 增员客户数据
     * @author wangyong
     */
    AddEmployeeDto getAddEmployeeDtoData(String id);

    /**
     * 获取增员_客户修改数据
     *
     * @param id 数据id
     * @return 增员_客户修改数据
     */
    AddEmployee getAddEmployeeUpdateData(String id);

    /**
     * 获取减员_客户数据
     *
     * @param id 数据id
     * @return 减员_客户数据
     */
    DeleteEmployee getDeleteEmployeeData(String id);

    /**
     * 获取减员_客户修改数据
     *
     * @param id 数据id
     * @return 减员_客户修改数据
     */
    DeleteEmployee getDeleteEmployeeUpdateData(String id);

    /**
     * 获取增员_上海数据
     *
     * @param id 数据id
     * @return 增员_上海数据
     */
    ShAddEmployee getShAddEmployeeData(String id);

    /**
     * 获取增员_上海数据
     *
     * @param id 数据id
     * @return 增员_上海数据
     */
    ShAddEmployee getShAddEmployeeUpdateData(String id);

    /**
     * 获取减员_上海数据
     *
     * @param id 数据id
     * @return 减员_上海数据
     */
    ShDeleteEmployee getShDeleteEmployeeData(String id);

    /**
     * 获取减员_上海修改数据
     *
     * @param id 数据id
     * @return 减员_上海数据
     */
    ShDeleteEmployee getShDeleteEmployeeUpdateData(String id);

    /**
     * 获取增员_全国数据
     *
     * @param id 数据id
     * @return 增员_全国数据
     */
    NationwideDispatch getNationwideAddEmployeeData(String id);

    /**
     * 获取增员_全国修改数据
     *
     * @param id 数据id
     * @return 增员_全国数据
     */
    NationwideDispatch getNationwideAddEmployeeUpdateData(String id);

    /**
     * 获取减员_上海数据
     *
     * @param id 数据id
     * @return 减员_上海数据
     */
    NationwideDispatch getNationwideDeleteEmployeeData(String id);

    /**
     * 获取减员_全国数据
     *
     * @param id 数据id
     * @return 减员_全国数据
     */
    NationwideDispatch getNationwideDeleteEmployeeUpdateData(String id);

    /**
     * 获取员工档案数据
     *
     * @param id 数据id
     * @return 员工档案数据
     */
    EmployeeFiles getEmployeeFilesData(String id);

    /**
     * 获取员工档案修改数据
     *
     * @param id 数据id
     * @return 员工档案修改数据
     */
    EmployeeFiles getEmployeeFilesUpdateData(String id);

    void addEmployeeUpdateDetail(List<ChangeValue> data);

    void shAddEmployeeUpdateDetail(List<ChangeValue> data);

    void nationAddUpdateDetail(List<ChangeValue> data);

    void delEmployeeUpdateDetail(List<ChangeValue> data);

    void shDelEmployeeUpdateDetail(List<ChangeValue> data);

    void nationDelUpdateDetail(List<ChangeValue> data);

    void employeeFilesUpdateDetail(List<ChangeValue> data);

    /**
     * 根据客户名称以及员工性质获取员工档案数据
     *
     * @param clientName     客户名称
     * @param employeeNature 员工性质
     * @return 员工档案
     */
    List<EmployeeFilesDto> getEmployeeFilesCanGenerateBillByClientName(String clientName, String employeeNature);

    /**
     * 根据员工档案id获取不是历史账单的订单信息
     *
     * @param id 员工档案id
     * @return 订单信息
     */
    List<EmployeeOrderFormDto> getEmployeeOrderFormByEmployeeFilesId(String id);

    /**
     * 根据员工订单id获取员工
     *
     * @param id
     * @return
     */
    List<SocialSecurityFundDetail> getSocialSecurityFundDetailByParentId(String id);

    /**
     * 根据员工的证件号码获取之前没有对比的账单
     *
     * @param idNo
     * @return
     */
    List<Bill> getNoCompareBills(String idNo);

    /**
     * 根据账单年月，证件号码获取薪资收费账单
     *
     * @param billYear: 账单年月
     * @param idNo:     证件号码
     * @return 薪资收费账单
     */
    PayrollBill getPayrollBill(String billYear, String idNo);

    /**
     * 更新员工档案
     *
     * @param employeeFilesDto
     */
    void updateEmployee(List<EmployeeFilesDto> employeeFilesDto);

    /**
     * 创建账单明细
     *
     * @param bills
     */
    void insertBills(List<Bill> bills);

    /**
     * 方法说明：根据证件号码查询未离职的最新一个员工档案数据
     *
     * @param identityNo
     * @return com.authine.cloudpivot.web.api.entity.EmployeeFiles
     * @author liulei
     * @Date 2020/5/6 14:12
     */
    EmployeeFiles getEmployeeFilesByIdentityNo(String identityNo) throws Exception;
}
