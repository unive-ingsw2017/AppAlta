package com.progetto.ingegneria.appalta.Classes;

/**
 * Created by matteo on 13/12/2017.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    InputStream inputStream;

    public CSVReader(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List<String[]> read(){
        List<String[]> resultList = new ArrayList<String[]>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                csvLine = csvLine.replaceAll("��","� �");
                String[] row = csvLine.split("�");
                resultList.add(row);
                //Log.d("CSVReader, Letta:", row[0]);
            }
        }
        catch (IOException ex) {
            Log.d("CSVReader", "Error in reading CSV file: "+ex.getMessage());
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                Log.d("CSVReader", "Error while closing input stream: "+e.getMessage());
            }
        }
        return resultList;
    }
}
