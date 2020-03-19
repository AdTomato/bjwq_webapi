package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liulei
 * @Description 委派单实体
 * @ClassName com.authine.cloudpivot.web.api.entity.AppointmentSheet
 * @Date 2020/3/11 13:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentSheet extends BaseEntity {
    /** 客户名称 */
    String clientName;
    /** 地区 */
    String city;
    /** 社保福利办理方 */
    String sWelfareHandler;
    /** 公积金福利办理方 */
    String gWelfareHandler;
}
