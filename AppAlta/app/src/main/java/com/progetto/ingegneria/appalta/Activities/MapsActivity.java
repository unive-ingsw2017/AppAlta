package com.progetto.ingegneria.appalta.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.Classes.Suggestion;
import com.progetto.ingegneria.appalta.R;
import com.progetto.ingegneria.appalta.Threads.DataLoader;
import com.progetto.ingegneria.appalta.Threads.DataSaver;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String MyPREFERENCES = "MyPrefs" ;
    private static final String Type = "type";
    private static final String Markers = "nMarkers";
    private int preferenceType, preferencesMarkers;
    private DataSaver saver;
    private DataLoader loader;
    private FloatingSearchView mSearchView;
    private ArrayList<Appalto> appalti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setSearchHint(String.valueOf(getApplication().getResources().getText(R.string.searh_location)));
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                appalti = new ArrayList<>(loader.getAppalti());
                List<Suggestion> lista = new ArrayList<Suggestion>();
                //get suggestions based on newQuery
                for(Appalto app: appalti){
                    if(app.getTitolo().toLowerCase().contains(newQuery.toLowerCase())){
                        lista.add(new Suggestion(app.getTitolo()));
                    }
                }
                //pass them on to the search view
                mSearchView.swapSuggestions(lista);

            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                    mSearchView.setSearchText(searchSuggestion.getBody());
                    this.onSearchAction(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String s) {
                loader = new DataLoader(MapsActivity.this, getApplicationContext(), mMap, preferencesMarkers, s);
                loader.execute();
                mSearchView.clearSearchFocus();
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        try {
                            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;

                    case R.id.action_refresh:
                        try {
                            if (saver.getStatus() == AsyncTask.Status.RUNNING)
                                saver.cancel(true);
                            if (loader.getStatus() == AsyncTask.Status.RUNNING)
                                loader.cancel(true);
                            if (loader.getStatus() != AsyncTask.Status.RUNNING && saver.getStatus() != AsyncTask.Status.RUNNING) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                builder.setTitle(R.string.confirm);
                                builder.setMessage(R.string.confirm2);

                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing but close the dialog
                                        saver = new DataSaver(MapsActivity.this, getApplicationContext());
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

                    case R.id.action_view:
                        try {
                            if (saver.getStatus() == AsyncTask.Status.RUNNING)
                                saver.cancel(true);
                            if (loader.getStatus() == AsyncTask.Status.RUNNING)
                                loader.cancel(true);
                            if (loader.getStatus() != AsyncTask.Status.RUNNING && saver.getStatus() != AsyncTask.Status.RUNNING) {
                                loader = new DataLoader(MapsActivity.this, getApplicationContext(), mMap, 0);
                                loader.execute();
                            }
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;

                    case R.id.action_delete:
                        try {
                            if (loader.getStatus() == AsyncTask.Status.RUNNING)
                                loader.cancel(true);
                            mMap.clear();
                            Log.d("MapsActivity onOptionsItemSelected()", "mappa pulita");
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;
                    /*
                    case R.id.action_list:
                        try {
                            startActivity(new Intent(getApplicationContext(), ListActivity.class));
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;

                    case R.id.action_list_company:
                        try {
                            startActivity(new Intent(getApplicationContext(), CompanyListActivity.class));
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;
                    */
                    case R.id.action_info:
                        try {
                            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                            alertDialog.setTitle("Info");
                            alertDialog.setMessage("Marker Verde: appalto con importo basso \nMarker Rosso: appalto con importo nella media \nMarker Blu: appalto con importo alto");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } catch (Exception e) {
                            Log.e("MapsActivity onOptionsItemSelected()", e.getMessage());
                        }
                        break;
                }
            }
        });

        loadPreferences();
        setUpMapIfNeeded();
        loader = new DataLoader(MapsActivity.this, getApplicationContext(), mMap, 0);
        loader.execute();
        saver = new DataSaver(MapsActivity.this, getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
        setUpMapIfNeeded();
        changeMapType();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        */
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap.setMyLocationEnabled(true);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        //può essere omessa in quanto la sua getione è affidata al ClusterManager in DataLoader
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                try {
                    final Intent intent = new Intent(getApplicationContext(), SelectedItemActivity.class);
                    intent.putExtra("titolo", arg0.getTitle());
                    Log.e("MapsActivity premuto", arg0.getTitle());
                    startActivity(intent);
                }
                catch(Exception e){
                    Log.e("MapsActivity onInfoWindowClick", e.getMessage());
                    final Toast toast = Toast.makeText(getApplicationContext(),R.string.error_data,Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mSearchView.isSearchBarFocused())
                    mSearchView.clearSearchFocus();
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                LatLng myLatLng = new LatLng(arg0.getLatitude(),
                        arg0.getLongitude());

                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng).zoom(8).bearing(0).tilt(30).build();
                mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(myPosition));
                //mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
            }
        });

        //inizialmente incentro la mappa in italia
        LatLng italy = new LatLng(41.53, 12.29);
        CameraPosition myPosition = new CameraPosition.Builder()
                .target(italy).zoom(1).bearing(0).tilt(30).build();
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(myPosition));
    }

    private void changeMapType(){
        switch (preferenceType) {
            case GoogleMap.MAP_TYPE_NORMAL:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case GoogleMap.MAP_TYPE_SATELLITE:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case GoogleMap.MAP_TYPE_HYBRID:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case GoogleMap.MAP_TYPE_TERRAIN:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
        Log.d("MapsActicity changeMapType()", "eseguita");
    }

    private void loadPreferences(){
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (sharedpreferences.contains(Type))
                preferenceType = sharedpreferences.getInt(Type, GoogleMap.MAP_TYPE_NORMAL);
            if (sharedpreferences.contains(Markers))
                preferencesMarkers = sharedpreferences.getInt(Markers, 10);
            Log.d("MapsActivity loadPreferences()", "eseguita - mappa caricata: "+preferenceType);
            Log.d("MapsActivity loadPreferences()", "eseguita - nMarkers caricato: "+preferencesMarkers);
        }
        catch (Exception e){
            Log.e("MapsActivity loadPreferences()", e.getMessage());
        }
    }

    //Menu MainActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.maps_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
