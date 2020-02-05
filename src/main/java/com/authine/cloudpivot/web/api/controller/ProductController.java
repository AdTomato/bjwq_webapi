package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.ProductService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.controller.ProductController
 * @Date 2020/1/16 15:45
 **/
@RestController
@RequestMapping("/controller/product")
@Slf4j
public class ProductController extends BaseController {

    @Resource
    private ProductService productService;

    /**
     * 方法说明：修改征缴产品时更新对应的征缴政策里的数据
     *
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @Param id 征缴产品id
     * @author liulei
     * @Date 2020/1/16 15:51
     */
    @GetMapping("/updateProduct")
    @ResponseBody
    public ResponseResult<String> updateProduct(String id) {
        if (StringUtils.isBlank(id)) {
            log.info("没有获取到id,更新征缴政策查询数据出错!");
            return this.getOkResponseResult("error", "没有获取到id,更新征缴政策查询数据出错!");
        }
        try {
            productService.updateProduct(id);
            log.info("更新征缴政策查询数据成功!");
            return this.getOkResponseResult("success", "更新征缴政策查询数据成功!");
        } catch (Exception e) {
            log.info("重置密码出错:" + e.getMessage());
            return this.getOkResponseResult("error", "更新征缴政策查询数据出错!");
        }
    }
}
