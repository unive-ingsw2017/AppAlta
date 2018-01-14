package com.progetto.ingegneria.appalta.Threads;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.CSVEntry;
import com.progetto.ingegneria.appalta.Classes.CSVReader;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.XMLEntry;
import com.progetto.ingegneria.appalta.Classes.XMLReader;
import com.progetto.ingegneria.appalta.R;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo on 28/09/2017.
 */
public class DataSaver extends AsyncTask<String, String, Void> {

    private ProgressDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mainContext, appContext;
    private ArrayList<Appalto> appalti_file;
    private boolean init=false;

    public DataSaver(Context mainContext, Context appContext) {
        appalti_file = new ArrayList<>();
        this.appContext = appContext;
        this.mainContext = mainContext;
    }

    public DataSaver(Context mainContext, Context appContext, boolean init) {
        appalti_file = new ArrayList<>();
        this.appContext = appContext;
        this.mainContext = mainContext;
        this.init = init;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mainContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Download dati in corso");
        progressDialog.setMessage("Potrebbe volerci qualche minuto...");
        //progressDialog.setProgress(0);
        //progressDialog.setMax(100);
        if(!init)
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                DataSaver.this.cancel(true);
            }
        });
    }

    @Override
    protected Void doInBackground(String... strings) {
        List<String[]> appaltiCSV = readCSV();
        ArrayList<XMLEntry> appaltiXML = readXML();
        downloadXML(appaltiXML);
        downloadCSV(appaltiCSV);
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonManager manager = new JsonManager(appContext);
            String jsonInString = mapper.writeValueAsString(appalti_file);
            manager.writeToFile(jsonInString,"appalti_file.txt");
            Log.d("DataSaver scrittura json", "successo");
        }
        catch (Exception e) {
            Log.e("DataSaver scrittura json", e.getMessage());
            progressDialog.setProgress(0);
        }
        return null;
    } // protected Void doInBackground(String... params)

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onPostExecute(Void v) {
        //parse JSON data
        if(!init)
        progressDialog.dismiss();
    } // protected void onPostExecute(Void v)

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(!init)
        progressDialog.dismiss();
    }

    private void downloadXML(ArrayList<XMLEntry> appaltiXML){
        int k=0;    //contatore per i log
        LatLng rovigo = new LatLng(45.069667, 11.787030);
        for (int i = 0; i < appaltiXML.size(); i++) {
            //DATI
            Appalto app;
            //JSONObject jObject = jArray.getJSONObject(i);
            try {
                //progressDialog.setProgress(Math.round(100 / (limite / (i + 1))));
                app = new Appalto();
                XMLEntry xml = appaltiXML.get(i);
                app.setCig(xml.getCig());
                app.setTitolo(xml.getOggetto());
                app.setTipo(xml.getSceltaContraente());
                app.setAnno(xml.getAnno());
                app.setAggiudicatario(xml.getRagioneSociale());
                app.setCod_fiscale_iva(xml.getCodiceFiscaleProp());
                app.setImporto_aggiudicazione(xml.getImportoAggiudicazione());
                app.setDataInizio(xml.getDataInizio());
                app.setResponsabile(String.valueOf(appContext.getResources().getText(R.string.null_data)));
                app.setCitta("Rovigo");
                app.setCoordinate(rovigo);
                appalti_file.add(app);
                //Log.d("DataSaver downloadXML()", xml.getCig());
                k++;
            }
            catch (Exception e){
                Log.e("DataSaver downloadXML()", e.getMessage());
            }
        } // End Loop
        Log.d("DataSaver downloadXML()","dati XML letti: "+k);
    }

    private void downloadCSV(List<String[]> appaltiCSV){
        int k=0;    //contatore per i log
        LatLng bologna = new LatLng(44.494934, 11.333057);
        for (int i = 0; i < appaltiCSV.size(); i++) {
            //DATI
            Appalto app;
            //JSONObject jObject = jArray.getJSONObject(i);
            try {
                //progressDialog.setProgress(Math.round(100 / (limite / (i + 1))));
                app = new Appalto();
                CSVEntry csv = setCSVEntry(appaltiCSV.get(i));
                app.setCig(csv.getCig());
                app.setTitolo(csv.getTitolo());
                app.setTipo(csv.getTipo_gara());
                app.setAnno(csv.getAnno_pg_atto_autorizzativo());
                app.setAggiudicatario(csv.getAggiudicatario());
                if(!TextUtils.isEmpty(csv.getCodice_fiscale()))
                    app.setCod_fiscale_iva(csv.getCodice_fiscale());
                else if(!TextUtils.isEmpty(csv.getIva()))
                    app.setCod_fiscale_iva(csv.getIva());
                else
                    app.setCod_fiscale_iva(String.valueOf(appContext.getResources().getText(R.string.null_data)));
                //Log.d("DataSaver", "appalto: " + app.getCod_fiscale_iva());
                app.setImporto_aggiudicazione(csv.getImporto_aggiudicazione());
                app.setDataInizio(csv.getData_atto_aggiudicazione());
                app.setResponsabile(csv.getResponsabile_gara());
                app.setCitta("Bologna");
                app.setCoordinate(bologna);
                appalti_file.add(app);

                k++;
            }
            catch (Exception e){
                Log.e("DataSaver downloadCSV()", e.getMessage());
            }
        } // End Loop
        Log.d("DataSaver downloadCSV()","dati CSV letti: "+k);
    }

    private CSVEntry setCSVEntry(String[] appalto){
        CSVEntry p = new CSVEntry();
        p.setN_pg_atto_autorizzativo(appalto[0]);
        p.setAnno_pg_atto_autorizzativo(appalto[1]);
        p.setTipo_gara(appalto[2]);
        p.setTitolo(appalto[3]);
        p.setCig(appalto[4]);
        p.setOggetto(appalto[5]);
        p.setImporto(appalto[6]);
        p.setTipo_appalto(appalto[7]);
        p.setDurata(appalto[8]);
        p.setUtilizzo_mepa(appalto[9]);
        p.setLink_determina_pubblica(appalto[10]);
        p.setOggetto_determina(appalto[11]);
        p.setSettore_dipartimento(appalto[12]);
        p.setServizio(appalto[13]);
        p.setUi(appalto[14]);
        p.setResponsabile_gara(appalto[15]);
        p.setResponsabile_procedimento(appalto[16]);
        p.setModalita_aggiudicazione(appalto[17]);
        p.setScadenza_offerte(appalto[18]);
        p.setOra_scadenza_offerte(appalto[19]);
        p.setVariante_tempi_completamento(appalto[20]);
        p.setImporto_somme_liquidate(appalto[21]);
        p.setNumero_atto_aggiudicazione(appalto[22]);
        p.setAnno_atto_aggiudicazione(appalto[23]);
        p.setData_atto_aggiudicazione(appalto[24]);
        p.setAggiudicatario(appalto[25]);
        p.setIva(appalto[26].replaceAll(" ",""));
        p.setCodice_fiscale(appalto[27].replaceAll(" ",""));
        p.setImporto_aggiudicazione(appalto[28]);
        p.setLink_aggiudicazione(appalto[29]);
        p.setElenco_operatori_aggiudicazione(appalto[30]);
        p.setEsiti_aggiudicazione(appalto[31]);
        p.setIndirizzo("Bologna");
        return p;
    }
    private LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            //Log.d("DataSaver","geLocationFromAdress completata");
        }
        catch (Exception e)
        {
            Log.e("DataSaver getLocationFromAddress","indirizzo non trovato: "+strAddress);
            Log.e("DataSaver getLocationFromAddress", e.getMessage());
            e.printStackTrace();
        }
        return p1;
    }

    private List<String[]> readCSV() {
        InputStream inputStream = appContext.getResources().openRawResource(R.raw.appalti2017);
        CSVReader csvFile = new CSVReader(inputStream);
        List<String[]> lista = csvFile.read();
        lista.remove(0);    //tolgo l'intestazione del file
        return lista;
    }

    private ArrayList<XMLEntry> readXML(){
        InputStream inputStream = appContext.getResources().openRawResource(R.raw.avcp_dataset_2015);
        XMLReader xmlFile = new XMLReader(inputStream);
        try {
            return xmlFile.read();
        }
        catch(Exception e){
            Log.e("DataSaver readXML", e.getMessage());
            return null;
        }
    }

    public ArrayList<Appalto> getAppalti() {
        return appalti_file;
    }
} //class MyAsyncTask extends AsyncTask<String, String, Void>