package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.entity.CityTimeNode;
import com.authine.cloudpivot.web.api.entity.CityTimeNodeSwDetails;
import com.authine.cloudpivot.web.api.mapper.CityTimeNodeMapper;
import com.authine.cloudpivot.web.api.service.CityTimeNodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.CityTimeNodeServiceImpl
 * @Date 2020/5/8 14:08
 **/
@Service
public class CityTimeNodeServiceImpl implements CityTimeNodeService {

    @Resource
    private CityTimeNodeMapper cityTimeNodeMapper;

    @Override
    public String insertCityTimeNode() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date curDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        Date lastMonth = calendar.getTime();
        // 当前业务年月
        String curBusinessYear = sdf.format(curDate);
        // 上一业务年月
        String lastBusinessYear = sdf.format(lastMonth);

        cityTimeNodeMapper.addCurCityTimeNode(curBusinessYear, lastBusinessYear);
        return curBusinessYear;
    }

    @Override
    public CityTimeNode getAnhuiCityTimeNode(String city, String businessYear) {
        return cityTimeNodeMapper.getAnhuiCityTimeNode(city, businessYear);
    }

    @Override
    public CityTimeNodeSwDetails getCityTimeNodeSwDetails(String city, String businessYear) throws Exception {
        return cityTimeNodeMapper.getCityTimeNodeSwDetails(city, businessYear);
    }

    /**
     * 根据业务年月、城市获取城市时间节点
     *
     * @param businessYear 业务年月
     * @param city         城市
     * @return 城市时间节点
     * @author wangyong
     */
    @Override
    public CityTimeNode getCityTimeNodeByBusinessYearAndCity(String businessYear, String city) {
        return cityTimeNodeMapper.getCityTimeNodeByBusinessYearAndCity(businessYear, city);
    }
}
