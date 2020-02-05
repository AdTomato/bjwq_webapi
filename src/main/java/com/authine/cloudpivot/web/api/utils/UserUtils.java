package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.OrganizationFacade;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import org.thymeleaf.util.StringUtils;

/**
 * @Author: wangyong
 * @Date: 2020-02-04 08:53
 * @Description:
 */
public class UserUtils {

    private static final String ADMIN_ID = "2c9280a26706a73a016706a93ccf002b";

    /**
     * @Author: wangyong
     * @Date: 2020/2/5 10:51
     * @param userId :
     * @return : java.lang.String
     * @Description: 确保userId不为空，如果userId为空，则将管理员id返回
     */
    public static String getUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return ADMIN_ID;
        } else {
            return userId;
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/2/5 10:50
     * @param userId :
     * @param organizationFacade :
     * @return : com.authine.cloudpivot.engine.api.model.organization.UserModel
     * @Description: 根据userId获取UserModel
     */
    private static UserModel getUserModel(String userId, OrganizationFacade organizationFacade) {

        return organizationFacade.getUser(getUserId(userId));
    }

}
