package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.ColumnComment;
import com.authine.cloudpivot.web.api.mapper.TableMapper;
import com.authine.cloudpivot.web.api.service.TableService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:11
 * @Description:
 */
@Component
public class ReadDeleteExcelFile extends ReadExcelFile {


    @Override
    protected Object conversion(String key, Object value) throws ParseException {
        Object result = value;
        if (StringUtils.isEmpty(value + "")) {
            return result;
        }
        switch (key) {
            case "OS_initiated_departure_time":
            case "departure_time":
            case "charge_end_time":
                // 转换成时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.parse(result + "");
                break;
        }
        return result;
    }

}
