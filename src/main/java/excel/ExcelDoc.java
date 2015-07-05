/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 *
 * @author konst
 */
public class ExcelDoc {

    String path = "ex.xls";
    private int rowsCount;

    public int getRowsCount() {
        return rowsCount;
    }

    public List<String> getValues() {

        List<String> values = new ArrayList<>();
        Workbook book = null;
        try (FileInputStream stream = new FileInputStream(path)) {

            book = Workbook.getWorkbook(stream);
            Sheet sheet = book.getSheet(0);

            int rows = sheet.getRows();

            for (int i = 0; i < rows; i++) {
                Cell[] cells = sheet.getRow(i);
                for (Cell cell : cells) {
                    values.add(cell.getContents());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            book.close();
        }
        return values;
    }

    public List<String> getValuesFromSheet(String currentSheet) {

        List<String> values = new ArrayList<>();
        Workbook book = null;
        try (FileInputStream stream = new FileInputStream(path)) {

            book = Workbook.getWorkbook(stream);
            Sheet sheet = book.getSheet(currentSheet);

            if (sheet == null) {
                System.out.println("Sheet " + currentSheet + " not found");
                return null;
            }

            int rows = sheet.getRows();
            rowsCount = rows;
            
            for (int i = 0; i < rows; i++) {
                Cell[] cells = sheet.getRow(i);
                for (Cell cell : cells) {
                    values.add(cell.getContents());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            book.close();
        }
        return values;

    }

//    public int getRowsCount(String currentSheet) {
//
//        int rowsCount;
//        Workbook book = null;
//        try (FileInputStream stream = new FileInputStream(path)) {
//
//            book = Workbook.getWorkbook(stream);
//            Sheet sheet = book.getSheet(currentSheet);
//
//            if (sheet == null) {
//                System.out.println("Sheet " + currentSheet + " not found");
//                return 0;
//            }
//
//            int rows = sheet.getRows();
//
//            for (int i = 1; i < rows; i++) {
//                Cell[] cells = sheet.getRow(i);
//                for (Cell cell : cells) {
//                    values.add(cell.getContents());
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            book.close();
//        }
//        return rowsCount;
//
//    }
}
