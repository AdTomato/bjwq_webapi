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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        String bizObjectId = baseControllerGetBaseNunInfo.getBizObjectId();
        // List<String> client = baseControllerGetBaseNunInfo.getClient();

        Attachment attachment = attachmentService.getFileName(bizObjectId, "submit_collect", "collect_data");
        if (attachment != null && StringUtils.isBlank(attachment.getName())) {
            return this.getOkResponseResult("error", "上传文件名为空");
        }

        String fileName = attachment.getName();
        // String fileName = "运行发布基数采集模板-社保.xlsx";
        if (!ExcelUtils.changeIsExcel(fileName)) {
            return this.getOkResponseResult("error", "上传的文件不必须是excel文件");
        }
        String filePath = "D:\\upload\\";
        String realPath = new StringBuilder().append(filePath).append(attachment.getRefId()).append(fileName).toString();
        File file = new File(realPath);
        FileInputStream fis = new FileInputStream(file);
        Workbook workBook = ParseExcelUtils.getWorkBook(fis, fileName);
        fis.close();
        Sheet sheet = workBook.getSheetAt(0);
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(7).getCellType() == CellType.BLANK || row.getCell(8).getCellType() == CellType.BLANK) {
                return this.getOkResponseResult("error", "原基数和新基数不允许存在空值");
            }
        }
        FileInputStream is = new FileInputStream(file);
        List<String> headName = ParseExcelUtils.getHeadName(realPath, is);
        if (headName == null || headName.isEmpty()) {
            return this.getOkResponseResult("error", "未解析到表头数据");
        }
        String[] headNameArray = new String[headName.size()];
        headName.toArray(headNameArray);
        Arrays.sort(headNameArray);

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


}
