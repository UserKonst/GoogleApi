/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googleapi;

import auth.Auth;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author konst
 */
public class DriveQuickstart {

    

    public static void main(String[] args) throws IOException, Exception {
        // Build a new authorized API client service.
        Drive service = Auth.getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setMaxResults(10)
                .execute();
        List<File> files = result.getItems();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            }
        }
    }

}
