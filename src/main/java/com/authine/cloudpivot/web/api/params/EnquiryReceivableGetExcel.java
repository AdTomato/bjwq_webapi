package com.authine.cloudpivot.web.api.params;

import lombok.Data;

import java.util.List;

/**
 * @author: wangyong
 * @time: 2020/4/24 14:13
 * @Description: 账单查询导出excel表格的接口
 */
@Data
public class EnquiryReceivableGetExcel {

    List<String> ids;

}
