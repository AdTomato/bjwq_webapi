package com.authine.cloudpivot.web.api.utils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ParseExcelUtils
 * @Author:lfh
 * @Date:2020/3/13 10:35
 * @Description: 解析excel
 **/
@Slf4j
public class ParseExcelUtils {
    private static final String FULL_DATA_FORMAT = "yyyy/MM/dd  HH:mm:ss";
    private static final String SHORT_DATA_FORMAT = "yyyy/MM/dd";


    /**
     * Excel表头对应Entity属性 解析封装javabean
     *
     * @param classzz    类
     * @param in         excel流
     * @param fileName   文件名
     * @param excelHeads excel表头与entity属性对应关系
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> readExcelToEntity(Class<T> classzz, InputStream in, String fileName, List<ExcelHead> excelHeads) throws Exception {
        checkFile(fileName);    //是否EXCEL文件
        Workbook workbook = getWorkBook(in, fileName); //兼容新老版本
        List<T> excelForBeans = readExcel(classzz, workbook, excelHeads);  //解析Excel
        return excelForBeans;
    }

    /**
     * 解析Excel转换为Entity
     *
     * @param classzz  类
     * @param in       excel流
     * @param fileName 文件名
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> readExcelToEntity(Class<T> classzz, InputStream in, String fileName) throws Exception {
        return readExcelToEntity(classzz, in, fileName, null);
    }

    /**
     * 校验是否是Excel文件
     *
     * @param fileName
     * @throws Exception
     */
    public static boolean checkFile(String fileName) throws Exception {
        if (!StringUtils.isEmpty(fileName) && !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            return false;
        }
        return true;
    }

    /**
     * 兼容新老版Excel
     *
     * @param in
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Workbook getWorkBook(InputStream in, String fileName) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            // return WorkbookFactory.create(in);
            return new XSSFWorkbook(in);
        } else {
            return WorkbookFactory.create(in);
        }
    }

    /**
     * 解析Excel
     *
     * @param classzz    类
     * @param workbook   工作簿对象
     * @param excelHeads excel与entity对应关系实体
     * @param <T>
     * @return
     * @throws Exception
     */
    private static <T> List<T> readExcel(Class<T> classzz, Workbook workbook, List<ExcelHead> excelHeads) throws Exception {
        List<T> beans = new ArrayList<T>();
        int sheetNum = workbook.getNumberOfSheets();
        // for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
        for (int sheetIndex = 0; sheetIndex < 1; sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            String sheetName = sheet.getSheetName();
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            Row head = sheet.getRow(firstRowNum);
            if (head == null)
                continue;
            short firstCellNum = head.getFirstCellNum();
            short lastCellNum = head.getLastCellNum();
            Field[] fields = classzz.getDeclaredFields();
            for (int rowIndex = firstRowNum + 1; rowIndex <= lastRowNum; rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);
                if (dataRow == null)
                    continue;
                T instance = classzz.newInstance();
                if (CollectionUtils.isEmpty(excelHeads)) {  //非头部映射方式，默认不校验是否为空，提高效率
                    firstCellNum = dataRow.getFirstCellNum();
                    lastCellNum = dataRow.getLastCellNum();
                }
                for (int cellIndex = firstCellNum; cellIndex < lastCellNum; cellIndex++) {
                    Cell headCell = head.getCell(cellIndex);
                    if (headCell == null)
                        continue;
                    Cell cell = dataRow.getCell(cellIndex);
                    headCell.setCellType(CellType.STRING);
                    String headName = headCell.getStringCellValue().trim();
                    if (StringUtils.isEmpty(headName)) {
                        continue;
                    }
                    ExcelHead eHead = null;
                    if (!CollectionUtils.isEmpty(excelHeads)) {
                        for (ExcelHead excelHead : excelHeads) {
                            if (headName.equals(excelHead.getExcelName())) {
                                eHead = excelHead;
                                headName = eHead.getEntityName();
                                break;
                            }
                        }
                    }
                    for (Field field : fields) {
                        if (headName.equalsIgnoreCase(field.getName())) {
                            String methodName = MethodUtils.setMethodName(field.getName());
                            Method method = classzz.getMethod(methodName, field.getType());
                            if (isDateFied(field)) {
                                Date date = null;
                                if (cell != null) {
                                    date = cell.getDateCellValue();
                                }
                                if (date == null) {
                                    volidateValueRequired(eHead, sheetName, rowIndex);
                                    break;
                                }
                                method.invoke(instance, cell.getDateCellValue());
                            } else {
                                String value = null;
                                if (cell != null) {
                                    if (CellType.NUMERIC.equals(cell.getCellType())) {
                                        value = realStringValueOfDouble(cell.getNumericCellValue());
                                    }else {
                                        cell.setCellType(CellType.STRING);
                                        value = cell.getStringCellValue();
                                    }

                                }
                                if (StringUtils.isEmpty(value)) {
                                    volidateValueRequired(eHead, sheetName, rowIndex);
                                    break;
                                }
                                method.invoke(instance, convertType(field.getType(), value.trim()));
                            }
                            break;
                        }
                    }
                }
                beans.add(instance);
            }
        }
        return beans;
    }

    /**
     * 是否日期字段
     *
     * @param field
     * @return
     */
    private static boolean isDateFied(Field field) {
        return (Date.class == field.getType());
    }

    /**
     * 空值校验
     *
     * @param excelHead
     * @throws Exception
     */
    private static void volidateValueRequired(ExcelHead excelHead, String sheetName, int rowIndex) throws Exception {
        if (excelHead != null && excelHead.isRequired()) {
            throw new Exception("《" + sheetName + "》第" + (rowIndex + 1) + "行:\"" + excelHead.getExcelName() + "\"不能为空！");
        }
    }

    /**
     * 类型转换
     *
     * @param classzz
     * @param value
     * @return
     */
    private static Object convertType(Class classzz, String value) {
        if (Integer.class == classzz || int.class == classzz) {
            return Integer.valueOf(value);
        }
        if (Short.class == classzz || short.class == classzz) {
            return Short.valueOf(value);
        }
        if (Byte.class == classzz || byte.class == classzz) {
            return Byte.valueOf(value);
        }
        if (Character.class == classzz || char.class == classzz) {
            return value.charAt(0);
        }
        if (Long.class == classzz || long.class == classzz) {
            return Long.valueOf(value);
        }
        if (Float.class == classzz || float.class == classzz) {
            return Float.valueOf(value);
        }
        if (Double.class == classzz || double.class == classzz) {
            return Double.valueOf(value);
        }
        if (Boolean.class == classzz || boolean.class == classzz) {
            return Boolean.valueOf(value.toLowerCase());
        }
        if (BigDecimal.class == classzz) {
            return new BigDecimal(value);
        }
       /* if (Date.class == classzz) {
            SimpleDateFormat formatter = new SimpleDateFormat(FULL_DATA_FORMAT);
            ParsePosition pos = new ParsePosition(0);
            Date date = formatter.parse(value, pos);
            return date;
        }*/
        return value;
    }

    /**
     * 获取properties的set和get方法
     */
    static class MethodUtils {
        private static final String SET_PREFIX = "set";
        private static final String GET_PREFIX = "get";

        private static String capitalize(String name) {
            if (name == null || name.length() == 0) {
                return name;
            }
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public static String setMethodName(String propertyName) {
            return SET_PREFIX + capitalize(propertyName);
        }

        public static String getMethodName(String propertyName) {
            return GET_PREFIX + capitalize(propertyName);
        }
    }


    public static String realStringValueOfDouble(Double d) {
        String doubleStr = d.toString();
        boolean b = doubleStr.contains("E");
        int indexOfPoint = doubleStr.indexOf('.');
        if (b) {
            int indexOfE = doubleStr.indexOf('E');
            BigInteger xs = new BigInteger(doubleStr.substring(indexOfPoint + BigInteger.ONE.intValue(), indexOfE));
            int pow = Integer.parseInt(doubleStr.substring(indexOfE + BigInteger.ONE.intValue()));
            int xsLen = xs.toByteArray().length;
            int scale = xsLen - pow > 0 ? xsLen - pow : 0;
            final String format = "%." + scale + "f";
            doubleStr = String.format(format, d);
        } else {
            Pattern p = Pattern.compile(".0$");
            Matcher m = p.matcher(doubleStr);
            if (m.find()) {
                doubleStr = doubleStr.replace(".0", "");
            }
        }
        return doubleStr;
    }
    

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param values    内容
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

    /**
     * @Author lfh
     * @Description 动态获取表头信息
     * @Date 2020/4/20 9:26
     * @throws
     * @param fileName 文件名
     * @param fis  文件输入流
     * @return {@link java.util.List<java.lang.String>}
     **/
    public static List<String> getHeadName(String fileName, FileInputStream fis) throws IOException {
        Workbook workBook = null;
        try {
            workBook = getWorkBook(fis, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fis.close();
        }
        Sheet sheet = workBook.getSheetAt(0);
        List<String> headList = new ArrayList<>();
        int firstRowNum = sheet.getFirstRowNum();
        Row row = sheet.getRow(firstRowNum);
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();
        for (int i = firstCellNum; i < lastCellNum; i++) {
            String stringCellValue = row.getCell(i).getStringCellValue();
            headList.add(stringCellValue);

        }
        return headList;
    }
    /**
     * 用于判断number是否为空或者是否为数字
     *
     * @param cell cell单元格
     * @return true 是数字且不为空 false 不是数字或者为空
     * @author wangyong
     */
    public static boolean checkIsNumber(Cell cell) {
        boolean result = true;

        if (cell.getCellType() == CellType.NUMERIC) {
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

        if (cell.getCellType() == CellType.STRING) {
            // 本身为字符串
            String value = cell.getStringCellValue();
            if (org.apache.commons.lang3.StringUtils.isEmpty(value)) {
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
