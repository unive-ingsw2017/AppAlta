package com.progetto.ingegneria.appalta.Classes;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by matteo on 08/01/2018.
 */

public class MyItem implements ClusterItem {
    private LatLng position;
    private String titolo;
    private double importo;

    public MyItem(LatLng coordinate, String mTitle, double importo) {
        this.titolo = mTitle;
        this.position = coordinate;
        this.importo = importo;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

}