package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.AddEmployee;
import com.authine.cloudpivot.web.api.entity.Unit;
import lombok.Data;

import java.util.List;

/**
 * @author wangyong
 * @time 2020/6/11 13:57
 */
@Data
public class AddEmployeeDto extends AddEmployee {

    /**
     * 操作人
     */
    List<Unit> operatorList;
    /**
     * 查询人
     */
    List<Unit> inquirerList;


    /**
     * 所属部门
     */
    List<Unit> subordinateDepartmentList;

}
