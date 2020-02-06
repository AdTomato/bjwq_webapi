package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.web.api.entity.ColumnComment;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: wangyong
 * @Date: 2020-02-06 13:37
 * @Description: 读取全国派单导入文件
 */
@Component
public class NationalDeliveryReadExcelFile extends ReadExcelFile {


    @Override
    protected Object conversion(String key, Object value) throws ParseException {
        return null;
    }


}
