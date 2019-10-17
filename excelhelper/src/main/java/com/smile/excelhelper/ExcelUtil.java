package com.smile.excelhelper;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;

public class ExcelUtil<T> {

    private static WritableFont arial14font = null;

    private WritableCellFormat arial14format = null;
    private WritableFont arial10font = null;
    private WritableCellFormat arial10format = null;
    private WritableFont arial12font = null;
    private WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";
    private WritableSheet writableSheet;
    private WritableWorkbook workbook = null;


    private static final class SingletonHolder {
        public static final ExcelUtil INSTANCE = new ExcelUtil();

        public SingletonHolder() {
        }
    }

    public ExcelUtil() {

    }

    public static ExcelUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    public ExcelUtil format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
        return this;
    }


    /**
     * 初始化Excel表格
     */
    public void initExcel(String filePath, String sheetName, String[] colName) {
        format();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
            workbook = Workbook.createWorkbook(file);
            //设置表格的名字
            writableSheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            writableSheet.addCell((WritableCell) new Label(0, 0, filePath, arial14format));
            for (int col = 0; col < colName.length; col++) {
                writableSheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            //设置行高
            writableSheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /***
     * 初始化标题栏
     * @param filePath
     * @param column
     * @return
     * @throws WriteException
     * @throws IOException
     */
    public ExcelUtil initSheetTitle(String filePath, String[] column) throws WriteException, IOException {
        checkNullFirst();
        //创建标题栏
        writableSheet.addCell(new Label(0, 0, filePath, arial10format));
        for (int col = 0; col < column.length; col++) {
            writableSheet.addCell(new Label(col, 0, column[col], arial10format));
        }
        writableSheet.setRowView(0, 340);
        workbook.write();
        return this;
    }


    /**
     * 注入数据
     * @param objList
     * @return
     * @throws WriteException
     * @throws IOException
     */
    public ExcelUtil injectData(List<T> objList) throws WriteException, IOException {
        checkNullFirst();
        if (objList != null && objList.size() > 0) {
            for (int j = 0; j < objList.size(); j++) {
                T t = objList.get(j);
                if (!(t instanceof ExcelBaseEntity)) {
                    throw new IllegalStateException("invoker use bean should extends ExcelBaseEntity");

                }

                if (!((ExcelBaseEntity) t).isToString()) {
                    throw new IllegalStateException("invoker use bean should override toString()");
                }
                String txt = t.toString();
                List<String> list = new ArrayList<>();
                if (!TextUtils.isEmpty(txt)) {
                    String replace = txt.replace(t.getClass().getSimpleName(), "");
                    if (replace.contains("{") && replace.contains("}")) {
                        String formatString = replace.replace("{", "").replace("}", "");
                        String[] split = formatString.split(",");
                        for (String str : split) {
                            if (str.contains("=")) {
                                String substring = str.substring(str.indexOf("=") + 1);
                                list.add(substring);

                            }
                        }
                    }

                }
                Log.e(ExcelUtil.class.getSimpleName(), "list:" + list.size());


                for (int i = 0; i < list.size(); i++) {
                    writableSheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                    if (list.get(i).length() <= 4) {
                        //设置列宽
                        writableSheet.setColumnView(i, list.get(i).length() + 8);
                    } else {
                        //设置列宽
                        writableSheet.setColumnView(i, list.get(i).length() + 5);
                    }
                }
                //设置行高
                writableSheet.setRowView(j + 1, 350);
            }
            workbook.write();
        }
        return this;
    }


    /**
     * 创建表格
     * @param dirPath
     * @param filePath
     * @return
     * @throws IOException
     */
    public ExcelUtil createExcel(String dirPath, String filePath) throws IOException {
        File file = new File(dirPath, filePath);
        Log.e(ExcelUtil.class.getSimpleName(), "filePath:" + filePath);
        if (!file.exists()) {
            file.createNewFile();
        } else {
        }
        workbook = Workbook.createWorkbook(file);
        return this;
    }

    /**
     * 打开excel
     * @param file
     * @return
     * @throws IOException
     * @throws BiffException
     */
    public ExcelUtil openExcel(File file) throws IOException, BiffException {
        FileInputStream fis = new FileInputStream(file);
        Workbook wb = Workbook.getWorkbook(fis);
        workbook = Workbook.createWorkbook(file, wb);
        return this;
    }

    /**
     * 创建sheet
     * @param name
     * @return
     */
    public ExcelUtil createSheet(String name) {
        checkNullFirst();
        writableSheet = workbook.createSheet(name, 0);
        return this;
    }

    /**
     * 打开sheet
     * @param position
     * @param name
     * @return
     */
    public ExcelUtil openSheet(int position, String name) {
        checkNullFirst();
        writableSheet = workbook.getSheet(name);
        return this;
    }

    /**
     * 释放资源
     * @return
     */
    public ExcelUtil close() {
        checkNullFirst();
        try {
            workbook.write();
            workbook.close();
            workbook = null;

            writableSheet = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JxlWriteException e1) {

        } catch (WriteException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 为空判断
     */
    private void checkNullFirst() {
        if (workbook == null) {
            throw new NullPointerException("writableWorkbook is null, please invoke the #createExcel(String, String) method or the #openExcel(File) method first.");
        }
    }
}
