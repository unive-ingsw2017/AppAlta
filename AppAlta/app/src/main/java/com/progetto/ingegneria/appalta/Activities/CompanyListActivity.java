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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.progetto.ingegneria.appalta.Adapters.RecyclerViewAdapterCompany;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.DividerItemDecoration;
import com.progetto.ingegneria.appalta.Classes.JsonManager;
import com.progetto.ingegneria.appalta.Classes.RecyclerItemListener;
import com.progetto.ingegneria.appalta.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyListActivity extends AppCompatActivity {

    private ArrayList<Appalto> appList = new ArrayList<>();
    private RecyclerViewAdapterCompany mAdapter;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "MyPrefs2";
    private HashMap<Type, String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.cig.toolbar);
        //setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                            final Intent intent = new Intent(getApplicationContext(), SelectedCompanyActivity.class);
                            intent.putExtra("azienda", app.getAggiudicatario());
                            startActivity(intent);
                        }
                        catch(Exception e){
                            Log.e("CompanyListActivity onClickItem()", e.getMessage());
                            final Toast toast = Toast.makeText(getApplicationContext(),R.string.error_data,Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }

                    public void onLongClickItem(View view, int position) {

                    }
                }));

        mAdapter = new RecyclerViewAdapterCompany(appList);
        recyclerView.setAdapter(mAdapter);
        resetFilter();

    }

    private boolean prepareData() {
        Appalto app;
        JsonManager manager = new JsonManager(getApplicationContext());
        appList.clear();
        ArrayList<String> aziende = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(manager.readFromFile("appalti_file.txt"));    // create JSON obj from string
            for (int i = 0; i < jArray.length(); i++) {
                //DATI
                try {
                    JSONObject jObject = jArray.getJSONObject(i);
                    if(filter(jObject) && !aziende.contains(jObject.getString("cod_fiscale_iva")))
                    {
                        app = new Appalto();
                        app.setAggiudicatario(jObject.getString("aggiudicatario"));
                        app.setCod_fiscale_iva(jObject.getString("cod_fiscale_iva"));
                        app.setCitta(jObject.getString("citta"));
                        appList.add(app);
                        aziende.add(jObject.getString("cod_fiscale_iva"));
                    }
                    //Log.d("Test", "aggiunto appalto: "+ app.getCig());
                }
                catch (Exception e) {
                    Log.e("CompanyListActivity prepareData() for", e.getMessage());
                }
            } // End Loop
            mAdapter.notifyDataSetChanged();
            return true;
        }
        catch (Exception e) {
            Log.e("CompanyListActivity prepareData()", e.getMessage());
            final Toast toast = Toast.makeText(getApplicationContext(), "Errore nella lettura dati ", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
    }

    private boolean filter(JSONObject obj){
        for(int i=0; i<filters.size(); i++) {
            try {
                String  citta=obj.getString("citta").toLowerCase(),
                        aggiudicatario=obj.getString("aggiudicatario").toLowerCase(),
                        cod_fiscale_iva=obj.getString("cod_fiscale_iva").toLowerCase();

                if (filters.get(Type.NONE).equalsIgnoreCase("yes"))
                    return true;
                if (!filters.get(Type.CITY).equalsIgnoreCase("") && !citta.contains(filters.get(Type.CITY)))
                    return false;
                if (!filters.get(Type.COMPANY).equalsIgnoreCase("") && !aggiudicatario.contains(filters.get(Type.COMPANY)))
                    return false;
                if (!filters.get(Type.COD_IVA).equalsIgnoreCase("") && !cod_fiscale_iva.contains(filters.get(Type.COD_IVA)))
                    return false;
            }
            catch (Exception e){
                Log.e("CompanyListActivity filter()", e.getMessage());
            }
        }
        return true;
    }

    private void resetFilter(){
        filters = new HashMap<>();
        filters.put(Type.NONE,"yes");
        filters.put(Type.CITY,"");
        filters.put(Type.COMPANY,"");
        filters.put(Type.COD_IVA,"");
    }

    private void alert(final Type filter_type){
        String title, message;
        switch (filter_type){
            case CITY: title=String.valueOf(getApplication().getResources().getText(R.string.filter_citta)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_citta)); break;
            case COMPANY: title=String.valueOf(getApplication().getResources().getText(R.string.filter_company)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_search)); break;
            case COD_IVA: title=String.valueOf(getApplication().getResources().getText(R.string.filter_cod_iva)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_cod_iva)); break;
            default: title=String.valueOf(getApplication().getResources().getText(R.string.filter_null)); message=String.valueOf(getApplication().getResources().getText(R.string.insert_null)); break;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompanyListActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        final EditText input1 = new EditText(CompanyListActivity.this);
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
            editor.putString(Type.CITY.toString(), filters.get(Type.CITY));
            editor.putString(Type.COMPANY.toString(), filters.get(Type.COMPANY));
            editor.putString(Type.COD_IVA.toString(), filters.get(Type.COD_IVA));
            editor.apply();
            Log.d("CompanyListActivity savePreferenes()", "eseguita - filtro salvato");
        }
        catch(Exception e){
            Log.e("CompanyListActivity savePreferenes()", e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadPreferences(){
        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            filters.put(Type.NONE, sharedpreferences.getString(Type.NONE.toString(),"yes"));
            filters.put(Type.CITY, sharedpreferences.getString(Type.CITY.toString(),""));
            filters.put(Type.COMPANY, sharedpreferences.getString(Type.COMPANY.toString(),""));
            filters.put(Type.COD_IVA, sharedpreferences.getString(Type.COD_IVA.toString(),""));
            prepareData();
            Log.d("CompanyListActivity loadPreferences()", "eseguita - filtro caricato");
        }
        catch(Exception e){
            Log.e("CompanyListActivity loadPreferences()", e.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(Type.NONE.toString(), filters.get(Type.NONE));
            outState.putString(Type.CITY.toString(), filters.get(Type.CITY));
            outState.putString(Type.COMPANY.toString(), filters.get(Type.COMPANY));
            outState.putString(Type.COD_IVA.toString(), filters.get(Type.COD_IVA));
            Log.d("CompanyListActivity onSaveInstanceState()", "filtro salvato");
        }
        catch (Exception e){
            Log.e("CompanyListActivity onSaveInstanceState()", e.getMessage());
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

            if (savedInstanceState.getString(Type.COMPANY.toString()) != null)
                filters.put(Type.COMPANY, savedInstanceState.getString(Type.COMPANY.toString()));
            else
                filters.put(Type.COMPANY, "");

            if (savedInstanceState.getString(Type.COD_IVA.toString()) != null)
                filters.put(Type.COD_IVA, savedInstanceState.getString(Type.COD_IVA.toString()));
            else
                filters.put(Type.COD_IVA, "");

            Log.d("CompanyListActivity onRestoreInstanceState()", "filtro caricato");
        }
        catch (Exception e){
            Log.e("CompanyListActivity onRestoreInstanceState()", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_company_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.filter_city:
                alert(Type.CITY);
                break;
            case R.id.filter_company:
                alert(Type.COMPANY);
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
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CompanyListActivity.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_filter_company_list, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle(R.string.filter_list);
                TextView view_city = (TextView) dialogView.findViewById(R.id.filter_city_alert);
                TextView view_title = (TextView) dialogView.findViewById(R.id.filter_company_alert);
                TextView view_cod_iva =(TextView) dialogView.findViewById(R.id.filter_cod_iva_alert);

                if(filters.get(Type.CITY).equalsIgnoreCase(""))
                    view_city.setHint(R.string.filter_null);
                else
                    view_city.setText(filters.get(Type.CITY));

                if(filters.get(Type.COMPANY).equalsIgnoreCase(""))
                    view_title.setHint(R.string.filter_null);
                else
                    view_title.setText(filters.get(Type.COMPANY));

                if(filters.get(Type.COD_IVA).equalsIgnoreCase(""))
                    view_cod_iva.setHint(R.string.filter_null);
                else
                    view_cod_iva.setText(filters.get(Type.COD_IVA));


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
        COMPANY,
        CITY,
        COD_IVA,
    }
}

