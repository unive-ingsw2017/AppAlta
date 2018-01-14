package com.progetto.ingegneria.appalta.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.progetto.ingegneria.appalta.Adapters.RecyclerViewAdapterSelectedCompany;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.DividerItemDecoration;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.RecyclerItemListener;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectedCompanyActivity extends AppCompatActivity {

    private ArrayList<Appalto> appList = new ArrayList<>();
    private RecyclerViewAdapterSelectedCompany mAdapter;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "MyPrefs3" ;
    private HashMap<Type, String> filters;
    private String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_company);
        //Toolbar toolbar = (Toolbar) findViewById(R.cig.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        android.support.design.widget.FloatingActionButton fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);

        Bundle dati = getIntent().getExtras();
        if (dati != null) {
            selected = dati.getString("azienda");
            setTitle(selected);
            if (selected != null) {
                selected = selected.toLowerCase();
            }
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Type.SELECTED.toString(), selected);
            editor.apply();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(100);
                v.startAnimation(animation1);
                alert(Type.ALL);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

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
                            Log.e("SelectedCompanyActivity onClickItem()", e.getMessage());
                            final Toast toast = Toast.makeText(getApplicationContext(),R.string.error_data,Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }

                    public void onLongClickItem(View view, int position) {

                    }
                }));

        mAdapter = new RecyclerViewAdapterSelectedCompany(appList);
        recyclerView.setAdapter(mAdapter);
        resetFilter();

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
                    Log.e("SelectedCompanyActivity prepareData() for", e.getMessage());
                }
            } // End Loop
            mAdapter.notifyDataSetChanged();
            return true;
        }
        catch (Exception e) {
            Log.e("SelectedCompanyActivity prepareData()", e.getMessage());
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
                        anno=obj.getString("anno").toLowerCase(),
                        aggiudicatario=obj.getString("aggiudicatario").toLowerCase(),
                        cod_fiscale_iva=obj.getString("cod_fiscale_iva").toLowerCase(),
                        importo_aggiudicazione=obj.getString("importo_aggiudicazione").toLowerCase(),
                        dataInizio=obj.getString("dataInizio").toLowerCase(),
                        responsabile=obj.getString("responsabile").toLowerCase();

                if (!TextUtils.isEmpty(selected) && !aggiudicatario.contains(selected))
                    return false;
                if (filters.get(Type.NONE).equalsIgnoreCase("yes"))
                    return true;
                if ((!filters.get(Type.ALL).equalsIgnoreCase("")) && (!tipo.contains(filters.get(Type.ALL)) && !citta.contains(filters.get(Type.ALL)) && !titolo.contains(filters.get(Type.ALL)) && !cig.contains(filters.get(Type.ALL)) && !anno.contains(filters.get(Type.ALL)) && !aggiudicatario.contains(filters.get(Type.ALL)) && !cod_fiscale_iva.contains(filters.get(Type.ALL)) && !importo_aggiudicazione.contains(filters.get(Type.ALL))&& !dataInizio.contains(filters.get(Type.ALL)) && !responsabile.contains(filters.get(Type.ALL)) ))
                    return false;
                if (!filters.get(Type.CITY).equalsIgnoreCase("") && !citta.contains(filters.get(Type.CITY)))
                    return false;
                if (!filters.get(Type.TITLE).equalsIgnoreCase("") && !titolo.contains(filters.get(Type.TITLE)))
                    return false;
                if (!filters.get(Type.CIG).equalsIgnoreCase("") && !cig.contains(filters.get(Type.CIG)))
                    return false;
                if (!filters.get(Type.TYPE).equalsIgnoreCase("") && !tipo.contains(filters.get(Type.TYPE)))
                    return false;
                if (!filters.get(Type.COD_IVA).equalsIgnoreCase("") && !cod_fiscale_iva.contains(filters.get(Type.COD_IVA)))
                    return false;
            }
            catch (Exception e){
                Log.e("SelectedCompanyActivity filter()", e.getMessage());
            }
        }
        return true;
    }

    private void resetFilter(){
        filters = new HashMap<>();
        filters.put(Type.NONE,"yes");
        filters.put(Type.CIG,"");
        filters.put(Type.CITY,"");
        filters.put(Type.TITLE,"");
        filters.put(Type.TYPE,"");
        filters.put(Type.COD_IVA,"");
        filters.put(Type.ALL,"");

        Log.e("Test",selected);
    }

    private void alert(final Type filter_type){
        String title, message;
        switch (filter_type){
            case CIG: title= String.valueOf(getApplication().getResources().getText(R.string.filter_cig)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_cig)); break;
            case CITY: title=String.valueOf(getApplication().getResources().getText(R.string.filter_citta)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_citta)); break;
            case TITLE: title=String.valueOf(getApplication().getResources().getText(R.string.filter_titolo)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_titolo)); break;
            case TYPE: title=String.valueOf(getApplication().getResources().getText(R.string.filter_type)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_type)); break;
            case COD_IVA: title=String.valueOf(getApplication().getResources().getText(R.string.filter_cod_iva)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_cod_iva)); break;
            case ALL: title=String.valueOf(getApplication().getResources().getText(R.string.filter_search)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_search)); break;
            default: title=String.valueOf(getApplication().getResources().getText(R.string.filter_null)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_null)); break;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectedCompanyActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        final EditText input1 = new EditText(SelectedCompanyActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input1.setLayoutParams(lp);
        alertDialog.setView(input1);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                        filters.put(filter_type, String.valueOf(input1.getText()).toLowerCase());
                        filters.put(Type.NONE, "");
                        prepareData();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // closed
        // Showing Alert Message
        alertDialog.show();
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
            editor.putString(Type.NONE.toString(), filters.get(Type.NONE));
            editor.putString(Type.CIG.toString(), filters.get(Type.CIG));
            editor.putString(Type.CITY.toString(), filters.get(Type.CITY));
            editor.putString(Type.TITLE.toString(), filters.get(Type.TITLE));
            editor.putString(Type.TYPE.toString(), filters.get(Type.TYPE));
            editor.putString(Type.COD_IVA.toString(), filters.get(Type.COD_IVA));
            editor.putString(Type.ALL.toString(), filters.get(Type.ALL));
            editor.putString(Type.SELECTED.toString(), selected);
            editor.apply();
            Log.d("SelectedCompanyActivity savePreferenes()", "eseguita - filtro salvato");
        }
        catch(Exception e){
            Log.e("SelectedCompanyActivity savePreferenes()", e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadPreferences(){
        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            filters.put(Type.NONE, sharedpreferences.getString(Type.NONE.toString(),"yes"));
            filters.put(Type.CIG, sharedpreferences.getString(Type.CIG.toString(),""));
            filters.put(Type.CITY, sharedpreferences.getString(Type.CITY.toString(),""));
            filters.put(Type.TITLE, sharedpreferences.getString(Type.TITLE.toString(),""));
            filters.put(Type.TYPE, sharedpreferences.getString(Type.TYPE.toString(),""));
            filters.put(Type.COD_IVA, sharedpreferences.getString(Type.COD_IVA.toString(),""));
            filters.put(Type.ALL, sharedpreferences.getString(Type.ALL.toString(),""));
            selected = sharedpreferences.getString(Type.SELECTED.toString(),"");
            prepareData();
            Log.d("SelectedCompanyActivity loadPreferences()", "eseguita - filtro caricato");
        }
        catch(Exception e){
            Log.e("SelectedCompanyActivity loadPreferences()", e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(Type.NONE.toString(), filters.get(Type.NONE));
            outState.putString(Type.CIG.toString(), filters.get(Type.CIG));
            outState.putString(Type.CITY.toString(), filters.get(Type.CITY));
            outState.putString(Type.TITLE.toString(), filters.get(Type.TITLE));
            outState.putString(Type.TYPE.toString(), filters.get(Type.TYPE));
            outState.putString(Type.COD_IVA.toString(), filters.get(Type.COD_IVA));
            outState.putString(Type.ALL.toString(), filters.get(Type.ALL));
            outState.putString(Type.SELECTED.toString(), selected);
            Log.d("SelectedCompanyActivity onSaveInstanceState()", "filtro salvato");
        }
        catch (Exception e){
            Log.e("SelectedCompanyActivity onSaveInstanceState()", e.getMessage());
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

            if (savedInstanceState.getString(Type.CIG.toString()) != null)
                filters.put(Type.CIG, savedInstanceState.getString(Type.CIG.toString()));
            else
                filters.put(Type.CIG, "");

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

            if (savedInstanceState.getString(Type.ALL.toString()) != null)
                filters.put(Type.ALL, savedInstanceState.getString(Type.ALL.toString()));
            else
                filters.put(Type.ALL, "");

            if (savedInstanceState.getString(Type.SELECTED.toString()) != null)
                selected = savedInstanceState.getString(Type.SELECTED.toString());
            else
                selected = "";

            Log.d("SelectedCompanyActivity onRestoreInstanceState()", "filtro caricato");
        }
        catch (Exception e){
            Log.e("SelectedCompanyActivity onRestoreInstanceState()", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
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
            case R.id.filter_cig:
                alert(Type.CIG);
                break;
            case R.id.filter_city:
                alert(Type.CITY);
                break;
            case R.id.filter_title:
                alert(Type.TITLE);
                break;
            case R.id.filter_type:
                alert(Type.TYPE);
                break;
            case R.id.filter_cod_iva:
                alert(Type.COD_IVA);
                break;
            case R.id.filter_delete:
                final Toast toast = Toast.makeText(getApplicationContext(),R.string.filter_deleted,Toast.LENGTH_SHORT);
                resetFilter();
                if(prepareData())
                    toast.show();
                break;
            case R.id.filter_list:
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SelectedCompanyActivity.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_filter_list, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle(R.string.filter_list);
                TextView view_city = (TextView) dialogView.findViewById(R.id.filter_city_alert);
                TextView view_title = (TextView) dialogView.findViewById(R.id.filter_title_alert);
                TextView view_cig = (TextView) dialogView.findViewById(R.id.filter_cig_alert);
                TextView view_type =(TextView) dialogView.findViewById(R.id.filter_type_alert);
                TextView view_cod_iva =(TextView) dialogView.findViewById(R.id.filter_cod_iva_alert);
                TextView view_all = (TextView) dialogView.findViewById(R.id.filter_search_alert);

                if(filters.get(Type.CITY).equalsIgnoreCase(""))
                    view_city.setHint(R.string.filter_null);
                else
                    view_city.setText(filters.get(Type.CITY));

                if(filters.get(Type.TITLE).equalsIgnoreCase(""))
                    view_title.setHint(R.string.filter_null);
                else
                    view_title.setText(filters.get(Type.TITLE));

                if (filters.get(Type.CIG).equalsIgnoreCase(""))
                    view_cig.setHint(R.string.filter_null);
                else
                    view_cig.setText(filters.get(Type.CIG));

                if(filters.get(Type.TYPE).equalsIgnoreCase(""))
                    view_type.setHint(R.string.filter_null);
                else
                    view_type.setText(filters.get(Type.TYPE));

                if(filters.get(Type.COD_IVA).equalsIgnoreCase(""))
                    view_cod_iva.setHint(R.string.filter_null);
                else
                    view_cod_iva.setText(filters.get(Type.COD_IVA));

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
        CIG,
        TITLE,
        CITY,
        TYPE,
        COD_IVA,
        ALL,
        SELECTED,
    }
}
