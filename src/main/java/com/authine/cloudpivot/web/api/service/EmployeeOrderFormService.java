package com.authine.cloudpivot.web.api.service;

import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @time 2020/6/18 16:40
 */
public interface EmployeeOrderFormService {


    /**
     * 根据社保申报id批量删除社保申报中汇缴数据
     *
     * @param parentId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsRemittanceSbsByParentId(List<String> parentId);

    /**
     * 根据社保申报id删除社保申报中汇缴数据
     *
     * @param parentId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsRemittanceSbByParentId(String parentId);

    /**
     * 根据社保申报id批量删除社保申报中补缴数据
     *
     * @param parentId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsPayBackSbsByParentId(List<String> parentId);

    /**
     * 根据社保申报id删除社保申报中补缴数据
     *
     * @param parentId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsPayBackSbByParentId(String parentId);

    /**
     * 根据社保申报id批量删除员工订单中社保部分
     *
     * @param socialSecurityDeclareId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsRemittancesById(List<String> socialSecurityDeclareId);

    /**
     * 根据社保申报id删除员工订单中社保部分
     *
     * @param socialSecurityDeclareId 社保申报id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsRemittanceById(String socialSecurityDeclareId);


    /**
     * 根据员工订单id批量删除员工订单中补缴社保部分
     *
     * @param id 员工订单id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsPayBacksById(List<String> id);

    /**
     * 根据员工订单id删除员工订单中补缴社保部分
     *
     * @param id 员工订单id
     * @return 删除数量
     * @author wangyong
     */
    int deleteOrderDetailsPayBackById(String id);

    /**
     * 订单明细中是否存在社保
     *
     * @param id
     * @return
     */
    boolean isHaveSocialSecurityInOrderForm(String id);


    /**
     * 订单明细中是否存在社保
     *
     * @param idCard          身份证
     * @param firstLevelName  一级客户名称
     * @param secondLevelName 二级客户名称
     * @return 数量
     * @author wangyong
     */
    boolean isHaveSocialSecurityInOrderFormByIdCard(String idCard, String firstLevelName, String secondLevelName);

    /**
     * 订单明细中是否存在公积金
     *
     * @param id
     * @return
     */
    boolean isHaveProvidentFundInOrderForm(String id);


    /**
     * 订单明细中是否存在公积金
     *
     * @param idCard          身份证
     * @param firstLevelName  一级客户名称
     * @param secondLevelName 二级客户名称
     * @return 数量
     * @author wangyong
     */
    boolean isHaveProvidentFundInOrderFormByIdCard(String idCard, String firstLevelName, String secondLevelName);


    /**
     * 根据社保申报id获取订单id
     *
     * @param socialSecurityDeclareId 社保申报id
     * @return 订单id和社保申报id对应关系
     */
    List<Map<String, String>> getOrderFormIdBySocialSecurityDeclareId(List<String> socialSecurityDeclareId);


}
