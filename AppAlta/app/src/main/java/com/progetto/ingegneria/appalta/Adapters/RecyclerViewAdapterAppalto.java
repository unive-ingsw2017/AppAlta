package com.progetto.ingegneria.appalta.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.progetto.ingegneria.appalta.Classes.Appalto;
import com.progetto.ingegneria.appalta.R;

import java.util.ArrayList;

/**
 * Created by matteo on 15/12/2017.
 */
public class RecyclerViewAdapterAppalto extends RecyclerView.Adapter<RecyclerViewAdapterAppalto.MyViewHolder> {

    private ArrayList<Appalto> appList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cig, titolo, citta;

        public MyViewHolder(View view) {
            super(view);
            cig = (TextView) view.findViewById(R.id.textCig);
            titolo = (TextView) view.findViewById(R.id.textTitolo);
            citta = (TextView) view.findViewById(R.id.textCitta);
        }
    }


    public RecyclerViewAdapterAppalto(ArrayList<Appalto> appList) {
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.cig.setText(appList.get(position).getCig());
        holder.titolo.setText(appList.get(position).getTitolo());
        holder.citta.setText(appList.get(position).getCitta());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public ArrayList<Appalto> getAppList() {
        return appList;
    }

    public void setAppList(ArrayList<Appalto> appList) {
        this.appList = appList;
    }

}
