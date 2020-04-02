package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.web.api.controller.base.BaseQueryRuntimeController;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.utils.ExcelUtils
 * @Date 2019/12/12 17:16
 **/
public class ExcelUtils extends BaseQueryRuntimeController {

    /**
     * 方法说明：判断文件类型是否为“xxxx.xlsx”
     * @Param fileName
     * @return boolean
     * @throws
     * @author liulei
     * @Date 2019/12/27 10:05
     */
    public static void checkFileType(String fileName) throws Exception{
        String[] fileNameArray = fileName.split("\\.");
        if (!"xlsx".equals(fileNameArray[1])) {
            throw new Exception("文件类型不是xlsx");
        }
    }

    /**
     * 方法说明：
     * @Param filePath 要解析的文件名称
     * @Param startRow 开始解析的行号  从0开始
     * @Param startCol 开始解析数据的列 从0开始
     * @Param endCol 解析结束的列 从0开始
     * @return java.util.List<java.lang.String[]>
     * @throws 
     * @author liulei
     * @Date 2019/12/12 16:40
     */
    public static List<String[]> readFile(String fileName,int startRow,int startCol,int endCol) throws Exception{
        return readFile(fileName, startRow, startCol, endCol,0);
    }
    /**
     * 方法说明：读取Excel中的内容
     * @Param fileName 要解析的文件名称
     * @Param startRow 开始解析的行号  从0开始
     * @Param startCol 开始解析数据的列 从0开始
     * @Param endCol 解析结束的列 从0开始
     * @Param sheetNum 要解析的sheet 从0开始
     * @return java.util.List<java.lang.String[]>
     * @throws
     * @author liulei
     * @Date 2019/12/12 16:43
     */
    private static List <String[]> readFile(String fileName, int startRow, int startCol, int endCol, int sheetNum)  throws Exception{

        if(startCol == endCol || startCol > endCol || endCol == 0){
            throw new Exception("startCol必须大于endCol，且endCol不能等于零");
        }

        //最终返回的excel内容
        List<String[]> excelContents = new ArrayList <String[]>();

        // 文件写入输入流
        InputStream inputStream = null;

        OutputStream os = null;

        try {

            File excelFile = FileOperateHelper.getFile(fileName);

            if (!excelFile.exists()) {// 文件不存在，不继续执行
                throw new RuntimeException("Excel文件导入-上传文件未找到！");
            }

            // 创建工作表对象
            Workbook workbook = null;
            try {
                workbook = new HSSFWorkbook(new FileInputStream(excelFile));
            } catch (Exception ex) {
                workbook = new XSSFWorkbook(new FileInputStream(excelFile));
            }

            // 获取第一个Sheet
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if (sheet == null) {
                throw new RuntimeException("Excel文件导入-文件中没有找到对应工作表");
            }

            // 开始从第一行遍历
            for (int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                //遍历该行的单元格 定义储存数据的单元格数组
                String[] cells = new String[endCol - startCol + 1];
                int colNum = 0;
                int emptyCellCount = 0;
                for (int i = startCol; i <= endCol; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null && StringUtils.isNotBlank(cell.toString())) {
                        cells[colNum] = getValue(cell);
                    } else {
                        emptyCellCount++;
                    }
                    colNum++;
                }
                //每个单元格都是空的，则认为该行就是空行，跳过
                if (emptyCellCount == (endCol - startCol + 1)) {
                    continue;
                }
                excelContents.add(cells);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Excel文件导入-文件读取失败:" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                throw new Exception("Excel文件导入-文件读取失败:" + e.getMessage());
            }
        }
        return excelContents;
    }

    public static String getValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType().getCode()) {
            case 1:
                return cell.getRichStringCellValue().getString().trim();
            case 0:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型
                    return sdf.format(dt);
                } else {
                    // modify mapp 2017-07-10
                    // 防止数值变成科学计数法
                    String strCell = "";
                    Double num = cell.getNumericCellValue();
                    BigDecimal bd = new BigDecimal(num.toString());
                    if (bd != null) {
                        strCell = bd.toPlainString();
                    }
                    // 去除 浮点型 自动加的 .0
                    if (strCell.endsWith(".0")) {
                        strCell = strCell.substring(0, strCell.indexOf("."));
                    }
                    return strCell;
                }
            case 4:
                return String.valueOf(cell.getBooleanCellValue());
            case 2:
//				return cell.getCellFormula();
                String strCell = "";
                try {
                    /*
                     * 此处判断使用公式生成的字符串有问题，因为HSSFDateUtil.isCellDateFormatted(cell)
                     * 判断过程中cell
                     * .getNumericCellValue();方法会抛出java.lang.NumberFormatException异常
                     */
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型
                        strCell = sdf.format(dt);
                    } else {
                        // modify mapp 2017-07-10
                        // 防止数值变成科学计数法
                        Double num = cell.getNumericCellValue();
                        BigDecimal bd = new BigDecimal(num.toString());
                        if (bd != null) {
                            strCell = bd.toPlainString();
                        }
                        // 去除 浮点型 自动加的 .0
                        if (strCell.endsWith(".0")) {
                            strCell = strCell.substring(0, strCell.indexOf("."));
                        }
                    }
                } catch (IllegalStateException e) {
                    strCell = String.valueOf(cell.getRichStringCellValue());
                }
                return strCell;
            case 3:
                return "";
            default:
                return "";
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/2/5 10:46
     * @param fileName : 文件名
     * @return : boolean
     * @Description: 判断是否是Excel
     */
    public static boolean changeIsExcel(String fileName) {
        if (!FileUtils.judgeSuffixName(fileName, "xlsx") && !FileUtils.judgeSuffixName(fileName, "xls")) {
            return false;
        }
        return true;
    }

    /**
     * @Author: wangyong
     * @Date: 2020/2/5 11:29
     * @param fileName :
     * @param excelFile :
     * @return : org.apache.poi.ss.usermodel.Workbook
     * @Description: 根据不同的excel版本返回相应的workbook
     */
    public static Workbook getWorkbook(String fileName, File excelFile) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(excelFile);
        Workbook workbook = null;
        if (FileUtils.isXLSX(fileName)) {
            // 是xlsx格式的
            workbook = new XSSFWorkbook(fileInputStream);
        }
        if (FileUtils.isXLS(fileName)) {
            // 是xls格式的
            workbook = new HSSFWorkbook(new POIFSFileSystem(fileInputStream));
        }
        return workbook;
    }

    public static List<String[]> readFile(String fileName) throws Exception{
        //最终返回的excel内容
        List<String[]> excelContents = new ArrayList <String[]>();
        // 文件写入输入流
        InputStream inputStream = null;

        OutputStream os = null;

        try {
            File excelFile = FileOperateHelper.getFile(fileName);

            if (!excelFile.exists()) {// 文件不存在，不继续执行
                throw new RuntimeException("Excel文件导入-上传文件未找到！");
            }

            // 创建工作表对象
            Workbook workbook = null;
            try {
                workbook = new HSSFWorkbook(new FileInputStream(excelFile));
            } catch (Exception ex) {
                workbook = new XSSFWorkbook(new FileInputStream(excelFile));
            }

            // 获取第一个Sheet
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Excel文件导入-文件中没有找到对应工作表");
            }

            // 开始从第一行遍历
            int endCol = 0, startCol = 0;
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (rowNum == 0) {
                    if (row == null) {
                        throw new RuntimeException("表头出错！");
                    }
                    endCol = row.getLastCellNum() - 1;
                    if (endCol < 1) {
                        throw new RuntimeException("表头出错！");
                    }
                }
                if (row == null) {
                    continue;
                }

                //遍历该行的单元格 定义储存数据的单元格数组
                String[] cells = new String[endCol - startCol + 1];
                int colNum = 0;
                int emptyCellCount = 0;
                for (int i = startCol; i <= endCol; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null && StringUtils.isNotBlank(cell.toString())) {
                        cells[colNum] = getValue(cell);
                    } else {
                        emptyCellCount++;
                    }
                    colNum++;
                }
                //每个单元格都是空的，则认为该行就是空行，跳过
                if (emptyCellCount == (endCol - startCol + 1)) {
                    continue;
                }
                excelContents.add(cells);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Excel文件导入-文件读取失败:" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                throw new Exception("Excel文件导入-文件读取失败:" + e.getMessage());
            }
        }
        return excelContents;
    }
}
