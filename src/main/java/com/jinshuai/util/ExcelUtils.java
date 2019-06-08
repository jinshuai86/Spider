package com.jinshuai.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: JS
 * @date: 2018/11/9
 * @description: handle excel
 */
@Slf4j
public class ExcelUtils implements OfficeUtils<String> {
    @Override
    public List<String> read() {
        List<String> result = new ArrayList<>();
        try {
            Workbook wb = WorkbookFactory.create(new File("F:/XXX.xls"));
            wb.close();
            Sheet sheet = wb.getSheetAt(0);
            for(Iterator rowIterator = sheet.rowIterator(); rowIterator.hasNext();) {
                Row row = (Row)rowIterator.next();
                Cell cell = row.getCell(1);
                result.add(cell.getStringCellValue());
            }
        } catch (IOException | InvalidFormatException e ) {
            log.error("读取excel出错，检查路径是否正确、行列号是否越界",e);
        }
        return result;
    }

    @Override
    @Deprecated
    public void write() {
    }

    public static void main(String[] args) {
        List<String> list = new ExcelUtils().read();
        list.forEach(System.out::println);
    }

}