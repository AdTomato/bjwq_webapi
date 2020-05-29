package com.authine.cloudpivot.web.api.params;

import com.authine.cloudpivot.web.api.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 增员客户提交校验返回值
 *
 * @author wangyong
 * @time 2020/5/27 15:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeCheckReturn {

    /**
     * 一级客户名称
     */
    private String firstLevelClientName;
    /**
     * 二级客户名称
     */
    private String secondLevelClientName;

    /**
     * 查询人
     */
    private List<Unit> look;

    /**
     * 查询人str
     */
    private String lookStr;

    /**
     * 操作人
     */
    private List<Unit> edit;

    /**
     * 操作人str
     */
    private String editStr;

    /**
     * 所属部门
     */
    private List<Unit> dept;

    /**
     * 所属部门str
     */
    private String deptStr;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 性别
     */
    private String gender;

    /**
     * 是否能够提交
     */
    private Boolean isCanSubmit;

    /**
     * 不能提交的理由
     */
    private String message;

}
