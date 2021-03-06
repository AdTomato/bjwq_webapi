package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.entity.LaborContractInfo;

import java.util.List;

/**
 * @author liulei
 * @Description 劳动合同Service
 * @ClassName com.authine.cloudpivot.web.api.service.LaborContractInfoService
 * @Date 2020/4/21 10:55
 **/
public interface LaborContractInfoService {
    /**
     * 方法说明：保存劳动合同
     * @param laborContractInfos 劳动合同
     * @return void
     * @author liulei
     * @Date 2020/4/21 10:58
     */
    void saveLaborContractInfos(List <LaborContractInfo> laborContractInfos) throws Exception;
}
