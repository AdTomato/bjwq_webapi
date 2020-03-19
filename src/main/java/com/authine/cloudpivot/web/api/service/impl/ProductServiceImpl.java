package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.dao.ProductDao;
import com.authine.cloudpivot.web.api.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.ProductServiceImpl
 * @Date 2020/1/16 15:49
 **/
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductDao productDao;

    /**
     * 方法说明：修改征缴产品时更新对应的征缴政策里的数据
     * @Param id 征缴产品id
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/16 15:52
     */
    @Override
    public void updateProduct(String id) throws Exception {
        productDao.updateProduct(id);
    }
}
