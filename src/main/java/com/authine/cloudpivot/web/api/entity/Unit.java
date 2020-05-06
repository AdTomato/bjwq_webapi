package com.authine.cloudpivot.web.api.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 15:44
 * @Description: User和Department实体类
 */
@Data
@Component
public class Unit {
    /**
     * id值
     */
    String id;

    /**
     * 类型
     */
    Integer type;
}
