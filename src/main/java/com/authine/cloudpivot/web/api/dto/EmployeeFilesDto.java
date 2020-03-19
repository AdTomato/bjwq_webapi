package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.entity.EmployeeFiles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/18 8:45
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFilesDto extends EmployeeFiles {

    private List<EmployeeOrderFormDto> employeeOrderFormDtos;

}
