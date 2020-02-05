package com.authine.cloudpivot.web.api.service;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.ProductService
 * @Date 2020/1/16 15:48
 **/
public interface ProductService {
    /**
     * 方法说明：修改征缴产品时更新对应的征缴政策里的数据
     *
     * @return void
     * @throws
     * @Param id 征缴产品id
     * @author liulei
     * @Date 2020/1/16 15:52
     */
    void updateProduct(String id) throws Exception;
}
