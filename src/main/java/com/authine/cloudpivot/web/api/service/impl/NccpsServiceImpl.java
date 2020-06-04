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


    /**
     * 根据一级客户名称、二级客户名称、城市、福利办理方获取客户征缴个性化里面的公积金比例
     *
     * @param firstClientName  一级客户名称
     * @param secondClientName 二级客户名称
     * @param city             城市
     * @param welfareHandler   福利办理方
     * @return 公积金比例
     * @author wangyong
     */
    public List<NccpsProvidentFundRatio> getNccpsProvidentFoundRatioByFirstOrSecondClientName(String firstClientName, String secondClientName, String city, String welfareHandler) {
        return nccpsMapper.getNccpsProvidentFoundRatioByFirstOrSecondClientName(firstClientName, secondClientName, city, welfareHandler);
    }

}
