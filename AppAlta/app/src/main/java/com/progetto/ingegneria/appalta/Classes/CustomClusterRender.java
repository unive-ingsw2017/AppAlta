package com.progetto.ingegneria.appalta.Classes;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.progetto.ingegneria.appalta.R;

/**
 * Created by matteo on 08/01/2018.
 */

public class CustomClusterRender extends DefaultClusterRenderer<MyItem> {

    public CustomClusterRender(Context context, GoogleMap map,
                               ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if(item.getImporto()<15000)
            markerOptions.position(item.getPosition()).title(item.getTitolo()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_low_cost));
        else if(item.getImporto()>100000)
            markerOptions.position(item.getPosition()).title(item.getTitolo()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_high_cost));
        else
            markerOptions.position(item.getPosition()).title(item.getTitolo()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_default));
    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }
}
