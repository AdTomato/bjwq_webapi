package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.LaborContractInfo;
import com.authine.cloudpivot.web.api.mapper.LaborContractInfoMapper;
import com.authine.cloudpivot.web.api.service.LaborContractInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    public void saveLaborContractInfos(List <LaborContractInfo> laborContractInfos) throws Exception {
        int insertNum = 0;
        List<LaborContractInfo> adds = new ArrayList <>();
        for (LaborContractInfo laborContractInfo : laborContractInfos) {
            insertNum++;
            adds.add(laborContractInfo);
            if (insertNum == Constants.MAX_INSERT_NUM) {
                insertNum = 0;
                laborContractInfoMapper.saveLaborContractInfos(adds);
                adds.clear();
            }
        }
        if (0 != adds.size()) {
            laborContractInfoMapper.saveLaborContractInfos(adds);
        }
    }
}
