package com.example.android.smartfridgeapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: CSVReader
 * Description: The class reads external file, process it and store data into array list.
 * @author: Deniss Timofejevs B00066599
 */

public class CSVReader {
    InputStream inputStream;

    public CSVReader(InputStream is) {

        this.inputStream = is;
    }
        /*reading external file and fill array list with product barcodes and their names*/
    public List<ExternalFile> read() {

        List<ExternalFile> resultList = new ArrayList<ExternalFile>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");

                ExternalFile exFile = new ExternalFile();
                exFile.setBarcode(row[0]);
                exFile.setName(row[6]);
                resultList.add(exFile);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file:" + ex);
        } finally {

            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return resultList;
    }
}
