package com.progetto.ingegneria.appalta.Classes;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by matteo on 06/10/2017.
 */
public class JsonManager {

    private Context context;

    public JsonManager(Context appContext) {
        this.context = appContext;
    }

    public void writeToFile(String data, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.d("JsonManafer writeToFile()",filename+" salvato");
        }
        catch (IOException e) {
            Log.e("JsonManafer writeToFile()", "File write failed: " + e.toString());
        }
    }


    public String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
            Log.d("JsonManafer readFromFile()",filename+" caricato");
        }
        catch (FileNotFoundException e) {
            Log.e("JsonManafer readFromFile()", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("JsonManafer readFromFile()", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
