package com.authine.cloudpivot.web.api.service.impl;


import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;
import com.authine.cloudpivot.web.api.mapper.ProvidentAndSocialDeclareMapper;
import com.authine.cloudpivot.web.api.service.ProvidentAndSocialDeclareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weiyao
 * @description: 北京外企公积金和社保 导出功能数据查询
 * @time: 2020-04-21
 */
@Service
@Slf4j  //日志打印
public class ProvidentAndSocialDeclareServiceImpl implements ProvidentAndSocialDeclareService {
    @Resource
    ProvidentAndSocialDeclareMapper providentAndSocialDeclareMapper;

    @Override
    public List<ProvidentFundDeclare> getProvidentFundDeclare(String welfareHandler) {
        return providentAndSocialDeclareMapper.getProvidentFundDeclare(StringUtils.trimToEmpty(welfareHandler));
    }

    @Override
    public List<SocialSecurityDeclare> getSocialSecurityDeclare(String welfareHandler) {
        return providentAndSocialDeclareMapper.getSocialSecurityDeclare(StringUtils.trimToEmpty(welfareHandler));
    }
}
