package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.LaborContractInfo;
import com.authine.cloudpivot.web.api.mapper.LaborContractInfoMapper;
import com.authine.cloudpivot.web.api.service.LaborContractInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 劳动合同ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.LaborContractInfoServiceImpl
 * @Date 2020/4/21 10:55
 **/
@Service
public class LaborContractInfoServiceImpl implements LaborContractInfoService {

    @Resource
    private LaborContractInfoMapper laborContractInfoMapper;

    @Override
    public void saveLaborContractInfo(LaborContractInfo laborContractInfo) throws Exception {
        laborContractInfoMapper.saveLaborContractInfo(laborContractInfo);
    }
}
