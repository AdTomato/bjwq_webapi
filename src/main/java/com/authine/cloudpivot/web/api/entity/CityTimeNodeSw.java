package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CityTimeNodeSw
 * @Date 2020/5/9 17:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityTimeNodeSw extends BaseEntity{
    String businessYear;
    List <CityTimeNodeSwDetails> cityTimeNodeSwDetails;
}
