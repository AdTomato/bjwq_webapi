package com.authine.cloudpivot.web.api.controller;

import cn.hutool.poi.excel.ExcelUtil;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.controller.ImportController
 * @Date 2019/12/12 14:44
 **/
@Controller
@RequestMapping("/controller/import")
@Slf4j
public class ImportController {

    @PostMapping("/importSocialSecurityCardsPersonnelInfo")
    @ResponseBody
    @CustomizedOrigin(level = 1)
    public String importSocialSecurityCardsPersonnelInfo(String filePath) throws Exception {
        // 测试使用
        filePath = "F:\\import\\1111.xlsx";
        List<String[]> fileList = ExcelUtils.readFile(filePath, 0, 0, 5);
        return "";
    }


}
