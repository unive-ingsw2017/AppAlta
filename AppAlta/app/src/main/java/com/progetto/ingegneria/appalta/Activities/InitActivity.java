package com.progetto.ingegneria.appalta.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.progetto.ingegneria.appalta.R;
import com.progetto.ingegneria.appalta.Threads.DataSaver;

import java.io.File;

public class InitActivity extends Activity {

    DataSaver saver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        try{
            File file = getApplicationContext().getFileStreamPath("appalti_file.txt");
            if(!file.exists()){
                saver = new DataSaver(InitActivity.this, getApplicationContext(), true);
                saver.execute();
                while(saver.getStatus() == AsyncTask.Status.RUNNING)
                    wait();
            }
            else{
                String value=null;
                //long Filesize=getFolderSize(file);    //servirà per vedere se ci sono cambiamenti sui file
            }
        }
        catch(Exception e){
            Log.e("Init:", e.getMessage());
        }
        finally{
            Intent intent = new Intent(InitActivity.this,StartActivity.class);
            startActivity(intent);
        }
    }

    public static long getFolderSize(File f) {  //return bytes
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.start_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


}
