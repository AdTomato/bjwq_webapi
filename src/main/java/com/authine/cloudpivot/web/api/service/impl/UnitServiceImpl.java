package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.Unit;
import com.authine.cloudpivot.web.api.mapper.UnitMapper;
import com.authine.cloudpivot.web.api.service.UnitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangyong
 * @time 2020/5/29 1:24
 */
@Service
public class UnitServiceImpl implements UnitService {

    @Resource
    UnitMapper unitMapper;

    /**
     * 根据人员id集合获取人员实体
     *
     * @param ids 人员id集合
     * @return 人员实体
     * @author wangyong
     */
    @Override
    public List<Unit> getOrgUnitByIds(List<String> ids) {
        return unitMapper.getOrgUnitByIds(ids);
    }


    /**
     * 根据部门id集合获取部门实体
     *
     * @param ids 部门id集合
     * @return 部门试题
     * @author wangyong
     */
    @Override
    public List<Unit> getDeptUnitByIds(List<String> ids) {
        return unitMapper.getDeptUnitByIds(ids);
    }


    /**
     * 根据用户id集合获取部门实体
     *
     * @param userIds 部门id集合
     * @return 部门实体
     * @author wangyong
     */
    @Override
    public List<Unit> getDeptUnitByUserIds(List<String> userIds) {
        return unitMapper.getDeptUnitByUserIds(userIds);
    }
}
