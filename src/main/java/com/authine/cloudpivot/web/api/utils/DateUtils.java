package com.authine.cloudpivot.web.api.utils;

import sun.awt.geom.AreaOp;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DateUtils
 * @Author:lfh
 * @Date:2020/3/30 17:23
 * @Description:
 **/
public class DateUtils {
    //获取当月的第一天和最后一天
    /* * @Author lfh
     * @Description  //获取当月的第一天和最后一天
     * @Date 2020/3/31 10:04
     * @Param [monthVari]
     * @return java.util.Map<java.lang.String,java.util.Date>
     **/
    public static Map<String, Date> getMonthBeginAndEndTime(Integer monthVari) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, monthVari);
        Date startTime = calendar.getTime();  // 当月的开始时间
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endTime = calendar.getTime();  // 当月的最后一天
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("startTime", startTime);
        dateMap.put("endTime", endTime);
        return dateMap;
    }

    public static int getDay(int year, int month) {
        int day = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            case 2:
                day = getTwoMonthDay(year);
        }
        return day;
    }

    /**
     * @param year 能被100整除且能被400整除是闰年，能被4整除的是闰年
     * @return
     */
    public static int getTwoMonthDay(int year) {
        int day = 0;
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                day = 29;
            } else {
                day = 28;
            }
        } else if (year % 4 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        return day;

    }

    public static int getTimeNode(int TimeNode, int year, int month) {
        if (TimeNode == 31) {
            int day = DateUtils.getDay(year, month);
            TimeNode = day;
            return TimeNode;
        } else if ((TimeNode == 30 || TimeNode == 29 || TimeNode == 28)) {
            if (month == 2) {
                int day = DateUtils.getTwoMonthDay(year);
                TimeNode = day;
                return TimeNode;
            }
        }
        return TimeNode;
    }

    //获取时间节点封装
    public static Map<String, Date> getLastAndNowTimeNode(Integer timeNode) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        timeNode = DateUtils.getTimeNode(timeNode, year, month);

        calendar.set(year, month, timeNode);
        Date nowTimeNode = calendar.getTime();  // 当月的时间节点
        Map timeNodeMap = new HashMap();

        calendar.add(Calendar.MONTH, -1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        timeNode = DateUtils.getTimeNode(timeNode, year, month);

        calendar.set(year, month, timeNode);
        Date lastTimeNode = calendar.getTime();
        timeNodeMap.put("lastTimeNode", lastTimeNode);
        timeNodeMap.put("nowTimeNode", nowTimeNode);
        return timeNodeMap;
    }

    // 日期转字符串
    public static String dateToString(Date date) {
        StringBuffer sb = new StringBuffer();
        if (date == null) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        sb.append(calendar.get(Calendar.YEAR) + "");
        Integer month = calendar.get(Calendar.MONTH) + 1;
        sb.append(month < 10 ? "0" + month : month + "");
        int day = calendar.get(Calendar.DATE);
        sb.append(day < 10 ? "0" + day : day + "");
        return sb.toString();
    }

    /**
     * 将时间中的年月日转换成字符串
     *
     * @param calendar
     * @return 年-月-日
     * @author wangyong
     */
    public static final String getYearMonthDate(Calendar calendar) {
        return calendar != null ? calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) : null;
    }
}