package com.authine.cloudpivot.web.api.utils;

/**
 * @Author: wangyong
 * @Date: 2020-02-04 09:12
 * @Description: 文件工具类
 */
public class FileUtils {

    /**
     * @param fileName : 文件名
     * @param type : 后缀名类型
     * @return : java.lang.Boolean true: 后缀名一致 false:后缀名不一致
     * @Author: wangyong
     * @Date: 2020/2/4 9:13
     * @Description: 判断该文件的后缀名
     */
    public static Boolean judgeSuffixName(String fileName, String type) {

        String[] split = fileName.split("\\.");
        if (split.length == 1) {
            // 不存在后缀名
            return false;
        } else {
            if (type.equals(split[split.length - 1])) {
                // 后缀名一致
                return true;
            } else {
                // 后缀名不一致
                return false;
            }
        }
    }

    public static Boolean isXLSX(String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length == 1) {
            // 不存在后缀名
            return false;
        } else {
            if ("xlsx".equals(split[split.length - 1])) {
                // 后缀名一致
                return true;
            } else {
                // 后缀名不一致
                return false;
            }
        }
    }

    public static Boolean isXLS(String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length == 1) {
            // 不存在后缀名
            return false;
        } else {
            if ("xls".equals(split[split.length - 1])) {
                // 后缀名一致
                return true;
            } else {
                // 后缀名不一致
                return false;
            }
        }
    }
}
