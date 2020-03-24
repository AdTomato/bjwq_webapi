package com.authine.cloudpivot.web.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ExportExcel
 * @Author:lfh
 * @Date:2020/3/13 16:16
 * @Description: 导出excel
 **/
@Slf4j
public class ExportExcel {
    public static <T>void exportExcel(Collection<T> dataset, OutputStream out) {
        exportExcel("POI导出EXCEL文档", null, dataset, out, "yyyy-MM-dd");
    }

    public static <T>void exportExcel(String title, String[] headers, Collection<T> dataset, OutputStream out) {
        exportExcel(title, headers, dataset, out, "yyyy-MM-dd");
    }

    public static <T>void exportExcel(String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
        exportExcel("POI导出EXCEL文档", headers, dataset, out, pattern);
    }
    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格属性列名数组
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    @SuppressWarnings("unchecked")
    public static<T> void exportExcel(String title, String[] headers,
                                   Collection<T> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);

        //设置样式风格
        CellStyle cs = workbook.createCellStyle();
        //居中对齐.
        cs.setAlignment(HorizontalAlignment.CENTER);
        //边框
        cs.setBorderBottom(BorderStyle.THIN); //下边框
        cs.setBorderLeft(BorderStyle.THIN);//左边框
        cs.setBorderTop(BorderStyle.THIN);//上边框
        cs.setBorderRight(BorderStyle.THIN);//右边框

        //产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                /*if(i==0) {
                    cell.setCellValue(index);
                    continue;
                }*/
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    Object value = getMethod.invoke(t, new Object[] {});
                    //判断值的类型后进行强制类型转换
                    String textValue;
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                    }  else{
                        //其它数据类型都当作字符串简单处理
                        if(null == value){
                            textValue = "";
                        }else {
                            textValue = value.toString();
                        }

                    }
                    //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if(textValue!=null){
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if(matcher.matches()){
                            //是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        }else{
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            HSSFFont font3 = workbook.createFont();
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    log.error(e.getMessage(),e);
                } catch (NoSuchMethodException e) {
                    log.error(e.getMessage(),e);
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(),e);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(),e);
                } catch (InvocationTargetException e) {
                    log.error(e.getMessage(),e);
                }

            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
