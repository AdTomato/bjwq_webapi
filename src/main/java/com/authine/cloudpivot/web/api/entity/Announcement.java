package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:wangyong
 * @Date:2020/4/16 9:41
 * @Description: 公告
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    private String announcementContent;
    private Integer isClose;

}
