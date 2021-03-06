package com.progetto.ingegneria.appalta.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.MovableFloatingActionButton;
import com.progetto.ingegneria.appalta.R;
import com.progetto.ingegneria.appalta.Threads.DataSaver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private AutoCompleteTextView textTitolo, textAzienda, textCodFiscIva;
    private EditText textImportoMin, textImportoMax;
    private Spinner spinnerAnno, spinnerTipo, spinnerCitta;
    private String inserted_titolo, inserted_azienda, inserted_importoMin, inserted_importoMax, inserted_anno, inserted_tipo, inserted_citta, inserted_cod;
    private List<String> titoli, aziende, cod, anni, tipi, citta;
    private int posAnno, posTipo, posCitta;
    private ArrayAdapter<String> dataAdapterTitolo, dataAdapterAzienda, dataAdapterCodFiscIva, dataAdapterTipo, dataAdapterAnno, dataAdapterCitta;
    private DataSaver saver;
    private String hidden_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupUI(findViewById(R.id.coordinator));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saver = new DataSaver(StartActivity.this, getApplicationContext());

        Button search = (Button) findViewById(R.id.button);
        LinearLayout layoutCity = (LinearLayout) findViewById(R.id.layout_city);
        textTitolo = (AutoCompleteTextView) findViewById(R.id.titolo);
        textAzienda = (AutoCompleteTextView) findViewById(R.id.company);
        textCodFiscIva = (AutoCompleteTextView) findViewById(R.id.codFisc);
        textImportoMin = (EditText) findViewById(R.id.importoMin);
        textImportoMax = (EditText) findViewById(R.id.importoMax);
        spinnerAnno = (Spinner) findViewById(R.id.anno);
        spinnerTipo = (Spinner) findViewById(R.id.tipo);
        spinnerCitta = (Spinner) findViewById(R.id.citta);

        MovableFloatingActionButton fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        CoordinatorLayout.LayoutParams lp  = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fab.setCoordinatorLayout(lp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,ListActivity.class);
                inserted_titolo = textTitolo.getText().toString();
                inserted_azienda = textAzienda.getText().toString();
                inserted_cod = textCodFiscIva.getText().toString();
                inserted_importoMin = textImportoMin.getText().toString();
                inserted_importoMax = textImportoMax.getText().toString();
                inserted_anno = anni.get(posAnno);
                inserted_tipo = tipi.get(posTipo);
                if(TextUtils.isEmpty(hidden_city))
                    inserted_citta = citta.get(posCitta);
                else
                    inserted_citta = hidden_city;
                if(inserted_anno.equalsIgnoreCase("tutti"))
                    inserted_anno="";
                if(inserted_tipo.equalsIgnoreCase("tutti"))
                    inserted_tipo="";
                if(inserted_citta.equalsIgnoreCase("tutte"))
                    inserted_citta="";
                intent.putExtra("inserted_titolo", inserted_titolo);
                intent.putExtra("inserted_azienda", inserted_azienda);
                intent.putExtra("inserted_cod", inserted_cod);
                intent.putExtra("inserted_importoMin", inserted_importoMin);
                intent.putExtra("inserted_importoMax", inserted_importoMax);
                intent.putExtra("inserted_anno", inserted_anno);
                intent.putExtra("inserted_tipo", inserted_tipo);
                intent.putExtra("inserted_citta", inserted_citta);
                startActivity(intent);
            }
        });

        Bundle dati = getIntent().getExtras();
        if (dati != null) {
            hidden_city = dati.getString("inserted_citta");
            if(!TextUtils.isEmpty(hidden_city)) {
                layoutCity.setVisibility(LinearLayout.GONE);
                fab.setVisibility(FloatingActionButton.GONE);
                final ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        else
            hidden_city = "";

        //init arraylist
        titoli = new ArrayList<>();
        tipi = new ArrayList<>();
        cod =new ArrayList<>();
        anni = new ArrayList<>();
        aziende = new ArrayList<>();
        citta = new ArrayList<>();


        //AutoCompleteTextView
        dataAdapterTitolo = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, titoli);
        dataAdapterAzienda = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, aziende);
        dataAdapterCodFiscIva = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, cod);
        textTitolo.setAdapter(dataAdapterTitolo);
        textAzienda.setAdapter(dataAdapterAzienda);
        textCodFiscIva.setAdapter(dataAdapterCodFiscIva);


        //Spinners
        dataAdapterTipo = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, tipi);
        dataAdapterAnno = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, anni);
        dataAdapterCitta = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, citta);
        addItemsOnSpinner(dataAdapterTipo, spinnerTipo);
        addItemsOnSpinner(dataAdapterAnno, spinnerAnno);
        addItemsOnSpinner(dataAdapterCitta, spinnerCitta);
        addListenerOnSpinnerItemSelection();

        load();
    }

    private void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText) && !(view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(StartActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void addItemsOnSpinner(ArrayAdapter<String> dataAdapter, Spinner spinner) {
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void addListenerOnSpinnerItemSelection() {
        spinnerAnno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posAnno = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posTipo = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCitta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posCitta = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setWidgets(){
        textTitolo.setText(inserted_titolo);
        textAzienda.setText(inserted_azienda);
        textCodFiscIva.setText(inserted_cod);
        textImportoMax.setText(inserted_importoMax);
        textImportoMin.setText(inserted_importoMin);
        spinnerCitta.setSelection(posCitta);
        spinnerAnno.setSelection(posAnno);
        spinnerTipo.setSelection(posTipo);
    }

    private void load(){
        try {
            JsonManager manager = new JsonManager(getApplicationContext());
            JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string
            for (int i = 0; i < jArray.length(); i++) {
                //DATI
                    JSONObject jObject = jArray.getJSONObject(i);
                    if(!titoli.contains(jObject.getString("titolo")))
                        titoli.add(jObject.getString("titolo"));
                    if(!aziende.contains(jObject.getString("aggiudicatario")))
                        aziende.add(jObject.getString("aggiudicatario"));
                    if(!cod.contains(jObject.getString("cod_fiscale_iva")))
                        cod.add(jObject.getString("cod_fiscale_iva"));
                    if(!tipi.contains(jObject.getString("tipo")))
                        tipi.add(jObject.getString("tipo"));
                    if(!anni.contains(jObject.getString("anno")))
                        anni.add(jObject.getString("anno"));
                    if(!citta.contains(jObject.getString("citta")))
                        citta.add(jObject.getString("citta"));
                //Log.d("FileDownloader", "Decompresso appalto: " + app.getCig()());
            } // End Loop
            Collections.sort(tipi);
            Collections.sort(anni);
            Collections.sort(citta);
            tipi.add(0,"Tutti");
            anni.add(0,"Tutti");
            citta.add(0,"Tutte");
            dataAdapterTitolo.notifyDataSetChanged();
            dataAdapterAzienda.notifyDataSetChanged();
            dataAdapterCodFiscIva.notifyDataSetChanged();
            dataAdapterTipo.notifyDataSetChanged();
            dataAdapterAnno.notifyDataSetChanged();
            dataAdapterCitta.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.e("StartActivity load", e.getMessage());
            final Toast toast = Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString("inserted_titolo", textTitolo.getText().toString());
            outState.putString("inserted_azienda", textAzienda.getText().toString());
            outState.putString("inserted_cod", textCodFiscIva.getText().toString());
            outState.putString("inserted_importoMin", textImportoMin.getText().toString());
            outState.putString("inserted_importoMax", textImportoMax.getText().toString());
            outState.putInt("inserted_anno", posAnno);
            outState.putInt("inserted_tipo", posTipo);
            outState.putInt("inserted_citta", posCitta);
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
            load();
            inserted_titolo = savedInstanceState.getString("inserted_titolo");
            inserted_azienda = savedInstanceState.getString("inserted_azienda");
            inserted_cod = savedInstanceState.getString("inserted_cod");
            inserted_importoMin = savedInstanceState.getString("inserted_importoMin");
            inserted_importoMax = savedInstanceState.getString("inserted_importoMax");
            posAnno = savedInstanceState.getInt("inserted_anno");
            posTipo = savedInstanceState.getInt("inserted_tipo");
            posCitta = savedInstanceState.getInt("inserted_citta");
            setWidgets();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_menu, menu);
        //menu.findItem(R.id.action_stats).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_refresh:
                try {
                    if (saver.getStatus() != AsyncTask.Status.RUNNING) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                        builder.setTitle(R.string.confirm_download);
                        builder.setMessage(R.string.confirm2);

                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                saver = new DataSaver(StartActivity.this, getApplicationContext());
                                saver.execute();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (Exception e) {
                    Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                }
                break;
            case R.id.action_stats:
                item.setVisible(false);
                Intent intent = new Intent(StartActivity.this,StatsActivity.class);
                //startActivity(intent);
                break;
            case android.R.id.home:
                NavUtils.navigateUpTo(this, getIntent());
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
