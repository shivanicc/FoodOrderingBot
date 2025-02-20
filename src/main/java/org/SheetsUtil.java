package org.example;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsUtil {
    private static final String SPREADSHEET_ID = "GOOGLE_SHEET_ID";
    private static final String RANGE = "Sheet1!A1";  // Adjust range as needed

    public static void appendOrderData(List<Object> rowData) throws IOException, GeneralSecurityException {
        Sheets service = SheetsServiceUtil.getSheetsService();
        ValueRange body = new ValueRange().setValues(Arrays.asList(rowData));
        AppendValuesResponse result = service.spreadsheets().values()
                .append(SPREADSHEET_ID, RANGE, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
    }
}
