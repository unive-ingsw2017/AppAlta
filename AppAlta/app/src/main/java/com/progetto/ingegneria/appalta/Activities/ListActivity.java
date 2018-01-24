package com.progetto.ingegneria.appalta.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.progetto.ingegneria.appalta.Adapters.EmptyRecyclerView;
import com.progetto.ingegneria.appalta.Adapters.RecyclerViewAdapterAppalto;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.DividerItemDecoration;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.RecyclerItemListener;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    private ArrayList<Appalto> appList = new ArrayList<>();
    private RecyclerViewAdapterAppalto mAdapter;
    private HashMap<Type, String> filters;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //setupUI(findViewById(R.id.main_content));

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) findViewById(R.id.recycler_view);
        android.support.design.widget.FloatingActionButton fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(100);
                v.startAnimation(animation1);
                alert();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setEmptyView(findViewById(R.id.empty_view));
        recyclerView.setClickable(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(
                new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.item_decorator)));

        recyclerView.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), recyclerView,
                new RecyclerItemListener.RecyclerTouchListener() {
            public void onClickItem(View view, int position) {
                Appalto app = appList.get(position);
                try {
                    final Intent intent = new Intent(getApplicationContext(), SelectedItemActivity.class);
                    intent.putExtra("titolo", app.getTitolo());
                    startActivity(intent);
                }
                catch(Exception e){
                    Log.e("ListActivity onClickItem()", e.getMessage());
                    final Toast toast = Toast.makeText(getApplicationContext(),R.string.error_data,Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            public void onLongClickItem(View view, int position) {

            }
        }));

        mAdapter = new RecyclerViewAdapterAppalto(appList);
        recyclerView.setAdapter(mAdapter);
        resetFilter();
        Bundle dati = getIntent().getExtras();
        if (dati != null) {
            String inserted_titolo = dati.getString("inserted_titolo");
            String inserted_azienda= dati.getString("inserted_azienda");
            String inserted_cod= dati.getString("inserted_cod");
            String inserted_importoMin= dati.getString("inserted_importoMin");
            String inserted_importoMax= dati.getString("inserted_importoMax");
            String inserted_anno= dati.getString("inserted_anno");
            String inserted_tipo= dati.getString("inserted_tipo");
            String inserted_citta= dati.getString("inserted_citta");
            if(TextUtils.isEmpty(inserted_titolo+inserted_azienda+inserted_importoMin+inserted_importoMax+inserted_anno+inserted_tipo+inserted_citta))
                filters.put(Type.NONE,"yes");
            else
                filters.put(Type.NONE,"");
            if(!TextUtils.isEmpty(inserted_titolo))
                filters.put(Type.TITLE,inserted_titolo.toLowerCase());
            if(!TextUtils.isEmpty(inserted_tipo))
                filters.put(Type.TYPE,inserted_tipo.toLowerCase());
            if(!TextUtils.isEmpty(inserted_cod))
                filters.put(Type.COD_IVA,inserted_cod.toLowerCase());
            if(!TextUtils.isEmpty(inserted_citta))
                filters.put(Type.CITY,inserted_citta.toLowerCase());
            if(!TextUtils.isEmpty(inserted_azienda))
                filters.put(Type.COMPANY,inserted_azienda.toLowerCase());
            if(!TextUtils.isEmpty(inserted_anno))
                filters.put(Type.ANNO,inserted_anno);
            if(!TextUtils.isEmpty(inserted_importoMin))
                filters.put(Type.MIN,inserted_importoMin);
            if(!TextUtils.isEmpty(inserted_importoMax))
                filters.put(Type.MAX,inserted_importoMax);
        }
    }

    private boolean prepareData() {
            Appalto app;
            JsonManager manager = new JsonManager(getApplicationContext());
            appList.clear();
            try {
                JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string
                for (int i = 0; i < jArray.length(); i++) {
                    //DATI
                    try {
                        JSONObject jObject = jArray.getJSONObject(i);
                        if(filter(jObject))
                        {
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
                        }
                        //Log.d("Test", "aggiunto appalto: "+ app.getCig());
                    }
                    catch (Exception e) {
                        Log.e("ListActivity prepareData() for", e.getMessage());
                    }
                } // End Loop
                mAdapter.notifyDataSetChanged();
                return true;
            }
            catch (Exception e) {
                Log.e("ListActivity prepareData()", e.getMessage());
                final Toast toast = Toast.makeText(getApplicationContext(), "Errore nella lettura dati ", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
    }

    private boolean filter(JSONObject obj){
        for(int i=0; i<filters.size(); i++) {
            try {
                String  citta=obj.getString("citta").toLowerCase(),
                        titolo=obj.getString("titolo").toLowerCase(),
                        cig=obj.getString("cig").toLowerCase(),
                        tipo=obj.getString("tipo").toLowerCase(),
                        anno=obj.getString("anno"),
                        aggiudicatario=obj.getString("aggiudicatario").toLowerCase(),
                        cod_fiscale_iva=obj.getString("cod_fiscale_iva").toLowerCase(),
                        importo_aggiudicazione=obj.getString("importo_aggiudicazione").toLowerCase(),
                        dataInizio=obj.getString("dataInizio").toLowerCase(),
                        responsabile=obj.getString("responsabile").toLowerCase();

                if (filters.get(Type.NONE).equalsIgnoreCase("yes")) {
                    //Log.d("ListActivity","NONE true: "+filters.get(Type.NONE));
                    return true;
                }
                if ((!filters.get(Type.ALL).equalsIgnoreCase("")) && (!tipo.contains(filters.get(Type.ALL)) && !citta.contains(filters.get(Type.ALL)) && !titolo.contains(filters.get(Type.ALL)) && !cig.contains(filters.get(Type.ALL)) && !anno.contains(filters.get(Type.ALL)) && !aggiudicatario.contains(filters.get(Type.ALL)) && !cod_fiscale_iva.contains(filters.get(Type.ALL)) && !importo_aggiudicazione.contains(filters.get(Type.ALL))&& !dataInizio.contains(filters.get(Type.ALL)) && !responsabile.contains(filters.get(Type.ALL)) )) {
                    //Log.d("ListActivity","ALL false");
                    return false;
                }
                if (!filters.get(Type.CITY).equalsIgnoreCase("") && !citta.contains(filters.get(Type.CITY))) {
                    //Log.d("ListActivity","CITY false: "+filters.get(Type.CITY)+" !contains "+citta);
                    return false;
                }
                if (!filters.get(Type.TITLE).equalsIgnoreCase("") && !titolo.contains(filters.get(Type.TITLE))) {
                    //Log.d("ListActivity","TITLE false: "+filters.get(Type.TITLE)+" !contains "+titolo);
                    return false;
                }
                if (!filters.get(Type.TYPE).equalsIgnoreCase("") && !tipo.contains(filters.get(Type.TYPE))) {
                    //Log.d("ListActivity","TYPE false: "+filters.get(Type.TYPE)+" !contains "+tipo);
                    return false;
                }
                if (!filters.get(Type.COD_IVA).equalsIgnoreCase("") && !cod_fiscale_iva.contains(filters.get(Type.COD_IVA))) {
                    //Log.d("ListActivity","COD_IVA false: "+filters.get(Type.COD_IVA)+" !contains "+cod_fiscale_iva);
                    return false;
                }
                if (!filters.get(Type.COMPANY).equalsIgnoreCase("") && !aggiudicatario.contains(filters.get(Type.COMPANY))){
                    //Log.d("ListActivity","COMPANY false: "+filters.get(Type.COMPANY)+" !contains "+aggiudicatario);
                    return false;
                }
                if (!filters.get(Type.ANNO).equalsIgnoreCase("") && !anno.contains(filters.get(Type.ANNO))){
                    //Log.d("ListActivity","ANNO false: "+filters.get(Type.ANNO)+" !contains "+anno);
                    return false;
                }
                double importo = 0;
                if(!TextUtils.isEmpty(importo_aggiudicazione) && !importo_aggiudicazione.contains("/"))
                    importo = Double.parseDouble(importo_aggiudicazione.replaceAll(",","."));
                if (!filters.get(Type.MIN).equalsIgnoreCase("") && (importo <= Double.parseDouble(filters.get(Type.MIN).replace(",",".")))) {
                    //Log.d("ListActivity","MIN false: "+importo+" < "+filters.get(Type.MIN));
                    return false;
                }
                if (!filters.get(Type.MAX).equalsIgnoreCase("") && (importo >= Double.parseDouble(filters.get(Type.MAX).replace(",",".")))) {
                    //Log.d("ListActivity","MAX false: "+importo+" > "+filters.get(Type.MAX));
                    return false;
                }
            }
            catch (Exception e){
                Log.e("ListActivity filter()", e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void resetFilter(){
        filters = new HashMap<>();
        filters.put(Type.NONE,"yes");
        filters.put(Type.CITY,"");
        filters.put(Type.TITLE,"");
        filters.put(Type.TYPE,"");
        filters.put(Type.COD_IVA,"");
        filters.put(Type.COMPANY,"");
        filters.put(Type.ANNO,"");
        filters.put(Type.MAX,"");
        filters.put(Type.MIN,"");
        filters.put(Type.ALL,"");
    }

    private void alert(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_search, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.search_list);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                Log.e("Test","onTouch");
                return false;
            }
        });
/*

*/      /*
        final EditText text_city = (EditText) dialogView.findViewById(R.id.filter_city_alert);
        final EditText text_title = (EditText) dialogView.findViewById(R.id.filter_title_alert);
        final EditText text_type =(EditText) dialogView.findViewById(R.id.filter_type_alert);
        final EditText text_cod_iva =(EditText) dialogView.findViewById(R.id.filter_cod_iva_alert);
        final EditText text_company =(EditText) dialogView.findViewById(R.id.filter_company_alert);
        final EditText text_anno =(EditText) dialogView.findViewById(R.id.filter_anno_alert);
        final EditText text_max =(EditText) dialogView.findViewById(R.id.filter_max_alert);
        final EditText text_min =(EditText) dialogView.findViewById(R.id.filter_min_alert);
        */
        final EditText text_all = (EditText) dialogView.findViewById(R.id.filter_search_alert);
        /*
        if(TextUtils.isEmpty(filters.get(Type.CITY)))
            text_city.setHint(R.string.insert_citta);
        else
            text_city.setText(filters.get(Type.CITY));

        if(TextUtils.isEmpty(filters.get(Type.TITLE)))
            text_title.setHint(R.string.insert_titolo);
        else
            text_title.setText(filters.get(Type.TITLE));

        if(TextUtils.isEmpty(filters.get(Type.TYPE)))
            text_type.setHint(R.string.insert_type);
        else
            text_type.setText(filters.get(Type.TYPE));

        if(TextUtils.isEmpty(filters.get(Type.COD_IVA)))
            text_cod_iva.setHint(R.string.insert_cod_iva);
        else
            text_cod_iva.setText(filters.get(Type.COD_IVA));

        if(TextUtils.isEmpty(filters.get(Type.COMPANY)))
            text_company.setHint(R.string.insert_company);
        else
            text_company.setText(filters.get(Type.COMPANY));

        if(TextUtils.isEmpty(filters.get(Type.MAX)))
            text_max.setHint(R.string.insert_max);
        else
            text_max.setText(filters.get(Type.MAX));

        if(TextUtils.isEmpty(filters.get(Type.MIN)))
            text_min.setHint(R.string.insert_min);
        else
            text_min.setText(filters.get(Type.MIN));

        if(TextUtils.isEmpty(filters.get(Type.ANNO)))
            text_anno.setHint(R.string.insert_anno);
        else
            text_anno.setText(filters.get(Type.ANNO));
        */
        if(TextUtils.isEmpty(filters.get(Type.ALL)))
            text_all.setHint(R.string.insert_search);
        else
            text_all.setText(filters.get(Type.ALL));

        dialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                    }
                });

        dialogBuilder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        /*
                        if(!TextUtils.isEmpty(text_city.getText().toString()))
                            filters.put(Type.CITY,text_city.getText().toString());

                        if(!TextUtils.isEmpty(text_title.getText().toString()))
                            filters.put(Type.TITLE,text_title.getText().toString());

                        if(!TextUtils.isEmpty(text_type.getText().toString()))
                            filters.put(Type.TYPE,text_type.getText().toString());

                        if(!TextUtils.isEmpty(text_cod_iva.getText().toString()))
                            filters.put(Type.COD_IVA,text_cod_iva.getText().toString());

                        if(!TextUtils.isEmpty(text_company.getText().toString()))
                            filters.put(Type.COMPANY,text_company.getText().toString());

                        if(!TextUtils.isEmpty(text_max.getText().toString()))
                            filters.put(Type.MAX,text_max.getText().toString());

                        if(!TextUtils.isEmpty(text_min.getText().toString()))
                            filters.put(Type.MIN,text_min.getText().toString().toLowerCase());

                        if(!TextUtils.isEmpty(text_anno.getText().toString()))
                            filters.put(Type.ANNO,text_anno.getText().toString().toLowerCase());
                        */
                        if(!TextUtils.isEmpty(text_all.getText().toString())) {
                            filters.put(Type.NONE,"");
                            filters.put(Type.ALL, text_all.getText().toString().toLowerCase());
                        }

                        prepareData();
                        dialog.cancel();
                    }
                });
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(Type.NONE.toString(), filters.get(Type.NONE));
            outState.putString(Type.CITY.toString(), filters.get(Type.CITY));
            outState.putString(Type.TITLE.toString(), filters.get(Type.TITLE));
            outState.putString(Type.TYPE.toString(), filters.get(Type.TYPE));
            outState.putString(Type.COD_IVA.toString(), filters.get(Type.COD_IVA));
            outState.putString(Type.COMPANY.toString(), filters.get(Type.COMPANY));
            outState.putString(Type.ANNO.toString(), filters.get(Type.ANNO));
            outState.putString(Type.MAX.toString(), filters.get(Type.MAX));
            outState.putString(Type.MIN.toString(), filters.get(Type.MIN));
            outState.putString(Type.ALL.toString(), filters.get(Type.ALL));
            Log.d("ListActivity onSaveInstanceState()", "filtro salvato");
        }
        catch (Exception e){
            Log.e("ListActivity onSaveInstanceState()", e.getMessage());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            if (savedInstanceState.getString(Type.NONE.toString()) != null)
                filters.put(Type.NONE, savedInstanceState.getString(Type.NONE.toString()));
            else
                filters.put(Type.NONE, "yes");

            if (savedInstanceState.getString(Type.CITY.toString()) != null)
                filters.put(Type.CITY, savedInstanceState.getString(Type.CITY.toString()));
            else
                filters.put(Type.CITY, "");

            if (savedInstanceState.getString(Type.TITLE.toString()) != null)
                filters.put(Type.TITLE, savedInstanceState.getString(Type.TITLE.toString()));
            else
                filters.put(Type.TITLE, "");

            if (savedInstanceState.getString(Type.TYPE.toString()) != null)
                filters.put(Type.TYPE, savedInstanceState.getString(Type.TYPE.toString()));
            else
                filters.put(Type.TYPE, "");

            if (savedInstanceState.getString(Type.COD_IVA.toString()) != null)
                filters.put(Type.COD_IVA, savedInstanceState.getString(Type.COD_IVA.toString()));
            else
                filters.put(Type.COD_IVA, "");

            if (savedInstanceState.getString(Type.COMPANY.toString()) != null)
                filters.put(Type.COMPANY, savedInstanceState.getString(Type.COMPANY.toString()));
            else
                filters.put(Type.COMPANY, "");

            if (savedInstanceState.getString(Type.ANNO.toString()) != null)
                filters.put(Type.ANNO, savedInstanceState.getString(Type.ANNO.toString()));
            else
                filters.put(Type.ANNO, "");

            if (savedInstanceState.getString(Type.MAX.toString()) != null)
                filters.put(Type.MAX, savedInstanceState.getString(Type.MAX.toString()));
            else
                filters.put(Type.MAX, "");

            if (savedInstanceState.getString(Type.MIN.toString()) != null)
                filters.put(Type.MIN, savedInstanceState.getString(Type.MIN.toString()));
            else
                filters.put(Type.MIN, "");

            if (savedInstanceState.getString(Type.ALL.toString()) != null)
                filters.put(Type.ALL, savedInstanceState.getString(Type.ALL.toString()));
            else
                filters.put(Type.ALL, "");

            Log.d("ListActivity onRestoreInstanceState()", "filtro caricato");
        }
        catch (Exception e){
            Log.e("ListActivity onRestoreInstanceState()", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.start_menu, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.filter_delete:
                final Toast toast = Toast.makeText(getApplicationContext(),R.string.filter_deleted,Toast.LENGTH_SHORT);
                //resetto la ricerca globale
                filters.put(Type.NONE,"yes");
                filters.put(Type.ALL,"");
                if(prepareData())
                    toast.show();
                break;
            case R.id.filter_list:
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListActivity.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_filter_list, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle(R.string.filter_list);
                TextView view_city = (TextView) dialogView.findViewById(R.id.filter_city_alert);
                TextView view_title = (TextView) dialogView.findViewById(R.id.filter_title_alert);
                TextView view_type =(TextView) dialogView.findViewById(R.id.filter_type_alert);
                TextView view_cod_iva =(TextView) dialogView.findViewById(R.id.filter_cod_iva_alert);
                TextView view_company =(TextView) dialogView.findViewById(R.id.filter_company_alert);
                TextView view_anno =(TextView) dialogView.findViewById(R.id.filter_anno_alert);
                TextView view_max =(TextView) dialogView.findViewById(R.id.filter_max_alert);
                TextView view_min =(TextView) dialogView.findViewById(R.id.filter_min_alert);
                TextView view_all = (TextView) dialogView.findViewById(R.id.filter_search_alert);

                if(filters.get(Type.CITY).equalsIgnoreCase(""))
                    view_city.setHint(R.string.filter_null);
                else
                    view_city.setText(filters.get(Type.CITY));

                if(filters.get(Type.TITLE).equalsIgnoreCase(""))
                    view_title.setHint(R.string.filter_null);
                else
                    view_title.setText(filters.get(Type.TITLE));

                if(filters.get(Type.TYPE).equalsIgnoreCase(""))
                    view_type.setHint(R.string.filter_null);
                else
                    view_type.setText(filters.get(Type.TYPE));

                if(filters.get(Type.COD_IVA).equalsIgnoreCase(""))
                    view_cod_iva.setHint(R.string.filter_null);
                else
                    view_cod_iva.setText(filters.get(Type.COD_IVA));

                if(filters.get(Type.COMPANY).equalsIgnoreCase(""))
                    view_company.setHint(R.string.filter_null);
                else
                    view_company.setText(filters.get(Type.COMPANY));

                if(filters.get(Type.MAX).equalsIgnoreCase(""))
                    view_max.setHint(R.string.filter_null);
                else
                    view_max.setText(filters.get(Type.MAX));

                if(filters.get(Type.MIN).equalsIgnoreCase(""))
                    view_min.setHint(R.string.filter_null);
                else
                    view_min.setText(filters.get(Type.MIN));

                if(filters.get(Type.ANNO).equalsIgnoreCase(""))
                    view_anno.setHint(R.string.filter_null);
                else
                    view_anno.setText(filters.get(Type.ANNO));

                if(filters.get(Type.ALL).equalsIgnoreCase(""))
                    view_all.setHint(R.string.filter_null);
                else
                    view_all.setText(filters.get(Type.ALL));

                dialogBuilder.setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                dialogBuilder.show();
                break;

            case android.R.id.home:
                NavUtils.navigateUpTo(this, getIntent());
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public enum Type{
        NONE,
        TITLE,
        CITY,
        TYPE,
        COD_IVA,
        COMPANY,
        ANNO,
        MAX,
        MIN,
        ALL,
    }
}
