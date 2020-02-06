package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.ColumnComment;
import com.authine.cloudpivot.web.api.service.TableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-05 11:10
 * @Description:
 */
@Component
public class ReadAddExcelFile extends ReadExcelFile {


    @Override
    protected Object conversion(String key, Object value) throws ParseException {

        Object result = value;
        if (StringUtils.isEmpty(result + "")) {
            return null;
        }
        switch (key) {
            case "service_fee":
            case "with_file":
            case "social_security_section":
            case "social_security_base":
            case "provident_fund_base":
            case "supplement_provident_fund_p":
            case "supplement_provident_fund_b":
            case "wage_trial":
            case "wage_positive":
                // 转换成double
                result = Double.parseDouble(result + "");
                break;
            case "entry_time":
            case "benefit_start_time":
            case "provident_fund_start_time":
            case "charge_start_date":
            case "dispatch_period_start_date":
            case "dispatch_deadline":
            case "start_date_trial":
            case "end_time_trial":
            case "start_date_positive":
            case "end_date_positive":
                // 转换成时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = sdf.parse(result + "");
                break;

        }
        return result;
    }

}
