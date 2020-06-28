package com.authine.cloudpivot.web.api.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.DeclareDto;
import com.authine.cloudpivot.web.api.entity.OrderDetails;
import com.authine.cloudpivot.web.api.excel.data.DeclareData;
import com.authine.cloudpivot.web.api.service.ProvidentFundDeclareService;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import com.authine.cloudpivot.web.api.utils.UuidUtil;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangyong
 * @time 2020/6/17 8:57
 */
@RestController
@RequestMapping("/controller/file")
@Api(value = "文件管理", tags = "二次开发：文件管理")
public class FileController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @Value("${file.path}")
    private String FILE_PATH;

    @Autowired
    SocialSecurityDeclareService socialSecurityDeclareService;

    @Autowired
    ProvidentFundDeclareService providentFundDeclareService;


    @ApiOperation(value = "文件上传")
    @RequestMapping("/fileUpload")
    public ResponseResult<Object> fileUpload(@RequestParam MultipartFile file) throws IOException {
        LOG.info("文件上传开始");
        LOG.info("文件名称:{}", file.getOriginalFilename());
        LOG.info("文件大小:{}", file.getSize());

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        File dir = new File(FILE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fullFileName = UuidUtil.getShortUuid() + "-" + format.format(new Date()) + "." + suffix;
        String fullFilePath = FILE_PATH + File.separator + fullFileName;
        File fullFile = new File(fullFilePath);
        file.transferTo(fullFile);
        return this.getErrResponseResult(fullFileName, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @ApiOperation(value = "模板文件下载")
    @GetMapping("/templateFileDownload")
    public void templateFileDownload(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        File file = new File(FILE_PATH + File.separator + "template" + File.separator + fileName);
        if (!file.exists()) {
            throw new RuntimeException("下载文件不存在");
        }
        FileInputStream inputStream = new FileInputStream(file);
        String templateName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.addHeader("Content-Disposition", "attachment;filename=" + new String((templateName + "_" + System.currentTimeMillis() + "." + suffix).getBytes("gbk"), "iso8859-1"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        byte[] data = new byte[1 * 1024 * 1024];
        int len = 0;
        while ((len = inputStream.read(data)) != -1) {
            bufferedOutputStream.write(data, 0, len);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        inputStream.close();
    }

    @ApiOperation(value = "社保申报数据导出")
    @RequestMapping("/socialSecurityDeclareDownload")
    public void socialSecurityDeclareDownload(Map conditions, HttpServletResponse response) throws IOException {
        List data = getDeclareData(conditions, "社保");
        download(response, data);
    }

    @ApiOperation(value = "公积金申报数据导出")
    @RequestMapping("/providentFundDeclareDownload")
    public void providentFundDeclareDownload(Map conditions, HttpServletResponse response) throws IOException {
        List data = getDeclareData(conditions, "公积金");
        download(response, data);
    }

    @ApiOperation(value = "社保申报反馈")
    @RequestMapping("/socialSecurityDeclareFeedback")
    public ResponseResult<Object> socialSecurityDeclareFeedback(@RequestParam String fileName) {
        return null;
    }

    @ApiOperation(value = "公积金申报反馈")
    @RequestMapping("/providentFundDeclareFeedback")
    public ResponseResult<Object> providentFundDeclareFeedback(@RequestParam String fileName) {
        return null;
    }

    /**
     * web导出的封装
     *
     * @param response HttpServletResponse
     * @param data     导出数据
     * @throws IOException
     * @author wangyong
     */
    private void download(HttpServletResponse response, List data) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("测试", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 这里需要设置不关闭流

            EasyExcel.write(response.getOutputStream(), DeclareData.class).autoCloseStream(Boolean.FALSE).sheet("模板")
                    .doWrite(data);
        } catch (Exception e) {
            System.out.println(e);
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    /**
     * 获取社保，公积金申报导出数据
     *
     * @param conditions 筛选条件
     * @param type       类型（社保、公积金）
     * @return 导出数据
     * @author wangyong
     */
    private List<DeclareData> getDeclareData(Map conditions, String type) {
        List<DeclareData> result = new ArrayList<>();
        List<DeclareDto> declareDtoList = null;
        if ("社保".equals(type)) {
            declareDtoList = socialSecurityDeclareService.getSocialSecurityDeclareDtoList(conditions);
        } else if ("公积金".equals(type)) {
            declareDtoList = providentFundDeclareService.getProvidentFundDeclareDtoList(conditions);
        }
        if (!CollectionUtils.isEmpty(declareDtoList)) {
            for (DeclareDto declareDto : declareDtoList) {
                DeclareData download = new DeclareData();
                download.setId(declareDto.getId());
                download.setEmployeeName(declareDto.getEmployeeName());
                download.setIdentityNo(declareDto.getIdentityNo());
                download.setFirstLevelClientName(declareDto.getFirstLevelClientName());
                download.setSecondLevelClientName(declareDto.getFirstLevelClientName());

                List<OrderDetails> orderDetailsPayBackList = declareDto.getOrderDetailsPayBackList();
                List<OrderDetails> orderDetailsRemittanceList = declareDto.getOrderDetailsRemittanceList();
                // 补缴个数
                int payBackSize = orderDetailsPayBackList.isEmpty() ? -1 : orderDetailsPayBackList.size();
                // 汇缴个数
                int remittanceSize = orderDetailsRemittanceList.isEmpty() ? -1 : orderDetailsRemittanceList.size();
                for (int i = 0; i < Math.max(payBackSize, remittanceSize); i++) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
                    if (i <= remittanceSize - 1) {
                        OrderDetails orderDetails = orderDetailsRemittanceList.get(i);
                        download.setProductNameH(orderDetails.getProductName());
                        download.setStartChargeTimeH(orderDetails.getStartChargeTime() == null ? null : format.format(orderDetails.getStartChargeTime()));
                        download.setEndChargeTimeH(orderDetails.getEndChargeTime() == null ? null : format.format(orderDetails.getEndChargeTime()));
                        download.setEmployeeBaseH(orderDetails.getEmployeeBase());
                        download.setCompanyBaseH(orderDetails.getCompanyBase());
                        download.setEmployeeRatioH(orderDetails.getEmployeeRatio());
                        download.setCompanyRatioH(orderDetails.getCompanyRatio());
                        download.setSumH(orderDetails.getSum());
                        download.setEmployeeMoneyH(orderDetails.getEmployeeMoney());
                        download.setCompanyMoneyH(orderDetails.getCompanyMoney());
                        download.setEmployeeSurchargeValueH(orderDetails.getEmployeeSurchargeValue());
                        download.setCompanySurchargeValueH(orderDetails.getCompanySurchargeValue());
                    }

                    if (i <= payBackSize - 1) {
                        OrderDetails orderDetails = orderDetailsPayBackList.get(i);
                        download.setProductNameB(orderDetails.getProductName());
                        download.setStartChargeTimeB(orderDetails.getStartChargeTime() == null ? null : format.format(orderDetails.getStartChargeTime()));
                        download.setEndChargeTimeB(orderDetails.getEndChargeTime() == null ? null : format.format(orderDetails.getEndChargeTime()));
                        download.setEmployeeBaseB(orderDetails.getEmployeeBase());
                        download.setCompanyBaseB(orderDetails.getCompanyBase());
                        download.setEmployeeRatioB(orderDetails.getEmployeeRatio());
                        download.setCompanyRatioB(orderDetails.getCompanyRatio());
                        download.setSumB(orderDetails.getSum());
                        download.setEmployeeMoneyB(orderDetails.getEmployeeMoney());
                        download.setCompanyMoneyB(orderDetails.getCompanyMoney());
                        download.setEmployeeSurchargeValueB(orderDetails.getEmployeeSurchargeValue());
                        download.setCompanySurchargeValueB(orderDetails.getCompanySurchargeValue());
                    }
                    result.add(download);
                    download = new DeclareData();
                    download.setId(declareDto.getId());
                    download.setEmployeeName(declareDto.getEmployeeName());
                    download.setIdentityNo(declareDto.getIdentityNo());
                    download.setFirstLevelClientName(declareDto.getFirstLevelClientName());
                    download.setSecondLevelClientName(declareDto.getFirstLevelClientName());
                }
                if (payBackSize == -1 && remittanceSize == -1) {
                    result.add(download);
                }
            }

        }
        return result;
    }

}
