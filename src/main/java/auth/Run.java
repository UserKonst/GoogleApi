/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auth;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import java.util.List;
import spreadsheetApi.GoogleTable;

/**
 *
 * @author konst
 */
public class Run {

    public static void main(String[] args) throws Exception {

        GoogleTable google = new GoogleTable();
        String spreadsheetName = "ex";

        List<SpreadsheetEntry> entries = google.getAllSpreadsheets();
        SpreadsheetEntry sp = google.findSpreadsheet(spreadsheetName);
        
        google.findWorksheets(sp);
        google.findWorsheet(sp,"Лист1");
        
        google.addWorksheet(sp, "new list");
        google.addWorksheet(sp, "sdf");
        
        google.deleteWorksheet(sp,"sdf");
        

    }

}
