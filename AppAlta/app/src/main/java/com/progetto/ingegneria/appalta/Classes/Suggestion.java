package com.progetto.ingegneria.appalta.Classes;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by matteo on 07/10/2017.
 */
public class Suggestion implements SearchSuggestion {
    private String suggest;

    public Suggestion() {
        this.suggest = null;
    }

    public Suggestion(String suggest) {
        this.suggest = suggest;
    }

    @Override
    public String getBody() {
        return suggest;
    }

    @Override
    public int describeContents() {
        return suggest.length();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }
}
