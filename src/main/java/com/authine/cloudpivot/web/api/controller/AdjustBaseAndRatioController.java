package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.AdjustBaseRatioTask;
import com.authine.cloudpivot.web.api.service.AdjustBaseAndRatioService;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 调基调比Controller
 * @ClassName com.authine.cloudpivot.web.api.controller.AdjustBaseAndRatioController
 * @Date 2020/4/13 17:04
 **/
@RestController
@RequestMapping("/controller/adjustBaseAndRatio")
@Slf4j
public class AdjustBaseAndRatioController extends BaseController {

    @Resource
    private AdjustBaseAndRatioService adjustBaseAndRatioService;

    /**
     * 方法说明：导入调基调比数据
     * @param fileName 文件名称
     * @param taskId 调整任务id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/4/23 13:38
     */
    @GetMapping("/adjustImport")
    @ResponseBody
    public ResponseResult <String> adjustImport(String fileName, String taskId) {
        if (StringUtils.isBlank(fileName)) {
            log.error("没有获取上传文件!");
            return this.getErrResponseResult("error", 404l, "没有获取上传文件!");
        }
        if (StringUtils.isBlank(taskId)) {
            log.error("没有获取调整任务id!");
            return this.getErrResponseResult("error", 404l, "没有获取调整任务id!");
        }
        try {
            // 判断文件类型
            ExcelUtils.checkFileType(fileName);
            // 根据id查询调整任务数据
            AdjustBaseRatioTask task = adjustBaseAndRatioService.getAdjustBaseRatioTaskById(taskId);
            if (task == null) {
                log.error("adjustImport:没有查询出调整任务信息!");
                return this.getErrResponseResult("error", 404l, "没有查询出调整任务信息!");
            }
            // 导入
            adjustBaseAndRatioService.adjustImport(fileName, task);
            return this.getOkResponseResult("success", "导入成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }

    /**
     * 方法说明：调整基数和比例
     * @param taskId 调整任务id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @author liulei
     * @Date 2020/4/23 14:54
     */
    @GetMapping("/adjustBaseAndRatio")
    @ResponseBody
    public ResponseResult <String> adjustBaseAndRatio(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            log.error("没有获取调整任务id!");
            return this.getErrResponseResult("error", 404l, "没有获取调整任务id!");
        }
        try {
            // 根据id查询调整任务数据
            AdjustBaseRatioTask task = adjustBaseAndRatioService.getAdjustBaseRatioTaskById(taskId);
            if (task == null) {
                log.error("adjustBaseAndRatio:没有查询出调整任务信息!");
                return this.getErrResponseResult("error", 404l, "没有查询出调整任务信息!");
            }
            if (task.getStartTime() == null) {
                log.error("开始调整时间为null!");
                return this.getErrResponseResult("error", 404l, "开始调整时间为null!");
            }
            // 调整基数和比例
            adjustBaseAndRatioService.adjustBaseAndRatio(task);
            return this.getOkResponseResult("success", "操作成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getErrResponseResult("error", 404l, e.getMessage());
        }
    }
}
