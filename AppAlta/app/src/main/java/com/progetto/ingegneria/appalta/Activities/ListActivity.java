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
import com.progetto.ingegneria.appalta.Classes.Filter;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.RecyclerItemListener;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    private ArrayList<Appalto> appList, fileRead;
    private RecyclerViewAdapterAppalto mAdapter;
    private Filter filter;
    private boolean otherFilters;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        appList = new ArrayList<>();
        fileRead = new ArrayList<>();

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
                    intent.putExtra("cig", app.getCig());
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
        initFilter();

        Bundle dati = getIntent().getExtras();
        if (dati != null) {
            String inserted_titolo = dati.getString("inserted_titolo");
            String inserted_azienda= dati.getString("inserted_azienda");
            String inserted_cod = dati.getString("inserted_cod");
            String inserted_importoMin= dati.getString("inserted_importoMin");
            String inserted_importoMax= dati.getString("inserted_importoMax");
            String inserted_anno= dati.getString("inserted_anno");
            String inserted_tipo= dati.getString("inserted_tipo");
            String inserted_citta= dati.getString("inserted_citta");
            if(TextUtils.isEmpty(inserted_titolo+inserted_azienda+inserted_cod+inserted_importoMin+inserted_importoMax+inserted_anno+inserted_tipo+inserted_citta)) {
                filter.activate(Type.NONE.toString());
                otherFilters =false;
            }
            else {
                filter.deactivate(Type.NONE.toString());
                otherFilters =true;
                if(!TextUtils.isEmpty(inserted_titolo)) {
                    filter.add(Type.TITLE.toString(), inserted_titolo, true);
                }
                if(!TextUtils.isEmpty(inserted_tipo)) {
                    filter.add(Type.TYPE.toString(), inserted_tipo, true);
                }
                if(!TextUtils.isEmpty(inserted_cod)) {
                    filter.add(Type.COD_IVA.toString(), inserted_cod, true);
                }
                if(!TextUtils.isEmpty(inserted_citta)) {
                    filter.add(Type.CITY.toString(), inserted_citta, true);
                }
                if(!TextUtils.isEmpty(inserted_azienda)) {
                    filter.add(Type.COMPANY.toString(), inserted_azienda, true);
                }
                if(!TextUtils.isEmpty(inserted_anno)) {
                    filter.add(Type.ANNO.toString(), inserted_anno, true);
                }
                if(!TextUtils.isEmpty(inserted_importoMin)) {
                    filter.add(Type.MIN.toString(), inserted_importoMin, true);
                }
                if(!TextUtils.isEmpty(inserted_importoMax)) {
                    filter.add(Type.MAX.toString(), inserted_importoMax, true);
                }
            }
        }
        else {
            filter.activate(Type.NONE.toString());
            otherFilters =false;
        }
    }

    private void prepareData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Appalto app;
                            //leggo da file la prima volta e memorizzo i dati
                            if(fileRead.isEmpty()){
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
                                            fileRead.add(app);
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
                            appList.clear();
                            for (int i = 0; i < fileRead.size(); i++) {
                                //DATI
                                try {
                                    if(filter(fileRead.get(i)))
                                    {
                                        appList.add(fileRead.get(i));
                                    }
                                }
                                catch (Exception e) {
                                    Log.e("ListActivity prepareData()3", e.getMessage());
                                }
                            } // End Loop
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (final Exception e) {
                    Log.e("ListActivity thread",e.getMessage());
                }
            }
        }.start();
    }

    private boolean filter(Appalto app){
        HashMap<String,String> toCompare = new HashMap<>();
        try {
            toCompare.put(Type.CITY.toString(),app.getCitta().toLowerCase());
            toCompare.put(Type.TITLE.toString(),app.getTitolo().toLowerCase());
            toCompare.put(Type.CIG.toString(),app.getCig().toLowerCase());
            toCompare.put(Type.TYPE.toString(),app.getTipo().toLowerCase());
            toCompare.put(Type.ANNO.toString(),app.getAnno());
            toCompare.put(Type.COMPANY.toString(),app.getAggiudicatario().toLowerCase());
            toCompare.put(Type.COD_IVA.toString(),app.getCod_fiscale_iva().toLowerCase());
            toCompare.put(Type.AMOUNT.toString(),app.getImporto_aggiudicazione());
            toCompare.put(Type.DATE.toString(),app.getDataInizio());
            toCompare.put(Type.ADMIN.toString(),app.getResponsabile().toLowerCase());

            for (Type t : Type.values()) {
                switch (t){
                    case ALL:
                        if(!filter.checkAll(t.toString(),toCompare))
                            return false;
                        //Log.d("Test", filter.get(t.toString()));
                        break;
                    case NONE:
                        if (filter.isActive(t.toString()))
                            return true;
                        break;
                    case MAX:
                        if(!filter.checkMax(t.toString(),toCompare.get(Type.AMOUNT.toString()))){
                            //Log.d("ListActivity MAX false",""+(filter.get(t.toString())+"<"+toCompare.get(t.toString())));
                            return false;
                        }
                        break;
                    case MIN:
                        if(!filter.checkMin(t.toString(),toCompare.get(Type.AMOUNT.toString()))){
                            //Log.d("ListActivity MIN false", ""+filter.get(t.toString())+">"+toCompare.get(t.toString()));
                            return false;
                        }
                        break;
                    case COD_IVA:
                        if(!filter.check(t.toString(),toCompare.get(t.toString()))){
                            //Log.d("ListActivity "+t.toString()+" false", "'"+s+"'");
                            return false;
                        }
                        //Log.d("Test "+t.toString(), toCompare.get(Type.CIG.toString())+": "+filter.get(t.toString())+" = "+toCompare.get(t.toString()));
                        break;
                    default:
                        if(!filter.check(t.toString(),toCompare.get(t.toString()))){
                            //Log.d("ListActivity "+t.toString()+" false", "'"+s+"'");
                            return false;
                        }
                        break;
                }
            }
        }
        catch (Exception e){
            Log.e("ListActivity filter()", e.getMessage());
            return false;
        }
        //Log.d("Test true", toCompare.get(Type.CIG.toString()));
        return true;
    }

    private void initFilter(){
        filter = new Filter();
        ArrayList<String> input = new ArrayList<>();
        for(Type t : Type.values()){
            input.add(t.toString());
        }
        filter.build(input);
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
        if(TextUtils.isEmpty(filter.get(Type.CITY)))
            text_city.setHint(R.string.insert_citta);
        else
            text_city.setText(filter.get(Type.CITY));

        if(TextUtils.isEmpty(filter.get(Type.TITLE)))
            text_title.setHint(R.string.insert_titolo);
        else
            text_title.setText(filter.get(Type.TITLE));

        if(TextUtils.isEmpty(filter.get(Type.TYPE)))
            text_type.setHint(R.string.insert_type);
        else
            text_type.setText(filter.get(Type.TYPE));

        if(TextUtils.isEmpty(filter.get(Type.COD_IVA)))
            text_cod_iva.setHint(R.string.insert_cod_iva);
        else
            text_cod_iva.setText(filter.get(Type.COD_IVA));

        if(TextUtils.isEmpty(filter.get(Type.COMPANY)))
            text_company.setHint(R.string.insert_company);
        else
            text_company.setText(filter.get(Type.COMPANY));

        if(TextUtils.isEmpty(filter.get(Type.MAX)))
            text_max.setHint(R.string.insert_max);
        else
            text_max.setText(filter.get(Type.MAX));

        if(TextUtils.isEmpty(filter.get(Type.MIN)))
            text_min.setHint(R.string.insert_min);
        else
            text_min.setText(filter.get(Type.MIN));

        if(TextUtils.isEmpty(filter.get(Type.ANNO)))
            text_anno.setHint(R.string.insert_anno);
        else
            text_anno.setText(filter.get(Type.ANNO));
        */
        if(TextUtils.isEmpty(filter.get(Type.ALL.toString())))
            text_all.setHint(R.string.insert_search);
        else
            text_all.setText(filter.get(Type.ALL.toString()));

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
                            filter.add(Type.CITY,text_city.getText().toString());

                        if(!TextUtils.isEmpty(text_title.getText().toString()))
                            filter.add(Type.TITLE,text_title.getText().toString());

                        if(!TextUtils.isEmpty(text_type.getText().toString()))
                            filter.add(Type.TYPE,text_type.getText().toString());

                        if(!TextUtils.isEmpty(text_cod_iva.getText().toString()))
                            filter.add(Type.COD_IVA,text_cod_iva.getText().toString());

                        if(!TextUtils.isEmpty(text_company.getText().toString()))
                            filter.add(Type.COMPANY,text_company.getText().toString());

                        if(!TextUtils.isEmpty(text_max.getText().toString()))
                            filter.add(Type.MAX,text_max.getText().toString());

                        if(!TextUtils.isEmpty(text_min.getText().toString()))
                            filter.add(Type.MIN,text_min.getText().toString());

                        if(!TextUtils.isEmpty(text_anno.getText().toString()))
                            filter.add(Type.ANNO,text_anno.getText().toString());
                        */
                        if(!TextUtils.isEmpty(text_all.getText().toString())) {
                            filter.deactivate(Type.NONE.toString());
                            filter.add(Type.ALL.toString(), text_all.getText().toString(), true);
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
        if(fileRead==null)
            fileRead = new ArrayList<>();
        prepareData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putBoolean("otherFilters", otherFilters);
            if(filter.isActive(Type.NONE.toString()))
                outState.putString(Type.NONE.toString(), "true");
            else
                outState.putString(Type.NONE.toString(), "false");
            outState.putString(Type.CITY.toString(), filter.get(Type.CITY.toString()));
            outState.putString(Type.TITLE.toString(), filter.get(Type.TITLE.toString()));
            outState.putString(Type.TYPE.toString(), filter.get(Type.TYPE.toString()));
            outState.putString(Type.COD_IVA.toString(), filter.get(Type.COD_IVA.toString()));
            outState.putString(Type.COMPANY.toString(), filter.get(Type.COMPANY.toString()));
            outState.putString(Type.ANNO.toString(), filter.get(Type.ANNO.toString()));
            outState.putString(Type.MAX.toString(), filter.get(Type.MAX.toString()));
            outState.putString(Type.MIN.toString(), filter.get(Type.MIN.toString()));
            outState.putString(Type.ALL.toString(), filter.get(Type.ALL.toString()));
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
            otherFilters = savedInstanceState.getBoolean("otherFilters");

            if (TextUtils.isEmpty(savedInstanceState.getString(Type.NONE.toString())))
                filter.add(Type.NONE.toString(), "", true);
            else
                if("true".equalsIgnoreCase(savedInstanceState.getString(Type.NONE.toString())))
                    filter.add(Type.NONE.toString(), "", true);
                else
                    filter.add(Type.NONE.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.CITY.toString())))
                filter.add(Type.CITY.toString(), savedInstanceState.getString(Type.CITY.toString()), true);
            else
                filter.add(Type.CITY.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.TITLE.toString())))
                filter.add(Type.TITLE.toString(), savedInstanceState.getString(Type.TITLE.toString()), true);
            else
                filter.add(Type.TITLE.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.TYPE.toString())))
                filter.add(Type.TYPE.toString(), savedInstanceState.getString(Type.TYPE.toString()), true);
            else
                filter.add(Type.TYPE.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.COD_IVA.toString())))
                filter.add(Type.COD_IVA.toString(), savedInstanceState.getString(Type.COD_IVA.toString()), true);
            else
                filter.add(Type.COD_IVA.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.COMPANY.toString())))
                filter.add(Type.COMPANY.toString(), savedInstanceState.getString(Type.COMPANY.toString()), true);
            else
                filter.add(Type.COMPANY.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.ANNO.toString())))
                filter.add(Type.ANNO.toString(), savedInstanceState.getString(Type.ANNO.toString()), true);
            else
                filter.add(Type.ANNO.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.MAX.toString())))
                filter.add(Type.MAX.toString(), savedInstanceState.getString(Type.MAX.toString()), true);
            else
                filter.add(Type.MAX.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.MIN.toString())))
                filter.add(Type.MIN.toString(), savedInstanceState.getString(Type.MIN.toString()), true);
            else
                filter.add(Type.MIN.toString(), "", false);

            if (!TextUtils.isEmpty(savedInstanceState.getString(Type.ALL.toString())))
                filter.add(Type.ALL.toString(), savedInstanceState.getString(Type.ALL.toString()), true);
            else
                filter.add(Type.ALL.toString(), "", false);

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
        inflater.inflate(R.menu.list_menu, menu);
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
                if(!otherFilters)
                    filter.activate(Type.NONE.toString());
                filter.add(Type.ALL.toString(),"",false);
                prepareData();
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

                if(TextUtils.isEmpty(filter.get(Type.CITY.toString())))
                    view_city.setHint(R.string.filter_null);
                else
                    view_city.setText(filter.get(Type.CITY.toString()));

                if(TextUtils.isEmpty(filter.get(Type.TITLE.toString())))
                    view_title.setHint(R.string.filter_null);
                else
                    view_title.setText(filter.get(Type.TITLE.toString()));

                if(TextUtils.isEmpty(filter.get(Type.TYPE.toString())))
                    view_type.setHint(R.string.filter_null);
                else
                    view_type.setText(filter.get(Type.TYPE.toString()));

                if(TextUtils.isEmpty(filter.get(Type.COD_IVA.toString())))
                    view_cod_iva.setHint(R.string.filter_null);
                else
                    view_cod_iva.setText(filter.get(Type.COD_IVA.toString()));

                if(TextUtils.isEmpty(filter.get(Type.COMPANY.toString())))
                    view_company.setHint(R.string.filter_null);
                else
                    view_company.setText(filter.get(Type.COMPANY.toString()));

                if(TextUtils.isEmpty(filter.get(Type.MAX.toString())))
                    view_max.setHint(R.string.filter_null);
                else
                    view_max.setText(filter.get(Type.MAX.toString()));

                if(TextUtils.isEmpty(filter.get(Type.MIN.toString())))
                    view_min.setHint(R.string.filter_null);
                else
                    view_min.setText(filter.get(Type.MIN.toString()));

                if(TextUtils.isEmpty(filter.get(Type.ANNO.toString())))
                    view_anno.setHint(R.string.filter_null);
                else
                    view_anno.setText(filter.get(Type.ANNO.toString()));

                if(TextUtils.isEmpty(filter.get(Type.ALL.toString())))
                    view_all.setHint(R.string.filter_null);
                else
                    view_all.setText(filter.get(Type.ALL.toString()));

                dialogBuilder.setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                dialogBuilder.show();
                Log.d("ListActivity Filters:",filter.getFilters().toString());
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
        CIG,
        TITLE,
        ADMIN,
        CITY,
        TYPE,
        COD_IVA,
        COMPANY,
        ANNO,
        DATE,
        AMOUNT,
        MAX,
        MIN,
        ALL,
    }
}
