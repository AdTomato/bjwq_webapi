package com.authine.cloudpivot.web.api.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.dto.SocialSecurityDeclareDto;
import com.authine.cloudpivot.web.api.entity.OrderDetails;
import com.authine.cloudpivot.web.api.excel.data.SocialSecurityDeclareData;
import com.authine.cloudpivot.web.api.service.SocialSecurityDeclareService;
import com.authine.cloudpivot.web.api.utils.UuidUtil;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wangyong
 * @time 2020/6/17 8:57
 */
@RestController
@RequestMapping("/file")
@Api(value = "文件管理", tags = "二次开发：文件管理")
public class FileController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @Value("${file.path}")
    private String FILE_PATH;

    @Autowired
    SocialSecurityDeclareService socialSecurityDeclareService;

    @ApiOperation(value = "社保申报数据导出")
    @RequestMapping("/socialSecurityDeclareDownload")
    public void socialSecurityDeclareDownload(Map conditions, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("测试", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 这里需要设置不关闭流

            List data = getEmployeeOrderFormDownloadData(conditions);

            EasyExcel.write(response.getOutputStream(), SocialSecurityDeclareData.class).autoCloseStream(Boolean.FALSE).sheet("模板")
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

    @ApiOperation(value = "社保申报数据导入")
    @RequestMapping("/socialSecurityDeclareUpload")
    public ResponseResult socialSecurityDeclareUpload(@RequestParam MultipartFile file) throws IOException {
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

    @ApiOperation(value = "社保申报反馈")
    @RequestMapping("/socialSecurityDeclareFeedback")
    public ResponseResult socialSecurityDeclareFeedback(@RequestParam String fileName) {

        return null;
    }

    /**
     * 获取社保申报反馈数据
     *
     * @param conditions
     * @return 社保申报反馈数据
     * @author wangyong
     */
    private List getEmployeeOrderFormDownloadData(Map conditions) {
        List<SocialSecurityDeclareData> result = new ArrayList<>();

        List<SocialSecurityDeclareDto> socialSecurityDeclareDtoList = socialSecurityDeclareService.getSocialSecurityDeclareDtoList(conditions);
        for (SocialSecurityDeclareDto socialSecurityDeclareDto : socialSecurityDeclareDtoList) {
            SocialSecurityDeclareData download = new SocialSecurityDeclareData();
            download.setId(socialSecurityDeclareDto.getId());
            download.setEmployeeName(socialSecurityDeclareDto.getEmployeeName());
            download.setIdentityNo(socialSecurityDeclareDto.getIdentityNo());
            download.setFirstLevelClientName(socialSecurityDeclareDto.getFirstLevelClientName());
            download.setSecondLevelClientName(socialSecurityDeclareDto.getFirstLevelClientName());

            List<OrderDetails> orderDetailsPayBackSbList = socialSecurityDeclareDto.getOrderDetailsPayBackSbList();
            List<OrderDetails> orderDetailsRemittanceSbList = socialSecurityDeclareDto.getOrderDetailsRemittanceSbList();
            // 补缴个数
            int payBackSize = orderDetailsPayBackSbList.isEmpty() ? -1 : orderDetailsPayBackSbList.size();
            // 汇缴个数
            int remittanceSize = orderDetailsRemittanceSbList.isEmpty() ? -1 : orderDetailsRemittanceSbList.size();
            for (int i = 0; i < Math.max(payBackSize, remittanceSize); i++) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if (i <= remittanceSize - 1) {
                    OrderDetails orderDetails = orderDetailsRemittanceSbList.get(i);
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
                    OrderDetails orderDetails = orderDetailsPayBackSbList.get(i);
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
                download = new SocialSecurityDeclareData();
            }
            if (payBackSize == -1 && remittanceSize == -1) {
                result.add(download);
            }
        }

        return result;
    }

}
