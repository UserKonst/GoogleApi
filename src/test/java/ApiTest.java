
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import excel.ExcelDoc;
import java.util.List;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;
import spreadsheetApi.BatchCellUpdater;
import spreadsheetApi.GoogleTable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author konst
 */
public class ApiTest {

    public ApiTest() {
    }

    String currentSheetName;
    WorksheetEntry currentWorksheet;
    int rowsCount;
    List<String> importData;

    @Test
    public void getCurrentSheet() {
        currentSheetName = "2015-07-08";//new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println("Current sheet: " + currentSheetName);
    }

    @Test(dependsOnMethods = "getCurrentSheet")
    public void getValuesFromSheet() {
        ExcelDoc doc = new ExcelDoc();
        importData = doc.getValuesFromSheet(currentSheetName);
        rowsCount = doc.getRowsCount();

        System.out.println("values: \n" + importData);
        assertNotNull(importData, "Values not found is sheet " + currentSheetName);
    }

    @Test(dependsOnMethods = "getValuesFromSheet")
    public void importDataToGoogle() throws Exception {

        GoogleTable google = new GoogleTable();
        String spreadsheetName = "ex";

        SpreadsheetEntry sp = google.findSpreadsheet(spreadsheetName);
        currentWorksheet = google.findWorksheet(sp, currentSheetName);

        if (currentWorksheet == null) {
            google.addWorksheet(sp, currentSheetName);
        }
        currentWorksheet = google.findWorksheet(sp, currentSheetName);

        BatchCellUpdater updater = new BatchCellUpdater(rowsCount);
        updater.updateSheet(currentWorksheet, importData);

    }

}
