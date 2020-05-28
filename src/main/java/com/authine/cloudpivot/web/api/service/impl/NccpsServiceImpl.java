package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.Nccps;
import com.authine.cloudpivot.web.api.entity.NccpsOrderCutOffTime;
import com.authine.cloudpivot.web.api.entity.NccpsProvidentFundRatio;
import com.authine.cloudpivot.web.api.entity.NccpsWorkInjuryRatio;
import com.authine.cloudpivot.web.api.mapper.NccpsMapper;
import com.authine.cloudpivot.web.api.service.NccpsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.NccpsServiceImpl
 * @Date 2020/5/11 13:37
 **/
@Service
public class NccpsServiceImpl implements NccpsService {
    @Resource
    private NccpsMapper nccpsMapper;

    @Override
    public Nccps getNccps(String firstLevelClientName, String secondLevelClientName, String sCity,
                                      String sWelfareHandler, String gCity, String gWelfareHandler) throws Exception {
        Nccps nccps = nccpsMapper.getNccpsByClientName(firstLevelClientName, secondLevelClientName);
        if (nccps == null) {
            return null;
        }
        // 城市时间节点数据
        List <NccpsOrderCutOffTime> nccpsOrderCutOffTimes = new ArrayList <>();
        // 公积金比例
        List <NccpsProvidentFundRatio> nccpsProvidentFundRatios = new ArrayList <>();
        // 工伤比例
        List <NccpsWorkInjuryRatio> nccpsWorkInjuryRatios = new ArrayList <>();
        if (StringUtils.isNotBlank(sCity)) {
            // 查询社保城市时间节点数据
            NccpsOrderCutOffTime sTime =
                    nccpsMapper.getNccpsOrderCutOffTimeByParentIdAndCity(nccps.getId(), sCity, sWelfareHandler);
            if (sTime != null) {
                nccpsOrderCutOffTimes.add(sTime);
            }
            // 查询工伤信息
            NccpsWorkInjuryRatio nccpsWorkInjuryRatio =
                    nccpsMapper.getNccpsWorkInjuryRatioByParentIdAndCity(nccps.getId(), sCity, sWelfareHandler);
            if (nccpsWorkInjuryRatio != null) {
                nccpsWorkInjuryRatios.add(nccpsWorkInjuryRatio);
            }
        }
        if (StringUtils.isNotBlank(gCity)) {
            if (!gCity.equals(sCity) || !gWelfareHandler.equals(sWelfareHandler)) {
                // 社保城市和公积金城市不一致
                NccpsOrderCutOffTime gTime =
                        nccpsMapper.getNccpsOrderCutOffTimeByParentIdAndCity(nccps.getId(), gCity, gWelfareHandler);
                if (gTime != null) {
                    nccpsOrderCutOffTimes.add(gTime);
                }
            }
            // 查询公积金比例
            nccpsProvidentFundRatios = nccpsMapper.getNccpsProvidentFundRatioByParentIdAndCity(nccps.getId(), gCity,
                    gWelfareHandler);
        }

        nccps.setNccpsOrderCutOffTimes(nccpsOrderCutOffTimes);
        nccps.setNccpsProvidentFundRatios(nccpsProvidentFundRatios);
        nccps.setNccpsWorkInjuryRatios(nccpsWorkInjuryRatios);

        return nccps;
    }
}
