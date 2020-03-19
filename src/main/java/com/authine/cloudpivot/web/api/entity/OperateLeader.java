package com.authine.cloudpivot.web.api.entity;

import lombok.Data;

/**
 * @author liulei
 * @Description 运行负责人
 * @ClassName com.authine.cloudpivot.web.api.entity.OperateLeader
 * @Date 2020/3/18 16:23
 **/
@Data
public class OperateLeader extends BaseEntity{
    /** 地区 */
    String city;
    /** 福利办理方 */
    String welfareHandler;
    /** 社保负责人 */
    String socialSecurityLeader;
    /** 公积金负责人 */
    String providentFundLeader;
}
