package com.progetto.ingegneria.appalta.Activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.maps.model.LatLng;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.MovableFloatingActionButton;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//https://github.com/appsthatmatter/GraphView
//https://github.com/PhilJay/MPAndroidChart/wiki
public class StatsActivity extends AppCompatActivity {

    private ArrayList<Appalto> appList;
    private ArrayList<String> companies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        appList = new ArrayList<>();
        MovableFloatingActionButton fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        BarChart graph1 = (BarChart) findViewById(R.id.graph1);
        BarChart graph2 = (BarChart) findViewById(R.id.graph2);
        CoordinatorLayout.LayoutParams lp  = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fab.setCoordinatorLayout(lp);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        prepareData();

        /*
        float sumBologna = getSum(appList, "","bologna","");
        float sumRovigo = getSum(appList, "","rovigo","");
        float sum2015 = getSum(appList, "2015","","");
        float sum2017 = getSum(appList, "2017","bologna","");
        */

        //GRAPH1
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        List<IBarDataSet> dataSets = new ArrayList<>();
        /*
        for(int i=0; i<companies.size() && i<10; i++) {
            entries1.add(new BarEntry(i, getSum(appList, "","rovigo",companies.get(i))));
        }
        */
        entries1.add(new BarEntry(0, getSum(appList, "","","fastweb")));
        BarDataSet dataSet1 = new BarDataSet(entries1, "Fastweb"); // add entries to dataset
        dataSet1.setColors(new int[]{R.color.red}, StatsActivity.this);
        dataSets.add(dataSet1);

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(1, getSum(appList, "","","as2 srl")));
        BarDataSet dataSet2 = new BarDataSet(entries2, "AS2 srl"); // add entries to dataset
        dataSet2.setColors(new int[]{R.color.green}, StatsActivity.this);
        dataSets.add(dataSet2);

        ArrayList<BarEntry> entries3 = new ArrayList<>();
        entries3.add(new BarEntry(2, getSum(appList, "","","telecom")));
        BarDataSet dataSet3 = new BarDataSet(entries3, "Telecom"); // add entries to dataset
        dataSet3.setColors(new int[]{R.color.yellow}, StatsActivity.this);
        dataSets.add(dataSet3);

        ArrayList<BarEntry> entries4 = new ArrayList<>();
        entries4.add(new BarEntry(3, getSum(appList, "","","poste italiane")));
        BarDataSet dataSet4 = new BarDataSet(entries4, "Poste Italiane"); // add entries to dataset
        dataSet4.setColors(new int[]{R.color.primary}, StatsActivity.this);
        dataSets.add(dataSet4);

        BarData barData = new BarData(dataSets);
        graph1.setData(barData);
        Description d = new Description();
        d.setText("Esempio Statistiche Aziende");
        d.setTextSize(getResources().getDimension(R.dimen.text_size));
        graph1.setDescription(d);
        graph1.invalidate(); // refresh
    }

    private float getSum(ArrayList<Appalto> list, String year, String city, String company){
        float sum=0;
        boolean filter;
        company = company.toLowerCase();
        for(Appalto app : list){
            try{
                filter=false;
                String readCompany = app.getAggiudicatario().toLowerCase();

                if(!TextUtils.isEmpty(year)) {
                    if (!app.getAnno().equalsIgnoreCase(year))
                        filter = true;
                }

                if(!TextUtils.isEmpty(city)) {
                    if (!app.getCitta().equalsIgnoreCase(city))
                        filter = true;
                }

                if(!TextUtils.isEmpty(company)) {
                    if (!readCompany.contains(company))
                        filter = true;
                }

                if(!filter) {
                    sum += Float.parseFloat(app.getImporto_aggiudicazione().replace(",", "."));
                }

            }
            catch(Exception e){
                Log.e("StatsActivity", e.getMessage());
            }
        }
        Log.d("StatsActivity", "somma: "+String.valueOf(sum));
        return sum;
    }

    private void prepareData() {
        Appalto app;
        //leggo da file la prima volta e memorizzo i dati
        if(appList.isEmpty()){
            companies = new ArrayList<>();
            try {
                JsonManager manager = new JsonManager(getApplicationContext());
                JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string
                for (int i = 0; i < jArray.length(); i++) {
                    //DATI
                    try {
                        JSONObject jObject = jArray.getJSONObject(i);
                        app = new Appalto();
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
                        appList.add(app);
                        if(!companies.contains(app.getAggiudicatario().toLowerCase()) && app.getImporto_aggiudicazione().length()>0 && !app.getImporto_aggiudicazione().equalsIgnoreCase(""))
                            companies.add(app.getAggiudicatario().toLowerCase());
                        //Log.d("Test", "aggiunto appalto: "+ app.getCig());
                    }
                    catch (Exception e) {
                        Log.e("ListActivity prepareData()1", e.getMessage());
                    }
                } // End Loop
            }
            catch (Exception e) {
                Log.e("ListActivity prepareData()2", e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.start_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpTo(this, getIntent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
