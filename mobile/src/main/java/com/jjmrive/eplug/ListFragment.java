package com.jjmrive.eplug;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class ListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ArrayList<Plug> plugList = DataHolder.getPlugsList();
        double[] plugDistances = new double[plugList.size()];
        ArrayList<Plug> sortedPlugList = new ArrayList<>();
        double[] sortedPlugDistances = new double[plugList.size()];
        SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);
        if (mapStatePrefs.getFloat("latitude" , 200) == 200){
            sortedPlugList = plugList;
            System.arraycopy(plugDistances, 0, sortedPlugDistances, 0, plugDistances.length);
        } else {
            float latitude = mapStatePrefs.getFloat("latitude", 200);
            float longitude = mapStatePrefs.getFloat("longitude", 200);

            for (int i = 0; i < plugList.size(); i++){
                plugDistances[i] = distance(plugList.get(i).getLatitude(), plugList.get(i).getLongitude(), latitude, longitude, 'K');
            }
            System.arraycopy(plugDistances, 0, sortedPlugDistances, 0, plugDistances.length);
            Arrays.sort(sortedPlugDistances);

            for (int i = 0; i < sortedPlugDistances.length; i++){
                for (int j = 0; j < plugDistances.length; j++){
                    if (sortedPlugDistances[i] == plugDistances[j]){
                        sortedPlugList.add(plugList.get(j));
                    }
                }
            }
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ListFragmentAdapter(sortedPlugList, sortedPlugDistances));

        return view;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }


    //This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
