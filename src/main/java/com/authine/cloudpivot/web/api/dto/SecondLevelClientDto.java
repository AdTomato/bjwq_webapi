package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.SecondContactInfo;
import com.authine.cloudpivot.web.api.entity.SecondLevelClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级客户扩展类
 *
 * @author wangyong
 * @time 2020/5/21 13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondLevelClientDto extends SecondLevelClient {

    private List<SecondContactInfo> secondContactInfoList;

}
