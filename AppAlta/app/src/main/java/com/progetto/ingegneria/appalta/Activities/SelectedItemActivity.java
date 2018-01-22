package com.progetto.ingegneria.appalta.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class SelectedItemActivity extends ActionBarActivity {

    private Appalto app;
    private TextView text_cig, text_titolo, text_tipo, text_anno, text_aggiudicatario, text_cod_fisc, text_importo, text_data_inizio, text_responsabile, text_citta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_mark);

        text_cig = (TextView) findViewById(R.id.textCig);
        text_titolo = (TextView) findViewById(R.id.textTitolo);
        text_tipo = (TextView) findViewById(R.id.textTipo);
        text_anno = (TextView) findViewById(R.id.textAnno);
        text_aggiudicatario = (TextView) findViewById(R.id.textAggiudicatario);
        text_cod_fisc = (TextView) findViewById(R.id.textCodFisc);
        text_importo = (TextView) findViewById(R.id.textImporto);
        text_data_inizio = (TextView) findViewById(R.id.textDataInizio);
        text_responsabile = (TextView) findViewById(R.id.textResponsabile);
        text_citta = (TextView) findViewById(R.id.textCitta);

        Bundle dati = getIntent().getExtras();
        if (dati != null) {
            String titolo = dati.getString("titolo");
            load(titolo);
            setTextView();
        }
    }

    void setTextView(){

        checkTextData(text_cig, app.getCig());
        checkTextData(text_titolo, app.getTitolo());
        checkTextData(text_tipo, app.getTipo());
        checkTextData(text_anno, app.getAnno());
        checkTextData(text_aggiudicatario, app.getAggiudicatario());
        checkTextData(text_cod_fisc, app.getCod_fiscale_iva());
        checkTextData(text_importo, app.getImporto_aggiudicazione().concat("€"));
        checkTextData(text_data_inizio, app.getDataInizio());
        checkTextData(text_responsabile, app.getResponsabile());
        checkTextData(text_citta, app.getCitta());

        setTitle(R.string.element_selection);
    }

    private void checkTextData(TextView t, String s){
        if(!TextUtils.isEmpty(s))
            t.setText(s);
        else
            t.setText(R.string.null_data);
    }

    private void load(String titolo){
        try {
            app = new Appalto();
            JsonManager manager = new JsonManager(getApplicationContext());
            JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string
            for (int i = 0; i < jArray.length(); i++) {
                //DATI
                JSONObject jObject = jArray.getJSONObject(i);

                if(jObject.getString("titolo").equalsIgnoreCase(titolo)) {
                    app.setCig(jObject.getString("cig"));
                    app.setTitolo(jObject.getString("titolo"));
                    app.setTipo(jObject.getString("tipo"));
                    app.setAnno(jObject.getString("anno"));
                    app.setAggiudicatario(jObject.getString("aggiudicatario"));
                    app.setCod_fiscale_iva(jObject.getString("cod_fiscale_iva"));
                    app.setImporto_aggiudicazione(jObject.getString("importo_aggiudicazione"));
                    app.setDataInizio(jObject.getString("dataInizio"));
                    app.setResponsabile(jObject.getString("responsabile"));
                    app.setCitta(jObject.getString("citta"));
                    JSONObject coordinate = jObject.getJSONObject("coordinate");
                    app.setCoordinate(new LatLng(Double.parseDouble(coordinate.getString("latitude")), Double.parseDouble(coordinate.getString("longitude"))));
                }
                //Log.d("FileDownloader", "Decompresso appalto: " + app.getCig()());
            } // End Loop
        }
        catch (Exception e){
            Log.e("SelectedMarker load", e.getMessage());
            final Toast toast = Toast.makeText(getApplicationContext(),"Errore nei Dati",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString("cig", app.getCig());
        saveInstanceState.putString("titolo", app.getTitolo());
        saveInstanceState.putString("tipo", app.getTipo());
        saveInstanceState.putString("anno", app.getAnno());
        saveInstanceState.putString("aggiudicatario", app.getAggiudicatario());
        saveInstanceState.putString("cod_fiscale_iva", app.getCod_fiscale_iva());
        saveInstanceState.putString("importo_aggiudicazione", app.getImporto_aggiudicazione());
        saveInstanceState.putString("dataInizio", app.getDataInizio());
        saveInstanceState.putString("responsabile", app.getResponsabile());
        saveInstanceState.putString("citta", app.getCitta());
        saveInstanceState.putDouble("latitudine", app.getCoordinate().latitude);
        saveInstanceState.putDouble("longitude", app.getCoordinate().longitude);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LatLng coordinate = new LatLng(savedInstanceState.getDouble("latitudine"),savedInstanceState.getDouble("longitudine"));
        app = new Appalto();
        app.setCig(savedInstanceState.getString("cig"));
        app.setTitolo(savedInstanceState.getString("titolo"));
        app.setTipo(savedInstanceState.getString("tipo"));
        app.setAnno(savedInstanceState.getString("anno"));
        app.setAggiudicatario(savedInstanceState.getString("aggiudicatario"));
        app.setCod_fiscale_iva(savedInstanceState.getString("cod_fiscale_iva"));
        app.setImporto_aggiudicazione(savedInstanceState.getString("importo_aggiudicazione"));
        app.setDataInizio(savedInstanceState.getString("dataInizio"));
        app.setResponsabile(savedInstanceState.getString("responsabile"));
        app.setCitta(savedInstanceState.getString("citta"));
        app.setCoordinate(coordinate);
        setTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_selected_mark, menu);
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
                NavUtils.navigateUpTo(this, getIntent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
