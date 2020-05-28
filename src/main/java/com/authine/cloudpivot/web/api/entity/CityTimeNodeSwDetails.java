package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.CityTimeNodeSwDetails
 * @Date 2020/5/9 17:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityTimeNodeSwDetails {
    String	province;
    String	city;
    String	supplierName;
    Integer	sTimeNode;
    Integer	gTimeNode;
    String	id;
    String	parentId;
    Double	sortKey;
}
