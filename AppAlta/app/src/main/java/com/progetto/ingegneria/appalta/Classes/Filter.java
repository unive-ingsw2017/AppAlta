package com.progetto.ingegneria.appalta.Classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matteo on 24/01/2018.
 */

public class Filter {
    private HashMap<String, String> filters;
    private HashMap<String, Boolean> active;

    public Filter() {
        filters = new HashMap<>();
        active = new HashMap<>();
    }

    public void setAll(String value){
        HashMap<String, String> copy = new HashMap<>(filters);
        for(Map.Entry<String, String> entry : copy.entrySet()) {
            filters.put(entry.getKey(), value);
            active.put(entry.getKey(), false);
        }
    }

    public void build(ArrayList<String> init){
        for (String s : init) {
            filters.put(s, "");
            active.put(s, false);
        }
    }

    public int size(){
        return filters.size();
    }

    public String remove(String key){
        String removed = filters.remove(key);
        active.remove(key);
        return removed;
    }

    public void clearAll(){
        filters.clear();
        active.clear();
    }

    public void add(String key, String value, Boolean isActive){
        filters.put(key,value.toLowerCase());
        active.put(key, isActive);
    }

    public void activate(String key){
        active.put(key, true);
    }

    public void deactivate(String key){
        active.put(key, false);
    }

    public String get(String key){
        return filters.get(key);
    }

    public boolean isActive(String key){
        return active.get(key);
    }

    public boolean check(String key, String check) {
        //Log.d("Test"+" key: "+key, "value: "+filters.get(key)+", check: "+check);
        return !active.get(key) || check.contains(filters.get(key));
    }

    public boolean checkMin(String key, String check){
        if (!active.get(key))
            return true;
        try {
            double importo = Double.parseDouble(check.replaceAll(",", "."));
            return importo >= Double.parseDouble(filters.get(key).replace(",", "."));
        }
        catch(Exception e){
            Log.e("Filter checkMin()", e.getMessage());
            return false;
        }
    }

    public boolean checkMax(String key, String check){
        if (!active.get(key))
            return true;
        try {
            double importo = Double.parseDouble(check.replaceAll(",", "."));
            return importo <= Double.parseDouble(filters.get(key).replace(",", "."));
        }
        catch(Exception e){
            Log.e("Filter checkMax()", e.getMessage());
            return false;
        }
    }

    public boolean checkAll(String key, HashMap<String,String> check){
        if (!active.get(key))
            return true;
        for(Map.Entry<String, String> entry : check.entrySet()) {
            if(check(key,entry.getValue()))
                return true;
        }
        return false;
    }

    public ArrayList<String> getFilters() {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            result.add(entry.getKey()+": '"+entry.getValue()+"'"+" "+active.get(entry.getKey()));
        }
        return result;
    }

}
