package com.progetto.ingegneria.appalta.Threads;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matteo on 04/10/2017.
 */
public class DataLoader extends AsyncTask<String, String, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context appContext, mainContext;
    private ProgressDialog progressDialog;
    private ArrayList<Appalto> appalti;
    private GoogleMap googleMap;
    private int nMarkers;
    private String search_string;
    private boolean search;
    private ArrayList<Appalto> trovati;

    public DataLoader(Context mainContext, Context appContext, GoogleMap googleMap, int nMarkers) {
        this.search = false;
        this.appalti = new ArrayList<>();
        this.googleMap = googleMap;
        this.nMarkers=nMarkers;
        this.appContext = appContext;
        this.mainContext = mainContext;
    }

    public DataLoader(Context mainContext, Context appContext, GoogleMap googleMap, int nMarkers, String search_string) {
        this.appalti = new ArrayList<>();
        this.search = true;
        this.search_string = search_string;
        this.googleMap = googleMap;
        this.nMarkers=nMarkers;
        this.appContext = appContext;
        this.mainContext = mainContext;
    }


    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mainContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Caricamento dati in corso");
        progressDialog.setMessage("Potrebbe volerci qualche minuto...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        if(nMarkers!=-1)
            progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                DataLoader.this.cancel(true);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        int k=0;    //contatore per i log
        String via, comune, provincia;
        Appalto app;
        trovati = new ArrayList<>();
        try{
            JsonManager manager = new JsonManager(appContext);
            JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string

            int limite;
            if (nMarkers == 0 || nMarkers >= jArray.length() || search || nMarkers==-1)    //controllo che il n°di markers non superi la dimensione degli elementi scaricati
                limite = jArray.length();
            else
                limite = nMarkers;

            for (int i = 0; i < limite; i++) {
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
                    appalti.add(app);
                    k++;
                    //Log.d("FileDownloader", "Decompressa dato: " + app.getCig());
                }
                catch (Exception e){
                    Log.e("DataLoader errore aggiunta dato", e.getMessage());
                }
            } // End Loop

            if(search)
                for (int i = 0; i < appalti.size(); i++) {
                    if(appalti.get(i).getTitolo().toLowerCase().contains(search_string.toLowerCase()))
                        trovati.add(appalti.get(i));
                }

            Log.d("DataLoader doInBackground()","dati caricati: "+k);

        } catch (JSONException e) {
            Log.e("DataLoader doInBackground()", e.getMessage());
        }
        return null;
    }


        @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onPostExecute(Void v) {
        try {
            if(nMarkers!=-1) {
                setMarkers();
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("DataLoader onPostExecute()", e.toString());
        }
    } // protected void onPostExecute(Void v)

    @Override
    protected void onCancelled() {
        super.onCancelled();
        progressDialog.dismiss();
    }

    private void setMarkers(){
        try {
            int k=0; //contatore per i log
            ArrayList<Appalto> visualizza;
            ArrayList<String> inseriti = new ArrayList<>();
            googleMap.clear();
            if(!search) //se non ho cercato, visualizzo tutta la lista
                visualizza = appalti;
            else    //altrimenti solo gli appalti trovati
                visualizza = trovati;

            for(int i=0; i<visualizza.size();i++){
                try {
                    //LatLng pos = random_marker(5000, visualizza.get(i).getCoordinate().latitude, visualizza.get(i).getCoordinate().longitude);
                    String importo = visualizza.get(i).getImporto_aggiudicazione().replaceAll(",",".");
                    String titolo="Ci sono appalti qui";
                    if(!inseriti.contains(visualizza.get(i).getCitta()))
                    try{
                        //se è stata fatta una ricerca
                        if(search) {
                            titolo = visualizza.get(i).getTitolo();
                            //se ha meno di due elementi (è una ricerca precisa)
                            if(visualizza.size()<2) {
                                if (Double.parseDouble(importo) < 15000)
                                    googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_low_cost)));
                                else if (Double.parseDouble(importo) > 100000)
                                    googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_high_cost)));
                                else
                                    googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));
                            }
                            else {  //se invece è imprecisa (>2 elementi trovati)
                                titolo="Ci sono appalti qui che contengono '"+search_string+"'";
                                googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));
                            }
                        }
                        else {  //se invece si devono visualizzare tutti
                            googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));
                        }
                        inseriti.add(visualizza.get(i).getCitta());
                    }
                    catch (Exception e){
                        Log.e("DataLoader", e.getMessage());
                        //se crasha è per la conversione nei double, quindi ne aggiungo uno con icon default per chi ha importo non valido
                        googleMap.addMarker(new MarkerOptions().position(visualizza.get(i).getCoordinate()).title(titolo).snippet(visualizza.get(i).getCitta()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default)));
                    }
                    k++;
                }
                catch (Exception e){
                    Log.e("DataLoader", e.getMessage());
                }
                //Log.d("Test:", "coordinate: "+pos.toString());
            }

            //zoom sul primo appalto aggiunto
            if(visualizza.size()>0) {
                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(visualizza.get(0).getCoordinate()).zoom(7).bearing(0).tilt(30).build();
                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(myPosition));
            }
            Log.d("DataLoader setMarkers()", "Marker Aggiunti: "+k);
        }
        catch (Exception e){
            Log.e("DataLoader setMarkers()", e.getMessage());
        }
    }
/*
    private LatLng random_marker(double max_range, double x0, double y0){
        Random r = new Random();
        double u,v,w,t,x,y;
        u = r.nextDouble(); //independent uniform random
        v = r.nextDouble(); //independent uniform random
        w = max_range/111000f * sqrt(u);
        t = 2 * Math.PI * v;
        x = (w * cos(t));
        y = w * sin(t);
        return new LatLng(x0+x,y0+y);
    }
*/
    public ArrayList<Appalto> getAppalti() {
        return appalti;
    }

    public ArrayList<Appalto> getTrovati() {
        return trovati;
    }

    public boolean isSearch() {
        return search;
    }

    public String getSearch_string() {
        return search_string;
    }
}
