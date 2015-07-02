package sheet;

import auth.Auth;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.security.sasl.AuthenticationException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author konst
 */
public class Sheets {

    public static void main(String[] a) throws IOException, MalformedURLException, ServiceException, Exception {

       addSpreadSheet();
    }

    public static void addSpreadSheet() throws MalformedURLException, IOException, ServiceException, Exception {

       
    }

    public static void getSpreadSheest()
            throws AuthenticationException, MalformedURLException, IOException, ServiceException, Exception {

        SpreadsheetService service
                = new SpreadsheetService("MySpreadsheetIntegration-v1");

        service.setOAuth2Credentials(Auth.authorize());

        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
        List<com.google.gdata.data.spreadsheet.SpreadsheetEntry> spreadsheets = feed.getEntries();

        System.out.println("title: " + spreadsheets.get(0).getTitle().getPlainText());
    }

    public static void addWorkSheet() throws MalformedURLException, Exception {

        SpreadsheetService service
                = new SpreadsheetService("Report");

        service.setOAuth2Credentials(Auth.authorize());

        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

        if (spreadsheets.isEmpty()) {
            // TODO: There were no spreadsheets, act accordingly.
        }

        // TODO: Choose a spreadsheet more intelligently based on your
        // app's needs.
        SpreadsheetEntry spreadsheet = spreadsheets.get(0);
        System.out.println(spreadsheet.getTitle().getPlainText());

        WorksheetFeed f = new WorksheetFeed();
        f.setEntries(null);
        
        
        // Create a local representation of the new worksheet.
        WorksheetEntry worksheet = new WorksheetEntry();
        worksheet.setTitle(new PlainTextConstruct("New Worksheet"));

        worksheet.setColCount(10);
        worksheet.setRowCount(20);
        

        // Send the local representation of the worksheet to the API for
        // creation.  The URL to use here is the worksheet feed URL of our
        // spreadsheet.
        URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
        service.insert(worksheetFeedUrl, worksheet);
    }

}
