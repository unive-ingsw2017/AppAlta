package com.progetto.ingegneria.appalta.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.progetto.ingegneria.appalta.R;


public class MenuActivity extends AppCompatActivity {

    private CheckBox checkNormale,checkIbrida,checkSatellite,checkTerreno;
    private EditText editnMarkers;
    private int preferenceType, preferencesMarkers;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "MyPrefs" ;
    private static final String Type = "type";
    private static final String Markers = "nMarkers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        checkNormale = (CheckBox) findViewById(R.id.checkBoxNormal);
        checkIbrida = (CheckBox) findViewById(R.id.checkBoxHybrid);
        checkSatellite = (CheckBox) findViewById(R.id.checkBoxSatellite);
        checkTerreno = (CheckBox) findViewById(R.id.checkBoxTerrain);
        editnMarkers = (EditText) findViewById(R.id.editnMarkers);

        if(savedInstanceState == null) {
            setCheckBox(checkNormale);
            preferenceType = GoogleMap.MAP_TYPE_NORMAL;
        }

        checkNormale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceType = GoogleMap.MAP_TYPE_NORMAL;
                setCheckBox(checkNormale);
                Log.d("MenuActivity onCreate()", "selezionata mappa normale: "+preferenceType);
            }
        });
        checkIbrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceType = GoogleMap.MAP_TYPE_HYBRID;
                setCheckBox(checkIbrida);
                Log.d("MenuActivity onCreate()", "selezionata mappa ibrida: "+preferenceType);
            }
        });
        checkSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceType = GoogleMap.MAP_TYPE_SATELLITE;
                setCheckBox(checkSatellite);
                Log.d("MenuActivity onCreate()", "selezionata mappa satellite: "+preferenceType);
            }
        });
        checkTerreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceType = GoogleMap.MAP_TYPE_TERRAIN;
                setCheckBox(checkTerreno);
                Log.d("MenuActivity onCreate(", "selezionata mappa terreno: "+preferenceType);
            }
        });

    }

    @Override
     protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    private void savePreferences(){
        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if(!String.valueOf(editnMarkers.getText()).equalsIgnoreCase(""))
                preferencesMarkers = Integer.parseInt(String.valueOf(editnMarkers.getText()));
            else
                preferencesMarkers = 0;
            editor.putInt(Type, preferenceType);
            editor.putInt(Markers, preferencesMarkers);
            editor.apply();
            Log.d("MenuAcivity savePreferenes()", "eseguita - mappa salvata: "+preferenceType);
            Log.d("MenuAcivity savePreferenes()", "eseguita - nMarkers salvato: "+preferencesMarkers);
        }
        catch(Exception e){
            Log.e("MenuActivity savePreferenes()", e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadPreferences(){
        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (sharedpreferences.contains(Type))
                switch (sharedpreferences.getInt(Type, GoogleMap.MAP_TYPE_NORMAL)) {
                    case GoogleMap.MAP_TYPE_NORMAL:
                        preferenceType = GoogleMap.MAP_TYPE_NORMAL;
                        setCheckBox(checkNormale);
                        break;
                    case GoogleMap.MAP_TYPE_SATELLITE:
                        preferenceType = GoogleMap.MAP_TYPE_SATELLITE;
                        setCheckBox(checkSatellite);
                        break;
                    case GoogleMap.MAP_TYPE_HYBRID:
                        preferenceType = GoogleMap.MAP_TYPE_HYBRID;
                        setCheckBox(checkIbrida);
                        break;
                    case GoogleMap.MAP_TYPE_TERRAIN:
                        preferenceType = GoogleMap.MAP_TYPE_TERRAIN;
                        setCheckBox(checkTerreno);
                        break;
                }
            if (sharedpreferences.contains(Markers)) {
                preferencesMarkers = sharedpreferences.getInt(Markers, 10);
                editnMarkers.setText(Integer.toString(preferencesMarkers));
            }
            Log.d("MenuActivity loadPreferences()", "eseguita - mappa caricata: "+preferenceType);
            Log.d("MenuActivity loadPreferences()", "eseguita - nMarkers caricato: "+preferencesMarkers);
        }
        catch(Exception e){
            Log.e("MenuActivity loadPreferences()", e.getMessage());
        }
    }

    private void setCheckBox(CheckBox enable){
        checkNormale.setChecked(false);
        checkIbrida.setChecked(false);
        checkSatellite.setChecked(false);
        checkTerreno.setChecked(false);
        enable.setChecked(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putInt(Type, preferenceType);
            Log.d("MenuActivity onSaveInstanceState()", "stato salvato");
        }
        catch (Exception e){
            Log.e("MenuActivity onSaveInstanceState()", e.getMessage());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            preferenceType = savedInstanceState.getInt(Type);
            Log.d("MenuActivity onRestoreInstanceState()", "stato caricato");
        }
        catch (Exception e){
            Log.e("MenuActivity onRestoreInstanceState()", e.getMessage());
        }
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
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                savePreferences();
                NavUtils.navigateUpTo(this, getIntent());
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
