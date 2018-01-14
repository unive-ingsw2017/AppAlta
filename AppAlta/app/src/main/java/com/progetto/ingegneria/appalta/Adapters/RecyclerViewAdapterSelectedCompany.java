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
public class RecyclerViewAdapterSelectedCompany extends RecyclerView.Adapter<RecyclerViewAdapterSelectedCompany.MyViewHolder> {

    private ArrayList<Appalto> appList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, azienda, citta, importo;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.textId);
            azienda = (TextView) view.findViewById(R.id.textCompany);
            citta = (TextView) view.findViewById(R.id.textCitta);
            importo = (TextView) view.findViewById(R.id.textImporto);
        }
    }


    public RecyclerViewAdapterSelectedCompany(ArrayList<Appalto> appList) {
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_selected_company_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.id.setText(appList.get(position).getCod_fiscale_iva());
        holder.azienda.setText(appList.get(position).getAggiudicatario());
        holder.citta.setText(appList.get(position).getCitta());
        holder.importo.setText(appList.get(position).getImporto_aggiudicazione());
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
