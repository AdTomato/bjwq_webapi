package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.CollectService;
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
 * @Description 采集数据
 * @ClassName com.authine.cloudpivot.web.api.controller.CollectController
 * @Date 2020/1/10 13:52
 **/
@RestController
@RequestMapping("/controller/collectController")
@Slf4j
public class CollectController extends BaseController {

    @Resource
    private CollectService collectService;

    /**
     * 方法说明：发起数据采集时，激活需要上传数据的人员对应流程
     * @Param id 发起数据采集的流程业务id
     * @Param refIds 附件的refId
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/13 14:14
     */
    @GetMapping("/startCollect")
    @ResponseBody
    public ResponseResult <String> startCollect(String id, String refIds) {
        if (StringUtils.isBlank(id)) {
            log.info("没有业务id，激活失败!");
            return this.getOkResponseResult("error", "没有业务id，激活失败!");
        }

        if (StringUtils.isBlank(refIds)) {
            log.info("没有采集模板，激活失败!");
            return this.getOkResponseResult("error", "没有采集模板，激活失败!");
        }

        try {
            // 当前用户
            String userId = getUserId();
            UserModel user = this.getOrganizationFacade().getUser(userId);
            // 激活基数采集
            collectService.startCollect(this.getBizObjectFacade(), this.getWorkflowInstanceFacade(),
                    this.getOrganizationFacade(), id, refIds, userId, user.getDepartmentId());
            return this.getOkResponseResult("success", "激活成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", "激活失败!");
        }
    }

    /**
     * 方法说明：基数采集回写附件到发起采集中去
     * @Param id 基数采集业务id
     * @Param refIds 基数采集附件refId
     * @Param startCollectId 发起采集业务id
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/13 15:34
     */
    @GetMapping("/returnCollect")
    @ResponseBody
    public ResponseResult <String> returnCollect(String id, String refIds, String startCollectId) {
        if (StringUtils.isBlank(id)) {
            log.info("没有业务id，回写附件失败!");
            return this.getOkResponseResult("error", "没有业务id，回写附件失败!");
        }

        if (StringUtils.isBlank(refIds)) {
            log.info("没有上传数据，回写附件失败!");
            return this.getOkResponseResult("error", "没有上传数据，回写附件失败!");
        }

        if (StringUtils.isBlank(startCollectId)) {
            log.info("没有对应的发起采集流程，无需回写附件!");
            return this.getOkResponseResult("success", "没有对应的发起采集流程，无需回写附件!");
        }

        try {
            // 回写附件
            collectService.returnCollect(id, refIds, startCollectId);
            return this.getOkResponseResult("success", "回写附件成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", "回写附件失败!");
        }
    }

    /**
     * 方法说明：过节点申请修改截止时间
     * @Param id 基数采集业务id
     * @Param endTime
     * @return com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.String>
     * @throws
     * @author liulei
     * @Date 2020/1/13 15:34
     */
    @GetMapping("/updateEndTime")
    @ResponseBody
    public ResponseResult <String> updateEndTime(String id, String endTime) {
        if (StringUtils.isBlank(id)) {
            log.info("没有业务id，过节点申请修改截止时间失败!");
            return this.getOkResponseResult("error", "没有业务id，过节点申请修改截止时间失败!");
        }

        if (StringUtils.isBlank(endTime)) {
            log.info("没有截止时间，过节点申请修改截止时间失败!");
            return this.getOkResponseResult("error", "没有截止时间，过节点申请修改截止时间失败!");
        }

        try {
            // 回写附件
            collectService.updateEndTime(id, endTime);
            return this.getOkResponseResult("success", "过节点申请修改截止时间成功!");
        } catch (Exception e) {
            log.info(e.getMessage());
            return this.getOkResponseResult("error", "过节点申请修改截止时间失败!");
        }
    }
}
