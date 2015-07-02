/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googleapi;

import auth.Auth;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author konst
 */
public class InsertFile {

    public static void main(String[] args) throws Exception {

        Drive service = Auth.getDriveService();

        //crateNewSpreadSheet(service, "ololo", "destcr", null, "application/vnd.google-apps.spreadsheet");
    
    insertFile(service, "ex", "destcr", null, "application/vnd.ms-excel", "ex.xlsx");
    }

    private static File crateNewSpreadSheet(Drive service, String title, String description,
            String parentId, String mimeType) {

        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);
        

        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }

        try {

            File file = service.files().insert(body).execute();

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: " + file.getId());
            return file;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

    private static File insertFile(Drive service, String title, String description,
            String parentId, String mimeType, String filename) {
        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);

        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }

        // File's content.
        java.io.File fileContent = new java.io.File(filename);
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        try {
            File file = service.files().insert(body, mediaContent).execute();

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: " + file.getId());
            return file;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

}
