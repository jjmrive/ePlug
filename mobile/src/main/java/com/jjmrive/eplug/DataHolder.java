package com.jjmrive.eplug;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataHolder {

    private static final String PLUGSLIST = "PlugsList";

    private static ArrayList<Plug> plugsList = new ArrayList<>();

    public static ArrayList<Plug> getPlugsList(){
        return plugsList;
    }

    public static void setPlugsList(ArrayList<Plug> plugsList){
        DataHolder.plugsList=plugsList;
    }

    public static void loadPlugsList(Context context){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(PLUGSLIST, "");
        Type type = new TypeToken<ArrayList<Plug>>() {
        }.getType();
        ArrayList<Plug> plugs = gson.fromJson(json, type);
        if (plugs != null){
            DataHolder.setPlugsList(plugs);
        }

        boolean init = appSharedPrefs.getBoolean("inicio", true);

        if (init){
            DataHolder.getPlugsList().add(new Plug(37.171824, -3.603083, "Palacio de los Patos", "Dispone de conector para vehículos Tesla", "https://t-ec.bstatic.com/images/hotel/max500/535/53554347.jpg", false));
            DataHolder.getPlugsList().add(new Plug(37.157282, -3.607968, "Kyoto Electric Vehicles", "Punto de recarga privado. Contactar a recarga@kyoto-motor.com", "", false));
            DataHolder.getPlugsList().add(new Plug(37.211926, -3.618892, "B&B Hotel Granada", "Dispone de conector para vehículos Tesla", "https://www.atrapalo.com/hoteles/picture/l/1346/2/8/376953865.jpg", false));
            DataHolder.getPlugsList().add(new Plug(37.553997, -2.613998, "Tesla Supercharger Cúllar", "Supercharger Tesla", "https://cfmedia.electromaps.com/fichas_images/40bb0063c014b27fb7b1d472b7b61897_l.JPG", true));
            DataHolder.getPlugsList().add(new Plug(39.134675,-2.039013, "Tesla Supercharger Albacete", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(41.313192,-2.002201, "Tesla Supercharger Ariza", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(42.312279,-3.7041722, "Tesla Supercharger Burgos", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(41.998407,2.817277, "Tesla Supercharger Girona", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(41.859029,2.767237, "Tesla Supercharger Caldes de Malavella", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(41.663959,0.605371, "Tesla Supercharger Lleida", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(40.753019,0.606421, "Tesla Supercharger L' Aldea", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(38.038172,-1.149343, "Tesla Supercharger Murcia", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(39.542965,-0.451521, "Tesla Supercharger Valencia", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
            DataHolder.getPlugsList().add(new Plug(41.134294,1.1683, "Tesla Supercharger Tarragona", "Supercharger Tesla", "https://www.tesla.com/sites/default/files/images/marketing/1200x600-Supercharger-Straight.jpg", true));
        }
    }

    public static void savePlugsList(Context context){
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String jsonPlugsList = gson.toJson(DataHolder.getPlugsList());
        prefsEditor.putString(PLUGSLIST, jsonPlugsList);
        prefsEditor.commit();

        prefsEditor.putBoolean("inicio", false);
        prefsEditor.commit();
    }
}
