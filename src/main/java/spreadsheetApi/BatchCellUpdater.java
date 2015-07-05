/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spreadsheetApi;

import auth.Auth;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author konst
 */
public class BatchCellUpdater {

    /**
     * The number of rows to fill in the destination workbook
     */
    private static int MAX_ROWS;

    /**
     * The number of columns to fill in the destination workbook
     */
    private static final int MAX_COLS = 11;

    public BatchCellUpdater(int rows) {
        MAX_ROWS = rows;
    }

    /**
     * A basic struct to store cell row/column information and the associated
     * RnCn identifier.
     */
    private static class CellAddress {

        public final int row;
        public final int col;
        public final String idString;

        /**
         * Constructs a CellAddress representing the specified {@code row} and
         * {@code col}. The idString will be set in 'RnCn' notation.
         */
        public CellAddress(int row, int col) {
            this.row = row;
            this.col = col;
            this.idString = String.format("R%sC%s", row, col);
        }
    }

    public void updateSheet(WorksheetEntry sheet, List<String> importData)
            throws AuthenticationException, MalformedURLException, IOException, ServiceException, Exception {

        long startTime = System.currentTimeMillis();

        // Prepare Spreadsheet Service
        SpreadsheetService service = new SpreadsheetService("Report");
        service.setOAuth2Credentials(Auth.authorize());

        URL cellFeedUrl = sheet.getCellFeedUrl();
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        // Build list of cell addresses to be filled in
        List<CellAddress> cellAddrs = new ArrayList<>();
        for (int row = 1; row <= MAX_ROWS; ++row) {
            for (int col = 1; col <= MAX_COLS; ++col) {
                cellAddrs.add(new CellAddress(row, col));
            }
        }

        // Prepare the update
        // getCellEntryMap is what makes the update fast.
        Map<String, CellEntry> cellEntries = getCellEntryMap(service, cellFeedUrl, cellAddrs);
        CellFeed batchRequest = new CellFeed();

        Iterator<String> it = importData.iterator();

        for (CellAddress cellAddr : cellAddrs) {
            CellEntry batchEntry = new CellEntry(cellEntries.get(cellAddr.idString));
            batchEntry.changeInputValueLocal(it.next());
            BatchUtils.setBatchId(batchEntry, cellAddr.idString);
            BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.UPDATE);
            batchRequest.getEntries().add(batchEntry);
        }

        // Submit the update
        Link batchLink = cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
        CellFeed batchResponse = service.batch(new URL(batchLink.getHref()), batchRequest);

        // Check the results
        boolean isSuccess = true;
        for (CellEntry entry : batchResponse.getEntries()) {
            String batchId = BatchUtils.getBatchId(entry);
            if (!BatchUtils.isSuccess(entry)) {
                isSuccess = false;
                BatchStatus status = BatchUtils.getBatchStatus(entry);
                //   System.out.printf("%s failed (%s) %s", batchId, status.getReason(), status.getContent());
            }
        }

        System.out.println(isSuccess ? "\nBatch operations successful." : "\nBatch operations failed");
        System.out.printf("\n%s ms elapsed\n", System.currentTimeMillis() - startTime);
    }

    /**
     * Connects to the specified {@link SpreadsheetService} and uses a batch
     * request to retrieve a {@link CellEntry} for each cell enumerated in {@code
     * cellAddrs}. Each cell entry is placed into a map keyed by its RnCn
     * identifier.
     *
     * @param ssSvc the spreadsheet service to use.
     * @param cellFeedUrl url of the cell feed.
     * @param cellAddrs list of cell addresses to be retrieved.
     * @return a map consisting of one {@link CellEntry} for each address in {@code
     *         cellAddrs}
     */
    public static Map<String, CellEntry> getCellEntryMap(
            SpreadsheetService ssSvc, URL cellFeedUrl, List<CellAddress> cellAddrs)
            throws IOException, ServiceException {
        CellFeed batchRequest = new CellFeed();
        for (CellAddress cellId : cellAddrs) {
            CellEntry batchEntry = new CellEntry(cellId.row, cellId.col, cellId.idString);
            batchEntry.setId(String.format("%s/%s", cellFeedUrl.toString(), cellId.idString));
            BatchUtils.setBatchId(batchEntry, cellId.idString);
            BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.QUERY);
            batchRequest.getEntries().add(batchEntry);
        }

        CellFeed cellFeed = ssSvc.getFeed(cellFeedUrl, CellFeed.class);
        CellFeed queryBatchResponse
                = ssSvc.batch(new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref()),
                        batchRequest);

        Map<String, CellEntry> cellEntryMap = new HashMap<String, CellEntry>(cellAddrs.size());
        for (CellEntry entry : queryBatchResponse.getEntries()) {
            cellEntryMap.put(BatchUtils.getBatchId(entry), entry);
            // System.out.printf("batch %s {CellEntry: id=%s editLink=%s inputValue=%s\n",
//                    BatchUtils.getBatchId(entry), entry.getId(), entry.getEditLink().getHref(),
//                    entry.getCell().getInputValue());
        }

        return cellEntryMap;
    }
}
