package com.jjmrive.eplug;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final String ADD_DIALOG = "addDialog";

    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                this.getChildFragmentManager().findFragmentById(R.id.add);

        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onPause(){
        super.onPause();

        if (map != null) {
            SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mapStatePrefs.edit();
            editor.putFloat("lat", (float) map.getCameraPosition().target.latitude);
            editor.putFloat("long", (float) map.getCameraPosition().target.longitude);
            editor.putFloat("zoom", map.getCameraPosition().zoom);
            editor.putFloat("tilt", map.getCameraPosition().tilt);
            editor.putFloat("bearing", map.getCameraPosition().bearing);
            editor.putBoolean("mapPaused", true);
            editor.apply();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        if ((getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE) != null)){
            SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE);

            boolean paused = mapStatePrefs.getBoolean("mapPaused", false);

            if (paused) {
                float latitude = mapStatePrefs.getFloat("lat", 0);
                float longitude = mapStatePrefs.getFloat("long", 0);
                float zoom = mapStatePrefs.getFloat("zoom", 0);
                float tilt = mapStatePrefs.getFloat("tilt", 0);
                float bearing = mapStatePrefs.getFloat("bearing", 0);

                mapStatePrefs.edit().clear().apply();

                CameraPosition savedPosition = new CameraPosition(new LatLng(latitude, longitude),
                        zoom, tilt, bearing);
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(savedPosition);
                map.moveCamera(update);
            }

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getActivity(), "ePlug needs ACCESS_FINE_LOCATION permission", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
                }
            }
        }

        map.setOnMapLongClickListener(this);
        map.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(getActivity(), "Permission error", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.new_plug))
                .snippet(getString(R.string.new_plug_description))
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))).showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(ADD_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        AddDialog dialog = new AddDialog().newInstance(marker.getPosition().latitude, marker.getPosition().longitude);
        dialog.setTargetFragment(this, 1);
        dialog.show(getFragmentManager().beginTransaction(), ADD_DIALOG);

    }

    public void onAddedPlug(Double latitude, Double longitude, String name, String description, String urlPhoto, boolean free){

        Plug plug = new Plug(latitude, longitude, name, description, urlPhoto, free);

        DataHolder.getPlugsList().add(plug);
        DataHolder.savePlugsList(getActivity());

        map.clear();

        Toast.makeText(getActivity(), getResources().getString(R.string.plug_added_ok), Toast.LENGTH_SHORT).show();
    }
}
