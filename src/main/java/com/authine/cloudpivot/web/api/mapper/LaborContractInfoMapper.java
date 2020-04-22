package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.LaborContractInfo;

/**
 * @author liulei
 * @Description 劳动合同mapper
 * @ClassName com.authine.cloudpivot.web.api.mapper.LaborContractInfoMapper
 * @Date 2020/4/21 10:56
 **/
public interface LaborContractInfoMapper {
    /**
     * 方法说明：保存劳动合同
     * @param laborContractInfo 劳动合同
     * @return void
     * @author liulei
     * @Date 2020/4/21 10:59
     */
    void saveLaborContractInfo(LaborContractInfo laborContractInfo);
}
