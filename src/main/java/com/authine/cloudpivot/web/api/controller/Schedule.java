package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.CityTimeNodeService;
import com.authine.cloudpivot.web.api.service.CollectionRuleService;
import com.authine.cloudpivot.web.api.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liulei
 * @Description 定时器任务类
 * @ClassName com.authine.cloudpivot.web.api.controller.Schedule
 * @Date 2020/1/2 11:29
 **/
@Component
@Slf4j
public class Schedule extends BaseController {

    @Resource
    private CityTimeNodeService cityTimeNodeService;

    @Resource
    private CollectionRuleService collectionRuleService;

    /**
     * 方法说明：每月一号凌晨1点执行一次，自动创建省内的城市时间节点设置
     * @return void
     * @author liulei
     * @Date 2020/5/8 14:06
     */
    @Scheduled(cron = "0 0 1 1/1 * ?")
    public void insertCityTimeNode() {
        try {
            String curBusinessYear = cityTimeNodeService.insertCityTimeNode();
            log.info("自动创建业务年月：" + curBusinessYear + "的省内的城市时间节点成功！");
        } catch (Exception e) {
            log.error("自动创建省内的城市时间节点失败！");
            e.printStackTrace();
        }
    }

    /**
     * 方法说明：每个月一号将有效终止月<当前时间的汇缴规则数据转移到历史数据中
     * @return void
     * @author liulei
     * @Date 2020/5/9 16:47
     */
    @Scheduled(cron = "0 30 1 1/1 * ?")
    public void collectionRuleMaintain() {
        try {
            collectionRuleService.moveCollectionRuleDataToHistory();
            log.info("将有效终止月<当前时间的汇缴规则数据转移到历史数据中成功！");
        } catch (Exception e) {
            log.error("将有效终止月<当前时间的汇缴规则数据转移到历史数据中失败！");
            e.printStackTrace();
        }
    }

}
