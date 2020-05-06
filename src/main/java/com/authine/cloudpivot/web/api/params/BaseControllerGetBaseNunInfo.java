package com.authine.cloudpivot.web.api.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/24 13:39
 * @Description: 基数采集参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseControllerGetBaseNunInfo {

    private String fileName;
    private String bizObjectId;
    private String welfare_operator;

}
