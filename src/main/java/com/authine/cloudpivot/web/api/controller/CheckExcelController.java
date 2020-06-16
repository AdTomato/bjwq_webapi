package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.Attachment;
import com.authine.cloudpivot.web.api.params.BaseControllerGetBaseNunInfo;
import com.authine.cloudpivot.web.api.service.AttachmentService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.utils.ParseExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName CheckExcelController
 * @Author:lfh
 * @Date:2020/4/15 10:02
 * @Description: 校验Excel空值控制类
 **/
@RestController
@RequestMapping("/controller/checkExcel")
@Slf4j
public class CheckExcelController extends BaseController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/checkNull")
    public ResponseResult<Object> checkNull(@RequestBody BaseControllerGetBaseNunInfo baseControllerGetBaseNunInfo) throws IOException {
        log.info("前台传入的参数为：" + baseControllerGetBaseNunInfo);
        log.info("id:" + baseControllerGetBaseNunInfo.getBizObjectId());
        log.info("文件名为" + baseControllerGetBaseNunInfo.getFileName());
        log.info("福利办理方为：" + baseControllerGetBaseNunInfo.getWelfare_operator());
        String bizObjectId = baseControllerGetBaseNunInfo.getBizObjectId();
        if (baseControllerGetBaseNunInfo == null || StringUtils.isBlank(baseControllerGetBaseNunInfo.getFileName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        String fileName = baseControllerGetBaseNunInfo.getFileName();
        log.info("fileName：" + fileName);
        // String fileName = "运行发布基数采集模板-社保.xlsx";
        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        String filePath = "D:\\upload\\";
        String realPath = new StringBuilder().append(filePath).append(fileName).toString();
        log.info("realPath:" + realPath);
        File file = new File(realPath);
        if (!file.exists()) {
            return this.getOkResponseResult("error", file.getName() + "不存在");
        }
        FileInputStream fis = new FileInputStream(file);
        Workbook workBook = ParseExcelUtils.getWorkBook(fis, fileName);
        fis.close();
        Sheet sheet = workBook.getSheetAt(0);
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (!checkIsNumber(row.getCell(7)) || !checkIsNumber(row.getCell(8))) {
                return this.getOkResponseResult("error", "原基数或者新基数为空或者为非数字");
            }
//            if (row.getCell(7).getStringCellValue() == null || row.getCell(8).getStringCellValue() == null) {
//                return this.getOkResponseResult("error", "原基数和新基数不允许存在空值");
//            }
//            if (row.getCell(7).getCellType() != CellType.NUMERIC || row.getCell(8).getCellType() != CellType.NUMERIC) {
//                return this.getOkResponseResult("error", "原基数和新基数填写存在非数字类型");
//            }
//            if (row.getCell(7).getCellType() == CellType.NUMERIC || row.getCell(8).getCellType() == CellType.NUMERIC) {
//                if (HSSFDateUtil.isCellDateFormatted(row.getCell(7)) || HSSFDateUtil.isCellDateFormatted(row.getCell(8))) {
//                    return this.getOkResponseResult("error", "原基数或新基数存在时间类型");
//                }
//            }
        }
        FileInputStream is = new FileInputStream(file);
        List<String> headName = ParseExcelUtils.getHeadName(realPath, is);
        if (headName == null || headName.isEmpty()) {
            return this.getOkResponseResult("error", "未解析到表头数据");
        }
        String[] headNameArray = new String[headName.size()];
        headName.toArray(headNameArray);
        Arrays.sort(headNameArray);
        log.info("headNameArray:" + headNameArray);
        if (headNameArray.length == 10) {
            String[] baseArry = {"序号", "委托方", "客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)", "新基数", "备注"};
            Arrays.sort(baseArry);
            if (!Arrays.equals(headNameArray, baseArry)) {
                return this.getErrResponseResult("error", 404L, "表头和规定格式不匹配");
            }


        }

        if (headNameArray.length == 11) {
            String[] baseArry = {"序号", "委托方", "客户名称", "员工姓名", "证件号码", "福利办理方", "业务员", "原基数(仅供参考)", "新基数", "公积金比例", "备注"};
            Arrays.sort(baseArry);
            if (!Arrays.equals(headNameArray, baseArry)) {
                return this.getErrResponseResult("error", 404L, "表头和规定格式不匹配");
            }
        }
        return this.getOkResponseResult("成功", "不存在null值");
    }

    /**
     * 用于判断number是否为空或者是否为数字
     *
     * @param cell cell单元格
     * @return true 是数字且不为空 false 不是数字或者为空
     * @author wangyong
     */
    private boolean checkIsNumber(Cell cell) {
        boolean result = true;

        if (cell.getCellType() == CellType.NUMERIC.getCode()) {
            // 本身为数字
            Double numericCellValue = cell.getNumericCellValue();
            if (numericCellValue == null) {
                // 不存在值
                result = false;
            } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                // 为时间
                result = false;
            }
        }

        if (cell.getCellType() == CellType.STRING.getCode()) {
            // 本身为字符串
            String value = cell.getStringCellValue();
            if (StringUtils.isEmpty(value)) {
                // 为空，校验失败
                result = false;
            } else {
                try {
                    Double d = Double.parseDouble(value);
                } catch (Exception e) {
                    // 类型转换异常，证明不是数字
                    result = false;
                }
            }
        }

        return result;
    }


}
