package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.ProvidentFundDeclare;
import com.authine.cloudpivot.web.api.entity.SocialSecurityDeclare;

import java.util.List;

//公积金申报和社保申报(导出功能)
public interface ProvidentAndSocialDeclareMapper {

    //查询公积金申报-根据福利办理方
    List<ProvidentFundDeclare> getProvidentFundDeclare(String welfareHandler);

    //查询社保申报-根据福利办理方
    List<SocialSecurityDeclare> getSocialSecurityDeclare(String welfareHandler);
}
