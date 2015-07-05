/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spreadsheetApi;

import auth.Auth;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthenticationException;

/**
 *
 * @author konst
 */
public class GoogleTable {

    private SpreadsheetService service;
    private final String feedURL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";

    public GoogleTable() {
        System.out.println("op");
        this.service = createService();
    }

    private SpreadsheetService createService() {

        SpreadsheetService svc = null;

        try {
            svc = new SpreadsheetService("Report");

            svc.setOAuth2Credentials(Auth.authorize());
        } catch (Exception ex) {
            Logger.getLogger(GoogleTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return svc;
    }

    public List<SpreadsheetEntry> getAllSpreadsheets() throws Exception {

        //=====================================================================
        URL SPREADSHEET_FEED_URL = new URL(feedURL);

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        //=====================================================================

        List<SpreadsheetEntry> spreadsheets = feed.getEntries();
        if (spreadsheets.isEmpty()) {

            System.out.println("Spreadsheets not found!");
            return null;
        }

        List<String> spreadsheetsNames = new ArrayList<>();
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            String name = spreadsheet.getTitle().getPlainText();
            spreadsheetsNames.add(name);
        }

        System.out.println("Found spreadsheets: " + spreadsheetsNames);
        return spreadsheets;
    }

    public SpreadsheetEntry findSpreadsheet(String name) throws Exception {
        //=====================================================================
//        SpreadsheetService service
//                = new SpreadsheetService("Report");
//
//        service.setOAuth2Credentials(Auth.authorize());

        URL SPREADSHEET_FEED_URL = new URL(feedURL);

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        //=====================================================================

        List<SpreadsheetEntry> entries = feed.getEntries();

        for (SpreadsheetEntry entry : entries) {
            String entryName = entry.getTitle().getPlainText();

            if (entryName.equalsIgnoreCase(name)) {
                System.out.println("Found spreadsheet wiht name '" + name + "'!");
                return entry;
            }
        }
        System.out.println("Entry with name '" + name + "' not found!");
        return null;
    }

    //==========================================================================
    //
    //==========================================================================
    //==========================================================================
    //
    //==========================================================================
    public void getSpreadSheest()
            throws AuthenticationException, MalformedURLException, IOException, ServiceException, Exception {

//        SpreadsheetService service
//                = new SpreadsheetService("MySpreadsheetIntegration-v1");
//
//        service.setOAuth2Credentials(Auth.authorize());
        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL(feedURL);

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
        List<com.google.gdata.data.spreadsheet.SpreadsheetEntry> spreadsheets = feed.getEntries();

        System.out.println("title: " + spreadsheets.get(0).getTitle().getPlainText());
    }

    public List<WorksheetEntry> findWorksheets(SpreadsheetEntry spreadsheet) throws Exception {

        if (spreadsheet == null) {
            System.out.println("Spreadsheet is null!");
            return null;
        }

        List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();
        if (worksheets.isEmpty()) {
            System.out.println("Spreadsheet '" + spreadsheet.getTitle().getPlainText() + "' is empty!");
            return null;
        }

        List<String> names = new ArrayList<>();
        for (WorksheetEntry worksheet : worksheets) {
            names.add(worksheet.getTitle().getPlainText());
        }

        System.out.println("Found worksheets: " + names);
        return worksheets;
    }

    public WorksheetEntry findWorksheet(SpreadsheetEntry sp, String sheetName) throws Exception {

        List<WorksheetEntry> entries = sp.getWorksheets();

        for (WorksheetEntry entry : entries) {

            String name = entry.getTitle().getPlainText();
            if (name.equalsIgnoreCase(sheetName)) {
                System.out.println("Found worksheet with name '" + sheetName + "'");
                return entry;
            }
        }

        System.out.println(
                "Worksheet '" + sheetName + "' not found in spreadsheet '"
                + sp.getTitle().getPlainText() + "'");
        return null;
    }

    public WorksheetEntry addWorksheet(SpreadsheetEntry sp, String sheetName) throws Exception {

        if (findWorksheet(sp, sheetName) != null) {
            System.out.println("Worksheet with name '" + sheetName + "' allready exists!");
            return null;
        }

        WorksheetEntry worksheet = new WorksheetEntry();
        worksheet.setTitle(new PlainTextConstruct(sheetName));
        worksheet.setColCount(11);
        worksheet.setRowCount(200);

        URL worksheetFeedUrl = sp.getWorksheetFeedUrl();

        service.insert(worksheetFeedUrl, worksheet);
        Thread.sleep(5000);
        
        System.out.println("Added worksheet with name '" + sheetName + "'");
        return worksheet;
    }

    public boolean deleteWorksheet(SpreadsheetEntry sp, String sheetName) throws Exception {

        if (findWorksheet(sp, sheetName) == null) {
            System.out.println(
                    "Worksheet '" + sheetName + "' not found in spreadsheet '"
                    + sp.getTitle().getPlainText() + "'");

            return false;
        }

        WorksheetEntry worksheet = findWorksheet(sp, sheetName);
        worksheet.delete();

        System.out.println("Sheet '" + sheetName + "' was deleted from spreadsheet '" + sp.getTitle().getPlainText() + "'!");
        return true;
    }

}
