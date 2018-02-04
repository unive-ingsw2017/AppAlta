package com.progetto.ingegneria.appalta.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.progetto.ingegneria.appalta.R;
import com.progetto.ingegneria.appalta.Threads.DataSaver;

import java.io.File;

public class InitActivity extends AppCompatActivity {

    DataSaver saver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        Button start = (Button) findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(InitActivity.this,StartActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch(Exception e){
                    Log.e("Init: onClick", e.getMessage());
                    final Toast toast = Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        try{
            File file = getApplicationContext().getFileStreamPath("appalti_file.txt");
            if(!file.exists()){
                saver = new DataSaver(InitActivity.this, getApplicationContext(), true);
                saver.execute();
            }
        }
        catch(Exception e){
            Log.e("Init:", e.getMessage());
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
        switch (item.getItemId()) {
            default: break;
        }

        return super.onOptionsItemSelected(item);
    }


}
