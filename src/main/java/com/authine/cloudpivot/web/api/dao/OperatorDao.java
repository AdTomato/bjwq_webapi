package com.authine.cloudpivot.web.api.dao;

import java.util.Map;

/**
 * 运行人员接口
 *
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.dao.OperatorDao
 * @Date 2019/12/18 14:12
 **/
public interface OperatorDao {

    /**
     * 方法说明：根据地址获取对应的运行人员
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @Param area
     * @author liulei
     * @Date 2019/12/18 14:19
     */
    Map<String, String> getOperatorByArea(String area) throws Exception;
}
