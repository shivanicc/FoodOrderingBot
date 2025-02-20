package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

public class SheetsServiceUtil {
    private static final String APPLICATION_NAME = "_";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        // Use a relative path since your JSON file is directly in the project directory
        String credentialsFilePath = "GOOGLE_SHEETS_CREDENTIALS";
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsFilePath))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
