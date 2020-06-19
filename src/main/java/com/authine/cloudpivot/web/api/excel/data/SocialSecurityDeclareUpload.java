package com.authine.cloudpivot.web.api.excel.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author wangyong
 * @time 2020/6/16 15:58
 */
@Data
public class SocialSecurityDeclareUpload {

    @ExcelProperty(index = 0)
    private String id;

    @ExcelProperty(index = 1)
    private String identityNo;

    @ExcelProperty(index = 2)
    private String firstLevelClientName;

    @ExcelProperty(index = 3)
    private String secondLevelClientName;



}
