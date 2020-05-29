package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.BaseEntity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: wangyong
 * @Date: 2020-02-25 15:16
 * @Description: 系统字段数据设置工具类
 */
public class SystemDataSetUtils {

    public static void dataSet(UserModel user, DepartmentModel department, String name, String sequenceStatus, Map<String, Object> data) {
        data.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
        data.put("name", name);
        data.put("creater", user.getId());
        data.put("createdDeptId", user.getDepartmentId());
        data.put("owner", user.getId());
        data.put("ownerDeptId", user.getDepartmentId());
        data.put("createdTime", new Date());
        data.put("modifier", user.getId());
        data.put("modifiedTime", new Date());
        data.put("sequenceStatus", sequenceStatus);
        data.put("ownerDeptQueryCode", department.getQueryCode());
    }

    public static void dataSet(UserModel user, DepartmentModel department, String name, String sequenceStatus, BaseEntity baseEntity) {
        baseEntity.setId(UUID.randomUUID().toString().replace("-", ""));
        baseEntity.setName(name);
        baseEntity.setCreater(user.getId());
        baseEntity.setCreatedDeptId(user.getDepartmentId());
        baseEntity.setOwner(user.getId());
        baseEntity.setOwnerDeptId(user.getDepartmentId());
        baseEntity.setCreatedTime(new Date());
        baseEntity.setModifier(user.getId());
        baseEntity.setModifiedTime(new Date());
        baseEntity.setSequenceStatus(sequenceStatus);
        baseEntity.setSequenceNo(null);
        baseEntity.setOwnerDeptQueryCode(department.getQueryCode());
    }

    public static boolean isCanShowAllData(String schemaCode) {
        boolean result = false;
        switch (schemaCode) {
            case "identityNo_type":
            case "welfare_set":
                result = true;
                break;
        }
        return result;
    }

}
