package com.progetto.ingegneria.appalta.Classes;

import android.view.View;

/**
 * Created by matteo on 15/12/2017.
 */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
