package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 福利地设置
 * @ClassName com.authine.cloudpivot.web.api.entity.WelfareSet
 * @Date 2020/5/6 14:43
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WelfareSet {
    String province;
    String city;
}
