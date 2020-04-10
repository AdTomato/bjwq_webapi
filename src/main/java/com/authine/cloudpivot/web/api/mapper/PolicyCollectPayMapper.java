package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.PolicyCollectPay;
import com.authine.cloudpivot.web.api.entity.ProductBaseNum;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.mapper.PolicyCollectPayMapper
 * @Date 2020/4/2 15:39
 **/
public interface PolicyCollectPayMapper {

    List <PolicyCollectPay> getPolicyCollectPaysByCity(String city);

    List <ProductBaseNum> getProductBaseNumsByParentId(String id);
}
