package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.Unit;

import java.util.List;

/**
 * 组织机构
 *
 * @author wangyong
 * @time 2020/5/29 1:19
 */
public interface UnitMapper {

    /**
     * 根据人员id集合获取人员实体
     *
     * @param ids 人员id集合
     * @return 人员实体
     * @author wangyong
     */
    List<Unit> getOrgUnitByIds(List<String> ids);

    /**
     * 根据部门id集合获取部门实体
     *
     * @param ids 部门id集合
     * @return 部门试题
     * @author wangyong
     */
    List<Unit> getDeptUnitByIds(List<String> ids);

}
