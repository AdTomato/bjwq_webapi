package com.authine.cloudpivot.web.api.service;


import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;

import java.util.List;

/**
 * @Author: weiyao
 * @Date: 2020-04-21
 * @Description: 公积金申报和社保申报(导出功能)
 */
public interface ProvidentAndSocialDeclareService {

    //查询公积金申报-根据福利办理方
    List<ProvidentFundDeclare> getProvidentFundDeclare(String welfareHandler);

    //查询社保申报-根据福利办理方
    List<SocialSecurityDeclare> getSocialSecurityDeclare(String welfareHandler);
}
